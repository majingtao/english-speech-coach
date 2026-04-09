
-- ============================================================
-- codegen-PartCollabTask.sql
-- ============================================================
-- 菜单 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status, component_name
)
VALUES (
    '协作任务 - 主体管理', '', 2, 0, 5047,
    'part-collab-task', '', 'english/partCollabTask/index', 0, 'PartCollabTask'
);

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '协作任务 - 主体查询', 'english:part-collab-task:query', 3, 1, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '协作任务 - 主体创建', 'english:part-collab-task:create', 3, 2, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '协作任务 - 主体更新', 'english:part-collab-task:update', 3, 3, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '协作任务 - 主体删除', 'english:part-collab-task:delete', 3, 4, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '协作任务 - 主体导出', 'english:part-collab-task:export', 3, 5, @parentId,
    '', '', '', 0
);

-- ============================================================
-- codegen-PartFindDiffPair.sql
-- ============================================================
-- 菜单 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status, component_name
)
VALUES (
    '找不同 - 图对管理', '', 2, 0, 5047,
    'part-find-diff-pair', '', 'english/partFindDiffPair/index', 0, 'PartFindDiffPair'
);

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '找不同 - 图对查询', 'english:part-find-diff-pair:query', 3, 1, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '找不同 - 图对创建', 'english:part-find-diff-pair:create', 3, 2, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '找不同 - 图对更新', 'english:part-find-diff-pair:update', 3, 3, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '找不同 - 图对删除', 'english:part-find-diff-pair:delete', 3, 4, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '找不同 - 图对导出', 'english:part-find-diff-pair:export', 3, 5, @parentId,
    '', '', '', 0
);

-- ============================================================
-- codegen-PartGeneralConvoTopic.sql
-- ============================================================
-- 菜单 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status, component_name
)
VALUES (
    '一般对话 - 主题管理', '', 2, 0, 5047,
    'part-general-convo-topic', '', 'english/partGeneralConvoTopic/index', 0, 'PartGeneralConvoTopic'
);

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '一般对话 - 主题查询', 'english:part-general-convo-topic:query', 3, 1, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '一般对话 - 主题创建', 'english:part-general-convo-topic:create', 3, 2, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '一般对话 - 主题更新', 'english:part-general-convo-topic:update', 3, 3, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '一般对话 - 主题删除', 'english:part-general-convo-topic:delete', 3, 4, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '一般对话 - 主题导出', 'english:part-general-convo-topic:export', 3, 5, @parentId,
    '', '', '', 0
);

-- ============================================================
-- codegen-PartInfoExchangeCard.sql
-- ============================================================
-- 菜单 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status, component_name
)
VALUES (
    '信息互换 - 卡片管理', '', 2, 0, 5047,
    'part-info-exchange-card', '', 'english/partInfoExchangeCard/index', 0, 'PartInfoExchangeCard'
);

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '信息互换 - 卡片查询', 'english:part-info-exchange-card:query', 3, 1, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '信息互换 - 卡片创建', 'english:part-info-exchange-card:create', 3, 2, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '信息互换 - 卡片更新', 'english:part-info-exchange-card:update', 3, 3, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '信息互换 - 卡片删除', 'english:part-info-exchange-card:delete', 3, 4, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '信息互换 - 卡片导出', 'english:part-info-exchange-card:export', 3, 5, @parentId,
    '', '', '', 0
);

-- ============================================================
-- codegen-PartLongTurnPhoto.sql
-- ============================================================
-- 菜单 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status, component_name
)
VALUES (
    '独立长答 - 图片描述管理', '', 2, 0, 5047,
    'part-long-turn-photo', '', 'english/partLongTurnPhoto/index', 0, 'PartLongTurnPhoto'
);

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '独立长答 - 图片描述查询', 'english:part-long-turn-photo:query', 3, 1, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '独立长答 - 图片描述创建', 'english:part-long-turn-photo:create', 3, 2, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '独立长答 - 图片描述更新', 'english:part-long-turn-photo:update', 3, 3, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '独立长答 - 图片描述删除', 'english:part-long-turn-photo:delete', 3, 4, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '独立长答 - 图片描述导出', 'english:part-long-turn-photo:export', 3, 5, @parentId,
    '', '', '', 0
);

-- ============================================================
-- codegen-PartPersonalQuestion.sql
-- ============================================================
-- 菜单 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status, component_name
)
VALUES (
    '个人问答 - 问题管理', '', 2, 0, 5047,
    'part-personal-question', '', 'english/partPersonalQuestion/index', 0, 'PartPersonalQuestion'
);

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '个人问答 - 问题查询', 'english:part-personal-question:query', 3, 1, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '个人问答 - 问题创建', 'english:part-personal-question:create', 3, 2, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '个人问答 - 问题更新', 'english:part-personal-question:update', 3, 3, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '个人问答 - 问题删除', 'english:part-personal-question:delete', 3, 4, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '个人问答 - 问题导出', 'english:part-personal-question:export', 3, 5, @parentId,
    '', '', '', 0
);

-- ============================================================
-- codegen-PartTellStoryFrame.sql
-- ============================================================
-- 菜单 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status, component_name
)
VALUES (
    '讲故事 - 单帧管理', '', 2, 0, 5047,
    'part-tell-story-frame', '', 'english/partTellStoryFrame/index', 0, 'PartTellStoryFrame'
);

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '讲故事 - 单帧查询', 'english:part-tell-story-frame:query', 3, 1, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '讲故事 - 单帧创建', 'english:part-tell-story-frame:create', 3, 2, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '讲故事 - 单帧更新', 'english:part-tell-story-frame:update', 3, 3, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '讲故事 - 单帧删除', 'english:part-tell-story-frame:delete', 3, 4, @parentId,
    '', '', '', 0
);
INSERT INTO system_menu(
    name, permission, type, sort, parent_id,
    path, icon, component, status
)
VALUES (
    '讲故事 - 单帧导出', 'english:part-tell-story-frame:export', 3, 5, @parentId,
    '', '', '', 0
);
