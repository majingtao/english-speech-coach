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