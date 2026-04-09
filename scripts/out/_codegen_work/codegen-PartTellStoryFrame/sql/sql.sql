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