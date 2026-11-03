package com.example.data.seed

import com.example.data.local.VocabularyEntity
import com.example.data.models.*

object InitialDataSeed {

    val STAGES = listOf(
        StageInfo(
            number = 1,
            name = "Forest",
            regionTheme = "Green Forest & Woodland Path",
            description = "Starting exploration area featuring a lush green forest, moss-covered rocks, and winding paths.",
            colorHex = "#4A943E"
        ),
        StageInfo(
            number = 2,
            name = "Island",
            regionTheme = "Tropical Island & Reefs",
            description = "A tropical island surrounded by blue water, sandy beaches, coconut palm trees, and coastal rocks.",
            colorHex = "#2B82C9"
        ),
        StageInfo(
            number = 3,
            name = "Ancient Rome",
            regionTheme = "Classical Colosseum & Ruins",
            description = "An ancient Roman city with historical ruins, monumental Colosseum, classical pillars, and stone arches.",
            colorHex = "#E5B83B"
        ),
        StageInfo(
            number = 4,
            name = "Mountain",
            regionTheme = "Rocky Cliffs & Snowy Peaks",
            description = "A challenging mountain region with rocky switchback trails, snowy peaks, and high-altitude mist.",
            colorHex = "#6C7A89"
        ),
        StageInfo(
            number = 5,
            name = "Cave",
            regionTheme = "Glowing Crystal Cavern",
            description = "The final destination featuring a mysterious subterranean cave entrance with glowing blue crystals.",
            colorHex = "#9B5DE5"
        )
    )

    val EXPLORATION_REGIONS = listOf(
        ExplorationRegion(
            stageNumber = 1,
            name = "Forest",
            subtitle = "Lush Green Woodland",
            description = "Starting exploration area in a lush tropical forest, dense trees, and woodland paths.",
            pixelX = 280f,
            pixelY = 1360f,
            bannerX = 240f,
            bannerY = 1285f,
            iconEmoji = "🌲",
            colorHex = "#4A943E"
        ),
        ExplorationRegion(
            stageNumber = 2,
            name = "Island",
            subtitle = "Tropical Shore & Lagoon",
            description = "A tropical island surrounded by blue sea, sandy beaches, coconut palm trees, and coastal rocks.",
            pixelX = 570f,
            pixelY = 890f,
            bannerX = 265f,
            bannerY = 940f,
            iconEmoji = "🏝️",
            colorHex = "#2B82C9"
        ),
        ExplorationRegion(
            stageNumber = 3,
            name = "Ancient Rome",
            subtitle = "Colosseum & Classical Ruins",
            description = "An ancient Roman city with historical ruins, monumental Colosseum, classical pillars, and stone arches.",
            pixelX = 510f,
            pixelY = 640f,
            bannerX = 440f,
            bannerY = 490f,
            iconEmoji = "🏛️",
            colorHex = "#E5B83B"
        ),
        ExplorationRegion(
            stageNumber = 4,
            name = "Mountain",
            subtitle = "Rocky & Snowy Peaks",
            description = "A towering snowy mountain region above Ancient Rome with rocky switchback trails, snowy peaks, and high cliffs.",
            pixelX = 440f,
            pixelY = 280f,
            bannerX = 365f,
            bannerY = 105f,
            iconEmoji = "🏔️",
            colorHex = "#6C7A89"
        ),
        ExplorationRegion(
            stageNumber = 5,
            name = "Cave",
            subtitle = "Glowing Blue Crystal Cavern",
            description = "The final destination featuring a mysterious subterranean cave entrance with glowing blue crystals.",
            pixelX = 770f,
            pixelY = 160f,
            bannerX = 805f,
            bannerY = 115f,
            iconEmoji = "💎",
            colorHex = "#9B5DE5"
        )
    )

