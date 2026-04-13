-- =====================================================================
-- 种子数据：导入 gf_1 (Go Flyers - Test 1)
-- 前置条件：esc_init.sql 已执行（表已建好）
-- 使用固定 ID 便于子表外键引用，ID 从 1001 起避免与后续自增冲突
-- =====================================================================

SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- 1. 字典数据（幂等：忽略重复）
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `esc_exam_level` (`id`, `code`, `name`, `cefr`, `description`, `sort`, `status`)
VALUES (1, 'flyers', 'Cambridge Flyers', 'A2', 'YLE Flyers，适合 7-12 岁', 1, 164);

INSERT IGNORE INTO `esc_exam_part_type` (`id`, `code`, `name`, `description`, `sort`, `status`) VALUES
(1, 'find_diff',       'Find the Differences', 'Flyers Part 1：看两张图找不同', 1, 0),
(2, 'info_exchange',   'Information Exchange',  'Flyers Part 2：信息互换问答',    2, 0),
(3, 'tell_story',      'Tell the Story',        'Flyers Part 3：看图讲故事',      3, 0),
(4, 'personal_qa',     'Personal Questions',    'Flyers Part 4：个人问答',        4, 164);

-- ---------------------------------------------------------------------
-- 2. 试卷主表
-- ---------------------------------------------------------------------
INSERT INTO `esc_exam` (`id`, `exam_code`, `version`, `is_active`, `level_code`, `label`, `source`, `status`, `tenant_id`)
VALUES (1001, 'gf_1', 1, 1, 'flyers', 'Go Flyers - Test 1', 'Go Flyers Teacher''s Notes', 1, 164);

-- ---------------------------------------------------------------------
-- 3. Part 头表（4 个 Part）
-- ---------------------------------------------------------------------
INSERT INTO `esc_exam_part` (`id`, `exam_id`, `part_no`, `part_type`, `title`, `intro`, `sort`, `tenant_id`) VALUES
(1001, 1001, 1, 'find_diff',     'Find the Differences',
 'Here are two pictures. My picture is similar to yours, but there are some differences.\nFor example, in my picture there''s a girl on a rock in the lake, but in your picture there isn''t.\nI will say something about my picture. Can you tell me how your picture is different?',
 1, 0),
(1002, 1001, 2, 'info_exchange', 'Information Exchange',
 'Richard and Harry are friends. They go to school together. I don''t know anything about Richard''s programme. So I''m going to ask you some questions.',
 2, 0),
(1003, 1001, 3, 'tell_story',    'Tell the Story',
 'These pictures tell a story. It''s called ''Betty''s visit to London''. Look at the pictures first.\nBetty, her brother Fred and their parents visit London. They arrive there on Monday and they go for a walk. They see Big Ben.\nNow you tell the story.',
 3, 0),
(1004, 1001, 4, 'personal_qa',   'Personal Questions',
 'Now let''s talk about what you do in your free time.',
 4, 164);

-- ---------------------------------------------------------------------
-- 4. Part 1: Find the Differences — 1 个图对 + 5 个差异点
-- ---------------------------------------------------------------------
-- 图对（gf_1 只有一组图，scene 作为 topic）
INSERT INTO `esc_part_find_diff_pair` (`id`, `part_id`, `pair_no`, `topic`, `sort`, `tenant_id`)
VALUES (1001, 1001, 1, 'Two pictures of an outdoor picnic scene near a lake (tent, bikes, blanket)', 1, 164);

-- 差异点（examiner = 描述, expected = 期望回答, keyword 取差异关键词）
INSERT INTO `esc_part_find_diff_difference` (`id`, `pair_id`, `diff_no`, `description`, `keyword`, `sort`, `tenant_id`) VALUES
(1001, 1001, 1, 'In my picture, two boys are sitting under a tree.|In my picture, two girls are sitting under a tree.', 'girls', 1, 0),
(1002, 1001, 2, 'In my picture, the woman is eating a sandwich.|In my picture, the woman is eating pizza.', 'pizza', 2, 0),
(1003, 1001, 3, 'In my picture, there isn''t a pizza on the blanket.|In my picture there is a pizza on the blanket.', 'pizza on blanket', 3, 0),
(1004, 1001, 4, 'In my picture, two boys are riding their bikes.|In my picture, one boy is riding his bike.', 'one boy', 4, 0),
(1005, 1001, 5, 'In my picture, a man is reading a newspaper outside the tent.|In my picture, a man is reading a magazine outside the tent.', 'magazine', 5, 164);

