package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.BackendConfigStatus
import com.example.data.auth.GoogleAuthResult
import com.example.data.auth.GoogleSignInHelper
import com.example.data.local.AppDatabase
import com.example.data.local.DailyVocabAssignmentEntity
import com.example.data.local.UserEntity
import com.example.data.models.*
import com.example.data.repository.WordExplorerRepository
import com.example.data.seed.InitialDataSeed
import com.example.util.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

sealed class AuthScreenState {
    object Login : AuthScreenState()
    object Register : AuthScreenState()
    object ForgotPassword : AuthScreenState()
    object EmailVerification : AuthScreenState()
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
    val repository = WordExplorerRepository(
        userDao = database.userDao(),
        progressDao = database.progressDao(),
        vocabularyDao = database.vocabularyDao(),
        firestoreRepository = com.example.data.firestore.WordExplorerFirestoreRepository(application)
    )

    private val googleSignInHelper = GoogleSignInHelper(application)

    // Audio effects manager (persisted preference, SoundPool)
    val soundManager = SoundManager(application)
    val isSoundEnabled: StateFlow<Boolean> = soundManager.isSoundEnabled

    fun toggleSound(): Boolean = soundManager.toggleSound()

    // Android Text-to-Speech for English Pronunciation Practice
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false

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

    // Email Verification Code State (SHA-256 OTP with 5-minute expiry)
    val authVerificationCode = MutableStateFlow("")
    val activeVerificationEmail = MutableStateFlow("")
    val activeSentCode = MutableStateFlow<String?>(null)
    val resendCooldownSeconds = MutableStateFlow(0)
    private var cooldownJob: Job? = null

    // Backend Configuration & Security Status Disclosure
    val backendConfigStatus = MutableStateFlow<BackendConfigStatus>(repository.getBackendConfigStatus())
    val showBackendStatusDialog = MutableStateFlow(false)

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
    val selectedRegion = MutableStateFlow<ExplorationRegion?>(null)
    val showRegionModal = MutableStateFlow(false)

    // Daily Adventure Limit (3 Levels/Day) & Countdown to Midnight
    val dailyCompletedLevelsCount = MutableStateFlow(0)
    val dailyLimitErrorMessage = MutableStateFlow<String?>(null)
    val timeUntilMidnightReset = MutableStateFlow("")
    private var midnightTimerJob: Job? = null

    // Daily Vocabulary Rotation System
    val dailyVocabAssignments = MutableStateFlow<List<DailyVocabAssignmentEntity>>(emptyList())

    // Quiz & Remedial state
    val quizState = MutableStateFlow(QuizUiState())

    // Interactive Quiz Response State
    val matchingSelectedEnglish = MutableStateFlow<String?>(null)
    val matchingAnswers = mutableStateMapOf<String, String>() // English -> Indonesian
    val typedAnswer = MutableStateFlow("")
    val showTypingHint = MutableStateFlow(false)
    val scrambledSlots = mutableStateListOf<String>()
    val availableLetters = mutableStateListOf<Pair<Int, String>>() // id to char