    // Precomputed isometric coordinate paths for the 50 levels (1-1 to 5-10)
    // Calibrated directly to the 899 x 1600 portrait fantasy adventure map along visible gold roads & bridges
    fun getLevelCoordinates(): List<Pair<Float, Float>> {
        val coords = mutableListOf<Pair<Float, Float>>()

        // Chapter 1 (1-1 to 1-10): Forest trail from starting point up across footbridges to Island bridge
        coords.add(Pair(324f, 1460f)) // 1-1 Adventurer Starting Point (Active PLAY)
        coords.add(Pair(340f, 1400f)) // 1-2 Forest clearing
        coords.add(Pair(366f, 1345f)) // 1-3 Winding woodland trail
        coords.add(Pair(390f, 1295f)) // 1-4 Wooden footbridge
        coords.add(Pair(416f, 1242f)) // 1-5 Forest cliff path
        coords.add(Pair(440f, 1205f)) // 1-6 Suspension bridge approach
        coords.add(Pair(461f, 1170f)) // 1-7 Suspension bridge start
        coords.add(Pair(469f, 1106f)) // 1-8 Suspension bridge over sea
        coords.add(Pair(500f, 1080f)) // 1-9 Island bridge landing
        coords.add(Pair(545f, 1055f)) // 1-10 Transition to Island Beach (Checkpoint Flag)

        // Chapter 2 (2-1 to 2-10): Tropical Island beach, palm trail & Roman arch causeway
        coords.add(Pair(585f, 1025f)) // 2-1 Island Beach landing
        coords.add(Pair(615f, 980f))  // 2-2 Golden sandy shore
        coords.add(Pair(575f, 940f))  // 2-3 Coconut palm grove
        coords.add(Pair(530f, 915f))  // 2-4 Island hut pathway
        coords.add(Pair(505f, 875f))  // 2-5 Coastal rock crossing
        coords.add(Pair(530f, 835f))  // 2-6 Island east shore
        coords.add(Pair(580f, 820f))  // 2-7 Causeway approach
        coords.add(Pair(635f, 815f))  // 2-8 Roman arch bridge
        coords.add(Pair(685f, 805f))  // 2-9 Aqueduct ramp
        coords.add(Pair(730f, 775f))  // 2-10 Island to Ancient Rome gate (Checkpoint Flag)

        // Chapter 3 (3-1 to 3-10): Ancient Rome Avenue, Colosseum & High Chasm
        coords.add(Pair(675f, 755f))  // 3-1 Grand Roman gate
        coords.add(Pair(624f, 750f))  // 3-2 Lower Roman avenue
        coords.add(Pair(565f, 740f))  // 3-3 Front of Colosseum
        coords.add(Pair(505f, 730f))  // 3-4 Imperial Forum plaza
        coords.add(Pair(465f, 705f))  // 3-5 Colosseum promenade
        coords.add(Pair(425f, 665f))  // 3-6 Classical pillars & statues
        coords.add(Pair(402f, 620f))  // 3-7 Ascending Roman avenue
        coords.add(Pair(404f, 565f))  // 3-8 Upper Roman ruins
        coords.add(Pair(430f, 520f))  // 3-9 Stone archway to mountain
        coords.add(Pair(480f, 485f))  // 3-10 Mountain gorge chasm bridge (Checkpoint Flag)

        // Chapter 4 (4-1 to 4-10): Mountain Switchbacks, Snowy Ridge & High Summit
        coords.add(Pair(530f, 445f))  // 4-1 Mountain gorge suspension bridge
        coords.add(Pair(500f, 405f))  // 4-2 Mountain bridge terminus
        coords.add(Pair(445f, 385f))  // 4-3 Lower alpine trail
        coords.add(Pair(385f, 375f))  // 4-4 Alpine chalet / Pine foothill
        coords.add(Pair(359f, 345f))  // 4-5 Rocky switchback ascent
        coords.add(Pair(390f, 315f))  // 4-6 High snowy mountain ridge
        coords.add(Pair(445f, 295f))  // 4-7 Snowy summit pass
        coords.add(Pair(492f, 280f))  // 4-8 Mountain descent trail
        coords.add(Pair(487f, 226f))  // 4-9 Rocky cliff trail
        coords.add(Pair(421f, 158f))  // 4-10 Mountain summit checkpoint path to Cave (Checkpoint Flag)

        // Chapter 5 (5-1 to 5-10): Canyon approach, Cave threshold & Crystal Chamber
        coords.add(Pair(465f, 140f))  // 5-1 Mountain ridge path toward cave
        coords.add(Pair(515f, 130f))  // 5-2 Canyon approach
        coords.add(Pair(570f, 125f))  // 5-3 Cave approach
        coords.add(Pair(625f, 120f))  // 5-4 Cave threshold
        coords.add(Pair(675f, 120f))  // 5-5 Cave mouth entrance
        coords.add(Pair(725f, 125f))  // 5-6 Stalactite arch
        coords.add(Pair(765f, 135f))  // 5-7 Subterranean grotto
        coords.add(Pair(795f, 155f))  // 5-8 Glowing cyan crystal vein
        coords.add(Pair(815f, 185f))  // 5-9 Deep crystal cavern
        coords.add(Pair(830f, 225f))  // 5-10 Master Glowing Crystal Cavern Vault (Victory Point)

        return coords
    }

