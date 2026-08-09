-- =====================================================================
-- V1_0_19: Expression practice library
-- This migration is idempotent and can also be executed manually.
-- =====================================================================

CREATE TABLE IF NOT EXISTS esc_expression_theme (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    code          VARCHAR(48)  NOT NULL COMMENT 'Stable topic code',
    name_cn       VARCHAR(64)  NOT NULL COMMENT 'Chinese topic name',
    name_en       VARCHAR(64)  NOT NULL COMMENT 'English topic name',
    description   VARCHAR(255) DEFAULT '' COMMENT 'Learner-facing introduction',
    level_code    VARCHAR(32)  NOT NULL DEFAULT 'ket',
    cover_url     VARCHAR(500) DEFAULT '' COMMENT 'Optional topic image',
    sort          INT          NOT NULL DEFAULT 0,
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '0 disabled, 1 enabled',
    tenant_id     BIGINT       NOT NULL DEFAULT 0,
    creator       VARCHAR(64)  DEFAULT '',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater       VARCHAR(64)  DEFAULT '',
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       BIT(1)       NOT NULL DEFAULT 0,
    UNIQUE KEY uk_expression_theme_code (tenant_id, level_code, code, deleted),
    INDEX idx_expression_theme_level (level_code, status, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Expression practice topics';

CREATE TABLE IF NOT EXISTS esc_expression_item (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    theme_id       BIGINT       NOT NULL COMMENT 'Expression topic ID',
    code           VARCHAR(64)  NOT NULL COMMENT 'Stable item code',
    prompt_en      VARCHAR(500) NOT NULL COMMENT 'Question or task in English',
    prompt_cn      VARCHAR(500) DEFAULT '' COMMENT 'Chinese support text',
    function_code  VARCHAR(48)  DEFAULT '' COMMENT 'Communication function',
    practice_mode  VARCHAR(16)  NOT NULL DEFAULT 'both' COMMENT 'speaking/writing/both',
    difficulty     TINYINT      NOT NULL DEFAULT 1 COMMENT '1 basic, 2 expanded, 3 challenge',
    answer_json    LONGTEXT     NOT NULL COMMENT 'Leveled answers, patterns, slots and vocabulary',
    image_urls_json TEXT        DEFAULT NULL COMMENT 'Optional image URL array',
    sort           INT          NOT NULL DEFAULT 0,
    status         TINYINT      NOT NULL DEFAULT 1 COMMENT '0 draft, 1 published, 2 archived',
    tenant_id      BIGINT       NOT NULL DEFAULT 0,
    creator        VARCHAR(64)  DEFAULT '',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater        VARCHAR(64)  DEFAULT '',
    update_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted        BIT(1)       NOT NULL DEFAULT 0,
    UNIQUE KEY uk_expression_item_code (tenant_id, code, deleted),
    INDEX idx_expression_item_theme (theme_id, status, sort),
    INDEX idx_expression_item_function (function_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Expression practice items';

CREATE TABLE IF NOT EXISTS esc_user_expression_progress (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id               BIGINT   NOT NULL,
    expression_item_id    BIGINT   NOT NULL,
    status                TINYINT  NOT NULL DEFAULT 0 COMMENT '0 new, 1 learning, 2 mastered',
    repetitions           INT      NOT NULL DEFAULT 0,
    interval_days         INT      NOT NULL DEFAULT 0,
    best_speaking_score   INT      DEFAULT NULL,
    best_writing_score    INT      DEFAULT NULL,
    attempt_count         INT      NOT NULL DEFAULT 0,
    last_practice_at      DATETIME DEFAULT NULL,
    next_review_at        DATETIME DEFAULT NULL,
    tenant_id             BIGINT   NOT NULL DEFAULT 0,
    creator               VARCHAR(64) DEFAULT '',
    create_time           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater               VARCHAR(64) DEFAULT '',
    update_time           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted               BIT(1)   NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_expression (user_id, expression_item_id, deleted),
    INDEX idx_user_expression_review (user_id, next_review_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Learner expression mastery and review schedule';

CREATE TABLE IF NOT EXISTS esc_expression_attempt (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id               BIGINT       NOT NULL,
    expression_item_id    BIGINT       NOT NULL,
    practice_mode         VARCHAR(16)  NOT NULL COMMENT 'speaking/writing',
    response_text         TEXT         NOT NULL COMMENT 'Transcript or written response',
    score                 INT          NOT NULL DEFAULT 0,
    feedback_json         LONGTEXT     DEFAULT NULL COMMENT 'Structured LLM feedback snapshot',
    duration_seconds      INT          DEFAULT NULL,
    tenant_id             BIGINT       NOT NULL DEFAULT 0,
    creator               VARCHAR(64)  DEFAULT '',
    create_time           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater               VARCHAR(64)  DEFAULT '',
    update_time           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted               BIT(1)       NOT NULL DEFAULT 0,
    INDEX idx_expression_attempt_user (user_id, create_time),
    INDEX idx_expression_attempt_item (expression_item_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Expression practice attempts';

-- Repair seed rows created by an earlier draft of this migration with tenant 0.
UPDATE esc_expression_theme
SET tenant_id = 1
WHERE code = 'food-drink' AND level_code = 'ket' AND tenant_id = 0 AND deleted = b'0';

UPDATE esc_expression_item
SET tenant_id = 1
WHERE theme_id IN (
    SELECT id FROM esc_expression_theme
    WHERE code = 'food-drink' AND level_code = 'ket' AND tenant_id = 1 AND deleted = b'0'
) AND tenant_id = 0 AND deleted = b'0';

INSERT IGNORE INTO esc_expression_theme
    (code, name_cn, name_en, description, level_code, sort, status, tenant_id)
VALUES
    ('food-drink', '食物与饮料', 'Food and Drink', '谈论喜欢的食物、健康饮食、烹饪和用餐习惯。', 'ket', 10, 1, 1);

SET @expression_food_theme_id := (
    SELECT id FROM esc_expression_theme
    WHERE code = 'food-drink' AND level_code = 'ket' AND tenant_id = 1 AND deleted = b'0'
    ORDER BY id LIMIT 1
);

INSERT IGNORE INTO esc_expression_item
    (theme_id, code, prompt_en, prompt_cn, function_code, practice_mode, difficulty, answer_json, sort, status, tenant_id)
VALUES
(@expression_food_theme_id, 'food-photo-favourite',
 'Which food in the photos do you like best?', '照片里的食物你最喜欢哪一种？',
 'give-preference', 'both', 1,
 '{"answers":{"basic":{"en":"I like the pizza best.","cn":"我最喜欢披萨。"},"expanded":{"en":"I like the pizza best because it is cheesy and delicious.","cn":"我最喜欢披萨，因为它有芝士而且很好吃。"},"challenge":{"en":"Of all the food in the photos, I would choose the pizza because I enjoy its rich flavour and I can share it with my family.","cn":"照片中的食物里我会选择披萨，因为我喜欢它浓郁的味道，也可以和家人分享。"}},"patterns":[{"en":"I like ___ best because ___.","cn":"我最喜欢___，因为___。","slots":{"food":["pizza","salad","noodles"],"reason":["it is delicious","it looks fresh","I often eat it with my family"]}}],"keywords":["best","because","delicious"]}',
 10, 1, 1),
(@expression_food_theme_id, 'food-healthy-choice',
 'Which food is healthy?', '哪一种食物是健康的？',
 'give-reason', 'both', 1,
 '{"answers":{"basic":{"en":"The salad is healthy.","cn":"沙拉是健康的。"},"expanded":{"en":"The salad is healthy because it has lots of fresh vegetables.","cn":"沙拉很健康，因为里面有很多新鲜蔬菜。"},"challenge":{"en":"I think the salad is the healthiest choice because it contains fresh vegetables and is not too oily.","cn":"我认为沙拉是最健康的选择，因为它含有新鲜蔬菜，而且不会太油腻。"}},"patterns":[{"en":"___ is healthy because it has ___.","cn":"___很健康，因为它含有___。","slots":{"food":["salad","fruit","vegetable soup"],"ingredient":["fresh vegetables","vitamins","less sugar"]}}],"keywords":["healthy","fresh","vegetables"]}',
 20, 1, 1),
(@expression_food_theme_id, 'food-favourite-meal',
 'What is your favourite meal?', '你最喜欢哪一餐或哪道饭？',
 'describe-preference', 'both', 1,
 '{"answers":{"basic":{"en":"My favourite meal is chicken rice.","cn":"我最喜欢的饭是鸡肉饭。"},"expanded":{"en":"My favourite meal is chicken rice because it is tasty and filling.","cn":"我最喜欢鸡肉饭，因为它好吃又能吃饱。"},"challenge":{"en":"My favourite meal is chicken rice. I usually have it for lunch, and I especially like the tender chicken and flavourful rice.","cn":"我最喜欢鸡肉饭。我通常午餐吃它，尤其喜欢嫩鸡肉和香喷喷的米饭。"}},"patterns":[{"en":"My favourite meal is ___ because ___.","cn":"我最喜欢的饭是___，因为___。","slots":{"meal":["chicken rice","noodles","fish and chips"],"reason":["it is tasty","it is filling","my family makes it well"]}}],"keywords":["favourite meal","usually","especially"]}',
 30, 1, 1),
(@expression_food_theme_id, 'food-same-favourite',
 'Find someone in your class who has the same favourite meal as you.', '找一位和你最喜欢同一种食物的同学。',
 'ask-and-compare', 'speaking', 2,
 '{"answers":{"basic":{"en":"What is your favourite meal? Mine is noodles.","cn":"你最喜欢什么饭？我最喜欢面条。"},"expanded":{"en":"What is your favourite meal? Mine is noodles. Do you like noodles too?","cn":"你最喜欢什么饭？我最喜欢面条。你也喜欢面条吗？"},"challenge":{"en":"My favourite meal is noodles because they are tasty and easy to share. What is yours? Perhaps we have the same favourite meal.","cn":"我最喜欢面条，因为好吃而且方便分享。你呢？也许我们最喜欢的是同一种饭。"}},"patterns":[{"en":"What is your favourite ___? Mine is ___. Do you like ___ too?","cn":"你最喜欢的___是什么？我的是___。你也喜欢___吗？","slots":{"category":["meal","food","drink"],"answer":["noodles","pizza","orange juice"]}}],"keywords":["mine","too","same"]}',
 40, 1, 1),
(@expression_food_theme_id, 'food-know-recipes',
 'Do you know any recipes?', '你会做什么菜吗？',
 'describe-ability', 'both', 1,
 '{"answers":{"basic":{"en":"Yes. I know how to make a sandwich.","cn":"会。我知道怎么做三明治。"},"expanded":{"en":"Yes. I know how to make a cheese sandwich. First, I put cheese and vegetables between two slices of bread.","cn":"会。我知道怎么做芝士三明治。首先，我把芝士和蔬菜放在两片面包中间。"},"challenge":{"en":"I know a simple recipe for vegetable soup. First, I wash and cut the vegetables. Then I cook them in water and add a little salt.","cn":"我会一个简单的蔬菜汤做法。首先洗好并切好蔬菜，然后放进水里煮，再加一点盐。"}},"patterns":[{"en":"I know how to make ___. First, ___. Then, ___.","cn":"我知道怎么做___。首先___，然后___。","slots":{"dish":["a sandwich","fruit salad","vegetable soup"],"first":["I prepare the ingredients","I wash the fruit","I cut the vegetables"],"then":["I put them together","I mix everything","I cook them in water"]}}],"keywords":["recipe","first","then"]}',
 50, 1, 1),
(@expression_food_theme_id, 'food-help-kitchen',
 'Do you help in the kitchen? What do you do?', '你会在厨房帮忙吗？你会做什么？',
 'describe-routine', 'both', 1,
 '{"answers":{"basic":{"en":"Yes. I wash the vegetables.","cn":"会。我洗蔬菜。"},"expanded":{"en":"Yes, I often help in the kitchen. I wash the vegetables and set the table.","cn":"会，我经常在厨房帮忙。我洗蔬菜并摆放餐具。"},"challenge":{"en":"I usually help my parents in the kitchen at weekends. I prepare the ingredients, set the table and wash the dishes after dinner.","cn":"周末我通常在厨房帮父母。我准备食材、摆放餐具，并在晚饭后洗碗。"}},"patterns":[{"en":"I usually help by ___ and ___.","cn":"我通常通过做___和___来帮忙。","slots":{"task":["washing the vegetables","setting the table","washing the dishes","preparing the ingredients"]}}],"keywords":["usually","help","prepare"]}',
 60, 1, 1);

-- Admin menu. Run this block once in environments where menu records are desired.
INSERT INTO system_menu
  (name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
SELECT '表达练习', '', 2, 42, 5047, 'expression', '#', 'english/expression/index', 0, b'1', b'1', b'1'
WHERE NOT EXISTS (
  SELECT 1 FROM system_menu WHERE parent_id = 5047 AND path = 'expression' AND deleted = b'0'
);

SET @expression_menu_id := (
    SELECT id FROM system_menu WHERE parent_id = 5047 AND path = 'expression' AND deleted = b'0'
    ORDER BY id LIMIT 1
);

INSERT INTO system_menu
  (name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
SELECT p.name, p.permission, 3, p.sort, @expression_menu_id, '', '#', NULL, 0, b'1', b'1', b'1'
FROM (
    SELECT '表达查询' name, 'english:expression:query' permission, 1 sort
    UNION ALL SELECT '表达创建', 'english:expression:create', 2
    UNION ALL SELECT '表达更新', 'english:expression:update', 3
    UNION ALL SELECT '表达删除', 'english:expression:delete', 4
) p
WHERE NOT EXISTS (
    SELECT 1 FROM system_menu m WHERE m.permission = p.permission AND m.deleted = b'0'
);
