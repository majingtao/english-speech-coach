-- =====================================================================
-- esc_seed_flyers.sql   (auto-generated, do not hand-edit)
-- 由 sql/kugua-module-english/migrate_question_bank.py 从
--   realtime/web/question-bank.json 生成
-- 共 17 套 Cambridge Flyers 试卷，全部 tenant_id=0 公共题库
-- =====================================================================

SET NAMES utf8mb4;
START TRANSACTION;

-- ============================================================
-- gf_1  Go Flyers - Test 1
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('gf_1',1,1,'flyers','Go Flyers - Test 1','Go Flyers Teacher''s Notes',NULL,1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture there''s a girl on a rock in the lake, but in your picture there isn''t.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of an outdoor picnic scene near a lake (tent, bikes, blanket)',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, two girls are sitting under a tree.','In my picture, two boys are sitting under a tree.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, the woman is eating pizza.','In my picture, the woman is eating a sandwich.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture there is a pizza on the blanket.','In my picture, there isn''t a pizza on the blanket.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, one boy is riding his bike.','In my picture, two boys are riding their bikes.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, a man is reading a magazine outside the tent.','In my picture, a man is reading a newspaper outside the tent.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Richard and Harry are friends who go to school together.
Richard and Harry are friends. They go to school together. I don''t know anything about Richard''s programme. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Richard and Harry are friends who go to school together.','Richard and Harry are friends. They go to school together. I don''t know anything about Richard''s programme. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'What time does he get up in the morning?','At 7:00.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'What time does he go to school?','At 8:00.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'What does he do at 4:00 p.m.?','He does his homework.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'What does he do at 7:00 p.m.?','He has a tennis lesson.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'What time does he go to bed?','At 10:00.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Richard and Harry are friends who go to school together.','Now, you don''t know anything about Harry''s programme, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'get up — time?','What time does he get up?','At 7:30.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'go to school — time?','What time does he go to school?','At 8:15.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'2 o''clock — do?','What does he do at 2 o''clock?','He has lunch.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'6 p.m. — do?','What does he do at 6 p.m.?','He plays football.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'go to bed — time?','What time does he go to bed?','At 10:30.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','Betty''s visit to London
These pictures tell a story. It''s called ''Betty''s visit to London''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Betty, her brother Fred and their parents visit London. They arrive there on Monday and they go for a walk. They see Big Ben.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'On Tuesday Betty and her family go shopping. They buy many presents and souvenirs.','What day is it? What does the family do? | What day is it? What do they do? | What do they buy?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'On Wednesday Betty writes a postcard to her grandmother.','What day is it? What is Betty doing? | What day is it? What does Betty do?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'On Thursday the family visits the zoo. They see a lot of animals there. Betty is afraid of the snakes and her brother Fred laughs.','What day is it? Where is the family? What do they see? Is Betty afraid of the snakes? Who laughs? | What day is it? Where do they go? | What do they see at the zoo? | Is Betty afraid of the snakes? Who laughs?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'On Friday they are at the airport. It is the last day in London. They have their bags with them. Betty''s father takes pictures of the family.','What day is it? Where is the family? What have they got with them? What does the father do? | What day is it? Where are they? | Is it their last day? | What have they got with them? | What does the father do?',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now let''s talk about what you do in your free time.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'What do you do after school?','Free time',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I do my homework.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'How often do you go out with your friends?','Free time',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Three times a week.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'What do you usually do at the weekend?','Free time',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I visit my grandparents.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'Do you play any sports?','Free time',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I play football.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'What other things do you do in your free time?','Free time',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I sometimes go to the cinema.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Do you play basketball or football?','Free time (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I play football.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you go to the cinema?','Free time (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Do you play computer games with your friends?','Free time (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, sometimes.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Do you help with the housework?','Free time (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);

-- ============================================================
-- gf_2  Go Flyers - Test 2
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('gf_2',1,1,'flyers','Go Flyers - Test 2','Go Flyers Teacher''s Notes',NULL,1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture two firemen are drinking coffee, but in your picture they are eating.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of a fire station',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, the man who is going to the toilet is wearing a black shirt.','In my picture, the man who is going to the toilet is wearing a white shirt.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, the driver has got a moustache.','In my picture, the driver in the fire engine has got a beard.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, the man is going down the stairs.','In my picture, a man is going up the stairs.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, there is a bookshop next to the fire station.','In my picture, there is a restaurant next to the fire station.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, there is a bike near the trees.','In my picture, there is a motorbike near the trees.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Mr Brown and Mrs Silver are two teachers.
Mr Brown and Mrs Silver are two teachers. I don''t know anything about Mr Brown, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Mr Brown and Mrs Silver are two teachers.','Mr Brown and Mrs Silver are two teachers. I don''t know anything about Mr Brown, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'What''s his name?','Richard.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'How old is he?','He''s 37.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'What subject does he teach?','Geography.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'How many years has he been a teacher?','Eight.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'Is he married?','Yes.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Mr Brown and Mrs Silver are two teachers.','Now, you don''t know anything about Mrs Silver, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'name — ?','What''s her name?','Emma.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'age — ?','How old is she?','She''s 35.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'subject — teach?','What subject does she teach?','History.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'teacher — how many years?','How many years has she been a teacher?','Six.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'married — ?','Is she married?','Yes.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','Michael''s dream
These pictures tell a story. It''s called ''Michael''s dream''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Michael is very tired. He brushes his teeth and he''s getting ready to go to bed.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'Michael is sleeping and he is dreaming. In his dream, he is in a desert and he is riding a camel. It''s very hot and Michael is very thirsty.','What is Michael doing? | What is Michael doing in bed? | What is happening in his dream? | How is the weather? How does Michael feel?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'Michael is now on a big bird. Michael is flying in the sky on the bird and he is excited.','Where is Michael now? | Where is Michael now? | What is he doing on the bird? How does he feel?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'Michael is in space now. He is going to the moon in a rocket. He is ready to get off the rocket and walk on the moon.','Where is Michael? What does he want to do? | Where is Michael now? | Where is he going? How? | What does he want to do on the moon?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'Michael falls off the bed and wakes up. He was dreaming.','What happens to Michael? Why? | What happens to Michael? | Why did he fall?',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now, let''s talk about what you do in the summer.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'Where do you like going for your holidays?','Summer holidays',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'To an island.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'What time do you usually get up?','Summer holidays',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'At 11 o''clock.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'Who do you spend your summer with?','Summer holidays',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'My family.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'What do you like doing in the summer?','Summer holidays',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I go swimming.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tell me about other things you do in the summer.','Summer holidays',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I do water sports.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Do you do water sports?','Summer holidays (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you spend time with your friends?','Summer holidays (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Do you look for shells on the beach?','Summer holidays (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, sometimes.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Do you go fishing?','Summer holidays (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'No, I don''t.',NULL,1,0);

-- ============================================================
-- gf_3  Go Flyers - Test 3
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('gf_3',1,1,'flyers','Go Flyers - Test 3','Go Flyers Teacher''s Notes',NULL,1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture the boy who is carrying the bottles has longer hair, but in your picture he has shorter hair.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of a countryside scene (farm, lake, bridge, truck)',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, the girl isn''t wearing glasses.','In my picture, the girl who is cleaning the table is wearing glasses.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, there are three rabbits behind the tree.','In my picture, there are two rabbits behind the tree.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, there is a duck in the lake.','In my picture, there is a swan in the lake.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, the woman isn''t wearing a ring.','In my picture, the woman on the bridge is wearing a ring.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, there isn''t a driver in the truck.','In my picture, there is a driver in the truck.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Robert and William are brothers who each have a pet.
Robert and William are brothers. They have got two pets. I don''t know anything about Robert''s pet, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Robert and William are brothers who each have a pet.','Robert and William are brothers. They have got two pets. I don''t know anything about Robert''s pet, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'What pet has he got?','A rabbit.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'What''s its name?','Snowball.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'What colour is it?','It''s white.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'What''s its favourite food?','Carrots.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'Where does it live?','It lives in a cage.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Robert and William are brothers who each have a pet.','Now, you don''t know anything about William''s pet, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'pet — what?','What pet has he got?','A snake.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'name — ?','What''s its name?','Ice.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'colour — ?','What colour is it?','It''s black and white.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'favourite food — ?','What''s its favourite food?','Spiders.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'live — where?','Where does it live?','It lives in a cage.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','A hungry bird
These pictures tell a story. It''s called ''A hungry bird''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Betty and her mum are making a cake.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'Betty and her mum are in the kitchen. Betty''s mum is putting some flour in a bowl and Betty is taking the eggs out of the fridge. Their pet parrot is in a cage.','What is Betty doing? What is her mother doing? Where is the parrot? | Where are Betty and her mum? | What are Betty and her mum doing? | Where is the parrot?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'Betty is holding the bowl. Her mother is looking for something in a cupboard. A boy is in the kitchen and is giving some water to the parrot.','Who is in the kitchen? What is Betty doing now? | What is Betty doing? | What is her mother doing? | Who else is in the kitchen? What is he doing?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'The boy leaves the room but he has left the door of the cage open. The parrot has come out of the cage. Betty and her mother don''t see that. The parrot is eating the butter.','Where is the parrot now? Where is the butter? | What did the boy forget to do? | Where is the parrot now? | Do Betty and her mother notice? | What is the parrot doing?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'The parrot has taken the chocolate to its cage. Betty and her mother can''t find anything on the table.','Where is the chocolate? Is the parrot in the cage? | What has the parrot done with the chocolate? | What do Betty and her mother see on the table?',5,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,6,'Betty is trying to see where the parrot is and she sees it in the cage. It''s just eaten all the chocolate. Betty and her mother are both angry.','Where is the parrot? What has it eaten? Are Betty and her mother happy? | Where does Betty find the parrot? | What has the parrot eaten? | How do Betty and her mother feel?',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now, let''s talk about school.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'What time do you get there?','School',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'At 8 o''clock.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'How do you get to school?','School',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I walk to school.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'What''s your favourite subject?','School',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Maths.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'What sports do you play at school?','School',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Basketball and volleyball.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tell me about other things you do at school.','School',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I spend time with my friends in the playground.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Do you spend time with your friends?','School (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you draw pictures?','School (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, sometimes.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Do you have lunch at school?','School (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);

-- ============================================================
-- gf_4  Go Flyers - Test 4
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('gf_4',1,1,'flyers','Go Flyers - Test 4','Go Flyers Teacher''s Notes',NULL,1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture the man is holding an octopus, but in your picture he''s holding a fish.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of a fishing trip (boat, boys, fish)',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture it isn''t raining.','In my picture, it''s windy and it''s raining.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, he has got a fork in his pocket.','In my picture, the first boy has got a knife in his pocket.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, he isn''t carrying a rucksack.','In my picture, the third boy is carrying a rucksack.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, the third boy is holding a map.','In my picture, the third boy isn''t holding anything.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, there are five fish.','In my picture, there are four fish in the boat.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Helen and Katy are best friends, each with a favourite toy.
Helen and Katy are best friends. They have got a favourite toy each. I don''t know anything about Helen''s favourite toy, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Helen and Katy are best friends, each with a favourite toy.','Helen and Katy are best friends. They have got a favourite toy each. I don''t know anything about Helen''s favourite toy, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'What is Helen''s favourite toy?','A doll.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'What colour is it?','It''s pink.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'What is it made of?','Plastic.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'Why does she like it?','Her mum bought it for her.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'How long has she had it?','A year.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Helen and Katy are best friends, each with a favourite toy.','Now, you don''t know anything about Katy''s favourite toy, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'favourite toy — what?','What is Katy''s favourite toy?','A rabbit.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'colour — ?','What colour is it?','It''s brown.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'made of — ?','What is it made of?','Wood.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'like — why?','Why does she like it?','She''s always wanted to have a pet.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'had it — how long?','How long has she had it?','6 months.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','Harry''s castle
These pictures tell a story. It''s called ''Harry''s castle''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Harry has to make a castle for a school project.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'Harry''s teacher is holding a castle and tells the students to make a castle, too.','What is the teacher doing? | What is the teacher showing? What does she tell the students?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'Harry goes back home. He is in the garage and he is thinking how to make the castle. He is sad. His dad comes in the garage carrying a box.','Where is Harry now? How does he feel? What is his dad carrying? | Where does Harry go? | Where is he? What is he thinking about? | How does Harry feel? | Who comes in? What is he carrying?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'Harry has an idea. He is going to ask his father to help him. They are going to use the box, some glue and some coloured pencils to make the castle.','Is Harry sad now? What is he thinking about? | Does Harry have a plan now? | Who is he going to ask for help? | What will they use to make the castle?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'His dad is helping him. The castle is ready and Harry is very happy now. Harry''s dad is putting a flag on the castle.','Is the castle ready? Does Harry like it? What is Harry''s dad putting on the castle? | Who is helping Harry? | Is the castle ready? How does Harry feel? | What is his dad putting on the castle?',5,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,6,'Harry takes the castle to school and everyone likes it.','Do Harry''s friends like the castle? | What happens at school?',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now, let''s talk about the future.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'What job would you like to do?','The future',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Businessman.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'How old are you going to be when you get married?','The future',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'25.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'Where would you like to live?','The future',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'In a big city.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'How many children would you like to have?','The future',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Two.',NULL,1,0);

-- ============================================================
-- gf_5  Go Flyers - Test 5
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('gf_5',1,1,'flyers','Go Flyers - Test 5','Go Flyers Teacher''s Notes',NULL,1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture there are five plates on the table, but in your picture there are six plates.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of an indoor scene with a table, plant, and a woman',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, the bottle is empty.','In my picture, the bottle of water is full.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, there are sweets on the table.','In my picture, there are flowers on the table.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, there is a belt under the table.','In my picture, there is a pair of scissors under the table.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, there are two insects on the plant.','In my picture, there is an insect on the plant.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, the woman is wearing a striped dress.','In my picture, the woman is wearing a spotted dress.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Betty and Helen are friends who each went on a holiday.
Betty and Helen are friends. They took some pictures of their last holiday. I don''t know anything about Betty''s holiday, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Betty and Helen are friends who each went on a holiday.','Betty and Helen are friends. They took some pictures of their last holiday. I don''t know anything about Betty''s holiday, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'Where did she go?','To the beach.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'Where did she stay?','In a tent.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'Who did she go with?','With her family.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'When did she go?','In July.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'What did she do there?','She went swimming.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Betty and Helen are friends who each went on a holiday.','Now, you don''t know anything about Helen''s holiday, but I do. So you are going to ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'go — where?','Where did she go?','In the mountains.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'stay — where?','Where did she stay?','At a hotel.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'go with — who?','Who did she go with?','With her grandparents.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'go — when?','When did she go?','In June.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'do there — what?','What did she do there?','She went for walks in the forest.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','Family shopping
These pictures tell a story. It''s called ''Family shopping''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Richard and his parents go to a shop. There are bookcases there.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'Richard''s mum is looking at some silver spoons, forks and knives and she likes them. I think she wants to buy them.','What is Richard''s mum looking at? How does she feel about them? What do you think she wants to do? | What is Richard''s mum looking at? Does she like them? | What do you think she wants to do?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'Now the family is in a room with things made of paper. For example, there are lots of boxes, envelopes and books.','Where are the people now? What are these things made of? | Where are they now? What are things made of? | What things can you see?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'Now the family is looking at some things made of glass, like vases. Richard drops one. He looks very unhappy.','What happens then? What are these made of? What does Richard do? How does he feel? | What are they looking at now? What are these made of? | What does Richard do? | How does Richard feel?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'In the end, the glass breaks.','What happens in the end? | What happens in the end?',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now, let''s talk about food.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'What''s your favourite food?','Food',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Pizza.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'What time do you have breakfast?','Food',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'At 8 o''clock.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'How often do you eat chocolate?','Food',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Three times a week.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'What do you usually have for dinner?','Food',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'A sandwich.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Now tell me what other food you like eating.','Food',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I like pasta and chicken.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Do you like pasta or chicken for lunch?','Food (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I like pasta.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you love eating sweets?','Food (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Do you drink orange juice?','Food (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);

-- ============================================================
-- f4_1  官方真题2022 - Test 1
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('f4_1',1,1,'flyers','官方真题2022 - Test 1','Flyers 4','2022',1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture there are two fish, but in your picture there are three.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of a museum/dinosaur scene with a tent, fire, and tiger display',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, the dinosaur''s tail is short.','In my picture, the dinosaur''s tail is long.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, I can see a bottle in his backpack.','In my picture, I can see a torch in the boy''s backpack.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, the sky is blue.','In my picture, the sky is yellow.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, it''s above the tiger.','In my picture, the spider is below the tiger.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, the door of the tent is closed.','In my picture, the door of the tent is open.',5,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,6,'In my picture, there are three stones around the fire.','In my picture, there are two stones around the fire.',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Harry and Helen have new books to read.
Harry and Helen have new books to read. I don''t know anything about Harry''s new book, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Harry and Helen have new books to read.','Harry and Helen have new books to read. I don''t know anything about Harry''s new book, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'What''s the name of Harry''s new book?','The Snowman.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'How many pages has the book got?','145.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'When did he buy it?','Yesterday.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'Is the book funny or sad?','It''s sad.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'Where did Harry buy it?','In the bookshop.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Harry and Helen have new books to read.','Now you don''t know anything about Helen''s new book, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'name — ?','What''s the name of Helen''s book?','The Circus Clown.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'where / buy — ?','Where did she buy it?','In the supermarket.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'when / buy — ?','When did she buy it?','On Saturday.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'how many pages — ?','How many pages has the book got?','200.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'funny / sad — ?','Is the book funny or sad?','It''s funny.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','Mum and the fruit salad
These pictures tell a story. It''s called ''Mum and the fruit salad''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Ben''s mum wants to make fruit salad. She''s just bought a lot of fruit. Now they''re going home.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'The fruit is falling out of Mum''s bags. A boy is picking up the fruit.','What''s happening to the fruit? What is the boy doing? | What''s happening to the fruit? | What is the boy doing?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'Mum''s saying, ''Where''s my fruit?'' Ben can see some people with the fruit.','What''s Mum saying? What can Ben see? | What''s Mum saying? | What can Ben see?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'The people are giving the fruit to Mum and Ben. Mum''s saying, ''Thank you.''','Has Mum got her fruit now? What''s she saying? | Has Mum got her fruit now? | What''s Mum saying?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'Mum''s making fruit salad. Everyone''s eating it.','What''s Mum doing? And the other people? | What''s Mum doing? | What are the other people doing?',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now let''s talk about your city, town or village.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'Where do you live?','Your city/town/village',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I live in Beijing.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'How do you go to school?','Your city/town/village',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I go by car.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'What''s your favourite place in your city/town/village?','Your city/town/village',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'The sports centre.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'Where do you go shopping?','Your city/town/village',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I go shopping in the town centre.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tell me what you do at the weekends in your city/town/village.','Your city/town/village',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I go to the cinema. I go to a restaurant with my family.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Do you live in a city or a village?','Your city/town/village (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I live in a city.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you go to school by car?','Your city/town/village (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Do you like the sports centre?','Your city/town/village (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Do you go to the cinema?','Your city/town/village (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, sometimes.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,10,'Do you play football?','Your city/town/village (followup)',10,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I play with my friends.',NULL,1,0);

-- ============================================================
-- f4_2  官方真题2022 - Test 2
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('f4_2',1,1,'flyers','官方真题2022 - Test 2','Flyers 4','2022',1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture the racing cars are red and blue, but in your picture they are red and yellow.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of a bedroom scene with toys',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, there are two eagles.','In my picture, there''s a camel on the poster.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, the tortoise is under the desk.','In my picture, the tortoise is on the rug.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, the robot''s sitting on the shelf.','In my picture, the robot is standing on the shelf.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, there''s a bridge outside the window.','In my picture, there''s a castle outside the window.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, there''s a photo on the computer screen.','In my picture, there''s a message on the computer screen.',5,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,6,'In my picture, the bin is on the left.','In my picture, the bin is on the right.',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Robert and Vicky have both got a present.
Robert and Vicky have both got a present. I don''t know anything about Robert''s present, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Robert and Vicky have both got a present.','Robert and Vicky have both got a present. I don''t know anything about Robert''s present, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'When did Robert get his present?','This morning.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'Who gave Robert his present?','His uncle.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'What''s the present?','It''s a computer.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'What colour is it?','It''s grey.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'Where will Robert put it?','In the living room.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Robert and Vicky have both got a present.','Now you don''t know anything about Vicky''s present, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'present — ?','What''s Vicky''s present?','A football shirt.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'who / give — ?','Who gave her the present?','Her aunt.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'where / put — ?','Where will she put it?','In her bedroom.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'when / get — ?','When did she get it?','Yesterday.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'colour — ?','What colour is it?','Red and black.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','Peter and Charlie get lost
These pictures tell a story. It''s called ''Peter and Charlie get lost''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Two brothers, Peter and Charlie, are camping. They decide to go for a long walk in the jungle.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'Peter and Charlie are in the jungle. A monkey''s throwing bananas to them.','Where are Peter and Charlie? What''s the monkey doing? | Where are Peter and Charlie? | What''s the monkey doing?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'The boys are looking at a map. They''re lost.','What are the boys doing now? Are the boys lost? | What are the boys doing? | Are the boys lost?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'The monkey can see the boys'' tent.','What can the monkey see? | What can the monkey see?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'The monkey''s taking the boys back to their tent. They''re happy.','Where''s the monkey taking the boys? Are they happy? | Where''s the monkey taking the boys? | Are they happy?',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now let''s talk about food.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'What do you have for breakfast?','Food',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Orange juice and bread.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'Where do you usually eat at home?','Food',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'In the kitchen.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'What time do you have lunch?','Food',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'At one o''clock.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'What food don''t you like?','Food',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I don''t like fish.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tell me about dinner in your home.','Food',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'My mum cooks dinner. We have chicken and rice. We eat at 8 o''clock.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Do you have orange juice?','Food (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you usually eat in the kitchen?','Food (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Do you have lunch at one o''clock?','Food (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Do you like fish?','Food (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'No, I don''t.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,10,'Who cooks dinner?','Food (followup)',10,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'My mum cooks dinner.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,11,'What time do you eat dinner?','Food (followup)',11,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'We eat at 8 o''clock.',NULL,1,0);

-- ============================================================
-- f4_3  官方真题2022 - Test 3
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('f4_3',1,1,'flyers','官方真题2022 - Test 3','Flyers 4','2022',1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture a dog is at the back of the truck, but in your picture it''s at the front.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of an airport scene',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, three people are on the steps of the plane.','In my picture, one person is on the steps of the plane.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, the blue plane is coming down.','In my picture, the blue plane is going up into the sky.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, the woman''s scarf is orange.','In my picture, the woman in the uniform is wearing a green scarf.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, the number is 57.','In my picture, the green number above the plane is 24.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, the man''s suitcase hasn''t got wheels on it.','In my picture, the man''s suitcase has got wheels on it.',5,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,6,'In my picture, I can see a fire engine.','In my picture, I can see an ambulance near the skyscrapers.',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Michael and Emma go to a sports club every week.
Michael and Emma go to a sports club every week. I don''t know anything about Michael''s sports club, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Michael and Emma go to a sports club every week.','Michael and Emma go to a sports club every week. I don''t know anything about Michael''s sports club, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'What sport does Michael do there?','Tennis.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'Where''s Michael''s club?','In Hill Street.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'What time does Michael go?','At half past six.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'Who does Michael go with?','His brother.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'Which day does Michael go there?','On Monday.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Michael and Emma go to a sports club every week.','Now you don''t know anything about Emma''s sports club, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'where — ?','Where is Emma''s club?','In Lake Road.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'which day — ?','Which day does Emma go?','On Saturday.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'sport — ?','What sport does Emma do?','Sailing.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'time — ?','What time does Emma go?','At half past two.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'who / with — ?','Who does Emma go with?','Her best friend.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','Tom and Betty go to the hospital
These pictures tell a story. It''s called ''Tom and Betty go to the hospital''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Tom and Betty''s mum is in hospital because she''s broken her leg. Tom and Betty and their dad are going to the hospital to visit her.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'Tom''s giving his mum a present. Mum''s smiling.','What''s Tom doing? | What''s Tom doing? | How does Mum feel?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'Mum''s opened her present. She''s got some chocolates.','What is the present? | What has Mum done? | What is the present?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'Betty''s taking the chocolates. Tom''s telling his parents but they aren''t listening.','What''s Betty doing? And what''s Tom doing? | What''s Betty doing? | What''s Tom doing?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tom''s angry because Betty''s eaten all the chocolates. Mum and Dad are laughing.','Why is Tom angry? | Why is Tom angry? | What are Mum and Dad doing?',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now let''s talk about your school.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'How many children are there in your class?','Your school',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Thirty.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'What colour is your classroom?','Your school',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Blue.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'What''s your favourite lesson?','Your school',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Maths.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'Where do you eat lunch?','Your school',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'At school.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tell me about your best friend at school.','Your school',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'His name''s Tom. He''s eleven. We play games together.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Are there thirty children in your class?','Your school (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, there are.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Is your classroom blue?','Your school (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, it is.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Do you like maths?','Your school (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Do you eat lunch at school?','Your school (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,10,'What''s his/her name?','Your school (followup)',10,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'His name''s Tom.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,11,'What do you do together?','Your school (followup)',11,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'We play games.',NULL,1,0);

-- ============================================================
-- aep1_1  官方真题2018(1) - Test 1
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('aep1_1',1,1,'flyers','官方真题2018(1) - Test 1','Authentic Exam Papers 1','2018',1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture there are two trees, but in your picture there are three.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of an outdoor scene with a car, tent, trees, and lake',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, it''s yellow.','In my picture, the car is blue.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, there''s a butterfly above the tent.','In my picture, there''s a bat above the tent.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, the girl is wearing blue shorts.','In my picture, the girl is wearing white shorts.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, she''s holding a flag.','In my picture, the girl''s holding a toothbrush.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, there isn''t a rabbit in the car.','In my picture, there''s a rabbit in the car.',5,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,6,'In my picture, the woman''s coming out of the tent.','In my picture, the woman''s going into the tent.',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Mary''s house and Tom''s house are on the same street.
Mary''s house and Tom''s house are on the same street. I don''t know anything about Mary''s house, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Mary''s house and Tom''s house are on the same street.','Mary''s house and Tom''s house are on the same street. I don''t know anything about Mary''s house, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'How many people live in the house?','6 people.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'How old is the house?','8 years old.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'What colour is the front door?','It''s red.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'Where does Mary do her homework?','In her bedroom.',4,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Mary''s house and Tom''s house are on the same street.','Now you don''t know anything about Tom''s house, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'big or small — ?','Is Tom''s house big or small?','It''s small.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'how old — ?','How old is the house?','100 years old.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'homework — where?','Where does Tom do his homework?','In the dining room.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'front door — colour?','What colour is the front door?','It''s white.',4,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','Betty''s birthday party
These pictures tell a story. It''s called ''Betty''s birthday party''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'It''s Betty''s birthday today. Her mother''s making a cake. Betty''s getting some sugar for the cake. She has to stand on a chair.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'Betty has fallen. She has hurt her leg. Betty can''t walk. She''s going to bed.','What has happened? What has Betty hurt? Why is Betty going to bed? | What has happened to Betty? | Why is Betty going to bed?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'She''s very sad. Betty''s sleeping. Her friends have come to see her.','How is she feeling? What is Betty doing now? Who has come to see her? | How is Betty feeling? | What is Betty doing? Who has come?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'Betty''s friends are giving her presents. Her mother''s bringing the birthday cake. Everyone''s enjoying the party.','What are Betty''s friends giving her? What''s her mother bringing? Is it a good party? | What are Betty''s friends doing? | What''s her mother bringing? | Is it a good party?',4,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now let''s talk about your school.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'What time do you go to school?','Your school',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'At 8 o''clock.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'How do you go to school?','Your school',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'By bus.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'What''s your favourite lesson?','Your school',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'English.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'What sports do you play at school?','Your school',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Football.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tell me about your best friend at school.','Your school',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'His name''s Tom. He''s tall. He has short hair.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Do you go to school at 8 o''clock?','Your school (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you go by bus?','Your school (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Do you like English?','Your school (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Do you play football?','Your school (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,10,'What''s his/her name?','Your school (followup)',10,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'His name''s Tom.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,11,'Is he/she tall? Does he/she have short hair?','Your school (followup)',11,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, he''s tall and has short hair.',NULL,1,0);

-- ============================================================
-- aep1_2  官方真题2018(1) - Test 2
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('aep1_2',1,1,'flyers','官方真题2018(1) - Test 2','Authentic Exam Papers 1','2018',1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture the camel''s got a short tail, but in your picture it''s got a long tail.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of a zoo or outdoor scene with animals, an island, and ice-cream seller',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, there are spots on the tent.','In my picture, there are stripes on the tent.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, there are five planes.','In my picture, there are four planes.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, the man with the white hat is selling burgers.','In my picture, I can see a man with a white hat. He''s selling ice-cream.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, the elephant is in front of the trees.','In my picture, the elephant is behind the trees.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, the woman with the umbrella is talking to a boy.','In my picture, the woman with the umbrella is talking to a girl.',5,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,6,'In my picture, there isn''t a swan on the island.','In my picture, there''s a swan on the island.',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Charlie and Emma each get a magazine every week.
Charlie and Emma get a magazine every week. I don''t know anything about Charlie''s magazine, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Charlie and Emma each get a magazine every week.','Charlie and Emma get a magazine every week. I don''t know anything about Charlie''s magazine, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'What day does Charlie get the magazine?','On Tuesday.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'What''s the name of the magazine?','Our Planet.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'Who buys the magazine for Charlie?','His mother.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'Is it expensive?','Yes.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'What''s it about?','Animals.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Charlie and Emma each get a magazine every week.','Now you don''t know anything about Emma''s magazine, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'name — magazine?','What''s the name of Emma''s magazine?','New Faces.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'about — what?','What is the magazine about?','Music.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'expensive — ?','Is it expensive?','No, it isn''t.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'what day — ?','What day does Emma get the magazine?','On Friday.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'who — buys?','Who buys the magazine for Emma?','Her father.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','The key
These pictures tell a story. It''s called ''The key''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Sarah and her mum have come home from the shops. Sarah is carrying lots of bags. She drops her door key.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'Mum can''t find her key. Sarah''s saying, ''Look! That window''s open.''','Can Mum find the key? What''s Sarah saying? | Can Mum find the key? | What''s Sarah saying?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'Sarah''s climbing in the window.','What''s Sarah doing? What''s she climbing? | What''s Sarah doing?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'Sarah''s opening the door from the inside.','What does Sarah do next? What''s she doing at the door? | What is Sarah doing at the door?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'Mum has found the key. It was in her pocket. Sarah and her mum are laughing.','Where was the key? How do they feel? | Where was the key? | How do they feel?',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now let''s talk about the things you do.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'What do you have for breakfast?','Things you do',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I have bread and milk.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'Where do you play after school?','Things you do',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'In the park.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'What do you like doing at weekends?','Things you do',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Shopping.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'What are your hobbies?','Things you do',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Computer games.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tell me about the things you do in the evenings.','Things you do',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I watch TV. I do my homework. I go to bed at nine o''clock.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Do you have breakfast?','Things you do (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you play in the park?','Things you do (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Do you like shopping?','Things you do (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Do you play computer games?','Things you do (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, sometimes.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,10,'Do you watch TV?','Things you do (followup)',10,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,11,'What time do you go to bed?','Things you do (followup)',11,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'At nine o''clock.',NULL,1,0);

-- ============================================================
-- aep1_3  官方真题2018(1) - Test 3
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('aep1_3',1,1,'flyers','官方真题2018(1) - Test 3','Authentic Exam Papers 1','2018',1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture there are three banana trees, but in your picture there are two.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of a beach scene with banana trees',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, the man is wearing white shorts.','In my picture, the man is wearing red shorts.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, some butterflies are flying in the sky.','In my picture, some planes are flying in the sky.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, the boy hasn''t got a rucksack on his back.','In my picture, the boy in the water has got a hat.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, the car doors are open.','In my picture, the car doors are closed.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, there''s a mouse in the girl''s pocket.','In my picture, there''s a frog in the girl''s pocket.',5,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,6,'In my picture, she''s wearing a striped dress.','In my picture, the woman is wearing a spotted dress.',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Betty and David go to different schools.
Betty and David go to different schools. I don''t know anything about Betty''s school, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Betty and David go to different schools.','Betty and David go to different schools. I don''t know anything about Betty''s school, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'What''s the name of Betty''s school?','Park School.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'What time does school start?','At 8:30.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'How many children study there?','500.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'What sport does Betty play there?','Tennis.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'Where''s the school?','It''s near the post office.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Betty and David go to different schools.','Now you don''t know anything about David''s school, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'name — school?','What''s the name of David''s school?','Greenhill School.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'what time — start?','What time does school start?','At 9 o''clock.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'how many — children?','How many children study there?','400.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'sport — ?','What sport does David play there?','Basketball.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'where — school?','Where''s the school?','It''s near the cinema.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','Charlie and the elephant
These pictures tell a story. It''s called ''Charlie and the elephant''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Charlie is in the garden with his mother. He''s eating some fruit and his mother''s reading a book.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'Charlie can see an elephant. It''s coming into the garden.','What can Charlie see? What is it doing? | What can Charlie see?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'Charlie is giving the elephant a banana. The elephant''s eating it. A man from the circus has come to get the elephant.','What is Charlie giving the elephant? Who has come? | What is Charlie doing? | Who has come to the house?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'Charlie and his mother are watching the elephant in the circus. They''re very happy.','Where are Charlie and his mother? What are they watching? | Where are Charlie and his mother? | How do they feel?',4,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now let''s talk about your holidays.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'When do you have holidays?','Your holidays',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'In the summer.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'Where do you like to go on holiday?','Your holidays',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'To the beach.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'What do you like doing on holiday?','Your holidays',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Swimming.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'Who do you go on holiday with?','Your holidays',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'My family.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tell me about your best holiday.','Your holidays',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'We went to the mountains. I went skiing. I had fun.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Do you have holidays in the summer?','Your holidays (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you like going to the beach?','Your holidays (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I love it.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Do you like swimming?','Your holidays (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Do you go with your family?','Your holidays (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,10,'Where did you go last holiday? What did you do?','Your holidays (followup)',10,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'We went to the mountains. I went skiing.',NULL,1,0);

-- ============================================================
-- aep2_1  官方真题2018(2) - Test 1
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('aep2_1',1,1,'flyers','官方真题2018(2) - Test 1','Authentic Exam Papers 2','2018',1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture it''s a cloudy day, but in your picture it''s a sunny day.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of an outdoor lake scene with boats, trees, and flowers',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, there are two butterflies near the flowers.','In my picture, there are three butterflies near the flowers.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, he''s getting on the boat.','In my picture, a man''s getting off the boat.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, the swan''s on the right.','In my picture, I can see a swan on the left.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, the children are playing with a kitten.','In my picture, the children are playing with a puppy.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, he isn''t taking a photo.','In my picture, a boy with blond hair is sitting by the water. He''s taking a photo.',5,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,6,'In my picture, the leaves on the biggest tree are yellow.','In my picture, the leaves on the biggest tree are brown.',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Oliver and Helen both go to a sports club.
Oliver and Helen both go to a sports club. I don''t know anything about Oliver''s sports club, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Oliver and Helen both go to a sports club.','Oliver and Helen both go to a sports club. I don''t know anything about Oliver''s sports club, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'Where''s Oliver''s sports club?','It''s in Park Street.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'Who does he go there with?','His cousin.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'What does he play there?','Golf.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'When does he go there?','At weekends.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'Is it expensive?','Yes, it is.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Oliver and Helen both go to a sports club.','Now you don''t know anything about Helen''s sports club, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'where — club?','Where''s Helen''s sports club?','It''s in Long Road.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'when — go?','When does she go there?','On Monday evenings.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'who — with?','Who does she go with?','Her sister.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'what — play?','What does she play?','Football.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'expensive — ?','Is it expensive?','No, it isn''t.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','The aliens go for a ride
These pictures tell a story. It''s called ''The aliens go for a ride''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'The astronauts want to go home. Some aliens are helping them with their things. Two aliens would like to go with them in their rocket.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'The astronauts are saying goodbye to the aliens. Two aliens are going into the rocket.','What are the astronauts doing? Where are two aliens going? | What are the astronauts doing? Where are the aliens going?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'Now the rocket is going up into space. The astronauts are waving to the aliens on the ground.','What are the astronauts doing now? | What''s happening to the rocket?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'Now the astronauts are back on their planet. Lots of people have come to see them. One boy has seen the aliens inside the rocket.','Where are the astronauts now? What has the boy seen? | Where are the astronauts now? | What has the boy seen?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'The astronauts have seen the two aliens. They''re very surprised.','What have the astronauts seen? How do they feel? | What have the astronauts seen? How do they feel?',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now let''s talk about your mornings.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'What time do you wake up?','Your mornings',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'At 6:30.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'What do you do first?','Your mornings',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I have a shower.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'What do you have for breakfast?','Your mornings',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Cereal.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'How do you go to school?','Your mornings',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'By car.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tell me about your lunch.','Your mornings',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I go home for lunch. I have lunch with my mum. I usually have a sandwich and a drink.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Do you wake up at 6:30?','Your mornings (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you have a shower?','Your mornings (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Do you have cereal for breakfast?','Your mornings (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Do you go to school by car?','Your mornings (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,10,'Where do you have lunch?','Your mornings (followup)',10,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I go home for lunch.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,11,'Who do you have lunch with?','Your mornings (followup)',11,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'My mum.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,12,'What do you usually have to eat?','Your mornings (followup)',12,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'A sandwich and a drink.',NULL,1,0);

-- ============================================================
-- aep2_2  官方真题2018(2) - Test 2
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('aep2_2',1,1,'flyers','官方真题2018(2) - Test 2','Authentic Exam Papers 2','2018',1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture there are two children under the blue table, but in your picture there''s only one child.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of a room with a table, window, and old man',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, there''s a girl at the window.','In my picture, there''s a boy at the window.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, there''s a photo of a swan on the wall.','In my picture, there''s a photo of a butterfly on the wall.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, the old man is reading a newspaper.','In my picture, the old man is reading a letter.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, there are some knives and forks on the green table.','In my picture, there are no knives and forks on the green table.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, there isn''t any money on the floor.','In my picture, I can see some money on the floor.',5,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,6,'In my picture, all the windows are closed.','In my picture, some windows are open.',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Jill and Nick go to the same school. They each have a favourite teacher.
Jill and Nick go to the same school. I don''t know anything about Jill''s favourite teacher, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Jill and Nick go to the same school. They each have a favourite teacher.','Jill and Nick go to the same school. I don''t know anything about Jill''s favourite teacher, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'What''s Jill''s teacher''s name?','Mr May.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'Where does he live?','In West Street.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'Is his hair long or short?','Short.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'What''s his hobby?','Cycling.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'What subject does he teach?','History.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Jill and Nick go to the same school. They each have a favourite teacher.','Now you don''t know anything about Nick''s favourite teacher, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'name — ?','What''s Nick''s teacher''s name?','Miss Cave.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'subject — teach?','What subject does she teach?','Geography.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'hobby — ?','What''s her hobby?','She likes doing puzzles.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'where — live?','Where does she live?','In Park Road.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'hair — long or short?','Is her hair long or short?','It''s long.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','A very good dog
These pictures tell a story. It''s called ''A very good dog''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Frank''s dad is working in the garden, but he has lost one of his gloves. The dog has found the glove. He''s bringing it to Dad.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'Frank''s putting on his shoes, but he can''t find one. The dog''s bringing him the shoe.','What''s Frank doing? What''s the dog bringing him? | What''s Frank doing? | What''s the dog bringing him?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'Mum''s looking for the newspaper. The dog''s bringing it to her.','What''s Mum looking for? What''s the dog got? | What''s Mum looking for? | What''s the dog got?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'Now the dog''s in the garden. He''s looking for his ball. Dad''s got the ball.','What''s the dog looking for? Who''s got the ball? | What''s the dog looking for? | Who''s got the ball?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'The family are all playing ball with the dog.','What are the family doing now? | What are the family doing now?',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now let''s talk about your home.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'Where do you live?','Your home',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'In ... Street.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'Who do you live with?','Your home',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'My parents.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'How many rooms are there in your home?','Your home',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Three.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'Where do you play?','Your home',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'In my bedroom.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tell me about your living room.','Your home',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'It''s big. It''s white and brown. There''s a TV in it.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'What''s the name of your street?','Your home (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'It''s ... Street.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you live with your parents?','Your home (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Are there three rooms?','Your home (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, there are.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Do you play in your bedroom?','Your home (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,10,'Is it big or small?','Your home (followup)',10,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'It''s big.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,11,'What colour is it?','Your home (followup)',11,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'White and brown.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,12,'Is there a TV in it?','Your home (followup)',12,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, there is.',NULL,1,0);

-- ============================================================
-- aep2_3  官方真题2018(2) - Test 3
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('aep2_3',1,1,'flyers','官方真题2018(2) - Test 3','Authentic Exam Papers 2','2018',1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture it''s sunny, but in your picture it''s cloudy.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of a waterfall/bridge scene',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, it''s white.','In my picture, the bridge is green.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, a woman is wearing a striped hat.','In my picture, there''s a man in the boat near the waterfall. He''s wearing a striped hat.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, I can see two swings.','In my picture, I can see three swings.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, there are stars on the flags.','In my picture, there are moons on the flags.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, there''s a swan on the right.','In my picture, there''s a swan on the left.',5,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,6,'In my picture, he isn''t reading a newspaper.','In my picture, I can see a man under the tree. He''s reading a newspaper.',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','William and Sarah both went on holiday last year.
William and Sarah both went on holiday last year. I don''t know anything about William''s holiday, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','William and Sarah both went on holiday last year.','William and Sarah both went on holiday last year. I don''t know anything about William''s holiday, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'Who did William go on holiday with?','His grandparents.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'What was the weather like?','It was windy.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'When did he go?','In August.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'What did he buy on holiday?','Trainers.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'Where did he go?','To the beach.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','William and Sarah both went on holiday last year.','Now you don''t know anything about Sarah''s holiday, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'where — go?','Where did Sarah go?','She went to the mountains.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'who — with?','Who did she go with?','Her cousins.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'when — go?','When did she go?','In January.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'what — buy?','What did she buy on holiday?','A bracelet.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'weather — ?','What was the weather like?','It was sunny.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','Grandma and Grandpa come to help
These pictures tell a story. It''s called ''Grandma and Grandpa come to help''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'David, Emma and their mum and dad are going to have a picnic in the countryside. Grandma and Grandpa are going to stay at home.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'David and Emma are leaving in the car with their parents. Grandma''s saying, ''They have forgotten the picnic!''','Where are David and Emma? What''s Grandma saying? | Where are David and Emma? | What''s Grandma saying?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'The family have stopped in a field. Mum''s saying, ''Where''s our picnic?'' Everyone is hungry.','What''s Mum saying? Is everyone hungry? | Where has the family stopped? | What''s Mum saying?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'Grandma and Grandpa have arrived in their car. They have brought the picnic.','Who''s arrived now? What have they brought? | Who''s arrived? What have they brought?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'Everyone is eating now. They are all happy.','What are they doing now? | What are they doing now?',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now let''s talk about your city, town or village.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'Where do you live?','Your city/town/village',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'In a town.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'How do you go to school?','Your city/town/village',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'By car.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'What''s your favourite place in your city/town/village?','Your city/town/village',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'The park.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'Tell me about what you do at weekends in your city/town/village.','Your city/town/village',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I go shopping. I go with my Mum. She buys food and I buy comics.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Do you live in ...?','Your city/town/village (followup)',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Do you go to school by car?','Your city/town/village (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you like the park?','Your city/town/village (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Where do you go at weekends?','Your city/town/village (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'I go shopping.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Who do you go with?','Your city/town/village (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'My Mum.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,10,'What do you do there?','Your city/town/village (followup)',10,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'She buys food and I buy comics.',NULL,1,0);

-- ============================================================
-- aep3_1  官方真题2019 - Test 1
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('aep3_1',1,1,'flyers','官方真题2019 - Test 1','Authentic Exam Papers 3','2019',1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture two penguins are by the water, but in your picture there is only one penguin by the water.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of a winter scene with penguins, snowboard, and eagle',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, the tree is bigger than the house.','In my picture, there''s a tree behind the house. It''s smaller than the house.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, the boy''s wearing gloves.','In my picture, the boy on the snowboard isn''t wearing gloves.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, the dog''s running in the snow.','In my picture, the dog is lying in the snow.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, there''s no traffic on the road.','In my picture, there''s some traffic on the road.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, she''s wearing trainers.','In my picture, there''s a girl holding a hat. She''s wearing boots.',5,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,6,'In my picture, the eagle''s on the right.','In my picture, there''s an eagle flying in the sky. It''s on the left.',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','John and Daisy both go to an art class.
John and Daisy both go to an art class. I don''t know anything about John''s art class, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','John and Daisy both go to an art class.','John and Daisy both go to an art class. I don''t know anything about John''s art class, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'What''s the teacher''s name?','It''s Miss Bird.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'When is the class?','On Saturday.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'How many students are there in the class?','Ten.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'Is John a good artist?','Yes, he''s very good.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'What does he like painting?','He likes painting people.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','John and Daisy both go to an art class.','Now you don''t know anything about Daisy''s art class, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'teacher — name?','What''s the teacher''s name?','Mr Black.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'how many — students?','How many students are there?','Eight.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'when — class?','When is the class?','On Friday.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'what — like painting?','What does she like painting?','Animals.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'good artist — ?','Is she a good artist?','She''s not bad.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','Vicky''s strange day
These pictures tell a story. It''s called ''Vicky''s strange day''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Vicky''s mum is a doctor. She''s saying to Vicky, ''I''ve got to go to work. Be quick! You''re late for school!''',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'Vicky is running to her school. She''s fallen over. She''s crying because her leg hurts.','What is Vicky doing? Why is Vicky crying? | What is Vicky doing? | Why is Vicky crying?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'A teacher is coming to help her. Now the teacher is taking Vicky to the hospital.','Who''s coming to help her? Where''s Vicky now? | Who''s coming to help her? | Where''s Vicky now?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'Now a doctor is looking after Vicky — and the doctor is her mum! Now Vicky feels better and she''s smiling.','Who is looking after Vicky? How does Vicky feel now? | Who is looking after Vicky? | How does Vicky feel now?',4,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now let''s talk about your best friend.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'What''s your best friend''s name?','Your best friend',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'He''s/She''s called ...',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'Where do you see him/her?','Your best friend',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'At school.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'Is he/she tall or short?','Your best friend',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'He''s/She''s tall.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'What do you like doing with your friend?','Your best friend',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Playing computer games.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tell me about your best friend''s family.','Your best friend',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'He/She lives with his/her mum and dad. They live in ... They''ve got two cats.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Is your best friend''s name ...?','Your best friend (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, it is.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you see him/her at school?','Your best friend (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Is he/she tall?','Your best friend (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, he/she is.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Do you like playing computer games with him/her?','Your best friend (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,10,'Who does he/she live with?','Your best friend (followup)',10,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'His/Her mum and dad.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,11,'Where do they live?','Your best friend (followup)',11,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'In ...',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,12,'Have they got any pets?','Your best friend (followup)',12,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, they''ve got two cats.',NULL,1,0);

-- ============================================================
-- aep3_2  官方真题2019 - Test 2
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('aep3_2',1,1,'flyers','官方真题2019 - Test 2','Authentic Exam Papers 3','2019',1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture the smaller drums are green, but in your picture they are red.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of a room with drums, guitars, and children playing instruments',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, the guitars look the same.','In my picture, the guitars on the wall are different.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, he''s got a moustache.','In my picture, the teacher has got a beard.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, there''s a poster of a pop star on the wall.','In my picture, there''s a poster of a building on the wall.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, her belt is yellow.','In my picture, the girl with the violin is wearing a blue belt.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, there''s a magazine on the rug.','In my picture, there''s a laptop on the rug.',5,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,6,'In my picture, five children are playing instruments.','In my picture, four children are playing instruments.',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Katy and Robert are friends. Their mums have just bought cars.
Katy and Robert are friends. Their mums have just bought cars. I don''t know anything about Katy''s mum''s car, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Katy and Robert are friends. Their mums have just bought cars.','Katy and Robert are friends. Their mums have just bought cars. I don''t know anything about Katy''s mum''s car, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'What colour is the car?','It''s silver.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'How old is it?','Two years old.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'How many doors has it got?','Three.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'Where''s the car now?','At home.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'Was it cheap or expensive?','It was cheap.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Katy and Robert are friends. Their mums have just bought cars.','Now you don''t know anything about Robert''s mum''s car, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'colour — car?','What colour is the car?','It''s black.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'how many — doors?','How many doors has it got?','Five.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'how old — ?','How old is it?','Six months old.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'cheap or expensive — ?','Was it cheap or expensive?','It was expensive.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'where — now?','Where''s the car now?','At the station.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','The goat and the coconuts
These pictures tell a story. It''s called ''The goat and the coconuts''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Alex and Lucy want to get some coconuts. The coconut tree is very tall so Alex is bringing a chair.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'Alex still can''t get the coconuts. A goat''s watching him.','Can Alex get the coconuts now? What''s watching Alex? | Can Alex get the coconuts? | What''s watching Alex?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'The goat''s hitting the tree with its head. Some coconuts are falling.','What''s the goat doing? | What''s the goat doing?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'Now Alex and Lucy have a lot of coconuts. They''re very happy.','How are Alex and Lucy feeling? Why? | How are Alex and Lucy feeling? Why?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'Alex is drinking from a coconut. Lucy''s eating some coconut and giving some to the goat.','What are the children doing now? | What are the children doing now?',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now let''s talk about your school.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'How many children are there in your class?','Your school',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Thirty.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'What time does school start?','Your school',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'At 7:30.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'What subject do you like best?','Your school',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Science.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'What games do you play at school?','Your school',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Football.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tell me about your best friend at school.','Your school',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'His/Her name''s ... He/She sits next to me. He''s/She''s ten years old.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Are there thirty children in your class?','Your school (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, there are.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Does school start at 7:30?','Your school (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, it does.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Do you like science best?','Your school (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Do you play football at school?','Your school (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,10,'What''s his/her name?','Your school (followup)',10,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'His/Her name''s ...',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,11,'Does he/she sit next to you?','Your school (followup)',11,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, he/she does.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,12,'How old is he/she?','Your school (followup)',12,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'He''s/She''s ten.',NULL,1,0);

-- ============================================================
-- aep3_3  官方真题2019 - Test 3
-- ============================================================
INSERT INTO `esc_exam` (`exam_code`,`version`,`is_active`,`level_code`,`label`,`source`,`year`,`status`,`tenant_id`) VALUES ('aep3_3',1,1,'flyers','官方真题2019 - Test 3','Authentic Exam Papers 3','2019',1,0);
SET @exam_id := LAST_INSERT_ID();

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,1,'find_diff','Find the Differences','Here are two pictures. My picture is similar to yours, but there are some differences.
For example, in my picture the helicopter is on the hill, but in your picture it''s in the air.
I will say something about my picture. Can you tell me how your picture is different?',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_pair` (`part_id`,`pair_no`,`topic`,`tenant_id`) VALUES (@part_id,1,'Two pictures of a beach scene with helicopter, towel, and striped chair',0);
SET @pair_id := LAST_INSERT_ID();
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,1,'In my picture, the umbrella''s red and white.','In my picture, the umbrella''s green and white.',1,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,2,'In my picture, the man isn''t wearing sunglasses.','In my picture, there''s a man lying on a towel. He''s wearing sunglasses.',2,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,3,'In my picture, there are three steps.','In my picture, there are four steps down to the beach.',3,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,4,'In my picture, a woman is sitting in the striped chair.','In my picture, no one is sitting in the striped chair.',4,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,5,'In my picture, the bin''s full.','In my picture, the bin''s empty.',5,0);
INSERT INTO `esc_part_find_diff_difference` (`pair_id`,`diff_no`,`description`,`keyword`,`sort`,`tenant_id`) VALUES (@pair_id,6,'In my picture, she''s making a sand castle.','In my picture, the girl in the white T-shirt is playing with a ball.',6,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,2,'info_exchange','Information Exchange','Robert and Emma both live in apartments.
Robert and Emma both live in apartments. I don''t know anything about Robert''s apartment, but you do. So I''m going to ask you some questions.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'A','Robert and Emma both live in apartments.','Robert and Emma both live in apartments. I don''t know anything about Robert''s apartment, but you do. So I''m going to ask you some questions.',1,0);
SET @cardA_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,1,NULL,'How many bedrooms has Robert''s apartment got?','Three.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,2,NULL,'What colour is the kitchen?','It''s blue.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,3,NULL,'Where is the apartment?','In Winter Street.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,4,NULL,'Which floor is it on?','On the fourth floor.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardA_id,5,NULL,'Has Robert''s apartment got a balcony?','Yes, it has.',5,0);
INSERT INTO `esc_part_info_exchange_card` (`part_id`,`phase`,`card_title`,`intro`,`sort`,`tenant_id`) VALUES (@part_id,'B','Robert and Emma both live in apartments.','Now you don''t know anything about Emma''s apartment, so you ask me some questions.',2,0);
SET @cardB_id := LAST_INSERT_ID();
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,1,'where — apartment?','Where is Emma''s apartment?','In River Road.',1,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,2,'which floor — ?','Which floor is it on?','On the fifth floor.',2,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,3,'how many — bedrooms?','How many bedrooms has the apartment got?','Four.',3,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,4,'kitchen — colour?','What colour is the kitchen?','Yellow.',4,0);
INSERT INTO `esc_part_info_exchange_qa` (`card_id`,`qa_no`,`prompt_label`,`question_text`,`answer_text`,`sort`,`tenant_id`) VALUES (@cardB_id,5,'balcony — ?','Has the apartment got a balcony?','No, it hasn''t.',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,3,'tell_story','Tell the Story','A frog goes to school
These pictures tell a story. It''s called ''A frog goes to school''. Look at the pictures first.
Now you tell the story.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,1,'Charlie is walking to school. He can see a frog. The frog is jumping into his pocket. Charlie is going to take the frog to school.',NULL,1,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,2,'Charlie is in his classroom now. The teacher has seen the frog. She''s saying, ''Put that frog outside!''','Where''s Charlie now? What''s the teacher saying? | Where''s Charlie now? | What''s the teacher saying?',2,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,3,'The frog''s outside the school now. It''s sad. It''s waiting for Charlie.','Where''s the frog now? Why is it sad? | Where''s the frog now? How does it feel?',3,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,4,'Charlie''s coming out of school. The frog''s jumping back into his pocket again.','Has school finished? What''s the frog doing? | Has school finished? What''s the frog doing?',4,0);
INSERT INTO `esc_part_tell_story_frame` (`part_id`,`frame_no`,`sentence_text`,`hint`,`sort`,`tenant_id`) VALUES (@part_id,5,'Charlie''s going home. The frog''s on the lake. Charlie''s saying, ''Goodbye, frog!''','Where''s Charlie going? What''s he saying? | Where''s Charlie going? | What''s he saying?',5,0);

INSERT INTO `esc_exam_part` (`exam_id`,`part_no`,`part_type`,`title`,`intro`,`tenant_id`) VALUES (@exam_id,4,'personal_qa','Personal Questions','Now let''s talk about holidays.',0);
SET @part_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,1,'When do you have holidays?','Your holidays',1,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'In the summer.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,2,'Where do you like going on holiday?','Your holidays',2,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'To the beach.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,3,'Who do you go on holiday with?','Your holidays',3,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'My family.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,4,'What weather do you like on holiday?','Your holidays',4,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Sunny weather.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,5,'Tell me about what you do on holiday.','Your holidays',5,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'We go swimming. We eat in restaurants. We go shopping.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,6,'Do you have holidays in the summer?','Your holidays (followup)',6,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,7,'Do you like going to the beach?','Your holidays (followup)',7,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,8,'Do you go with your family?','Your holidays (followup)',8,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,9,'Do you like sunny weather?','Your holidays (followup)',9,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,10,'Do you go swimming?','Your holidays (followup)',10,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, I do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,11,'Do you eat in restaurants?','Your holidays (followup)',11,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, we do.',NULL,1,0);
INSERT INTO `esc_part_personal_question` (`part_id`,`q_no`,`question_text`,`topic`,`sort`,`tenant_id`) VALUES (@part_id,12,'Do you go shopping?','Your holidays (followup)',12,0);
SET @q_id := LAST_INSERT_ID();
INSERT INTO `esc_part_personal_qa_sample` (`question_id`,`sample_no`,`sample_text`,`level_tag`,`sort`,`tenant_id`) VALUES (@q_id,1,'Yes, we do.',NULL,1,0);

COMMIT;