    val COMPANIONS = listOf(
        CompanionAnimal(
            id = "fox",
            name = "Rusty the Forest Fox",
            species = "Red Fox",
            trait = "Quick Reflexes",
            description = "Guides you through tangled woods and alerts you to hidden treasure.",
            requiredStreak = 0,
            isMythical = false,
            emoji = "🦊",
            isUnlocked = true
        ),
        CompanionAnimal(
            id = "parrot",
            name = "Echo the Jungle Parrot",
            species = "Macaw",
            trait = "Vocabulary Mimic",
            description = "Repeats pronunciations and sings cheering tunes when you learn new words.",
            requiredStreak = 7,
            isMythical = false,
            emoji = "🦜"
        ),
        CompanionAnimal(
            id = "otter",
            name = "Splash the Island Otter",
            species = "River Otter",
            trait = "Curious Fisher",
            description = "Dives into clear currents and retrieves ancient forgotten coins.",
            requiredStreak = 14,
            isMythical = false,
            emoji = "🦦"
        ),
        CompanionAnimal(
            id = "fennec",
            name = "Zephyr the Desert Fennec",
            species = "Fennec Fox",
            trait = "Acuity Listener",
            description = "Large ears hear faint desert whispers and helps unscramble tricky words.",
            requiredStreak = 21,
            isMythical = false,
            emoji = "🐾"
        ),
        CompanionAnimal(
            id = "hawk",
            name = "Sol the Mountain Hawk",
            species = "Golden Hawk",
            trait = "Apex Scout",
            description = "Soars above misty mountain peaks to map out the next expedition.",
            requiredStreak = 28,
            isMythical = false,
            emoji = "🦅"
        ),
        CompanionAnimal(
            id = "griffin",
            name = "Aurelius the Celestial Griffin",
            species = "Mythical Griffin",
            trait = "Ancient Wisdom",
            description = "Protector of classical ruins and master of Greek & Latin word roots.",
            requiredStreak = 30,
            isMythical = true,
            emoji = "🦁"
        ),
        CompanionAnimal(
            id = "phoenix",
            name = "Ignis the Solar Phoenix",
            species = "Flame Phoenix",
            trait = "Endless Resilience",
            description = "Reborn from study flames, empowering uninterrupted streaks.",
            requiredStreak = 60,
            isMythical = true,
            emoji = "🔥"
        ),
        CompanionAnimal(
            id = "dragon",
            name = "Azure the Crystal Dragon",
            species = "Crystal Drake",
            trait = "Omni Lexicon",
            description = "Ancient keeper of the deepest subterranean vocabulary vault.",
            requiredStreak = 90,
            isMythical = true,
            emoji = "🐉"
        )
    )

    val DIAGNOSTIC_QUESTIONS = listOf(
        DiagnosticQuestion(
            id = "diag_1",
            prompt = "What is the English word for 'hutan'?",
            options = listOf("Forest", "Ocean", "Desert", "City"),
            correctIndex = 0,
            cefrLevel = "A1",
            explanation = "'Forest' artinya hutan. Ocean = samudera, desert = gurun."
        ),
        DiagnosticQuestion(
            id = "diag_2",
            prompt = "Which object does an explorer use to find direction?",
            options = listOf("Backpack", "Compass", "Torch", "Tent"),
            correctIndex = 1,
            cefrLevel = "A1",
            explanation = "A 'compass' (kompas) is used to determine geographic direction."
        ),
        DiagnosticQuestion(
            id = "diag_3",
            prompt = "Complete: 'The students are _______ their English homework.'",
            options = listOf("doing", "make", "swimming", "eating"),
            correctIndex = 0,
            cefrLevel = "A2",
            explanation = "Collocation: we say 'doing homework'."
        ),
        DiagnosticQuestion(
            id = "diag_4",
            prompt = "What is the synonym of 'ancient'?",
            options = listOf("Modern", "Very old", "Futuristic", "Small"),
            correctIndex = 1,
            cefrLevel = "A2",
            explanation = "'Ancient' means belonging to the very distant past (sangat kuno/tua)."
        ),
        DiagnosticQuestion(
            id = "diag_5",
            prompt = "Choose the correct sentence:",
            options = listOf(
                "She go to school yesterday.",
                "She went to school yesterday.",
                "She goes to school yesterday.",
                "She has go to school yesterday."
            ),
            correctIndex = 1,
            cefrLevel = "A2",
            explanation = "Past simple of 'go' with 'yesterday' is 'went'."
        ),
        DiagnosticQuestion(
            id = "diag_6",
            prompt = "An island is a piece of land completely _______ by water.",
            options = listOf("surrounded", "frozen", "planted", "escaped"),
            correctIndex = 0,
            cefrLevel = "B1",
            explanation = "'Surrounded' means encircled on all sides (dikelilingi)."
        ),
        DiagnosticQuestion(
            id = "diag_7",
            prompt = "Which word best describes a hard, dangerous journey?",
            options = listOf("Stroll", "Expedition", "Picnic", "Nap"),
            correctIndex = 1,
            cefrLevel = "B1",
            explanation = "An 'expedition' is a journey undertaken by a group with a definite objective."
        ),
        DiagnosticQuestion(
            id = "diag_8",
            prompt = "'The ruins were remarkably preserved despite centuries of weathering.' What does 'preserved' mean?",
            options = listOf("Destroyed", "Kept in good condition", "Forgotten", "Hidden underwater"),
            correctIndex = 1,
            cefrLevel = "B1",
            explanation = "'Preserved' means maintained in its original or existing state (terpelihara/terjaga)."
        ),
        DiagnosticQuestion(
            id = "diag_9",
            prompt = "Select the word that does NOT belong with the others:",
            options = listOf("Cavern", "Cave", "Chamber", "Summit"),
            correctIndex = 3,
            cefrLevel = "B2",
            explanation = "'Summit' is the highest peak of a mountain, whereas cavern, cave, and chamber refer to underground spaces."
        ),
        DiagnosticQuestion(
            id = "diag_10",
            prompt = "'Her perseverance was commendable; she never wavered in her quest.' What is the opposite of 'wavered'?",
            options = listOf("Stayed firm", "Hesitated", "Trembled", "Gave up"),
            correctIndex = 0,
            cefrLevel = "B2",
            explanation = "'Waver' means to shake or falter; its antonym is to stay firm or remain steadfast."
        )
    )

