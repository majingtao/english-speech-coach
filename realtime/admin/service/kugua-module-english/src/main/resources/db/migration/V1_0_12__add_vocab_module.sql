-- =====================================================================
-- V1_0_12: 词汇模块（Vocabulary）
--   - 6 张新表：esc_vocab / esc_vocab_theme / esc_vocab_theme_rel
--               esc_user_vocab_progress / esc_user_vocab_list / esc_user_vocab_list_item
--   - 主题字典种子（15 个常用 KET 主题）
--   - admin 菜单：5200 词汇管理（列表）、5210 词汇主题（字典）
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. 全量词汇主表
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS esc_vocab (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    word          VARCHAR(64)   NOT NULL COMMENT '单词',
    level_code    VARCHAR(32)   NOT NULL COMMENT '级别 flyers/ket/pet，引用 esc_exam_level.code',
    pos           VARCHAR(32)   DEFAULT '' COMMENT '词性，逗号分隔（noun,verb）',
    difficulty    TINYINT       DEFAULT 1 COMMENT '难度：1=A2 必备 2=B1 延展',
    status        TINYINT       DEFAULT 0 COMMENT '0=草稿 1=发布 2=归档',
    sort          INT           DEFAULT 0 COMMENT '排序',
    content_json  TEXT          DEFAULT NULL COMMENT 'LLM/IPA 缓存：{definition_cn, definition_en, ipa, examples:[{en,cn}], gen_at, model_used}',
    -- yudao 标准字段
    tenant_id     BIGINT        NOT NULL DEFAULT 0,
    creator       VARCHAR(64)   DEFAULT '',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater       VARCHAR(64)   DEFAULT '',
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       BIT(1)        NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tenant_level_word (tenant_id, level_code, word, deleted),
    INDEX idx_level_status (level_code, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='词汇-全量词汇主表';

-- ---------------------------------------------------------------------
-- 2. 主题字典
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS esc_vocab_theme (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    code          VARCHAR(32)   NOT NULL COMMENT '主题编码 sports/food/school...',
    name_cn       VARCHAR(64)   NOT NULL COMMENT '主题中文名',
    name_en       VARCHAR(64)   NOT NULL COMMENT '主题英文名',
    level_code    VARCHAR(32)   DEFAULT '' COMMENT '所属级别（可空表示通用）',
    sort          INT           DEFAULT 0,
    status        TINYINT       DEFAULT 1 COMMENT '0=禁用 1=启用',
    -- yudao 标准字段
    tenant_id     BIGINT        NOT NULL DEFAULT 0,
    creator       VARCHAR(64)   DEFAULT '',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater       VARCHAR(64)   DEFAULT '',
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       BIT(1)        NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tenant_code (tenant_id, code, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='词汇-主题字典';

-- ---------------------------------------------------------------------
-- 3. 词 × 主题 关联
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS esc_vocab_theme_rel (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    vocab_id      BIGINT        NOT NULL COMMENT '词条ID',
    theme_id      BIGINT        NOT NULL COMMENT '主题ID',
    -- yudao 标准字段
    tenant_id     BIGINT        NOT NULL DEFAULT 0,
    creator       VARCHAR(64)   DEFAULT '',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater       VARCHAR(64)   DEFAULT '',
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       BIT(1)        NOT NULL DEFAULT 0,
    UNIQUE KEY uk_vocab_theme (vocab_id, theme_id, deleted),
    INDEX idx_theme (theme_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='词汇-词与主题关联';

-- ---------------------------------------------------------------------
-- 4. 学员 SRS 进度
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS esc_user_vocab_progress (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT        NOT NULL COMMENT '用户ID',
    vocab_id         BIGINT        NOT NULL COMMENT '词条ID',
    status           TINYINT       DEFAULT 0 COMMENT '0=新词 1=学习中 2=已掌握',
    repetitions      INT           DEFAULT 0 COMMENT '连续答对次数',
    interval_days    INT           DEFAULT 0 COMMENT '当前间隔（1/2/4/7/15/30）',
    ease             INT           DEFAULT 250 COMMENT '难易系数×100（SM-2 备用）',
    last_review_at   DATETIME      DEFAULT NULL,
    next_review_at   DATETIME      DEFAULT NULL,
    correct_count    INT           DEFAULT 0,
    wrong_count      INT           DEFAULT 0,
    -- yudao 标准字段
    tenant_id        BIGINT        NOT NULL DEFAULT 0,
    creator          VARCHAR(64)   DEFAULT '',
    create_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater          VARCHAR(64)   DEFAULT '',
    update_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted          BIT(1)        NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_vocab (user_id, vocab_id, deleted),
    INDEX idx_user_next (user_id, next_review_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='词汇-学员 SRS 进度';

-- ---------------------------------------------------------------------
-- 5. 用户自建词库
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS esc_user_vocab_list (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT        NOT NULL COMMENT '用户ID',
    name          VARCHAR(64)   NOT NULL COMMENT '词库名',
    description   VARCHAR(255)  DEFAULT '' COMMENT '描述',
    source        VARCHAR(32)   DEFAULT 'manual' COMMENT 'manual/wrongs/teacher_push',
    status        TINYINT       DEFAULT 0 COMMENT '0=私有 1=共享（预留）',
    -- yudao 标准字段
    tenant_id     BIGINT        NOT NULL DEFAULT 0,
    creator       VARCHAR(64)   DEFAULT '',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater       VARCHAR(64)   DEFAULT '',
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       BIT(1)        NOT NULL DEFAULT 0,
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='词汇-用户自建词库';

-- ---------------------------------------------------------------------
-- 6. 用户词库词条
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS esc_user_vocab_list_item (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    list_id       BIGINT        NOT NULL COMMENT '词库ID',
    vocab_id      BIGINT        NOT NULL COMMENT '词条ID',
    added_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- yudao 标准字段
    tenant_id     BIGINT        NOT NULL DEFAULT 0,
    creator       VARCHAR(64)   DEFAULT '',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater       VARCHAR(64)   DEFAULT '',
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       BIT(1)        NOT NULL DEFAULT 0,
    UNIQUE KEY uk_list_vocab (list_id, vocab_id, deleted),
    INDEX idx_list (list_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='词汇-用户词库词条';

-- ---------------------------------------------------------------------
-- 主题字典种子（KET A2 官方 24 个主题分类）
-- 注：旧 15 主题（sports/food/school/...）已被 V1_0_15 替换为官方分类。
--     此处保留官方分类，便于全新部署一次到位。
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `esc_vocab_theme` (`code`, `name_cn`, `name_en`, `level_code`, `sort`, `status`) VALUES
    ('appliances',               '家用电器',                  'Appliances',                                                'ket',  1, 1),
    ('clothes-accessories',      '服饰与配件',                'Clothes and Accessories',                                   'ket',  2, 1),
    ('comm-tech',                '通讯与科技',                'Communication and Technology',                              'ket',  3, 1),
    ('documents-texts',          '文件与文本',                'Documents and Texts',                                       'ket',  4, 1),
    ('education',                '教育',                      'Education',                                                 'ket',  5, 1),
    ('entertainment-media',      '娱乐与媒体',                'Entertainment and Media',                                   'ket',  6, 1),
    ('family-friends',           '家人与朋友',                'Family and Friends',                                        'ket',  7, 1),
    ('food-drink',               '饮食',                      'Food and Drink',                                            'ket',  8, 1),
    ('health-medicine-exercise', '健康、医疗与运动',          'Health, Medicine and Exercise',                             'ket',  9, 1),
    ('hobbies-leisure',          '爱好与休闲',                'Hobbies and Leisure',                                       'ket', 10, 1),
    ('house-home',               '家与住所',                  'House and Home',                                            'ket', 11, 1),
    ('measurements',             '计量单位',                  'Measurements',                                              'ket', 12, 1),
    ('personal-feelings',        '个人感受、观点与经历（形容词）', 'Personal Feelings, Opinions and Experiences (adjectives)', 'ket', 13, 1),
    ('places-buildings',         '场所：建筑',                'Places: Buildings',                                         'ket', 14, 1),
    ('places-countryside',       '场所：乡村',                'Places: Countryside',                                       'ket', 15, 1),
    ('places-town-city',         '场所：城镇',                'Places: Town and City',                                     'ket', 16, 1),
    ('services',                 '服务',                      'Services',                                                  'ket', 17, 1),
    ('shopping',                 '购物',                      'Shopping',                                                  'ket', 18, 1),
    ('sport',                    '运动',                      'Sport',                                                     'ket', 19, 1),
    ('natural-world',            '自然世界',                  'The Natural World',                                         'ket', 20, 1),
    ('time',                     '时间',                      'Time',                                                      'ket', 21, 1),
    ('travel-transport',         '旅行与交通',                'Travel and Transport',                                      'ket', 22, 1),
    ('weather',                  '天气',                      'Weather',                                                   'ket', 23, 1),
    ('work-jobs',                '工作与职业',                'Work and Jobs',                                             'ket', 24, 1);

-- ---------------------------------------------------------------------
-- admin 菜单（挂在 5047 "英语口语" 下）
--   - 不固定 id，避免与既有模块冲突（历史上 5200 被 AI 模型菜单占用过）
--   - 父菜单 INSERT 后用 LAST_INSERT_ID() 关联子按钮
-- ---------------------------------------------------------------------
-- 词汇管理
INSERT INTO system_menu
  (name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
VALUES
  ('词汇管理', '', 2, 40, 5047, 'vocab', '#', 'english/vocab/index', 0, b'1', b'1', b'1');
SET @vocab_pid := LAST_INSERT_ID();

INSERT INTO system_menu
  (name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
VALUES
  ('词汇查询', 'english:vocab:query',  3, 1, @vocab_pid, '', '#', NULL, 0, b'1', b'1', b'1'),
  ('词汇创建', 'english:vocab:create', 3, 2, @vocab_pid, '', '#', NULL, 0, b'1', b'1', b'1'),
  ('词汇更新', 'english:vocab:update', 3, 3, @vocab_pid, '', '#', NULL, 0, b'1', b'1', b'1'),
  ('词汇删除', 'english:vocab:delete', 3, 4, @vocab_pid, '', '#', NULL, 0, b'1', b'1', b'1'),
  ('重新生成', 'english:vocab:regen',  3, 5, @vocab_pid, '', '#', NULL, 0, b'1', b'1', b'1');

-- 词汇主题
INSERT INTO system_menu
  (name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
VALUES
  ('词汇主题', '', 2, 41, 5047, 'vocab-theme', '#', 'english/vocabTheme/index', 0, b'1', b'1', b'1');
SET @theme_pid := LAST_INSERT_ID();

INSERT INTO system_menu
  (name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
VALUES
  ('主题查询', 'english:vocab-theme:query',  3, 1, @theme_pid, '', '#', NULL, 0, b'1', b'1', b'1'),
  ('主题创建', 'english:vocab-theme:create', 3, 2, @theme_pid, '', '#', NULL, 0, b'1', b'1', b'1'),
  ('主题更新', 'english:vocab-theme:update', 3, 3, @theme_pid, '', '#', NULL, 0, b'1', b'1', b'1'),
  ('主题删除', 'english:vocab-theme:delete', 3, 4, @theme_pid, '', '#', NULL, 0, b'1', b'1', b'1');
