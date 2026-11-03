package com.example.data.seed

import com.example.data.local.VocabularyEntity
import com.example.data.models.LearningPath
import com.example.data.models.Question
import com.example.data.models.QuestionType

data class LevelTopicDefinition(
    val stage: Int,
    val level: Int,
    val levelId: String,
    val topicTitle: String,
    val learningObjective: String,
    val vocabulary: List<VocabularyEntity>
)

object LevelTopicCatalog {

    val TOPICS: List<LevelTopicDefinition> by lazy {
        buildList {
            // ==================== CHAPTER 1: FOREST (1-1 to 1-10) ====================
            add(
                LevelTopicDefinition(
                    stage = 1, level = 1, levelId = "1-1",
                    topicTitle = "Greetings and Introductions",
                    learningObjective = "Master everyday greetings, polite self-introductions, and friendly expressions.",
                    vocabulary = listOf(
                        vocab("f_hello", "Hello", "Halo", "Used as a greeting or to begin a conversation", "Interjection", "/həˈləʊ/", "Hello! Welcome to the adventure island.", "Halo! Selamat datang di pulau petualangan.", "A1", "Greetings"),
                        vocab("f_morning", "Morning", "Pagi", "The early part of the day from sunrise to noon", "Noun", "/ˈmɔːnɪŋ/", "Good morning, fellow explorer!", "Selamat pagi, kawan penjelajah!", "A1", "Greetings"),
                        vocab("f_name", "Name", "Nama", "A word or set of words by which a person is known", "Noun", "/neɪm/", "What is your explorer name?", "Siapa nama penjelajahmu?", "A1", "Greetings"),
                        vocab("f_welcome", "Welcome", "Selamat datang", "Used to greet someone in a warm, friendly way", "Verb", "/ˈwelkəm/", "Welcome to the tropical forest!", "Selamat datang di hutan tropis!", "A1", "Greetings"),
                        vocab("f_friend", "Friend", "Teman", "A person with whom one has a bond of mutual affection", "Noun", "/frend/", "A loyal friend joins our forest expedition.", "Seorang teman setia bergabung dalam ekspedisi hutan kita.", "A1", "Greetings"),
                        vocab("f_meet", "Meet", "Bertemu", "To encounter someone or make their acquaintance", "Verb", "/miːt/", "I am very pleased to meet you here.", "Saya sangat senang bertemu Anda di sini.", "A1", "Greetings"),
                        vocab("f_glad", "Glad", "Senang", "Pleased and delightful", "Adjective", "/ɡlæd/", "I am glad to discover the forest trail.", "Saya senang menemukan jalan setapak di hutan.", "A1", "Greetings"),
                        vocab("f_smile", "Smile", "Tersenyum", "Form one's features into an amused or pleased expression", "Verb", "/smaɪl/", "The young explorer greeted everyone with a warm smile.", "Penjelajah muda menyapa semua orang dengan senyum hangat.", "A1", "Greetings"),
                        vocab("f_introduce", "Introduce", "Memperkenalkan", "To present someone by name to others", "Verb", "/ˌɪntrəˈdjuːs/", "Let me introduce our guide.", "Izinkan saya memperkenalkan pemandu kita.", "A2", "Greetings"),
                        vocab("f_speak", "Speak", "Berbicara", "Say something in order to convey information", "Verb", "/spiːk/", "We speak English together every day.", "Kita berbicara bahasa Inggris bersama setiap hari.", "A1", "Greetings")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 2, levelId = "1-2",
                    topicTitle = "Family Members and Roles",
                    learningObjective = "Recognize relationships, family titles, and household bonds.",
                    vocabulary = listOf(
                        vocab("f_father", "Father", "Ayah", "A man in relation to his natural child", "Noun", "/ˈfɑːðə/", "My father taught me how to read maps.", "Ayahku mengajariku cara membaca peta.", "A1", "Family"),
                        vocab("f_mother", "Mother", "Ibu", "A woman in relation to her child", "Noun", "/ˈmʌðə/", "Her mother packed the exploration backpack.", "Ibunya menyiapkan tas ransel penjelajahan.", "A1", "Family"),
                        vocab("f_brother", "Brother", "Saudara laki-laki", "A male sibling", "Noun", "/ˈbrʌðə/", "His brother spotted a deer in the woods.", "Saudara laki-lakinya melihat seekor rusa di hutan.", "A1", "Family"),
                        vocab("f_sister", "Sister", "Saudara perempuan", "A female sibling", "Noun", "/ˈsɪstə/", "My sister draws maps of the island.", "Saudara perempuanku menggambar peta pulau.", "A1", "Family"),
                        vocab("f_parents", "Parents", "Orang tua", "A father or mother", "Noun", "/ˈpeərənts/", "Our parents encourage our curiosity.", "Orang tua kita menyemangati rasa ingin tahu kita.", "A1", "Family"),
                        vocab("f_grandfather", "Grandfather", "Kakek", "The father of one's father or mother", "Noun", "/ˈɡrændˌfɑːðə/", "Grandfather told stories about ancient Rome.", "Kakek menceritakan kisah tentang Romawi kuno.", "A1", "Family"),
                        vocab("f_grandmother", "Grandmother", "Nenek", "The mother of one's father or mother", "Noun", "/ˈɡrændˌmʌðə/", "Grandmother gave him a brass compass.", "Nenek memberinya sebuah kompas kuningan.", "A1", "Family"),
                        vocab("f_uncle", "Uncle", "Paman", "The brother of one's father or mother", "Noun", "/ˈʌŋkl/", "Uncle Bob is a skilled sailor.", "Paman Bob adalah seorang pelaut yang terampil.", "A1", "Family"),
                        vocab("f_aunt", "Aunt", "Bibi", "The sister of one's father or mother", "Noun", "/ɑːnt/", "Aunt Maya gave us fresh tropical fruit.", "Bibi Maya memberi kami buah tropis segar.", "A1", "Family"),
                        vocab("f_family", "Family", "Keluarga", "A group consisting of parents and children", "Noun", "/ˈfæməli/", "A supportive family helps young adventurers grow.", "Keluarga yang suportif membantu petualang muda berkembang.", "A1", "Family")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 3, levelId = "1-3",
                    topicTitle = "School Objects and Classroom Tools",
                    learningObjective = "Identify essential learning tools, writing instruments, and school equipment.",
                    vocabulary = listOf(
                        vocab("f_book", "Book", "Buku", "A written or printed work consisting of pages", "Noun", "/bʊk/", "Open your English explorer book to page ten.", "Buka buku penjelajah bahasa Inggrismu di halaman sepuluh.", "A1", "School"),
                        vocab("f_pencil", "Pencil", "Pensil", "An instrument for writing or drawing", "Noun", "/ˈpensl/", "Use a sharp pencil to sketch the pathway.", "Gunakan pensil runcing untuk membuat sketsa jalur.", "A1", "School"),
                        vocab("f_eraser", "Eraser", "Penghapus", "A piece of rubber used to remove pencil marks", "Noun", "/ɪˈreɪzə/", "He corrected the note using an eraser.", "Dia memperbaiki catatannya menggunakan penghapus.", "A1", "School"),
                        vocab("f_ruler", "Ruler", "Penggaris", "A straight strip used to measure distance", "Noun", "/ˈruːlə/", "Measure the map distance with a wooden ruler.", "Ukur jarak di peta dengan penggaris kayu.", "A1", "School"),
                        vocab("f_backpack", "Backpack", "Tas ransel", "A bag with shoulder straps carried on the back", "Noun", "/ˈbækpæk/", "Arka carries his notebook in a sturdy backpack.", "Arka membawa buku catatannya dalam tas ransel kokoh.", "A1", "School"),
                        vocab("f_notebook", "Notebook", "Buku catatan", "A book of plain paper for notes", "Noun", "/ˈnəʊtbʊk/", "Write new vocabulary in your study notebook.", "Tulis kosakata baru di buku catatan belajarmu.", "A1", "School"),
                        vocab("f_desk", "Desk", "Meja belajar", "A piece of furniture with a flat top for working", "Noun", "/desk/", "Keep your study desk neat and organized.", "Jaga meja belajarmu tetap rapi dan teratur.", "A1", "School"),
                        vocab("f_chair", "Chair", "Kursi", "A separate seat for one person", "Noun", "/tʃeə/", "Sit on the wooden chair during class.", "Duduklah di kursi kayu selama pelajaran.", "A1", "School"),
                        vocab("f_blackboard", "Blackboard", "Papan tulis", "A dark smooth surface used in schools for writing with chalk", "Noun", "/ˈblækbɔːd/", "The teacher wrote the new words on the blackboard.", "Guru menulis kata-kata baru di papan tulis.", "A1", "School"),
                        vocab("f_sharpener", "Sharpener", "Rautan pensil", "A device for sharpening pencils", "Noun", "/ˈʃɑːpnə/", "Sharpen your dull pencil before class.", "Raut pensilmu yang tumpul sebelum pelajaran.", "A1", "School")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 4, levelId = "1-4",
                    topicTitle = "Classroom Activities and Verbs",
                    learningObjective = "Describe verbs of learning, listening, practicing, and active studying.",
                    vocabulary = listOf(
                        vocab("f_read", "Read", "Membaca", "Look at and comprehend the meaning of written words", "Verb", "/riːd/", "Students read the adventure story carefully.", "Siswa membaca cerita petualangan dengan seksama.", "A1", "Classroom"),
                        vocab("f_write", "Write", "Menulis", "Mark letters or words on a surface with a pen", "Verb", "/raɪt/", "Write the English definition in your book.", "Tulis definisi bahasa Inggris di bukumu.", "A1", "Classroom"),
                        vocab("f_listen", "Listen", "Mendengarkan", "Give one's attention to a sound", "Verb", "/ˈlɪsn/", "Listen attentively to the audio pronunciation.", "Dengarkan pelafalan audio dengan penuh perhatian.", "A1", "Classroom"),
                        vocab("f_learn", "Learn", "Belajar", "Acquire knowledge of or skill in something", "Verb", "/lɜːn/", "We learn forty new words each week.", "Kita belajar empat puluh kata baru setiap minggu.", "A1", "Classroom"),
                        vocab("f_practice", "Practice", "Berlatih", "Perform an activity repeatedly to improve skill", "Verb", "/ˈpræktɪs/", "Practice speaking English with your classmate.", "Berlatihlah berbicara bahasa Inggris dengan teman sekelasmu.", "A1", "Classroom"),
                        vocab("f_ask", "Ask", "Bertanya", "Say something in order to obtain an answer", "Verb", "/ɑːsk/", "Do not hesitate to ask when you are confused.", "Jangan ragu untuk bertanya saat kamu bingung.", "A1", "Classroom"),
                        vocab("f_answer", "Answer", "Menjawab", "Say or write something in response to a question", "Verb", "/ˈɑːnsə/", "He gave the correct answer to question ten.", "Dia memberikan jawaban yang benar untuk pertanyaan sepuluh.", "A1", "Classroom"),
                        vocab("f_count", "Count", "Menghitung", "Determine the total number of items", "Verb", "/kaʊnt/", "Count how many stars you have earned.", "Hitung berapa banyak bintang yang telah kamu peroleh.", "A1", "Classroom"),
                        vocab("f_draw", "Draw", "Menggambar", "Produce a picture by making lines on paper", "Verb", "/drɔː/", "Draw a map of the island on paper.", "Gambarlah peta pulau di atas kertas.", "A1", "Classroom"),
                        vocab("f_study", "Study", "Mempelajari", "Devote time and attention to acquiring knowledge", "Verb", "/ˈstʌdi/", "Diligent students study vocabulary every day.", "Siswa yang rajin mempelajari kosakata setiap hari.", "A1", "Classroom")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 5, levelId = "1-5",
                    topicTitle = "Colors and Geometric Shapes",
                    learningObjective = "Identify primary colors, secondary hues, and geometric figures.",
                    vocabulary = listOf(
                        vocab("f_red", "Red", "Merah", "Of a color at the end of the spectrum next to orange", "Adjective", "/red/", "The checkpoint flag is bright red.", "Bendera titik pemeriksaan berwarna merah terang.", "A1", "Colors"),
                        vocab("f_blue", "Blue", "Biru", "Of a color intermediate between green and violet", "Adjective", "/bluː/", "A deep blue ocean surrounds the island.", "Samudra biru tua mengelilingi pulau itu.", "A1", "Colors"),
                        vocab("f_green", "Green", "Hijau", "Of the color between blue and yellow, like grass", "Adjective", "/ɡriːn/", "The forest canopy is rich and green.", "Kanopi hutan sangat rimbun dan hijau.", "A1", "Colors"),
                        vocab("f_yellow", "Yellow", "Kuning", "Of the color between green and orange, like ripe lemons", "Adjective", "/ˈjeləʊ/", "The winding pathway is paved in bright yellow.", "Jalan setapak berkelok dilapisi warna kuning cerah.", "A1", "Colors"),
                        vocab("f_circle", "Circle", "Lingkaran", "A round plane figure whose boundary consists of points equidistant from the center", "Noun", "/ˈsɜːkl/", "The level node is shaped like a circle.", "Titik level berbentuk lingkaran.", "A1", "Shapes"),
                        vocab("f_square", "Square", "Persegi", "A plane figure with four equal straight sides", "Noun", "/skweə/", "Draw a small square around the landmark.", "Gambarlah persegi kecil di sekitar tengara.", "A1", "Shapes"),
                        vocab("f_triangle", "Triangle", "Segitiga", "A plane figure with three straight sides and three angles", "Noun", "/ˈtraɪæŋɡl/", "The mountain peak forms a sharp triangle.", "Puncak gunung membentuk segitiga runcing.", "A1", "Shapes"),
                        vocab("f_purple", "Purple", "Ungu", "A color intermediate between red and blue", "Adjective", "/ˈpɜːpl/", "Glowing purple crystals sparkle inside the cave.", "Kristal ungu menyala berkilau di dalam gua.", "A1", "Colors"),
                        vocab("f_orange", "Orange", "Oranye", "A bright reddish-yellow color", "Adjective", "/ˈɒrɪndʒ/", "The explorer wore an orange vest.", "Penjelajah itu mengenakan rompi oranye.", "A1", "Colors"),
                        vocab("f_oval", "Oval", "Bujur telur", "Having a rounded and slightly elongated outline", "Noun", "/ˈəʊvl/", "The ancient Roman arena has an oval layout.", "Arena Romawi kuno memiliki tata letak berbentuk bujur telur.", "A2", "Shapes")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 6, levelId = "1-6",
                    topicTitle = "Numbers, Time and Calendar",
                    learningObjective = "Express intervals of time, calendar periods, and daily moments.",
                    vocabulary = listOf(
                        vocab("f_morning_t", "Sunrise", "Matahari terbit", "The time in the morning when the sun appears", "Noun", "/ˈsʌnraɪz/", "We set off on our journey at sunrise.", "Kami berangkat dalam perjalanan kami saat matahari terbit.", "A2", "Time"),
                        vocab("f_afternoon", "Afternoon", "Sore", "The time from noon or lunchtime to evening", "Noun", "/ˌɑːftəˈnuːn/", "They crossed the river in the warm afternoon.", "Mereka menyeberangi sungai di sore yang hangat.", "A1", "Time"),
                        vocab("f_evening", "Evening", "Malam", "The period of time at the end of the day", "Noun", "/ˈiːvnɪŋ/", "The forest rests quietly in the evening.", "Hutan beristirahat dengan tenang di malam hari.", "A1", "Time"),
                        vocab("f_clock", "Clock", "Jam dinding", "A mechanical or electrical device for measuring time", "Noun", "/klɒk/", "The wooden clock on the wall ticks softly.", "Jam dinding kayu di dinding berdetak pelan.", "A1", "Time"),
                        vocab("f_minute", "Minute", "Menit", "A period of time equal to sixty seconds", "Noun", "/ˈmɪnɪt/", "Wait five minutes for the team to regroup.", "Tunggu lima menit sampai tim berkumpul kembali.", "A1", "Time"),
                        vocab("f_hour", "Hour", "Jam / waktu", "A period of time equal to a twenty-fourth part of a day", "Noun", "/ˈaʊə/", "It takes one hour to reach the ancient gate.", "Dibutuhkan satu jam untuk mencapai gerbang kuno.", "A1", "Time"),
                        vocab("f_today", "Today", "Hari ini", "On or in the course of this present day", "Noun", "/təˈdeɪ/", "Today we explore Chapter 1 together.", "Hari ini kita menjelajahi Bab 1 bersama-sama.", "A1", "Time"),
                        vocab("f_tomorrow", "Tomorrow", "Besok", "On the day after today", "Noun", "/təˈmɒrəʊ/", "Tomorrow we will reach the tropical island.", "Besok kita akan mencapai pulau tropis.", "A1", "Time"),
                        vocab("f_week", "Week", "Minggu", "A period of seven consecutive days", "Noun", "/wiːk/", "Practice vocabulary for one full week.", "Berlatihlah kosakata selama satu minggu penuh.", "A1", "Time"),
                        vocab("f_calendar", "Calendar", "Kalender", "A chart showing the days, weeks, and months of a year", "Noun", "/ˈkælɪndə/", "Check your study calendar for the next streak.", "Periksa kalender belajarmu untuk streak berikutnya.", "A2", "Time")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 7, levelId = "1-7",
                    topicTitle = "Food, Breakfast and Refreshing Drinks",
                    learningObjective = "Identify staple foods, morning meals, and energizing drinks.",
                    vocabulary = listOf(
                        vocab("f_rice", "Rice", "Beras / Nasi", "A swamp grass cultivated widely in warm climates", "Noun", "/raɪs/", "The team ate warm rice before the forest trek.", "Tim makan nasi hangat sebelum perjalanan ke hutan.", "A1", "Food"),
                        vocab("f_bread", "Bread", "Roti", "Food made of flour, water, and yeast or another leavening agent", "Noun", "/bred/", "Fresh bread provides energy for the journey.", "Roti segar memberikan energi untuk perjalanan.", "A1", "Food"),
                        vocab("f_water", "Water", "Air", "A colorless, transparent, odorless liquid", "Noun", "/ˈwɔːtə/", "Always carry clean drinking water in your flask.", "Selalu bawa air minum bersih dalam botolmu.", "A1", "Food"),
                        vocab("f_milk", "Milk", "Susu", "An opaque white fluid rich in fat and protein", "Noun", "/mɪlk/", "He drinks a glass of cold milk every morning.", "Dia minum segelas susu dingin setiap pagi.", "A1", "Food"),
                        vocab("f_tea", "Tea", "Teh", "A hot drink made by infusing dried crushed leaves in boiling water", "Noun", "/tiː/", "Warm tea soothes the explorer after a rainy march.", "Teh hangat menenangkan penjelajah setelah berjalan di tengah hujan.", "A1", "Food"),
                        vocab("f_fish", "Fish", "Ikan", "A limbless cold-blooded vertebrate animal with gills", "Noun", "/fɪʃ/", "Island fishermen caught fresh fish this morning.", "Nelayan pulau menangkap ikan segar pagi ini.", "A1", "Food"),
                        vocab("f_chicken", "Chicken", "Ayam", "A domestic fowl kept for its eggs or meat", "Noun", "/ˈtʃɪkɪn/", "They roasted chicken over the campfire.", "Mereka memanggang ayam di atas api unggun.", "A1", "Food"),
                        vocab("f_soup", "Soup", "Sup", "A liquid dish, typically made by boiling meat or vegetables", "Noun", "/suːp/", "Hot vegetable soup warms everyone at camp.", "Sup sayur hangat menghangatkan semua orang di perkemahan.", "A1", "Food"),
                        vocab("f_noodles", "Noodles", "Mie", "Strips or sheets of dough cooked in boiling liquid", "Noun", "/ˈnuːdlz/", "A bowl of noodles makes a satisfying field lunch.", "Semangkuk mie menjadi makan siang lapangan yang mengenyangkan.", "A1", "Food"),
                        vocab("f_cheese", "Cheese", "Keju", "A food made from the pressed curds of milk", "Noun", "/tʃiːz/", "The traveler shared bread and cheese with his companion.", "Pelancong itu berbagi roti dan keju dengan temannya.", "A1", "Food")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 8, levelId = "1-8",
                    topicTitle = "Fruits and Garden Vegetables",
                    learningObjective = "Recognize nutritious tropical fruits, orchard produce, and fresh greens.",
                    vocabulary = listOf(
                        vocab("f_apple", "Apple", "Apel", "The round fruit of a tree of the rose family", "Noun", "/ˈæpl/", "He picked a sweet red apple from the orchard.", "Dia memetik apel merah manis dari kebun.", "A1", "Fruits"),
                        vocab("f_banana", "Banana", "Pisang", "A long curved fruit with a yellow skin and soft sweet flesh", "Noun", "/bəˈnɑːnə/", "Wild monkeys enjoy ripe yellow bananas.", "Monyet liar menyukai pisang kuning yang matang.", "A1", "Fruits"),
                        vocab("f_orange_f", "Orange", "Jeruk", "A round juicy citrus fruit with a tough bright reddish-yellow rind", "Noun", "/ˈɒrɪndʒ/", "Drink fresh orange juice for breakfast.", "Minumlah jus jeruk segar untuk sarapan.", "A1", "Fruits"),
                        vocab("f_mango", "Mango", "Mangga", "A fleshy oval yellowish-red tropical fruit", "Noun", "/ˈmæŋɡəʊ/", "Sweet mangoes grow abundantly near the coast.", "Mangga manis tumbuh berlimpah di dekat pantai.", "A1", "Fruits"),
                        vocab("f_carrot", "Carrot", "Wortel", "A tapering orange-colored root eaten as a vegetable", "Noun", "/ˈkærət/", "Rabbits in the clearing crunch on fresh carrots.", "Kelinci di tempat terbuka mengunyah wortel segar.", "A1", "Vegetables"),
                        vocab("f_potato", "Potato", "Kentang", "A starchy plant tuber that is one of the most important food crops", "Noun", "/pəˈteɪtəʊ/", "Baked potatoes taste delicious at the evening camp.", "Kentang panggang terasa lezat di perkemahan malam hari.", "A1", "Vegetables"),
                        vocab("f_tomato", "Tomato", "Tomat", "A glossy red, or occasionally yellow, pulpy edible fruit", "Noun", "/təˈmɑːtəʊ/", "Ripe red tomatoes brighten the garden salad.", "Tomat merah matang mempercantik salad kebun.", "A1", "Vegetables"),
                        vocab("f_corn", "Corn", "Jagung", "A tall cereal plant that produces large grains set in rows", "Noun", "/kɔːn/", "Sweet golden corn was harvested yesterday.", "Jagung manis keemasan dipanen kemarin.", "A1", "Vegetables"),
                        vocab("f_spinach", "Spinach", "Bayam", "An edible flowering plant in the family Amaranthaceae", "Noun", "/ˈspɪnɪtʃ/", "Fresh green spinach gives you strength and stamina.", "Bayam hijau segar memberimu kekuatan dan stamina.", "A2", "Vegetables"),
                        vocab("f_grape", "Grape", "Anggur", "A berry, typically green, purple, or black, growing in clusters", "Noun", "/ɡreɪp/", "Sweet purple grapes ripened in the morning sun.", "Anggur ungu manis matang di bawah sinar matahari pagi.", "A1", "Fruits")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 9, levelId = "1-9",
                    topicTitle = "Forest Animals and Wildlife",
                    learningObjective = "Identify forest mammals, birds, and biodiversity in natural habitats.",
                    vocabulary = listOf(
                        vocab("f_bird", "Bird", "Burung", "A warm-blooded egg-laying vertebrate distinguished by feathers", "Noun", "/bɜːd/", "A colorful tropical bird sang high in the canopy.", "Seekor burung tropis berwarna-warni bernyanyi tinggi di kanopi.", "A1", "Animals"),
                        vocab("f_rabbit", "Rabbit", "Kelinci", "A burrowing, gregarious plant-eating mammal with long ears", "Noun", "/ˈræbɪt/", "A small brown rabbit hopped across the mossy path.", "Seekor kelinci cokelat kecil melompat melintasi jalan berlumut.", "A1", "Animals"),
                        vocab("f_monkey", "Monkey", "Monyet", "A small to medium-sized primate that typically has a long tail", "Noun", "/ˈmʌŋki/", "The curious monkey swung from branch to branch.", "Monyet yang penasaran itu berayun dari dahan ke dahan.", "A1", "Animals"),
                        vocab("f_deer", "Deer", "Rusa", "A hoofed grazing or browsing animal, with antlers in males", "Noun", "/dɪə/", "A gentle deer grazed peacefully near the riverbank.", "Seekor rusa yang anggun merumput dengan damai di dekat tepi sungai.", "A1", "Animals"),
                        vocab("f_elephant", "Elephant", "Gajah", "A very large plant-eating mammal with a trunk and ivory tusks", "Noun", "/ˈelɪfənt/", "The wise elephant led the herd through the jungle.", "Gajah yang bijaksana memimpin kawanannya melewati hutan rimba.", "A1", "Animals"),
                        vocab("f_tiger", "Tiger", "Harimau", "A very large solitary cat with a yellow-brown coat striped with black", "Noun", "/ˈtaɪɡə/", "We observed the majestic tiger from a safe distance.", "Kami mengamati harimau yang agung dari jarak yang aman.", "A1", "Animals"),
                        vocab("f_bear", "Bear", "Beruang", "A large, heavy mammal with thick fur and a very short tail", "Noun", "/beə/", "The mountain bear searched for honey in old hollow trees.", "Beruang gunung mencari madu di pohon tua yang berlubang.", "A1", "Animals"),
                        vocab("f_duck", "Duck", "Bebek", "A waterbird with a broad blunt bill and webbed feet", "Noun", "/dʌk/", "Ducks splashed happily in the clear forest creek.", "Bebek-bebek memercikkan air dengan gembira di anak sungai hutan yang jernih.", "A1", "Animals"),
                        vocab("f_horse", "Horse", "Kuda", "A solid-hoofed plant-eating domesticated mammal with a flowing mane", "Noun", "/hɔːs/", "The traveler rode a sturdy brown horse along the trail.", "Pelancong itu menunggangi kuda cokelat yang kokoh di sepanjang jalan setapak.", "A1", "Animals"),
                        vocab("f_butterfly", "Butterfly", "Kupu-kupu", "An insect with two pairs of large, typically brightly colored wings", "Noun", "/ˈbʌtəflaɪ/", "A vibrant blue butterfly fluttered over tropical blossoms.", "Kupu-kupu biru cerah berkibar di atas bunga-bunga tropis.", "A1", "Animals")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 10, levelId = "1-10",
                    topicTitle = "Daily Activities, Routines and Habits",
                    learningObjective = "Describe everyday routines, healthy habits, and chores from morning to night.",
                    vocabulary = listOf(
                        vocab("f_wake", "Wake", "Bangun", "Emerge or cause to emerge from sleep", "Verb", "/weɪk/", "Adventurers wake up early when the birds begin to sing.", "Para petualang bangun pagi saat burung-burung mulai bernyanyi.", "A1", "Routines"),
                        vocab("f_brush", "Brush", "Menyikat", "Clean with a brush", "Verb", "/brʌʃ/", "Brush your teeth every morning and evening.", "Sikat gigimu setiap pagi dan malam hari.", "A1", "Routines"),
                        vocab("f_wash", "Wash", "Mencuci", "Clean with water and, typically, soap or detergent", "Verb", "/wɒʃ/", "Wash your hands before eating your trail meal.", "Cuci tanganmu sebelum menyantap makanan bekalmu.", "A1", "Routines"),
                        vocab("f_eat", "Eat", "Makan", "Put food into the mouth and chew and swallow it", "Verb", "/iːt/", "Eat a nutritious breakfast to stay alert and energetic.", "Makanlah sarapan bergizi agar tetap waspada dan berenergi.", "A1", "Routines"),
                        vocab("f_sleep", "Sleep", "Tidur", "A condition of body and mind that typically recurs for several hours every night", "Verb", "/sliːp/", "Get enough sleep so your mind is sharp for tomorrow.", "Tidurlah yang cukup agar pikiranmu tajam untuk hari esok.", "A1", "Routines"),
                        vocab("f_walk", "Walk", "Berjalan", "Move at a regular pace by lifting and setting down each foot in turn", "Verb", "/wɔːk/", "We walk together across the suspension bridge.", "Kita berjalan bersama melintasi jembatan gantung.", "A1", "Routines"),
                        vocab("f_play", "Play", "Bermain", "Engage in activity for enjoyment and recreation", "Verb", "/pleɪ/", "Children play word puzzle games after study hours.", "Anak-anak bermain tebak kata setelah jam belajar.", "A1", "Routines"),
                        vocab("f_rest", "Rest", "Beristirahat", "Cease work or movement in order to relax or recover strength", "Verb", "/rest/", "Let us rest under the shade of this giant palm.", "Mari kita beristirahat di bawah naungan pohon palem raksasa ini.", "A1", "Routines"),
                        vocab("f_help", "Help", "Membantu", "Make it easier for someone to do something by offering services or resources", "Verb", "/help/", "Good teammates help each other overcome difficult obstacles.", "Rekan tim yang baik saling membantu mengatasi rintangan yang sulit.", "A1", "Routines"),
                        vocab("f_clean", "Clean", "Membersihkan", "Make free of dirt, marks, or unwanted matter", "Verb", "/kliːn/", "Keep our exploration campsite neat and clean.", "Jaga perkemahan penjelajahan kita tetap rapi dan bersih.", "A1", "Routines")
                    )
                )
            )

            // ==================== CHAPTER 2: ISLAND (2-1 to 2-10) ====================
            add(
                LevelTopicDefinition(
                    stage = 2, level = 1, levelId = "2-1",
                    topicTitle = "Tropical Weather and Climate Conditions",
                    learningObjective = "Describe maritime weather, wind patterns, rain, and tropical climates.",
                    vocabulary = listOf(
                        vocab("i_sunny", "Sunny", "Cerah berawan / Terang", "Bright with sunlight", "Adjective", "/ˈsʌni/", "It is a sunny day on the tropical island shore.", "Hari ini cerah di pantai pulau tropis.", "A1", "Weather"),
                        vocab("i_rainy", "Rainy", "Hujan", "Having a great deal of rain", "Adjective", "/ˈreɪni/", "The rainy season fills the freshwater streams.", "Musim hujan mengisi aliran air tawar.", "A1", "Weather"),
                        vocab("i_windy", "Windy", "Berangin", "Marked by or exposed to strong winds", "Adjective", "/ˈwɪndi/", "A windy afternoon caused white waves along the coast.", "Sore yang berangin menimbulkan ombak putih di sepanjang pantai.", "A1", "Weather"),
                        vocab("i_stormy", "Stormy", "Badai", "Characterized by violent weather with much rain, wind, or snow", "Adjective", "/ˈstɔːmi/", "Sailors stayed ashore during the stormy night.", "Pelaut tetap di pantai selama malam badai.", "A2", "Weather"),
                        vocab("i_cloudy", "Cloudy", "Berawan", "Covered with or characterized by clouds", "Adjective", "/ˈklaʊdi/", "Cloudy skies kept the tropical heat pleasant.", "Langit berawan membuat panas tropis tetap menyenangkan.", "A1", "Weather"),
                        vocab("i_rainbow", "Rainbow", "Pelangi", "An arch of colors formed in the sky in certain circumstances", "Noun", "/ˈreɪnbəʊ/", "A magnificent rainbow arched over the lagoon.", "Pelangi yang megah melengkung di atas laguna.", "A1", "Weather"),
                        vocab("i_breeze", "Breeze", "Angin sepoi-sepoi", "A gentle wind", "Noun", "/briːz/", "A gentle sea breeze cooled the sandy beach.", "Angin laut sepoi-sepoi mendinginkan pantai berpasir.", "A2", "Weather"),
                        vocab("i_warm", "Warm", "Hangat", "Of or at a fairly or comfortably high temperature", "Adjective", "/wɔːm/", "The turquoise ocean water felt pleasant and warm.", "Air laut toska terasa menyenangkan dan hangat.", "A1", "Weather"),
                        vocab("i_humid", "Humid", "Lembap", "Marked by a relatively high level of water vapor in the atmosphere", "Adjective", "/ˈhjuːmɪd/", "The island air is warm and humid near the rainforest.", "Udara pulau hangat dan lembap di dekat hutan hujan.", "B1", "Weather"),
                        vocab("i_monsoon", "Monsoon", "Musim muson", "A seasonal prevailing wind in the region of South and Southeast Asia", "Noun", "/mɒnˈsuːn/", "The monsoon brought refreshing rain to the island crops.", "Muson membawa hujan yang menyegarkan bagi tanaman pulau.", "B1", "Weather")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 2, levelId = "2-2",
                    topicTitle = "Island Clothing, Garments and Accessories",
                    learningObjective = "Identify coastal wear, footwear, sun protection, and outdoor gear.",
                    vocabulary = listOf(
                        vocab("i_shirt", "Shirt", "Kemeja / Kaos", "A garment for the upper body made of cotton or a similar fabric", "Noun", "/ʃɜːt/", "Wear a light cotton shirt on hot island days.", "Kenakan kemeja katun ringan pada hari-hari panas di pulau.", "A1", "Clothing"),
                        vocab("i_pants", "Pants", "Celana panjang", "An outer garment covering the body from the waist to the ankles", "Noun", "/pænts/", "Durable cargo pants protect your legs while hiking.", "Celana kargo yang tahan lama melindungi kakimu saat mendaki.", "A1", "Clothing"),
                        vocab("i_jacket", "Jacket", "Jaket", "An outer garment extending either to the waist or the hips", "Noun", "/ˈdʒækɪt/", "Put on your waterproof jacket if rain starts falling.", "Kenakan jaket tahan airmu jika hujan mulai turun.", "A1", "Clothing"),
                        vocab("i_shoes", "Shoes", "Sepatu", "A pair of coverings for the foot, typically made of leather", "Noun", "/ʃuːz/", "Sturdy hiking shoes provide great traction on coastal rocks.", "Sepatu daki yang kokoh memberikan cengkeraman hebat di bebatuan pantai.", "A1", "Clothing"),
                        vocab("i_hat", "Hat", "Topi", "A shaped covering for the head worn for warmth or as a fashion item", "Noun", "/hæt/", "A wide brim hat shades your face from the tropical sun.", "Topi bertepi lebar melindungi wajahmu dari terik matahari tropis.", "A1", "Clothing"),
                        vocab("i_sunglasses", "Sunglasses", "Kacamata hitam", "Glasses tinted to protect the eyes from sunlight or glare", "Noun", "/ˈsʌnˌɡlɑːsɪz/", "Wear sunglasses to protect your eyes on the bright beach.", "Gunakan kacamata hitam untuk melindungi mata di pantai yang terang.", "A2", "Clothing"),
                        vocab("i_sandals", "Sandals", "Sandal", "A light shoe with straps that fasten over the foot", "Noun", "/ˈsændlz/", "He slipped into sandals while walking along the sandy shore.", "Dia memakai sandal saat berjalan di sepanjang pantai berpasir.", "A1", "Clothing"),
                        vocab("i_scarf", "Scarf", "Syal", "A length of fabric worn around the neck or head", "Noun", "/skɑːf/", "She tied a light cotton scarf to protect her neck from wind.", "Dia mengikatkan syal katun ringan untuk melindungi lehernya dari angin.", "A2", "Clothing"),
                        vocab("i_belt", "Belt", "Ikat pinggang", "A strip of leather or other material worn around the waist", "Noun", "/belt/", "His leather belt holds his water flask and pouch.", "Ikat pinggang kulitnya menampung botol air dan kantong kecilnya.", "A1", "Clothing"),
                        vocab("i_uniform", "Uniform", "Seragam", "The distinctive clothing worn by members of the same organization", "Noun", "/ˈjuːnɪfɔːm/", "Young explorers wear a neat school uniform with pride.", "Penjelajah muda mengenakan seragam sekolah yang rapi dengan bangga.", "A2", "Clothing")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 3, levelId = "2-3",
                    topicTitle = "Ocean Wonders and Marine Creatures",
                    learningObjective = "Explore aquatic life, coral reefs, and oceanic biodiversity.",
                    vocabulary = listOf(
                        vocab("i_ocean", "Ocean", "Samudra", "A very large expanse of sea, in particular each of the main areas", "Noun", "/ˈəʊʃn/", "The vast turquoise ocean stretches as far as the eye can see.", "Samudra toska yang luas membentang sejauh mata memandang.", "A1", "Marine"),
                        vocab("i_coral", "Coral", "Karang laut", "A hard stony substance secreted by certain marine organisms", "Noun", "/ˈkɒrəl/", "Colorful coral formations shelter hundreds of fish.", "Formasi karang laut berwarna-warni melindungi ratusan ikan.", "A2", "Marine"),
                        vocab("i_dolphin", "Dolphin", "Lumba-lumba", "A small gregarious toothed whale that typically has a beaklike snout", "Noun", "/ˈdɒlfɪn/", "A friendly dolphin leaped beside our expedition canoe.", "Seekor lumba-lumba yang ramah melompat di samping kano ekspedisi kami.", "A1", "Marine"),
                        vocab("i_turtle", "Turtle", "Penyu", "A slow-moving reptile enclosed in a scaly or leathery domed shell", "Noun", "/ˈtɜːtl/", "A sea turtle laid her eggs quietly on the midnight shore.", "Seekor penyu bertelur dengan tenang di pantai tengah malam.", "A1", "Marine"),
                        vocab("i_whale", "Whale", "Paus", "A very large marine mammal with a streamlined body", "Noun", "/weɪl/", "We saw the magnificent blowhole spray of a blue whale.", "Kami melihat semprotan lubang sembur yang luar biasa dari paus biru.", "A1", "Marine"),
                        vocab("i_shell", "Shell", "Kerang", "The hard protective outer case of a mollusk", "Noun", "/ʃel/", "Children collect shiny spiral shells along the tidal sand.", "Anak-anak mengumpulkan cangkang kerang spiral yang berkilau di pasir pasang.", "A1", "Marine"),
                        vocab("i_wave", "Wave", "Ombak", "A long body of water curling into an arched form and breaking on the shore", "Noun", "/weɪv/", "A rhythmic ocean wave broke gently against the wooden pier.", "Ombak samudra yang berirama pecah lembut di dermaga kayu.", "A1", "Marine"),
                        vocab("i_tide", "Tide", "Pasang surut", "The alternate rising and falling of the sea", "Noun", "/taɪd/", "At low tide, hidden rock pools become visible.", "Saat air surut, kolam batu tersembunyi menjadi terlihat.", "B1", "Marine"),
                        vocab("i_crab", "Crab", "Kepiting", "A crustacean with a broad carapace and stalked eyes", "Noun", "/kræb/", "A little hermit crab scurried behind a driftwood log.", "Seekor kepiting pertapa kecil bergegas di balik batang kayu apung.", "A1", "Marine"),
                        vocab("i_starfish", "Starfish", "Bintang laut", "A marine echinoderm with five or more radiating arms", "Noun", "/ˈstɑːfɪʃ/", "A bright orange starfish rested in the shallow tidal pool.", "Bintang laut berwarna oranye terang beristirahat di kolam pasang dangkal.", "A2", "Marine")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 4, levelId = "2-4",
                    topicTitle = "Beach Geography and Island Coastal Features",
                    learningObjective = "Identify coastal landforms, lagoons, reefs, and tropical shores.",
                    vocabulary = listOf(
                        vocab("i_coast", "Coast", "Pesisir", "The part of the land adjoining or near the sea", "Noun", "/kəʊst/", "Lush palm trees line the island's rocky coast.", "Pohon kelapa yang rimbun berjajar di pesisir berbatu pulau itu.", "A2", "Geography"),
                        vocab("i_shore", "Shore", "Tepi pantai", "The land along the edge of a sea, lake, or other large body of water", "Noun", "/ʃɔː/", "Waves wash glistening golden sand onto the shore.", "Ombak menyapu pasir keemasan yang berkilau ke tepi pantai.", "A1", "Geography"),
                        vocab("i_sand", "Sand", "Pasir", "A loose granular substance resulting from the erosion of siliceous rocks", "Noun", "/sænd/", "Walking barefoot on warm sand is very relaxing.", "Berjalan tanpa alas kaki di atas pasir hangat sangat menenangkan.", "A1", "Geography"),
                        vocab("i_lagoon", "Lagoon", "Laguna", "A stretch of salt water separated from the sea by a low sandbank or reef", "Noun", "/ləˈɡuːn/", "The sheltered lagoon is calm and crystal clear.", "Laguna yang terlindung tenang dan sebening kristal.", "B1", "Geography"),
                        vocab("i_reef", "Reef", "Terumbu karang", "A ridge of jagged rock, coral, or sand just above or below the surface", "Noun", "/riːf/", "Colorful marine animals thrive around the offshore reef.", "Hewan laut berwarna-warni berkembang biak di sekitar terumbu karang lepas pantai.", "B1", "Geography"),
                        vocab("i_palm", "Palm", "Pohon palem / kelapa", "An unbranched evergreen tree with a crown of large leaves", "Noun", "/pɑːm/", "Tall palm trees sway gracefully with the ocean breeze.", "Pohon palem yang tinggi bergoyang anggun ditiup angin laut.", "A1", "Geography"),
                        vocab("i_cove", "Cove", "Teluk kecil", "A small, sheltered bay along a coast", "Noun", "/kəʊv/", "Pirate legends say gold is buried in this hidden cove.", "Legenda bajak laut mengatakan emas terkubur di teluk kecil tersembunyi ini.", "B1", "Geography"),
                        vocab("i_peninsula", "Peninsula", "Semenanjung", "A piece of land almost surrounded by water or projecting out into a body of water", "Noun", "/pəˈnɪnsjələ/", "A lighthouse stands at the tip of the rocky peninsula.", "Sebuah mercusuar berdiri di ujung semenanjung berbatu.", "B2", "Geography"),
                        vocab("i_harbor", "Harbor", "Pelabuhan alam", "A place on the coast where vessels may find shelter", "Noun", "/ˈhɑːbə/", "Wooden fishing boats rest peacefully in the island harbor.", "Perahu nelayan kayu beristirahat dengan damai di pelabuhan pulau.", "B1", "Geography"),
                        vocab("i_bay", "Bay", "Teluk", "A broad inlet of the sea where the land curves inward", "Noun", "/beɪ/", "Dolphins gather every morning inside Emerald Bay.", "Lumba-lumba berkumpul setiap pagi di dalam Teluk Zamrud.", "A2", "Geography")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 5, levelId = "2-5",
                    topicTitle = "Emotions, Feelings and Explorer Mindsets",
                    learningObjective = "Express feelings, courage, curiosity, and psychological states.",
                    vocabulary = listOf(
                        vocab("i_happy", "Happy", "Bahagia / Senang", "Feeling or showing pleasure or contentment", "Adjective", "/ˈhæpi/", "Students feel happy when solving challenging vocabulary quizzes.", "Siswa merasa bahagia saat menyelesaikan kuis kosakata yang menantang.", "A1", "Emotions"),
                        vocab("i_excited", "Excited", "Bersemangat", "Very enthusiastic and eager", "Adjective", "/ɪkˈsaɪtɪd/", "We were excited to discover the Roman bridge ahead.", "Kami sangat bersemangat menemukan jembatan Romawi di depan.", "A1", "Emotions"),
                        vocab("i_calm", "Calm", "Tenang", "Not showing or feeling nervousness, anger, or other strong emotions", "Adjective", "/kɑːm/", "Take a deep breath and stay calm under pressure.", "Tarik napas dalam-dalam dan tetap tenang di bawah tekanan.", "A2", "Emotions"),
                        vocab("i_tired", "Tired", "Lelah", "In need of sleep or rest; weary", "Adjective", "/ˈtaɪəd/", "After a long jungle hike, the young explorers felt tired.", "Setelah perjalanan panjang di hutan, para penjelajah muda merasa lelah.", "A1", "Emotions"),
                        vocab("i_surprised", "Surprised", "Terkejut", "Feeling or showing surprise", "Adjective", "/səˈpraɪzd/", "She was surprised to see glowing blue crystals.", "Dia terkejut melihat kristal biru yang menyala.", "A2", "Emotions"),
                        vocab("i_proud", "Proud", "Bangga", "Feeling deep pleasure or satisfaction as a result of one's achievements", "Adjective", "/praʊd/", "He was proud to earn three stars on his first attempt.", "Dia bangga memperoleh tiga bintang pada percobaan pertamanya.", "A2", "Emotions"),
                        vocab("i_brave", "Brave", "Pemberani", "Ready to face and endure danger or pain; showing courage", "Adjective", "/breɪv/", "A brave adventurer crosses the suspension bridge without fear.", "Penjelajah pemberani menyeberangi jembatan gantung tanpa rasa takut.", "A2", "Emotions"),
                        vocab("i_curious", "Curious", "Penasaran / Ingin tahu", "Eager to know or learn something", "Adjective", "/ˈkjʊəriəs/", "Curious students ask insightful questions in class.", "Siswa yang ingin tahu mengajukan pertanyaan berbobot di kelas.", "B1", "Emotions"),
                        vocab("i_nervous", "Nervous", "Gugup", "Easily agitated or alarmed; tending to be anxious", "Adjective", "/ˈnɜːvəs/", "Do not feel nervous before answering English questions.", "Jangan merasa gugup sebelum menjawab pertanyaan bahasa Inggris.", "A2", "Emotions"),
                        vocab("i_cheerful", "Cheerful", "Ceria", "Noticeably happy and optimistic", "Adjective", "/ˈtʃɪəfl/", "Her cheerful greeting made everyone smile warmly.", "Salam cerianya membuat semua orang tersenyum hangat.", "B1", "Emotions")
                    )
                )
            )

            // Continue for remaining levels in chapters 2, 3, 4, 5...
            add(
                LevelTopicDefinition(
                    stage = 2, level = 6, levelId = "2-6",
                    topicTitle = "Health, Energy and the Human Body",
                    learningObjective = "Recognize body parts, physical fitness, and health maintenance.",
                    vocabulary = listOf(
                        vocab("i_head", "Head", "Kepala", "The upper part of the human body", "Noun", "/hed/", "Wear a protective helmet on your head while climbing.", "Kenakan helm pelindung di kepalamu saat mendaki.", "A1", "Health"),
                        vocab("i_eyes", "Eyes", "Mata", "Globular organs of sight in the head", "Noun", "/aɪz/", "Keep your eyes open for hidden trail markers.", "Buka matamu untuk melihat penanda jalur tersembunyi.", "A1", "Health"),
                        vocab("i_ears", "Ears", "Telinga", "The organs of hearing and balance in humans", "Noun", "/ɪəz/", "Our ears hear the soothing sound of ocean waves.", "Telinga kita mendengar suara menenangkan dari ombak samudra.", "A1", "Health"),
                        vocab("i_hands", "Hands", "Tangan", "The end parts of a person's arms beyond the wrist", "Noun", "/hændz/", "Use both hands to grip the climbing rope firmly.", "Gunakan kedua tangan untuk menggenggam tali panjat dengan erat.", "A1", "Health"),
                        vocab("i_feet", "Feet", "Kaki", "The lower extremities of the legs below the ankles", "Noun", "/fiːt/", "Rest your feet after ten kilometers of trail walking.", "Istirahatkan kakimu setelah berjalan di jalan setapak sejauh sepuluh kilometer.", "A1", "Health"),
                        vocab("i_heart", "Heart", "Jantung", "A hollow muscular organ that pumps blood through the circulatory system", "Noun", "/hɑːt/", "His heart beat with excitement as he unlocked the gate.", "Jantungnya berdegup kencang karena gembira saat dia membuka gerbang.", "A2", "Health"),
                        vocab("i_healthy", "Healthy", "Sehat", "In good health; not diseased", "Adjective", "/ˈhelθi/", "Fresh fruit and clean water keep you strong and healthy.", "Buah segar dan air bersih membuatmu tetap kuat dan sehat.", "A1", "Health"),
                        vocab("i_medicine", "Medicine", "Obat", "The science or practice of the diagnosis, treatment, and prevention of disease", "Noun", "/ˈmedsn/", "Carry a first-aid kit with basic antiseptic medicine.", "Bawalah kotak P3K dengan obat antiseptik dasar.", "A2", "Health"),
                        vocab("i_energy", "Energy", "Energi", "The strength and vitality required for sustained activity", "Noun", "/ˈenədʒi/", "A wholesome breakfast gives you lasting trail energy.", "Sarapan sehat memberimu energi perjalanan yang tahan lama.", "A2", "Health"),
                        vocab("i_fit", "Fit", "Bugar", "In good health, especially because of regular physical exercise", "Adjective", "/fɪt/", "Regular exercise keeps junior high students strong and fit.", "Olahraga teratur membuat siswa SMP tetap kuat dan bugar.", "A2", "Health")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 7, levelId = "2-7",
                    topicTitle = "Sports, Athletics and Outdoor Games",
                    learningObjective = "Describe sporting activities, team sports, and athletic competitions.",
                    vocabulary = listOf(
                        vocab("i_swimming", "Swimming", "Berenang", "The sport or activity of propelling oneself through water", "Noun", "/ˈswɪmɪŋ/", "Swimming in the clear blue lagoon is great exercise.", "Berenang di laguna biru jernih adalah latihan yang bagus.", "A1", "Sports"),
                        vocab("i_running", "Running", "Berlari", "The action or movement of a runner", "Noun", "/ˈrʌnɪŋ/", "Morning running along the beach builds strong legs.", "Berlari pagi di sepanjang pantai membentuk kaki yang kuat.", "A1", "Sports"),
                        vocab("i_football", "Football", "Sepak bola", "A game played by two teams of eleven players with a spherical ball", "Noun", "/ˈfʊtbɔːl/", "Students enjoy playing football on the school field.", "Siswa senang bermain sepak bola di lapangan sekolah.", "A1", "Sports"),
                        vocab("i_basketball", "Basketball", "Bola basket", "A game played between two teams of five players on a court", "Noun", "/ˈbɑːskɪtbɔːl/", "He scored three points in yesterday's basketball match.", "Dia mencetak tiga poin dalam pertandingan bola basket kemarin.", "A1", "Sports"),
                        vocab("i_cycling", "Cycling", "Bersepeda", "The activity of riding a bicycle", "Noun", "/ˈsaɪklɪŋ/", "Island cycling paths offer scenic ocean views.", "Jalur bersepeda di pulau menawarkan pemandangan laut yang indah.", "A2", "Sports"),
                        vocab("i_diving", "Diving", "Menyelam", "The sport or activity of swimming or exploring underwater", "Noun", "/ˈdaɪvɪŋ/", "Scuba diving allows explorers to inspect submerged ruins.", "Menyelam memungkinkan penjelajah memeriksa reruntuhan yang tenggelam.", "B1", "Sports"),
                        vocab("i_surf", "Surf", "Berselancar", "Ride on the crest of a wave, typically toward the shore", "Verb", "/sɜːf/", "Skilled surfers glide across rolling coastal waves.", "Peselancar terampil meluncur melintasi ombak pantai yang bergulung.", "A2", "Sports"),
                        vocab("i_catch", "Catch", "Menangkap", "Intercept and hold something which has been thrown", "Verb", "/kætʃ/", "Catch the ball before it lands in the sand.", "Tangkap bolanya sebelum mendarat di pasir.", "A1", "Sports"),
                        vocab("i_race", "Race", "Perlombaan", "A competition between runners or vessels to see which is fastest", "Noun", "/reɪs/", "The annual canoe race begins at the island pier.", "Lomba kano tahunan dimulai di dermaga pulau.", "A2", "Sports"),
                        vocab("i_victory", "Victory", "Kemenangan", "An act of defeating an enemy or opponent in a battle or competition", "Noun", "/ˈvɪktəri/", "Celebrate victory with good sportsmanship and humility.", "Rayakan kemenangan dengan sportivitas dan kerendahan hati.", "B1", "Sports")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 8, levelId = "2-8",
                    topicTitle = "Hobbies, Arts and Creative Leisure",
                    learningObjective = "Express creative pursuits, artistic hobbies, and recreational interests.",
                    vocabulary = listOf(
                        vocab("i_reading", "Reading", "Membaca", "The action or skill of reading written or printed matter", "Noun", "/ˈriːdɪŋ/", "Reading English adventure novels broadens your vocabulary.", "Membaca novel petualangan berbahasa Inggris memperluas kosakatamu.", "A1", "Hobbies"),
                        vocab("i_painting", "Painting", "Melukis", "The action or art of using paint", "Noun", "/ˈpeɪntɪŋ/", "Watercolor painting captured the vibrant island sunset.", "Lukisan cat air menangkap matahari terbenam pulau yang semarak.", "A2", "Hobbies"),
                        vocab("i_singing", "Singing", "Bernyanyi", "The activity of performing songs with the voice", "Noun", "/ˈsɪŋɪŋ/", "Singing cheerful melodies lifts everyone's spirits.", "Bernyanyi melodi ceria membangkitkan semangat semua orang.", "A1", "Hobbies"),
                        vocab("i_dancing", "Dancing", "Menari", "The movement of the body in a rhythmic way, usually to music", "Noun", "/ˈdɑːnsɪŋ/", "Traditional island dancing welcomes visiting travelers.", "Tarian tradisional pulau menyambut para pelancong yang berkunjung.", "A1", "Hobbies"),
                        vocab("i_gardening", "Gardening", "Berkebun", "The activity of tending and cultivating a garden", "Noun", "/ˈɡɑːdnɪŋ/", "Gardening teaches patience and respect for nature.", "Berkebun mengajarkan kesabaran dan rasa hormat terhadap alam.", "A2", "Hobbies"),
                        vocab("i_cooking", "Cooking", "Memasak", "The practice or skill of preparing food by combining ingredients", "Noun", "/ˈkʊkɪŋ/", "Cooking with fresh local herbs makes delicious meals.", "Memasak dengan rempah-rempah lokal segar menghasilkan hidangan lezat.", "A1", "Hobbies"),
                        vocab("i_photography", "Photography", "Fotografi", "The art or practice of taking and processing photographs", "Noun", "/fəˈtɒɡrəfi/", "Landscape photography documents rare animals on our journey.", "Fotografi pemandangan mendokumentasikan hewan langka dalam perjalanan kami.", "B1", "Hobbies"),
                        vocab("i_music", "Music", "Musik", "Vocal or instrumental sounds combined in such a way as to produce beauty", "Noun", "/ˈmjuːzɪk/", "Soft acoustic guitar music played beside the campfire.", "Musik gitar akustik yang lembut dimainkan di samping api unggun.", "A1", "Hobbies"),
                        vocab("i_craft", "Craft", "Kerajinan tangan", "An activity involving skill in making things by hand", "Noun", "/krɑːft/", "Wood carving is a cherished island artisan craft.", "Ukiran kayu adalah kerajinan tangan pengrajin pulau yang sangat berharga.", "B1", "Hobbies"),
                        vocab("i_camping", "Camping", "Berkemah", "The activity of spending a vacation living in a tent", "Noun", "/ˈkæmpɪŋ/", "Camping under starry skies is an unforgettable experience.", "Berkemah di bawah langit berbintang adalah pengalaman yang tak terlupakan.", "A2", "Hobbies")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 9, levelId = "2-9",
                    topicTitle = "Travel, Maritime Voyages and Transportation",
                    learningObjective = "Describe methods of transit, nautical journeys, and travel tools.",
                    vocabulary = listOf(
                        vocab("i_boat", "Boat", "Perahu", "A small vessel for traveling on water", "Noun", "/bəʊt/", "A small wooden boat sailed across the tranquil lagoon.", "Perahu kayu kecil berlayar melintasi laguna yang tenang.", "A1", "Travel"),
                        vocab("i_ship", "Ship", "Kapal besar", "A large boat for transporting people or goods by sea", "Noun", "/ʃɪp/", "An ancient trading ship anchored outside the reef.", "Kapal dagang kuno berlabuh di luar terumbu karang.", "A1", "Travel"),
                        vocab("i_canoe", "Canoe", "Kano", "A light, narrow boat with pointed ends, propelled with a paddle", "Noun", "/kəˈnuː/", "We paddled the wooden canoe along the calm river.", "Kami mendayung kano kayu di sepanjang sungai yang tenang.", "A2", "Travel"),
                        vocab("i_bicycle", "Bicycle", "Sepeda", "A vehicle consisting of two wheels held in a frame one behind the other", "Noun", "/ˈbaɪsɪkl/", "Riding a bicycle is an eco-friendly way to tour the island.", "Mengendarai sepeda adalah cara ramah lingkungan untuk berkeliling pulau.", "A1", "Travel"),
                        vocab("i_bus", "Bus", "Bus", "A large motor vehicle carrying passengers by road", "Noun", "/bʌs/", "The expedition bus brought students to the coastal trail.", "Bus ekspedisi membawa siswa ke jalan setapak pesisir.", "A1", "Travel"),
                        vocab("i_ferry", "Ferry", "Kapal feri", "A boat or ship for conveying passengers and goods across water", "Noun", "/ˈferi/", "The island ferry operates twice daily between harbors.", "Kapal feri pulau beroperasi dua kali sehari antar pelabuhan.", "A2", "Travel"),
                        vocab("i_voyage", "Voyage", "Pelayaran panjang", "A long journey involving travel by sea or in space", "Noun", "/ˈvɔɪɪdʒ/", "Their voyage across the uncharted sea lasted three weeks.", "Pelayaran mereka melintasi laut yang belum dipetakan berlangsung selama tiga minggu.", "B1", "Travel"),
                        vocab("i_ticket", "Ticket", "Tiket", "A piece of paper or card that gives the holder a certain right", "Noun", "/ˈtɪkɪt/", "Show your boarding ticket before entering the ship.", "Tunjukkan tiket naik Anda sebelum memasuki kapal.", "A1", "Travel"),
                        vocab("i_guide", "Guide", "Pemandu", "A person who advises or shows the way to others", "Noun", "/ɡaɪd/", "Our experienced island guide pointed out safe mountain passes.", "Pemandu pulau kami yang berpengalaman menunjukkan celah gunung yang aman.", "A2", "Travel"),
                        vocab("i_compass_t", "Compass", "Kompas", "An instrument containing a magnetized pointer showing directions", "Noun", "/ˈkʌmpəs/", "Hold the brass compass level to read magnetic north.", "Pegang kompas kuningan mendatar untuk membaca utara magnetis.", "A2", "Travel")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 10, levelId = "2-10",
                    topicTitle = "Island Community, Village and Traditional Culture",
                    learningObjective = "Understand community life, local markets, and cultural heritage.",
                    vocabulary = listOf(
                        vocab("i_village", "Village", "Desa", "A group of houses and associated buildings in a rural area", "Noun", "/ˈvɪlɪdʒ/", "A tranquil fishing village nestles beside the coconut grove.", "Sebuah desa nelayan yang tenang terletak di samping hutan kelapa.", "A1", "Community"),
                        vocab("i_market", "Market", "Pasar", "A regular gathering of people for the purchase and sale of provisions", "Noun", "/ˈmɑːkɪt/", "Buy tropical fruit and spices at the morning market.", "Belilah buah tropis dan rempah-rempah di pasar pagi.", "A1", "Community"),
                        vocab("i_fisherman", "Fisherman", "Nelayan", "A person who catches fish for a living or for sport", "Noun", "/ˈfɪʃəmən/", "The humble fisherman mended his nets on the pier.", "Nelayan sederhana itu memperbaiki jalanya di dermaga.", "A2", "Community"),
                        vocab("i_hut", "Hut", "Pondok", "A small, simple, single-story house or shelter", "Noun", "/hʌt/", "A thatched bamboo hut provided shelter from sudden showers.", "Pondok bambu beratap jerami memberikan perlindungan dari hujan mendadak.", "A2", "Community"),
                        vocab("i_community", "Community", "Komunitas / Masyarakat", "A group of people living in the same place or having a particular characteristic", "Noun", "/kəˈmjuːnəti/", "The island community welcomed travelers with open arms.", "Masyarakat pulau menyambut para pelancong dengan tangan terbuka.", "B1", "Community"),
                        vocab("i_gather", "Gather", "Berkumpul", "Come together; assemble or accumulate", "Verb", "/ˈɡæðə/", "Villagers gather around the ancient banyan tree at sunset.", "Warga desa berkumpul di sekitar pohon beringin kuno saat matahari terbenam.", "A2", "Community"),
                        vocab("i_neighbor", "Neighbor", "Tetangga", "A person living next door to or very near to the speaker", "Noun", "/ˈneɪbə/", "A friendly neighbor shared freshly baked coconut cakes.", "Tetangga yang ramah berbagi kue kelapa yang baru dipanggang.", "A2", "Community"),
                        vocab("i_festival", "Festival", "Festival / Perayaan", "A day or period of celebration, typically for religious reasons", "Noun", "/ˈfestɪvl/", "Music and colorful lanterns lit up the harvest festival.", "Musik dan lampion warna-warni memeriahkan festival panen.", "A2", "Community"),
                        vocab("i_feast", "Feast", "Pesta perjamuan", "A large meal, typically one in celebration of something", "Noun", "/fiːst/", "The celebratory feast included tropical delicacies for all.", "Pesta perjamuan perayaan mencakup makanan lezat tropis untuk semua.", "B1", "Community"),
                        vocab("i_heritage", "Heritage", "Warisan budaya", "Property that is or may be inherited; an inheritance", "Noun", "/ˈherɪtɪdʒ/", "Preserve the island's unique architectural heritage.", "Lestarikan warisan arsitektur pulau yang unik.", "B2", "Community")
                    )
                )
            )

            // ==================== CHAPTER 3: ANCIENT ROME (3-1 to 3-10) ====================
            add(
                LevelTopicDefinition(
                    stage = 3, level = 1, levelId = "3-1",
                    topicTitle = "Classical Roman Architecture and Colosseum",
                    learningObjective = "Identify monumental arches, stone pillars, and classical Roman building techniques.",
                    vocabulary = listOf(
                        vocab("r_colosseum", "Colosseum", "Koloseum", "A vast ancient amphitheater in Rome used for public spectacles", "Noun", "/ˌkɒləˈsiːəm/", "The grand Colosseum stands majestically under the Italian sun.", "Koloseum yang megah berdiri megah di bawah sinar matahari Italia.", "B1", "Architecture"),
                        vocab("r_pillar", "Pillar", "Pilar / Tiang batu", "A tall vertical structure used as a support for a building", "Noun", "/ˈpɪlə/", "Carved marble pillars hold up the classical temple roof.", "Pilar marmer berukir menopang atap kuil klasik.", "A2", "Architecture"),
                        vocab("r_arch", "Arch", "Lengkungan batu", "A curved symmetrical structure spanning an opening", "Noun", "/ɑːtʃ/", "Roman engineers perfected the stone triumphal arch.", "Insinyur Romawi menyempurnakan lengkungan kemenangan dari batu.", "B1", "Architecture"),
                        vocab("r_temple", "Temple", "Kuil kuno", "A building devoted to the worship of a god or gods", "Noun", "/ˈtempl/", "The classical temple featured bronze doors and marble statues.", "Kuil klasik menampilkan pintu perunggu dan patung marmer.", "A2", "Architecture"),
                        vocab("r_forum", "Forum", "Forum / Pusat kota", "A place, meeting, or medium where ideas and views can be exchanged", "Noun", "/ˈfɔːrəm/", "Citizens gathered in the central Roman Forum to discuss civic laws.", "Warga berkumpul di Forum Romawi pusat untuk membahas hukum sipil.", "B1", "Architecture"),
                        vocab("r_aqueduct", "Aqueduct", "Saluran air Romawi", "An artificial channel for conveying water, typically in the form of a bridge", "Noun", "/ˈækwɪdʌkt/", "The stone aqueduct brought pure spring water from distant hills.", "Saluran air batu membawa air mata air murni dari perbukitan yang jauh.", "B2", "Architecture"),
                        vocab("r_monument", "Monument", "Monumen", "A statue, building, or other structure erected to commemorate a person or event", "Noun", "/ˈmɒnjumənt/", "The carved stone monument honors ancient architects.", "Monumen batu berukir menghormati arsitek kuno.", "B1", "Architecture"),
                        vocab("r_marble", "Marble", "Batu marmer", "A hard crystalline metamorphic form of limestone", "Noun", "/ˈmɑːbl/", "White Carrara marble covered the public bathhouses.", "Marmer Carrara putih menutupi pemandian umum.", "B1", "Architecture"),
                        vocab("r_dome", "Dome", "Kubah", "A rounded vault forming the roof of a building or structure", "Noun", "/dəʊm/", "The Pantheon is famous for its grand concrete dome.", "Pantheon terkenal dengan kubah betonnya yang megah.", "B1", "Architecture"),
                        vocab("r_palace", "Palace", "Istana", "A large and impressive building forming the official residence of a ruler", "Noun", "/ˈpæləs/", "The Emperor built an opulent palace on the Palatine Hill.", "Kaisar membangun istana mewah di Bukit Palatine.", "A2", "Architecture")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 3, level = 2, levelId = "3-2",
                    topicTitle = "Imperial History and Civilization",
                    learningObjective = "Understand terms of ancient governance, emperors, legions, and dynasties.",
                    vocabulary = listOf(
                        vocab("r_emperor", "Emperor", "Kaisar", "A sovereign ruler of great power and rank, especially one ruling an empire", "Noun", "/ˈempərə/", "The Roman Emperor proclaimed a month of public festivals.", "Kaisar Romawi mengumumkan sebulan festival umum.", "B1", "History"),
                        vocab("r_empire", "Empire", "Kekaisaran", "An extensive group of states or countries under a single supreme authority", "Noun", "/ˈempaɪə/", "The Roman Empire stretched across three continents.", "Kekaisaran Romawi membentang di tiga benua.", "B1", "History"),
                        vocab("r_history", "History", "Sejarah", "The study of past events, particularly in human affairs", "Noun", "/ˈhɪstri/", "Studying ancient history helps us build a brighter future.", "Mempelajari sejarah kuno membantu kita membangun masa depan yang lebih cerah.", "A1", "History"),
                        vocab("r_roman", "Roman", "Bangsa Romawi", "Relating to ancient Rome or its empire or people", "Adjective", "/ˈrəʊmən/", "Roman roads were built to last for thousands of years.", "Jalan Romawi dibangun untuk bertahan selama ribuan tahun.", "A2", "History"),
                        vocab("r_soldier", "Soldier", "Prajurit", "A person who serves in an army", "Noun", "/ˈsəʊldʒə/", "The brave soldier carried a bronze helmet and shield.", "Prajurit pemberani membawa helm perunggu dan perisai.", "A2", "History"),
                        vocab("r_legion", "Legion", "Legiun militer", "A vast host, multitude, or division in the ancient Roman army", "Noun", "/ˈliːdʒən/", "A disciplined Roman legion marched along the paved highway.", "Legiun Romawi yang disiplin berbaris di sepanjang jalan raya beraspal.", "B2", "History"),
                        vocab("r_senator", "Senator", "Senator / Anggota dewan", "A member of a senate or government council", "Noun", "/ˈsenətə/", "The respected senator spoke eloquently before the assembly.", "Senator yang dihormati berpidato dengan fasih di depan majelis.", "B2", "History"),
                        vocab("r_citizen", "Citizen", "Warga negara", "A legally recognized subject or national of a state", "Noun", "/ˈsɪtɪzn/", "Every Roman citizen possessed legal rights and protections.", "Setiap warga negara Romawi memiliki hak dan perlindungan hukum.", "B1", "History"),
                        vocab("r_ancient", "Ancient", "Kuno / Purba", "Belonging to the very distant past and no longer in existence", "Adjective", "/ˈeɪnʃənt/", "We uncovered ancient stone inscriptions along the forum path.", "Kami menemukan prasasti batu kuno di sepanjang jalan forum.", "A2", "History"),
                        vocab("r_dynasty", "Dynasty", "Dinasti", "A line of hereditary rulers of a country", "Noun", "/ˈdɪnəsti/", "The Flavian dynasty oversaw the construction of the Colosseum.", "Dinasti Flavia mengawasi pembangunan Koloseum.", "B2", "History")
                    )
                )
            )

            // Adding Level 3-3 to 3-10
            for (lvl in 3..10) {
                val subTitle = when (lvl) {
                    3 -> "Historical Trades, Blacksmiths and Crafts"
                    4 -> "Roman City Layout, Streets and Plazas"
                    5 -> "Ancient Laws, Civic Justice and Assemblies"
                    6 -> "Classical Science, Inventions and Sundials"
                    7 -> "Mythology, Classical Drama and Epics"
                    8 -> "Imperial Banquets, Triumphs and Festivities"
                    9 -> "Mosaics, Bronze Statues and Artifacts"
                    else -> "Ancient Philosophy, Rhetoric and Wisdom"
                }
                add(
                    LevelTopicDefinition(
                        stage = 3, level = lvl, levelId = "3-$lvl",
                        topicTitle = subTitle,
                        learningObjective = "Explore $subTitle with advanced contextual vocabulary.",
                        vocabulary = generateChapter3Vocabs(lvl)
                    )
                )
            }

            // ==================== CHAPTER 4: MOUNTAIN (4-1 to 4-10) ====================
            for (lvl in 1..10) {
                val subTitle = when (lvl) {
                    1 -> "Alpine Summits, Precipices and High Peaks"
                    2 -> "Snow Phenomena, Avalanches and Blizzards"
                    3 -> "Mountaineering Gear and Alpine Tools"
                    4 -> "Alpine Flora, Pines and Mountain Fauna"
                    5 -> "High-Altitude Navigation and Bearings"
                    6 -> "Atmospheric Pressure and Mountain Winds"
                    7 -> "Alpine Rescue Expeditions and Teamwork"
                    8 -> "Geological Strata, Granite and Minerals"
                    9 -> "High-Altitude Ecosystems and Adaptations"
                    else -> "Summit Conquest, Panorama and Triumph"
                }
                add(
                    LevelTopicDefinition(
                        stage = 4, level = lvl, levelId = "4-$lvl",
                        topicTitle = subTitle,
                        learningObjective = "Master vocabulary on $subTitle.",
                        vocabulary = generateChapter4Vocabs(lvl)
                    )
                )
            }

            // ==================== CHAPTER 5: CAVE (5-1 to 5-10) ====================
            for (lvl in 1..10) {
                val subTitle = when (lvl) {
                    1 -> "Subterranean Entrances and Caverns"
                    2 -> "Speleothems, Stalactites and Mineral Columns"
                    3 -> "Underground Rivers, Sump Lakes and Aquifers"
                    4 -> "Bioluminescence and Glowing Crystals"
                    5 -> "Cave Wildlife, Bats and Subterranean Biota"
                    6 -> "Ancient Runes, Inscribed Altars and Crypts"
                    7 -> "Speleology Tools, Flares and Sonar"
                    8 -> "Gemstones, Amethysts and Crystalline Geodes"
                    9 -> "Subterranean Reverberation and Cave Acoustics"
                    else -> "The Grand Lexicon Vault and Master Victory"
                }
                add(
                    LevelTopicDefinition(
                        stage = 5, level = lvl, levelId = "5-$lvl",
                        topicTitle = subTitle,
                        learningObjective = "Master deep subterranean exploration with $subTitle.",
                        vocabulary = generateChapter5Vocabs(lvl)
                    )
                )
            }
        }
    }

    private fun vocab(
        id: String, word: String, indonesian: String, englishDef: String,
        pos: String, pron: String, exSent: String, exTrans: String, cefr: String, cat: String
    ) = VocabularyEntity(
        id = id, word = word, indonesian = indonesian, englishDef = englishDef,
        partOfSpeech = pos, pronunciation = pron, exampleSentence = exSent,
        exampleTranslation = exTrans, cefr = cefr, category = cat
    )

    private fun generateChapter3Vocabs(lvl: Int): List<VocabularyEntity> {
        val sets = listOf(
            listOf("Blacksmith", "Pandai besi", "Sculptor", "Pematung", "Builder", "Pembangun", "Merchant", "Pedagang", "Weaver", "Penenun", "Pottery", "Tembikar", "Stone", "Batu", "Chisel", "Pahat", "Workshop", "Bengkel kerja", "Trade", "Perdagangan"),
            listOf("Plaza", "Alun-alun", "Boulevard", "Jalan raya kota", "Gate", "Gerbang", "Fountain", "Air mancur", "Bathhouse", "Pemandian umum", "Stadium", "Stadion", "Wall", "Tembok", "Road", "Jalan", "Center", "Pusat", "District", "Distrik"),
            listOf("Law", "Hukum", "Justice", "Keadilan", "Rule", "Aturan", "Council", "Dewan", "Leader", "Pemimpin", "Rights", "Hak-hak", "Order", "Ketertiban", "Assembly", "Sidang majelis", "Decree", "Keputusan resmi", "Peace", "Perdamaian"),
            listOf("Invention", "Penemuan", "Lever", "Tuas", "Wheel", "Roda", "Sundial", "Jam matahari", "Pulley", "Katrol", "Scale", "Timbangan", "Measure", "Mengukur", "Gear", "Roda gigi", "Bronze", "Perunggu", "Paved", "Diaspal / Dipadatkan"),
            listOf("Myth", "Mitos", "Legend", "Legenda", "Hero", "Pahlawan", "Drama", "Drama panggung", "Theater", "Teater", "Lyre", "Kecapi", "Chorus", "Paduan suara", "Mask", "Topeng", "Poetry", "Puisi", "Symphony", "Simfoni"),
            listOf("Banquet", "Perjamuan", "Feast", "Pesta makan", "Triumph", "Pawai kemenangan", "Chariot", "Kereta perang", "Laurel", "Daun salam mahkota", "Crown", "Mahkota", "Ceremony", "Upacara", "Horn", "Trompet tanduk", "Toast", "Bersulang", "Honor", "Kehormatan"),
            listOf("Mosaic", "Mosaik", "Statue", "Patung", "Carving", "Ukiran", "Fresco", "Lukisan dinding", "Masterpiece", "Karya agung", "Bronze", "Perunggu", "Gallery", "Galeri seni", "Ornament", "Ornamen hiasan", "Canvas", "Kanvas", "Artisan", "Pengrajin terampil"),
            listOf("Wisdom", "Kebijaksanaan", "Philosophy", "Filsafat", "Logic", "Logika", "Truth", "Kebenaran", "Scholar", "Cendekiawan", "Scroll", "Gulungan naskah", "Debate", "Perdebatan", "Intellect", "Kecerdasan", "Virtue", "Kebajikan", "Knowledge", "Pengetahuan")
        )
        val data = sets[(lvl - 3).coerceIn(0, sets.size - 1)]
        return (0 until 10).map { i ->
            val w = data[i * 2]
            val indo = data[i * 2 + 1]
            vocab("r_${lvl}_$i", w, indo, "Historical concept of $w in classical civilization", "Noun", "/$w/", "The Roman $w was celebrated throughout the empire.", "$w bangsa Romawi dirayakan di seluruh kekaisaran.", "B1", "Ancient Rome")
        }
    }

    private fun generateChapter4Vocabs(lvl: Int): List<VocabularyEntity> {
        val wordBank = listOf(
            listOf("Summit", "Puncak tertinggi", "Peak", "Puncak gunung", "Cliff", "Tebing curam", "Ridge", "Punggung bukit", "Valley", "Lembah", "Glacier", "Gletser es", "Slope", "Lereng", "Canyon", "Ngarai", "Altitude", "Ketinggian", "Plateau", "Dataran tinggi"),
            listOf("Blizzard", "Badai salju", "Avalanche", "Longsor salju", "Frost", "Embun beku", "Hail", "Hujan es", "Snowdrift", "Tumpukan salju", "Chill", "Hawa dingin", "Freeze", "Membeku", "Iceberg", "Gunung es", "Fog", "Kabut tebal", "Thaw", "Mencair"),
            listOf("Rope", "Tali panjat", "Carabiner", "Cincin kait", "Harness", "Tali pengaman tubuh", "Crampons", "Sepatu duri es", "Tent", "Tenda gunung", "Thermos", "Termos air panas", "Flare", "Suar darurat", "Whistle", "Peluit sinyal", "Goggles", "Kacamata salju", "Knapsack", "Ransel punggung"),
            listOf("Pine", "Pohon pinus", "Fir", "Pohon cemara", "Edelweiss", "Bunga edelweiss", "Cedar", "Pohon aras", "Falcon", "Burung elang", "Ibex", "Kambing gunung", "Leopard", "Macan tutul salju", "Marmot", "Marmot gunung", "Lichen", "Lumut kerak", "Tundra", "Tundra beku"),
            listOf("North", "Utara", "South", "Selatan", "East", "Timur", "West", "Barat", "Elevation", "Elevasi", "Latitude", "Garis lintang", "Horizon", "Cakrawala", "Meridian", "Garis bujur", "Zenith", "Titik zenit", "Bearing", "Arah kompas"),
            listOf("Barometer", "Barometer", "Humidity", "Kelembapan", "Atmosphere", "Atmosfer", "Gale", "Angin kencang", "Gust", "Hembusan angin tiba-tiba", "Vapor", "Uap air", "Forecast", "Prakiraan cuaca", "Cyclone", "Siklon", "Condensation", "Kondensasi", "Front", "Garis cuaca"),
            listOf("Rescue", "Penyelamatan", "Teamwork", "Kerja sama tim", "Shelter", "Tempat berlindung", "Signal", "Sinyal darurat", "Courage", "Keberanian", "FirstAid", "Pertolongan pertama", "Stretcher", "Tandu darurat", "Radio", "Radio komunikasi", "Survival", "Kelangsungan hidup", "Cooperation", "Kerja sama"),
            listOf("Granite", "Batu granit", "Basalt", "Batu basal", "Limestone", "Batu kapur", "Quartz", "Kuarsa", "Mineral", "Mineral batuan", "Strata", "Lapisan batuan", "Erosion", "Erosi", "Fault", "Patahan bumi", "Magma", "Magma", "Fossil", "Fosil"),
            listOf("Adaptation", "Adaptasi", "Habitat", "Habitat alami", "Ecosystem", "Ekosistem", "Endurance", "Daya tahan", "Rare", "Langka", "Harsh", "Keras / Ekstrem", "Preserve", "Melestarikan", "Flora", "Tumbuhan flora", "Fauna", "Hewan fauna", "Biomass", "Biomassa"),
            listOf("Panorama", "Pemandangan menyeluruh", "Vista", "Pemandangan indah", "Triumph", "Kemenangan", "Conquest", "Penaklukan puncak", "Horizon", "Kaki langit", "Skyline", "Garis cakrawala", "Achievement", "Prestasi gemilang", "Milestone", "Tonggak pencapaian", "Expedition", "Ekspedisi", "Legacy", "Warisan abadi")
        )
        val data = wordBank[(lvl - 1).coerceIn(0, wordBank.size - 1)]
        return (0 until 10).map { i ->
            val w = data[i * 2]
            val indo = data[i * 2 + 1]
            vocab("m_${lvl}_$i", w, indo, "High-altitude alpine term: $w", "Noun", "/$w/", "The alpine explorer mastered the concept of $w.", "Penjelajah alpine menguasai konsep $w.", "B1", "Mountain")
        }
    }

    private fun generateChapter5Vocabs(lvl: Int): List<VocabularyEntity> {
        val wordBank = listOf(
            listOf("Cavern", "Gua raksasa", "Grotto", "Gua kecil berbatu", "Threshold", "Ambang pintu masuk", "Descent", "Penurunan ke bawah", "Abyss", "Jurang tanpa dasar", "Tunnel", "Terowongan", "Chamber", "Ruang bawah tanah", "Hollow", "Rongga", "Chasm", "Celah curam", "Crevice", "Rekahan batu"),
            listOf("Stalactite", "Stalaktit atas", "Stalagmite", "Stalagmit bawah", "Column", "Pilar batu kapur", "Dripstone", "Batu tetes", "Calcite", "Kalsit", "Limestone", "Batu gamping", "Vein", "Urat mineral", "Cluster", "Gugusan kristal", "Droplet", "Tetesan air", "Pillar", "Tiang kapur"),
            listOf("Aquifer", "Lapisan akuifer", "Subterranean", "Bawah tanah", "Waterfall", "Air terjun gua", "Spring", "Mata air alami", "Sump", "Genangan air dalam", "Current", "Arus air", "Moisture", "Kelembapan", "Reservoir", "Waduk alami", "Basin", "Cekungan air", "Torrent", "Arus deras"),
            listOf("Luminescence", "Pendaran cahaya", "Glow", "Kilau menyala", "Phosphor", "Zat fosfor", "Fluorescence", "Fluoresensi", "Radiance", "Kemilau terang", "Glimmer", "Kelap-kelip", "Iridescent", "Berkilau aneka warna", "Beam", "Sorotan sinar", "Luster", "Kilau kristal", "Sparkle", "Percikan kilau"),
            listOf("Bat", "Kelelawar", "Cricket", "Jangkrik gua", "Blindfish", "Ikan buta gua", "Salamander", "Salamander gua", "Fungi", "Jamur spora", "Spore", "Spora", "Organism", "Organisme hidup", "Nocturnal", "Aktif di malam hari", "Colony", "Koloni", "Predator", "Pemangsa"),
            listOf("Hieroglyph", "Hieroglif", "Rune", "Huruf rune kuno", "Inscription", "Prasasti berukir", "Crypt", "Ruang rahasia bawah tanah", "Relic", "Relikui pusaka", "Artifact", "Artefak bersejarah", "Altar", "Altar batu", "Tablet", "Lempengan batu tulis", "Papyrus", "Naskah papirus", "Symbol", "Simbol sakral"),
            listOf("Headlamp", "Lampu kepala", "Flare", "Suar terang", "Lantern", "Lentera", "Sonar", "Sensor sonar", "Battery", "Baterai cadangan", "Piton", "Paku panjat tebing", "Tether", "Tali pengaman", "Sensor", "Sensor gerak", "Mapping", "Pemetaan gua", "Compass", "Kompas kedalaman"),
            listOf("Amethyst", "Batu kecubung", "Emerald", "Batu zamrud", "Sapphire", "Batu safir", "Ruby", "Batu mirah delima", "Diamond", "Intan berlian", "Geode", "Batuan geoda berongga", "Prism", "Prisma pemantul", "Facet", "Sisi potongan permata", "Lode", "Lidah tambang emas", "Refraction", "Pembiasan cahaya"),
            listOf("Echo", "Gema suara", "Resonance", "Resonansi nada", "Soundwave", "Gelombang suara", "Reverberation", "Dengungan suara", "Vibration", "Getaran", "Murmur", "Bisikan pelan", "Whisper", "Bisikan", "Frequency", "Frekuensi", "Amplitude", "Amplitudo", "Tone", "Nada suara"),
            listOf("Lexicon", "Gudang leksikon", "Mastery", "Penguasaan sempurna", "Wisdom", "Kearifan luhur", "Sanctuary", "Tempat perlindungan suci", "Treasure", "Harta karun ilmu", "Victory", "Kemenangan mutlak", "Crown", "Mahkota penjelajah", "Explorer", "Penjelajah sejati", "Eternity", "Keabadian ilmu", "Legacy", "Warisan pengetahuan")
        )
        val data = wordBank[(lvl - 1).coerceIn(0, wordBank.size - 1)]
        return (0 until 10).map { i ->
            val w = data[i * 2]
            val indo = data[i * 2 + 1]
            vocab("c_${lvl}_$i", w, indo, "Subterranean cavern concept: $w", "Noun", "/$w/", "The mysterious crystal cavern revealed the $w.", "Gua kristal misterius mengungkapkan $w.", "B2", "Cave")
        }
    }

    fun getTopicForLevel(stage: Int, level: Int): LevelTopicDefinition {
        return TOPICS.find { it.stage == stage && it.level == level }
            ?: TOPICS.first()
    }

    // Generate exactly 10 high-variety questions for this specific level
    // utilizing all 8 formats:
    // 1. Drag and Match
    // 2. Fill in the Blanks
    // 3. Word Arrangement (Unscramble)
    // 4. Image-based Vocabulary
    // 5. Sentence Completion
    // 6. Word-to-Meaning Matching
    // 7. Contextual Dialogue
    // 8. Pronunciation Practice / Audio Match
    fun generateTenQuestionsForLevel(path: LearningPath, stage: Int, level: Int): List<Question> {
        val topic = getTopicForLevel(stage, level)
        val vocabList = topic.vocabulary
        val questions = mutableListOf<Question>()

        for (qIdx in 1..10) {
            val targetVocab = vocabList[(qIdx - 1) % vocabList.size]
            val otherVocabs = vocabList.filter { it.id != targetVocab.id }.shuffled()
            val wrong1 = otherVocabs.getOrNull(0)?.word ?: "Compass"
            val wrong2 = otherVocabs.getOrNull(1)?.word ?: "Bridge"
            val wrong3 = otherVocabs.getOrNull(2)?.word ?: "Trail"

            val qId = "q_${path.code}_s${stage}_l${level}_$qIdx"

            val question = when (qIdx) {
                // 1: Drag & Match (3 matching pairs)
                1 -> {
                    val p1 = targetVocab
                    val p2 = otherVocabs.getOrElse(0) { targetVocab }
                    val p3 = otherVocabs.getOrElse(1) { targetVocab }
                    Question(
                        id = qId,
                        type = QuestionType.DRAG_AND_MATCH,
                        prompt = "Tarik dan Cocokkan Kosakata Topik ${topic.topicTitle}:",
                        subText = "Pasangkan kata bahasa Inggris dengan arti bahasa Indonesia yang benar.",
                        correctAnswer = targetVocab.indonesian,
                        explanation = "${targetVocab.word} = ${targetVocab.indonesian}. Pengucapan: ${targetVocab.pronunciation}",
                        targetWord = targetVocab.word,
                        matchingPairs = mapOf(
                            p1.word to p1.indonesian,
                            p2.word to p2.indonesian,
                            p3.word to p3.indonesian
                        )
                    )
                }

                // 2: Fill in the Blanks
                2 -> {
                    val sentenceWithBlank = targetVocab.exampleSentence.replace(targetVocab.word, "_______", ignoreCase = true)
                    val hint = when (path) {
                        LearningPath.LOW -> "Petunjuk: ${targetVocab.indonesian} (${targetVocab.partOfSpeech})"
                        LearningPath.MIDDLE -> "Hint: ${targetVocab.englishDef}"
                        LearningPath.HIGH -> "Context Clue: Part of speech is ${targetVocab.partOfSpeech}"
                    }
                    Question(
                        id = qId,
                        type = QuestionType.FILL_BLANK,
                        prompt = "Ketik Kosakata yang Tepat untuk Melengkapi Kalimat:",
                        subText = "\"$sentenceWithBlank\"\n$hint",
                        correctAnswer = targetVocab.word,
                        explanation = "Jawaban yang benar adalah '${targetVocab.word}'. Terjemahan: ${targetVocab.exampleTranslation}",
                        targetWord = targetVocab.word,
                        sentenceWithBlank = sentenceWithBlank
                    )
                }

                // 3: Word Arrangement / Unscramble
                3 -> {
                    val scrambled = targetVocab.word.uppercase().map { it.toString() }.shuffled()
                    Question(
                        id = qId,
                        type = QuestionType.WORD_UNSCRAMBLE,
                        prompt = "Susun Huruf Acak Menjadi Kata yang Benar:",
                        subText = "Topik: ${topic.topicTitle} • Arti: \"${targetVocab.indonesian}\"",
                        correctAnswer = targetVocab.word.uppercase(),
                        explanation = "Susunan yang benar adalah '${targetVocab.word}'. Contoh: ${targetVocab.exampleSentence}",
                        targetWord = targetVocab.word,
                        scrambledLetters = scrambled
                    )
                }

                // 4: Image-Based Vocabulary
                4 -> {
                    val emoji = InitialDataSeed.getEmojiForWord(targetVocab.word)
                    val opts = listOf(targetVocab.word, wrong1, wrong2, wrong3).shuffled()
                    Question(
                        id = qId,
                        type = QuestionType.IMAGE_VOCABULARY,
                        prompt = "Simbol visual ini melambangkan kosakata apa?",
                        subText = "Definisi: ${targetVocab.englishDef} (Arti: ${targetVocab.indonesian})",
                        options = opts,
                        correctAnswer = targetVocab.word,
                        explanation = "${targetVocab.word} (${targetVocab.indonesian}): ${targetVocab.englishDef}",
                        targetWord = targetVocab.word,
                        iconEmoji = emoji
                    )
                }

                // 5: Sentence Completion
                5 -> {
                    val sentenceWithBlank = targetVocab.exampleSentence.replace(targetVocab.word, "_______", ignoreCase = true)
                    val opts = listOf(targetVocab.word, wrong1, wrong2, wrong3).shuffled()
                    Question(
                        id = qId,
                        type = QuestionType.SENTENCE_COMPLETION,
                        prompt = "Pilih kata yang paling sesuai untuk menyempurnakan kalimat:",
                        subText = "\"$sentenceWithBlank\"\nTerjemahan: (${targetVocab.exampleTranslation})",
                        options = opts,
                        correctAnswer = targetVocab.word,
                        explanation = "'${targetVocab.word}' tepat melengkapi kalimat. ${targetVocab.englishDef}",
                        targetWord = targetVocab.word,
                        sentenceWithBlank = sentenceWithBlank
                    )
                }

                // 6: Word-to-Meaning Matching
                6 -> {
                    val wrongIndo1 = otherVocabs.getOrNull(0)?.indonesian ?: "Jembatan"
                    val wrongIndo2 = otherVocabs.getOrNull(1)?.indonesian ?: "Hutan"
                    val wrongIndo3 = otherVocabs.getOrNull(2)?.indonesian ?: "Pulau"
                    val opts = listOf(targetVocab.indonesian, wrongIndo1, wrongIndo2, wrongIndo3).shuffled()
                    Question(
                        id = qId,
                        type = QuestionType.MULTIPLE_CHOICE,
                        prompt = "Apa arti kata '${targetVocab.word}' dalam bahasa Indonesia?",
                        subText = "Konteks topik: ${topic.topicTitle} (${targetVocab.partOfSpeech})",
                        options = opts,
                        correctAnswer = targetVocab.indonesian,
                        explanation = "'${targetVocab.word}' berarti '${targetVocab.indonesian}'. Pelafalan: ${targetVocab.pronunciation}",
                        targetWord = targetVocab.word
                    )
                }

                // 7: Contextual Dialogue / Situational Question
                7 -> {
                    val speaker = "Explorer Arka"
                    val dialoguePrompt = "Situasi di ${topic.topicTitle}:\n$speaker: \"We need to be mindful of the _______ along our trail.\""
                    val sentenceWithBlank = dialoguePrompt.replace(targetVocab.word, "_______", ignoreCase = true)
                    val opts = listOf(targetVocab.word, wrong1, wrong2, wrong3).shuffled()
                    Question(
                        id = qId,
                        type = QuestionType.MULTIPLE_CHOICE,
                        prompt = "Lengkapi percakapan petualang dengan kata yang tepat:",
                        subText = sentenceWithBlank,
                        options = opts,
                        correctAnswer = targetVocab.word,
                        explanation = "Kata '${targetVocab.word}' sesuai dengan situasi dialog. Arti: ${targetVocab.indonesian}",
                        targetWord = targetVocab.word
                    )
                }

                // 8: Pronunciation & Audio Practice Question
                8 -> {
                    val opts = listOf(targetVocab.word, wrong1, wrong2, wrong3).shuffled()
                    Question(
                        id = qId,
                        type = QuestionType.MULTIPLE_CHOICE,
                        prompt = "Dengarkan/Perhatikan Fonetik: ${targetVocab.pronunciation}",
                        subText = "Kata manakah yang dilafalkan dengan transkripsi fonetik di atas?",
                        options = opts,
                        correctAnswer = targetVocab.word,
                        explanation = "Fonetik ${targetVocab.pronunciation} adalah cara membaca '${targetVocab.word}' (${targetVocab.indonesian}).",
                        targetWord = targetVocab.word
                    )
                }

                // 9: Fill in Blank with Contextual Gloss
                9 -> {
                    val sentenceWithBlank = targetVocab.exampleSentence.replace(targetVocab.word, "_______", ignoreCase = true)
                    Question(
                        id = qId,
                        type = QuestionType.FILL_BLANK,
                        prompt = "Lengkapi kalimat berikut dengan kosakata '${topic.topicTitle}':",
                        subText = "\"$sentenceWithBlank\"\nPetunjuk: '${targetVocab.indonesian}'",
                        correctAnswer = targetVocab.word,
                        explanation = "Kata yang tepat adalah '${targetVocab.word}' (${targetVocab.indonesian}).",
                        targetWord = targetVocab.word,
                        sentenceWithBlank = sentenceWithBlank
                    )
                }

                // 10: Master Synthesizing Question
                else -> {
                    val scrambled = targetVocab.word.uppercase().map { it.toString() }.shuffled()
                    Question(
                        id = qId,
                        type = QuestionType.WORD_UNSCRAMBLE,
                        prompt = "Tantangan Puncak Level: Susun kata kunci '${targetVocab.indonesian}':",
                        subText = "Definisi: ${targetVocab.englishDef} • Pelafalan: ${targetVocab.pronunciation}",
                        correctAnswer = targetVocab.word.uppercase(),
                        explanation = "Luar biasa! '${targetVocab.word}' mengunci kemenangan level ini!",
                        targetWord = targetVocab.word,
                        scrambledLetters = scrambled
                    )
                }
            }
            questions.add(question)
        }

        return questions
    }
}
