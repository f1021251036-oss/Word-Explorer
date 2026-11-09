import sys
import re

file_path = "app/src/main/java/com/example/data/seed/LevelTopicCatalog.kt"
with open(file_path, "r", encoding="utf-8") as f:
    code = f.read()

# Let's extract all 50 level definitions from LevelTopicCatalog.kt
# Stages 1 and 2 are explicit LevelTopicDefinition blocks
# Stages 3, 4, 5 are loops with generateChapter3Vocabs, generateChapter4Vocabs, generateChapter5Vocabs

# Let's parse Stage 1 & 2 vocabs
stage_level_vocabs = {}

# Parse Stage 1 & 2
pattern_s12 = r'LevelTopicDefinition\s*\(\s*stage\s*=\s*(\d+),\s*level\s*=\s*(\d+).*?vocabulary\s*=\s*listOf\s*\((.*?)\)\s*\)'
for m in re.finditer(pattern_s12, code, re.DOTALL):
    stg = int(m.group(1))
    lvl = int(m.group(2))
    voc_block = m.group(3)
    words = re.findall(r'vocab\s*\(\s*"[^"]+",\s*"([^"]+)"', voc_block)
    stage_level_vocabs[(stg, lvl)] = words

# Parse Chapter 3
ch3_match = re.search(r'generateChapter3Vocabs\(lvl:\s*Int\).*?val sets = listOf\((.*?)\n\s*\)', code, re.DOTALL)
if ch3_match:
    lines = ch3_match.group(1).strip().split('\n')
    for lvl in range(1, 11):
        line = lines[lvl - 1]
        raw_words = re.findall(r'"([^"]+)"', line)
        # raw_words is [w, indo, w, indo, ...]
        w_list = [raw_words[i*2] for i in range(10)]
        stage_level_vocabs[(3, lvl)] = w_list

# Parse Chapter 4
ch4_match = re.search(r'generateChapter4Vocabs\(lvl:\s*Int\).*?val wordBank = listOf\((.*?)\n\s*\)', code, re.DOTALL)
if ch4_match:
    lines = ch4_match.group(1).strip().split('\n')
    for lvl in range(1, 11):
        line = lines[lvl - 1]
        raw_words = re.findall(r'"([^"]+)"', line)
        w_list = [raw_words[i*2] for i in range(10)]
        stage_level_vocabs[(4, lvl)] = w_list

# Parse Chapter 5
ch5_match = re.search(r'generateChapter5Vocabs\(lvl:\s*Int\).*?val wordBank = listOf\((.*?)\n\s*\)', code, re.DOTALL)
if ch5_match:
    lines = ch5_match.group(1).strip().split('\n')
    for lvl in range(1, 11):
        line = lines[lvl - 1]
        raw_words = re.findall(r'"([^"]+)"', line)
        w_list = [raw_words[i*2] for i in range(10)]
        stage_level_vocabs[(5, lvl)] = w_list

print(f"Total levels extracted from Catalog: {len(stage_level_vocabs)} / 50")

# Now simulate generateTenQuestionsForLevel for all 50 levels exactly as written in Kotlin
recPermutations = [
    ["PICTURE_TAP", "MULTIPLE_CHOICE", "TRUE_OR_FALSE", "DRAG_AND_MATCH"],
    ["MULTIPLE_CHOICE", "DRAG_AND_MATCH", "PICTURE_TAP", "TRUE_OR_FALSE"],
    ["TRUE_OR_FALSE", "PICTURE_TAP", "DRAG_AND_MATCH", "MULTIPLE_CHOICE"],
    ["DRAG_AND_MATCH", "TRUE_OR_FALSE", "MULTIPLE_CHOICE", "PICTURE_TAP"]
]

manPermutations = [
    ["CATEGORY_SORT", "WORD_HUNT", "SCRAMBLED_LETTERS", "SENTENCE_BUILDER"],
    ["SCRAMBLED_LETTERS", "SENTENCE_BUILDER", "CATEGORY_SORT", "WORD_HUNT"],
    ["WORD_HUNT", "CATEGORY_SORT", "SENTENCE_BUILDER", "SCRAMBLED_LETTERS"],
    ["SENTENCE_BUILDER", "SCRAMBLED_LETTERS", "WORD_HUNT", "CATEGORY_SORT"]
]