    private val CORE_VOCABULARY = listOf(
        // A1 Vocabulary
        VocabularyEntity("v1", "Compass", "Kompas", "An instrument containing a magnetized pointer showing direction", "Noun", "/ˈkʌm.pəs/", "The explorer checked his compass to find north.", "Penjelajah itu memeriksa kompasnya untuk mencari arah utara.", "A1", "Exploration"),
        VocabularyEntity("v2", "Forest", "Hutan", "A large area covered chiefly with trees and undergrowth", "Noun", "/ˈfɒr.ɪst/", "We walked quietly through the dense forest.", "Kami berjalan dengan tenang melewati hutan lebat.", "A1", "Nature"),
        VocabularyEntity("v3", "Path", "Jalan setapak", "A way or track laid down for walking or made by continual treading", "Noun", "/pɑːθ/", "Follow the yellow path to the river.", "Ikuti jalan setapak kuning menuju sungai.", "A1", "Travel"),
        VocabularyEntity("v4", "Tree", "Pohon", "A woody perennial plant, typically having a single stem or trunk", "Noun", "/triː/", "Birds sang on top of the tall tree.", "Burung-burung bernyanyi di atas pohon yang tinggi.", "A1", "Nature"),
        VocabularyEntity("v5", "Map", "Peta", "A diagrammatic representation of an area of land or sea", "Noun", "/mæp/", "He unfolded the parchment map on the table.", "Dia membuka lipatan peta perkamen di atas meja.", "A1", "Exploration"),
        VocabularyEntity("v6", "River", "Sungai", "A large natural stream of water flowing in a channel to the sea", "Noun", "/ˈrɪv.ər/", "The blue river flows under the wooden bridge.", "Sungai biru itu mengalir di bawah jembatan kayu.", "A1", "Nature"),
        VocabularyEntity("v7", "Camp", "Perkemahan", "A place with temporary accommodation of tents", "Noun", "/kæmp/", "We set up our camp before sunset.", "Kami mendirikan perkemahan kami sebelum matahari terbenam.", "A1", "Travel"),
        VocabularyEntity("v8", "Torch", "Obor / Senter", "A portable means of illumination", "Noun", "/tɔːtʃ/", "Light the torch inside the dark cave.", "Nyalakan obor di dalam gua yang gelap.", "A1", "Equipment"),
        VocabularyEntity("v9", "Backpack", "Ransel", "A bag with shoulder straps, carried on the back", "Noun", "/ˈbæk.pæk/", "Put your water bottle inside your backpack.", "Masukkan botol airmu ke dalam ransel.", "A1", "Equipment"),
        VocabularyEntity("v10", "Bridge", "Jembatan", "A structure carrying a pathway over an obstacle such as water", "Noun", "/brɪdʒ/", "Cross the wooden bridge carefully.", "Seberangi jembatan kayu itu dengan hati-hati.", "A1", "Landmarks"),

        // A2 Vocabulary
        VocabularyEntity("v11", "Island", "Pulau", "A piece of land surrounded by water", "Noun", "/ˈaɪ.lənd/", "The lost island appeared on the horizon.", "Pulau terpencil itu muncul di cakrawala.", "A2", "Geography"),
        VocabularyEntity("v12", "Ruins", "Reruntuhan", "The physical destruction or remains of a building or town", "Noun", "/ˈruː.ɪnz/", "Roman ruins stood silently among the vines.", "Reruntuhan Romawi berdiri membisu di antara tanaman merambat.", "A2", "History"),
        VocabularyEntity("v13", "Mountain", "Gunung", "A large natural elevation of the earth's surface", "Noun", "/ˈmaʊn.tɪn/", "Snow covered the peak of the mountain.", "Salju menutupi puncak gunung itu.", "A2", "Nature"),
        VocabularyEntity("v14", "Valley", "Lembah", "A low area of land between hills or mountains", "Noun", "/ˈvæl.i/", "Wildflowers bloomed across the green valley.", "Bunga-bunga liar mekar di seluruh lembah hijau.", "A2", "Geography"),
        VocabularyEntity("v15", "Discover", "Menemukan", "Find unexpectedly or in the course of a search", "Verb", "/dɪˈskʌv.ər/", "They hope to discover an ancient secret.", "Mereka berharap menemukan rahasia kuno.", "A2", "Action"),
        VocabularyEntity("v16", "Adventure", "Petualangan", "An unusual and exciting, typically hazardous experience", "Noun", "/ədˈven.tʃər/", "Learning new words is a thrilling adventure.", "Mempelajari kosakata baru adalah petualangan yang mendebarkan.", "A2", "Exploration"),
        VocabularyEntity("v17", "Ancient", "Kuno", "Belonging to the very distant past and no longer in existence", "Adjective", "/ˈeɪn.ʃənt/", "We examined the ancient stone carvings.", "Kami meneliti ukiran batu kuno tersebut.", "A2", "History"),
        VocabularyEntity("v18", "Shelter", "Tempat berlindung", "A place giving temporary protection from bad weather or danger", "Noun", "/ˈʃel.tər/", "They sought shelter under the cliff during the storm.", "Mereka mencari tempat berlindung di bawah tebing saat badai.", "A2", "Survival"),
        VocabularyEntity("v19", "Journey", "Perjalanan", "An act of traveling from one place to another", "Noun", "/ˈdʒɜː.ni/", "Our journey through the wilderness began at dawn.", "Perjalanan kita melintasi belantara dimulai saat fajar.", "A2", "Travel"),
        VocabularyEntity("v20", "Lantern", "Lentera", "A lamp with a transparent case protecting the flame or electric bulb", "Noun", "/ˈlæn.tən/", "The brass lantern cast a warm glow.", "Lentera kuningan itu memancarkan kilau hangat.", "A2", "Equipment"),

        // B1 Vocabulary
        VocabularyEntity("v21", "Expedition", "Ekspedisi", "A journey undertaken by a group with a particular purpose", "Noun", "/ˌek.spəˈdɪʃ.ən/", "The archaeological expedition unearthed gold artifacts.", "Ekspedisi arkeologi tersebut menemukan artefak emas.", "B1", "Exploration"),
        VocabularyEntity("v22", "Pillar", "Pilar / Tiang batu", "A tall vertical structure used as a support or monument", "Noun", "/ˈpɪl.ər/", "Carved Roman pillars sustained the temple roof.", "Pilar-pilar Romawi berukir menopang atap kuil.", "B1", "Architecture"),
        VocabularyEntity("v23", "Cavern", "Gua besar", "A large cave or chamber in a cave", "Noun", "/ˈkæv.ən/", "The underground cavern echoed with dripping water.", "Gua bawah tanah itu bergema oleh tetesan air.", "B1", "Nature"),
        VocabularyEntity("v24", "Stalactite", "Stalaktit", "A tapering structure hanging like an icicle from the roof of a cave", "Noun", "/ˈstæl.ək.taɪt/", "Glistening stalactites hung from the cavern ceiling.", "Stalaktit berkilauan menggantung dari langit-langit gua.", "B1", "Geology"),
        VocabularyEntity("v25", "Navigate", "Menavigasi / Mengarahkan", "Direct or manage the course of a ship, aircraft, or journey", "Verb", "/ˈnæv.ɪ.ɡeɪt/", "She used stellar positions to navigate the desert.", "Dia menggunakan posisi bintang untuk menavigasi gurun.", "B1", "Action"),
        VocabularyEntity("v26", "Surround", "Mengelilingi", "Be all around someone or something", "Verb", "/səˈraʊnd/", "Misty clouds surround the icy peak.", "Awan berkabut mengelilingi puncak es.", "B1", "Geography"),
        VocabularyEntity("v27", "Preserve", "Memelihara / Melestarikan", "Maintain something in its original or existing state", "Verb", "/prɪˈzɜːv/", "The dry cave air helped preserve the scrolls.", "Udara gua yang kering membantu melestarikan gulungan naskah itu.", "B1", "History"),
        VocabularyEntity("v28", "Trek", "Menempuh perjalanan jauh", "Go on a long arduous journey, typically on foot", "Verb", "/trek/", "The adventurers trekked up the rocky switchback.", "Para petualang menempuh perjalanan mendaki jalur berbatu.", "B1", "Travel"),
        VocabularyEntity("v29", "Courageous", "Pemberani", "Not deterred by danger or pain; brave", "Adjective", "/kəˈreɪ.dʒəs/", "A courageous explorer entered the dark fissure.", "Seorang penjelajah pemberani memasuki celah gelap tersebut.", "B1", "Traits"),
        VocabularyEntity("v30", "Summit", "Puncak tertinggi", "The highest point of a hill or mountain", "Noun", "/ˈsʌm.ɪt/", "Reaching the summit rewarded them with a breathtaking view.", "Mencapai puncak menghadiahkan mereka pemandangan yang menakjubkan.", "B1", "Geography"),

        // B2 Vocabulary
        VocabularyEntity("v31", "Archaeology", "Arkeologi", "The study of human history through excavation of sites", "Noun", "/ˌɑː.kiˈɒl.ə.dʒi/", "Her passion for archaeology led her to Roman ruins.", "Kecintaannya pada arkeologi membawanya ke reruntuhan Romawi.", "B2", "Science"),
        VocabularyEntity("v32", "Luminescence", "Pendar cahaya", "The emission of light by a substance not resulting from heat", "Noun", "/ˌluː.mɪˈnes.əns/", "Crystals exhibited an eerie blue luminescence.", "Kristal-kristal itu memperlihatkan pendar cahaya biru yang memukau.", "B2", "Science"),
        VocabularyEntity("v33", "Monumental", "Monumental / Bersejarah", "Great in importance, extent, or size", "Adjective", "/ˌmɒn.jəˈmen.təl/", "They stood beneath a monumental archway.", "Mereka berdiri di bawah gapura lengkung yang monumental.", "B2", "Architecture"),
        VocabularyEntity("v34", "Precipice", "Tebing terjal yang curam", "A very steep rock face or cliff, typically a tall one", "Noun", "/ˈpres.ɪ.pɪs/", "The narrow ledge bordered a dizzying precipice.", "Tepian sempit itu berbatasan dengan tebing curam yang memusingkan.", "B2", "Geography"),
        VocabularyEntity("v35", "Subterranean", "Bawah tanah", "Existing, occurring, or done under the earth's surface", "Adjective", "/ˌsʌb.təˈreɪ.ni.ən/", "A subterranean river rushed through the depths.", "Sungai bawah tanah mengalir deras melewati kedalaman.", "B2", "Geology"),
        VocabularyEntity("v36", "Steadfast", "Kukuh / Teguh", "Resolutely or dutifully firm and unwavering", "Adjective", "/ˈsted.fɑːst/", "Remain steadfast even when solving difficult riddles.", "Tetaplah teguh bahkan saat memecahkan teka-teki sulit.", "B2", "Traits"),
        VocabularyEntity("v37", "Inconspicuous", "Tidak mencolok", "Not clearly visible or attracting attention", "Adjective", "/ˌɪn.kənˈspɪk.ju.əs/", "An inconspicuous doorway was hidden behind the ivy.", "Pintu masuk yang tidak mencolok tersembunyi di balik tanaman ivy.", "B2", "Traits"),
        VocabularyEntity("v38", "Ingenuity", "Kecerdikan / Daya cipta", "The quality of being clever, original, and inventive", "Noun", "/ˌɪn.dʒəˈnjuː.ə.ti/", "Surviving the expedition required wits and ingenuity.", "Bertahan dalam ekspedisi menuntut kecerdasan dan daya cipta.", "B2", "Traits"),
        VocabularyEntity("v39", "Decipher", "Membaca sandi / Menguraikan", "Convert into normal language or succeed in understanding", "Verb", "/dɪˈsaɪ.fər/", "The scholar worked all night to decipher the runes.", "Sang cendekiawan bekerja semalaman untuk menguraikan tulisan kuno.", "B2", "Action"),
        VocabularyEntity("v40", "Panorama", "Panorama / Pemandangan luas", "An unbroken view of the whole region surrounding an observer", "Noun", "/ˌpæn.ərˈɑː.mə/", "From the summit, they enjoyed an alpine panorama.", "Dari puncak, mereka menikmati panorama pegunungan alpen yang luas.", "B2", "Geography")
    )