    // Interactive Question Types State
    val sentenceBuilderSlots = mutableStateListOf<String>()
    val availableSentenceChips = mutableStateListOf<Pair<Int, String>>()
    val categoryBucketAssignments = mutableStateMapOf<String, String>() // Word -> Bucket Name
    val selectedCategoryWord = MutableStateFlow<String?>(null)
    val memoryFlippedIndices = mutableStateListOf<Int>()
    val memoryMatchedCards = mutableStateListOf<String>() // Set of matched labels
    val selectedTrueFalse = MutableStateFlow<Boolean?>(null)
    val selectedGridCoords = mutableStateListOf<Pair<Int, Int>>() // row, col

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
        // Initialize Android Text-to-Speech
        try {
            textToSpeech = TextToSpeech(application) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = textToSpeech?.setLanguage(Locale.US)
                    isTtsInitialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
                }
            }
        } catch (e: Exception) {
            isTtsInitialized = false
        }

        // Start Daily Midnight Countdown Timer
        startMidnightCountdownTimer()

        viewModelScope.launch {
            repository.prepopulateVocabularyIfEmpty()
            refreshVocabulary()

            // Automatic Session Restoration on App Launch
            val restoredUser = repository.restoreUserSession()
            if (restoredUser != null && restoredUser.isEmailVerified) {
                onUserAuthenticated(restoredUser)
            }
        }
    }

    private fun startMidnightCountdownTimer() {
        midnightTimerJob?.cancel()
        midnightTimerJob = viewModelScope.launch {
            while (true) {
                val now = Calendar.getInstance()
                val midnight = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val diff = (midnight.timeInMillis - now.timeInMillis).coerceAtLeast(0)
                val hours = diff / (1000 * 60 * 60)
                val mins = (diff % (1000 * 60 * 60)) / (1000 * 60)
                val secs = (diff % (1000 * 60)) / 1000
                timeUntilMidnightReset.value = String.format("%02d:%02d:%02d", hours, mins, secs)
                delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        cooldownJob?.cancel()
        midnightTimerJob?.cancel()
    }

    // Pronunciation Practice Audio Playback
    fun speakWord(word: String) {
        if (isTtsInitialized && textToSpeech != null) {
            textToSpeech?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "vocab_tts_${word}")
        } else {
            try {
                textToSpeech?.language = Locale.US
                textToSpeech?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "vocab_tts_${word}")
            } catch (e: Exception) {
                // Ignore fallback
            }
        }
    }

    // --- Authentication Actions ---

    fun toggleBackendStatusDialog() {
        backendConfigStatus.value = repository.getBackendConfigStatus()
        showBackendStatusDialog.value = !showBackendStatusDialog.value
    }

    // Official Google Sign-In with Account Linking
    fun signInWithGoogle() {
        viewModelScope.launch {
            isAuthLoading.value = true
            authError.value = null
            authSuccessMessage.value = null

            val result = googleSignInHelper.signInWithGoogle()
            when (result) {
                is GoogleAuthResult.Success -> {
                    val repoResult = repository.handleGoogleSignIn(
                        googleId = result.firebaseUid,
                        email = result.email,
                        displayName = result.displayName
                    )
                    repoResult.onSuccess { user ->
                        authSuccessMessage.value = "Berhasil masuk dengan akun Google: ${user.email}"
                        onUserAuthenticated(user)
                    }.onFailure { err ->
                        authError.value = err.message ?: "Gagal menautkan akun Google."
                    }
                }
                is GoogleAuthResult.ClientNotConfigured -> {
                    // Disclose requirement clearly as instructed by prompt
                    authError.value = result.message
                    toggleBackendStatusDialog()
                }
                is GoogleAuthResult.Cancelled -> {
                    authError.value = result.message
                }
                is GoogleAuthResult.Error -> {
                    authError.value = result.message
                }
            }
            isAuthLoading.value = false
        }
    }

    fun login() {
        val email = authEmail.value.trim().lowercase()
        val password = authPassword.value
        if (email.isBlank() || password.isBlank()) {
            authError.value = "Harap masukkan email dan kata sandi."
            return
        }
        viewModelScope.launch {
            isAuthLoading.value = true
            authError.value = null
            authSuccessMessage.value = null
            val result = repository.loginUser(email, password)
            isAuthLoading.value = false
            result.onSuccess { user ->
                onUserAuthenticated(user)
            }.onFailure { err ->
                val msg = err.message ?: "Gagal masuk."
                if (msg.startsWith("UNVERIFIED_EMAIL:")) {
                    val unverifiedEmail = msg.substringAfter("UNVERIFIED_EMAIL:")
                    activeVerificationEmail.value = unverifiedEmail
                    authError.value = "Akun belum diverifikasi. Kode verifikasi telah dikirimkan."
                    resendVerificationCode()
                    authScreenState.value = AuthScreenState.EmailVerification
                } else {
                    authError.value = msg
                }
            }
        }
    }

    fun register() {
        val email = authEmail.value.trim().lowercase()
        val password = authPassword.value
        val name = authDisplayName.value.trim()

        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            authError.value = "Harap lengkapi semua kolom pendaftaran."
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            authError.value = "Format email tidak valid. Masukkan alamat Gmail yang dapat diakses."
            return
        }
        if (password.length < 6) {
            authError.value = "Kata sandi minimal harus 6 karakter."
            return
        }

        viewModelScope.launch {
            isAuthLoading.value = true
            authError.value = null
            authSuccessMessage.value = null
            val result = repository.registerUser(email, name, password)
            isAuthLoading.value = false
            result.onSuccess { pair ->
                val (user, code) = pair
                activeVerificationEmail.value = user.email
                activeSentCode.value = code
                authVerificationCode.value = ""
                startResendCooldown()
                authSuccessMessage.value = "Kode OTP 6-digit berhasil dikirimkan ke ${user.email} (berlaku 5 menit)."
                authScreenState.value = AuthScreenState.EmailVerification
            }.onFailure { err ->
                authError.value = err.message ?: "Pendaftaran gagal."
            }
        }
    }

    fun verifyEmailCode() {
        val email = activeVerificationEmail.value.trim().lowercase()
        val code = authVerificationCode.value.trim()
        if (email.isBlank() || code.isBlank()) {
            authError.value = "Harap masukkan kode OTP 6-digit."
            return
        }
        viewModelScope.launch {
            isAuthLoading.value = true
            authError.value = null
            val result = repository.verifyEmailCode(email, code)
            isAuthLoading.value = false
            result.onSuccess { verifiedUser ->
                authSuccessMessage.value = "Akun berhasil diverifikasi! Selamat datang, ${verifiedUser.displayName}."
                onUserAuthenticated(verifiedUser)
            }.onFailure { err ->
                authError.value = err.message ?: "Kode OTP salah atau telah kadaluarsa."
            }
        }
    }

    fun resendVerificationCode() {
        val email = activeVerificationEmail.value.trim().lowercase()
        if (email.isBlank()) return
        if (resendCooldownSeconds.value > 0) return

        viewModelScope.launch {
            isAuthLoading.value = true
            authError.value = null
            val result = repository.resendVerificationCode(email)
            isAuthLoading.value = false
            result.onSuccess { newCode ->
                activeSentCode.value = newCode
                startResendCooldown()
                authSuccessMessage.value = "Kode OTP baru (berlaku 5 menit) telah dikirimkan ke $email."
            }.onFailure { err ->
                authError.value = err.message ?: "Gagal mengirim ulang kode."
            }
        }
    }

    private fun startResendCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            resendCooldownSeconds.value = 60 // 60-second cooldown
            while (resendCooldownSeconds.value > 0) {
                delay(1000)
                resendCooldownSeconds.value -= 1
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
            authError.value = "Masukkan email dan kata sandi baru untuk mengatur ulang."
            return
        }
        viewModelScope.launch {
            isAuthLoading.value = true
            val result = repository.resetPassword(email, newPass)
            isAuthLoading.value = false
            result.onSuccess {
                authSuccessMessage.value = "Kata sandi berhasil diperbarui! Silakan masuk kembali."
                authScreenState.value = AuthScreenState.Login
            }.onFailure {
                authError.value = it.message ?: "Gagal mengatur ulang kata sandi."
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearUserSession()
            _currentUser.value = null
            _userProfile.value = null
            currentAppScreen.value = "auth"
            authScreenState.value = AuthScreenState.Login
            authPassword.value = ""
            authVerificationCode.value = ""
            activeSentCode.value = null
            dailyCompletedLevelsCount.value = 0
            dailyLimitErrorMessage.value = null
        }
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

            // Observe daily completed levels count (Strict 3 levels/day limit)
            repository.observeDailyCompletedLevelsCount(user.id).collect { count ->
                dailyCompletedLevelsCount.value = count
            }
        }

        // Initialize Daily Vocabulary Rotation (no repeat from yesterday)
        viewModelScope.launch {
            val currentPath = _userProfile.value?.currentPath ?: LearningPath.LOW
            repository.getOrCreateDailyVocabSet(user.id, currentPath)
            repository.observeDailyVocabAssignments(user.id).collect { assignments ->
                dailyVocabAssignments.value = assignments
            }
        }

        // Start listening to profile changes
        viewModelScope.launch {
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
        val currIdx = diagnosticIndex.value
        val questions = InitialDataSeed.DIAGNOSTIC_QUESTIONS
        if (currIdx >= questions.size) return

        val q = questions[currIdx]
        val isCorrect = selectedOptionIndex == q.correctIndex
        if (isCorrect) {
            diagnosticScore.value += 1
            soundManager.playCorrect()
        } else {
            soundManager.playIncorrect()
        }

        if (currIdx < questions.size - 1) {
            diagnosticIndex.value = currIdx + 1
        } else {
            diagnosticFinished.value = true
            val finalScore = diagnosticScore.value
            val assignedPath = when {
                finalScore <= 4 -> LearningPath.LOW
                finalScore in 5..7 -> LearningPath.MIDDLE
                else -> LearningPath.HIGH
            }
            recommendedPath.value = assignedPath
        }
    }

    fun finishDiagnosticAndStartAdventure() {
        val user = _currentUser.value ?: return
        val path = recommendedPath.value
        viewModelScope.launch {
            repository.completeOnboardingAndDiagnostic(
                userId = user.id,
                diagnosticScore = diagnosticScore.value,
                assignedPath = path
            )
            selectedMapPath.value = path
            loadLevelsForPath(user.id, path)
            // Initialize Daily Vocabulary for assigned path
            repository.getOrCreateDailyVocabSet(user.id, path)
            currentAppScreen.value = "map"
        }
    }

    fun switchMap(path: LearningPath) {
        val user = _currentUser.value ?: return
        selectedMapPath.value = path
        loadLevelsForPath(user.id, path)
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
        dailyLimitErrorMessage.value = null
    }

    fun dismissLevelModal() {
        showLevelModal.value = false
        selectedLevelInfo.value = null
        dailyLimitErrorMessage.value = null
    }

    fun selectRegion(region: ExplorationRegion) {
        selectedRegion.value = region
        showRegionModal.value = true
    }

    fun dismissRegionModal() {
        showRegionModal.value = false
        selectedRegion.value = null
    }

    fun isRegionUnlocked(stageNumber: Int): Boolean {
        if (stageNumber == 1) return true
        val levels = levelsForCurrentPath.value
        val prevStageLevels = levels.filter { it.stageNumber == stageNumber - 1 }
        return prevStageLevels.isNotEmpty() && prevStageLevels.all { it.status == LevelStatus.COMPLETED }
    }

    // --- Quiz & Adaptive Remedial Engine ---

    fun startQuizForSelectedLevel() {
        val level = selectedLevelInfo.value ?: return
        if (level.status == LevelStatus.LOCKED) return

        // Anti-Exploit Check: Daily Adventure Level Limit (Max 3/day)
        // If the level is NOT already completed and user completed >= 3 levels today:
        if (level.status != LevelStatus.COMPLETED && dailyCompletedLevelsCount.value >= 3) {
            dailyLimitErrorMessage.value = "You've completed today's 3 adventure levels! Come back tomorrow to continue your journey."
            return
        }

        showLevelModal.value = false
        dailyLimitErrorMessage.value = null

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
        questions.firstOrNull()?.let { setupInteractiveQuestion(it) }
        currentAppScreen.value = "quiz"
    }

    private fun setupInteractiveQuestion(q: Question) {
        matchingSelectedEnglish.value = null
        matchingAnswers.clear()
        typedAnswer.value = ""
        showTypingHint.value = false
        scrambledSlots.clear()
        availableLetters.clear()
        sentenceBuilderSlots.clear()
        availableSentenceChips.clear()
        categoryBucketAssignments.clear()
        selectedCategoryWord.value = null
        memoryFlippedIndices.clear()
        memoryMatchedCards.clear()
        selectedTrueFalse.value = null
        selectedGridCoords.clear()

        when (q.type) {
            QuestionType.WORD_UNSCRAMBLE, QuestionType.SCRAMBLED_LETTERS -> {
                val letters = q.scrambledLetters ?: q.correctAnswer.uppercase().map { it.toString() }.shuffled()
                letters.forEachIndexed { idx, char ->
                    availableLetters.add(Pair(idx, char))
                }
            }
            QuestionType.SENTENCE_BUILDER -> {
                val tokens = q.sentenceTokens ?: q.correctAnswer.split(" ").shuffled()
                tokens.forEachIndexed { idx, token ->
                    availableSentenceChips.add(Pair(idx, token))
                }
            }
            QuestionType.LISTEN_AND_TYPE -> {
                if (isSoundEnabled.value) {
                    speakWord(q.audioPromptWord ?: q.targetWord)
                }
            }
            else -> {}
        }
    }

    // Drag and Match Actions
    fun selectMatchingEnglish(word: String) {
        matchingSelectedEnglish.value = word
    }

    fun pairMatchingIndonesian(indonesian: String) {
        val eng = matchingSelectedEnglish.value ?: return
        matchingAnswers[eng] = indonesian
        matchingSelectedEnglish.value = null
        soundManager.playMatchPair()
    }

    fun unpairMatching(english: String) {
        matchingAnswers.remove(english)
        soundManager.playTileTap()
    }

    // Fill in the Blanks / Typing Action
    fun setTypedAnswer(text: String) {
        typedAnswer.value = text
    }

    fun toggleTypingHint() {
        showTypingHint.value = !showTypingHint.value
        soundManager.playClick()
    }

    // Word Unscramble Actions
    fun pickScrambledLetter(id: Int, char: String) {
        val item = availableLetters.find { it.first == id } ?: return
        availableLetters.remove(item)
        scrambledSlots.add(char)
        soundManager.playTileTap()
    }

    fun removeScrambledLetter(slotIndex: Int) {
        if (slotIndex in scrambledSlots.indices) {
            val char = scrambledSlots.removeAt(slotIndex)
            availableLetters.add(Pair((0..999999).random(), char))
            soundManager.playTileTap()
        }
    }

    fun resetScrambledLetters() {
        val currentQ = quizState.value.questions.getOrNull(quizState.value.currentQuestionIndex) ?: return
        scrambledSlots.clear()
        availableLetters.clear()
        val letters = currentQ.scrambledLetters ?: currentQ.correctAnswer.uppercase().map { it.toString() }.shuffled()
        letters.forEachIndexed { idx, char ->
            availableLetters.add(Pair(idx, char))
        }
        soundManager.playClick()
    }

    // Sentence Builder Actions
    fun pickSentenceChip(id: Int, token: String) {
        val item = availableSentenceChips.find { it.first == id } ?: return
        availableSentenceChips.remove(item)
        sentenceBuilderSlots.add(token)
        soundManager.playTileTap()
    }

    fun removeSentenceChip(slotIndex: Int) {
        if (slotIndex in sentenceBuilderSlots.indices) {
            val token = sentenceBuilderSlots.removeAt(slotIndex)
            availableSentenceChips.add(Pair((0..999999).random(), token))
            soundManager.playTileTap()
        }
    }

    fun resetSentenceBuilder() {
        val currentQ = quizState.value.questions.getOrNull(quizState.value.currentQuestionIndex) ?: return
        sentenceBuilderSlots.clear()
        availableSentenceChips.clear()
        val tokens = currentQ.sentenceTokens ?: currentQ.correctAnswer.split(" ").shuffled()
        tokens.forEachIndexed { idx, token ->
            availableSentenceChips.add(Pair(idx, token))
        }
    }

    // Category Sort Actions
    fun selectCategoryWord(word: String) {
        selectedCategoryWord.value = if (selectedCategoryWord.value == word) null else word
    }

    fun assignCategoryWord(word: String, bucket: String) {
        categoryBucketAssignments[word] = bucket
        selectedCategoryWord.value = null
        soundManager.playMatchPair()
    }

    fun unassignCategoryWord(word: String) {
        categoryBucketAssignments.remove(word)
        soundManager.playTileTap()
    }

    // Memory Flip Actions
    fun flipMemoryCard(cardIndex: Int, cardKey: String, matchingMap: Map<String, String>) {
        if (memoryFlippedIndices.contains(cardIndex) || memoryMatchedCards.contains(cardKey)) return

        if (memoryFlippedIndices.size == 2) {
            memoryFlippedIndices.clear()
        }

        memoryFlippedIndices.add(cardIndex)
        soundManager.playTileTap()

        if (memoryFlippedIndices.size == 2) {
            val idx1 = memoryFlippedIndices[0]
            val idx2 = memoryFlippedIndices[1]
            // Check if card 1 and card 2 are a valid match
            val currentQ = quizState.value.questions.getOrNull(quizState.value.currentQuestionIndex) ?: return
            val pairs = currentQ.matchingPairs ?: emptyMap()
            // Pairs are English -> Indonesian
            val allCards = pairs.flatMap { listOf(it.key, it.value) }.sorted()
            val card1 = allCards.getOrNull(idx1) ?: ""
            val card2 = allCards.getOrNull(idx2) ?: ""

            val isMatch = pairs[card1] == card2 || pairs[card2] == card1
            if (isMatch) {
                memoryMatchedCards.add(card1)
                memoryMatchedCards.add(card2)
                soundManager.playMatchPair()
            }
        }
    }

    // True or False Action
    fun selectTrueFalse(value: Boolean) {
        if (!quizState.value.isAnswerChecked) {
            selectedTrueFalse.value = value
            soundManager.playClick()
        }
    }

    // Word Hunt Action
    fun toggleGridCoord(row: Int, col: Int) {
        if (quizState.value.isAnswerChecked) return
        val coord = Pair(row, col)
        if (selectedGridCoords.contains(coord)) {
            selectedGridCoords.remove(coord)
        } else {
            selectedGridCoords.add(coord)
        }
        soundManager.playTileTap()
    }

    fun clearGridCoords() {
        selectedGridCoords.clear()
        soundManager.playClick()
    }

    fun selectQuizOption(option: String) {
        if (!quizState.value.isAnswerChecked) {
            quizState.value = quizState.value.copy(selectedOption = option)
            soundManager.playClick()
        }
    }

    fun checkQuizAnswer() {
        val state = quizState.value
        if (state.isAnswerChecked) return
        val currentQ = state.questions.getOrNull(state.currentQuestionIndex) ?: return

        val isCorrect = when (currentQ.type) {
            QuestionType.DRAG_AND_MATCH -> {
                val pairs = currentQ.matchingPairs ?: emptyMap()
                pairs.isNotEmpty() && pairs.all { (eng, indo) ->
                    matchingAnswers[eng].equals(indo, ignoreCase = true)
                }
            }
            QuestionType.FILL_BLANK, QuestionType.LISTEN_AND_TYPE -> {
                typedAnswer.value.trim().equals(currentQ.correctAnswer.trim(), ignoreCase = true)
            }
            QuestionType.WORD_UNSCRAMBLE, QuestionType.SCRAMBLED_LETTERS -> {
                scrambledSlots.joinToString("").trim().equals(currentQ.correctAnswer.trim(), ignoreCase = true)
            }
            QuestionType.SENTENCE_BUILDER -> {
                val built = sentenceBuilderSlots.joinToString(" ").trim()
                val expected = (currentQ.orderedSentence ?: currentQ.correctAnswer).trim()
                built.equals(expected, ignoreCase = true)
            }
            QuestionType.CATEGORY_SORT -> {
                val buckets = currentQ.categoryBuckets ?: emptyMap()
                buckets.isNotEmpty() && buckets.all { (bucketName, expectedWords) ->
                    expectedWords.all { word -> categoryBucketAssignments[word] == bucketName }
                }
            }
            QuestionType.MEMORY_FLIP -> {
                val pairs = currentQ.matchingPairs ?: emptyMap()
                pairs.isNotEmpty() && memoryMatchedCards.size >= pairs.size * 2
            }
            QuestionType.TRUE_OR_FALSE -> {
                selectedTrueFalse.value == currentQ.isStatementTrue
            }
            QuestionType.WORD_HUNT -> {
                val grid = currentQ.gridLetters ?: emptyList()
                val tappedChars = selectedGridCoords.mapNotNull { (r, c) ->
                    grid.getOrNull(r)?.getOrNull(c)
                }.joinToString("")
                tappedChars.equals(currentQ.correctAnswer, ignoreCase = true)
            }
            QuestionType.PICTURE_TAP,
            QuestionType.IMAGE_VOCABULARY,
            QuestionType.SENTENCE_COMPLETION,
            QuestionType.MULTIPLE_CHOICE -> {
                state.selectedOption == currentQ.correctAnswer
            }
        }

        if (isCorrect) {
            soundManager.playCorrect()
            // Mark daily vocab as MASTERED if present in daily set
            _currentUser.value?.let { u ->
                viewModelScope.launch {
                    repository.updateDailyVocabStatus(u.id, currentQ.targetWord.lowercase(), "MASTERED")
                }
            }
        } else {
            soundManager.playIncorrect()
            // Mark daily vocab as NEEDS_REVIEW
            _currentUser.value?.let { u ->
                viewModelScope.launch {
                    repository.updateDailyVocabStatus(u.id, currentQ.targetWord.lowercase(), "NEEDS_REVIEW")
                }
            }
        }

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
            val nextIdx = state.currentQuestionIndex + 1
            quizState.value = state.copy(
                currentQuestionIndex = nextIdx,
                selectedOption = null,
                isAnswerChecked = false,
                isAnswerCorrect = false
            )
            state.questions.getOrNull(nextIdx)?.let { setupInteractiveQuestion(it) }
        } else {
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
                soundManager.playVictoryFanfare()
                quizState.value = state.copy(
                    isQuizFinished = true,
                    isRemedialMode = false,
                    starsAwarded = 3,
                    isLevelCompleted = true
                )
            } else {
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
        if (state.isRemedialChecked) return
        val task = state.remedialTasks.getOrNull(state.currentRemedialIndex) ?: return
        val isCorrect = state.remedialSelectedOption == task.question.correctAnswer

        if (isCorrect) {
            soundManager.playCorrect()
        } else {
            soundManager.playIncorrect()
        }

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
                    soundManager.playVictoryFanfare()
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
            quizState.value = state.copy(isLevelCompleted = true)
        }
    }

    fun exitQuizToMap() {
        currentAppScreen.value = "map"
        val user = _currentUser.value
        if (user != null) {
            loadLevelsForPath(user.id, selectedMapPath.value)
        }
    }

    fun enterAdventureAfterDiagnostic() {
        finishDiagnosticAndStartAdventure()
    }

    fun changeUserCurrentPath(path: LearningPath) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.switchLearningPath(user.id, path)
            selectedMapPath.value = path
            loadLevelsForPath(user.id, path)
            repository.getOrCreateDailyVocabSet(user.id, path)
        }
    }

    // --- Vocabulary Library ---

    fun setVocabCategory(category: String) {
        vocabCategoryFilter.value = category
    }

    fun refreshVocabulary() {
        viewModelScope.launch {
            repository.observeVocabulary(vocabSearchQuery.value).collect { list ->
                vocabularyList.value = list
            }
        }
    }

    fun searchVocabulary(query: String) {
        vocabSearchQuery.value = query
        viewModelScope.launch {
            repository.observeVocabulary(query).collect { list ->
                vocabularyList.value = list
            }
        }
    }

    fun toggleLearnedWord(vocab: VocabularyItem) {
        viewModelScope.launch {
            repository.toggleVocabularyLearned(vocab.id, vocab.isLearned)
        }
    }

    fun toggleLearnedWord(vocabId: String, currentLearned: Boolean) {
        viewModelScope.launch {
            repository.toggleVocabularyLearned(vocabId, currentLearned)
        }
    }

    // --- Adventure Stories ---

    fun openStory(story: AdventureStory) {
        activeStory.value = story
    }

    fun closeStory() {
        activeStory.value = null
    }

    fun completeStory(storyId: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.completeStory(user.id, storyId)
        }
    }

    fun completeActiveStory(storyId: String) {
        completeStory(storyId)
    }

    // --- Companion Selection ---

    fun selectCompanion(companionId: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.setActiveCompanion(user.id, companionId)
        }
    }

    fun equipCompanion(companion: CompanionAnimal) {
        selectCompanion(companion.id)
    }

    fun equipCompanion(companionId: String) {
        selectCompanion(companionId)
    }
}
