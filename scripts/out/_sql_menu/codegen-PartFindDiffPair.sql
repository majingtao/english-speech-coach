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