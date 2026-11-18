import type { LearningPath, QuestionType } from './models';
import {
  getTopicForLevel,
  INITIAL_VOCABULARY,
  type PictureOption,
  type Question,
  type VocabularyItem,
} from './vocabCatalog';

export function javaStringHashCode(str: string): number {
  let hash = 0;
  for (let i = 0; i < str.length; i += 1) {
    hash = (hash * 31 + str.charCodeAt(i)) | 0;
  }
  return hash;
}

function mulberry32(seed: number): () => number {
  let a = seed >>> 0;
  return () => {
    a |= 0;
    a = (a + 0x6d2b79f5) | 0;
    let t = Math.imul(a ^ (a >>> 15), 1 | a);
    t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

export function shuffleWithRng<T>(items: readonly T[], rng: () => number = Math.random): T[] {
  const arr = [...items];
  for (let i = arr.length - 1; i > 0; i -= 1) {
    const j = Math.floor(rng() * (i + 1));
    const tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }
  return arr;
}

export function getIconKeyForWord(word: string): string {
  const w = word.toLowerCase();
  if (
    ['compass', 'guide', 'bearing', 'direction', 'north', 'south', 'east', 'west', 'latitude', 'meridian', 'zenith'].includes(
      w
    )
  ) {
    return 'compass';
  }
  if (['map', 'trail', 'path', 'route', 'chart', 'survey', 'scroll', 'papyrus', 'inscription', 'tablet'].includes(w)) {
    return 'map';
  }
  if (['tree', 'forest', 'woods', 'branch', 'leaf', 'pine', 'fir', 'cedar', 'moss', 'lichen', 'flora', 'plant'].includes(w)) {
    return 'tree';
  }
  if (['tent', 'camp', 'shelter', 'cabin', 'hut', 'grotto', 'cavern'].includes(w)) {
    return 'tent';
  }
  if (['lantern', 'torch', 'light', 'candle', 'lamp', 'headlamp', 'beam', 'glow', 'luminescence', 'phosphor'].includes(w)) {
    return 'lantern';
  }
  if (['boots', 'shoes', 'footprint', 'track', 'step', 'stride', 'crampons', 'descent'].includes(w)) {
    return 'boots';
  }
  if (['backpack', 'gear', 'pack', 'knapsack', 'harness', 'kit', 'rope', 'thermos', 'carabiner'].includes(w)) {
    return 'backpack';
  }
  if (['fire', 'campfire', 'flame', 'smoke', 'sparks', 'blaze', 'ember', 'flare'].includes(w)) {
    return 'campfire';
  }
  const iconKeys = ['compass', 'map', 'tree', 'tent', 'lantern', 'boots', 'backpack', 'campfire'];
  return iconKeys[Math.abs(javaStringHashCode(word)) % iconKeys.length];
}

export function getIndirectClue1(target: VocabularyItem): string {
  const w = target.word.toLowerCase();
  switch (w) {
    case 'compass':
      return 'Instrumen bermagnet jarum penunjuk arah mata angin agar regu penjelajah tidak kehilangan orientasi.';
    case 'forest':
      return 'Kawasan lebat yang dipenuhi pepohonan rimbun, lumut, dan jalur jelajah yang teduh.';
    case 'path':
      return 'Lajur tapak tanah alami yang memandu para pengembara melintasi rimbunnya belantara.';
    case 'tree':
      return 'Tumbuhan berkayu tinggi menjulang dengan dahan dan kanopi daun pelindung terik.';
    case 'map':
      return 'Lembaran panduan bergambar relief rute, garis kontur daratan, dan titik koordinat ekspedisi.';
    case 'trail':
      return 'Rute setapak yang sengaja ditandai dengan patok kayu untuk pemandu kelompok pendaki.';
    case 'moss':
      return 'Lapisan flora hijau lembut tanpa bunga yang menempel lembap pada permukaan batu cadas.';
    case 'woods':
      return 'Area alam terbuka yang diselimuti tegakan pepohonan lebat tempat bersarang satwa liar.';
    case 'branch':
      return 'Bagian cabang kayu yang tumbuh menjulur keluar dari batang utama pohon.';
    case 'guide':
      return 'Sosok penunjuk arah berpengalaman yang memimpin regu ekspedisi melewati jalur tersembunyi.';
    case 'camp':
      return 'Area peristirahatan sementara di alam bebas dengan api unggun untuk bermalam.';
    case 'tent':
      return 'Struktur bertiang fleksibel dan lembaran kain kanvas sebagai tempat berteduh di alam terbuka.';
    case 'torch':
      return 'Penerang portabel berapi menyala di ujung tongkat untuk menembus lorong dan malam gelap.';
    case 'lantern':
      return 'Lampu pelindung berkerangka kuningan dengan kaca bening pemancar sinar hangat di perkemahan.';
    case 'fire':
      return 'Kobaran nyala hangat yang memancarkan cahaya dan asap untuk mengusir pemangsa malam.';
    case 'shelter':
      return 'Struktur naungan pelindung dari terpaan hujan angin di bawah ceruk tebing batu.';
    case 'timber':
      return 'Potongan batang kayu kering yang dikumpulkan untuk menjaga perapian tetap membara.';
    case 'smoke':
      return 'Gumpalan uap tipis yang membubung ke udara dari abu perapian yang menyala.';
    case 'sparks':
      return 'Percikan pijar api kecil yang berhamburan ke angkasa malam saat kayu terbakar.';
    case 'rest':
      return 'Meredakan lelah dan memulihkan stamina di dalam kantung tidur setelah seharian menjelajah.';
    case 'backpack':
      return 'Wadah perbekalan dan perlengkapan lapangan yang dipanggul kokoh di pundak penjelajah.';
    case 'rope':
      return 'Untaian jalinan serat kuat untuk menuruni lereng tebing terjal dan menambat muatan.';
    case 'boots':
      return 'Alas pelindung telapak dan pergelangan berbahan tebal kedap air untuk melintasi kerikil cadas.';
    case 'flask':
      return 'Wadah logam penyimpan cadangan air minum segar pelepas dahaga selama perjalanan jauh.';
    case 'knife':
      return 'Bilah baja tajam serbaguna untuk memotong sulur, dahan perintang, atau meraut pasak.';
    case 'strap':
      return 'Sabuk pengikat dari kulit atau anyaman serat sintetis untuk mengencangkan beban ransel.';
    case 'buckle':
      return 'Pengait gesper logam yang mengunci rapat ujung sabuk dan tali penahan beban.';
    case 'pouch':
      return 'Kantong kecil fleksibel untuk menyimpan kompas mini, geretan, atau perkakas berharga.';
    case 'whistle':
      return 'Alat peniup bersuara melengking keras untuk mengirim sinyal marabahaya saat terpisah.';
    case 'gear':
      return 'Kumpulan segala peralatan dan perkakas penting yang wajib dibawa selama ekspedisi.';
    case 'river':
      return 'Aliran air tawar alami berarus deras yang melintasi lembah menuju samudra lepas.';
    case 'bridge':
      return 'Struktur titian kokoh yang melintang di atas jurang atau perairan agar regu dapat menyeberang.';
    case 'stream':
      return 'Aliran air jernih sempit bergemericik di sela-sela bebatuan pegunungan.';
    case 'crossing':
      return 'Titik dangkal berdasar cadas tempat regu dapat melangkah melintasi aliran arus.';
    case 'current':
      return 'Dorongan energi alami pergerakan aliran air deras yang mengalir kuat di jeram.';
    case 'bank':
      return 'Tepian tanah berumput landai di kanan-kiri yang membatasi badan perairan sungai.';
    case 'plank':
      return 'Bantalan papan kayu tebal yang disusun rapi membentuk lantai titian gantung.';
    case 'waterfall':
      return 'Limpahan aliran deras yang terjun bebas dari ketinggian tebing batu cadas.';
    case 'summit':
    case 'peak':
      return 'Titik elevasi paling menjulang di atas punggung formasi batuan tinggi.';
    case 'cliff':
      return 'Dinding batuan vertikal yang berdiri curam di tepi ngarai jurang dalam.';
    case 'cavern':
      return 'Rongga megah di perut bumi yang terbentuk dari pengikisan batuan kapur selama jutaan tahun.';
    case 'headlamp':
      return 'Lampu sorot penerang yang diikatkan pada helm pelindung untuk membebaskan kedua tangan.';
    case 'lexicon':
      return 'Khazanah perbendaharaan istilah bahasa yang dikuasai oleh penjelajah ulung.';
    default: {
      const cat = target.category.toLowerCase();
      if (cat.includes('navigation')) {
        return 'Instrumen atau panduan penting yang digunakan regu pengembara untuk memetakan arah.';
      }
      if (cat.includes('equipment') || cat.includes('gear')) {
        return 'Perlengkapan lapangan berdaya tahan tinggi yang membantu petualang menghadapi rintangan.';
      }
      if (cat.includes('nature')) {
        return 'Unsur bentang alam atau fitur lingkungan yang ditemui di jalur penjelajahan.';
      }
      if (cat.includes('cave')) {
        return 'Unsur geologis atau bentukan rongga bawah tanah di kedalaman bumi.';
      }
      if (cat.includes('mountain')) {
        return 'Fitur dataran tinggi alpine atau fenomena cuaca ekstrem di kawasan perbukitan.';
      }
      return 'Konsep ekspedisi penting yang memperkaya wawasan penjelajah dalam mencatat log perjalanan.';
    }
  }
}

export function getIndirectClue2(target: VocabularyItem): string {
  const w = target.word.toLowerCase();
  const len = target.word.length;
  let context = 'Diperlukan dalam catatan log penjelajahan untuk menyelesaikan misi ekspedisi.';
  switch (w) {
    case 'compass':
      context = 'Diperiksa penjelajah saat mencocokkan arah jarum dengan peta perkamen.';
      break;
    case 'boots':
      context = 'Dikenakan pada kaki sebelum melangkah menyusuri bebatuan terjal dan lumpur.';
      break;
    case 'backpack':
      context = 'Dipanggul di punggung untuk memuat perbekalan dan kompas navigasi.';
      break;
    case 'rope':
      context = 'Digulung rapi dan diikatkan pada sabuk pengaman sebelum memanjat tebing.';
      break;
    case 'tent':
      context = 'Ditegakkan di tanah datar sebelum kabut malam menyelimuti pos perkemahan.';
      break;
    case 'torch':
      context = 'Dinyalakan di kegelapan malam saat obor minyak menerangi rute gelap.';
      break;
    case 'map':
      context = 'Dibentangkan di atas batu datar untuk merancang rute ekspedisi berikutnya.';
      break;
    case 'river':
      context = 'Arus airnya deras melintasi lembah hutan menuju ke samudra lepas.';
      break;
    case 'bridge':
      context = 'Dilewati satu per satu oleh penjelajah untuk menyeberangi ngarai sungai.';
      break;
    case 'knife':
      context = 'Diselipkan di saku sabuk sebagai bilah serbaguna untuk memotong rintangan.';
      break;
    case 'flask':
      context = 'Diisi di mata air tawar pegunungan untuk cadangan minum sepanjang hari.';
      break;
    case 'summit':
      context = 'Dipancangkan bendera penjelajah saat berhasil mencapai puncak tertinggi.';
      break;
    case 'cavern':
      context = 'Ditelusuri menggunakan lampu kepala untuk mengamati formasi batuan purba.';
      break;
  }
  return `${context} Panjang kata: ${len} huruf.`;
}

export interface CategorySortSetup {
  bucket1Name: string;
  bucket1Words: string[];
  bucket2Name: string;
  bucket2Words: string[];
}

export function getCategoryBucketsForLevel(
  stage: number,
  level: number,
  words: VocabularyItem[]
): CategorySortSetup {
  const key = `${stage}-${level}`;
  switch (key) {
    case '1-1':
      return {
        bucket1Name: 'Alat Navigasi & Panduan',
        bucket1Words: ['Compass', 'Map'],
        bucket2Name: 'Bentang Alam & Vegetasi',
        bucket2Words: ['Forest', 'Tree'],
      };
    case '1-2':
      return {
        bucket1Name: 'Perkemahan & Hunian',
        bucket1Words: ['Camp', 'Tent'],
        bucket2Name: 'Penerangan & Sumber Api',
        bucket2Words: ['Torch', 'Lantern'],
      };
    case '1-3':
      return {
        bucket1Name: 'Perlengkapan Membawa & Memakai',
        bucket1Words: ['Backpack', 'Boots'],
        bucket2Name: 'Peralatan Tali & Perkakas',
        bucket2Words: ['Rope', 'Knife'],
      };
    case '1-4':
      return {
        bucket1Name: 'Arus & Jalur Perairan',
        bucket1Words: ['River', 'Stream'],
        bucket2Name: 'Struktur Titian Jalur',
        bucket2Words: ['Bridge', 'Crossing'],
      };
    case '1-5':
      return {
        bucket1Name: 'Satwa & Fauna Rimba',
        bucket1Words: ['Wolf', 'Bear'],
        bucket2Name: 'Lingkungan & Sarang Alami',
        bucket2Words: ['Den', 'Cave'],
      };
    case '1-6':
      return {
        bucket1Name: 'Flora & Tanaman Liar',
        bucket1Words: ['Vine', 'Fern'],
        bucket2Name: 'Formasi Batuan Medan',
        bucket2Words: ['Stone', 'Rock'],
      };
    case '1-7':
      return {
        bucket1Name: 'Peralatan Komunikasi',
        bucket1Words: ['Signal', 'Mirror'],
        bucket2Name: 'Kondisi Cuaca Rimba',
        bucket2Words: ['Rain', 'Fog'],
      };
    case '1-8':
      return {
        bucket1Name: 'Instrumen Observasi',
        bucket1Words: ['Scope', 'Lens'],
        bucket2Name: 'Fenomena Langit',
        bucket2Words: ['Star', 'Moon'],
      };
    case '1-9':
      return {
        bucket1Name: 'Logistik & Makanan',
        bucket1Words: ['Ration', 'Berry'],
        bucket2Name: 'Alat Masak Lapangan',
        bucket2Words: ['Pot', 'Kettle'],
      };
    case '1-10':
      return {
        bucket1Name: 'Pusaka & Peta Wilayah',
        bucket1Words: ['Relic', 'Scroll'],
        bucket2Name: 'Penjaga & Pemandu Rimba',
        bucket2Words: ['Ranger', 'Scout'],
      };
    default: {
      const w1 = [words[0]?.word ?? 'Item 1', words[1]?.word ?? 'Item 2'];
      const w2 = [words[2]?.word ?? 'Item 3', words[3]?.word ?? 'Item 4'];
      switch (stage) {
        case 2:
          return {
            bucket1Name: 'Navigasi & Kemudi Bahari',
            bucket1Words: w1,
            bucket2Name: 'Biota & Lautan Tropis',
            bucket2Words: w2,
          };
        case 3:
          return {
            bucket1Name: 'Arsitektur & Peninggalan Klasik',
            bucket1Words: w1,
            bucket2Name: 'Masyarakat & Lembaga Sipil',
            bucket2Words: w2,
          };
        case 4:
          return {
            bucket1Name: 'Peralatan Alpine & Perlindungan',
            bucket1Words: w1,
            bucket2Name: 'Relief & Cuaca Pegunungan',
            bucket2Words: w2,
          };
        case 5:
          return {
            bucket1Name: 'Mineral Kristal & Formasi Batuan',
            bucket1Words: w1,
            bucket2Name: 'Eksplorasi & Perkakas Gua',
            bucket2Words: w2,
          };
        default:
          return {
            bucket1Name: 'Divisi Ekspedisi A',
            bucket1Words: w1,
            bucket2Name: 'Divisi Ekspedisi B',
            bucket2Words: w2,
          };
      }
    }
  }
}

function escapeRegExp(text: string): string {
  return text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

export function generateTenQuestionsForLevel(
  path: LearningPath,
  stage: number,
  level: number
): Question[] {
  const topic = getTopicForLevel(stage, level);
  const v = topic.vocabulary;
  const lvlIdx = (stage - 1) * 10 + (level - 1);
  const hintsEnabled = !(stage === 5 && level == 10);

  const recPermutations: QuestionType[][] = [
    ['PICTURE_TAP', 'MULTIPLE_CHOICE', 'TRUE_OR_FALSE', 'DRAG_AND_MATCH'],
    ['MULTIPLE_CHOICE', 'DRAG_AND_MATCH', 'PICTURE_TAP', 'TRUE_OR_FALSE'],
    ['TRUE_OR_FALSE', 'PICTURE_TAP', 'DRAG_AND_MATCH', 'MULTIPLE_CHOICE'],
    ['DRAG_AND_MATCH', 'TRUE_OR_FALSE', 'MULTIPLE_CHOICE', 'PICTURE_TAP'],
  ];

  const manPermutations: QuestionType[][] = [
    ['CATEGORY_SORT', 'WORD_HUNT', 'SCRAMBLED_LETTERS', 'SENTENCE_BUILDER'],
    ['SCRAMBLED_LETTERS', 'SENTENCE_BUILDER', 'CATEGORY_SORT', 'WORD_HUNT'],
    ['WORD_HUNT', 'CATEGORY_SORT', 'SENTENCE_BUILDER', 'SCRAMBLED_LETTERS'],
    ['SENTENCE_BUILDER', 'SCRAMBLED_LETTERS', 'WORD_HUNT', 'CATEGORY_SORT'],
  ];

  const prodPermutations: QuestionType[][] = [
    ['LISTEN_AND_TYPE', 'FILL_BLANK'],
    ['FILL_BLANK', 'LISTEN_AND_TYPE'],
  ];

  const recTypes = [...recPermutations[lvlIdx % recPermutations.length]];
  const manTypes = [...manPermutations[lvlIdx % manPermutations.length]];
  const prodTypes = [...prodPermutations[lvlIdx % prodPermutations.length]];

  for (let i = 0; i < 4; i += 1) {
    const qType = manTypes[i];
    const word = v[4 + i].word;
    if (qType === 'WORD_HUNT' && word.length > 8) {
      const swapIdx = [0, 1, 2, 3].find(
        (j) => j !== i && v[4 + j].word.length <= 8 && manTypes[j] !== 'WORD_HUNT'
      );
      if (swapIdx !== undefined) {
        const tmp = manTypes[i];
        manTypes[i] = manTypes[swapIdx];
        manTypes[swapIdx] = tmp;
      }
    }
  }

  for (let i = 0; i < 4; i += 1) {
    const qType = manTypes[i];
    const word = v[4 + i].word;
    if (qType === 'SCRAMBLED_LETTERS' && word.length < 4) {
      const swapIdx = [0, 1, 2, 3].find(
        (j) => j !== i && v[4 + j].word.length >= 4 && manTypes[j] !== 'SCRAMBLED_LETTERS'
      );
      if (swapIdx !== undefined) {
        const tmp = manTypes[i];
        manTypes[i] = manTypes[swapIdx];
        manTypes[swapIdx] = tmp;
      }
    }
  }

  const allTypes: QuestionType[] = [...recTypes, ...manTypes, ...prodTypes];

  return Array.from({ length: 10 }, (_, i): Question => {
    const target = v[i];
    const qType = allTypes[i];
    const qNum = i + 1;
    const qId = `q_${path}_s${stage}_l${level}_${qNum}`;
    const clue1 = getIndirectClue1(target);
    const clue2 = getIndirectClue2(target);

    switch (qType) {
      case 'MULTIPLE_CHOICE': {
        const distractors = shuffleWithRng(v.filter((item) => item.word !== target.word))
          .slice(0, 3)
          .map((item) => item.word);
        const options = shuffleWithRng([...distractors, target.word]);
        return {
          id: qId,
          type: 'MULTIPLE_CHOICE',
          prompt: `Pilih istilah petualangan yang tepat untuk: '${target.englishDef}':`,
          subText: clue1,
          targetWord: target.word,
          options,
          correctAnswer: target.word,
          clue: clue1,
          clue2,
          hintsEnabled,
          explanation: `'${target.word}' berarti '${target.indonesian}'. Contoh dalam ekspedisi: ${target.exampleSentence}`,
        };
      }

      case 'PICTURE_TAP': {
        const otherVocabs = shuffleWithRng(v.filter((item) => item.word !== target.word)).slice(0, 3);
        const optsList = shuffleWithRng([target, ...otherVocabs]);
        const pictureOptions: PictureOption[] = optsList.map((item, idx) => ({
          id: `opt_${idx + 1}`,
          vectorIconKey: getIconKeyForWord(item.word),
          label: `${item.word} (${item.indonesian})`,
        }));
        const correctLabel = `${target.word} (${target.indonesian})`;
        return {
          id: qId,
          type: 'PICTURE_TAP',
          prompt: `Ketuk spesimen petualangan untuk konsep: '${target.englishDef}':`,
          subText: clue1,
          targetWord: target.word,
          options: [],
          pictureOptions,
          correctAnswer: correctLabel,
          clue: clue1,
          clue2,
          hintsEnabled,
          explanation: `'${target.word}' adalah ${target.indonesian}. Definisi: ${target.englishDef}.`,
        };
      }

      case 'TRUE_OR_FALSE': {
        const isTrue = (lvlIdx + i) % 2 === 0;
        const wrongWord = v.find((item) => item.word !== target.word) ?? v[0];
        const statement = isTrue
          ? `"${target.exampleSentence}"`
          : `"Istilah '${target.word}' dalam log ekspedisi diartikan sebagai: '${wrongWord.englishDef}'"`;
        const explanation = isTrue
          ? `Pernyataan BENAR. '${target.word}' berarti '${target.indonesian}'. ${target.englishDef}.`
          : `Pernyataan SALAH. '${target.word}' sebenarnya berarti '${target.indonesian}' (${target.englishDef}).`;
        return {
          id: qId,
          type: 'TRUE_OR_FALSE',
          prompt: 'Tentukan kebenaran pernyataan log petualangan berikut:',
          subText: statement,
          targetWord: target.word,
          options: [],
          isStatementTrue: isTrue,
          correctAnswer: isTrue ? 'BENAR' : 'SALAH',
          clue: clue1,
          clue2,
          hintsEnabled,
          explanation,
        };
      }

      case 'DRAG_AND_MATCH': {
        const others = v.filter((item) => item.word !== target.word).slice(0, 2);
        const matchVocabs = [target, ...others];
        const matchingPairs: Record<string, string> = {};
        for (const item of matchVocabs) {
          matchingPairs[item.word] = item.indonesian;
        }
        return {
          id: qId,
          type: 'DRAG_AND_MATCH',
          prompt: `Pasangkan istilah petualangan wilayah '${topic.topicTitle}' dengan artinya:`,
          subText: clue1,
          targetWord: target.word,
          options: [],
          matchingPairs,
          correctAnswer: matchVocabs.map((m) => `${m.word} -> ${m.indonesian}`).join(', '),
          clue: clue1,
          clue2,
          hintsEnabled,
          explanation:
            'Pasangan benar: ' + matchVocabs.map((m) => `'${m.word}' = '${m.indonesian}'`).join('; '),
        };
      }

      case 'CATEGORY_SORT': {
        const setup = getCategoryBucketsForLevel(stage, level, v);
        const failureFeedback =
          `Klasifikasi belum tepat: ${setup.bucket1Words.join(', ')} masuk ke kategori '${setup.bucket1Name}', ` +
          `sedangkan ${setup.bucket2Words.join(', ')} masuk ke '${setup.bucket2Name}'.`;
        const explanation =
          `Tepat sekali! ${setup.bucket1Words.join(', ')} berhasil diklasifikasikan ke '${setup.bucket1Name}', ` +
          `dan ${setup.bucket2Words.join(', ')} ke '${setup.bucket2Name}'.`;
        return {
          id: qId,
          type: 'CATEGORY_SORT',
          prompt: `Sortir kosakata wilayah '${topic.topicTitle}' ke dalam divisi kategori:`,
          subText: 'Kelompokkan istilah ke dalam klasifikasi yang sesuai',
          targetWord: target.word,
          options: [],
          categoryBuckets: {
            [setup.bucket1Name]: setup.bucket1Words,
            [setup.bucket2Name]: setup.bucket2Words,
          },
          correctAnswer: 'Sorted into categories',
          clue: clue1,
          clue2,
          hintsEnabled,
          failureFeedback,
          explanation,
        };
      }

      case 'WORD_HUNT': {
        const wordUpper = target.word.toUpperCase().slice(0, 8);
        const gridSize = wordUpper.length <= 6 ? 6 : 8;
        const gridArray: string[][] = Array.from({ length: gridSize }, () =>
          Array.from({ length: gridSize }, () => ' ')
        );

        const dir = ((stage * 13 + level * 7 + javaStringHashCode(wordUpper)) & 0x7fffffff) % 3;
        const dr = dir === 1 || dir === 2 ? 1 : 0;
        const dc = dir === 0 || dir === 2 ? 1 : 0;

        const maxR = gridSize - 1 - (wordUpper.length - 1) * dr;
        const maxC = gridSize - 1 - (wordUpper.length - 1) * dc;
        const r0 = maxR > 0 ? ((stage * 17 + level * 3) & 0x7fffffff) % (maxR + 1) : 0;
        const c0 = maxC > 0 ? ((stage * 11 + level * 5) & 0x7fffffff) % (maxC + 1) : 0;

        for (let k = 0; k < wordUpper.length; k += 1) {
          gridArray[r0 + dr * k][c0 + dc * k] = wordUpper[k];
        }

        const rng = mulberry32(lvlIdx * 31 + javaStringHashCode(target.word));
        for (let r = 0; r < gridSize; r += 1) {
          for (let c = 0; c < gridSize; c += 1) {
            if (gridArray[r][c] === ' ') {
              gridArray[r][c] = String.fromCharCode(65 + Math.floor(rng() * 26));
            }
          }
        }

        return {
          id: qId,
          type: 'WORD_HUNT',
          prompt: `Temukan istilah petualangan tersembunyi (${wordUpper.length} huruf):`,
          subText: clue1,
          targetWord: target.word,
          options: [],
          gridLetters: gridArray,
          correctAnswer: wordUpper,
          clue: clue1,
          clue2,
          hintsEnabled,
          explanation: `'${target.word}' (${target.indonesian}) berhasil ditemukan di dalam kisi huruf! Definisi: ${target.englishDef}.`,
        };
      }

      case 'SCRAMBLED_LETTERS': {
        const wordUpper = target.word.toUpperCase();
        const letters = wordUpper.split('');
        let scrambled = shuffleWithRng(letters);
        let tries = 0;
        while (scrambled.join('') === wordUpper && tries < 15) {
          scrambled = shuffleWithRng(letters);
          tries += 1;
        }
        if (scrambled.join('') === wordUpper && scrambled.length > 1) {
          const tmp = scrambled[0];
          scrambled[0] = scrambled[1];
          scrambled[1] = tmp;
        }
        return {
          id: qId,
          type: 'SCRAMBLED_LETTERS',
          prompt: 'Susun kepingan huruf acak menjadi istilah petualangan:',
          subText: clue1,
          targetWord: target.word,
          options: [],
          scrambledLetters: scrambled,
          correctAnswer: wordUpper,
          clue: clue1,
          clue2,
          hintsEnabled,
          explanation: `'${target.word}' (${target.indonesian}) tersusun dengan benar! Contoh: ${target.exampleSentence}`,
        };
      }

      case 'SENTENCE_BUILDER': {
        const sentence = target.exampleSentence.trim();
        const rawTokens = sentence.split(/\s+/).filter((t) => t.length > 0);
        let tokens = shuffleWithRng(rawTokens);
        if (tokens.join(' ') === rawTokens.join(' ') && tokens.length > 1) {
          const tmp = tokens[0];
          tokens[0] = tokens[1];
          tokens[1] = tmp;
        }
        return {
          id: qId,
          type: 'SENTENCE_BUILDER',
          prompt: 'Susun kepingan kata berikut menjadi kalimat catatan ekspedisi yang utuh:',
          subText: target.exampleTranslation,
          targetWord: target.word,
          options: [],
          sentenceTokens: tokens,
          orderedSentence: sentence,
          correctAnswer: sentence,
          clue: clue1,
          clue2,
          hintsEnabled,
          explanation: `Susunan kalimat yang tepat: '${sentence}'`,
        };
      }

      case 'LISTEN_AND_TYPE': {
        return {
          id: qId,
          type: 'LISTEN_AND_TYPE',
          prompt: 'Dengarkan pelafalan penjelajah lalu ketik kata yang kamu dengar:',
          subText: 'Ketuk tombol suara perunggu untuk memutar ulang audio',
          targetWord: target.word,
          options: [],
          audioPromptWord: target.word.toLowerCase(),
          hint: clue1,
          correctAnswer: target.word.toLowerCase(),
          clue: clue1,
          clue2,
          hintsEnabled,
          explanation: `Kata yang dilafalkan adalah '${target.word}' (${target.pronunciation}), yang berarti '${target.indonesian}'.`,
        };
      }

      case 'FILL_BLANK':
      default: {
        const regex = new RegExp(`\\b${escapeRegExp(target.word)}\\b`, 'i');
        const replaced = target.exampleSentence.replace(regex, '_______');
        const displaySentence = replaced.includes('_______')
          ? replaced
          : `${target.exampleSentence} (_______)`;
        return {
          id: qId,
          type: 'FILL_BLANK',
          prompt: 'Ketik kata yang hilang untuk melengkapi catatan petualangan:',
          subText: `"${displaySentence}"`,
          sentenceWithBlank: displaySentence,
          targetWord: target.word,
          options: [],
          hint: clue1,
          correctAnswer: target.word,
          clue: clue1,
          clue2,
          hintsEnabled,
          explanation: `Kata yang tepat adalah '${target.word}' (${target.indonesian}): ${target.exampleSentence}`,
        };
      }
    }
  });
}

export function generateRemedialQuestion(original: Question): Question {
  const target =
    INITIAL_VOCABULARY.find((item) => item.word.toLowerCase() === original.targetWord.toLowerCase()) ??
    INITIAL_VOCABULARY[0];

  const sentence = target.exampleSentence.trim();
  const regex = new RegExp(`\\b${escapeRegExp(target.word)}\\b`, 'i');
  const masked = sentence.replace(regex, '_______');
  const displaySentence = masked.includes('_______') ? masked : `${sentence} (_______)`;

  const distractors = shuffleWithRng(
    INITIAL_VOCABULARY.filter((item) => item.word.toLowerCase() !== target.word.toLowerCase())
  )
    .slice(0, 3)
    .map((item) => item.word);

  const options = shuffleWithRng([target.word, ...distractors]);

  return {
    id: `remedial_${original.id}`,
    type: 'SENTENCE_COMPLETION',
    prompt: `Latihan Penguatan Kata '${target.word}' (${target.indonesian}):`,
    subText: `"${displaySentence}"`,
    options,
    correctAnswer: target.word,
    explanation: `'${target.word}' berarti '${target.indonesian}'. Contoh: ${target.exampleSentence}`,
    targetWord: target.word,
    hintsEnabled: false,
  };
}

export function getDailyWordsOfTheDay(vocabList: VocabularyItem[], dateStr: string): VocabularyItem[] {
  if (vocabList.length === 0) return [];
  const count = 5;
  const dateHash = Math.abs(javaStringHashCode(dateStr));
  const rng = mulberry32(dateHash);
  const sorted = [...vocabList].sort((a, b) => a.id.localeCompare(b.id));
  return shuffleWithRng(sorted, rng).slice(0, count);
}
