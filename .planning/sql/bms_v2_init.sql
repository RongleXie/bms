-- ============================================================
-- BMS V2.0.0 数据初始化脚本
-- 版本: v2.0.0
-- 日期: 2026-03-26
-- 说明: V2.0.0 功能的菜单权限和 API 权限初始化
-- ============================================================

-- ============================================================
-- 1. 创建全文搜索索引
-- ============================================================

-- 检查并创建全文索引（如果不存在）
-- ALTER TABLE BMS_ARTICLE ADD FULLTEXT INDEX FT_ARTICLE_TITLE_CONTENT (TITLE, CONTENT) WITH PARSER ngram;
-- ALTER TABLE BMS_ARTICLE ADD FULLTEXT INDEX FT_ARTICLE_SUMMARY (SUMMARY) WITH PARSER ngram;

-- ============================================================
-- 2. 菜单资源 (SYS_RESOURCE)
-- ============================================================

-- 2.1 版本历史管理菜单 (作为文章管理子菜单)
INSERT INTO SYS_RESOURCE (ID, PARENT_ID, TITLE, NAME, CODE, CATEGORY, MENU_TYPE, MODULE, PATH, COMPONENT, ICON, SORT_CODE, VISIBLE, DELETE_FLAG, CREATE_TIME) VALUES
('1801060000000000001', '1801000000000000002', '版本历史', 'version', 'bmsArticleVersion', 'MENU', 'MENU', '1548901111999773976', '/biz/article/version', 'biz/article/version', 'history-outlined', 100, 'TRUE', 'NOT_DELETE', NOW());

-- 2.2 版本历史按钮权限
INSERT INTO SYS_RESOURCE (ID, PARENT_ID, TITLE, NAME, CODE, CATEGORY, MENU_TYPE, MODULE, SORT_CODE, DELETE_FLAG, CREATE_TIME) VALUES
('1801060100000000001', '1801060000000000001', '查看版本', NULL, 'bizArticleVersionList', 'BUTTON', 'BUTTON', '1548901111999773976', 1, 'NOT_DELETE', NOW()),
('1801060100000000002', '1801060000000000001', '版本对比', NULL, 'bizArticleVersionCompare', 'BUTTON', 'BUTTON', '1548901111999773976', 2, 'NOT_DELETE', NOW()),
('1801060100000000003', '1801060000000000001', '版本回滚', NULL, 'bizArticleVersionRollback', 'BUTTON', 'BUTTON', '1548901111999773976', 3, 'NOT_DELETE', NOW()),
('1801060100000000004', '1801060000000000001', '删除版本', NULL, 'bizArticleVersionDelete', 'BUTTON', 'BUTTON', '1548901111999773976', 4, 'NOT_DELETE', NOW());

-- 2.3 文章管理新增定时发布按钮
INSERT INTO SYS_RESOURCE (ID, PARENT_ID, TITLE, NAME, CODE, CATEGORY, MENU_TYPE, MODULE, SORT_CODE, DELETE_FLAG, CREATE_TIME) VALUES
('1801010000000000007', '1801000000000000002', '定时发布', NULL, 'bizArticleScheduledPublish', 'BUTTON', 'BUTTON', '1548901111999773976', 7, 'NOT_DELETE', NOW()),
('1801010000000000008', '1801000000000000002', '取消定时', NULL, 'bizArticleCancelScheduled', 'BUTTON', 'BUTTON', '1548901111999773976', 8, 'NOT_DELETE', NOW());

-- 2.4 全文搜索菜单 (独立菜单)
INSERT INTO SYS_RESOURCE (ID, PARENT_ID, TITLE, NAME, CODE, CATEGORY, MENU_TYPE, MODULE, PATH, COMPONENT, ICON, SORT_CODE, VISIBLE, DELETE_FLAG, CREATE_TIME) VALUES
('1801070000000000001', '0', '全文搜索', 'search', 'bmsArticleSearch', 'MENU', 'MENU', '1548901111999773976', '/biz/article/search', 'biz/article/search', 'search-outlined', 110, 'TRUE', 'NOT_DELETE', NOW());

-- 2.5 全文搜索按钮权限
INSERT INTO SYS_RESOURCE (ID, PARENT_ID, TITLE, NAME, CODE, CATEGORY, MENU_TYPE, MODULE, SORT_CODE, DELETE_FLAG, CREATE_TIME) VALUES
('1801070100000000001', '1801070000000000001', '搜索', NULL, 'bizArticleSearchQuery', 'BUTTON', 'BUTTON', '1548901111999773976', 1, 'NOT_DELETE', NOW()),
('1801070100000000002', '1801070000000000001', '高级搜索', NULL, 'bizArticleSearchAdvanced', 'BUTTON', 'BUTTON', '1548901111999773976', 2, 'NOT_DELETE', NOW());

-- 2.6 定时发布管理菜单 (独立菜单)
INSERT INTO SYS_RESOURCE (ID, PARENT_ID, TITLE, NAME, CODE, CATEGORY, MENU_TYPE, MODULE, PATH, COMPONENT, ICON, SORT_CODE, VISIBLE, DELETE_FLAG, CREATE_TIME) VALUES
('1801080000000000001', '0', '定时发布', 'scheduled', 'bmsScheduledPublish', 'MENU', 'MENU', '1548901111999773976', '/biz/article/scheduled', 'biz/article/scheduled', 'clock-circle-outlined', 120, 'TRUE', 'NOT_DELETE', NOW());