    val INITIAL_VOCABULARY: List<VocabularyEntity> by lazy {
        (CORE_VOCABULARY + LevelTopicCatalog.TOPICS.flatMap { it.vocabulary })
            .distinctBy { it.word.lowercase() }
    }

    val ADVENTURE_STORIES = listOf(
        AdventureStory(
            id = "story_1",
            title = "The Whispering Forest",
            region = "Green Forest",
            cefr = "A1–A2",
            targetWords = listOf("Forest", "Compass", "Path", "Tree", "Torch"),
            synopsis = "A young adventurer enters the enchanted whispering woods guided by a golden compass.",
            paragraphs = listOf(
                "Arka stood at the edge of the great green forest. Tall trees towered overhead, blocking the morning sunlight with their thick emerald leaves.",
                "In his hand, he held an old brass compass. The needle spun wildly before pointing toward a faint stone path covered in moss.",
                "'Follow the path,' Arka whispered to himself. He gripped his torch and stepped forward into the woodland shadows, determined to find what lay ahead."
            ),
            comprehensionQuestions = listOf(
                Question(
                    id = "sq_1_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    prompt = "Where did Arka stand at the beginning of the story?",
                    options = listOf("At the edge of the forest", "Inside a castle", "On an island beach", "At a subway station"),
                    correctAnswer = "At the edge of the forest",
                    explanation = "Paragraph 1 mentions: 'Arka stood at the edge of the great green forest.'",
                    targetWord = "Forest"
                ),
                Question(
                    id = "sq_1_2",
                    type = QuestionType.FILL_BLANK,
                    prompt = "The needle pointed toward a faint stone _______ covered in moss.",
                    options = listOf("path", "bridge", "river", "mountain"),
                    correctAnswer = "path",
                    explanation = "The needle pointed to the stone path.",
                    targetWord = "Path"
                )
            )
        ),
        AdventureStory(
            id = "story_2",
            title = "Secrets of the Sunken Island",
            region = "Lost Island",
            cefr = "A2",
            targetWords = listOf("Island", "Bridge", "River", "Shell", "Discover"),
            synopsis = "Crossing the rope bridge to a secluded island where coconut palms hide an ancient pier.",
            paragraphs = listOf(
                "Beyond the swift blue river lay an isolated island. A wooden suspension bridge swayed gently in the coastal breeze.",
                "Arka carefully crossed the planks. Beneath his boots, crystal waters revealed colorful reef fish swimming between coral branches.",
                "On the sandy shore, coconut trees rustled. He discovered strange shell carvings marking the way to an old explorer's pier."
            ),
            comprehensionQuestions = listOf(
                Question(
                    id = "sq_2_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    prompt = "How did Arka cross to the isolated island?",
                    options = listOf("Via a wooden suspension bridge", "By swimming", "In a helicopter", "On a horse"),
                    correctAnswer = "Via a wooden suspension bridge",
                    explanation = "The text states 'A wooden suspension bridge swayed gently in the coastal breeze.'",
                    targetWord = "Bridge"
                )
            )
        ),
        AdventureStory(
            id = "story_3",
            title = "Echoes of the Roman Pillars",
            region = "Ancient Ruins",
            cefr = "A2–B1",
            targetWords = listOf("Ruins", "Pillar", "Ancient", "Monumental", "Archaeology"),
            synopsis = "Standing before towering Roman arches weathered by centuries of rain and time.",
            paragraphs = listOf(
                "The jungle suddenly gave way to a grand valley of white and ochre stone. Ancient Roman ruins stood proud against the blue sky.",
                "Monumental pillars carved with leaves of laurel supported crumbled stone arches. Ivy and wild roses wound around the pedestals.",
                "Arka ran his fingers across the engraved Latin letters. Here, archaeology was not just studying the past—it felt alive."
            ),
            comprehensionQuestions = listOf(
                Question(
                    id = "sq_3_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    prompt = "What supported the crumbled stone arches?",
                    options = listOf("Monumental pillars", "Wooden sticks", "Metal chains", "Giant mushrooms"),
                    correctAnswer = "Monumental pillars",
                    explanation = "'Monumental pillars carved with leaves of laurel supported crumbled stone arches.'",
                    targetWord = "Pillar"
                )
            )
        ),
        AdventureStory(
            id = "story_4",
            title = "Climbing the Frozen Precipice",
            region = "Mountain Passage",
            cefr = "B1",
            targetWords = listOf("Mountain", "Summit", "Precipice", "Shelter", "Trek"),
            synopsis = "Enduring icy winds on rocky switchbacks to conquer the mountain summit.",
            paragraphs = listOf(
                "The air grew thin and cold as the trail climbed into the high peaks. Jagged cliffs bordered dizzying precipices.",
                "Arka pulled his explorer coat tighter. Snow swirled across the rocky ledges, but he continued his trek upward.",
                "As the sun broke through alpine clouds, he reached the summit. Before him lay a panorama of mountains stretching to infinity."
            ),
            comprehensionQuestions = listOf(
                Question(
                    id = "sq_4_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    prompt = "What did Arka reach when the sun broke through alpine clouds?",
                    options = listOf("The summit", "The ocean beach", "A subway train", "His classroom"),
                    correctAnswer = "The summit",
                    explanation = "'As the sun broke through alpine clouds, he reached the summit.'",
                    targetWord = "Summit"
                )
            )
        ),
        AdventureStory(
            id = "story_5",
            title = "The Luminescent Vault",
            region = "Crystal Cave",
            cefr = "B1–B2",
            targetWords = listOf("Cavern", "Luminescence", "Stalactite", "Subterranean", "Decipher"),
            synopsis = "Uncovering the mythical crystal chamber where ancient lexicons glow in the dark.",
            paragraphs = listOf(
                "Descending beneath the mountain roots, Arka entered a vast subterranean cavern. No torches were needed here.",
                "Clusters of cyan and violet crystals bathed the stalactites in a gentle luminescence.",
                "In the center of the hall rested a stone podium with glowing runes. Arka paused, prepared to decipher the final riddle of Word Explorer."
            ),
            comprehensionQuestions = listOf(
                Question(
                    id = "sq_5_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    prompt = "Why were torches not needed inside the subterranean cavern?",
                    options = listOf(
                        "Crystals bathed the cavern in gentle luminescence",
                        "The sun was shining underground",
                        "There was electric neon lighting",
                        "Arka closed his eyes"
                    ),
                    correctAnswer = "Crystals bathed the cavern in gentle luminescence",
                    explanation = "Paragraph 2 explains: 'Clusters of cyan and violet crystals bathed the stalactites in a gentle luminescence.'",
                    targetWord = "Luminescence"
                )
            )
        )
    )

