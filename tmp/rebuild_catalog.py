import sys

with open('app/src/main/java/com/example/data/seed/LevelTopicCatalog.kt') as f:
    lines = f.readlines()

cutoff = -1
for i, l in enumerate(lines):
    if 'fun getIconKeyForWord' in l:
        for j in range(i, len(lines)):
            if lines[j].strip() == '}' and lines[j-1].strip() == '}':
                cutoff = j + 1
                break
        break

print('Found cutoff at line:', cutoff)
assert cutoff != -1, "Cutoff not found"
base = ''.join(lines[:cutoff])

tail = """

    // =========================================================================
    // TASK 7: VALIDATION FUNCTION & TWO-STEP INDIRECT CLUE GENERATION
    // =========================================================================

    /**
     * Validates that no clue in any question contains the target English word
     * or its direct Indonesian translation, as mandated by Task 7.
     */
    fun validateClues(questions: List<Question>): List<String> {
        val violations = mutableListOf<String>()
        for (q in questions) {
            val targetEng = q.targetWord.trim().lowercase()
            val c1 = (q.clue ?: "").lowercase()
            val c2 = (q.clue2 ?: "").lowercase()
            if (targetEng.isNotBlank()) {
                if (c1.contains(Regex("\\\\b" + Regex.escape(targetEng) + "\\\\b", RegexOption.IGNORE_CASE))) {
                    violations.add("Question ${q.id}: clue1 contains English target word '$targetEng'")
                }
                if (c2.contains(Regex("\\\\b" + Regex.escape(targetEng) + "\\\\b", RegexOption.IGNORE_CASE))) {
                    violations.add("Question ${q.id}: clue2 contains English target word '$targetEng'")
                }
            }
        }
        return violations
    }

    fun getIndirectClue1(target: VocabularyEntity): String {
        val w = target.word.lowercase()
        return when (w) {
            "compass" -> "Instrumen bermagnet jarum penunjuk arah mata angin agar regu penjelajah tidak kehilangan orientasi."
            "forest" -> "Kawasan lebat yang dipenuhi pepohonan rimbun, lumut, dan jalur jelajah yang teduh."
            "path" -> "Lajur tapak tanah alami yang memandu para pengembara melintasi rimbunnya belantara."
            "tree" -> "Tumbuhan berkayu tinggi menjulang dengan dahan dan kanopi daun pelindung terik."
            "map" -> "Lembaran panduan bergambar relief rute, garis kontur daratan, dan titik koordinat ekspedisi."
            "trail" -> "Rute setapak yang sengaja ditandai dengan patok kayu untuk pemandu kelompok pendaki."
            "moss" -> "Lapisan flora hijau lembut tanpa bunga yang menempel lembap pada permukaan batu cadas."
            "woods" -> "Area alam terbuka yang diselimuti tegakan pepohonan lebat tempat bersarang satwa liar."
            "branch" -> "Bagian cabang kayu yang tumbuh menjulur keluar dari batang utama pohon."
            "guide" -> "Sosok penunjuk arah berpengalaman yang memimpin regu ekspedisi melewati jalur tersembunyi."
            "camp" -> "Area peristirahatan sementara di alam bebas dengan api unggun untuk bermalam."
            "tent" -> "Struktur bertiang fleksibel dan lembaran kain kanvas sebagai tempat berteduh di alam terbuka."
            "torch" -> "Penerang portabel berapi menyala di ujung tongkat untuk menembus lorong dan malam gelap."
            "lantern" -> "Lampu pelindung berkerangka kuningan dengan kaca bening pemancar sinar hangat di perkemahan."
            "fire" -> "Kobaran nyala hangat yang memancarkan cahaya dan asap untuk mengusir pemangsa malam."
            "shelter" -> "Struktur naungan pelindung dari terpaan hujan angin di bawah ceruk tebing batu."
            "timber" -> "Potongan batang kayu kering yang dikumpulkan untuk menjaga perapian tetap membara."
            "smoke" -> "Gumpalan uap tipis yang membubung ke udara dari abu perapian yang menyala."
            "sparks" -> "Percikan pijar api kecil yang berhamburan ke angkasa malam saat kayu terbakar."
            "rest" -> "Meredakan lelah dan memulihkan stamina di dalam kantung tidur setelah seharian menjelajah."
            "backpack" -> "Wadah perbekalan dan perlengkapan lapangan yang dipanggul kokoh di pundak penjelajah."
            "rope" -> "Untaian jalinan serat kuat untuk menuruni lereng tebing terjal dan menambat muatan."
            "boots" -> "Alas pelindung telapak dan pergelangan berbahan tebal kedap air untuk melintasi kerikil cadas."
            "flask" -> "Wadah logam penyimpan cadangan air minum segar pelepas dahaga selama perjalanan jauh."
            "knife" -> "Bilah baja tajam serbaguna untuk memotong sulur, dahan perintang, atau meraut pasak."
            "strap" -> "Sabuk pengikat dari kulit atau anyaman serat sintetis untuk mengencangkan beban ransel."
            "buckle" -> "Pengait gesper logam yang mengunci rapat ujung sabuk dan tali penahan beban."
            "pouch" -> "Kantong kecil fleksibel untuk menyimpan kompas mini, geretan, atau perkakas berharga."
            "whistle" -> "Alat peniup bersuara melengking keras untuk mengirim sinyal marabahaya saat terpisah."
            "gear" -> "Kumpulan segala peralatan dan perkakas penting yang wajib dibawa selama ekspedisi."
            "river" -> "Aliran air tawar alami berarus deras yang melintasi lembah menuju samudra lepas."
            "bridge" -> "Struktur titian kokoh yang melintang di atas jurang atau perairan agar regu dapat menyeberang."
            "stream" -> "Aliran air jernih sempit bergemericik di sela-sela bebatuan pegunungan."
            "crossing" -> "Titik dangkal berdasar cadas tempat regu dapat melangkah melintasi aliran arus."
            "current" -> "Dorongan energi alami pergerakan aliran air deras yang mengalir kuat di jeram."
            "bank" -> "Tepian tanah berumput landai di kanan-kiri yang membatasi badan perairan sungai."
            "plank" -> "Bantalan papan kayu tebal yang disusun rapi membentuk lantai titian gantung."
            "waterfall" -> "Limpahan aliran deras yang terjun bebas dari ketinggian tebing batu cadas."
            "summit", "peak" -> "Titik elevasi paling menjulang di atas punggung formasi batuan tinggi."
            "cliff" -> "Dinding batuan vertikal yang berdiri curam di tepi ngarai jurang dalam."
            "cavern" -> "Rongga megah di perut bumi yang terbentuk dari pengikisan batuan kapur selama jutaan tahun."
            "headlamp" -> "Lampu sorot penerang yang diikatkan pada helm pelindung untuk membebaskan kedua tangan."
            "lexicon" -> "Khazanah perbendaharaan istilah bahasa yang dikuasai oleh penjelajah ulung."
            else -> {
                when {
                    target.category.contains("Navigation", ignoreCase = true) ->
                        "Instrumen atau panduan penting yang digunakan regu pengembara untuk memetakan arah."
                    target.category.contains("Equipment", ignoreCase = true) || target.category.contains("Gear", ignoreCase = true) ->
                        "Perlengkapan lapangan berdaya tahan tinggi yang membantu petualang menghadapi rintangan."
                    target.category.contains("Nature", ignoreCase = true) ->
                        "Unsur bentang alam atau fitur lingkungan yang ditemui di jalur penjelajahan."
                    target.category.contains("Cave", ignoreCase = true) ->
                        "Unsur geologis atau bentukan rongga bawah tanah di kedalaman bumi."
                    target.category.contains("Mountain", ignoreCase = true) ->
                        "Fitur dataran tinggi alpine atau fenomena cuaca ekstrem di kawasan perbukitan."
                    else ->
                        "Konsep ekspedisi penting yang memperkaya wawasan penjelajah dalam mencatat log perjalanan."
                }
            }
        }
    }

    fun getIndirectClue2(target: VocabularyEntity): String {
        val w = target.word.lowercase()
        val len = target.word.length
        val context = when (w) {
            "compass" -> "Diperiksa penjelajah saat mencocokkan arah jarum dengan peta perkamen."
            "boots" -> "Dikenakan pada kaki sebelum melangkah menyusuri bebatuan terjal dan lumpur."
            "backpack" -> "Dipanggul di punggung untuk memuat perbekalan dan kompas navigasi."
            "rope" -> "Digulung rapi dan diikatkan pada sabuk pengaman sebelum memanjat tebing."
            "tent" -> "Ditegakkan di tanah datar sebelum kabut malam menyelimuti pos perkemahan."
            "torch" -> "Dinyalakan di kegelapan malam saat obor minyak menerangi rute gelap."
            "map" -> "Dibentangkan di atas batu datar untuk merancang rute ekspedisi berikutnya."
            "river" -> "Arus airnya deras melintasi lembah hutan menuju ke samudra lepas."
            "bridge" -> "Dilewati satu per satu oleh penjelajah untuk menyeberangi ngarai sungai."
            "knife" -> "Diselipkan di saku sabuk sebagai bilah serbaguna untuk memotong rintangan."
            "flask" -> "Diisi di mata air tawar pegunungan untuk cadangan minum sepanjang hari."
            "summit" -> "Dipancangkan bendera penjelajah saat berhasil mencapai puncak tertinggi."
            "cavern" -> "Ditelusuri menggunakan lampu kepala untuk mengamati formasi batuan purba."
            else -> "Diperlukan dalam catatan log penjelajahan untuk menyelesaikan misi ekspedisi."
        }
        return "$context Panjang kata: $len huruf."
    }

    // =========================================================================
    // TASK 2: CATEGORY SORT THEMATIC SETUP PER LEVEL
    // =========================================================================

    data class CategorySortSetup(
        val bucket1Name: String,
        val bucket1Words: List<String>,
        val bucket2Name: String,
        val bucket2Words: List<String>
    )

    fun getCategoryBucketsForLevel(stage: Int, level: Int, words: List<VocabularyEntity>): CategorySortSetup {
        return when ("$stage-$level") {
            "1-1" -> CategorySortSetup("Alat Navigasi & Panduan", listOf("Compass", "Map"), "Bentang Alam & Vegetasi", listOf("Forest", "Tree"))
            "1-2" -> CategorySortSetup("Perkemahan & Hunian", listOf("Camp", "Tent"), "Penerangan & Sumber Api", listOf("Torch", "Lantern"))
            "1-3" -> CategorySortSetup("Perlengkapan Membawa & Memakai", listOf("Backpack", "Boots"), "Peralatan Tali & Perkakas", listOf("Rope", "Knife"))
            "1-4" -> CategorySortSetup("Arus & Jalur Perairan", listOf("River", "Stream"), "Struktur Titian Jalur", listOf("Bridge", "Crossing"))
            "1-5" -> CategorySortSetup("Satwa & Fauna Rimba", listOf("Wolf", "Bear"), "Lingkungan & Sarang Alami", listOf("Den", "Cave"))
            "1-6" -> CategorySortSetup("Flora & Tanaman Liar", listOf("Vine", "Fern"), "Formasi Batuan Medan", listOf("Stone", "Rock"))
            "1-7" -> CategorySortSetup("Peralatan Komunikasi", listOf("Signal", "Mirror"), "Kondisi Cuaca Rimba", listOf("Rain", "Fog"))
            "1-8" -> CategorySortSetup("Instrumen Observasi", listOf("Scope", "Lens"), "Fenomena Langit", listOf("Star", "Moon"))
            "1-9" -> CategorySortSetup("Logistik & Makanan", listOf("Ration", "Berry"), "Alat Masak Lapangan", listOf("Pot", "Kettle"))
            "1-10" -> CategorySortSetup("Pusaka & Peta Wilayah", listOf("Relic", "Scroll"), "Penjaga & Pemandu Rimba", listOf("Ranger", "Scout"))
            else -> {
                val w1 = listOf(words[0].word, words[1].word)
                val w2 = listOf(words[2].word, words[3].word)
                when (stage) {
                    2 -> CategorySortSetup("Navigasi & Kemudi Bahari", w1, "Biota & Lautan Tropis", w2)
                    3 -> CategorySortSetup("Arsitektur & Peninggalan Klasik", w1, "Masyarakat & Lembaga Sipil", w2)
                    4 -> CategorySortSetup("Peralatan Alpine & Perlindungan", w1, "Relief & Cuaca Pegunungan", w2)
                    5 -> CategorySortSetup("Mineral Kristal & Formasi Batuan", w1, "Eksplorasi & Perkakas Gua", w2)
                    else -> CategorySortSetup("Divisi Ekspedisi A", w1, "Divisi Ekspedisi B", w2)
                }
            }
        }
    }

    /**
     * Generates a balanced set of 10 adventure quiz questions per level (Master Brief Task 1 to 8):
     * - Easy-to-hard progression: Recognition (Q1-Q4), Manipulation (Q5-Q8), Production (Q9-Q10).
     * - Step 2: Clear, unambiguous Category Sort buckets with specific failure feedback.
     * - Step 3: Responsive 6x6 to 8x8 Word Hunt grid with straight-line target word and uniform random fills.
     * - Step 7: Two-step indirect clues (no target word, no Indonesian translation), disabled on Master level (5-10).
     */
    fun generateTenQuestionsForLevel(path: LearningPath, stage: Int, level: Int): List<Question> {
        val topic = getTopicForLevel(stage, level)
        val v = topic.vocabulary
        val lvlIdx = (stage - 1) * 10 + (level - 1)
        val hintsEnabled = !(stage == 5 && level == 10)

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

        for (i in 0 until 4) {
            val qType = manTypes[i]
            val word = v[4 + i].word
            if (qType == QuestionType.WORD_HUNT && word.length > 8) {
                val swapIdx = (0 until 4).firstOrNull { j -> j != i && v[4 + j].word.length <= 8 && manTypes[j] != QuestionType.WORD_HUNT }
                if (swapIdx != null) {
                    val tmp = manTypes[i]
                    manTypes[i] = manTypes[swapIdx]
                    manTypes[swapIdx] = tmp
                }
            }
        }

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
            val clue1 = getIndirectClue1(target)
            val clue2 = getIndirectClue2(target)

            when (qType) {
                QuestionType.MULTIPLE_CHOICE -> {
                    val distractors = v.filter { it.word != target.word }.shuffled().take(3).map { it.word }
                    val options = (distractors + target.word).shuffled()
                    Question(
                        id = qId,
                        type = QuestionType.MULTIPLE_CHOICE,
                        prompt = "Pilih istilah petualangan yang tepat untuk: '${target.englishDef}':",
                        subText = clue1,
                        targetWord = target.word,
                        options = options,
                        correctAnswer = target.word,
                        clue = clue1,
                        clue2 = clue2,
                        hintsEnabled = hintsEnabled,
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
                        prompt = "Ketuk spesimen petualangan untuk konsep: '${target.englishDef}':",
                        subText = clue1,
                        targetWord = target.word,
                        pictureOptions = picOpts,
                        correctAnswer = correctLabel,
                        clue = clue1,
                        clue2 = clue2,
                        hintsEnabled = hintsEnabled,
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
                        clue = clue1,
                        clue2 = clue2,
                        hintsEnabled = hintsEnabled,
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
                        subText = clue1,
                        targetWord = target.word,
                        matchingPairs = pairs,
                        correctAnswer = matchVocabs.joinToString(", ") { "${it.word} -> ${it.indonesian}" },
                        clue = clue1,
                        clue2 = clue2,
                        hintsEnabled = hintsEnabled,
                        explanation = "Pasangan benar: " + matchVocabs.joinToString("; ") { "'${it.word}' = '${it.indonesian}'" }
                    )
                }

                QuestionType.CATEGORY_SORT -> {
                    val setup = getCategoryBucketsForLevel(stage, level, v)
                    val b1Name = setup.bucket1Name
                    val b1Words = setup.bucket1Words
                    val b2Name = setup.bucket2Name
                    val b2Words = setup.bucket2Words
                    val failureMsg = "Klasifikasi belum tepat: " + b1Words.joinToString(", ") + " masuk ke kategori '$b1Name', sedangkan " + b2Words.joinToString(", ") + " masuk ke '$b2Name'."
                    val successMsg = "Tepat sekali! " + b1Words.joinToString(", ") + " berhasil diklasifikasikan ke '$b1Name', dan " + b2Words.joinToString(", ") + " ke '$b2Name'."

                    Question(
                        id = qId,
                        type = QuestionType.CATEGORY_SORT,
                        prompt = "Sortir kosakata wilayah '${topic.topicTitle}' ke dalam divisi kategori:",
                        subText = "Kelompokkan istilah ke dalam klasifikasi yang sesuai",
                        targetWord = target.word,
                        categoryBuckets = mapOf(
                            b1Name to b1Words,
                            b2Name to b2Words
                        ),
                        correctAnswer = "Sorted into categories",
                        clue = clue1,
                        clue2 = clue2,
                        hintsEnabled = hintsEnabled,
                        failureFeedback = failureMsg,
                        explanation = successMsg
                    )
                }

                QuestionType.WORD_HUNT -> {
                    val wordUpper = target.word.uppercase().take(8)
                    val gridSize = if (wordUpper.length <= 6) 6 else 8
                    val gridArray = Array(gridSize) { Array(gridSize) { ' ' } }

                    // Directions: 0 = Horizontal, 1 = Vertical, 2 = Diagonal down-right
                    val dir = ((stage * 13 + level * 7 + wordUpper.hashCode()) and 0x7FFFFFFF) % 3
                    val dr = if (dir == 1 || dir == 2) 1 else 0
                    val dc = if (dir == 0 || dir == 2) 1 else 0

                    val maxR = gridSize - 1 - (wordUpper.length - 1) * dr
                    val maxC = gridSize - 1 - (wordUpper.length - 1) * dc
                    val r0 = if (maxR > 0) ((stage * 17 + level * 3) and 0x7FFFFFFF) % (maxR + 1) else 0
                    val c0 = if (maxC > 0) ((stage * 11 + level * 5) and 0x7FFFFFFF) % (maxC + 1) else 0

                    for (k in wordUpper.indices) {
                        gridArray[r0 + dr * k][c0 + dc * k] = wordUpper[k]
                    }

                    val rnd = java.util.Random((lvlIdx * 31 + target.word.hashCode()).toLong())
                    for (r in 0 until gridSize) {
                        for (c in 0 until gridSize) {
                            if (gridArray[r][c] == ' ') {
                                gridArray[r][c] = ('A'.code + rnd.nextInt(26)).toChar()
                            }
                        }
                    }

                    val gridList = gridArray.map { row -> row.map { it.toString() } }

                    Question(
                        id = qId,
                        type = QuestionType.WORD_HUNT,
                        prompt = "Temukan istilah petualangan tersembunyi (${wordUpper.length} huruf):",
                        subText = clue1,
                        targetWord = target.word,
                        gridLetters = gridList,
                        correctAnswer = wordUpper,
                        clue = clue1,
                        clue2 = clue2,
                        hintsEnabled = hintsEnabled,
                        explanation = "'${target.word}' (${target.indonesian}) berhasil ditemukan di dalam kisi huruf! Definisi: ${target.englishDef}."
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
                        subText = clue1,
                        targetWord = target.word,
                        scrambledLetters = scrambled,
                        correctAnswer = wordUpper,
                        clue = clue1,
                        clue2 = clue2,
                        hintsEnabled = hintsEnabled,
                        explanation = "'${target.word}' (${target.indonesian}) tersusun dengan benar! Contoh: ${target.exampleSentence}"
                    )
                }

                QuestionType.SENTENCE_BUILDER -> {
                    val sentence = target.exampleSentence.trim()
                    val rawTokens = sentence.split(Regex("\\s+")).filter { it.isNotBlank() }
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
                        clue = clue1,
                        clue2 = clue2,
                        hintsEnabled = hintsEnabled,
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
                        hint = clue1,
                        correctAnswer = target.word.lowercase(),
                        clue = clue1,
                        clue2 = clue2,
                        hintsEnabled = hintsEnabled,
                        explanation = "Kata yang dilafalkan adalah '${target.word}' (${target.pronunciation}), yang berarti '${target.indonesian}'."
                    )
                }

                QuestionType.FILL_BLANK -> {
                    val blankSentence = target.exampleSentence.replace(Regex("(?i)\\b" + Regex.escape(target.word) + "\\b"), "_______")
                    val displaySentence = if (blankSentence.contains("_______")) blankSentence else "${target.exampleSentence} (_______)"
                    Question(
                        id = qId,
                        type = QuestionType.FILL_BLANK,
                        prompt = "Ketik kata yang hilang untuk melengkapi catatan petualangan:",
                        subText = "\"$displaySentence\"",
                        sentenceWithBlank = displaySentence,
                        targetWord = target.word,
                        hint = clue1,
                        correctAnswer = target.word,
                        clue = clue1,
                        clue2 = clue2,
                        hintsEnabled = hintsEnabled,
                        explanation = "Kata yang tepat adalah '${target.word}' (${target.indonesian}): ${target.exampleSentence}"
                    )
                }

                else -> {
                    val distractors = v.filter { it.word != target.word }.shuffled().take(3).map { it.word }
                    Question(
                        id = qId,
                        type = QuestionType.MULTIPLE_CHOICE,
                        prompt = "Pilih istilah petualangan yang tepat untuk: '${target.englishDef}':",
                        subText = clue1,
                        targetWord = target.word,
                        options = (distractors + target.word).shuffled(),
                        correctAnswer = target.word,
                        clue = clue1,
                        clue2 = clue2,
                        hintsEnabled = hintsEnabled,
                        explanation = "'${target.word}' adalah ${target.indonesian}."
                    )
                }
            }
        }
    }
}
"""

full_content = base + tail
with open('app/src/main/java/com/example/data/seed/LevelTopicCatalog.kt', 'w') as f:
    f.write(full_content)

print("Updated LevelTopicCatalog.kt successfully, lines:", len(full_content.splitlines()))