-- ---------------------------------------------------------------------
-- 5. Part 2: Information Exchange — 2 张卡片 (phaseA + phaseB) + 各 5 个问答
-- ---------------------------------------------------------------------
-- Phase A：考官问，学生答（基于 Richard 的日程）
INSERT INTO `esc_part_info_exchange_card` (`id`, `part_id`, `phase`, `card_title`, `intro`, `sort`, `tenant_id`)
VALUES (1001, 1002, 'A', 'Richard''s Programme', 'Richard and Harry are friends who go to school together.', 1, 164);

INSERT INTO `esc_part_info_exchange_qa` (`id`, `card_id`, `qa_no`, `question_text`, `answer_text`, `sort`, `tenant_id`) VALUES
(1001, 1001, 1, 'What time does he get up in the morning?', 'At 7:00.', 1, 0),
(1002, 1001, 2, 'What time does he go to school?', 'At 8:00.', 2, 0),
(1003, 1001, 3, 'What does he do at 4:00 p.m.?', 'He does his homework.', 3, 0),
(1004, 1001, 4, 'What does he do at 7:00 p.m.?', 'He has a tennis lesson.', 4, 0),
(1005, 1001, 5, 'What time does he go to bed?', 'At 10:00.', 5, 164);

-- Phase B：学生问，考官答（基于 Harry 的日程）
INSERT INTO `esc_part_info_exchange_card` (`id`, `part_id`, `phase`, `card_title`, `intro`, `sort`, `tenant_id`)
VALUES (1002, 1002, 'B', 'Harry''s Programme', 'Now, you don''t know anything about Harry''s programme, so you ask me some questions.', 2, 164);

INSERT INTO `esc_part_info_exchange_qa` (`id`, `card_id`, `qa_no`, `prompt_label`, `question_text`, `answer_text`, `sort`, `tenant_id`) VALUES
(1006, 1002, 1, 'get up — time?', 'What time does he get up?', 'At 7:30.', 1, 0),
(1007, 1002, 2, 'go to school — time?', 'What time does he go to school?', 'At 8:15.', 2, 0),
(1008, 1002, 3, '2 o''clock — do?', 'What does he do at 2 o''clock?', 'He has lunch.', 3, 0),
(1009, 1002, 4, '6 p.m. — do?', 'What does he do at 6 p.m.?', 'He plays football.', 4, 0),
(1010, 1002, 5, 'go to bed — time?', 'What time does he go to bed?', 'At 10:30.', 5, 164);

-- ---------------------------------------------------------------------
-- 6. Part 3: Tell the Story — 4 帧（pic 2-5，pic 1 是考官示范已在 intro 中）
-- ---------------------------------------------------------------------
-- 注意：每帧可能有多个句子，tell_story_frame 表是单帧单句设计，
-- 多句拆为多行，用 frame_no 区分帧，sort 区分句子顺序

-- Pic 2: Tuesday shopping
INSERT INTO `esc_part_tell_story_frame` (`id`, `part_id`, `frame_no`, `sentence_text`, `hint`, `sort`, `tenant_id`) VALUES
(1001, 1003, 2, 'On Tuesday Betty and her family go shopping.', 'What day is it? What do they do?', 1, 0),
(1002, 1003, 2, 'They buy many presents and souvenirs.', 'What do they buy?', 2, 164);

-- Pic 3: Wednesday postcard
INSERT INTO `esc_part_tell_story_frame` (`id`, `part_id`, `frame_no`, `sentence_text`, `hint`, `sort`, `tenant_id`) VALUES
(1003, 1003, 3, 'On Wednesday Betty writes a postcard to her grandmother.', 'What day is it? What does Betty do?', 3, 164);

