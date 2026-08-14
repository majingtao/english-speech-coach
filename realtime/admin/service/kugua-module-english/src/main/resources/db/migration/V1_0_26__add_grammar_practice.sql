CREATE TABLE IF NOT EXISTS esc_grammar_point (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(64) NOT NULL,
  parent_code VARCHAR(64) DEFAULT '',
  name_cn VARCHAR(96) NOT NULL,
  name_en VARCHAR(128) NOT NULL,
  description VARCHAR(500) DEFAULT '',
  level_code VARCHAR(32) NOT NULL DEFAULT 'ket',
  difficulty_config_json TEXT DEFAULT NULL,
  sort INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '0 disabled, 1 enabled',
  tenant_id BIGINT NOT NULL DEFAULT 0,
  creator VARCHAR(64) DEFAULT '', create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64) DEFAULT '', update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted BIT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY uk_grammar_point_code (tenant_id, level_code, code, deleted),
  INDEX idx_grammar_point_level (level_code, status, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Grammar knowledge points';

CREATE TABLE IF NOT EXISTS esc_grammar_generation_job (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  grammar_point_id BIGINT NOT NULL,
  requested_count INT NOT NULL,
  accepted_count INT NOT NULL DEFAULT 0,
  difficulty TINYINT NOT NULL DEFAULT 1,
  question_types VARCHAR(128) NOT NULL,
  auto_publish BIT(1) NOT NULL DEFAULT 1,
  model VARCHAR(128) DEFAULT '',
  prompt_version VARCHAR(32) DEFAULT 'grammar-v1',
  settings_json TEXT DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '0 generating, 1 completed, 2 failed',
  error_message VARCHAR(1000) DEFAULT '',
  tenant_id BIGINT NOT NULL DEFAULT 0,
  creator VARCHAR(64) DEFAULT '', create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64) DEFAULT '', update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted BIT(1) NOT NULL DEFAULT 0,
  INDEX idx_grammar_job_point (grammar_point_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Grammar AI generation jobs';

CREATE TABLE IF NOT EXISTS esc_grammar_question (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  grammar_point_id BIGINT NOT NULL,
  generation_job_id BIGINT DEFAULT NULL,
  source_question_id BIGINT DEFAULT NULL,
  code VARCHAR(80) NOT NULL,
  question_type VARCHAR(32) NOT NULL COMMENT 'single_choice/text_input',
  difficulty TINYINT NOT NULL DEFAULT 1,
  instruction VARCHAR(255) DEFAULT '',
  stem VARCHAR(1000) NOT NULL,
  options_json TEXT DEFAULT NULL,
  answer_json TEXT NOT NULL,
  explanation_zh VARCHAR(1000) NOT NULL,
  rule_text VARCHAR(1000) DEFAULT '',
  error_tags_json TEXT DEFAULT NULL,
  media_json TEXT DEFAULT NULL,
  source VARCHAR(16) NOT NULL DEFAULT 'ai',
  validation_status TINYINT NOT NULL DEFAULT 1 COMMENT '0 pending, 1 passed, 2 warning',
  sort INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 0 COMMENT '0 draft, 1 published, 2 disabled',
  tenant_id BIGINT NOT NULL DEFAULT 0,
  creator VARCHAR(64) DEFAULT '', create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64) DEFAULT '', update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted BIT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY uk_grammar_question_code (tenant_id, code, deleted),
  INDEX idx_grammar_question_pool (grammar_point_id, difficulty, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Grammar questions';

CREATE TABLE IF NOT EXISTS esc_grammar_attempt (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  question_id BIGINT NOT NULL,
  grammar_point_id BIGINT NOT NULL,
  difficulty TINYINT NOT NULL,
  answer_text VARCHAR(1000) NOT NULL,
  correct BIT(1) NOT NULL,
  duration_seconds INT DEFAULT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  creator VARCHAR(64) DEFAULT '', create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64) DEFAULT '', update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted BIT(1) NOT NULL DEFAULT 0,
  INDEX idx_grammar_attempt_user (user_id, grammar_point_id, create_time),
  INDEX idx_grammar_attempt_question (question_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Grammar practice attempts';

INSERT IGNORE INTO esc_grammar_point
  (code, parent_code, name_cn, name_en, description, level_code, difficulty_config_json, sort, status, tenant_id)
VALUES
('present-simple', '', '一般现在时', 'Present simple', '习惯、事实、三单和频率副词', 'ket', '{"l1":"肯定句、明显时间词、常见规则动词","l2":"加入否定句、疑问句和频率副词"}', 10, 1, 1),
('present-continuous', '', '现在进行时', 'Present continuous', '正在发生的动作及与一般现在时辨析', 'ket', '{"l1":"be + doing，带 now/look/listen","l2":"与一般现在时进行基础辨析"}', 20, 1, 1),
('nouns-quantifiers', '', '名词和数量', 'Nouns and quantifiers', '可数不可数、some/any、much/many、a few/a little', 'ket', '{"l1":"单一数量词规则","l2":"相似数量词辨析"}', 30, 1, 1),
('comparison-degree', '', '比较和程度', 'Comparison and degree', '比较级、最高级、too 和 enough', 'ket', '{"l1":"规则形容词变化","l2":"不规则变化及 too/enough"}', 40, 1, 1),
('past-tenses', '', '过去时', 'Past tenses', '一般过去时、过去进行时及两者辨析', 'ket', '{"l1":"一般过去时和常见不规则动词","l2":"加入过去进行时和 when/while"}', 50, 1, 1),
('modals-rules', '', '情态与规则', 'Modals and rules', 'can/could、have to、must 和 should', 'ket', '{"l1":"单一情态动词功能","l2":"义务、禁止和建议辨析"}', 60, 1, 1),
('future-forms', '', '将来表达', 'Future forms', 'will、going to、现在进行时和一般现在时表将来', 'ket', '{"l1":"will 与 going to 的明显语境","l2":"加入安排和时刻表"}', 70, 1, 1),
('verb-patterns', '', '动词搭配', 'Verb patterns', '动词加 -ing 或 to do', 'ket', '{"l1":"常见动词固定搭配","l2":"混合辨析"}', 80, 1, 1),
('first-conditional', '', '第一条件句', 'First conditional', 'if + 一般现在时，will + 动词原形', 'ket', '{"l1":"结构套用","l2":"主从句位置变化"}', 90, 1, 1),
('present-perfect', '', '现在完成时', 'Present perfect', '经历、for/since、just/already/yet', 'ket', '{"l1":"have/has + 过去分词和经历","l2":"for/since、just/already/yet"}', 100, 1, 1),
('pronouns-determiners', '', '代词与限定词', 'Pronouns and determiners', '宾格代词和不定代词', 'ket', '{"l1":"主格宾格替换","l2":"something/anything/nothing"}', 110, 1, 1),
('core-structures', '', '基础句型', 'Core structures', '冠词、介词、祈使句、疑问句和 there be', 'ket', '{"l1":"单一基础规则","l2":"场景化辨析"}', 120, 1, 1);

INSERT INTO system_menu
  (name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
SELECT '语法题库', '', 2, 43, 5047, 'grammar', '#', 'english/grammar/index', 0, b'1', b'1', b'1'
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE parent_id = 5047 AND path = 'grammar' AND deleted = b'0');
SET @grammar_menu_id := (SELECT id FROM system_menu WHERE parent_id = 5047 AND path = 'grammar' AND deleted = b'0' ORDER BY id LIMIT 1);
INSERT INTO system_menu
  (name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
SELECT p.name, p.permission, 3, p.sort, @grammar_menu_id, '', '#', NULL, 0, b'1', b'1', b'1'
FROM (
  SELECT '语法查询' name, 'english:grammar:query' permission, 1 sort
  UNION ALL SELECT '语法创建', 'english:grammar:create', 2
  UNION ALL SELECT '语法更新', 'english:grammar:update', 3
  UNION ALL SELECT '语法删除', 'english:grammar:delete', 4
) p WHERE NOT EXISTS (SELECT 1 FROM system_menu m WHERE m.permission = p.permission AND m.deleted = b'0');