    fun getEmojiForWord(word: String): String {
        return when (word.trim().lowercase()) {
            "compass" -> "🧭"
            "forest" -> "🌲"
            "path" -> "🛤️"
            "tree" -> "🌳"
            "map" -> "🗺️"
            "river" -> "🌊"
            "camp" -> "⛺"
            "torch" -> "🔦"
            "backpack" -> "🎒"
            "bridge" -> "🌉"
            "island" -> "🏝️"
            "ruins" -> "🏛️"
            "mountain" -> "🏔️"
            "valley" -> "🏞️"
            "discover" -> "🔍"
            "adventure" -> "🧭"
            "ancient" -> "⏳"
            "shelter" -> "🛖"
            "journey" -> "🚶"
            "lantern" -> "🏮"
            "expedition" -> "🧭"
            "pillar" -> "🏛️"
            "cavern" -> "🕳️"
            "stalactite" -> "💎"
            "navigate" -> "🧭"
            "surround" -> "🔄"
            "preserve" -> "🏺"
            "trek" -> "🥾"
            "courageous" -> "🦁"
            "summit" -> "🚩"
            "archaeology" -> "⛏️"
            "luminescence" -> "✨"
            "monumental" -> "🗿"
            "precipice" -> "🧗"
            "subterranean" -> "🦇"
            "steadfast" -> "🛡️"
            "inconspicuous" -> "🕶️"
            "ingenuity" -> "💡"
            "decipher" -> "📜"
            "panorama" -> "🌄"
            else -> "✨"
        }
    }

