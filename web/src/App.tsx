import { lazy, Suspense } from 'react';
import { AppShell } from './components/AppShell';
import { AuthScreen } from './screens/AuthScreen';
import { MapScreen } from './screens/MapScreen';
import { useExplorerStore } from './state/useExplorerStore';

const CompanionsScreen = lazy(() =>
  import('./screens/CompanionsScreen').then((m) => ({ default: m.CompanionsScreen }))
);
const DashboardScreen = lazy(() =>
  import('./screens/DashboardScreen').then((m) => ({ default: m.DashboardScreen }))
);
const DiagnosticScreen = lazy(() =>
  import('./screens/OnboardingFlowScreens').then((m) => ({ default: m.DiagnosticScreen }))
);
const OnboardingScreen = lazy(() =>
  import('./screens/OnboardingFlowScreens').then((m) => ({ default: m.OnboardingScreen }))
);
const QuestionnaireScreen = lazy(() =>
  import('./screens/OnboardingFlowScreens').then((m) => ({ default: m.QuestionnaireScreen }))
);
const QuizScreen = lazy(() =>
  import('./screens/QuizScreen').then((m) => ({ default: m.QuizScreen }))
);
const SettingsScreen = lazy(() =>
  import('./screens/SettingsScreen').then((m) => ({ default: m.SettingsScreen }))
);
const StoriesScreen = lazy(() =>
  import('./screens/StoriesScreen').then((m) => ({ default: m.StoriesScreen }))
);
const VocabularyScreen = lazy(() =>
  import('./screens/VocabularyScreen').then((m) => ({ default: m.VocabularyScreen }))
);

function ScreenSuspenseFallback() {
  return (
    <div className="app-loading-fallback" role="status" aria-live="polite">
      <div className="loading-spinner-compass" aria-hidden="true">🧭</div>
      <p>Memuat halaman ekspedisi...</p>
    </div>
  );
}

