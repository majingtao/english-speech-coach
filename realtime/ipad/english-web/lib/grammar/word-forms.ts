export type VerbKind = "irregular" | "regular"

export interface VerbForm {
  id: string
  base: string
  past: string
  participle: string
  zh: string
  kind: VerbKind
  pastSentence: string
  perfectSentence: string
  note?: string
}

export interface ComparisonForm {
  id: string
  base: string
  comparative: string
  superlative: string
  zh: string
  rule: "直接加 -er/-est" | "结尾 e 加 -r/-st" | "双写末尾辅音" | "y 变 i" | "more / most" | "不规则变化"
  comparativeSentence: string
  superlativeSentence: string
  note?: string
}

export const VERBS: VerbForm[] = [
  { id: "be", base: "be", past: "was / were", participle: "been", zh: "是；在", kind: "irregular", pastSentence: "Mia ___ at home yesterday.", perfectSentence: "I have ___ to London twice.", note: "I/he/she/it was；you/we/they were。have been to 表示去过并已回来。" },
  { id: "become", base: "become", past: "became", participle: "become", zh: "变成", kind: "irregular", pastSentence: "The sky ___ dark.", perfectSentence: "She has ___ more confident.", },
  { id: "begin", base: "begin", past: "began", participle: "begun", zh: "开始", kind: "irregular", pastSentence: "The lesson ___ at nine.", perfectSentence: "The film has already ___." },
  { id: "break", base: "break", past: "broke", participle: "broken", zh: "打破；弄坏", kind: "irregular", pastSentence: "Leo ___ his glasses yesterday.", perfectSentence: "Someone has ___ the window." },
  { id: "build", base: "build", past: "built", participle: "built", zh: "建造", kind: "irregular", pastSentence: "They ___ this bridge in 2010.", perfectSentence: "We have ___ a model plane." },
  { id: "burn", base: "burn", past: "burnt / burned", participle: "burnt / burned", zh: "燃烧；烧伤", kind: "irregular", pastSentence: "I ___ my hand on the pan.", perfectSentence: "The sun has ___ my face.", note: "burnt 和 burned 都正确；英式英语常用 burnt。" },
  { id: "buy", base: "buy", past: "bought", participle: "bought", zh: "买", kind: "irregular", pastSentence: "Dad ___ some fruit yesterday.", perfectSentence: "We have ___ the tickets." },
  { id: "catch", base: "catch", past: "caught", participle: "caught", zh: "抓住；赶上", kind: "irregular", pastSentence: "I ___ the last bus home.", perfectSentence: "The cat has ___ a mouse." },
  { id: "choose", base: "choose", past: "chose", participle: "chosen", zh: "选择", kind: "irregular", pastSentence: "Amy ___ the blue dress.", perfectSentence: "Have you ___ a team yet?" },
  { id: "come", base: "come", past: "came", participle: "come", zh: "来", kind: "irregular", pastSentence: "Ben ___ to my party.", perfectSentence: "Spring has ___ early this year." },
  { id: "cost", base: "cost", past: "cost", participle: "cost", zh: "花费", kind: "irregular", pastSentence: "The book ___ ten pounds.", perfectSentence: "The trip has ___ us a lot." },
  { id: "cut", base: "cut", past: "cut", participle: "cut", zh: "切；割", kind: "irregular", pastSentence: "She ___ the paper carefully.", perfectSentence: "I have ___ the cake into six pieces." },
  { id: "do", base: "do", past: "did", participle: "done", zh: "做", kind: "irregular", pastSentence: "We ___ our homework after dinner.", perfectSentence: "Tom has already ___ the washing-up." },
  { id: "draw", base: "draw", past: "drew", participle: "drawn", zh: "画；拉", kind: "irregular", pastSentence: "Lucy ___ a picture of her dog.", perfectSentence: "He has ___ a map for us." },
  { id: "dream", base: "dream", past: "dreamt / dreamed", participle: "dreamt / dreamed", zh: "做梦；梦想", kind: "irregular", pastSentence: "I ___ about flying last night.", perfectSentence: "She has always ___ of being a pilot.", note: "dreamt 和 dreamed 都正确。" },
  { id: "drink", base: "drink", past: "drank", participle: "drunk", zh: "喝", kind: "irregular", pastSentence: "He ___ all the orange juice.", perfectSentence: "I have never ___ coffee." },
  { id: "drive", base: "drive", past: "drove", participle: "driven", zh: "驾驶", kind: "irregular", pastSentence: "Mum ___ us to school.", perfectSentence: "Have you ever ___ a van?" },
  { id: "eat", base: "eat", past: "ate", participle: "eaten", zh: "吃", kind: "irregular", pastSentence: "We ___ pizza on Friday.", perfectSentence: "Sam has already ___ lunch." },
  { id: "fall", base: "fall", past: "fell", participle: "fallen", zh: "落下；跌倒", kind: "irregular", pastSentence: "The cup ___ onto the floor.", perfectSentence: "A lot of snow has ___." },
  { id: "feel", base: "feel", past: "felt", participle: "felt", zh: "感觉", kind: "irregular", pastSentence: "I ___ tired after the race.", perfectSentence: "Have you ever ___ this happy?" },
  { id: "find", base: "find", past: "found", participle: "found", zh: "找到；发现", kind: "irregular", pastSentence: "Ella ___ her keys under the sofa.", perfectSentence: "We have ___ a better way." },
  { id: "fly", base: "fly", past: "flew", participle: "flown", zh: "飞", kind: "irregular", pastSentence: "The bird ___ out of the window.", perfectSentence: "I have never ___ in a helicopter." },
  { id: "forget", base: "forget", past: "forgot", participle: "forgotten", zh: "忘记", kind: "irregular", pastSentence: "I ___ my umbrella yesterday.", perfectSentence: "She has ___ my name." },
  { id: "get", base: "get", past: "got", participle: "got / gotten", zh: "得到；到达", kind: "irregular", pastSentence: "We ___ home at six.", perfectSentence: "I have ___ much better at tennis.", note: "英式英语通常用 got；美式英语也用 gotten。" },
  { id: "give", base: "give", past: "gave", participle: "given", zh: "给", kind: "irregular", pastSentence: "My aunt ___ me this watch.", perfectSentence: "The teacher has ___ us extra time." },
  { id: "go", base: "go", past: "went", participle: "gone", zh: "去", kind: "irregular", pastSentence: "They ___ to the museum yesterday.", perfectSentence: "Dad has ___ to the supermarket.", note: "has gone to 表示去了还没回来；has been to 表示去过并已回来。" },
  { id: "grow", base: "grow", past: "grew", participle: "grown", zh: "生长；种植", kind: "irregular", pastSentence: "The puppy ___ very quickly.", perfectSentence: "We have ___ tomatoes in the garden." },
  { id: "have", base: "have", past: "had", participle: "had", zh: "有；吃；经历", kind: "irregular", pastSentence: "I ___ cereal for breakfast.", perfectSentence: "We have ___ a wonderful day." },
  { id: "hear", base: "hear", past: "heard", participle: "heard", zh: "听见", kind: "irregular", pastSentence: "I ___ a strange noise outside.", perfectSentence: "Have you ___ the news?" },
  { id: "hit", base: "hit", past: "hit", participle: "hit", zh: "击；碰撞", kind: "irregular", pastSentence: "The ball ___ the wall.", perfectSentence: "The storm has ___ the coast." },
  { id: "hurt", base: "hurt", past: "hurt", participle: "hurt", zh: "伤害；疼痛", kind: "irregular", pastSentence: "Jack ___ his knee in the match.", perfectSentence: "I have ___ my back." },
  { id: "keep", base: "keep", past: "kept", participle: "kept", zh: "保留；保持", kind: "irregular", pastSentence: "She ___ the letter for years.", perfectSentence: "I have ___ your secret." },
  { id: "know", base: "know", past: "knew", participle: "known", zh: "知道；认识", kind: "irregular", pastSentence: "I ___ the answer at once.", perfectSentence: "We have ___ each other for five years." },
  { id: "learn", base: "learn", past: "learnt / learned", participle: "learnt / learned", zh: "学习；学会", kind: "irregular", pastSentence: "We ___ about space at school.", perfectSentence: "I have ___ ten new words today.", note: "learnt 和 learned 都正确；英式英语常用 learnt。" },
  { id: "leave", base: "leave", past: "left", participle: "left", zh: "离开；留下", kind: "irregular", pastSentence: "The train ___ at seven.", perfectSentence: "Someone has ___ a bag here." },
  { id: "let", base: "let", past: "let", participle: "let", zh: "让；允许", kind: "irregular", pastSentence: "Mum ___ me stay up late.", perfectSentence: "They have ___ us use their garden." },
  { id: "lie", base: "lie", past: "lied", participle: "lied", zh: "撒谎", kind: "irregular", pastSentence: "He ___ about his age.", perfectSentence: "She has never ___ to me.", note: "这里是“撒谎”。表示“躺”时是 lie – lay – lain。" },
  { id: "lose", base: "lose", past: "lost", participle: "lost", zh: "丢失；输掉", kind: "irregular", pastSentence: "Our team ___ the game.", perfectSentence: "I have ___ my phone." },
  { id: "make", base: "make", past: "made", participle: "made", zh: "制作；使得", kind: "irregular", pastSentence: "We ___ a cake yesterday.", perfectSentence: "Ella has ___ a new friend." },
  { id: "mean", base: "mean", past: "meant", participle: "meant", zh: "意思是；意味着", kind: "irregular", pastSentence: "I ___ to call you.", perfectSentence: "This has ___ a lot to me." },
  { id: "meet", base: "meet", past: "met", participle: "met", zh: "遇见；会面", kind: "irregular", pastSentence: "I ___ Ali at the station.", perfectSentence: "Have you ever ___ a famous person?" },
  { id: "pay", base: "pay", past: "paid", participle: "paid", zh: "支付", kind: "irregular", pastSentence: "Dad ___ for the meal.", perfectSentence: "We have already ___ for the tickets." },
  { id: "put", base: "put", past: "put", participle: "put", zh: "放", kind: "irregular", pastSentence: "I ___ the milk in the fridge.", perfectSentence: "She has ___ her coat on." },
  { id: "read", base: "read", past: "read", participle: "read", zh: "阅读", kind: "irregular", pastSentence: "I ___ that story last week.", perfectSentence: "Have you ___ this book?", note: "原形读 /riːd/；过去式和过去分词读 /red/。" },
  { id: "ride", base: "ride", past: "rode", participle: "ridden", zh: "骑", kind: "irregular", pastSentence: "We ___ our bikes in the park.", perfectSentence: "Have you ever ___ a horse?" },
  { id: "run", base: "run", past: "ran", participle: "run", zh: "跑；经营", kind: "irregular", pastSentence: "Leo ___ five kilometres yesterday.", perfectSentence: "She has ___ three races this year." },
  { id: "say", base: "say", past: "said", participle: "said", zh: "说", kind: "irregular", pastSentence: "He ___ hello to everyone.", perfectSentence: "I have already ___ sorry." },
  { id: "see", base: "see", past: "saw", participle: "seen", zh: "看见", kind: "irregular", pastSentence: "We ___ a rainbow yesterday.", perfectSentence: "I have never ___ a whale." },
  { id: "sell", base: "sell", past: "sold", participle: "sold", zh: "卖", kind: "irregular", pastSentence: "They ___ their old car.", perfectSentence: "The shop has ___ all the cakes." },
  { id: "send", base: "send", past: "sent", participle: "sent", zh: "发送；寄", kind: "irregular", pastSentence: "I ___ you an email yesterday.", perfectSentence: "She has ___ the parcel." },
  { id: "show", base: "show", past: "showed", participle: "shown", zh: "展示", kind: "irregular", pastSentence: "Max ___ me his new bike.", perfectSentence: "The guide has ___ us the old castle." },
  { id: "shut", base: "shut", past: "shut", participle: "shut", zh: "关闭", kind: "irregular", pastSentence: "She ___ the door quietly.", perfectSentence: "The library has ___ early today." },
  { id: "sing", base: "sing", past: "sang", participle: "sung", zh: "唱歌", kind: "irregular", pastSentence: "Maya ___ at the concert.", perfectSentence: "We have ___ this song before." },
  { id: "sit", base: "sit", past: "sat", participle: "sat", zh: "坐", kind: "irregular", pastSentence: "I ___ next to Ben.", perfectSentence: "She has ___ there all morning." },
  { id: "sleep", base: "sleep", past: "slept", participle: "slept", zh: "睡觉", kind: "irregular", pastSentence: "The baby ___ for ten hours.", perfectSentence: "I have never ___ in a tent." },
  { id: "speak", base: "speak", past: "spoke", participle: "spoken", zh: "说话", kind: "irregular", pastSentence: "I ___ to the teacher yesterday.", perfectSentence: "Have you ___ to Mia yet?" },
  { id: "spell", base: "spell", past: "spelt / spelled", participle: "spelt / spelled", zh: "拼写", kind: "irregular", pastSentence: "He ___ my name incorrectly.", perfectSentence: "You have ___ every word correctly.", note: "spelt 和 spelled 都正确；英式英语常用 spelt。" },
  { id: "spend", base: "spend", past: "spent", participle: "spent", zh: "花费；度过", kind: "irregular", pastSentence: "We ___ Sunday at the beach.", perfectSentence: "I have ___ all my pocket money." },
  { id: "stand", base: "stand", past: "stood", participle: "stood", zh: "站立", kind: "irregular", pastSentence: "They ___ outside the cinema.", perfectSentence: "He has ___ there for an hour." },
  { id: "steal", base: "steal", past: "stole", participle: "stolen", zh: "偷", kind: "irregular", pastSentence: "Someone ___ my bicycle.", perfectSentence: "A thief has ___ the painting." },
  { id: "swim", base: "swim", past: "swam", participle: "swum", zh: "游泳", kind: "irregular", pastSentence: "We ___ in the lake yesterday.", perfectSentence: "I have never ___ in the sea." },
  { id: "take", base: "take", past: "took", participle: "taken", zh: "拿；带；乘坐", kind: "irregular", pastSentence: "I ___ the bus to school.", perfectSentence: "Dad has ___ lots of photos." },
  { id: "teach", base: "teach", past: "taught", participle: "taught", zh: "教", kind: "irregular", pastSentence: "Mrs Green ___ us English last year.", perfectSentence: "She has ___ here since 2020." },
  { id: "tell", base: "tell", past: "told", participle: "told", zh: "告诉", kind: "irregular", pastSentence: "He ___ me a funny story.", perfectSentence: "I have ___ you the truth." },
  { id: "think", base: "think", past: "thought", participle: "thought", zh: "想；认为", kind: "irregular", pastSentence: "I ___ the test was easy.", perfectSentence: "Have you ___ about my idea?" },
  { id: "throw", base: "throw", past: "threw", participle: "thrown", zh: "扔", kind: "irregular", pastSentence: "Ben ___ the ball to me.", perfectSentence: "Someone has ___ my notes away." },
  { id: "understand", base: "understand", past: "understood", participle: "understood", zh: "理解", kind: "irregular", pastSentence: "I finally ___ the question.", perfectSentence: "She has always ___ me." },
  { id: "wake", base: "wake", past: "woke", participle: "woken", zh: "醒来；唤醒", kind: "irregular", pastSentence: "I ___ at six this morning.", perfectSentence: "The noise has ___ the baby." },
  { id: "wear", base: "wear", past: "wore", participle: "worn", zh: "穿；戴", kind: "irregular", pastSentence: "She ___ a red coat yesterday.", perfectSentence: "I have never ___ glasses." },
  { id: "win", base: "win", past: "won", participle: "won", zh: "赢", kind: "irregular", pastSentence: "Our team ___ the final.", perfectSentence: "Mia has ___ three prizes." },
  { id: "write", base: "write", past: "wrote", participle: "written", zh: "写", kind: "irregular", pastSentence: "I ___ a postcard yesterday.", perfectSentence: "She has ___ three emails." },
  { id: "play", base: "play", past: "played", participle: "played", zh: "玩；演奏", kind: "regular", pastSentence: "We ___ football after school.", perfectSentence: "I have ___ this game before." },
  { id: "watch", base: "watch", past: "watched", participle: "watched", zh: "观看", kind: "regular", pastSentence: "They ___ a film last night.", perfectSentence: "We have already ___ that video." },
  { id: "live", base: "live", past: "lived", participle: "lived", zh: "居住", kind: "regular", pastSentence: "I ___ in York as a child.", perfectSentence: "She has ___ here for two years." },
  { id: "study", base: "study", past: "studied", participle: "studied", zh: "学习", kind: "regular", pastSentence: "He ___ for the test yesterday.", perfectSentence: "We have ___ English since 2022." },
  { id: "stop", base: "stop", past: "stopped", participle: "stopped", zh: "停止", kind: "regular", pastSentence: "The bus ___ near our house.", perfectSentence: "It has finally ___ raining." },
  { id: "visit", base: "visit", past: "visited", participle: "visited", zh: "参观；拜访", kind: "regular", pastSentence: "We ___ the castle on Saturday.", perfectSentence: "I have ___ Paris twice." },
]

