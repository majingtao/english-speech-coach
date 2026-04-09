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