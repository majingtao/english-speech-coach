-- =====================================================================
-- 方案 A：级别（esc_exam_level）× 系列（esc_exam_series）双维度
--   Level  = 能力标尺（Starters/Movers/Flyers/KET/PET/新概念 1..3 …）
--   Series = 教材/真题来源（Go Flyers / Flyers 1-4 / AEP / ...）
--   esc_exam 挂两个外键：level_code + series_code
-- =====================================================================

-- 1) 系列字典表（跨租户共享，参考 esc_exam_level 没有 tenant_id）
CREATE TABLE IF NOT EXISTS `esc_exam_series` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `code`         VARCHAR(60)  NOT NULL COMMENT '系列编码：go_flyers / flyers_1 / aep_1 ...',
    `level_code`   VARCHAR(30)  NOT NULL COMMENT '所属级别，引用 esc_exam_level.code',
    `name`         VARCHAR(100) NOT NULL COMMENT '显示名：Go Flyers / Flyers 1 (2014) / AEP Vol.1',
    `publisher`    VARCHAR(60)           DEFAULT NULL COMMENT '出版方：Cambridge / Macmillan / ...',
    `description`  VARCHAR(500)          DEFAULT NULL COMMENT '说明',
    `sort`         INT          NOT NULL DEFAULT 0  COMMENT '排序',
    `status`       TINYINT      NOT NULL DEFAULT 0  COMMENT '0=启用 1=停用',
    `creator`      VARCHAR(64)           DEFAULT '' COMMENT '创建者',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      VARCHAR(64)           DEFAULT '' COMMENT '更新者',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_esc_exam_series_code` (`code`),
    KEY `idx_esc_exam_series_level` (`level_code`)
) COMMENT='考试系列字典（教材/真题集）';

-- 2) esc_exam 增加 series_code
ALTER TABLE `esc_exam`
    ADD COLUMN `series_code` VARCHAR(60) DEFAULT NULL COMMENT '引用 esc_exam_series.code' AFTER `level_code`,
    ADD KEY `idx_esc_exam_series` (`series_code`);

-- 3) 级别种子（Starters / Movers / Flyers / KET / PET / NCE 1..3）
--    如果已存在同 code 会跳过（WHERE NOT EXISTS 模式，不依赖 esc_exam_level.code 是否有 UK）
INSERT INTO `esc_exam_level` (`code`, `name`, `cefr`, `sort`, `status`, `description`)
SELECT * FROM (
    SELECT 'starters' AS code, 'Pre A1 Starters' AS name, 'Pre A1' AS cefr, 10 AS sort, 0 AS status, '剑桥少儿英语 Starters' AS description UNION ALL
    SELECT 'movers',    'A1 Movers',     'A1',   20, 0, '剑桥少儿英语 Movers' UNION ALL
    SELECT 'flyers',    'A2 Flyers',     'A2',   30, 0, '剑桥少儿英语 Flyers' UNION ALL
    SELECT 'ket',       'KET',           'A2',   40, 0, 'Cambridge Key (for Schools)' UNION ALL
    SELECT 'pet',       'PET',           'B1',   50, 0, 'Cambridge Preliminary (for Schools)' UNION ALL
    SELECT 'nce_1',     '新概念英语 1',   NULL,   60, 0, 'New Concept English Book 1' UNION ALL
    SELECT 'nce_2',     '新概念英语 2',   NULL,   70, 0, 'New Concept English Book 2' UNION ALL
    SELECT 'nce_3',     '新概念英语 3',   NULL,   80, 0, 'New Concept English Book 3'
) AS seed
WHERE NOT EXISTS (
    SELECT 1 FROM `esc_exam_level` L WHERE L.`code` = seed.code
);

-- 4) 系列种子：Flyers 下的 Go Flyers / Mini Trainer / Flyers 1..4 / AEP 1..3
INSERT INTO `esc_exam_series` (`code`, `level_code`, `name`, `publisher`, `sort`, `status`, `description`)
VALUES
    -- Starters
    ('go_starters',            'starters', 'GO STARTERS',             'Cambridge/Macmillan', 10, 0, 'Go Starters 官方 Teacher''s Notes'),
    ('starters_mini_trainer',  'starters', 'Starters Mini Trainer',   'Cambridge',           20, 0, NULL),
    ('starters_1',             'starters', 'Starters 1',              'Cambridge',           30, 0, NULL),
    ('starters_2',             'starters', 'Starters 2',              'Cambridge',           40, 0, NULL),
    ('starters_3',             'starters', 'Starters 3',              'Cambridge',           50, 0, NULL),
    ('starters_4',             'starters', 'Starters 4',              'Cambridge',           60, 0, NULL),
    -- Movers
    ('go_movers',              'movers',   'GO MOVERS',               'Cambridge/Macmillan', 10, 0, NULL),
    ('movers_mini_trainer',    'movers',   'Movers Mini Trainer',     'Cambridge',           20, 0, NULL),
    ('movers_1',               'movers',   'Movers 1',                'Cambridge',           30, 0, NULL),
    ('movers_2',               'movers',   'Movers 2',                'Cambridge',           40, 0, NULL),
    ('movers_3',               'movers',   'Movers 3',                'Cambridge',           50, 0, NULL),
    ('movers_4',               'movers',   'Movers 4',                'Cambridge',           60, 0, NULL),
    -- Flyers
    ('go_flyers',              'flyers',   'GO FLYERS',               'Cambridge/Macmillan', 10, 0, 'Go Flyers 官方 Teacher''s Notes（5 套）'),
    ('flyers_mini_trainer',    'flyers',   'Flyers Mini Trainer',     'Cambridge',           20, 0, NULL),
    ('flyers_1',               'flyers',   'Flyers 1 (2014)',         'Cambridge',           30, 0, NULL),
    ('flyers_2',               'flyers',   'Flyers 2 (2017)',         'Cambridge',           40, 0, NULL),
    ('flyers_3',               'flyers',   'Flyers 3 (2019)',         'Cambridge',           50, 0, NULL),
    ('flyers_4',               'flyers',   'Flyers 4 (2022)',         'Cambridge',           60, 0, 'Fly4 2022（3 套）'),
    ('aep_1',                  'flyers',   'Authentic Exam Papers 1 (2018)', 'Cambridge',    70, 0, 'AEP1 2018（3 套）'),
    ('aep_2',                  'flyers',   'Authentic Exam Papers 2 (2018)', 'Cambridge',    80, 0, 'AEP2 2018（3 套）'),
    ('aep_3',                  'flyers',   'Authentic Exam Papers 3 (2019)', 'Cambridge',    90, 0, 'AEP3 2019（3 套）');

-- 5) 回填已有 esc_exam.series_code（依据 exam_code 前缀）
UPDATE `esc_exam` SET `series_code` = 'go_flyers' WHERE `exam_code` LIKE 'gf\_%' ESCAPE '\\';
UPDATE `esc_exam` SET `series_code` = 'flyers_4' WHERE `exam_code` LIKE 'f4\_%' ESCAPE '\\';
UPDATE `esc_exam` SET `series_code` = 'aep_1'    WHERE `exam_code` LIKE 'aep1\_%' ESCAPE '\\';
UPDATE `esc_exam` SET `series_code` = 'aep_2'    WHERE `exam_code` LIKE 'aep2\_%' ESCAPE '\\';
UPDATE `esc_exam` SET `series_code` = 'aep_3'    WHERE `exam_code` LIKE 'aep3\_%' ESCAPE '\\';

-- 6) 菜单：在 5047(英语口语) 下新增"考试系列字典"页
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`, `component_name`)
VALUES ('考试系列字典', '', 2, 15, 5047, 'exam-series', 'ep:menu', 'english/examSeries/index', 0, b'1', b'1', b'1', 'EnglishExamSeriesIndex');
SET @pid := LAST_INSERT_ID();
INSERT INTO `system_menu` (`name`, `permission`,                        `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`) VALUES
    ('考试系列字典查询',  'english:exam-series:query',                   3, 1, @pid, '', '#', '', 0),
    ('考试系列字典创建',  'english:exam-series:create',                  3, 2, @pid, '', '#', '', 0),
    ('考试系列字典更新',  'english:exam-series:update',                  3, 3, @pid, '', '#', '', 0),
    ('考试系列字典删除',  'english:exam-series:delete',                  3, 4, @pid, '', '#', '', 0);