-- 2.7 定时发布按钮权限
INSERT INTO SYS_RESOURCE (ID, PARENT_ID, TITLE, NAME, CODE, CATEGORY, MENU_TYPE, MODULE, SORT_CODE, DELETE_FLAG, CREATE_TIME) VALUES
('1801080100000000001', '1801080000000000001', '查看列表', NULL, 'bizScheduledList', 'BUTTON', 'BUTTON', '1548901111999773976', 1, 'NOT_DELETE', NOW()),
('1801080100000000002', '1801080000000000001', '立即发布', NULL, 'bizScheduledPublishNow', 'BUTTON', 'BUTTON', '1548901111999773976', 2, 'NOT_DELETE', NOW()),
('1801080100000000003', '1801080000000000001', '取消定时', NULL, 'bizScheduledCancel', 'BUTTON', 'BUTTON', '1548901111999773976', 3, 'NOT_DELETE', NOW());

-- ============================================================
-- 3. 角色菜单关系 (SYS_RELATION - SYS_ROLE_HAS_RESOURCE)
-- 超级管理员角色ID: 1570687866138206208
-- ============================================================

-- 3.1 版本历史菜单关系
INSERT INTO SYS_RELATION (ID, OBJECT_ID, TARGET_ID, CATEGORY, EXT_JSON) VALUES
('1802000000000000101', '1570687866138206208', '1801060000000000001', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1801060000000000001","buttonInfo":["1801060100000000001","1801060100000000002","1801060100000000003","1801060100000000004"]}');

-- 3.2 全文搜索菜单关系
INSERT INTO SYS_RELATION (ID, OBJECT_ID, TARGET_ID, CATEGORY, EXT_JSON) VALUES
('1802000000000000102', '1570687866138206208', '1801070000000000001', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1801070000000000001","buttonInfo":["1801070100000000001","1801070100000000002"]}');

-- 3.3 定时发布菜单关系
INSERT INTO SYS_RELATION (ID, OBJECT_ID, TARGET_ID, CATEGORY, EXT_JSON) VALUES
('1802000000000000103', '1570687866138206208', '1801080000000000001', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1801080000000000001","buttonInfo":["1801080100000000001","1801080100000000002","1801080100000000003"]}');

-- 3.4 更新文章管理菜单的 buttonInfo，添加定时发布按钮和版本历史菜单
-- UPDATE SYS_RELATION 
-- SET EXT_JSON = '{"menuId":"1801000000000000002","buttonInfo":["1801010000000000001","1801010000000000002","1801010000000000003","1801010000000000004","1801010000000000005","1801010000000000006","1801010000000000007","1801010000000000008","1801060000000000001"]}'
-- WHERE OBJECT_ID = '1570687866138206208' AND TARGET_ID = '1801000000000000002';

-- ============================================================
-- 4. API权限 (SYS_RELATION - SYS_ROLE_HAS_PERMISSION)
-- 超级管理员角色ID: 1570687866138206208
-- ============================================================

-- 4.1 版本历史 API 权限
INSERT INTO SYS_RELATION (ID, OBJECT_ID, TARGET_ID, CATEGORY, EXT_JSON) VALUES
('1803000000000000001', '1570687866138206208', '/biz/articleVersion/page', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/biz/articleVersion/page","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
('1803000000000000002', '1570687866138206208', '/biz/articleVersion/list', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/biz/articleVersion/list","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
('1803000000000000003', '1570687866138206208', '/biz/articleVersion/detail', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/biz/articleVersion/detail","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
('1803000000000000004', '1570687866138206208', '/biz/articleVersion/rollback', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/biz/articleVersion/rollback","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
('1803000000000000005', '1570687866138206208', '/biz/articleVersion/latest', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/biz/articleVersion/latest","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}');

-- 4.2 全文搜索 API 权限
INSERT INTO SYS_RELATION (ID, OBJECT_ID, TARGET_ID, CATEGORY, EXT_JSON) VALUES
('1803000000000000011', '1570687866138206208', '/biz/articleSearch/search', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/biz/articleSearch/search","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
('1803000000000000012', '1570687866138206208', '/biz/articleSearch/searchAdvanced', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/biz/articleSearch/searchAdvanced","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
('1803000000000000013', '1570687866138206208', '/biz/articleSearch/suggest', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/biz/articleSearch/suggest","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}');

-- 4.3 定时发布 API 权限
INSERT INTO SYS_RELATION (ID, OBJECT_ID, TARGET_ID, CATEGORY, EXT_JSON) VALUES
('1803000000000000021', '1570687866138206208', '/biz/article/scheduledPublish', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/biz/article/scheduledPublish","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
('1803000000000000022', '1570687866138206208', '/biz/article/cancelScheduled', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/biz/article/cancelScheduled","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
('1803000000000000023', '1570687866138206208', '/biz/article/scheduledList', 'SYS_ROLE_HAS_PERMISSION', '{"apiUrl":"/biz/article/scheduledList","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}');

-- ============================================================
-- 5. 清除缓存
-- ============================================================
-- 执行完成后需要清除 Redis 缓存或重新登录

SELECT 'V2.0.0 数据初始化完成!' AS result;