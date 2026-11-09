const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: Alice can create and read her own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set({
    uid: ALICE_UID,
    displayName: "Alice Explorer",
    email: "alice@gmail.com",
    authProviders: "google",
    learningPathway: "LOW"
  }));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Cross-user isolation: Bob cannot read Alice's profile or progress", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      uid: ALICE_UID,
      displayName: "Alice Explorer",
      email: "alice@gmail.com",
      authProviders: "google",
      learningPathway: "LOW"
    });
    await context.firestore().collection("users").doc(ALICE_UID).collection("learningProgress").doc("main").set({
      currentChapter: 1,
      currentLevel: 1,
      completedLevels: ["1-1"],
      unlockedLevels: ["1-1", "1-2"],
      totalStars: 3,
      totalCompletedLevels: 1
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("learningProgress").doc("main").get());
});

test("Daily limit anti-exploit: Alice cannot write adventureLevelsCompleted > 3", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(ALICE_UID).collection("dailyActivity").doc("2026-11-03").set({
    date: "2026-11-03",
    vocabularyCompleted: 10,
    adventureLevelsCompleted: 4, // Exceeds max 3
    dailyTargetCompleted: true,
    streakQualified: true
  }));
});

test("Daily limit compliance: Alice can write adventureLevelsCompleted <= 3", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("dailyActivity").doc("2026-11-03").set({
    date: "2026-11-03",
    vocabularyCompleted: 10,
    adventureLevelsCompleted: 3, // Valid
    dailyTargetCompleted: true,
    streakQualified: true
  }));
});
