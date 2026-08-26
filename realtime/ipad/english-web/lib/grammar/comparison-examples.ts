export interface ComparisonExamples {
  base: [sentence: string, translation: string]
  comparativeZh: string
  superlativeZh: string
}

export const COMPARISON_EXAMPLES: Record<string, ComparisonExamples> = {
  tall: { base: ["Tom is tall.", "汤姆很高。"], comparativeZh: "汤姆比杰克高。", superlativeZh: "汤姆是班里最高的男孩。" },
  short: { base: ["This route is short.", "这条路线很短。"], comparativeZh: "这条路线比那条短。", superlativeZh: "二月是一年中最短的月份。" },
  long: { base: ["My hair is long.", "我的头发很长。"], comparativeZh: "我的头发比你的长。", superlativeZh: "这是这个国家最长的河流。" },
  fast: { base: ["This train is fast.", "这列火车很快。"], comparativeZh: "火车比公交车快。", superlativeZh: "利奥是队里跑得最快的人。" },
  slow: { base: ["The blue car is slow.", "蓝色汽车很慢。"], comparativeZh: "蓝色汽车比红色汽车慢。", superlativeZh: "这是这里最慢的电脑。" },
  old: { base: ["This building is old.", "这栋建筑很古老。"], comparativeZh: "我的姐姐比我年长。", superlativeZh: "那是镇上最古老的建筑。" },
  young: { base: ["Sam is young.", "萨姆很年轻。"], comparativeZh: "萨姆比他的哥哥年轻。", superlativeZh: "米娅是家里年龄最小的孩子。" },
  small: { base: ["My room is small.", "我的房间很小。"], comparativeZh: "我的房间比你的房间小。", superlativeZh: "这是最小的钥匙。" },
  nice: { base: ["The weather is nice today.", "今天天气很好。"], comparativeZh: "今天比昨天更好。", superlativeZh: "她是我认识的人中最友好的。" },
  large: { base: ["Their garden is large.", "他们的花园很大。"], comparativeZh: "他们的花园比我们的更大。", superlativeZh: "这是房子里最大的房间。" },
  safe: { base: ["This road is safe.", "这条路很安全。"], comparativeZh: "坐火车比骑自行车更安全。", superlativeZh: "这是回家最安全的路。" },
  big: { base: ["A horse is big.", "马很大。"], comparativeZh: "大象比马大。", superlativeZh: "这是最大的盒子。" },
  hot: { base: ["May is hot here.", "这里五月很热。"], comparativeZh: "七月比五月更热。", superlativeZh: "那是一年中最热的一天。" },
  thin: { base: ["This book is thin.", "这本书很薄。"], comparativeZh: "这本书比那本更薄。", superlativeZh: "使用最薄的那张纸。" },
  happy: { base: ["She is happy today.", "她今天很开心。"], comparativeZh: "她今天看起来更开心。", superlativeZh: "那是我一生中最快乐的一天。" },
  easy: { base: ["This question is easy.", "这个问题很容易。"], comparativeZh: "这个问题比上一个更容易。", superlativeZh: "第三题是最容易的问题。" },
  busy: { base: ["The shop is busy today.", "商店今天很忙。"], comparativeZh: "商店星期六更忙。", superlativeZh: "星期五是我们最忙的一天。" },
  funny: { base: ["This film is funny.", "这部电影很有趣。"], comparativeZh: "这部电影比第一部更有趣。", superlativeZh: "本讲了最好笑的笑话。" },
  beautiful: { base: ["The lake is beautiful.", "这个湖很美。"], comparativeZh: "这个湖在早晨更美。", superlativeZh: "这是这个地区最美丽的地方。" },
  interesting: { base: ["The book is interesting.", "这本书很有趣。"], comparativeZh: "这本书比电影更有趣。", superlativeZh: "科学对我来说是最有趣的科目。" },
  expensive: { base: ["This phone is expensive.", "这部手机很贵。"], comparativeZh: "这部手机比我的更贵。", superlativeZh: "那是镇上最贵的酒店。" },
  difficult: { base: ["Part One is difficult.", "第一部分很难。"], comparativeZh: "第三部分比第一部分更难。", superlativeZh: "这是最困难的任务。" },
  popular: { base: ["Tennis is popular here.", "网球在这里很受欢迎。"], comparativeZh: "足球在这里比网球更受欢迎。", superlativeZh: "这是镇上最受欢迎的咖啡馆。" },
  good: { base: ["This idea is good.", "这个主意很好。"], comparativeZh: "这个主意比我的更好。", superlativeZh: "那是最好的答案。" },
  bad: { base: ["The weather is bad today.", "今天天气很糟。"], comparativeZh: "今天的天气更糟。", superlativeZh: "那是今年最严重的暴风雨。" },
  far: { base: ["The library is far from here.", "图书馆离这里很远。"], comparativeZh: "车站比图书馆更远。", superlativeZh: "那是我走过的最远距离。" },
  many: { base: ["Amy has many books.", "艾米有很多书。"], comparativeZh: "艾米的书比我多。", superlativeZh: "利奥的空闲时间最多。" },
  little: { base: ["I have little homework today.", "我今天作业很少。"], comparativeZh: "我今天的作业更少。", superlativeZh: "这条路线花费的时间最少。" },
}