export const COMPARISONS: ComparisonForm[] = [
  { id: "tall", base: "tall", comparative: "taller", superlative: "tallest", zh: "高的", rule: "直接加 -er/-est", comparativeSentence: "Tom is ___ than Jack.", superlativeSentence: "Tom is the ___ boy in his class." },
  { id: "short", base: "short", comparative: "shorter", superlative: "shortest", zh: "矮的；短的", rule: "直接加 -er/-est", comparativeSentence: "This route is ___ than that one.", superlativeSentence: "February is the ___ month." },
  { id: "long", base: "long", comparative: "longer", superlative: "longest", zh: "长的", rule: "直接加 -er/-est", comparativeSentence: "My hair is ___ than yours.", superlativeSentence: "This is the ___ river in the country." },
  { id: "fast", base: "fast", comparative: "faster", superlative: "fastest", zh: "快的", rule: "直接加 -er/-est", comparativeSentence: "A train is ___ than a bus.", superlativeSentence: "Leo is the ___ runner on the team." },
  { id: "slow", base: "slow", comparative: "slower", superlative: "slowest", zh: "慢的", rule: "直接加 -er/-est", comparativeSentence: "The blue car is ___ than the red one.", superlativeSentence: "This is the ___ computer here." },
  { id: "old", base: "old", comparative: "older", superlative: "oldest", zh: "年长的；旧的", rule: "直接加 -er/-est", comparativeSentence: "My sister is ___ than me.", superlativeSentence: "That is the ___ building in town." },
  { id: "young", base: "young", comparative: "younger", superlative: "youngest", zh: "年轻的", rule: "直接加 -er/-est", comparativeSentence: "Sam is ___ than his brother.", superlativeSentence: "Mia is the ___ child in the family." },
  { id: "small", base: "small", comparative: "smaller", superlative: "smallest", zh: "小的", rule: "直接加 -er/-est", comparativeSentence: "My room is ___ than yours.", superlativeSentence: "This is the ___ key." },
  { id: "nice", base: "nice", comparative: "nicer", superlative: "nicest", zh: "友好的；美好的", rule: "结尾 e 加 -r/-st", comparativeSentence: "Today is ___ than yesterday.", superlativeSentence: "She is the ___ person I know." },
  { id: "large", base: "large", comparative: "larger", superlative: "largest", zh: "大的", rule: "结尾 e 加 -r/-st", comparativeSentence: "Their garden is ___ than ours.", superlativeSentence: "This is the ___ room in the house." },
  { id: "safe", base: "safe", comparative: "safer", superlative: "safest", zh: "安全的", rule: "结尾 e 加 -r/-st", comparativeSentence: "Travelling by train is ___ than cycling.", superlativeSentence: "This is the ___ way home." },
  { id: "big", base: "big", comparative: "bigger", superlative: "biggest", zh: "大的", rule: "双写末尾辅音", comparativeSentence: "An elephant is ___ than a horse.", superlativeSentence: "This is the ___ box." },
  { id: "hot", base: "hot", comparative: "hotter", superlative: "hottest", zh: "热的", rule: "双写末尾辅音", comparativeSentence: "July is ___ than May.", superlativeSentence: "It was the ___ day of the year." },
  { id: "thin", base: "thin", comparative: "thinner", superlative: "thinnest", zh: "瘦的；薄的", rule: "双写末尾辅音", comparativeSentence: "This book is ___ than that one.", superlativeSentence: "Use the ___ piece of paper." },
  { id: "happy", base: "happy", comparative: "happier", superlative: "happiest", zh: "快乐的", rule: "y 变 i", comparativeSentence: "She looks ___ today.", superlativeSentence: "That was the ___ day of my life." },
  { id: "easy", base: "easy", comparative: "easier", superlative: "easiest", zh: "容易的", rule: "y 变 i", comparativeSentence: "This question is ___ than the last one.", superlativeSentence: "Number three is the ___ question." },
  { id: "busy", base: "busy", comparative: "busier", superlative: "busiest", zh: "忙碌的", rule: "y 变 i", comparativeSentence: "The shop is ___ on Saturdays.", superlativeSentence: "Friday is our ___ day." },
  { id: "funny", base: "funny", comparative: "funnier", superlative: "funniest", zh: "有趣的；滑稽的", rule: "y 变 i", comparativeSentence: "This film is ___ than the first one.", superlativeSentence: "Ben told the ___ joke." },
  { id: "beautiful", base: "beautiful", comparative: "more beautiful", superlative: "most beautiful", zh: "美丽的", rule: "more / most", comparativeSentence: "The lake is ___ in the morning.", superlativeSentence: "It is the ___ place in the area." },
  { id: "interesting", base: "interesting", comparative: "more interesting", superlative: "most interesting", zh: "有趣的", rule: "more / most", comparativeSentence: "The book is ___ than the film.", superlativeSentence: "Science is the ___ subject for me." },
  { id: "expensive", base: "expensive", comparative: "more expensive", superlative: "most expensive", zh: "昂贵的", rule: "more / most", comparativeSentence: "This phone is ___ than mine.", superlativeSentence: "That is the ___ hotel in town." },
  { id: "difficult", base: "difficult", comparative: "more difficult", superlative: "most difficult", zh: "困难的", rule: "more / most", comparativeSentence: "Part Three is ___ than Part One.", superlativeSentence: "This was the ___ task." },
  { id: "popular", base: "popular", comparative: "more popular", superlative: "most popular", zh: "受欢迎的", rule: "more / most", comparativeSentence: "Football is ___ than tennis here.", superlativeSentence: "It is the ___ café in town." },
  { id: "good", base: "good", comparative: "better", superlative: "best", zh: "好的", rule: "不规则变化", comparativeSentence: "This idea is ___ than mine.", superlativeSentence: "That was the ___ answer." },
  { id: "bad", base: "bad", comparative: "worse", superlative: "worst", zh: "坏的；糟糕的", rule: "不规则变化", comparativeSentence: "The weather is ___ today.", superlativeSentence: "It was the ___ storm this year." },
  { id: "far", base: "far", comparative: "farther / further", superlative: "farthest / furthest", zh: "远的", rule: "不规则变化", comparativeSentence: "The station is ___ than the library.", superlativeSentence: "That is the ___ I have ever walked.", note: "farther/farthest 和 further/furthest 均可表示实际距离。" },
  { id: "many", base: "many / much", comparative: "more", superlative: "most", zh: "多的", rule: "不规则变化", comparativeSentence: "Amy has ___ books than I do.", superlativeSentence: "Leo has the ___ free time." },
  { id: "little", base: "little", comparative: "less", superlative: "least", zh: "少的", rule: "不规则变化", comparativeSentence: "I have ___ homework today.", superlativeSentence: "This route takes the ___ time." },
]

export function primaryForm(value: string) {
  return value.split(" / ")[0]
}

export function acceptedForms(value: string) {
  return value.split(" / ").map((part) => part.trim().toLowerCase())
}
