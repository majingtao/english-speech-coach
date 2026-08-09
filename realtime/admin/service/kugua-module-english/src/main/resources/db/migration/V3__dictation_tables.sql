-- ============================
-- 听写模块：4张表
-- ============================

-- 1. 单词主表
CREATE TABLE esc_dictation_word (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    en          VARCHAR(100)  NOT NULL COMMENT '英文单词/短语',
    cn          VARCHAR(200)  DEFAULT '' COMMENT '中文释义',
    pos         VARCHAR(30)   DEFAULT '' COMMENT '词性 n./v./adj./adv./prep.',
    forms       VARCHAR(500)  DEFAULT '' COMMENT '词形变化展示行',
    forms_json  TEXT          DEFAULT NULL COMMENT '结构化词形 JSON [{word,en,zh}]',
    example     TEXT          DEFAULT NULL COMMENT '例句（en\\nzh 成对）',
    difficulty  TINYINT       DEFAULT 1 COMMENT '难度 1-5',
    llm_status  TINYINT       DEFAULT 0 COMMENT '0=待处理 1=已完成 2=失败',
    -- yudao 标准字段
    tenant_id   BIGINT        NOT NULL DEFAULT 0,
    creator     VARCHAR(64)   DEFAULT '',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)   DEFAULT '',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     BIT(1)        NOT NULL DEFAULT 0,
    UNIQUE KEY uk_en_tenant (en, tenant_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='听写-单词主表';

-- 2. 词书/词单
CREATE TABLE esc_dictation_wordlist (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)  NOT NULL COMMENT '显示名',
    category_type   VARCHAR(20)   NOT NULL COMMENT 'SCHOOL_GRADE 或 EXAM',
    school_level    VARCHAR(20)   DEFAULT NULL COMMENT 'primary/middle/high',
    grade           TINYINT       DEFAULT NULL COMMENT '年级 1-12',
    semester        TINYINT       DEFAULT NULL COMMENT '1=上学期 2=下学期',
    edition         VARCHAR(30)   DEFAULT NULL COMMENT '教材版本：人教版/PEP版/外研版',
    unit_label      VARCHAR(50)   DEFAULT NULL COMMENT '单元标签 Unit 1',
    exam_level_code VARCHAR(30)   DEFAULT NULL COMMENT '引用 esc_exam_level.code',
    description     VARCHAR(500)  DEFAULT '' COMMENT '描述',
    word_count      INT           DEFAULT 0 COMMENT '缓存单词数',
    sort            INT           DEFAULT 0 COMMENT '排序',
    status          TINYINT       DEFAULT 1 COMMENT '0=草稿 1=发布 2=下架',
    -- yudao 标准字段
    tenant_id       BIGINT        NOT NULL DEFAULT 0,
    creator         VARCHAR(64)   DEFAULT '',
    create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)   DEFAULT '',
    update_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         BIT(1)        NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='听写-词书';

-- 3. 词书-单词关联
CREATE TABLE esc_dictation_wordlist_word (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    wordlist_id BIGINT  NOT NULL COMMENT '词书ID',
    word_id     BIGINT  NOT NULL COMMENT '单词ID',
    seq         INT     DEFAULT 0 COMMENT '排序',
    -- yudao 标准字段
    tenant_id   BIGINT  NOT NULL DEFAULT 0,
    creator     VARCHAR(64)   DEFAULT '',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)   DEFAULT '',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     BIT(1)        NOT NULL DEFAULT 0,
    UNIQUE KEY uk_list_word (wordlist_id, word_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='听写-词书单词关联';

-- 4. 学生练习记录
CREATE TABLE esc_dictation_record (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id   BIGINT  NOT NULL COMMENT '学生ID',
    word_id      BIGINT  NOT NULL COMMENT '单词ID',
    wordlist_id  BIGINT  DEFAULT NULL COMMENT '词书ID',
    know_meaning BIT(1)  NOT NULL DEFAULT 0 COMMENT '是否认识',
    can_spell    BIT(1)  NOT NULL DEFAULT 0 COMMENT '是否会拼',
    -- yudao 标准字段
    tenant_id    BIGINT  NOT NULL DEFAULT 0,
    creator      VARCHAR(64)   DEFAULT '',
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater      VARCHAR(64)   DEFAULT '',
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted      BIT(1)        NOT NULL DEFAULT 0,
    INDEX idx_student_word (student_id, word_id),
    INDEX idx_student_list (student_id, wordlist_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='听写-学生练习记录';
