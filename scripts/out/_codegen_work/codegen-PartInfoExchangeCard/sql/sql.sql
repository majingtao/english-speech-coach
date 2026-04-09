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