package com.example.data.seed

import com.example.data.local.VocabularyEntity
import com.example.data.models.LearningPath
import com.example.data.models.PictureOption
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
                    topicTitle = "Forest Entrance and Navigation",
                    learningObjective = "Master foundational wilderness navigation tools and forest orientation.",
                    vocabulary = listOf(
                        vocab("f_1_compass", "Compass", "Kompas", "A navigational instrument with a magnetic needle pointing north", "Noun", "/ˈkʌmpəs/", "The explorer checked his brass compass to find the north trail.", "Penjelajah itu memeriksa kompas kuningan miliknya untuk menemukan jalur utara.", "A1", "Navigation"),
                        vocab("f_1_forest", "Forest", "Hutan", "A dense area covered with trees, moss, and undergrowth", "Noun", "/ˈfɒrɪst/", "A vast green forest stretches across the island hills.", "Hutan hijau luas membentang di perbukitan pulau.", "A1", "Nature"),
                        vocab("f_1_path", "Path", "Jalan setapak", "A beaten track along which hikers and explorers travel", "Noun", "/pɑːθ/", "Follow the stone path through the overgrown woods.", "Ikuti jalan setapak batu melewati hutan yang lebat.", "A1", "Navigation"),
                        vocab("f_1_tree", "Tree", "Pohon", "A tall woody perennial plant with a trunk and branches", "Noun", "/triː/", "Tall cedar trees shade the cool woodland clearing.", "Pohon-pohon aras tinggi menaungi tempat terbuka yang sejuk di hutan.", "A1", "Nature"),
                        vocab("f_1_map", "Map", "Peta", "A diagrammatic representation of terrain, rivers, and trails", "Noun", "/mæp/", "He unfolded an ancient parchment map of the valley.", "Dia membuka lipatan peta perkamen kuno tentang lembah itu.", "A1", "Navigation"),
                        // Distractor & Enrichment Words
                        vocab("f_1_trail", "Trail", "Rute jalur", "A marked wilderness route for hiking expeditions", "Noun", "/treɪl/", "Mark the trail with wooden signs.", "Tandai rute jalur dengan papan kayu.", "A1", "Navigation"),
                        vocab("f_1_moss", "Moss", "Lumut", "Small green flowerless plants growing in dense mats on rocks", "Noun", "/mɒs/", "Green moss covered the ancient stepping stones.", "Lumut hijau menutupi batu-batu pijakan kuno.", "A1", "Nature"),
                        vocab("f_1_woods", "Woods", "Belantara", "An area of land covered with growing trees", "Noun", "/wʊdz/", "Sunlight filtered softly through the quiet woods.", "Sinar matahari menyaring lembut melewati belantara yang tenang.", "A1", "Nature"),
                        vocab("f_1_branch", "Branch", "Dahan", "A part of a tree which grows out from the trunk", "Noun", "/brɑːntʃ/", "A bird called out from a leafy tree branch.", "Seekor burung bersiul dari dahan pohon yang rimbun.", "A1", "Nature"),
                        vocab("f_1_guide", "Guide", "Pemandu", "A person who shows the way on an expedition", "Noun", "/ɡaɪd/", "Our experienced guide pointed to the secret trail.", "Pemandu berpengalaman kami menunjuk ke arah rute rahasia.", "A1", "Navigation")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 2, levelId = "1-2",
                    topicTitle = "Expedition Campsite and Nightfall",
                    learningObjective = "Identify wilderness shelters, illumination tools, and campfire essentials.",
                    vocabulary = listOf(
                        vocab("f_2_camp", "Camp", "Perkemahan", "A temporary outdoor lodging place with tents and fires", "Noun", "/kæmp/", "We set up our expedition camp before twilight.", "Kami mendirikan perkemahan ekspedisi sebelum senja tiba.", "A1", "Camp"),
                        vocab("f_2_tent", "Tent", "Tenda", "A portable shelter made of canvas or nylon fabric", "Noun", "/tent/", "Secure the green tent against the night wind.", "Kencangkan tenda hijau dari hembusan angin malam.", "A1", "Camp"),
                        vocab("f_2_torch", "Torch", "Obor", "A portable light source burning with a bright flame", "Noun", "/tɔːtʃ/", "Light the pitch torch to illuminate the dark trail.", "Nyalakan obor minyak untuk menerangi jalan setapak yang gelap.", "A1", "Equipment"),
                        vocab("f_2_lantern", "Lantern", "Lentera", "A lamp with a protective casing around a warm glow", "Noun", "/ˈlæntən/", "Hang the brass lantern on the sturdy branch.", "Gantung lentera kuningan di dahan yang kokoh.", "A1", "Equipment"),
                        vocab("f_2_fire", "Fire", "Api unggun", "Flames and embers providing heat and light at camp", "Noun", "/ˈfaɪə/", "A crackling camp fire keeps wild predators away.", "Api unggun yang menyala menjauhkan pemangsa liar.", "A1", "Survival"),
                        // Distractor & Enrichment Words
                        vocab("f_2_shelter", "Shelter", "Tempat berlindung", "A structure providing protection from weather", "Noun", "/ˈʃeltə/", "Build a dry shelter under the rocky ledge.", "Bangun tempat berlindung kering di bawah tebing batu.", "A1", "Survival"),
                        vocab("f_2_timber", "Timber", "Kayu bakar", "Wood prepared for building or fuel", "Noun", "/ˈtɪmbə/", "Gather dry timber for the evening blaze.", "Kumpulkan kayu bakar kering untuk api malam hari.", "A1", "Camp"),
                        vocab("f_2_smoke", "Smoke", "Asap", "Visible vapor rising from a burning fire", "Noun", "/sməʊk/", "A thin plume of smoke rose above the treetops.", "Gumpalan tipis asap membubung di atas puncak pohon.", "A1", "Camp"),
                        vocab("f_2_sparks", "Sparks", "Percikan api", "Small fiery glowing particles thrown off from fire", "Noun", "/spɑːks/", "Bright sparks danced upward into the starry sky.", "Percikan api terang menari ke atas menuju langit berbintang.", "A1", "Camp"),
                        vocab("f_2_rest", "Rest", "Beristirahat", "To relax and recover strength after a long trek", "Verb", "/rest/", "Tired explorers rest quietly inside their sleeping bags.", "Penjelajah yang lelah beristirahat tenang di dalam kantung tidur.", "A1", "Survival")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 3, levelId = "1-3",
                    topicTitle = "Trail Gear and Explorer Tools",
                    learningObjective = "Recognize essential trail equipment, cords, and field tools.",
                    vocabulary = listOf(
                        vocab("f_3_backpack", "Backpack", "Tas ransel", "A sturdy bag with shoulder straps carried on the back", "Noun", "/ˈbækpæk/", "Pack your field notebook and rations inside the backpack.", "Masukkan buku catatan lapangan dan bekalmu ke dalam tas ransel.", "A1", "Equipment"),
                        vocab("f_3_rope", "Rope", "Tali tambang", "A strong thick cord used for climbing and securing loads", "Noun", "/rəʊp/", "Coil the braided nylon rope before descending the slope.", "Gulung tali nilon berkepang sebelum menuruni lereng.", "A1", "Equipment"),
                        vocab("f_3_boots", "Boots", "Sepatu bot mendaki", "Sturdy footwear covering the foot and ankle for rugged trails", "Noun", "/buːts/", "Waterproof leather boots protect your feet from sharp stones.", "Sepatu bot kulit kedap air melindungi kakimu dari batu tajam.", "A1", "Equipment"),
                        vocab("f_3_flask", "Flask", "Botol air minum", "A container used to hold fresh drinking water on expeditions", "Noun", "/flɑːsk/", "Fill your metal flask at the pure spring.", "Isi botol air logammu di mata air yang murni.", "A1", "Equipment"),
                        vocab("f_3_knife", "Knife", "Pisau saku lapangan", "A cutting tool with a sharp blade used for survival", "Noun", "/naɪf/", "Use the pocket knife to cut vines and mend gear.", "Gunakan pisau saku untuk memotong sulur dan memperbaiki perlengkapan.", "A1", "Equipment"),
                        // Distractor & Enrichment Words
                        vocab("f_3_strap", "Strap", "Tali pengikat", "A strip of leather or webbing used to fasten items", "Noun", "/stræp/", "Tighten the shoulder strap of your pack.", "Kencangkan tali pengikat bahu pada ranselmu.", "A1", "Equipment"),
                        vocab("f_3_buckle", "Buckle", "Gesper pengunci", "A clasp used for fastening two ends of a belt or strap", "Noun", "/ˈbʌkl/", "Snap the brass buckle tightly in place.", "Kancingkan gesper kuningan dengan erat pada tempatnya.", "A1", "Equipment"),
                        vocab("f_3_pouch", "Pouch", "Kantung kecil", "A small flexible bag for carrying essential trinkets", "Noun", "/paʊtʃ/", "Keep compass needles inside the leather pouch.", "Simpan jarum kompas di dalam kantung kulit.", "A1", "Equipment"),
                        vocab("f_3_whistle", "Whistle", "Peluit sinyal", "A small device blown to emit loud rescue alerts", "Noun", "/ˈwɪsl/", "Blow the rescue whistle three times if lost.", "Tiup peluit sinyal penyelamat tiga kali jika tersesat.", "A1", "Equipment"),
                        vocab("f_3_gear", "Gear", "Perlengkapan", "The equipment and apparatus required for an expedition", "Noun", "/ɡɪə/", "Double check all survival gear before departing.", "Periksa kembali semua perlengkapan bertahan hidup sebelum berangkat.", "A1", "Equipment")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 4, levelId = "1-4",
                    topicTitle = "River Crossing and Bridges",
                    learningObjective = "Describe waterways, river rapids, bridges, and safe bank crossings.",
                    vocabulary = listOf(
                        vocab("f_4_river", "River", "Sungai", "A large natural stream of fresh water flowing to the sea", "Noun", "/ˈrɪvə/", "The azure river rushes through the tropical forest.", "Sungai biru cerah mengalir deras melewati hutan tropis.", "A1", "Geography"),
                        vocab("f_4_bridge", "Bridge", "Jembatan", "A structure built over water enabling hikers to cross safely", "Noun", "/brɪdʒ/", "Cross the wooden suspension bridge one hiker at a time.", "Seberangi jembatan gantung kayu satu penjelajah setiap kali.", "A1", "Landmarks"),
                        vocab("f_4_stream", "Stream", "Anak sungai", "A small, narrow river of clear running water", "Noun", "/striːm/", "Clear fresh water gurgles in the mountain stream.", "Air tawar jernih berdeguk di anak sungai pegunungan.", "A1", "Geography"),
                        vocab("f_4_crossing", "Crossing", "Penyeberangan", "A place where a river or ravine may be traversed", "Noun", "/ˈkrɒsɪŋ/", "The guide scouted a shallow rock crossing.", "Pemandu mencari tempat penyeberangan batu yang dangkal.", "A1", "Travel"),
                        vocab("f_4_current", "Current", "Arus air", "The flow and force of moving river water", "Noun", "/ˈkʌrənt/", "Do not step into the swift river current.", "Jangan melangkah ke dalam arus air sungai yang deras.", "A2", "Nature"),
                        // Distractor & Enrichment Words
                        vocab("f_4_bank", "Bank", "Tepi sungai", "The rising ground bordering a river or stream", "Noun", "/bæŋk/", "Pitch the tents high on the grassy river bank.", "Dirikan tenda tinggi di tepi sungai yang berumput.", "A1", "Geography"),
                        vocab("f_4_plank", "Plank", "Papan kayu", "A long thin flat piece of timber used in bridges", "Noun", "/plæŋk/", "Step carefully on each sturdy bridge plank.", "Melangkahlah hati-hati pada setiap papan kayu jembatan yang kokoh.", "A1", "Landmarks"),
                        vocab("f_4_waterfall", "Waterfall", "Air terjun", "A cascade of water falling from a height over rocks", "Noun", "/ˈwɔːtəfɔːl/", "The thunder of a giant waterfall echoed ahead.", "Gemuruh air terjun raksasa bergema di depan.", "A1", "Nature"),
                        vocab("f_4_pebble", "Pebble", "Batu kerikil", "A small smooth rounded stone found in riverbeds", "Noun", "/ˈpebl/", "Smooth pebbles lined the shallow riverbed.", "Batu-batu kerikil halus melapisi dasar sungai yang dangkal.", "A1", "Nature"),
                        vocab("f_4_ford", "Ford", "Bagian sungai dangkal", "A shallow place in a river where crossing is safe", "Noun", "/fɔːd/", "The team forded the river where the gravel was firm.", "Tim menyeberangi bagian sungai yang dangkal di mana kerikilnya padat.", "B1", "Travel")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 5, levelId = "1-5",
                    topicTitle = "Forest Canopy and Plant Life",
                    learningObjective = "Identify rainforest vegetation, canopy layers, and forest biodiversity.",
                    vocabulary = listOf(
                        vocab("f_5_canopy", "Canopy", "Kanopi hutan", "The high continuous layer of foliage formed by tree crowns", "Noun", "/ˈkænəpi/", "Sunlight barely penetrates the dense rainforest canopy.", "Sinar matahari hampir tak menembus kanopi hutan hujan yang lebat.", "A2", "Nature"),
                        vocab("f_5_vine", "Vine", "Sulur rambat", "A climbing or trailing woody-stemmed plant", "Noun", "/vaɪn/", "Thick hanging vines draped across the ancient ruins.", "Sulur-sulur rambat tebal bergelantungan di atas reruntuhan kuno.", "A1", "Nature"),
                        vocab("f_5_fern", "Fern", "Tanaman paku / Pakis", "A flowerless plant with feathery green fronds", "Noun", "/fɜːn/", "Giant emerald ferns carpet the moist woodland floor.", "Pakis zamrud raksasa menyelimuti lantai hutan yang lembap.", "A1", "Nature"),
                        vocab("f_5_root", "Root", "Akar pohon", "The underground or aerial anchoring base of a tree", "Noun", "/ruːt/", "Step over the massive gnarled tree roots.", "Melangkahlah melewati akar-akar pohon besar yang berliku.", "A1", "Nature"),
                        vocab("f_5_leaf", "Leaf", "Daun", "A flattened green structure of a plant attached to a stem", "Noun", "/liːf/", "Broad tropical leaves collect morning raindrops.", "Daun-daun tropis yang lebar menampung tetesan air hujan pagi.", "A1", "Nature"),
                        // Distractor & Enrichment Words
                        vocab("f_5_trunk", "Trunk", "Batang pohon", "The main woody stem of a tree", "Noun", "/trʌŋk/", "The moss-covered trunk stood for centuries.", "Batang pohon yang berlumut itu telah berdiri berabad-abad.", "A1", "Nature"),
                        vocab("f_5_bark", "Bark", "Kulit pohon", "The tough protective outer sheath of tree trunks", "Noun", "/bɑːk/", "Birch trees have papery white protective bark.", "Pohon birch memiliki kulit pelindung putih seperti kertas.", "A2", "Nature"),
                        vocab("f_5_seed", "Seed", "Biji", "The unit of reproduction of a flowering plant", "Noun", "/siːd/", "Birds scattered wild berry seeds along the trail.", "Burung-burung menyebarkan biji buah beri liar di sepanjang jalan.", "A1", "Nature"),
                        vocab("f_5_sprout", "Sprout", "Tunas baru", "A young shoot emerging from a plant or seed", "Noun", "/spraʊt/", "Green sprouts pushed up through the rich soil.", "Tunas-tunas hijau menyembul dari tanah yang subur.", "A2", "Nature"),
                        vocab("f_5_jungle", "Jungle", "Rimba tropis", "An area of land overgrown with dense forest and tangled vegetation", "Noun", "/ˈdʒʌŋɡl/", "Our expedition plunged into the deep wild jungle.", "Ekspedisi kita memasuki rimba tropis liar yang dalam.", "A1", "Nature")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 6, levelId = "1-6",
                    topicTitle = "Trailhead Bearings and Cardinal Directions",
                    learningObjective = "Master orientation bearings, cardinal points, and heading alignments.",
                    vocabulary = listOf(
                        vocab("f_6_north", "North", "Utara", "The cardinal direction pointing toward the North Pole", "Noun", "/nɔːθ/", "The needle of our compass pointed straight north.", "Jarum kompas kami menunjuk tepat ke arah utara.", "A1", "Navigation"),
                        vocab("f_6_south", "South", "Selatan", "The direction directly opposite to the north", "Noun", "/saʊθ/", "Migrating hawks flew toward the warm south.", "Burung elang yang bermigrasi terbang ke arah selatan yang hangat.", "A1", "Navigation"),
                        vocab("f_6_east", "East", "Timur", "The direction toward the point of sunrise", "Noun", "/iːst/", "The golden morning sun rises in the east.", "Matahari pagi keemasan terbit di sebelah timur.", "A1", "Navigation"),
                        vocab("f_6_west", "West", "Barat", "The direction toward the point of sunset", "Noun", "/west/", "Travel west to reach the sandy coastal cove.", "Berjalanlah ke arah barat untuk mencapai teluk pesisir berpasir.", "A1", "Navigation"),
                        vocab("f_6_route", "Route", "Rute perjalanan", "A course taken in getting from a starting point to a goal", "Noun", "/ruːt/", "Chart the safest climbing route through the ridges.", "Petakan rute pendakian paling aman melewati punggung bukit.", "A2", "Navigation"),
                        // Distractor & Enrichment Words
                        vocab("f_6_heading", "Heading", "Arah haluan", "The direction in which a traveler or vessel is moving", "Noun", "/ˈhedɪŋ/", "Keep your heading steady along the creek.", "Jaga arah haluanmu tetap stabil di sepanjang sungai kecil.", "B1", "Navigation"),
                        vocab("f_6_bearing", "Bearing", "Sudut bantalan", "The angular direction of a landmark measured by compass", "Noun", "/ˈbeərɪŋ/", "Take a bearing on the high mountain peak.", "Ambil sudut bantalan kompas pada puncak gunung yang tinggi.", "B1", "Navigation"),
                        vocab("f_6_landmark", "Landmark", "Tengara tanda darat", "A prominent recognizable natural or built feature", "Noun", "/ˈlændmɑːk/", "The giant cedar tree is our primary trail landmark.", "Pohon aras raksasa adalah tengara utama jalan setapak kita.", "A2", "Navigation"),
                        vocab("f_6_horizon", "Horizon", "Cakrawala", "The line where the earth's surface and the sky appear to meet", "Noun", "/həˈraɪzn/", "The distant volcanic peaks met the blue horizon.", "Puncak gunung berapi yang jauh bertemu dengan cakrawala biru.", "A2", "Geography"),
                        vocab("f_6_summit_dir", "Summit", "Puncak sasaran", "The highest point on the expedition's path", "Noun", "/ˈsʌmɪt/", "Our ultimate destination is the snowy mountain summit.", "Tujuan akhir kita adalah puncak gunung yang bersalju.", "A2", "Geography")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 7, levelId = "1-7",
                    topicTitle = "Trail Rations and Wilderness Hydration",
                    learningObjective = "Identify survival provisions, drinking water sources, and energy rations.",
                    vocabulary = listOf(
                        vocab("f_7_water", "Water", "Air bersih", "Pure liquid essential for hydration on wilderness treks", "Noun", "/ˈwɔːtə/", "Carry at least two liters of fresh drinking water.", "Bawalah setidaknya dua liter air minum bersih.", "A1", "Survival"),
                        vocab("f_7_rations", "Rations", "Perbekalan bekal", "A fixed portion of food allotted for an expedition day", "Noun", "/ˈræʃnz/", "Dry fruit and nuts make lightweight trail rations.", "Buah kering dan kacang adalah perbekalan bekal yang ringan.", "A2", "Survival"),
                        vocab("f_7_canteen", "Canteen", "Tempat air minum", "A portable flask for carrying water used by hikers", "Noun", "/kænˈtiːn/", "Clip the insulated canteen securely to your belt.", "Kaitkan tempat air minum berinsulasi dengan aman ke sabukmu.", "A2", "Equipment"),
                        vocab("f_7_provisions", "Provisions", "Persediaan pangan", "Supplies of food, drink, and equipment for a voyage", "Noun", "/prəˈvɪʒnz/", "The explorers carefully rationed their food provisions.", "Para penjelajah dengan hemat membagi persediaan pangan mereka.", "B1", "Survival"),
                        vocab("f_7_energy", "Energy", "Energi daya", "Strength and vitality required for sustained exertion", "Noun", "/ˈenədʒi/", "Nutritious trail mix provides sustained walking energy.", "Camilan bernutrisi memberikan energi berjalan yang tahan lama.", "A1", "Health"),
                        // Distractor & Enrichment Words
                        vocab("f_7_spring", "Spring", "Mata air alami", "A natural flow of fresh ground water from the earth", "Noun", "/sprɪŋ/", "We refilled our bottles at the bubbling forest spring.", "Kami mengisi ulang botol kami di mata air hutan yang jernih.", "A2", "Nature"),
                        vocab("f_7_filter", "Filter", "Penyaring air", "A device to purify wild river water for safe drinking", "Noun", "/ˈfɪltə/", "Pump the stream water through a ceramic filter.", "Pompalah air anak sungai melewati penyaring keramik.", "A2", "Equipment"),
                        vocab("f_7_hunger", "Hunger", "Rasa lapar", "The feeling of needing sustenance after hours of hiking", "Noun", "/ˈhʌŋɡə/", "A bowl of trail stew satisfied our evening hunger.", "Semangkuk sup hangat memuaskan rasa lapar malam kami.", "A1", "Health"),
                        vocab("f_7_thirst", "Thirst", "Rasa haus", "A feeling of needing to drink water", "Noun", "/θɜːst/", "The cold mountain stream quenched our fierce thirst.", "Anak sungai pegunungan yang dingin memuaskan rasa haus kami.", "A1", "Health"),
                        vocab("f_7_feast", "Feast", "Santapan bersama", "A generous celebratory meal at basecamp", "Noun", "/fiːst/", "The team enjoyed a warm feast upon clearing the forest.", "Tim menikmati santapan bersama yang hangat setelah keluar dari hutan.", "A2", "Camp")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 8, levelId = "1-8",
                    topicTitle = "Wilderness Foraging and Edible Flora",
                    learningObjective = "Recognize wild forest flora, foraging safety, and medicinal herbs.",
                    vocabulary = listOf(
                        vocab("f_8_berry", "Berry", "Buah beri liar", "A small round juicy fruit growing wild on forest bushes", "Noun", "/ˈberi/", "Edible purple berries grow along the sunny clearing.", "Buah beri ungu yang aman dimakan tumbuh di tempat terbuka yang cerah.", "A1", "Nature"),
                        vocab("f_8_herb", "Herb", "Tanaman herbal", "Any plant with leaves or seeds used for medicine or seasoning", "Noun", "/hɜːb/", "The native guide brewed healing tea from wild herbs.", "Pemandu lokal menyeduh teh obat dari tanaman herbal liar.", "A2", "Nature"),
                        vocab("f_8_mushroom", "Mushroom", "Jamur hutan", "A fleshy fungal fruiting body growing on damp forest soil", "Noun", "/ˈmʌʃruːm/", "Beware of brightly colored toxic wild mushrooms.", "Waspadalah terhadap jamur liar beracun yang berwarna cerah.", "A1", "Nature"),
                        vocab("f_8_forage", "Forage", "Meramban / Mencari bekal liar", "To search widely for wild food, herbs, or provisions", "Verb", "/ˈfɒrɪdʒ/", "Explorers forage for safe roots and wild fruit.", "Para penjelajah meramban mencari akar aman dan buah liar.", "B1", "Survival"),
                        vocab("f_8_harvest", "Harvest", "Memanen hasil alam", "To gather wild plants, nuts, and ripe fruits in season", "Verb", "/ˈhɑːvɪst/", "Harvest only mature nuts and leave the rest for nature.", "Panen hanya kacang yang sudah matang dan sisakan untuk alam.", "A2", "Nature"),
                        // Distractor & Enrichment Words
                        vocab("f_8_nut", "Nut", "Kacang hutan", "A hard-shelled dry fruit with an edible kernel inside", "Noun", "/nʌt/", "Squirrels hid wild acorns and hazel nuts in hollow trees.", "Tupai menyembunyikan biji ek dan kacang di pohon berlubang.", "A1", "Nature"),
                        vocab("f_8_bark_m", "Flora", "Flora tetumbuhan", "The plant life characteristic of a region or period", "Noun", "/ˈflɔːrə/", "The botanical guide cataloged endemic forest flora.", "Buku panduan botani mencatat flora hutan endemik.", "B1", "Nature"),
                        vocab("f_8_toxic", "Toxic", "Beracun", "Poisonous and unsafe to ingest or touch", "Adjective", "/ˈtɒksɪk/", "Never ingest wild berries that may be toxic.", "Jangan pernah memakan buah beri liar yang mungkin beracun.", "B1", "Survival"),
                        vocab("f_8_edible", "Edible", "Layak dimakan", "Fit or safe to be eaten without harm", "Adjective", "/ˈedəbl/", "Verify whether forest roots are edible before cooking.", "Pastikan apakah akar hutan layak dimakan sebelum dimasak.", "B1", "Survival"),
                        vocab("f_8_sap", "Sap", "Getah pohon", "The fluid which circulates in the vascular system of a plant", "Noun", "/sæp/", "Sweet maple sap dripped from the carved wooden spigot.", "Getah manis menetes dari sumbat kayu yang dipahat.", "A2", "Nature")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 9, levelId = "1-9",
                    topicTitle = "Forest Wildlife and Animal Tracks",
                    learningObjective = "Identify forest fauna, animal footprints, and wildlife tracking signs.",
                    vocabulary = listOf(
                        vocab("f_9_track", "Track", "Jejak kaki hewan", "A rough footprint or trail left by an animal in the mud", "Noun", "/træk/", "Fresh wolf tracks were pressed into the damp soil.", "Jejak kaki serigala yang baru tercetak di tanah yang lembap.", "A1", "Wildlife"),
                        vocab("f_9_deer", "Deer", "Rusa hutan", "A graceful hoofed mammal browsing quietly in forest glades", "Noun", "/dɪə/", "A gentle wild deer bounded across the mossy brook.", "Seekor rusa hutan yang anggun melompat melintasi anak sungai berlumut.", "A1", "Wildlife"),
                        vocab("f_9_wolf", "Wolf", "Serigala", "A wild carnivorous mammal hunting in forest packs", "Noun", "/wʊlf/", "We heard the distant howl of a lone forest wolf.", "Kami mendengar lolongan serigala hutan yang jauh di kejauhan.", "A1", "Wildlife"),
                        vocab("f_9_hawk", "Hawk", "Burung elang", "A sharp-eyed bird of prey soaring above tree canopies", "Noun", "/hɔːk/", "A golden hawk circled high above the green forest canopy.", "Seekor elang emas berputar-putar tinggi di atas kanopi hutan hijau.", "A1", "Wildlife"),
                        vocab("f_9_fox", "Fox", "Rubah merah", "A clever carnivorous mammal with a bushy tail and red fur", "Noun", "/fɒks/", "A curious red fox watched our campsite from behind ferns.", "Seekor rubah merah yang penasaran mengawasi perkemahan kami dari balik pakis.", "A1", "Wildlife"),
                        // Distractor & Enrichment Words
                        vocab("f_9_paw", "Paw", "Kaki bercakar", "The foot of an animal that has claws or pads", "Noun", "/pɔː/", "Deep paw prints marked the clearing entrance.", "Bekas kaki bercakar yang dalam menandai pintu masuk tempat terbuka.", "A1", "Wildlife"),
                        vocab("f_9_nest", "Nest", "Sarang burung", "A structure built by birds to lay eggs and shelter young", "Noun", "/nest/", "A high eagle nest sat atop the highest pine branch.", "Sarang elang yang tinggi berada di puncak dahan pinus tertinggi.", "A1", "Wildlife"),
                        vocab("f_9_burrow", "Burrow", "Liang tanah", "A hole or tunnel dug by a small animal as a dwelling", "Noun", "/ˈbʌrəʊ/", "Rabbits retreated into their subterranean dirt burrow.", "Kelinci-kelinci berlindung ke dalam liang tanah bawah tanah mereka.", "A2", "Wildlife"),
                        vocab("f_9_howl", "Howl", "Lolongan", "A long loud mournful cry uttered by wolves or dogs", "Noun", "/haʊl/", "The midnight howl echoed through the rocky ravine.", "Lolongan tengah malam bergema di jurang berbatu.", "A2", "Wildlife"),
                        vocab("f_9_predator", "Predator", "Pemangsa", "An animal that naturally preys on other creatures", "Noun", "/ˈpredətə/", "Store food high up on branches to deter wild predators.", "Simpan makanan tinggi di dahan pohon untuk mencegah pemangsa liar.", "B1", "Wildlife")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 1, level = 10, levelId = "1-10",
                    topicTitle = "Forest Outpost and Checkpoint Victory",
                    learningObjective = "Conquer the forest chapter checkpoint and reach the wooden bridge outpost.",
                    vocabulary = listOf(
                        vocab("f_10_outpost", "Outpost", "Pos terdepan", "A remote settlement or station at the edge of the wilderness", "Noun", "/ˈaʊtpəʊst/", "The wooden outpost marks the frontier between forest and sea.", "Pos terdepan kayu menandai perbatasan antara hutan dan laut.", "A2", "Landmarks"),
                        vocab("f_10_beacon", "Beacon", "Suar penanda", "A signaling light or fire set on a high point as a guide", "Noun", "/ˈbiːkən/", "The lookout beacon burned brightly to guide weary travelers.", "Suar penanda pos pemantau menyala terang memandu para pelancong yang lelah.", "B1", "Landmarks"),
                        vocab("f_10_gateway", "Gateway", "Gerbang perbatasan", "An entrance or means of access to a new exploration realm", "Noun", "/ˈɡeɪtweɪ/", "The grand stone arch is the gateway to the tropical island.", "Lengkungan batu megah adalah gerbang perbatasan menuju pulau tropis.", "A2", "Landmarks"),
                        vocab("f_10_triumph", "Triumph", "Kemenangan gemilang", "A great victory, achievement, or overcoming of obstacles", "Noun", "/ˈtraɪʌmf/", "Reaching the forest checkpoint was a memorable team triumph.", "Mencapai titik pos pemeriksaan hutan adalah kemenangan gemilang tim.", "B1", "Achievement"),
                        vocab("f_10_frontier", "Frontier", "Garis perbatasan", "The extreme limit of settled land beyond which lies wilderness", "Noun", "/ˈfrʌntɪə/", "Explorers stood proudly at the oceanic island frontier.", "Para penjelajah berdiri dengan bangga di garis perbatasan pulau samudra.", "B1", "Geography"),
                        // Distractor & Enrichment Words
                        vocab("f_10_checkpoint", "Checkpoint", "Titik pos pemeriksaan", "A barrier or station where travelers are verified and rest", "Noun", "/ˈtʃekpɔɪnt/", "Stamp your explorer passport at the level checkpoint.", "Cap paspor penjelajahmu di titik pos pemeriksaan level.", "A2", "Landmarks"),
                        vocab("f_10_horizon", "Shoreline", "Garis pantai", "The edge where island land meets coastal waves", "Noun", "/ˈʃɔːlaɪn/", "From the outpost tower, the blue shoreline gleamed.", "Dari menara pos terdepan, garis pantai biru berkilau indah.", "A2", "Geography"),
                        vocab("f_10_flag", "Banner", "Panji bendera", "A flag or cloth bearing a heraldic symbol of the team", "Noun", "/ˈbænə/", "The golden expedition banner fluttered above the watchtower.", "Panji bendera ekspedisi keemasan berkibar di atas menara pengawas.", "A2", "Landmarks"),
                        vocab("f_10_milestone", "Milestone", "Tonggak capaian", "An action or event marking a significant stage in a quest", "Noun", "/ˈmaɪlstəʊn/", "Completing Chapter 1 is a proud learning milestone.", "Menyelesaikan Bab 1 adalah tonggak capaian belajar yang membanggakan.", "B1", "Achievement"),
                        vocab("f_10_passage", "Passage", "Jalan terusan", "A narrow way, path, or bridge leading to the next region", "Noun", "/ˈpæsɪdʒ/", "Cross the long wooden bridge passage toward the island.", "Seberangi jalan terusan jembatan kayu panjang menuju pulau.", "A2", "Landmarks")
                    )
                )
            )

            // ==================== CHAPTER 2: ISLAND (2-1 to 2-10) ====================
            add(
                LevelTopicDefinition(
                    stage = 2, level = 1, levelId = "2-1",
                    topicTitle = "Maritime Weather and Tropical Breezes",
                    learningObjective = "Describe coastal squalls, trade winds, ocean breezes, and tropical climate.",
                    vocabulary = listOf(
                        vocab("i_1_breeze", "Breeze", "Angin sepoi-sepoi", "A gentle, refreshing coastal wind blowing from the ocean", "Noun", "/briːz/", "A cooling sea breeze rustled the coconut palm fronds.", "Angin sepoi-sepoi laut yang sejuk mengayunkan daun kelapa.", "A2", "Weather"),
                        vocab("i_1_storm", "Storm", "Badai tropis", "Violent weather marked by fierce wind, heavy rain, and waves", "Noun", "/stɔːm/", "Sailors anchored their boats securely before the tropical storm.", "Para pelaut menambatkan perahu mereka dengan aman sebelum badai tropis.", "A1", "Weather"),
                        vocab("i_1_tide", "Tide", "Pasang surut air laut", "The periodic alternate rising and falling of sea surface", "Noun", "/taɪd/", "We waited for low tide to explore the exposed coral reef.", "Kami menunggu air laut surut untuk menjelajahi terumbu karang.", "A2", "Weather"),
                        vocab("i_1_wave", "Wave", "Ombak laut", "A ridge or swell moving along the surface of ocean water", "Noun", "/weɪv/", "Crisp white waves crashed gently against the golden sand.", "Ombak putih yang jernih menghantam lembut pasir keemasan.", "A1", "Nature"),
                        vocab("i_1_humid", "Humid", "Lembap beruap", "Having high moisture and water vapor in the tropical air", "Adjective", "/ˈhjuːmɪd/", "The island rainforest air is warm, thick, and humid.", "Udara hutan hujan pulau terasa hangat, pekat, dan lembap.", "B1", "Weather"),
                        // Distractor & Enrichment Words
                        vocab("i_1_monsoon", "Monsoon", "Musim muson", "A seasonal wind bringing torrential refreshing rains", "Noun", "/mɒnˈsuːn/", "The summer monsoon replenished all freshwater springs.", "Muson musim panas mengisi kembali seluruh mata air tawar.", "B1", "Weather"),
                        vocab("i_1_squall", "Squall", "Angin ribut mendadak", "A sudden violent gust of wind or localized rainstorm", "Noun", "/skwɔːl/", "The captain steered through a sudden ocean squall.", "Kapten mengemudikan kapal melewati angin ribut laut yang mendadak.", "B1", "Weather"),
                        vocab("i_1_rainbow", "Rainbow", "Pelangi", "An arc of spectral colors appearing in the sky after rain", "Noun", "/ˈreɪnbəʊ/", "A double rainbow arched over the turquoise island lagoon.", "Pelangi ganda melengkung di atas laguna pulau yang berwarna toska.", "A1", "Weather"),
                        vocab("i_1_gale", "Gale", "Angin kencang", "A very strong wind measuring between breeze and storm", "Noun", "/ɡeɪl/", "The coastal gale whipped up spray from the barrier reef.", "Angin kencang pesisir meniupkan buih air dari terumbu karang penghalang.", "B1", "Weather"),
                        vocab("i_1_sunshine", "Sunshine", "Sinar surya terang", "Direct sunlight warming the open island shores", "Noun", "/ˈsʌnʃaɪn/", "Bright sunshine illuminated the colorful tropical reef.", "Sinar surya yang terang menyinari terumbu karang tropis beraneka warna.", "A1", "Weather")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 2, levelId = "2-2",
                    topicTitle = "Coastal Exploration Gear and Island Apparel",
                    learningObjective = "Identify seaworthy garments, sun protection, and coastal hiking gear.",
                    vocabulary = listOf(
                        vocab("i_2_cloak", "Cloak", "Jubah pelindung", "A sleeveless outdoor garment protecting from rain and spray", "Noun", "/kləʊk/", "Fasten your waterproof cloak against the flying sea spray.", "Kencangkan jubah kedap airmu dari percikan air laut.", "A2", "Equipment"),
                        vocab("i_2_sandals", "Sandals", "Sandal karang", "Light footwear strapped to the foot for wading rocky shores", "Noun", "/ˈsændlz/", "Wear rubber reef sandals to walk safely over sharp coral.", "Kenakan sandal karang karet untuk berjalan aman di atas karang tajam.", "A1", "Equipment"),
                        vocab("i_2_goggles", "Goggles", "Kacamata renang", "Close-fitting protective eye gear for underwater vision", "Noun", "/ˈɡɒɡlz/", "Slip on diving goggles to examine the seabed corals.", "Kenakan kacamata renang untuk meneliti terumbu karang dasar laut.", "A2", "Equipment"),
                        vocab("i_2_poncho", "Poncho", "Jas hujan ponco", "A waterproof sheet with a center hole protecting hikers", "Noun", "/ˈpɒntʃəʊ/", "A lightweight poncho keeps your pack dry in downpours.", "Jas hujan ponco yang ringan menjaga ranselmu tetap kering saat hujan lebat.", "A2", "Equipment"),
                        vocab("i_2_vest", "Vest", "Rompi apung keselamatan", "A sleeveless buoyant garment worn for maritime safety", "Noun", "/vest/", "Fasten the life vest securely before boarding the canoe.", "Kencangkan rompi apung keselamatan dengan aman sebelum naik kano.", "A1", "Equipment"),
                        // Distractor & Enrichment Words
                        vocab("i_2_hat", "Hat", "Topi rimba", "A wide-brimmed head covering shielding from fierce tropical sun", "Noun", "/hæt/", "A broad brimmed hat protects your face from sunburn.", "Topi bertepi lebar melindungi wajahmu dari sengatan terik matahari.", "A1", "Equipment"),
                        vocab("i_2_towel", "Towel", "Handuk cepat-kering", "An absorbent cloth used for drying off after river wading", "Noun", "/ˈtaʊəl/", "Keep a microfiber towel clipped to your daypack.", "Simpan handuk mikrofiber yang dikaitkan ke tas harianmu.", "A1", "Equipment"),
                        vocab("i_2_belt", "Belt", "Sabuk perlengkapan", "A sturdy band worn around the waist to carry tools and pouch", "Noun", "/belt/", "Attach your canteen and compass to the heavy leather belt.", "Kaitkan tempat air dan kompasmu ke sabuk kulit yang kokoh.", "A1", "Equipment"),
                        vocab("i_2_fabric", "Fabric", "Kain serat kanvas", "Durable woven material used for sails and expedition tents", "Noun", "/ˈfæbrɪk/", "Tough canvas fabric resisted tears from thorny coastal vines.", "Kain kanvas yang tangguh tahan robekan dari sulur berduri di pesisir.", "A2", "Equipment"),
                        vocab("i_2_pack", "Drybag", "Kantung kedap air", "A sealed watertight container preventing moisture damage", "Noun", "/ˈdraɪbæɡ/", "Store maps and matches securely in the waterproof drybag.", "Simpan peta dan korek api dengan aman di dalam kantung kedap air.", "A2", "Equipment")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 3, levelId = "2-3",
                    topicTitle = "Marine Wonders and Coral Reef Exploration",
                    learningObjective = "Explore maritime biodiversity, coral lagoons, and coastal sea life.",
                    vocabulary = listOf(
                        vocab("i_3_coral", "Coral", "Terumbu karang", "Hard stony skeletons of marine organisms forming reefs", "Noun", "/ˈkɒrəl/", "Vibrant pink and orange coral flourished in the lagoon.", "Terumbu karang merah muda dan oranye cerah berkembang di laguna.", "A2", "Nature"),
                        vocab("i_3_reef", "Reef", "Beting karang", "A ridge of jagged rock or coral near the surface of the sea", "Noun", "/riːf/", "The barrier reef protects the calm inner bay from huge waves.", "Beting karang penghalang melindungi teluk dalam yang tenang dari ombak besar.", "A2", "Geography"),
                        vocab("i_3_shell", "Shell", "Cangkang kerang", "The hard protective outer case of marine mollusks", "Noun", "/ʃel/", "Arka discovered a pearlescent spiral shell on the sandbar.", "Arka menemukan cangkang kerang berputar sewarna mutiara di gosong pasir.", "A1", "Nature"),
                        vocab("i_3_lagoon", "Lagoon", "Laguna air tenang", "A body of shallow calm turquoise water separated from the sea", "Noun", "/ləˈɡuːn/", "Colorful fish swam peacefully inside the crystal lagoon.", "Ikan berwarna-warni berenang tenang di dalam laguna yang jernih.", "A2", "Geography"),
                        vocab("i_3_turtle", "Turtle", "Penyu laut", "A large marine reptile with flippers and a bony shell", "Noun", "/ˈtɜːtl/", "A graceful sea turtle glided across the shallow reef.", "Seekor penyu laut yang anggun meluncur melintasi terumbu karang dangkal.", "A1", "Wildlife"),
                        // Distractor & Enrichment Words
                        vocab("i_3_dolphin", "Dolphin", "Lumba-lumba", "An intelligent aquatic mammal known for jumping waves", "Noun", "/ˈdɒlfɪn/", "A pod of friendly dolphins guided our sailboat into harbor.", "Sekawanan lumba-lumba ramah memandu perahu layar kita memasuki pelabuhan.", "A1", "Wildlife"),
                        vocab("i_3_crab", "Crab", "Kepiting pasir", "A crustacean with pincers scuttling along the shoreline", "Noun", "/kræb/", "Tiny red crabs scurried into their tidal sand burrows.", "Kepiting merah kecil bergegas masuk ke liang pasir pasang surut mereka.", "A1", "Wildlife"),
                        vocab("i_3_seabed", "Seabed", "Dasar samudra", "The bottom floor of the ocean covered with sand and rocks", "Noun", "/ˈsiːbed/", "Sunbeams illuminated the rippling white sand of the seabed.", "Sinar matahari menyinari pasir putih dasar samudra yang beriak.", "A2", "Geography"),
                        vocab("i_3_anchor", "Anchor", "Jangkar kapal", "A heavy iron device cast overboard to moor a vessel", "Noun", "/ˈæŋkə/", "Drop the iron anchor safely into the sandy seabed.", "Turunkan jangkar besi dengan aman ke dasar samudra berpasir.", "A2", "Maritime"),
                        vocab("i_3_harbor", "Harbor", "Pelabuhan alam", "A sheltered body of water where vessels seek safety", "Noun", "/ˈhɑːbə/", "The quiet natural harbor provided calm waters for our canoe.", "Pelabuhan alam yang tenang menyediakan perairan damai untuk kano kita.", "A2", "Maritime")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 4, levelId = "2-4",
                    topicTitle = "Beach Geography and Island Coastal Features",
                    learningObjective = "Map coastal landforms, rocky headlands, sandbars, and secluded bays.",
                    vocabulary = listOf(
                        vocab("i_4_shore", "Shore", "Pesisir pantai", "The land along the edge of the sea or ocean", "Noun", "/ʃɔː/", "Waves broke softly along the curved island shore.", "Ombak pecah perlahan di sepanjang pesisir pulau yang melengkung.", "A1", "Geography"),
                        vocab("i_4_coast", "Coast", "Garis pantai luas", "The boundary where land meets the open expanse of sea", "Noun", "/kəʊst/", "Rugged cliffs dominate the western coast of the island.", "Tebing terjal mendominasi garis pantai barat pulau tersebut.", "A1", "Geography"),
                        vocab("i_4_sand", "Sand", "Pasir pantai", "Loose granular material forming beaches and dunes", "Noun", "/sænd/", "Golden sand warmed our bare feet as we walked.", "Pasir keemasan menghangatkan telapak kaki kami saat kami melangkah.", "A1", "Geography"),
                        vocab("i_4_cliff", "Cliff", "Tebing karang", "A steep high rock face overlooking the crashing surf", "Noun", "/klɪf/", "The lookout station is perched high atop the sea cliff.", "Pos pemantau bertengger tinggi di atas tebing karang laut.", "A2", "Geography"),
                        vocab("i_4_bay", "Bay", "Teluk tenang", "A broad curved inlet of the sea where land curves inward", "Noun", "/beɪ/", "Our expedition landed the wooden boat in a sheltered bay.", "Ekspedisi kita mendaratkan perahu kayu di teluk yang terlindung.", "A2", "Geography"),
                        // Distractor & Enrichment Words
                        vocab("i_4_dune", "Dune", "Bukit pasir", "A mound or ridge of sand formed by coastal wind", "Noun", "/djuːn/", "Tall sea grasses held the shifting sand dune in place.", "Rumput laut tinggi menahan bukit pasir yang mudah bergeser.", "A2", "Geography"),
                        vocab("i_4_cove", "Cove", "Teluk kecil terpencil", "A small sheltered recessed bay often bordered by rocks", "Noun", "/kəʊv/", "Pirates once hid secret supplies in the hidden rocky cove.", "Bajak laut dahulu menyembunyikan perbekalan rahasia di teluk kecil terpencil ini.", "A2", "Geography"),
                        vocab("i_4_inlet", "Inlet", "Ceruk perairan", "A narrow body of water between islands or cliffs", "Noun", "/ˈɪnlet/", "Paddle gently through the tidal inlet between the reefs.", "Mendayunglah perlahan melewati ceruk perairan pasang surut di antara karang.", "B1", "Geography"),
                        vocab("i_4_peninsula", "Peninsula", "Semenanjung tanah", "A piece of land almost entirely surrounded by water", "Noun", "/pəˈnɪnsjələ/", "The stone lighthouse stands at the tip of the peninsula.", "Mercusuar batu berdiri di ujung semenanjung tanah tersebut.", "B1", "Geography"),
                        vocab("i_4_sandbar", "Sandbar", "Gosong pasir", "A ridge of sand built up by ocean currents near shore", "Noun", "/ˈsændbɑː/", "At low tide, a natural sandbar linked the two islands.", "Saat air surut, gosong pasir alami menghubungkan kedua pulau itu.", "A2", "Geography")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 5, levelId = "2-5",
                    topicTitle = "Explorer Mindset, Fortitude and Resolve",
                    learningObjective = "Describe psychological resilience, courage, curiosity, and teamwork in expeditions.",
                    vocabulary = listOf(
                        vocab("i_5_brave", "Brave", "Pemberani tangguh", "Showing courage and readiness to face danger without hesitation", "Adjective", "/breɪv/", "Brave scouts stepped into the uncharted jungle first.", "Peninjau pemberani melangkah masuk ke rimba tak terpetakan lebih dulu.", "A1", "Character"),
                        vocab("i_5_curious", "Curious", "Penuh rasa ingin tahu", "Eager to explore, learn, and discover unknown mysteries", "Adjective", "/ˈkjʊəriəs/", "Curious adventurers examine ancient stone inscriptions closely.", "Petualang yang penuh rasa ingin tahu meneliti prasasti batu kuno dengan seksama.", "A2", "Character"),
                        vocab("i_5_steadfast", "Steadfast", "Kukuh teguh", "Firmly resolute and unwavering in loyalty and purpose", "Adjective", "/ˈstedfɑːst/", "Remain steadfast even when solving perilous island puzzles.", "Tetaplah teguh bahkan saat memecahkan teka-teki pulau yang berbahaya.", "B2", "Character"),
                        vocab("i_5_resilient", "Resilient", "Ulet berdaya tahan", "Able to quickly recover from hardship, fatigue, and setbacks", "Adjective", "/rɪˈzɪliənt/", "A resilient explorer bounces back after a grueling march.", "Penjelajah yang ulet bangkit kembali setelah perjalanan jauh yang melelahkan.", "B2", "Character"),
                        vocab("i_5_vigilant", "Vigilant", "Waspada siaga", "Keeping careful watch for potential danger and traps", "Adjective", "/ˈvɪdʒɪlənt/", "The night sentry remained vigilant against rising tides.", "Penjaga malam tetap waspada terhadap pasang air laut yang naik.", "B1", "Character"),
                        // Distractor & Enrichment Words
                        vocab("i_5_patient", "Patient", "Sabar teliti", "Able to accept delays or trials without becoming annoyed", "Adjective", "/ˈpeɪʃnt/", "Be patient when decoding the weathered island carvings.", "Bersabarlah saat menerjemahkan ukiran pulau yang telah lapuk.", "A2", "Character"),
                        vocab("i_5_courage", "Courage", "Keberanian luhur", "The mental strength to face fear, pain, or dangerous trials", "Noun", "/ˈkʌrɪdʒ/", "It takes courage to sail into unknown ocean storms.", "Dibutuhkan keberanian untuk berlayar menuju badai laut yang tak dikenal.", "B1", "Character"),
                        vocab("i_5_calm", "Calm", "Tenang terkendali", "Not showing agitation or panic in emergencies", "Adjective", "/kɑːm/", "Keep a calm mind when navigating through thick sea fog.", "Jagalah pikiran tetap tenang saat menavigasi kabut laut yang tebal.", "A1", "Character"),
                        vocab("i_5_eager", "Eager", "Bersemangat tinggi", "Keenly enthusiastic and ready to proceed on quests", "Adjective", "/ˈiːɡə/", "The young team was eager to solve the stone riddle.", "Tim muda sangat bersemangat tinggi memecahkan teka-teki batu.", "A2", "Character"),
                        vocab("i_5_resolve", "Resolve", "Tekad bulat", "Firm determination to complete a perilous journey", "Noun", "/rɪˈzɒlv/", "Nothing shook our resolve to reach the crystal cave.", "Tak ada yang menggoyahkan tekad bulat kita untuk mencapai gua kristal.", "B1", "Character")
                    )
                )
            )

            // Add remaining Chapter 2 levels (2-6 to 2-10) with pure adventure focus
            add(
                LevelTopicDefinition(
                    stage = 2, level = 6, levelId = "2-6",
                    topicTitle = "Stamina, Field Endurance and Conditioning",
                    learningObjective = "Master physical capabilities, endurance, breath control, and trail agility.",
                    vocabulary = listOf(
                        vocab("i_6_stamina", "Stamina", "Daya tahan fisik", "The ability to sustain prolonged physical and mental effort", "Noun", "/ˈstæmɪnə/", "Climbing steep sandy dunes requires steady stamina.", "Mendaki bukit pasir curam menuntut daya tahan fisik yang stabil.", "B1", "Health"),
                        vocab("i_6_endurance", "Endurance", "Ketahanan tubuh", "The capacity of something to last and withstand hardship", "Noun", "/ɪnˈdjʊərəns/", "Long maritime rowing tested the endurance of our crew.", "Mendayung di laut lepas dalam waktu lama menguji ketahanan tubuh awak kapal.", "B1", "Health"),
                        vocab("i_6_reflex", "Reflex", "Refleks tanggap", "An involuntary and swift instinctive physical reaction", "Noun", "/ˈriːfleks/", "Quick reflexes helped him catch the slipping compass.", "Refleks tanggap yang cepat membantunya menangkap kompas yang tergelincir.", "B1", "Health"),
                        vocab("i_6_stride", "Stride", "Langkah tegap", "A long decisive step taken while walking purposefully", "Noun", "/straɪd/", "Match your walking stride with the team lead.", "Sesuaikan langkah tegapmu dengan pemimpin perjalanan.", "B1", "Movement"),
                        vocab("i_6_agility", "Agility", "Kelincahan gerak", "The ability to move quickly and easily over obstacles", "Noun", "/əˈdʒɪləti/", "The mountain goat leaped across boulders with effortless agility.", "Kambing gunung melompat melewati batu-batu besar dengan kelincahan gerak yang mudah.", "B2", "Movement"),
                        // Distractors
                        vocab("i_6_vigor", "Vigor", "Kekuatan prima", "Physical strength and good health", "Noun", "/ˈvɪɡə/", "Rest restored his vigor for the next day.", "Istirahat memulihkan kekuatan primanya untuk hari esok.", "B2", "Health"),
                        vocab("i_6_pace", "Pace", "Irama kecepatan", "A consistent speed in walking or running", "Noun", "/peɪs/", "Set a sustainable pace across the sand flats.", "Tetapkan irama kecepatan yang berkelanjutan di dataran pasir.", "A2", "Movement"),
                        vocab("i_6_vitality", "Vitality", "Daya hidup", "The state of being strong and active", "Noun", "/vaɪˈtæləti/", "Fresh coconut water renewed their vitality.", "Air kelapa segar memulihkan daya hidup mereka.", "B2", "Health"),
                        vocab("i_6_muscle", "Muscle", "Otot tubuh", "A band of fibrous tissue that produces movement", "Noun", "/ˈmʌsl/", "Rowing built powerful arm and shoulder muscles.", "Mendayung membentuk otot lengan dan bahu yang kokoh.", "A1", "Health"),
                        vocab("i_6_balance", "Balance", "Keseimbangan", "An even distribution of weight enabling one to remain upright", "Noun", "/ˈbæləns/", "Keep your balance while crossing slippery rocks.", "Jaga keseimbanganmu saat melintasi batu-batu licin.", "A2", "Movement")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 7, levelId = "2-7",
                    topicTitle = "Coastal Traversal and Rock Scrambling",
                    learningObjective = "Describe active traversal verbs: climbing, scrambling, wading, and navigating cliffs.",
                    vocabulary = listOf(
                        vocab("i_7_climb", "Climb", "Memanjat", "To ascend steep rock faces or cliffs using hands and feet", "Verb", "/klaɪm/", "Climb the sea cliff carefully using marked handholds.", "Panjatlah tebing laut dengan hati-hati menggunakan pegangan yang ditandai.", "A1", "Action"),
                        vocab("i_7_trek", "Trek", "Menjelajah jauh", "To make a long arduous journey especially on foot", "Verb", "/trek/", "The adventurers trekked along the uncharted coastal peninsula.", "Para petualang menjelajah jauh di sepanjang semenanjung pesisir yang belum terpetakan.", "B1", "Action"),
                        vocab("i_7_wade", "Wade", "Mengarungi air", "To walk through shallow water or mud with effort", "Verb", "/weɪd/", "We had to wade across the tidal sandbar before high water.", "Kami harus mengarungi air melintasi gosong pasir pasang sebelum air naik.", "A2", "Action"),
                        vocab("i_7_scramble", "Scramble", "Merayap di bebatuan", "To move quickly and with difficulty over rocks using hands", "Verb", "/ˈskræmbl/", "The scouts scrambled up the loose gravel bluff.", "Para peninjau merayap di bebatuan mendaki tebing kerikil yang gembur.", "B1", "Action"),
                        vocab("i_7_descend", "Descend", "Menuruni lereng", "To move downward along a steep incline or slope", "Verb", "/dɪˈsend/", "Descend the stone stairs slowly to prevent stumbling.", "Turunilah tangga batu perlahan-lahan untuk mencegah tersandung.", "A2", "Action"),
                        // Distractors
                        vocab("i_7_traverse", "Traverse", "Menyeberang melintang", "To travel across or through an obstacle horizontally", "Verb", "/trəˈvɜːs/", "Traverse the narrow ledge above the crashing waves.", "Seberangi jalan setapak sempit melintang di atas deburan ombak.", "B2", "Action"),
                        vocab("i_7_leap", "Leap", "Melompat jauh", "To jump or spring a long distance over a gap", "Verb", "/liːp/", "Leap across the deep rock fissure with caution.", "Melompatlah melewati celah batu yang dalam dengan waspada.", "A2", "Action"),
                        vocab("i_7_hike", "Hike", "Mendaki jalan setapak", "To walk a long distance in the country or wilderness", "Verb", "/haɪk/", "Hikers follow the winding trail toward the ruins.", "Para pendaki mengikuti jalan setapak berliku menuju reruntuhan.", "A1", "Action"),
                        vocab("i_7_paddle", "Paddle", "Mendayung", "To propel a canoe or small boat through water", "Verb", "/ˈpædl/", "Paddle in steady rhythm toward the island pier.", "Mendayunglah dengan irama stabil menuju dermaga pulau.", "A1", "Action"),
                        vocab("i_7_anchor_v", "Moor", "Menambatkan kapal", "To make fast a boat by cables or anchors", "Verb", "/mʊə/", "Moor the wooden raft securely to the wooden piling.", "Tambatkan rakit kayu dengan erat pada tiang kayu dermaga.", "B1", "Action")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 8, levelId = "2-8",
                    topicTitle = "Field Surveying, Charting and Cartography",
                    learningObjective = "Master navigation charts, coastal mapping, and archaeological documentation.",
                    vocabulary = listOf(
                        vocab("i_8_chart", "Chart", "Bagan peta laut", "A specialized map of sea areas, reefs, and navigational hazards", "Noun", "/tʃɑːt/", "Examine the naval chart for submerged coral reefs.", "Periksalah bagan peta laut untuk mengetahui karang yang terendam.", "A2", "Navigation"),
                        vocab("i_8_survey", "Survey", "Menyurvei medan", "To examine and record the geographical features of an area", "Verb", "/səˈveɪ/", "The explorers climbed the lookout tower to survey the coastline.", "Para penjelajah menaiki menara pengawas untuk menyurvei garis pantai.", "B1", "Navigation"),
                        vocab("i_8_sketch", "Sketch", "Membuat sketsa", "A rough drawing showing the main features of landmarks", "Noun", "/sketʃ/", "Arka drew a pencil sketch of the ancient pier.", "Arka membuat sketsa pensil tentang dermaga kuno.", "A1", "Art"),
                        vocab("i_8_record", "Record", "Mencatat teliti", "To set down in writing for preservation in a journal", "Verb", "/rɪˈkɔːd/", "Record all new vocabulary words in your leather journal.", "Catatlah semua kosakata baru dengan teliti di buku harian kulitmu.", "A2", "Study"),
                        vocab("i_8_observe", "Observe", "Mengamati cermat", "To notice or perceive terrain features with watchful care", "Verb", "/əbˈzɜːv/", "Observe the ocean currents before launching the vessel.", "Amati arus laut dengan cermat sebelum meluncurkan perahu.", "A2", "Science"),
                        // Distractors
                        vocab("i_8_grid", "Grid", "Kisi koordinat", "A network of lines forming squares on a navigation map", "Noun", "/ɡrɪd/", "Align your ruler with the map's coordinate grid.", "Sejajarkan penggarismu dengan kisi koordinat peta.", "B1", "Navigation"),
                        vocab("i_8_legend", "Legend", "Keterangan simbol peta", "A key on a map explaining the meaning of symbols", "Noun", "/ˈledʒənd/", "Read the map legend to find fresh water springs.", "Bacalah keterangan simbol peta untuk menemukan mata air tawar.", "A2", "Navigation"),
                        vocab("i_8_scale", "Scale", "Skala perbandingan", "The ratio of a distance on the map to real distance on earth", "Noun", "/skeɪl/", "Use the map scale to calculate marching hours.", "Gunakan skala perbandingan peta untuk menghitung jam berjalan.", "B1", "Navigation"),
                        vocab("i_8_symbol", "Symbol", "Simbol tanda", "A sign or character representing an island landmark", "Noun", "/ˈsɪmbl/", "The red triangle symbol marks a hidden cavern.", "Simbol tanda segitiga merah menandai gua tersembunyi.", "A1", "Navigation"),
                        vocab("i_8_journal", "Journal", "Buku harian ekspedisi", "A daily written record of expedition discoveries and trials", "Noun", "/ˈdʒɜːnl/", "The traveler penned every triumph in his expedition journal.", "Pelancong itu menulis setiap kemenangan di buku harian ekspedisinya.", "A2", "Study")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 9, levelId = "2-9",
                    topicTitle = "Maritime Voyages and Sailing Craft",
                    learningObjective = "Identify watercraft parts, sailing vessels, and seafaring navigation terms.",
                    vocabulary = listOf(
                        vocab("i_9_vessel", "Vessel", "Kapal penjelajah", "A ship or large boat used for maritime expeditions", "Noun", "/ˈvesl/", "A sturdy wooden vessel carried the explorers across the sea.", "Kapal penjelajah kayu yang kokoh membawa para penjelajah melintasi samudra.", "B1", "Maritime"),
                        vocab("i_9_sail", "Sail", "Layar kapal", "A piece of heavy canvas catching the wind to propel a boat", "Noun", "/seɪl/", "Hoist the white canvas sail to catch the morning breeze.", "Kerek layar kapal kanvas putih untuk menangkap angin sepoi-sepoi pagi.", "A1", "Maritime"),
                        vocab("i_9_rudder", "Rudder", "Kemudi kapal", "A flat piece hinged to the stern for steering a boat", "Noun", "/ˈrʌdə/", "The helmsman gripped the wooden rudder to steer clear of reefs.", "Juru mudi memegang kemudi kapal kayu untuk menghindar dari karang.", "B1", "Maritime"),
                        vocab("i_9_mast", "Mast", "Tiang layar", "A tall upright post on a ship supporting the sails and rigging", "Noun", "/mɑːst/", "The lookout climbed the tall cedar mast to spot land.", "Pengawas menaiki tiang layar aras tinggi untuk melihat daratan.", "A2", "Maritime"),
                        vocab("i_9_voyage", "Voyage", "Pelayaran samudra", "A long journey involving travel by sea or water", "Noun", "/ˈvɔɪɪdʒ/", "A perilous voyage brought the crew to the island ruins.", "Pelayaran samudra yang penuh tantangan membawa awak kapal ke reruntuhan pulau.", "A2", "Travel"),
                        // Distractors
                        vocab("i_9_canoe", "Canoe", "Perahu kano", "A narrow boat with pointed ends propelled by a paddle", "Noun", "/kəˈnuː/", "The scouts paddled the dugout canoe up the quiet creek.", "Para peninjau mendayung perahu kano menyusuri anak sungai yang tenang.", "A1", "Maritime"),
                        vocab("i_9_raft", "Raft", "Rakit bambu", "A flat buoyant structure made of lashed logs or bamboo", "Noun", "/rɑːft/", "Build a bamboo raft to haul heavy supplies across the bay.", "Bangun rakit bambu untuk mengangkut perbekalan berat melintasi teluk.", "A1", "Maritime"),
                        vocab("i_9_hull", "Hull", "Lambung kapal", "The watertight body of a ship or boat", "Noun", "/hʌl/", "Barnacles clung to the submerged timber hull.", "Teritip menempel pada lambung kapal kayu yang terendam.", "B1", "Maritime"),
                        vocab("i_9_helm", "Helm", "Roda kemudi", "The steering apparatus of a ship or vessel", "Noun", "/helm/", "Hold the helm steady through the rolling ocean waves.", "Pegang roda kemudi dengan stabil menembus ombak samudra yang bergulung.", "B1", "Maritime"),
                        vocab("i_9_dock", "Dock", "Dermaga labuh", "A structure where ships are moored for loading supplies", "Noun", "/dɒk/", "Tie the mooring ropes securely to the wooden dock.", "Ikat tali tambat dengan erat pada dermaga labuh kayu.", "A1", "Maritime")
                    )
                )
            )

            add(
                LevelTopicDefinition(
                    stage = 2, level = 10, levelId = "2-10",
                    topicTitle = "Island Sanctuary and Ancient Haven",
                    learningObjective = "Conquer the island checkpoint, discover the ancient stone haven, and prepare for Ancient Rome.",
                    vocabulary = listOf(
                        vocab("i_10_haven", "Haven", "Tempat perlindungan damai", "A harbor or safe refuge offering safety and replenishment", "Noun", "/ˈheɪvn/", "The secluded cove was a peaceful haven for battered ships.", "Teluk terpencil itu adalah tempat perlindungan damai bagi kapal-kapal yang terhempas badai.", "B1", "Landmarks"),
                        vocab("i_10_sanctuary", "Sanctuary", "Suaka suci", "A place of safety, quiet reverence, and ancient protection", "Noun", "/ˈsæŋktʃuəri/", "The mossy stone pillars marked the entrance to the sanctuary.", "Pilar batu berlumut menandai pintu masuk menuju suaka suci.", "B1", "Landmarks"),
                        vocab("i_10_totem", "Totem", "Tiang totem ukir", "A carved emblem or pillar serving as an island heraldry", "Noun", "/ˈtəʊtəm/", "A majestic wooden totem honored the ancient ocean spirits.", "Tiang totem ukir kayu yang megah menghormati roh samudra kuno.", "A2", "History"),
                        vocab("i_10_legacy", "Legacy", "Warisan sejarah", "Something handed down from an ancestor or ancient civilization", "Noun", "/ˈleɡəsi/", "The ruins left a rich legacy of navigational knowledge.", "Reruntuhan itu meninggalkan warisan sejarah pengetahuan navigasi yang kaya.", "B1", "History"),
                        vocab("i_10_passage_r", "Causeway", "Jalan lintas batu", "A raised road or track across low or wet coastal ground", "Noun", "/ˈkɔːzweɪ/", "A monumental stone causeway leads onward toward Ancient Rome.", "Jalan lintas batu monumental mengarah maju menuju Romawi Kuno.", "B1", "Landmarks"),
                        // Distractors
                        vocab("i_10_pier", "Pier", "Dermaga pancang", "A platform on pillars projecting out over water", "Noun", "/pɪə/", "Watch the sunset from the edge of the wooden pier.", "Saksikan matahari terbenam dari ujung dermaga pancang kayu.", "A2", "Landmarks"),
                        vocab("i_10_altar", "Altar", "Altar batu persembahan", "A flat-topped table used for ancient ceremonies", "Noun", "/ˈɔːltə/", "The carved stone altar was bathed in moonlight.", "Altar batu persembahan berukir bermandikan cahaya rembulan.", "B1", "Landmarks"),
                        vocab("i_10_talisman", "Talisman", "Jimat pelindung", "An inscribed object thought to bring good luck and protection", "Noun", "/ˈtælɪzmən/", "The elder gifted Arka a carved shell talisman.", "Sang tetua menghadiahkan Arka sebuah jimat pelindung dari kerang berukir.", "B2", "History"),
                        vocab("i_10_citadel", "Citadel", "Benteng pertahanan", "A fortress commanding a city and defending against assaults", "Noun", "/ˈsɪtədəl/", "High Roman walls crowned the rocky island citadel.", "Tembok tinggi Romawi memahkotai benteng pertahanan pulau berbatu.", "B1", "Landmarks"),
                        vocab("i_10_archway", "Archway", "Gerbang lengkung", "A curved monumental entrance constructed of carved stone", "Noun", "/ˈɑːtʃweɪ/", "Walk through the archway to begin Chapter 3.", "Melangkahlah melewati gerbang lengkung untuk memulai Bab 3.", "A2", "Landmarks")
                    )
                )
            )

            // ==================== CHAPTER 3: ANCIENT ROME (3-1 to 3-10) ====================
            for (lvl in 1..10) {
                val subTitle = when (lvl) {
                    1 -> "Classical Roman Architecture and Colosseum"
                    2 -> "Imperial History and Civilization"
                    3 -> "Ancient Roman Crafts and Trade"
                    4 -> "Roman Urban Life and Forum"
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
            listOf("Colosseum", "Koloseum", "Pillar", "Pilar batu", "Arch", "Lengkungan batu", "Aqueduct", "Saluran air Romawi", "Monument", "Monumen agung", "Marble", "Batu marmer", "Arena", "Gelanggang arena", "Gate", "Gerbang kota", "Temple", "Kuil pemujaan", "Ruins", "Reruntuhan kuno"),
            listOf("Empire", "Kekaisaran", "Emperor", "Kaisar", "Senate", "Senat majelis", "Legion", "Legiun pasukan", "Gladiator", "Petarung gladiator", "Shield", "Perisai tempur", "Sword", "Pedang prajurit", "Armor", "Baju zirah", "Centurion", "Perwira senturion", "Triumph", "Pawai kemenangan"),
            listOf("Blacksmith", "Pandai besi", "Sculptor", "Pematung", "Builder", "Pembangun", "Merchant", "Pedagang", "Weaver", "Penenun", "Pottery", "Tembikar", "Stone", "Batu pahat", "Chisel", "Pahat besi", "Workshop", "Bengkel kerja", "Trade", "Perdagangan"),
            listOf("Plaza", "Alun-alun forum", "Boulevard", "Jalan raya kota", "Fountain", "Air mancur", "Bathhouse", "Pemandian umum", "Stadium", "Stadion lomba", "Wall", "Tembok kota", "Road", "Jalan batu", "Center", "Pusat kota", "District", "Distrik wilayah", "Bustle", "Keriuhan kota"),
            listOf("Justice", "Keadilan", "Law", "Hukum perdata", "Rule", "Aturan resmi", "Council", "Dewan penasihat", "Leader", "Pemimpin", "Rights", "Hak-hak warga", "Order", "Ketertiban", "Assembly", "Sidang majelis", "Decree", "Keputusan resmi", "Peace", "Perdamaian"),
            listOf("Invention", "Penemuan", "Sundial", "Jam matahari", "Lever", "Tuas pengangkat", "Wheel", "Roda gerobak", "Pulley", "Katrol tali", "Scale", "Timbangan neraca", "Measure", "Mengukur akurat", "Gear", "Roda gigi", "Bronze", "Perunggu", "Paved", "Diaspal batu"),
            listOf("Myth", "Mitos kuno", "Legend", "Legenda kepahlawanan", "Hero", "Pahlawan agung", "Drama", "Drama panggung", "Theater", "Teater batu", "Lyre", "Kecapi petik", "Chorus", "Paduan suara", "Mask", "Topeng teater", "Poetry", "Puisi epik", "Symphony", "Simfoni nada"),
            listOf("Banquet", "Perjamuan agung", "Feast", "Pesta makan", "Chariot", "Kereta perang", "Laurel", "Mahkota daun salam", "Crown", "Mahkota emas", "Ceremony", "Upacara khidmat", "Horn", "Trompet tanduk", "Toast", "Bersulang minum", "Honor", "Kehormatan", "Pageant", "Arak-arakan"),
            listOf("Mosaic", "Mosaik ubin", "Statue", "Patung marmer", "Carving", "Ukiran batu", "Fresco", "Lukisan dinding", "Masterpiece", "Karya agung", "Gallery", "Galeri seni", "Ornament", "Ornamen hiasan", "Canvas", "Kanvas lukis", "Artisan", "Pengrajin terampil", "Relief", "Relief timbul"),
            listOf("Wisdom", "Kebijaksanaan", "Philosophy", "Filsafat hidup", "Logic", "Penalaran logika", "Truth", "Kebenaran hakiki", "Scholar", "Cendekiawan bijak", "Scroll", "Gulungan naskah", "Debate", "Perdebatan ilmiah", "Intellect", "Kecerdasan akal", "Virtue", "Kebajikan budi", "Knowledge", "Pengetahuan luas")
        )
        val data = sets[(lvl - 1).coerceIn(0, sets.size - 1)]
        return (0 until 10).map { i ->
            val w = data[i * 2]
            val indo = data[i * 2 + 1]
            vocab("r_${lvl}_$i", w, indo, "Historical concept of $w in classical Roman civilization", "Noun", "/$w/", "The ancient Roman $w stood as a symbol of classical civilization.", "$w Romawi kuno berdiri sebagai simbol peradaban klasik.", "B1", "Ancient Rome")
        }
    }

    private fun generateChapter4Vocabs(lvl: Int): List<VocabularyEntity> {
        val wordBank = listOf(
            listOf("Summit", "Puncak tertinggi", "Peak", "Puncak gunung", "Cliff", "Tebing curam", "Ridge", "Punggung bukit", "Valley", "Lembah hijau", "Glacier", "Gletser es", "Slope", "Lereng gunung", "Canyon", "Ngarai terjal", "Altitude", "Ketinggian tempat", "Plateau", "Dataran tinggi"),
            listOf("Blizzard", "Badai salju lebat", "Avalanche", "Longsoran salju", "Frost", "Embun beku es", "Hail", "Hujan es batu", "Snowdrift", "Tumpukan salju", "Chill", "Hawa dingin", "Freeze", "Membeku es", "Iceberg", "Gunung es", "Fog", "Kabut tebal", "Thaw", "Mencair salju"),
            listOf("Rope", "Tali panjat nilon", "Carabiner", "Cincin kait baja", "Harness", "Tali pengaman tubuh", "Crampons", "Sepatu duri es", "Tent", "Tenda gunung tahan angin", "Thermos", "Termos air panas", "Flare", "Suar darurat terang", "Whistle", "Peluit penyelamat", "Goggles", "Kacamata salju", "Knapsack", "Ransel punggung"),
            listOf("Pine", "Pohon pinus alpine", "Fir", "Pohon cemara", "Edelweiss", "Bunga edelweiss", "Cedar", "Pohon aras gunung", "Falcon", "Burung elang falcon", "Ibex", "Kambing gunung liar", "Leopard", "Macan tutul salju", "Marmot", "Marmot alpine", "Lichen", "Lumut kerak beku", "Tundra", "Tundra dingin"),
            listOf("North", "Utara kompas", "South", "Selatan", "East", "Timur fajar", "West", "Barat senja", "Elevation", "Elevasi ketinggian", "Latitude", "Garis lintang", "Horizon", "Garis cakrawala", "Meridian", "Garis bujur", "Zenith", "Titik zenit atas", "Bearing", "Arah kompas derajat"),
            listOf("Barometer", "Barometer pengukur", "Humidity", "Kelembapan udara", "Atmosphere", "Lapisan atmosfer", "Gale", "Angin kencang", "Gust", "Hembusan angin tiba-tiba", "Vapor", "Uap air beku", "Forecast", "Prakiraan cuaca", "Cyclone", "Pusaran siklon", "Condensation", "Kondensasi embun", "Front", "Garis depan cuaca"),
            listOf("Rescue", "Penyelamatan darurat", "Teamwork", "Kerja sama tim", "Shelter", "Gubuk perlindungan", "Signal", "Sinyal pertolongan", "Courage", "Keberanian mendaki", "FirstAid", "Pertolongan pertama", "Stretcher", "Tandu lipat", "Radio", "Radio komunikasi", "Survival", "Kelangsungan hidup", "Cooperation", "Kerja sama padu"),
            listOf("Granite", "Batu granit keras", "Basalt", "Batu basal gunung api", "Limestone", "Batu kapur", "Quartz", "Kristal kuarsa", "Mineral", "Mineral batuan", "Strata", "Lapisan batuan bumi", "Erosion", "Pengikisan erosi", "Fault", "Patahan lempeng", "Magma", "Cairan magma", "Fossil", "Fosil purba"),
            listOf("Adaptation", "Adaptasi iklim dingin", "Habitat", "Habitat alami gunung", "Ecosystem", "Ekosistem alpine", "Endurance", "Daya tahan cuaca", "Rare", "Langka terlindungi", "Harsh", "Keras dan ekstrem", "Preserve", "Melestarikan alam", "Flora", "Tetumbuhan flora", "Fauna", "Satwa fauna", "Biomass", "Biomassa alam"),
            listOf("Panorama", "Pemandangan menyeluruh", "Vista", "Pemandangan megah", "Triumph", "Kemenangan penaklukan", "Conquest", "Penaklukan puncak", "Horizon", "Kaki langit luas", "Skyline", "Garis cakrawala puncak", "Achievement", "Pencapaian bersejarah", "Milestone", "Tonggak keberhasilan", "Expedition", "Ekspedisi agung", "Legacy", "Warisan petualang")
        )
        val data = wordBank[(lvl - 1).coerceIn(0, wordBank.size - 1)]
        return (0 until 10).map { i ->
            val w = data[i * 2]
            val indo = data[i * 2 + 1]
            vocab("m_${lvl}_$i", w, indo, "High-altitude alpine term: $w", "Noun", "/$w/", "The alpine explorer conquered the heights of the mountain $w.", "Penjelajah alpine menaklukkan ketinggian $w gunung.", "B1", "Mountain")
        }
    }

    private fun generateChapter5Vocabs(lvl: Int): List<VocabularyEntity> {
        val wordBank = listOf(
            listOf("Cavern", "Gua raksasa", "Grotto", "Gua kecil berbatu", "Threshold", "Ambang pintu gua", "Descent", "Penurunan curam ke bawah", "Abyss", "Jurang tanpa dasar", "Tunnel", "Terowongan batu", "Chamber", "Ruang kristal bawah tanah", "Hollow", "Rongga gua", "Chasm", "Celah jurang menganga", "Crevice", "Rekahan batu sempit"),
            listOf("Stalactite", "Stalaktit langit-langit", "Stalagmite", "Stalagmit lantai gua", "Column", "Pilar kapur utuh", "Dripstone", "Batu tetes mineral", "Calcite", "Mineral kalsit", "Limestone", "Batu gamping", "Vein", "Urat mineral kristal", "Cluster", "Gugusan kristal permata", "Droplet", "Tetesan air gua", "Pillar", "Tiang penyangga gua"),
            listOf("Aquifer", "Lapisan air tanah", "Subterranean", "Bawah tanah purba", "Waterfall", "Air terjun dalam gua", "Spring", "Mata air kristal", "Sump", "Genangan air dalam", "Current", "Arus air bawah tanah", "Moisture", "Kelembapan udara gua", "Reservoir", "Waduk alami bawah tanah", "Basin", "Cekungan air batu", "Torrent", "Arus deras gua"),
            listOf("Luminescence", "Pendaran cahaya misterius", "Glow", "Kilau cahaya terang", "Phosphor", "Zat fosfor bercahaya", "Fluorescence", "Fluoresensi berkilau", "Radiance", "Kemilau terang kristal", "Glimmer", "Kelap-kelip cahaya", "Iridescent", "Berkilau aneka warna", "Beam", "Sorotan sinar lampu", "Luster", "Kilau permukaan kristal", "Sparkle", "Percikan kilau permata"),
            listOf("Bat", "Kelelawar gua", "Cricket", "Jangkrik gua", "Blindfish", "Ikan buta perairan gua", "Salamander", "Salamander gua", "Fungi", "Jamur spora bawah tanah", "Spore", "Spora jamur", "Organism", "Organisme hidup unik", "Nocturnal", "Aktif di kegelapan", "Colony", "Koloni kawanan gua", "Predator", "Pemangsa kegelapan"),
            listOf("Hieroglyph", "Aksara hieroglif", "Rune", "Huruf rune kuno", "Inscription", "Prasasti batu berukir", "Crypt", "Ruang rahasia bawah tanah", "Relic", "Relikui pusaka kuno", "Artifact", "Artefak bersejarah", "Altar", "Altar batu keramat", "Tablet", "Lempengan prasasti", "Papyrus", "Gulungan naskah papirus", "Symbol", "Simbol sakral gua"),
            listOf("Headlamp", "Lampu kepala penjelajah", "Flare", "Suar darurat terang", "Lantern", "Lentera gantung gua", "Sonar", "Sensor pendeteksi sonar", "Battery", "Baterai daya cadangan", "Piton", "Paku panjat tebing baja", "Tether", "Tali pengaman regu", "Sensor", "Sensor pemindai gerak", "Mapping", "Pemetaan gua tiga dimensi", "Compass", "Kompas magnetik bawah tanah"),
            listOf("Amethyst", "Batu kecubung ungu", "Emerald", "Batu zamrud hijau", "Sapphire", "Batu safir biru", "Ruby", "Batu mirah delima merah", "Diamond", "Intan berlian berkilau", "Geode", "Batuan geoda berongga kristal", "Prism", "Prisma pemantul cahaya", "Facet", "Sisi potongan kristal", "Lode", "Lidah tambang mineral", "Refraction", "Pembiasan cahaya kristal"),
            listOf("Echo", "Gema suara pantul", "Resonance", "Resonansi nada getar", "Soundwave", "Gelombang suara", "Reverberation", "Dengungan suara berkepanjangan", "Vibration", "Getaran batu gua", "Murmur", "Bisikan arus air pelan", "Whisper", "Bisikan angin gua", "Frequency", "Frekuensi getaran", "Amplitude", "Amplitudo suara", "Tone", "Nada suara bergema"),
            listOf("Lexicon", "Gudang leksikon agung", "Mastery", "Penguasaan bahasa sempurna", "Wisdom", "Kearifan ilmu luhur", "Sanctuary", "Suaka ilmu abadi", "Treasure", "Harta karun pengetahuan", "Victory", "Kemenangan mutlak penjelajah", "Crown", "Mahkota sang juara", "Explorer", "Penjelajah sejati", "Eternity", "Keabadian ilmu bahasa", "Legacy", "Warisan kemenangan abadi")
        )
        val data = wordBank[(lvl - 1).coerceIn(0, wordBank.size - 1)]
        return (0 until 10).map { i ->
            val w = data[i * 2]
            val indo = data[i * 2 + 1]
            vocab("c_${lvl}_$i", w, indo, "Subterranean cavern concept: $w", "Noun", "/$w/", "The mysterious crystal cavern revealed the ancient $w.", "Gua kristal misterius mengungkapkan $w kuno.", "B2", "Cave")
        }
    }

    fun getTopicForLevel(stage: Int, level: Int): LevelTopicDefinition {
        return TOPICS.find { it.stage == stage && it.level == level }
            ?: TOPICS.first()
    }

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
                        ""${target.exampleSentence}""
                    } else {
                        val wrongWord = v.first { it.word != target.word }
                        ""Istilah '${target.word}' dalam log ekspedisi diartikan sebagai: '${wrongWord.englishDef}'""
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
                    val blankSentence = target.exampleSentence.replace(Regex("(?i)\\b${Regex.escape(target.word)}\\b"), "_______")
                    val displaySentence = if (blankSentence.contains("_______")) blankSentence else "${target.exampleSentence} (_______)"
                    Question(
                        id = qId,
                        type = QuestionType.FILL_BLANK,
                        prompt = "Ketik kata yang hilang untuk melengkapi catatan petualangan:",
                        subText = "\"$displaySentence\"",
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