export function App() {
  const store = useExplorerStore();

  const accessibilityAttrs = {
    'data-high-contrast': store.highContrast ? 'true' : 'false',
    'data-reduced-motion': store.reducedMotion ? 'true' : 'false',
  };

  if (store.notFoundPath) {
    return (
      <div className="app-root" {...accessibilityAttrs}>
        <div className="flow-page">
          <main className="flow-container flow-container--centered">
            <div className="onboarding-card">
              <div className="onboarding-emblem" aria-hidden="true">
                <span>🧭</span>
              </div>
              <h1 className="flow-heading">Halaman Tidak Ditemukan (404)</h1>
              <p className="flow-subcopy">
                Rute <strong>{store.notFoundPath}</strong> tidak terdaftar di peta{' '}
                <em>Word Explorer</em>. Mari kembali ke jalur ekspedisi utama.
              </p>
              <button
                type="button"
                className="btn btn--primary flow-cta-btn"
                data-testid="not_found_recover_button"
                onClick={store.recoverFromNotFound}
              >
                {store.profile
                  ? store.profile.onboardingCompleted
                    ? 'Kembali ke Peta Petualangan'
                    : 'Lanjutkan Pengenalan'
                  : 'Kembali ke Halaman Masuk'}
              </button>
            </div>
          </main>
        </div>
      </div>
    );
  }

  if (store.appScreen === 'auth' || !store.profile) {
    return (
      <div className="app-root" {...accessibilityAttrs}>
        <AuthScreen
          mode={store.authMode}
          email={store.authEmail}
          password={store.authPassword}
          displayName={store.authDisplayName}
          verificationCode={store.authVerificationCode}
          activeVerificationEmail={store.activeVerificationEmail}
          sentCode={store.activeSentCode}
          resendCooldownSeconds={store.resendCooldownSeconds}
          error={store.authError}
          successMessage={store.authSuccessMessage}
          isLoading={store.isAuthLoading}
          backendStatus={store.backendStatus}
          googleWebClientId={store.googleWebClientId}
          showBackendDialog={store.showBackendStatusDialog}
          onModeChange={(nextMode) => {
            store.setAuthError(null);
            store.setAuthSuccessMessage(null);
            store.setAuthMode(nextMode);
          }}
          onEmailChange={store.setAuthEmail}
          onPasswordChange={store.setAuthPassword}
          onDisplayNameChange={store.setAuthDisplayName}
          onVerificationCodeChange={store.setAuthVerificationCode}
          onLogin={store.login}
          onRegister={store.register}
          onResetPassword={store.resetPassword}
          onVerifyCode={store.verifyEmailCode}
          onResendCode={store.resendVerificationCode}
          onGoogleSignIn={store.signInWithGoogle}
          onGoogleCredential={store.verifyGoogleCredential}
          onQuickDemo={store.quickDemoLogin}
          onToggleBackendDialog={store.setShowBackendStatusDialog}
        />
      </div>
    );
  }

  if (store.appScreen === 'onboarding') {
    return (
      <div className="app-root" {...accessibilityAttrs}>
        <Suspense fallback={<ScreenSuspenseFallback />}>
          <OnboardingScreen onComplete={store.completeOnboardingIntro} />
        </Suspense>
      </div>
    );
  }

  if (store.appScreen === 'questionnaire') {
    return (
      <div className="app-root" {...accessibilityAttrs}>
        <Suspense fallback={<ScreenSuspenseFallback />}>
          <QuestionnaireScreen
            answers={store.questionnaireAnswers}
            onSelectAnswer={store.submitQuestionnaireAnswer}
            onComplete={store.completeQuestionnaire}
          />
        </Suspense>
      </div>
    );
  }

  if (store.appScreen === 'diagnostic') {
    return (
      <div className="app-root" {...accessibilityAttrs}>
        <Suspense fallback={<ScreenSuspenseFallback />}>
          <DiagnosticScreen
            qIndex={store.diagnosticIndex}
            score={store.diagnosticScore}
            finished={store.diagnosticFinished}
            recommendedPath={store.recommendedPath}
            onAnswer={store.answerDiagnosticQuestion}
            onStartAdventure={store.enterAdventureAfterDiagnostic}
          />
        </Suspense>
      </div>
    );
  }

  if (store.appScreen === 'quiz') {
    return (
      <div className="app-root" {...accessibilityAttrs}>
        <Suspense fallback={<ScreenSuspenseFallback />}>
          <QuizScreen
            state={store.quizState}
            isSoundEnabled={store.isSoundEnabled}
            onToggleSound={store.toggleSound}
            onOptionSelected={store.selectQuizOption}
            onCheckAnswer={store.checkQuizAnswer}
            onNextQuestion={store.nextQuizQuestion}
            onExit={store.exitQuizToMap}
            matchingSelectedEnglish={store.matchingSelectedEnglish}
            matchingAnswers={store.matchingAnswers}
            onSelectMatchingEnglish={store.selectMatchingEnglish}
            onPairMatchingIndonesian={store.pairMatchingIndonesian}
            onAssignMatchingDirect={store.assignMatchingDirect}
            onUnpairMatching={store.unpairMatching}
            typedAnswer={store.typedAnswer}
            onTypedAnswerChange={store.setTypedAnswer}
            clueStep={store.clueStep}
            onAdvanceClue={store.advanceClueStep}
            scrambledSlots={store.scrambledSlots}
            availableLetters={store.availableLetters}
            onPickScrambledLetter={store.pickScrambledLetter}
            onRemoveScrambledLetter={store.removeScrambledLetter}
            onResetScrambledLetters={store.resetScrambledLetters}
            sentenceBuilderSlots={store.sentenceBuilderSlots}
            availableSentenceChips={store.availableSentenceChips}
            onPickSentenceChip={store.pickSentenceChip}
            onRemoveSentenceChip={store.removeSentenceChip}
            onResetSentenceBuilder={store.resetSentenceBuilder}
            categoryBucketAssignments={store.categoryBucketAssignments}
            selectedCategoryWord={store.selectedCategoryWord}
            onSelectCategoryWord={store.selectCategoryWord}
            onAssignCategoryWord={store.assignCategoryWord}
            onUnassignCategoryWord={store.unassignCategoryWord}
            memoryFlippedIndices={store.memoryFlippedIndices}
            memoryMatchedCards={store.memoryMatchedCards}
            onFlipMemoryCard={store.flipMemoryCard}
            selectedTrueFalse={store.selectedTrueFalse}
            onSelectTrueFalse={store.selectTrueFalse}
            selectedGridCoords={store.selectedGridCoords}
            onToggleGridCoord={store.toggleGridCoord}
            onClearGridCoords={store.clearGridCoords}
            onSelectRemedialOption={store.selectRemedialOption}
            onCheckRemedialAnswer={store.checkRemedialAnswer}
            onRetryRemedialTask={store.retryRemedialTask}
            onNextRemedialTask={store.nextRemedialTask}
            onSpeak={store.speakWord}
          />
        </Suspense>
      </div>
    );
  }

  return (
    <div className="app-root" {...accessibilityAttrs}>
      <AppShell
        profile={store.profile}
        currentTab={store.currentTab}
        selectedPath={store.selectedPath}
        isSoundEnabled={store.isSoundEnabled}
        onSelectTab={store.setCurrentTab}
        onSelectPath={store.switchPath}
        onToggleSound={store.toggleSound}
        onLogout={store.logout}
      >
        {store.storageWarning && (
          <div className="storage-warning-banner" role="alert">
            <span>{store.storageWarning}</span>
            <button
              type="button"
              className="btn btn--secondary daily-limit-banner__close"
              onClick={store.dismissStorageWarning}
            >
              Tutup
            </button>
          </div>
        )}

        {store.dailyLimitError && (
          <div className="daily-limit-banner" role="alert">
            <span>{store.dailyLimitError}</span>
            <button
              type="button"
              className="btn btn--secondary daily-limit-banner__close"
              onClick={() => store.setDailyLimitError(null)}
            >
              Tutup
            </button>
          </div>
        )}

        <Suspense fallback={<ScreenSuspenseFallback />}>
          {store.currentTab === 'map' && (
            <MapScreen
              levels={store.levels}
              activeCompanionId={store.profile.activeCompanionId}
              dailyCompletedCount={store.dailyCompletedCount}
              selectedLevel={store.selectedLevel}
              selectedRegion={store.selectedRegion}
              showFirstTimeGuide={store.showFirstTimeGuide}
              guideStep={store.guideStep}
              isNextStepChipDismissed={store.isNextStepChipDismissed}
              isRegionUnlocked={store.checkRegionUnlocked}
              onSelectLevel={store.setSelectedLevel}
              onSelectRegion={store.setSelectedRegion}
              onStartQuiz={store.startQuizForLevel}
              onAdvanceGuide={store.advanceGuide}
              onCompleteGuide={store.completeGuide}
              onReplayGuide={store.replayGuide}
              onDismissNextStepChip={store.dismissNextStepChip}
            />
          )}

          {store.currentTab === 'camp' && (
            <DashboardScreen
              profile={store.profile}
              levels={store.levels}
              selectedPath={store.selectedPath}
              dailyCompletedCount={store.dailyCompletedCount}
              timeUntilMidnightReset={store.timeUntilMidnightReset}
              dailyVocabAssignments={store.dailyVocabAssignments}
              onContinueToMap={(lvl) => {
                if (lvl) store.setSelectedLevel(lvl);
                store.setCurrentTab('map');
              }}
              onReplayGuide={store.replayGuide}
              onChangePath={store.switchPath}
            />
          )}

          {store.currentTab === 'lexicon' && (
            <VocabularyScreen
              allVocabulary={store.vocabularyList}
              filteredVocabulary={store.filteredVocabulary}
              searchQuery={store.vocabSearchQuery}
              activeCategory={store.vocabCategoryFilter}
              timeUntilMidnightReset={store.timeUntilMidnightReset}
              onSearchChange={store.setVocabSearchQuery}
              onCategoryChange={store.setVocabCategoryFilter}
              onToggleLearned={store.toggleLearnedWord}
              onSpeak={store.speakWord}
            />
          )}

          {store.currentTab === 'lore' && (
            <StoriesScreen
              completedStoryIds={store.storyCompletions}
              activeStory={store.activeStory}
              onOpenStory={store.setActiveStory}
              onCloseStory={() => store.setActiveStory(null)}
              onCompleteStory={store.completeStory}
              onSpeak={store.speakWord}
            />
          )}

          {store.currentTab === 'beasts' && (
            <CompanionsScreen
              companions={store.companionsList}
              activeCompanionId={store.profile.activeCompanionId}
              currentStreak={store.profile.currentStreak}
              onEquipCompanion={store.equipCompanion}
            />
          )}

          {store.currentTab === 'settings' && (
            <SettingsScreen
              profile={store.profile}
              selectedPath={store.selectedPath}
              isSoundEnabled={store.isSoundEnabled}
              highContrast={store.highContrast}
              reducedMotion={store.reducedMotion}
              onToggleSound={store.toggleSound}
              onToggleHighContrast={store.toggleHighContrast}
              onToggleReducedMotion={store.toggleReducedMotion}
              onChangePath={store.switchPath}
              onReplayGuide={store.replayGuide}
              onExportBackup={store.exportProgressBackup}
              onImportBackup={store.importProgressBackup}
              onLogout={store.logout}
            />
          )}
        </Suspense>
      </AppShell>
    </div>
  );
}

export default App;
