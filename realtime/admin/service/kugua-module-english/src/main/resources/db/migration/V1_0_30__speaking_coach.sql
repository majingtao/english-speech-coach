-- KET coach v1. Explicit user/tenant predicates are required for these JDBC tables.
CREATE TABLE IF NOT EXISTS esc_coach_task (
  id VARCHAR(80) PRIMARY KEY,
  payload_json LONGTEXT NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  tenant_id BIGINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS esc_coach_session (
  id VARCHAR(36) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  tenant_id BIGINT NOT NULL,
  mode VARCHAR(16) NOT NULL,
  task_ids LONGTEXT NOT NULL,
  hint_task_ids LONGTEXT NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'active',
  plan_id VARCHAR(36),
  plan_task_id VARCHAR(80),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deadline_at DATETIME NULL,
  ended_at DATETIME NULL,
  INDEX idx_coach_sessions_user (tenant_id, user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS esc_coach_attempt (
  id VARCHAR(36) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  tenant_id BIGINT NOT NULL,
  session_id VARCHAR(36) NOT NULL,
  task_id VARCHAR(80) NOT NULL,
  stage VARCHAR(16) NOT NULL,
  parent_id VARCHAR(36),
  assisted TINYINT NOT NULL DEFAULT 0,
  turns_json LONGTEXT NOT NULL,
  feedback_json LONGTEXT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'pending',
  error_text VARCHAR(300) NOT NULL DEFAULT '',
  grade_started DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_coach_attempt_session (tenant_id, user_id, session_id, created_at),
  INDEX idx_coach_attempt_evidence (tenant_id, user_id, status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS esc_coach_plan (
  id VARCHAR(36) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  tenant_id BIGINT NOT NULL,
  plan_date DATE NOT NULL,
  minutes INT NOT NULL,
  items_json LONGTEXT NOT NULL,
  UNIQUE KEY uk_coach_plan_day (tenant_id, user_id, plan_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