-- Pic 4: Thursday zoo
INSERT INTO `esc_part_tell_story_frame` (`id`, `part_id`, `frame_no`, `sentence_text`, `hint`, `sort`, `tenant_id`) VALUES
(1004, 1003, 4, 'On Thursday the family visits the zoo.', 'What day is it? Where do they go?', 4, 0),
(1005, 1003, 4, 'They see a lot of animals there.', 'What do they see at the zoo?', 5, 0),
(1006, 1003, 4, 'Betty is afraid of the snakes and her brother Fred laughs.', 'Is Betty afraid of the snakes? Who laughs?', 6, 164);

-- Pic 5: Friday airport
INSERT INTO `esc_part_tell_story_frame` (`id`, `part_id`, `frame_no`, `sentence_text`, `hint`, `sort`, `tenant_id`) VALUES
(1007, 1003, 5, 'On Friday they are at the airport.', 'What day is it? Where are they?', 7, 0),
(1008, 1003, 5, 'It is the last day in London.', 'Is it their last day?', 8, 0),
(1009, 1003, 5, 'They have their bags with them.', 'What have they got with them?', 9, 0),
(1010, 1003, 5, 'Betty''s father takes pictures of the family.', 'What does the father do?', 10, 164);

-- ---------------------------------------------------------------------
-- 7. Part 4: Personal Questions — 5 个主问题 + 4 个追问，各带示例答案
-- ---------------------------------------------------------------------
-- 主问题
INSERT INTO `esc_part_personal_question` (`id`, `part_id`, `q_no`, `question_text`, `topic`, `sort`, `tenant_id`) VALUES
(1001, 1004, 1, 'What do you do after school?', 'Free time', 1, 0),
(1002, 1004, 2, 'How often do you go out with your friends?', 'Free time', 2, 0),
(1003, 1004, 3, 'What do you usually do at the weekend?', 'Free time', 3, 0),
(1004, 1004, 4, 'Do you play any sports?', 'Free time', 4, 0),
(1005, 1004, 5, 'What other things do you do in your free time?', 'Free time', 5, 164);

-- 主问题的示例答案
INSERT INTO `esc_part_personal_qa_sample` (`id`, `question_id`, `sample_no`, `sample_text`, `sort`, `tenant_id`) VALUES
(1001, 1001, 1, 'I do my homework.', 1, 0),
(1002, 1002, 1, 'Three times a week.', 1, 0),
(1003, 1003, 1, 'I visit my grandparents.', 1, 0),
(1004, 1004, 1, 'Yes, I play football.', 1, 0),
(1005, 1005, 1, 'I sometimes go to the cinema.', 1, 164);

-- 追问（也存在 personal_question 表，用 q_no 6-9 区分）
INSERT INTO `esc_part_personal_question` (`id`, `part_id`, `q_no`, `question_text`, `topic`, `sort`, `tenant_id`) VALUES
(1006, 1004, 6, 'Do you play basketball or football?', 'Free time (follow-up)', 6, 0),
(1007, 1004, 7, 'Do you go to the cinema?', 'Free time (follow-up)', 7, 0),
(1008, 1004, 8, 'Do you play computer games with your friends?', 'Free time (follow-up)', 8, 0),
(1009, 1004, 9, 'Do you help with the housework?', 'Free time (follow-up)', 9, 164);

-- 追问的示例答案
INSERT INTO `esc_part_personal_qa_sample` (`id`, `question_id`, `sample_no`, `sample_text`, `sort`, `tenant_id`) VALUES
(1006, 1006, 1, 'I play football.', 1, 0),
(1007, 1007, 1, 'Yes, I do.', 1, 0),
(1008, 1008, 1, 'Yes, sometimes.', 1, 0),
(1009, 1009, 1, 'Yes, I do.', 1, 164);

-- =====================================================================
-- 完成：gf_1 共插入
--   字典: 1 level + 4 part_type
--   试卷: 1 exam + 4 parts
--   Part1: 1 pair + 5 differences
--   Part2: 2 cards + 10 qa
--   Part3: 10 frames
--   Part4: 9 questions + 9 samples
-- =====================================================================
