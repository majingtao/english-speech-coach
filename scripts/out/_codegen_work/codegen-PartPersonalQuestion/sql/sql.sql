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