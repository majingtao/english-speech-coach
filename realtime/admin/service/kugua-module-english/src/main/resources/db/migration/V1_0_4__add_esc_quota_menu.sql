-- 英语课配额管理菜单（挂在 id=5047 "英语口语" 下）
-- 父菜单 + 查询/更新两个按钮权限（对应 @PreAuthorize 'esc:quota:query' / 'esc:quota:update'）

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
VALUES (5180, '额度配置', '', 2, 30, 5047, 'quota', '#', 'english/quota/index', 0, b'1', b'1', b'1');

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
VALUES (5181, '配额查询', 'esc:quota:query', 3, 1, 5180, '', '#', NULL, 0, b'1', b'1', b'1');

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show)
VALUES (5182, '配额更新', 'esc:quota:update', 3, 2, 5180, '', '#', NULL, 0, b'1', b'1', b'1');