    // Generator for 10 deterministic high-quality interactive questions for any given level (1-1 to 5-10)
    // using distinct topics and diverse formats (Drag & Match, Fill in the Blanks, Unscramble, Image-based, Sentence, Audio/Pronunciation)
    fun generateQuestionsForLevel(path: LearningPath, stage: Int, level: Int): List<Question> {
        return LevelTopicCatalog.generateTenQuestionsForLevel(path, stage, level)
    }

    // Generate remedial targeted question for a failed vocabulary word
    fun generateRemedialQuestion(originalQuestion: Question, path: LearningPath): Question {
        val word = originalQuestion.targetWord
        val matchingVocab = INITIAL_VOCABULARY.find { it.word.equals(word, ignoreCase = true) }
            ?: VocabularyEntity("remedial_fallback", word, word, "An adventure concept", "Noun", "", "", "", "A2", "Adventure")

        val wrongOptions = listOf("Sky", "Stone", "Water", "House", "Book")
            .filter { !it.equals(word, ignoreCase = true) }
            .take(3)
        val options = (wrongOptions + word).shuffled()

        val sentenceWithBlank = matchingVocab.exampleSentence.replace(matchingVocab.word, "_______", ignoreCase = true)

        return Question(
            id = "rem_${originalQuestion.id}",
            type = QuestionType.SENTENCE_COMPLETION,
            prompt = "Misi Remedial: Kuasai kata '${matchingVocab.word}'!",
            subText = "\"$sentenceWithBlank\"\nArti: ${matchingVocab.indonesian} | Definisi: ${matchingVocab.englishDef}",
            options = options,
            correctAnswer = matchingVocab.word,
            explanation = "Hebat! Kamu telah menaklukkan '${matchingVocab.word}' (${matchingVocab.indonesian})!",
            targetWord = matchingVocab.word,
            sentenceWithBlank = sentenceWithBlank
        )
    }
}
