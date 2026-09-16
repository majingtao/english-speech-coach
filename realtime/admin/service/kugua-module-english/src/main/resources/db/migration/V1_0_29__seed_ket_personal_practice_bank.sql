-- Original A2 Key for Schools practice bank.
-- Coverage: Speaking Part 1/2 and Writing Part 6/7. Re-running is safe because
-- practice_type + title is treated as the natural key for seed content.

INSERT INTO esc_personal_practice
    (practice_type, title, prompt_en, prompt_cn, reference_json, content_points_json,
     min_sentences, min_words, sort, status, tenant_id, creator, updater)
SELECT bank.practice_type, bank.title, bank.prompt_en, bank.prompt_cn,
       bank.reference_json, bank.content_points_json, bank.min_sentences,
       bank.min_words, bank.sort, 1, 1, 'system', 'system'
FROM (
    SELECT 'speaking' practice_type, '口语P1-01 我的学校日常' title,
           'Tell me about a normal school day. What time do you start, and what lesson do you like best?' prompt_en,
           '介绍一个普通的上学日。你几点开始上课？最喜欢哪门课？' prompt_cn,
           '[{"en":"My school starts at eight o clock, so I usually arrive at half past seven.","cn":"学校八点上课，所以我通常七点半到校。"},{"en":"I like English best because we play word games and learn useful expressions.","cn":"我最喜欢英语，因为我们会玩单词游戏，还能学到实用表达。"},{"en":"After school, I often finish my homework before dinner.","cn":"放学后，我经常在晚饭前完成作业。"}]' reference_json,
           '["说明上学时间","说出最喜欢的课程","给出喜欢这门课的理由","补充一个日常细节"]' content_points_json,
           3 min_sentences, 0 min_words, 101 sort
    UNION ALL SELECT 'speaking', '口语P1-02 我的家庭',
           'Who do you live with, and what do you enjoy doing together?',
           '你和谁住在一起？你们喜欢一起做什么？',
           '[{"en":"I live with my parents and my younger sister.","cn":"我和爸爸妈妈以及妹妹住在一起。"},{"en":"At weekends, we enjoy riding our bikes in the park.","cn":"周末我们喜欢在公园骑自行车。"},{"en":"It is fun because we can talk and spend time outdoors together.","cn":"这很有趣，因为我们可以聊天并一起享受户外时光。"}]',
           '["介绍家庭成员","说明共同活动","给出喜欢该活动的理由"]', 3, 0, 102
    UNION ALL SELECT 'speaking', '口语P1-03 我的家',
           'What is your home like? Which room do you like best and why?',
           '你的家是什么样的？你最喜欢哪个房间？为什么？',
           '[{"en":"I live in a flat near my school, and it has three bedrooms.","cn":"我住在学校附近的一套公寓里，家里有三间卧室。"},{"en":"My favourite room is the living room because it is bright and comfortable.","cn":"我最喜欢客厅，因为那里明亮又舒适。"},{"en":"I usually read or watch films there with my family.","cn":"我通常在那里和家人一起读书或看电影。"}]',
           '["描述住房类型或位置","说出最喜欢的房间","解释原因","说明在房间里做什么"]', 3, 0, 103
    UNION ALL SELECT 'speaking', '口语P1-04 最好的朋友',
           'Tell me about your best friend. What do you usually do together?',
           '介绍你最好的朋友。你们通常一起做什么？',
           '[{"en":"My best friend is Leo, who is in the same class as me.","cn":"我最好的朋友是Leo，他和我在同一个班。"},{"en":"He is friendly and always makes me laugh.","cn":"他很友好，总能逗我笑。"},{"en":"We often play basketball after school and help each other with homework.","cn":"我们经常放学后打篮球，也会互相帮助做作业。"}]',
           '["介绍朋友是谁","描述一个性格特点","说明共同活动"]', 3, 0, 104
    UNION ALL SELECT 'speaking', '口语P1-05 周末安排',
           'What do you usually do at weekends? Tell me about last weekend too.',
           '你周末通常做什么？也说说上周末做了什么。',
           '[{"en":"I usually play badminton and visit my grandparents at weekends.","cn":"周末我通常打羽毛球并去看望爷爷奶奶。"},{"en":"Last Saturday, I went to the sports centre with my cousin.","cn":"上周六，我和表哥去了体育中心。"},{"en":"We had a great time, although the centre was quite busy.","cn":"我们玩得很开心，虽然体育中心人很多。"}]',
           '["说明通常的周末活动","使用过去时描述上周末","补充感受或细节"]', 3, 0, 105
    UNION ALL SELECT 'speaking', '口语P1-06 喜欢的食物',
           'What food do you like most? When and where do you usually eat it?',
           '你最喜欢什么食物？通常什么时候、在哪里吃？',
           '[{"en":"My favourite food is noodles with vegetables and chicken.","cn":"我最喜欢的食物是蔬菜鸡肉面。"},{"en":"I usually eat them at home for lunch on Sundays.","cn":"我通常周日午饭时在家吃面。"},{"en":"I like them because they are tasty, warm and easy to share with my family.","cn":"我喜欢它，因为它美味又暖和，也方便和家人分享。"}]',
           '["说出最喜欢的食物","说明时间和地点","给出喜欢的理由"]', 3, 0, 106
    UNION ALL SELECT 'speaking', '口语P1-07 运动习惯',
           'Do you play any sports? How often do you practise, and why do you like it?',
           '你参加什么运动？多久练习一次？为什么喜欢？',
           '[{"en":"I play table tennis twice a week at the school sports hall.","cn":"我每周在学校体育馆打两次乒乓球。"},{"en":"I usually practise with two classmates after lessons.","cn":"我通常下课后和两名同学一起练习。"},{"en":"I enjoy it because it is fast, exciting and good for my health.","cn":"我喜欢它，因为它节奏快、很刺激，而且有益健康。"}]',
           '["说出运动项目","说明练习频率或地点","说明和谁练习","给出理由"]', 3, 0, 107
    UNION ALL SELECT 'speaking', '口语P1-08 兴趣爱好',
           'What is your favourite hobby? When did you start it?',
           '你最喜欢的爱好是什么？什么时候开始的？',
           '[{"en":"My favourite hobby is drawing pictures of animals and places.","cn":"我最喜欢的爱好是画动物和风景。"},{"en":"I started drawing when I was seven years old.","cn":"我七岁时开始画画。"},{"en":"I practise every evening because drawing helps me relax and notice small details.","cn":"我每天晚上练习，因为画画让我放松，也让我注意细节。"}]',
           '["说出爱好","说明开始时间","说明练习频率","给出喜欢的理由"]', 3, 0, 108
    UNION ALL SELECT 'speaking', '口语P1-09 最近的假期',
           'Tell me about your last holiday. Where did you go and what did you do?',
           '介绍你最近的一次假期。你去了哪里？做了什么？',
           '[{"en":"Last summer, I went to Qingdao with my parents by train.","cn":"去年夏天，我和父母坐火车去了青岛。"},{"en":"We walked along the beach, ate seafood and visited a museum.","cn":"我们沿着海滩散步、吃了海鲜，还参观了博物馆。"},{"en":"My favourite part was swimming in the sea because the water was clear.","cn":"我最喜欢在海里游泳，因为海水很清澈。"}]',
           '["使用过去时说明地点","说明同行人或交通方式","列举活动","表达最喜欢的部分及理由"]', 3, 0, 109
    UNION ALL SELECT 'speaking', '口语P1-10 生日庆祝',
           'How do you usually celebrate your birthday? What was your last birthday like?',
           '你通常怎样庆祝生日？上一次生日过得怎么样？',
           '[{"en":"I usually have a small birthday meal with my family at home.","cn":"我通常在家和家人吃一顿简单的生日餐。"},{"en":"On my last birthday, three friends came and we made pizzas together.","cn":"上次生日有三个朋友来，我们一起做了披萨。"},{"en":"It was special because everyone wrote a kind message in my birthday card.","cn":"那次生日很特别，因为每个人都在生日卡上写了暖心的话。"}]',
           '["说明通常如何庆祝","使用过去时描述上次生日","补充人物或活动","表达感受和原因"]', 3, 0, 110
    UNION ALL SELECT 'speaking', '口语P1-11 上学交通',
           'How do you travel to school? Is it a good way to travel?',
           '你怎样去学校？这种出行方式好吗？',
           '[{"en":"I usually take the bus to school, and the journey takes about twenty minutes.","cn":"我通常坐公交车上学，路上大约需要二十分钟。"},{"en":"I think it is convenient because the bus stop is close to my home.","cn":"我觉得很方便，因为公交站离我家很近。"},{"en":"However, I sometimes walk when the weather is sunny.","cn":"不过，天气晴朗时我有时会步行。"}]',
           '["说明交通方式","说明所需时间","评价这种方式并给出理由","可补充另一种方式"]', 3, 0, 111
    UNION ALL SELECT 'speaking', '口语P1-12 未来计划',
           'What would you like to do during the next school holiday?',
           '下一个学校假期你想做什么？',
           '[{"en":"During the next holiday, I would like to learn how to swim better.","cn":"下个假期我想提高游泳水平。"},{"en":"I am going to take lessons at the sports centre with my friend.","cn":"我打算和朋友一起去体育中心上课。"},{"en":"I hope to practise twice a week because swimming is useful and enjoyable.","cn":"我希望每周练习两次，因为游泳既实用又有趣。"}]',
           '["说明未来想做的事","使用将来计划表达","说明地点或同行人","给出理由"]', 3, 0, 112

    UNION ALL SELECT 'speaking', '口语P2-01 周末活动选择',
           'Which is better for a weekend: going to the cinema, playing sport, or visiting a museum? Say what you like and why.',
           '周末去电影院、参加运动还是参观博物馆更好？说出你的选择和理由。',
           '[{"en":"I think playing sport is the best choice because it is active and healthy.","cn":"我认为参加运动是最好的选择，因为它让人活跃并且有益健康。"},{"en":"The cinema is fun too, but tickets can be expensive.","cn":"看电影也很有趣，但电影票可能很贵。"},{"en":"I would choose badminton because I can play it with my friends outdoors.","cn":"我会选择羽毛球，因为我可以和朋友在户外一起打。"}]',
           '["明确表达首选活动","至少比较另一个选项","给出两个理由或细节"]', 3, 0, 201
    UNION ALL SELECT 'speaking', '口语P2-02 放学后学习地点',
           'Where is the best place to study after school: at home, in the library, or at a cafe? Why?',
           '放学后在家、图书馆还是咖啡馆学习最好？为什么？',
           '[{"en":"For me, the library is the best place because it is quiet and has many useful books.","cn":"对我来说图书馆最好，因为那里安静并且有很多有用的书。"},{"en":"Studying at home is comfortable, but I sometimes watch television instead.","cn":"在家学习很舒服，但我有时会转而看电视。"},{"en":"I would go to the library with a classmate so we could help each other.","cn":"我会和同学一起去图书馆，这样我们可以互相帮助。"}]',
           '["选择一个学习地点","说明优点","比较另一个地点","补充实际安排"]', 3, 0, 202
    UNION ALL SELECT 'speaking', '口语P2-03 健康零食',
           'Which snack would you choose: fruit, a sandwich, or chocolate? Discuss what you like and dislike.',
           '水果、三明治和巧克力，你会选哪种零食？讨论喜欢和不喜欢的原因。',
           '[{"en":"I would choose a fruit salad because it is fresh, colourful and healthy.","cn":"我会选水果沙拉，因为它新鲜、多彩又健康。"},{"en":"A sandwich is more filling, so it is good after sport.","cn":"三明治更管饱，所以运动后吃很合适。"},{"en":"I like chocolate, but I do not eat it often because it has a lot of sugar.","cn":"我喜欢巧克力，但不会经常吃，因为它含糖很多。"}]',
           '["明确选择","说明选择理由","比较其他选项","表达一项不喜欢或限制"]', 3, 0, 203
    UNION ALL SELECT 'speaking', '口语P2-04 班级旅行',
           'What would be best for a class trip: a zoo, a science museum, or a farm? Give reasons.',
           '班级旅行去动物园、科学博物馆还是农场最好？说明理由。',
           '[{"en":"A science museum would be best because everyone could learn by doing experiments.","cn":"科学博物馆最好，因为每个人都能通过实验来学习。"},{"en":"A zoo might be exciting, but some students may not like seeing animals in cages.","cn":"动物园可能很有趣，但有些同学也许不喜欢看到笼子里的动物。"},{"en":"The museum is also indoors, so bad weather would not spoil the trip.","cn":"博物馆还在室内，所以坏天气不会破坏旅行。"}]',
           '["选择班级旅行地点","说明教育或趣味价值","比较另一选项","考虑天气、费用或同学需求"]', 3, 0, 204
    UNION ALL SELECT 'speaking', '口语P2-05 给朋友的礼物',
           'Which present is best for a friend: a book, a game, or tickets to an event?',
           '送朋友书、游戏还是活动门票，哪种礼物最好？',
           '[{"en":"I think event tickets are the best present because we can enjoy the experience together.","cn":"我认为活动门票是最好的礼物，因为我们能一起享受这段经历。"},{"en":"A book is useful if you know what stories your friend likes.","cn":"如果你知道朋友喜欢什么故事，书也是实用的礼物。"},{"en":"I would buy two concert tickets because my friend loves music.","cn":"我会买两张音乐会门票，因为我的朋友热爱音乐。"}]',
           '["选择礼物","说明适合朋友的原因","比较另一个选项","补充具体例子"]', 3, 0, 205
    UNION ALL SELECT 'speaking', '口语P2-06 雨天活动',
           'What is the most enjoyable thing to do on a rainy day: cook, watch a film, or play a board game?',
           '雨天做饭、看电影还是玩桌游最有趣？',
           '[{"en":"Playing a board game is the most enjoyable because everyone can join in and talk.","cn":"玩桌游最有趣，因为每个人都能参与并交流。"},{"en":"Watching a film is relaxing, but people do not speak very much.","cn":"看电影很放松，但大家交流不多。"},{"en":"I would invite two friends over and choose a game that is easy to learn.","cn":"我会邀请两个朋友来，并选一个容易学会的游戏。"}]',
           '["选择雨天活动","给出理由","比较另一个选项","说明和谁一起或如何安排"]', 3, 0, 206
    UNION ALL SELECT 'speaking', '口语P2-07 学习新技能',
           'Which skill would you most like to learn: cooking, photography, or playing an instrument?',
           '烹饪、摄影和演奏乐器，你最想学哪项技能？',
           '[{"en":"I would most like to learn cooking because it is useful every day.","cn":"我最想学烹饪，因为它每天都很实用。"},{"en":"Photography sounds creative, but a good camera can be expensive.","cn":"摄影听起来很有创意，但一台好相机可能很贵。"},{"en":"I want to start with simple meals and cook dinner for my family one day.","cn":"我想从简单的饭菜开始，希望有一天能为家人做晚饭。"}]',
           '["选择一项技能","说明实用性或兴趣","比较另一项技能","说明具体学习计划"]', 3, 0, 207
    UNION ALL SELECT 'speaking', '口语P2-08 环保上学方式',
           'Which way of travelling to school is best for the environment: walking, cycling, or taking a bus?',
           '步行、骑车还是坐公交，哪种上学方式最环保？',
           '[{"en":"Walking is best for the environment because it does not cause any pollution.","cn":"步行最环保，因为它不会产生污染。"},{"en":"Cycling is faster, but students need a safe cycle path and a helmet.","cn":"骑车更快，但学生需要安全的自行车道和头盔。"},{"en":"I would walk when the weather is fine because my school is not far away.","cn":"天气好时我会步行，因为学校离我家不远。"}]',
           '["选择交通方式","说明环保理由","比较速度或安全性","结合自己的情况说明"]', 3, 0, 208
    UNION ALL SELECT 'speaking', '口语P2-09 学校俱乐部',
           'Which new school club would students enjoy most: art, drama, or basketball?',
           '美术、戏剧和篮球，学生最喜欢哪种新社团？',
           '[{"en":"I think a drama club would be the most enjoyable because students could act and work as a team.","cn":"我认为戏剧社最有趣，因为学生可以表演并进行团队合作。"},{"en":"Basketball is healthy, but the school needs enough space and equipment.","cn":"篮球有益健康，但学校需要足够的场地和设备。"},{"en":"The drama club could put on a short show for parents at the end of term.","cn":"戏剧社可以在学期末为家长表演一个短剧。"}]',
           '["选择社团","说明学生会喜欢的原因","比较另一选项","提出一个具体社团活动"]', 3, 0, 209
    UNION ALL SELECT 'speaking', '口语P2-10 暑假住宿',
           'Where would you prefer to stay on holiday: a hotel, a campsite, or a family home?',
           '假期住酒店、露营地还是亲友家，你更喜欢哪个？',
           '[{"en":"I would prefer a campsite because I enjoy being close to nature.","cn":"我更喜欢露营地，因为我喜欢亲近大自然。"},{"en":"A hotel is more comfortable, but it is usually more expensive.","cn":"酒店更舒适，但通常也更贵。"},{"en":"At a campsite, I could cook outdoors and look at the stars with my family.","cn":"在露营地，我可以和家人在户外做饭、看星星。"}]',
           '["说明住宿偏好","给出理由","比较舒适度或价格","描述一项可做的活动"]', 3, 0, 210
    UNION ALL SELECT 'speaking', '口语P2-11 获取新闻',
           'What is the best way for young people to get news: television, websites, or talking to adults?',
           '年轻人通过电视、网站还是和成年人交流来获取新闻最好？',
           '[{"en":"Reliable websites are a quick way to get news from different places.","cn":"可靠的网站能让我们快速了解不同地方的新闻。"},{"en":"However, some online information is not true, so we should check more than one source.","cn":"不过有些网络信息并不真实，所以我们应该核对多个来源。"},{"en":"Talking to adults is helpful because they can explain difficult stories.","cn":"和成年人交流也很有帮助，因为他们能解释难懂的新闻。"}]',
           '["选择或评价一种新闻来源","说明优点","指出一个问题","提出核实或理解新闻的方法"]', 3, 0, 211
    UNION ALL SELECT 'speaking', '口语P2-12 理想生日活动',
           'Which birthday activity would be most fun: a picnic, a sports party, or a visit to a theme park?',
           '野餐、运动派对和游乐园，哪种生日活动最有趣？',
           '[{"en":"A picnic would be most fun because friends could bring food and play games together.","cn":"野餐最有趣，因为朋友们可以带食物并一起玩游戏。"},{"en":"A theme park is exciting, but it may be crowded and costly.","cn":"游乐园很刺激，但可能拥挤而且费用高。"},{"en":"I would choose a park near my home and make a simple plan for wet weather.","cn":"我会选择家附近的公园，并为下雨准备一个简单的备用计划。"}]',
           '["选择生日活动","说明互动或乐趣","比较费用、天气或拥挤程度","补充具体安排"]', 3, 0, 212

    UNION ALL SELECT 'writing', '写作P6-01 邀请朋友参加生日会',
           'Write an email to your English friend Sam. Invite Sam to your birthday party. Say when and where it is, and tell Sam what to bring. Write 25 words or more.',
           '给英国朋友Sam写邮件，邀请他参加生日会。说明时间、地点以及需要带什么。不少于25词。',
           '[{"en":"Hi Sam,","cn":"你好，Sam："},{"en":"Would you like to come to my birthday party at my home this Saturday at three o clock?","cn":"你愿意本周六三点来我家参加生日会吗？"},{"en":"Please bring your favourite board game because we are going to play games after lunch.","cn":"请带上你最喜欢的桌游，因为午饭后我们要一起玩游戏。"},{"en":"I hope you can come!","cn":"希望你能来！"}]',
           '["邀请Sam参加生日会","说明具体时间","说明地点","说明需要携带的物品"]', 4, 25, 301
    UNION ALL SELECT 'writing', '写作P6-02 回复学校旅行',
           'Your English friend Mia asks about your school trip. Write an email to Mia. Say where you went, what you did, and what you liked best. Write 25 words or more.',
           '英国朋友Mia询问你的学校旅行。写邮件说明去了哪里、做了什么、最喜欢什么。不少于25词。',
           '[{"en":"Hi Mia,","cn":"你好，Mia："},{"en":"Our class went to the science museum by bus yesterday.","cn":"昨天我们班坐公交去了科学博物馆。"},{"en":"We did some experiments and watched a short film about space.","cn":"我们做了实验，还看了一部关于太空的短片。"},{"en":"I liked the robot show best because it was exciting and funny.","cn":"我最喜欢机器人表演，因为它既刺激又有趣。"}]',
           '["说明旅行地点","说明做过的活动","说出最喜欢的部分","给出喜欢的理由"]', 4, 25, 302
    UNION ALL SELECT 'writing', '写作P6-03 周末见面安排',
           'Write a note to your friend Alex. Suggest meeting this weekend. Say where to meet, what time, and what you want to do. Write 25 words or more.',
           '给朋友Alex写便条，建议周末见面。说明见面地点、时间和想做的事。不少于25词。',
           '[{"en":"Hi Alex,","cn":"你好，Alex："},{"en":"Are you free this Sunday? Let us meet outside the sports centre at ten in the morning.","cn":"你这周日有空吗？我们上午十点在体育中心外见吧。"},{"en":"We can play badminton and then have lunch at the new cafe nearby.","cn":"我们可以打羽毛球，然后去附近的新咖啡馆吃午饭。"},{"en":"Please tell me if that time is good for you.","cn":"请告诉我这个时间是否合适。"}]',
           '["提出周末见面","说明见面地点","说明具体时间","说明计划活动"]', 4, 25, 303
    UNION ALL SELECT 'writing', '写作P6-04 借用英语书',
           'Write an email to your classmate Ben. Ask to borrow an English book. Say why you need it, when you need it, and when you will return it. Write 25 words or more.',
           '给同学Ben写邮件借一本英语书。说明借书原因、需要时间和归还时间。不少于25词。',
           '[{"en":"Hi Ben,","cn":"你好，Ben："},{"en":"Could I borrow your English storybook for my homework, please?","cn":"我可以借你的英语故事书做作业吗？"},{"en":"I need it on Wednesday because our book report is due on Friday.","cn":"我周三需要它，因为读书报告周五截止。"},{"en":"I will take good care of it and return it next Monday.","cn":"我会保管好，并在下周一归还。"}]',
           '["礼貌提出借书","说明借书原因","说明需要时间","说明归还时间"]', 4, 25, 304
    UNION ALL SELECT 'writing', '写作P6-05 推荐课外俱乐部',
           'Your English friend asks which after-school club to join. Write an email. Recommend a club, say when it meets, and explain why it is good. Write 25 words or more.',
           '英国朋友询问该参加哪个课外俱乐部。写邮件推荐一个俱乐部，说明活动时间和推荐理由。不少于25词。',
           '[{"en":"Hi Lucy,","cn":"你好，Lucy："},{"en":"I think you should join the school art club, which meets every Tuesday after lessons.","cn":"我认为你应该参加学校美术社，它每周二放学后活动。"},{"en":"The teacher is friendly, and you can learn to paint with different colours.","cn":"老师很友好，而且你能学习用不同颜色画画。"},{"en":"It is also a good place to make new friends.","cn":"那里也是结交新朋友的好地方。"}]',
           '["推荐一个社团","说明活动时间","给出至少一个推荐理由","补充参加社团的好处"]', 4, 25, 305
    UNION ALL SELECT 'writing', '写作P6-06 感谢朋友的礼物',
           'Write an email to your English friend Kim to thank Kim for a present. Say what you received, why you like it, and how you will use it. Write 25 words or more.',
           '给英国朋友Kim写感谢邮件。说明收到什么、为什么喜欢、将怎样使用。不少于25词。',
           '[{"en":"Hi Kim,","cn":"你好，Kim："},{"en":"Thank you very much for the blue backpack you sent me for my birthday.","cn":"非常感谢你送给我的蓝色生日背包。"},{"en":"I love it because it is light and has plenty of space for my books.","cn":"我很喜欢它，因为它很轻，也有足够空间放书。"},{"en":"I am going to take it on our class trip next week.","cn":"下周班级旅行时我准备背着它。"}]',
           '["表达感谢","说明收到的礼物","解释喜欢的原因","说明将如何使用"]', 4, 25, 306
    UNION ALL SELECT 'writing', '写作P6-07 缺席音乐课',
           'Write a message to your music teacher. Say why you cannot attend tomorrow, ask about the homework, and say when you will return. Write 25 words or more.',
           '给音乐老师写留言。说明明天无法上课的原因、询问作业、说明何时回来。不少于25词。',
           '[{"en":"Dear Ms Green,","cn":"Green老师您好："},{"en":"I cannot come to music class tomorrow because I have a dentist appointment.","cn":"我明天不能来上音乐课，因为我要去看牙医。"},{"en":"Could you please tell me what I need to practise at home?","cn":"您能告诉我需要在家练习什么吗？"},{"en":"I will be back at school on Thursday.","cn":"我周四会回学校。"}]',
           '["说明无法出席","说明原因","询问作业或练习内容","说明返回时间"]', 4, 25, 307
    UNION ALL SELECT 'writing', '写作P6-08 给新同学介绍学校',
           'A new English-speaking student is joining your class. Write an email. Say where your classroom is, what to bring, and what students do at lunchtime. Write 25 words or more.',
           '一名说英语的新生将加入班级。写邮件说明教室位置、需要带什么以及午餐时间的活动。不少于25词。',
           '[{"en":"Hi Jamie,","cn":"你好，Jamie："},{"en":"Our classroom is on the second floor, next to the school library.","cn":"我们的教室在二楼，紧挨着学校图书馆。"},{"en":"Please bring a notebook, some pens and your sports clothes.","cn":"请带笔记本、几支笔和运动服。"},{"en":"At lunchtime, we eat in the dining hall and often play outside afterwards.","cn":"午餐时我们在食堂吃饭，之后经常去户外玩。"}]',
           '["说明教室位置","说明需要携带的物品","说明午餐地点","说明午餐后活动"]', 4, 25, 308
    UNION ALL SELECT 'writing', '写作P6-09 取消野餐计划',
           'Write a message to your friend Eva. Cancel your picnic because of the weather. Apologise, explain the problem, and suggest a new plan. Write 25 words or more.',
           '给朋友Eva写留言，因为天气取消野餐。道歉、解释问题并提出新计划。不少于25词。',
           '[{"en":"Hi Eva,","cn":"你好，Eva："},{"en":"I am sorry, but we need to cancel our picnic because heavy rain is forecast for Saturday.","cn":"很抱歉，我们需要取消野餐，因为预报周六有大雨。"},{"en":"Would you like to come to my house and watch a film instead?","cn":"你愿意改为来我家看电影吗？"},{"en":"We could have the picnic next weekend if the weather is better.","cn":"如果天气好转，我们可以下周末再去野餐。"}]',
           '["表达歉意并取消计划","说明天气原因","提出替代活动","建议新的野餐时间"]', 4, 25, 309
    UNION ALL SELECT 'writing', '写作P6-10 询问夏令营信息',
           'Write an email to a summer camp. Ask about the dates, the sports available, and what clothes you should bring. Write 25 words or more.',
           '给夏令营写邮件，询问日期、可参加的运动以及需要携带的衣物。不少于25词。',
           '[{"en":"Dear Sir or Madam,","cn":"尊敬的负责人："},{"en":"I am interested in your summer camp and would like some more information.","cn":"我对你们的夏令营感兴趣，想了解更多信息。"},{"en":"Could you tell me the camp dates and which sports students can try?","cn":"您能告诉我夏令营日期以及学生可以体验哪些运动吗？"},{"en":"I would also like to know what clothes I should bring.","cn":"我还想知道应该携带哪些衣物。"}]',
           '["说明写信目的","询问夏令营日期","询问运动项目","询问需要携带的衣物"]', 4, 25, 310
    UNION ALL SELECT 'writing', '写作P6-11 分享一部电影',
           'Write an email to your English friend Pat about a film you watched. Say what kind of film it was, who you watched it with, and why you liked or disliked it. Write 25 words or more.',
           '给英国朋友Pat写邮件介绍看过的一部电影。说明电影类型、和谁观看以及喜欢或不喜欢的原因。不少于25词。',
           '[{"en":"Hi Pat,","cn":"你好，Pat："},{"en":"I watched an exciting adventure film with my brother on Friday evening.","cn":"周五晚上我和哥哥看了一部刺激的冒险电影。"},{"en":"It was about two children who found a hidden island.","cn":"电影讲的是两个孩子发现一座隐秘岛屿的故事。"},{"en":"I liked it because the story was surprising and the characters were funny.","cn":"我喜欢它，因为故事出人意料，人物也很有趣。"}]',
           '["说明电影类型","说明和谁以及何时观看","简述电影内容","给出喜欢或不喜欢的理由"]', 4, 25, 311
    UNION ALL SELECT 'writing', '写作P6-12 安排小组作业',
           'Write a message to your project partner Jo. Say which part you have finished, ask Jo to bring something, and suggest a time to meet. Write 25 words or more.',
           '给项目搭档Jo写留言。说明已完成哪部分、请Jo带一样东西并建议见面时间。不少于25词。',
           '[{"en":"Hi Jo,","cn":"你好，Jo："},{"en":"I have finished the pictures and the first page of our science project.","cn":"我已经完成了科学项目的图片和第一页。"},{"en":"Please bring your notes about sea animals when we meet.","cn":"见面时请带上你关于海洋动物的笔记。"},{"en":"Can we work in the library at four o clock tomorrow afternoon?","cn":"我们明天下午四点在图书馆一起做项目，可以吗？"}]',
           '["说明已经完成的内容","请对方携带物品","建议见面地点","建议具体时间"]', 4, 25, 312

    UNION ALL SELECT 'writing', '写作P7-01 公园里的意外野餐',
           'Write a story of 35 words or more. Picture 1: A family prepares a picnic in a sunny park. Picture 2: It suddenly starts to rain. Picture 3: They eat happily under a large shelter.',
           '根据三幅图写不少于35词的故事：一家人在晴朗的公园准备野餐；突然下雨；他们在大棚下开心用餐。',
           '[{"en":"Last Sunday, Amy went to the park with her family for a picnic.","cn":"上周日，Amy和家人去公园野餐。"},{"en":"They put their food on a blanket, but dark clouds soon appeared and it began to rain.","cn":"他们把食物放在毯子上，但乌云很快出现，天开始下雨。"},{"en":"They quickly carried everything to a large shelter nearby.","cn":"他们很快把所有东西搬到附近的大棚下。"},{"en":"In the end, they ate together and laughed about their wet adventure.","cn":"最后，他们一起吃饭，还笑着谈论这次淋雨的小冒险。"}]',
           '["交代人物、时间和地点","描述开始野餐","描述突然下雨的问题","说明解决办法和结局"]', 4, 35, 401
    UNION ALL SELECT 'writing', '写作P7-02 找回丢失的小狗',
           'Write a story of 35 words or more. Picture 1: A boy sees a lost dog near school. Picture 2: He reads the phone number on its collar. Picture 3: The owner arrives and thanks him.',
           '根据三幅图写不少于35词的故事：男孩在学校附近看到走失的小狗；查看项圈电话；主人赶来感谢他。',
           '[{"en":"When Tom left school, he saw a small dog standing alone by the gate.","cn":"Tom放学时看到一只小狗独自站在校门旁。"},{"en":"The dog looked frightened, so Tom gave it some water and checked its collar.","cn":"小狗看起来很害怕，于是Tom给它喝水并查看项圈。"},{"en":"He called the phone number and waited with the dog.","cn":"他拨打了电话号码并陪着小狗等待。"},{"en":"Soon, the owner arrived, thanked Tom and took the happy dog home.","cn":"很快主人赶来，感谢Tom并把开心的小狗带回了家。"}]',
           '["描述发现小狗","描述小狗的状态或男孩的行动","说明如何联系主人","写出主人到来和结果"]', 4, 35, 402
    UNION ALL SELECT 'writing', '写作P7-03 烤蛋糕失败又成功',
           'Write a story of 35 words or more. Picture 1: Two friends make a birthday cake. Picture 2: The first cake burns. Picture 3: They make another cake and everyone enjoys it.',
           '根据三幅图写不少于35词的故事：两个朋友做生日蛋糕；第一个蛋糕烤焦；重新做后大家开心享用。',
           '[{"en":"Ella and Max decided to bake a cake for their friend’s birthday.","cn":"Ella和Max决定为朋友的生日烤蛋糕。"},{"en":"They mixed everything carefully, but they forgot to check the oven and the cake burned.","cn":"他们认真混合材料，却忘了查看烤箱，蛋糕烤焦了。"},{"en":"They did not give up and quickly made a second, smaller cake.","cn":"他们没有放弃，很快又做了一个小一点的蛋糕。"},{"en":"Their friend loved it, and everybody enjoyed a piece at the party.","cn":"朋友很喜欢，派对上的每个人都开心地吃了一块。"}]',
           '["说明做蛋糕的目的","描述蛋糕烤焦的原因或结果","描述重新尝试","写出生日会上的结局"]', 4, 35, 403
    UNION ALL SELECT 'writing', '写作P7-04 错过公交车',
           'Write a story of 35 words or more. Picture 1: A girl runs towards a bus stop. Picture 2: The bus leaves without her. Picture 3: Her neighbour gives her a lift to school.',
           '根据三幅图写不少于35词的故事：女孩跑向公交站；错过公交；邻居开车送她上学。',
           '[{"en":"One morning, Lily left home late and ran towards the bus stop.","cn":"一天早晨，Lily出门晚了，跑向公交站。"},{"en":"She waved to the driver, but the bus left before she reached it.","cn":"她向司机挥手，但还没跑到公交站，车就开走了。"},{"en":"Lily was worried because she had an important test that day.","cn":"Lily很担心，因为当天有一场重要考试。"},{"en":"Luckily, her neighbour saw her and drove her to school just in time.","cn":"幸运的是，邻居看见她并开车及时把她送到学校。"}]',
           '["交代迟到或奔跑的原因","描述错过公交车","表达人物感受及原因","写出邻居帮助和结局"]', 4, 35, 404
    UNION ALL SELECT 'writing', '写作P7-05 海滩上的钱包',
           'Write a story of 35 words or more. Picture 1: Two children find a wallet on a beach. Picture 2: They take it to a police officer. Picture 3: A woman gets it back and smiles.',
           '根据三幅图写不少于35词的故事：两个孩子在海滩发现钱包；交给警察；一位女士找回钱包并微笑。',
           '[{"en":"During a day at the beach, Ben and Sara found a wallet beside a chair.","cn":"在海滩游玩时，Ben和Sara在椅子旁发现了一个钱包。"},{"en":"There was some money inside, but they knew it belonged to someone else.","cn":"里面有一些钱，但他们知道钱包属于别人。"},{"en":"They took it to a police officer who was walking nearby.","cn":"他们把钱包交给了附近巡逻的警察。"},{"en":"Later, a worried woman collected it and thanked the children with a big smile.","cn":"后来，一位焦急的女士领回钱包，并笑着感谢两个孩子。"}]',
           '["说明发现钱包的地点","描述钱包或人物判断","说明把钱包交给警察","写出失主找回钱包的结局"]', 4, 35, 405
    UNION ALL SELECT 'writing', '写作P7-06 自行车爆胎',
           'Write a story of 35 words or more. Picture 1: Friends cycle in the countryside. Picture 2: One bicycle gets a flat tyre. Picture 3: A farmer helps them repair it.',
           '根据三幅图写不少于35词的故事：朋友们在乡间骑车；一辆车爆胎；农民帮助修好。',
           '[{"en":"Jack and his friends were cycling through the countryside on a warm afternoon.","cn":"一个温暖的下午，Jack和朋友们骑车经过乡间。"},{"en":"Suddenly, Jack heard a strange noise and saw that his tyre was flat.","cn":"突然Jack听到奇怪的声音，发现轮胎瘪了。"},{"en":"They had no tools, so they walked to a nearby farm for help.","cn":"他们没有工具，于是走到附近农场求助。"},{"en":"A kind farmer repaired the tyre, and the friends continued their journey happily.","cn":"一位好心农民修好轮胎，朋友们高兴地继续旅程。"}]',
           '["交代骑行背景","描述轮胎问题","说明如何寻求帮助","写出修好后继续旅程"]', 4, 35, 406
    UNION ALL SELECT 'writing', '写作P7-07 图书馆里的作业',
           'Write a story of 35 words or more. Picture 1: A student starts homework at home. Picture 2: The internet stops working. Picture 3: The student finishes the work in a library.',
           '根据三幅图写不少于35词的故事：学生在家做作业；网络中断；去图书馆完成作业。',
           '[{"en":"After school, Nina started a geography project on her computer at home.","cn":"放学后，Nina在家用电脑开始做地理项目。"},{"en":"She needed some information, but suddenly the internet stopped working.","cn":"她需要查资料，但网络突然中断了。"},{"en":"Nina packed her books and hurried to the local library before it closed.","cn":"Nina收好书，在图书馆关门前赶了过去。"},{"en":"She found everything she needed there and finished the project on time.","cn":"她在那里找到了所需资料，并按时完成了项目。"}]',
           '["说明作业任务","描述网络故障","说明前往图书馆","写出按时完成的结果"]', 4, 35, 407
    UNION ALL SELECT 'writing', '写作P7-08 第一次滑冰',
           'Write a story of 35 words or more. Picture 1: A boy puts on skates. Picture 2: He falls on the ice. Picture 3: A friend helps him and he skates successfully.',
           '根据三幅图写不少于35词的故事：男孩穿上冰鞋；在冰上摔倒；朋友帮助后成功滑行。',
           '[{"en":"Ryan felt excited when he tried ice skating for the first time.","cn":"Ryan第一次尝试滑冰时非常兴奋。"},{"en":"He moved too quickly, lost his balance and fell onto the ice.","cn":"他滑得太快，失去平衡摔在冰面上。"},{"en":"His friend showed him how to bend his knees and move slowly.","cn":"朋友教他弯曲膝盖并慢慢移动。"},{"en":"After some practice, Ryan skated across the rink without falling and felt proud.","cn":"练习一会儿后，Ryan没有摔倒就滑过冰场，感到很自豪。"}]',
           '["说明第一次滑冰和感受","描述摔倒","说明朋友如何帮助","写出成功和最终感受"]', 4, 35, 408
    UNION ALL SELECT 'writing', '写作P7-09 种下向日葵',
           'Write a story of 35 words or more. Picture 1: A girl plants sunflower seeds. Picture 2: She waters them every day. Picture 3: A tall sunflower grows and she takes a photo.',
           '根据三幅图写不少于35词的故事：女孩种下向日葵种子；每天浇水；花长高后拍照。',
           '[{"en":"In spring, Maya planted some sunflower seeds in a sunny part of the garden.","cn":"春天，Maya在花园阳光充足的地方种下向日葵种子。"},{"en":"She watered the ground every morning and waited patiently.","cn":"她每天早晨给土地浇水并耐心等待。"},{"en":"After several weeks, one plant became taller than Maya.","cn":"几周后，一株向日葵长得比Maya还高。"},{"en":"When its yellow flower opened, she took a photo to show her classmates.","cn":"黄色花朵开放时，她拍照给同学们看。"}]',
           '["说明种植时间和地点","描述每天照料植物","描述植物生长变化","写出开花和拍照"]', 4, 35, 409
    UNION ALL SELECT 'writing', '写作P7-10 博物馆走散',
           'Write a story of 35 words or more. Picture 1: A class visits a museum. Picture 2: One boy cannot find his group. Picture 3: A guide helps him meet them again.',
           '根据三幅图写不少于35词的故事：班级参观博物馆；男孩找不到同伴；工作人员帮助他重新会合。',
           '[{"en":"A class was visiting a large history museum when Oliver stopped to look at an old train.","cn":"一个班正在参观大型历史博物馆，Oliver停下来看一列老火车。"},{"en":"When he turned around, his teacher and classmates had disappeared.","cn":"他转身时，老师和同学们已经不见了。"},{"en":"Oliver stayed calm and asked a museum guide for help.","cn":"Oliver保持冷静，向博物馆工作人员求助。"},{"en":"The guide called the teacher, and Oliver soon joined his group near the cafe.","cn":"工作人员联系了老师，Oliver很快在咖啡馆附近重新加入队伍。"}]',
           '["交代参观博物馆","说明因停留而走散","描述正确求助方式","写出重新会合的结局"]', 4, 35, 410
    UNION ALL SELECT 'writing', '写作P7-11 风筝挂在树上',
           'Write a story of 35 words or more. Picture 1: Children fly a kite in a field. Picture 2: The kite gets stuck in a tree. Picture 3: They use a long branch to get it down.',
           '根据三幅图写不少于35词的故事：孩子们在田野放风筝；风筝挂在树上；用长树枝取下来。',
           '[{"en":"On a windy day, two children took their new kite to a field.","cn":"一个有风的日子，两个孩子带着新风筝去了田野。"},{"en":"The kite flew high, but a strong wind blew it into a tree.","cn":"风筝飞得很高，但一阵强风把它吹进了树上。"},{"en":"They could not reach it, so they found a long fallen branch.","cn":"他们够不到风筝，于是找到一根掉落的长树枝。"},{"en":"Together, they carefully pushed the kite free and continued playing.","cn":"他们一起小心地把风筝推下来，然后继续玩。"}]',
           '["说明天气和放风筝","描述风筝挂树","说明寻找工具","写出合作取回并继续玩"]', 4, 35, 411
    UNION ALL SELECT 'writing', '写作P7-12 为奶奶准备惊喜',
           'Write a story of 35 words or more. Picture 1: Two children decorate a room. Picture 2: Their grandmother arrives. Picture 3: They give her a handmade card and cake.',
           '根据三幅图写不少于35词的故事：两个孩子装饰房间；奶奶到来；送上手工卡片和蛋糕。',
           '[{"en":"On Saturday morning, Emma and her brother planned a surprise for their grandmother.","cn":"周六早晨，Emma和哥哥计划给奶奶一个惊喜。"},{"en":"They decorated the living room and made a small chocolate cake.","cn":"他们装饰了客厅，还做了一个小巧克力蛋糕。"},{"en":"When their grandmother opened the door, everyone shouted, Surprise!","cn":"奶奶打开门时，大家都喊道：惊喜！"},{"en":"The children gave her a handmade card, and she hugged them happily.","cn":"孩子们送给她一张手工卡片，她开心地拥抱了他们。"}]',
           '["说明准备惊喜","描述装饰或做蛋糕","描述奶奶到来和反应","写出赠送卡片及温暖结局"]', 4, 35, 412
) AS bank
WHERE NOT EXISTS (
    SELECT 1
    FROM esc_personal_practice existing
    WHERE existing.practice_type = bank.practice_type
      AND existing.title = bank.title
      AND existing.tenant_id = 1
      AND existing.deleted = b'0'
);
