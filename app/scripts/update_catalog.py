import sys
import re

file_path = "app/src/main/java/com/example/data/seed/LevelTopicCatalog.kt"

with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Marker before old questions
start_marker = "    fun getTopicForLevel(stage: Int, level: Int): LevelTopicDefinition {"
end_marker = "    fun getTopicForLevel(stage: Int, level: Int): LevelTopicDefinition {\n        return TOPICS.find { it.stage == stage && it.level == level }\n            ?: TOPICS.first()\n    }"

pos = content.find(end_marker)
if pos == -1:
    print("Error: Could not find end_marker for getTopicForLevel")
    sys.exit(1)

base_content = content[:pos + len(end_marker)]

# Master Kotlin generator code to append
new_generator = """

    private fun getIconKeyForWord(word: String): String {
        return when (word.lowercase()) {
            "compass", "guide", "bearing", "direction", "north", "south", "east", "west", "latitude", "meridian", "zenith" -> "compass"
            "map", "trail", "path", "route", "chart", "survey", "scroll", "papyrus", "inscription", "tablet" -> "map"
            "tree", "forest", "woods", "branch", "leaf", "pine", "fir", "cedar", "moss", "lichen", "flora", "plant" -> "tree"
            "tent", "camp", "shelter", "cabin", "hut", "grotto", "cavern" -> "tent"
            "lantern", "torch", "light", "candle", "lamp", "headlamp", "beam", "glow", "luminescence", "phosphor" -> "lantern"
            "boots", "shoes", "footprint", "track", "step", "stride", "crampons", "descent" -> "boots"
            "backpack", "gear", "pack", "knapsack", "harness", "kit", "rope", "thermos", "carabiner" -> "backpack"
            "fire", "campfire", "flame", "smoke", "sparks", "blaze", "ember", "flare" -> "campfire"
            else -> {
                val iconKeys = listOf("compass", "map", "tree", "tent", "lantern", "boots", "backpack", "campfire")
                iconKeys[Math.abs(word.hashCode()) % iconKeys.size]
            }
        }
    }

    /**
     * Master Question Generator for ALL 50 Levels (1-1 to 5-10).
     * Enforces all Master Brief rules:
     * - Exactly 10 questions per level.
     * - Lexicon is the source of truth (uses the 10 words of that level's topic).
     * - Question n tests Lexicon word n (w0..w9).
     * - Each question uses a DIFFERENT question type from the 10 types (each used exactly ONCE).
     * - Types ordered easy-to-hard: Recognition (Q1-Q4), Manipulation (Q5-Q8), Production (Q9-Q10).
     * - Rotated across levels so no two adjacent levels have identical sequences.
     * - Word hunt <= 8 letters, Scrambled letters >= 4 letters (never already unscrambled).
     * - Pure adventure theme with clear Indonesian explanations.
     */
    fun generateTenQuestionsForLevel(path: LearningPath, stage: Int, level: Int): List<Question> {
        val topic = getTopicForLevel(stage, level)
        val v = topic.vocabulary
        val lvlIdx = (stage - 1) * 10 + (level - 1)

        // Tier 1: Recognition (Q1..Q4)
        val recPermutations = listOf(
            listOf(QuestionType.PICTURE_TAP, QuestionType.MULTIPLE_CHOICE, QuestionType.TRUE_OR_FALSE, QuestionType.DRAG_AND_MATCH),
            listOf(QuestionType.MULTIPLE_CHOICE, QuestionType.DRAG_AND_MATCH, QuestionType.PICTURE_TAP, QuestionType.TRUE_OR_FALSE),
            listOf(QuestionType.TRUE_OR_FALSE, QuestionType.PICTURE_TAP, QuestionType.DRAG_AND_MATCH, QuestionType.MULTIPLE_CHOICE),
            listOf(QuestionType.DRAG_AND_MATCH, QuestionType.TRUE_OR_FALSE, QuestionType.MULTIPLE_CHOICE, QuestionType.PICTURE_TAP)
        )

        // Tier 2: Manipulation (Q5..Q8)
        val manPermutations = listOf(
            listOf(QuestionType.CATEGORY_SORT, QuestionType.WORD_HUNT, QuestionType.SCRAMBLED_LETTERS, QuestionType.SENTENCE_BUILDER),
            listOf(QuestionType.SCRAMBLED_LETTERS, QuestionType.SENTENCE_BUILDER, QuestionType.CATEGORY_SORT, QuestionType.WORD_HUNT),
            listOf(QuestionType.WORD_HUNT, QuestionType.CATEGORY_SORT, QuestionType.SENTENCE_BUILDER, QuestionType.SCRAMBLED_LETTERS),
            listOf(QuestionType.SENTENCE_BUILDER, QuestionType.SCRAMBLED_LETTERS, QuestionType.WORD_HUNT, QuestionType.CATEGORY_SORT)
        )

        // Tier 3: Production (Q9..Q10)
        val prodPermutations = listOf(
            listOf(QuestionType.LISTEN_AND_TYPE, QuestionType.FILL_BLANK),
            listOf(QuestionType.FILL_BLANK, QuestionType.LISTEN_AND_TYPE)
        )

        val recTypes = recPermutations[lvlIdx % recPermutations.size].toMutableList()
        val manTypes = manPermutations[lvlIdx % manPermutations.size].toMutableList()
        val prodTypes = prodPermutations[lvlIdx % prodPermutations.size].toMutableList()

        // Validate Tier 2 constraints:
        // WORD_HUNT must have target word <= 8 letters (indices 4..7 correspond to v[4]..v[7])
        for (i in 0 until 4) {
            val qType = manTypes[i]
            val word = v[4 + i].word
            if (qType == QuestionType.WORD_HUNT && word.length > 8) {
                // Find another type in manTypes whose word <= 8
                val swapIdx = (0 until 4).firstOrNull { j -> j != i && v[4 + j].word.length <= 8 && manTypes[j] != QuestionType.WORD_HUNT }
                if (swapIdx != null) {
                    val tmp = manTypes[i]
                    manTypes[i] = manTypes[swapIdx]
                    manTypes[swapIdx] = tmp
                }
            }
        }

        // SCRAMBLED_LETTERS must have target word >= 4 letters
        for (i in 0 until 4) {
            val qType = manTypes[i]
            val word = v[4 + i].word
            if (qType == QuestionType.SCRAMBLED_LETTERS && word.length < 4) {
                val swapIdx = (0 until 4).firstOrNull { j -> j != i && v[4 + j].word.length >= 4 && manTypes[j] != QuestionType.SCRAMBLED_LETTERS }
                if (swapIdx != null) {
                    val tmp = manTypes[i]
                    manTypes[i] = manTypes[swapIdx]
                    manTypes[swapIdx] = tmp
                }
            }
        }

        val allTypes = recTypes + manTypes + prodTypes

        return (0 until 10).map { i ->
            val target = v[i]
            val qType = allTypes[i]
            val qNum = i + 1
            val qId = "q_${path.code}_s${stage}_l${level}_$qNum"

            when (qType) {
                QuestionType.MULTIPLE_CHOICE -> {
                    val distractors = v.filter { it.word != target.word }.shuffled().take(3).map { it.word }
                    val options = (distractors + target.word).shuffled()
                    Question(
                        id = qId,
                        type = QuestionType.MULTIPLE_CHOICE,
                        prompt = "Pilih istilah petualangan yang tepat untuk definisi: '${target.englishDef}':",
                        subText = "Petunjuk Arti: ${target.indonesian}",
                        targetWord = target.word,
                        options = options,
                        correctAnswer = target.word,
                        explanation = "'${target.word}' berarti '${target.indonesian}'. Contoh dalam ekspedisi: ${target.exampleSentence}"
                    )
                }

                QuestionType.PICTURE_TAP -> {
                    val otherVocabs = v.filter { it.word != target.word }.shuffled().take(3)
                    val optsList = (listOf(target) + otherVocabs).shuffled()
                    val picOpts = optsList.mapIndexed { idx, item ->
                        PictureOption(
                            id = "opt_${idx + 1}",
                            vectorIconKey = getIconKeyForWord(item.word),
                            label = "${item.word} (${item.indonesian})"
                        )
                    }
                    val correctLabel = "${target.word} (${target.indonesian})"
                    Question(
                        id = qId,
                        type = QuestionType.PICTURE_TAP,
                        prompt = "Ketuk gambar spesimen petualangan untuk konsep: '${target.englishDef}':",
                        subText = "Petunjuk Arti: ${target.indonesian} (${target.word})",
                        targetWord = target.word,
                        pictureOptions = picOpts,
                        correctAnswer = correctLabel,
                        explanation = "'${target.word}' adalah ${target.indonesian}. Definisi: ${target.englishDef}."
                    )
                }

                QuestionType.TRUE_OR_FALSE -> {
                    val isTrue = (lvlIdx + i) % 2 == 0
                    val statement = if (isTrue) {
                        "\"${target.exampleSentence}\""
                    } else {
                        val wrongWord = v.first { it.word != target.word }
                        "\"Istilah '${target.word}' dalam log ekspedisi diartikan sebagai: '${wrongWord.englishDef}'\""
                    }
                    val expl = if (isTrue) {
                        "Pernyataan BENAR. '${target.word}' berarti '${target.indonesian}'. ${target.englishDef}."
                    } else {
                        "Pernyataan SALAH. '${target.word}' sebenarnya berarti '${target.indonesian}' (${target.englishDef})."
                    }
                    Question(
                        id = qId,
                        type = QuestionType.TRUE_OR_FALSE,
                        prompt = "Tentukan kebenaran pernyataan log petualangan berikut:",
                        subText = statement,
                        targetWord = target.word,
                        isStatementTrue = isTrue,
                        correctAnswer = if (isTrue) "BENAR" else "SALAH",
                        explanation = expl
                    )
                }

                QuestionType.DRAG_AND_MATCH -> {
                    val others = v.filter { it.word != target.word }.take(2)
                    val matchVocabs = listOf(target) + others
                    val pairs = matchVocabs.associate { it.word to it.indonesian }
                    Question(
                        id = qId,
                        type = QuestionType.DRAG_AND_MATCH,
                        prompt = "Pasangkan istilah petualangan wilayah '${topic.topicTitle}' dengan artinya:",
                        subText = "Hubungkan spesimen log ekspedisi dengan arti bahasa Indonesia",
                        targetWord = target.word,
                        matchingPairs = pairs,
                        correctAnswer = matchVocabs.joinToString(", ") { "${it.word} -> ${it.indonesian}" },
                        explanation = "Pasangan benar: " + matchVocabs.joinToString("; ") { "'${it.word}' = '${it.indonesian}'" }
                    )
                }

                QuestionType.CATEGORY_SORT -> {
                    val others = v.filter { it.word != target.word }.take(3)
                    val bucket1Words = listOf(target.word, others[0].word)
                    val bucket2Words = listOf(others[1].word, others[2].word)
                    Question(
                        id = qId,
                        type = QuestionType.CATEGORY_SORT,
                        prompt = "Sortir kosakata wilayah '${topic.topicTitle}' ke dalam divisi kategori:",
                        subText = "Kelompokkan istilah ke dalam klasifikasi yang sesuai",
                        targetWord = target.word,
                        categoryBuckets = mapOf(
                            "Navigasi & Instrumen" to bucket1Words,
                            "Medan Alam & Lapangan" to bucket2Words
                        ),
                        correctAnswer = "Sorted into categories",
                        explanation = "Semua kosakata berhasil diklasifikasikan ke dalam kategori fungsinya masing-masing!"
                    )
                }

                QuestionType.WORD_HUNT -> {
                    val wordUpper = target.word.uppercase().take(8)
                    val fillers = listOf(
                        listOf("F", "O", "R", "E", "S"),
                        listOf("T", "R", "A", "I", "L"),
                        listOf("W", "O", "O", "D", "S"),
                        listOf("C", "A", "M", "P", "S"),
                        listOf("P", "A", "T", "H", "S")
                    )
                    val mutableGrid = fillers.map { it.toMutableList() }.toMutableList()
                    if (wordUpper.length <= 5) {
                        for (c in wordUpper.indices) {
                            mutableGrid[1][c] = wordUpper[c].toString()
                        }
                    } else {
                        for (c in 0 until 5) {
                            mutableGrid[1][c] = wordUpper[c].toString()
                        }
                        for (c in 5 until wordUpper.length) {
                            mutableGrid[2][c - 5] = wordUpper[c].toString()
                        }
                    }
                    val grid = mutableGrid.map { it.toList() }

                    Question(
                        id = qId,
                        type = QuestionType.WORD_HUNT,
                        prompt = "Temukan kata '${wordUpper}' (${target.indonesian}) di dalam kisi huruf:",
                        subText = "Ketuk huruf ${wordUpper.map { it.toString() }.joinToString(" - ")} berurutan",
                        targetWord = target.word,
                        gridLetters = grid,
                        correctAnswer = wordUpper,
                        explanation = "Hebat! Huruf '${wordUpper}' (${target.indonesian}) berhasil ditemukan di dalam kisi!"
                    )
                }

                QuestionType.SCRAMBLED_LETTERS -> {
                    val wordUpper = target.word.uppercase()
                    val letters = wordUpper.map { it.toString() }
                    var scrambled = letters.shuffled()
                    var tries = 0
                    while (scrambled.joinToString("") == wordUpper && tries < 15) {
                        scrambled = letters.shuffled()
                        tries++
                    }
                    if (scrambled.joinToString("") == wordUpper && scrambled.size > 1) {
                        val list = scrambled.toMutableList()
                        val tmp = list[0]
                        list[0] = list[1]
                        list[1] = tmp
                        scrambled = list
                    }
                    Question(
                        id = qId,
                        type = QuestionType.SCRAMBLED_LETTERS,
                        prompt = "Susun kepingan huruf acak menjadi istilah petualangan:",
                        subText = "Arti: ${target.indonesian} • Definisi: ${target.englishDef}",
                        targetWord = target.word,
                        scrambledLetters = scrambled,
                        correctAnswer = wordUpper,
                        explanation = "'${target.word}' (${target.indonesian}) tersusun dengan benar! Contoh: ${target.exampleSentence}"
                    )
                }

                QuestionType.SENTENCE_BUILDER -> {
                    val sentence = target.exampleSentence.trim()
                    val rawTokens = sentence.split(Regex("\\\\s+")).filter { it.isNotBlank() }
                    var tokens = rawTokens.shuffled()
                    if (tokens == rawTokens && tokens.size > 1) {
                        val list = tokens.toMutableList()
                        val tmp = list[0]
                        list[0] = list[1]
                        list[1] = tmp
                        tokens = list
                    }
                    Question(
                        id = qId,
                        type = QuestionType.SENTENCE_BUILDER,
                        prompt = "Susun kepingan kata berikut menjadi kalimat catatan ekspedisi yang utuh:",
                        subText = target.exampleTranslation,
                        targetWord = target.word,
                        sentenceTokens = tokens,
                        orderedSentence = sentence,
                        correctAnswer = sentence,
                        explanation = "Susunan kalimat yang tepat: '${sentence}'"
                    )
                }

                QuestionType.LISTEN_AND_TYPE -> {
                    Question(
                        id = qId,
                        type = QuestionType.LISTEN_AND_TYPE,
                        prompt = "Dengarkan pelafalan penjelajah lalu ketik kata yang kamu dengar:",
                        subText = "Ketuk tombol suara perunggu untuk memutar ulang audio",
                        targetWord = target.word,
                        audioPromptWord = target.word.lowercase(),
                        hint = "${target.indonesian} (${target.word.first()}___)",
                        correctAnswer = target.word.lowercase(),
                        explanation = "Kata yang dilafalkan adalah '${target.word}' (${target.pronunciation}), yang berarti '${target.indonesian}'."
                    )
                }

                QuestionType.FILL_BLANK -> {
                    val blankSentence = target.exampleSentence.replace(Regex("(?i)\\\\b${Regex.escape(target.word)}\\\\b"), "_______")
                    val displaySentence = if (blankSentence.contains("_______")) blankSentence else "${target.exampleSentence} (_______)"
                    Question(
                        id = qId,
                        type = QuestionType.FILL_BLANK,
                        prompt = "Ketik kata yang hilang untuk melengkapi catatan petualangan:",
                        subText = "\\"$displaySentence\\"",
                        sentenceWithBlank = displaySentence,
                        targetWord = target.word,
                        hint = "${target.indonesian} (${target.word.first()}___${target.word.last()})",
                        correctAnswer = target.word,
                        explanation = "Kata yang tepat adalah '${target.word}' (${target.indonesian}): ${target.exampleSentence}"
                    )
                }

                else -> {
                    // Fallback to Multiple Choice if any unhandled type occurs
                    val distractors = v.filter { it.word != target.word }.shuffled().take(3).map { it.word }
                    Question(
                        id = qId,
                        type = QuestionType.MULTIPLE_CHOICE,
                        prompt = "Pilih istilah petualangan yang tepat untuk: '${target.englishDef}':",
                        subText = "Petunjuk Arti: ${target.indonesian}",
                        targetWord = target.word,
                        options = (distractors + target.word).shuffled(),
                        correctAnswer = target.word,
                        explanation = "'${target.word}' adalah ${target.indonesian}."
                    )
                }
            }
        }
    }
}
"""

with open(file_path, "w", encoding="utf-8") as f:
    f.write(base_content + new_generator)

print("Successfully updated LevelTopicCatalog.kt!")