prodPermutations = [
    ["LISTEN_AND_TYPE", "FILL_BLANK"],
    ["FILL_BLANK", "LISTEN_AND_TYPE"]
]

total_questions = 0
type_counts = {}
coverage_table = []
errors = []

for stg in range(1, 6):
    for lvl in range(1, 11):
        vocabs = stage_level_vocabs.get((stg, lvl), [])
        if len(vocabs) != 10:
            errors.append(f"Level {stg}-{lvl} does not have exactly 10 vocabs, found {len(vocabs)}")
            continue

        lvlIdx = (stg - 1) * 10 + (lvl - 1)
        recTypes = list(recPermutations[lvlIdx % len(recPermutations)])
        manTypes = list(manPermutations[lvlIdx % len(manPermutations)])
        prodTypes = list(prodPermutations[lvlIdx % len(prodPermutations)])

        # Apply constraints
        for i in range(4):
            qType = manTypes[i]
            word = vocabs[4 + i]
            if qType == "WORD_HUNT" and len(word) > 8:
                swapIdx = next((j for j in range(4) if j != i and len(vocabs[4 + j]) <= 8 and manTypes[j] != "WORD_HUNT"), None)
                if swapIdx is not None:
                    manTypes[i], manTypes[swapIdx] = manTypes[swapIdx], manTypes[i]

        for i in range(4):
            qType = manTypes[i]
            word = vocabs[4 + i]
            if qType == "SCRAMBLED_LETTERS" and len(word) < 4:
                swapIdx = next((j for j in range(4) if j != i and len(vocabs[4 + j]) >= 4 and manTypes[j] != "SCRAMBLED_LETTERS"), None)
                if swapIdx is not None:
                    manTypes[i], manTypes[swapIdx] = manTypes[swapIdx], manTypes[i]

        allTypes = recTypes + manTypes + prodTypes

        # Rule a: exactly 10 questions
        if len(allTypes) != 10:
            errors.append(f"Level {stg}-{lvl} question count != 10")

        # Rule d: no question type appears twice consecutively & exactly once each
        if len(set(allTypes)) != 10:
            errors.append(f"Level {stg}-{lvl} contains duplicate question types: {allTypes}")

        for i in range(len(allTypes) - 1):
            if allTypes[i] == allTypes[i + 1]:
                errors.append(f"Level {stg}-{lvl} has consecutive identical types: {allTypes[i]}")

        # Rule b & R1: Question n tests Lexicon word n
        for i in range(10):
            target_word = vocabs[i]
            qType = allTypes[i]
            total_questions += 1
            type_counts[qType] = type_counts.get(qType, 0) + 1

            if qType == "WORD_HUNT" and len(target_word) > 8:
                errors.append(f"Level {stg}-{lvl} Q{i+1} WORD_HUNT target '{target_word}' length {len(target_word)} > 8")

            if qType == "SCRAMBLED_LETTERS" and len(target_word) < 4:
                errors.append(f"Level {stg}-{lvl} Q{i+1} SCRAMBLED_LETTERS target '{target_word}' length {len(target_word)} < 4")

            coverage_table.append({
                "stage": stg,
                "level": lvl,
                "qNum": i + 1,
                "type": qType,
                "targetWord": target_word
            })

print(f"Validation finished. Total questions checked: {total_questions} (Expected 500)")
print("Question Type Counts & Percentages across all 50 levels:")
for qType, count in sorted(type_counts.items()):
    pct = (count / total_questions) * 100
    print(f"  - {qType:20s}: {count:3d} ({pct:5.1f}%)")

mc_pct = (type_counts.get("MULTIPLE_CHOICE", 0) / total_questions) * 100
if mc_pct > 15.0:
    errors.append(f"MULTIPLE_CHOICE share {mc_pct:.1f}% exceeds maximum 15.0%")

if errors:
    print(f"FAILED with {len(errors)} errors:")
    for err in errors[:20]:
        print("  - " + err)
    sys.exit(1)
else:
    print("ALL 50 LEVELS AND 500 QUESTIONS PASSED ALL VALIDATION CHECKS WITH ZERO ERRORS!")
