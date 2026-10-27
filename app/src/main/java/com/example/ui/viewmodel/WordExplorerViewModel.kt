package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.UserEntity
import com.example.data.models.*
import com.example.data.repository.WordExplorerRepository
import com.example.data.seed.InitialDataSeed
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class AuthScreenState {
    object Login : AuthScreenState()
    object Register : AuthScreenState()
    object ForgotPassword : AuthScreenState()
}

data class QuizUiState(
    val stage: Int = 1,
    val level: Int = 1,
    val path: LearningPath = LearningPath.LOW,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOption: String? = null,
    val isAnswerChecked: Boolean = false,
    val isAnswerCorrect: Boolean = false,
    val firstAttemptScore: Int = 0,
    val failedQuestions: List<Question> = emptyList(),
    val isQuizFinished: Boolean = false,
    // Remedial Mode
    val isRemedialMode: Boolean = false,
    val initialRemedialCount: Int = 0,
    val remedialTasks: List<RemedialTask> = emptyList(),
    val currentRemedialIndex: Int = 0,
    val remedialSelectedOption: String? = null,
    val isRemedialChecked: Boolean = false,
    val isRemedialCorrect: Boolean = false,
    // Final Celebration
    val starsAwarded: Int = 0,
    val isLevelCompleted: Boolean = false
)

class WordExplorerViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = WordExplorerRepository(
        userDao = database.userDao(),
        progressDao = database.progressDao(),
        vocabularyDao = database.vocabularyDao()
    )

    // Current User & Profile
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    // Auth Form State
    val authScreenState = MutableStateFlow<AuthScreenState>(AuthScreenState.Login)
    val authEmail = MutableStateFlow("")
    val authPassword = MutableStateFlow("")
    val authDisplayName = MutableStateFlow("")
    val authError = MutableStateFlow<String?>(null)
    val authSuccessMessage = MutableStateFlow<String?>(null)
    val isAuthLoading = MutableStateFlow(false)

    // Active Navigation Screen: "auth", "onboarding", "questionnaire", "diagnostic", "map", "quiz", "dashboard", "vocab", "stories", "companions"
    val currentAppScreen = MutableStateFlow("auth")

    // Questionnaire state
    val questionnaireAnswers = androidx.compose.runtime.mutableStateMapOf<Int, String>()

    // Diagnostic state
    val diagnosticIndex = MutableStateFlow(0)
    val diagnosticScore = MutableStateFlow(0)
    val diagnosticFinished = MutableStateFlow(false)
    val recommendedPath = MutableStateFlow(LearningPath.LOW)

    // Map & Level state
    val selectedMapPath = MutableStateFlow(LearningPath.LOW)
    val levelsForCurrentPath = MutableStateFlow<List<LevelInfo>>(emptyList())
    val selectedLevelInfo = MutableStateFlow<LevelInfo?>(null)
    val showLevelModal = MutableStateFlow(false)

    // Quiz & Remedial state
    val quizState = MutableStateFlow(QuizUiState())

    // Vocabulary state
    val vocabSearchQuery = MutableStateFlow("")
    val vocabularyList = MutableStateFlow<List<VocabularyItem>>(emptyList())
    val vocabCategoryFilter = MutableStateFlow("All")

    // Stories state
    val storyCompletions = MutableStateFlow<Set<String>>(emptySet())
    val activeStory = MutableStateFlow<AdventureStory?>(null)

    // Companions state
    val companionsList = MutableStateFlow<List<CompanionAnimal>>(emptyList())

    init {
        viewModelScope.launch {
            repository.prepopulateVocabularyIfEmpty()
            refreshVocabulary()
        }
    }

    // --- Authentication Actions ---

    fun login() {
        val email = authEmail.value.trim()
        val password = authPassword.value
        if (email.isBlank() || password.isBlank()) {
            authError.value = "Please fill in email and password."
            return
        }
        viewModelScope.launch {
            isAuthLoading.value = true
            authError.value = null
            val result = repository.loginUser(email, password)
            isAuthLoading.value = false
            result.onSuccess { user ->
                onUserAuthenticated(user)
            }.onFailure { err ->
                authError.value = err.message ?: "Login failed."
            }
        }
    }

    fun register() {
        val email = authEmail.value.trim()
        val password = authPassword.value
        val name = authDisplayName.value.trim()
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            authError.value = "Please fill in all registration fields."
            return
        }
        viewModelScope.launch {
            isAuthLoading.value = true
            authError.value = null
            val result = repository.registerUser(email, name, password)
            isAuthLoading.value = false
            result.onSuccess { user ->
                onUserAuthenticated(user)
            }.onFailure { err ->
                authError.value = err.message ?: "Registration failed."
            }
        }
    }

    fun quickDemoLogin() {
        viewModelScope.launch {
            isAuthLoading.value = true
            val demoUser = repository.quickLoginDemo()
            isAuthLoading.value = false
            onUserAuthenticated(demoUser)
        }
    }

    fun resetPassword() {
        val email = authEmail.value.trim()
        val newPass = authPassword.value
        if (email.isBlank() || newPass.isBlank()) {
            authError.value = "Enter email and new password to reset."
            return
        }
        viewModelScope.launch {
            isAuthLoading.value = true
            val result = repository.resetPassword(email, newPass)
            isAuthLoading.value = false
            result.onSuccess {
                authSuccessMessage.value = "Password updated! You can now login."
                authScreenState.value = AuthScreenState.Login
            }.onFailure {
                authError.value = it.message ?: "Reset failed."
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _userProfile.value = null
        currentAppScreen.value = "auth"
        authScreenState.value = AuthScreenState.Login
        authPassword.value = ""
    }

    private fun onUserAuthenticated(user: UserEntity) {
        _currentUser.value = user
        viewModelScope.launch {
            val profile = repository.getUserProfile(user.id)
            _userProfile.value = profile

            if (profile == null || !profile.onboardingCompleted) {
                currentAppScreen.value = "onboarding"
            } else {
                selectedMapPath.value = profile.currentPath
                currentAppScreen.value = "map"
                loadLevelsForPath(user.id, profile.currentPath)
            }

            // Start listening to profile changes
            repository.observeUserProfile(user.id).collect { updated ->
                _userProfile.value = updated
                updated?.let {
                    companionsList.value = repository.getCompanions(it.currentStreak)
                }
            }
        }
        // Observe story completions
        viewModelScope.launch {
            repository.observeStoryCompletions(user.id).collect {
                storyCompletions.value = it
            }
        }
    }

    // --- Onboarding & Diagnostic ---

    fun completeOnboardingIntro() {
        currentAppScreen.value = "questionnaire"
    }

    fun submitQuestionnaireAnswer(questionIndex: Int, answer: String) {
        questionnaireAnswers[questionIndex] = answer
    }

    fun completeQuestionnaire() {
        diagnosticIndex.value = 0
        diagnosticScore.value = 0
        diagnosticFinished.value = false
        currentAppScreen.value = "diagnostic"
    }

    fun answerDiagnosticQuestion(selectedOptionIndex: Int) {
        val questions = InitialDataSeed.DIAGNOSTIC_QUESTIONS
        val currentQ = questions.getOrNull(diagnosticIndex.value) ?: return

        if (selectedOptionIndex == currentQ.correctIndex) {
            diagnosticScore.value += 1
        }

        if (diagnosticIndex.value < questions.size - 1) {
            diagnosticIndex.value += 1
        } else {
            // Diagnostic finished! Calculate recommended path:
            val score = diagnosticScore.value
            val assignedPath = when {
                score <= 4 -> LearningPath.LOW
                score in 5..7 -> LearningPath.MIDDLE
                else -> LearningPath.HIGH
            }
            recommendedPath.value = assignedPath
            diagnosticFinished.value = true

            val user = _currentUser.value ?: return
            viewModelScope.launch {
                repository.completeOnboardingAndDiagnostic(user.id, score, assignedPath)
                selectedMapPath.value = assignedPath
            }
        }
    }

    fun enterAdventureAfterDiagnostic() {
        val user = _currentUser.value ?: return
        currentAppScreen.value = "map"
        loadLevelsForPath(user.id, selectedMapPath.value)
    }

    // --- Map & Level Management ---

    fun switchMap(path: LearningPath) {
        val profile = _userProfile.value ?: return
        // Check access:
        if (profile.currentPath.canAccess(path)) {
            selectedMapPath.value = path
            val user = _currentUser.value ?: return
            loadLevelsForPath(user.id, path)
        }
    }

    fun changeUserCurrentPath(newPath: LearningPath) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.switchLearningPath(user.id, newPath)
            selectedMapPath.value = newPath
            loadLevelsForPath(user.id, newPath)
        }
    }

    fun loadLevelsForPath(userId: String, path: LearningPath) {
        viewModelScope.launch {
            repository.observeLevels(userId, path).collect { list ->
                levelsForCurrentPath.value = list
            }
        }
    }

    fun selectLevel(level: LevelInfo) {
        selectedLevelInfo.value = level
        showLevelModal.value = true
    }

    fun dismissLevelModal() {
        showLevelModal.value = false
        selectedLevelInfo.value = null
    }

    // --- Quiz & Adaptive Remedial Engine ---

    fun startQuizForSelectedLevel() {
        val level = selectedLevelInfo.value ?: return
        if (level.status == LevelStatus.LOCKED) return

        showLevelModal.value = false
        val questions = InitialDataSeed.generateQuestionsForLevel(level.path, level.stageNumber, level.levelNumber)

        quizState.value = QuizUiState(
            stage = level.stageNumber,
            level = level.levelNumber,
            path = level.path,
            questions = questions,
            currentQuestionIndex = 0,
            selectedOption = null,
            isAnswerChecked = false,
            isAnswerCorrect = false,
            firstAttemptScore = 0,
            failedQuestions = emptyList(),
            isQuizFinished = false,
            isRemedialMode = false,
            initialRemedialCount = 0,
            remedialTasks = emptyList(),
            currentRemedialIndex = 0,
            remedialSelectedOption = null,
            isRemedialChecked = false,
            isRemedialCorrect = false,
            starsAwarded = 0,
            isLevelCompleted = false
        )
        currentAppScreen.value = "quiz"
    }

    fun selectQuizOption(option: String) {
        if (!quizState.value.isAnswerChecked) {
            quizState.value = quizState.value.copy(selectedOption = option)
        }
    }

    fun checkQuizAnswer() {
        val state = quizState.value
        val currentQ = state.questions.getOrNull(state.currentQuestionIndex) ?: return
        val isCorrect = state.selectedOption == currentQ.correctAnswer

        val newScore = if (isCorrect) state.firstAttemptScore + 1 else state.firstAttemptScore
        val newFailed = if (!isCorrect) state.failedQuestions + currentQ else state.failedQuestions

        quizState.value = state.copy(
            isAnswerChecked = true,
            isAnswerCorrect = isCorrect,
            firstAttemptScore = newScore,
            failedQuestions = newFailed
        )
    }

    fun nextQuizQuestion() {
        val state = quizState.value
        if (state.currentQuestionIndex < state.questions.size - 1) {
            quizState.value = state.copy(
                currentQuestionIndex = state.currentQuestionIndex + 1,
                selectedOption = null,
                isAnswerChecked = false,
                isAnswerCorrect = false
            )
        } else {
            // Main 10 questions complete! Evaluate Adaptive Remedial obligations.
            finalizeMainQuizAttempt()
        }
    }

    private fun finalizeMainQuizAttempt() {
        val state = quizState.value
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            val (stars, remedialTasks) = repository.recordFirstAttemptAndCheckRemedials(
                userId = user.id,
                path = state.path,
                stage = state.stage,
                level = state.level,
                firstAttemptScore = state.firstAttemptScore,
                failedQuestions = state.failedQuestions
            )

            if (remedialTasks.isEmpty()) {
                // Perfect 10/10 on first attempt -> Level instantly completed with 3 Stars!
                quizState.value = state.copy(
                    isQuizFinished = true,
                    isRemedialMode = false,
                    starsAwarded = 3,
                    isLevelCompleted = true
                )
            } else {
                // Must complete remedial obligations!
                // Initial remedial count strictly equals number of mistakes.
                quizState.value = state.copy(
                    isQuizFinished = true,
                    isRemedialMode = true,
                    initialRemedialCount = remedialTasks.size,
                    remedialTasks = remedialTasks,
                    currentRemedialIndex = 0,
                    remedialSelectedOption = null,
                    isRemedialChecked = false,
                    isRemedialCorrect = false,
                    starsAwarded = stars,
                    isLevelCompleted = false
                )
            }
        }
    }

    // --- Remedial Task Flow ---

    fun selectRemedialOption(option: String) {
        if (!quizState.value.isRemedialChecked) {
            quizState.value = quizState.value.copy(remedialSelectedOption = option)
        }
    }

    fun checkRemedialAnswer() {
        val state = quizState.value
        val task = state.remedialTasks.getOrNull(state.currentRemedialIndex) ?: return
        val isCorrect = state.remedialSelectedOption == task.question.correctAnswer

        quizState.value = state.copy(
            isRemedialChecked = true,
            isRemedialCorrect = isCorrect
        )

        if (isCorrect) {
            val user = _currentUser.value ?: return
            viewModelScope.launch {
                val isAllResolved = repository.resolveRemedialItem(
                    userId = user.id,
                    path = state.path,
                    stage = state.stage,
                    level = state.level,
                    questionId = task.originalQuestionId,
                    firstAttemptStars = state.starsAwarded,
                    firstAttemptScore = state.firstAttemptScore
                )
                if (isAllResolved) {
                    quizState.value = quizState.value.copy(isLevelCompleted = true)
                }
            }
        }
    }

    fun retryRemedialTask() {
        quizState.value = quizState.value.copy(
            remedialSelectedOption = null,
            isRemedialChecked = false,
            isRemedialCorrect = false
        )
    }

    fun nextRemedialTask() {
        val state = quizState.value
        if (state.currentRemedialIndex < state.remedialTasks.size - 1) {
            quizState.value = state.copy(
                currentRemedialIndex = state.currentRemedialIndex + 1,
                remedialSelectedOption = null,
                isRemedialChecked = false,
                isRemedialCorrect = false
            )
        } else {
            // All remedial tasks cleared! Level completed.
            quizState.value = state.copy(
                isLevelCompleted = true
            )
        }
    }

    fun exitQuizToMap() {
        currentAppScreen.value = "map"
        val user = _currentUser.value ?: return
        loadLevelsForPath(user.id, selectedMapPath.value)
    }

    // --- Vocabulary Search & Filter ---

    fun searchVocabulary(query: String) {
        vocabSearchQuery.value = query
        refreshVocabulary()
    }

    fun setVocabCategory(category: String) {
        vocabCategoryFilter.value = category
        refreshVocabulary()
    }

    private fun refreshVocabulary() {
        viewModelScope.launch {
            repository.observeVocabulary(vocabSearchQuery.value).collect { list ->
                val filtered = if (vocabCategoryFilter.value == "All") {
                    list
                } else {
                    list.filter { it.category == vocabCategoryFilter.value }
                }
                vocabularyList.value = filtered
            }
        }
    }

    fun toggleLearnedWord(vocab: VocabularyItem) {
        viewModelScope.launch {
            repository.toggleVocabularyLearned(vocab.id, vocab.isLearned)
        }
    }

    // --- Adventure Stories ---

    fun openStory(story: AdventureStory) {
        activeStory.value = story
    }

    fun closeStory() {
        activeStory.value = null
    }

    fun completeActiveStory(storyId: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.completeStory(user.id, storyId)
        }
    }

    // --- Animal Companion Equipment ---

    fun equipCompanion(companion: CompanionAnimal) {
        if (!companion.isUnlocked) return
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.setActiveCompanion(user.id, companion.id)
        }
    }
}
