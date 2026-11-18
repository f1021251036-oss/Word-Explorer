ROLE
You are a senior front-end developer and ESL content editor working inside Google AI Studio (Build mode). You are fixing bugs and making targeted improvements in an EXISTING English-learning web app with an adventure theme that is already in this project. Screenshots are attached. If you cannot see them, rely on the descriptions below.

WORKING RULES (STRICT)
- This is a fix, NOT a rewrite. Modify the existing project files only. Keep folder structure, file names, exported names, function names, component names, and data field names. Create new files only when a step needs them.
- Do NOT regenerate the app or any file from scratch. When you change a file, keep everything in it that the step does not touch exactly as it is (imports, state, handlers, styles, data). Never shorten, drop, or simplify existing code.
- READ BEFORE YOU EDIT. Open and read every file you will change, plus the files it imports and the files that import it, in the project's code editor. Never edit from assumptions. Never invent file contents.
- Smallest safe change. Do not rename, reformat, or reorganize unrelated code.
- Do not change scoring, XP, streak, level progress, progress saving, login, or navigation logic, except where a step below explicitly says so.
- Reuse the existing design tokens, fonts, palette (Forest #15370D, Sage #728E6D, Sand #E5CD82, Cream #FFF2B4, White #FFFFFF), and the existing shared button base and sound manager if they exist. Do not introduce new colors, fonts, icon libraries, or new npm dependencies.
- Fix every bug at its ROOT CAUSE in the shared component, not per screen, so it is fixed for ALL levels and ALL question types that use it.
- Complete, working code only. No placeholders, no "rest of the code here", no "// ... existing code ...". Every file you output must be complete and must compile.
- You cannot run a terminal, build, or test commands here. Verify by carefully reading and tracing the code, and by checking that every import, export, prop, and type you touch still matches.
- Make decisions yourself. If something is unclear, choose the most reasonable option, list it under ASSUMPTIONS, and continue. Only stop if a file is truly unreadable.
- If you reach your output limit, stop at a clean boundary (after a fully completed step), write "CONTINUING: Step [n], [what remains]", and continue when I reply "continue". Never leave a file half-written, and never skip or shorten work.

Do STEP 1 to STEP 8 in order, in one run. Each step is ONE point of work. Finish one step completely before starting the next. Then run the QUALITY GATE and write the FINAL REPORT.


STEP 1: ADD A "BACK" BUTTON ON THE LEVEL RESULT SCREEN
Current state: after finishing a level, the result screen ("Sertifikat Wilayah / Wilayah Berhasil Dikuasai!", with the stars) has no way to leave. The lower half of the card is empty.
Do this:
- Add a clearly visible primary button at the bottom area of the result card, for example "KEMBALI KE PETA" (use the app's existing label style and language; if the app has a home/map/level-select screen, go there).
- The button must navigate to the existing level-select / home screen using the existing navigation mechanism. Do not create a new route system.
- Use the shared button base, minimum 44px tap target, clear pressed and focus-visible states, and the existing click sound if one exists.
- Keep the stars, texts, and scoring display unchanged. Make sure the result is saved BEFORE leaving, so progress is never lost.
- Handle the Android back gesture/browser back button so it behaves the same as the new button and never leaves the user on a blank screen.
Acceptance: finishing any level shows the button, tapping it returns to the level list, the progress/stars are saved, and the finished level shows its updated state.

STEP 2: FIX THE CATEGORY SORT QUESTION ("SORTIR KATEGORI")
Observed bug: all four words (Boots, Rope, Strap, Backpack) were placed into the bucket "Navigasi & Instrumen", the other bucket "Medan Alam & Lapangan" stayed empty, and the result card shows a RED header "Analisis Jalur Kurang Tepat!" together with the GREEN-style text "Semua kosakata berhasil diklasifikasikan ke dalam kategori fungsinya masing-masing!". The user says they sorted everything correctly, yet the system marked it wrong.
Do this:
1. Reproduce and find the real cause by reading the code. Check ALL of these: the answer key (word to bucket mapping) in the data; whether the check compares ids vs labels, uses different case or whitespace, or compares object references; whether the check is order-dependent; whether it reads stale state (the last placed word not yet included when the answer is checked); whether the answer key is reversed; and whether the feedback message and the result are driven by two different values.
2. The feedback header, color, icon, and body text must all come from ONE single boolean/result object. A wrong result must never show the success text, and a correct result must never show the failure text. Wrong results should name which words were in the wrong bucket.
3. Check the content: the buckets must make sense for the words. Gear items such as Boots, Rope, Strap, and Backpack do not belong in "Navigasi & Instrumen". If the data places all words of a question in one bucket, or the bucket names do not fit the words, redesign the buckets and answer key so that: the level's words are split across at least 2 buckets, every word belongs to exactly ONE bucket without ambiguity, and bucket names are clear. Use ONLY existing Lexicon words of that level. Do not invent vocabulary. List every changed answer key under ASSUMPTIONS.
4. Review every other category-sort question in all levels for the same problems.
Acceptance: placing every word into its correct bucket always shows the success state; any wrong placement shows the failure state with the right explanation; the result is the same no matter the order of placement.

STEP 3: REBUILD THE WORD HUNT ("CARI KATA") GRID
Observed bug: the grid is not a real puzzle. The answer sits in a row of its own (K-N-I-F-E in row 2), every other row is also a plain word (FORES, WOODS, CAMPS, PATHS), and the instruction literally spells the answer ("Ketuk huruf K - N - I - F - E berurutan").
Do this:
- Generate the grid with the target word hidden at a random position and direction (horizontal, vertical, and diagonal; reversed directions only in harder levels), and fill every other cell with random letters. Do NOT make rows of readable words. Optionally hide 1 or 2 other Lexicon words of the same level as decoys.
- Remove the spelled-out instruction. Do not show the target word or its letters in the prompt. Instead, show an indirect clue from Step 7 and the number of letters (for example "Kata dengan 5 huruf"). The user must find the word.
- Generate the grid once per question (use a seed or store it in state) so it does NOT reshuffle on every re-render, answer check, or resize.
- Validate by CELL POSITIONS: the selected cells must form a contiguous straight line that spells the target word (any valid occurrence). Do not validate only by string order of tapped letters.
- Keep: the "Kata Terpilih" display, the Reset button, tap and keyboard support, and the existing scoring. Make sure random fill letters never create an accidental second copy of the target word.
- Use this only for words of 8 letters or fewer, as before. Grid size stays about 6x6 to 8x8 and must fit 320px width with no overflow.
Acceptance: two plays of the same question show different, believable letter grids; the answer is never in a fixed row; the target word is never printed in the question.

STEP 4: FIX THE TEXT INPUT BORDER (ALL TYPING QUESTIONS)
Observed bug: when the user starts typing (fill in the blank, listen and type, and any other typing question), the input's outline breaks: a thick dark line appears across the top starting after the placeholder text, there is a stray curved stroke at the top-left corner, and the border no longer follows the rounded shape of the field.
Do this:
- Audit the input component: floating label or fieldset/legend notch, ::before/::after pseudo-elements, focus and :not(:placeholder-shown) styles, outline, box-shadow, border-radius mismatches between layers, and any transform or translate on the label.
- Replace it with ONE shared input component/class used by every typing question. Rules: a single border that follows one border-radius (inner layers use border-radius: inherit); the label or placeholder never overlaps or cuts the border; focus is shown with a ring or color change that follows the same shape; no layout shift or size change between empty, focused, filled states.
- States to verify: empty, focused, filled, correct, wrong, disabled, with short and very long text, at 320px, 375px, and 768px width.
Acceptance: the outline looks identical before, during, and after typing, in every typing question in every level.

STEP 5: FIX THE "PERIKSA JAWABAN" (CHECK ANSWER) BUTTON, ALL LEVELS
Observed bug: in the disabled state the button looks blurry and layered, like 3 shapes stacked on top of each other: an outer rounded shape, a darker offset layer, and a lighter square-cornered inner rectangle behind the label. In the enabled state it also has stacked gold and dark outline layers.
Do this:
- Audit every layer: stacked box-shadows, ::before/::after elements, inner wrappers with their own background, opacity applied on both the parent and the children (this creates the double image), filter/blur, text-shadow, background-clip, and square-cornered inner elements inside a rounded button.
- Rebuild the button with the shared button base: ONE rounded shape, ONE border, ONE hard offset shadow (same radius), and no inner rectangle. Every layer must follow the same border-radius.
- Disabled state: a single flat muted Sage tone with readable text (WCAG AA), no stacked effect, no opacity trick that duplicates layers, cursor not-allowed. Enabled, hover, pressed, and focus-visible states must be crisp and clearly different from disabled. Pressed moves the button down by the shadow offset.
- Use the same fix wherever the same button style exists (other "check", "continue", and "next" buttons).
Acceptance: at 320px, 375px, 768px, and 200% zoom, the button shows no ghosting, blur, or inner rectangle in any state, in every level and question type.

STEP 6: (reserved) no change.

STEP 7: INDIRECT CLUES INSTEAD OF DIRECT TRANSLATIONS
Observed problem: the hint button ("Butuh Petunjuk Arti?") currently gives the Indonesian meaning directly (for Rope it shows "tali"), so it gives the answer away.
Do this:
- For every Lexicon word, write a clue in simple, natural Indonesian that describes FUNCTION, USE, CHARACTERISTICS, or SITUATION, never the translation. Store it in a new data field (for example clue and clue2) next to the word's data, without renaming existing fields. Example for Rope: "Digunakan untuk memanjat dan bisa diikat pada pohon atau batu." Make descriptions rich and specific enough that a learner can guess, but not obvious.
- Two-step hint: the first tap shows clue (function/use). A second tap shows clue2 (extra detail such as where or when it is used, plus the number of letters). Each step is optional and shown only when tapped.
- Forbidden inside any clue: the English target word, its Indonesian translation or close synonyms, a part of the word, and the word spelled out. Clues for different words must be distinguishable from each other.
- Replace the direct-meaning hint in ALL question types that have a hint button (fill in the blank, listen and type, scrambled letters, word hunt, and others). Update the label from "Butuh Petunjuk Arti?" to something like "Butuh Kisi-kisi?".
- HIGHEST LEVEL: determine the highest/last level from the data (do not hard-code a number) and show NO hint button and NO clue in that level. Keep this as a simple config flag per level (for example hintsEnabled) so I can change it later. State in the report which level you detected as the highest.
- Add a small self-check function (for example validateClues) next to the data that, for every word, verifies clue and clue2 exist, are non-empty, and do not contain the English word or the Indonesian translation (case-insensitive). Make it log any failure with console.error in development only, and fix every failure you can find by reading the data.
Do not change the Lexicon's existing word, meaning, or example fields. Only add the clue fields.

STEP 8: DAILY VOCABULARY ROTATION IN THE LEXICON
Do this:
- At the top of the Lexicon screen, add a "Kata Hari Ini" (Words of the Day) section that shows a different set of words every day, taken from the existing Lexicon only. Do not add or edit Lexicon words.
- Show DAILY_WORD_COUNT = 5 words per day (a single constant I can change). Each card shows the word, its Indonesian meaning, the example sentence if one exists, and the existing audio/pronunciation button.
- Selection must be deterministic and based on the device's LOCAL date (YYYY-MM-DD): the same day gives the same words on every open, and the set changes at local midnight. Use a seeded shuffle and cycle through ALL words before any word repeats. If the Lexicon has fewer words than the count, show them all.
- If the app stays open past midnight, refresh the section when the date changes (check on visibilitychange and on a timer).
- Show the date and a small "Kata baru dalam HH:MM" countdown.
- Style it with the existing design system. Do not change the rest of the Lexicon list, the scoring, or the quiz.
Acceptance: opening the Lexicon on two different days shows two different sets; reopening the same day shows the same set; changing the device date to the next day changes the set; no word repeats until the cycle completes.


QUALITY GATE (RUN BEFORE THE FINAL REPORT)
- You cannot run commands, so trace each code path manually and list what you verified: every changed file imports and exports correctly, there are no unused or missing variables, no type errors, and no broken references to renamed or removed code.
- Verify: the app starts; a full level still plays from first to last question; XP, streak, stars, and progress still save; no runtime errors you introduced; no layout overflow at 320px.
- Be honest about what you verified and what you could NOT verify.

FINAL REPORT
1. What was completed, per step
2. Root cause found for Step 2 and for Steps 4 and 5
3. ASSUMPTIONS (changed category answer keys, detected highest level, daily word count, anything else)
4. Files created or changed
5. Quality Gate results
6. 6 manual checks I should do on my phone (using the AI Studio preview)

START NOW: read the project, then do Step 1 through Step 8 in order.
