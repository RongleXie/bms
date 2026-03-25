-- ============================================================
-- 博客管理系统 (BMS) 菜单权限初始化脚本
-- 版本: v1.0.0
-- 日期: 2026-03-24
-- 说明: 包含菜单资源、按钮资源、角色菜单关系、API权限
-- ============================================================

SET NAMES utf8mb4;

-- ============================================================
-- 变量定义
-- ============================================================
-- 超级管理员角色ID
SET @SUPER_ADMIN_ROLE_ID = '1570687866138206208';
-- 业务模块ID
SET @BIZ_MODULE_ID = '1548901111999773976';

-- ============================================================
-- 1. 菜单资源配置 (sys_resource)
-- ============================================================

-- -----------------------------------------------------------
-- 1.1 一级目录: 博客管理
-- -----------------------------------------------------------
INSERT INTO `sys_resource` (
    `ID`, `TITLE`, `CATEGORY`, `MENU_TYPE`, `PARENT_ID`, `MODULE`, 
    `PATH`, `COMPONENT`, `ICON`, `CODE`, `SORT_CODE`, `VISIBLE`, `DELETE_FLAG`
) VALUES (
    '1801000000000000001', '博客管理', 'MENU', 'CATALOG', '0', @BIZ_MODULE_ID,
    '/biz', NULL, 'read-outlined', 'bms_blog_manage', 60, 'TRUE', 'NOT_DELETE'
) ON DUPLICATE KEY UPDATE 
    TITLE = VALUES(TITLE), 
    CODE = VALUES(CODE), 
    VISIBLE = VALUES(VISIBLE), 
    SORT_CODE = VALUES(SORT_CODE),
    COMPONENT = VALUES(COMPONENT),
    DELETE_FLAG = VALUES(DELETE_FLAG);

-- -----------------------------------------------------------
-- 1.2 二级菜单
-- -----------------------------------------------------------

-- 文章管理
INSERT INTO `sys_resource` (
    `ID`, `TITLE`, `CATEGORY`, `MENU_TYPE`, `PARENT_ID`, `MODULE`,
    `PATH`, `COMPONENT`, `ICON`, `CODE`, `SORT_CODE`, `VISIBLE`, `DELETE_FLAG`
) VALUES (
    '1801000000000000002', '文章管理', 'MENU', 'MENU', '1801000000000000001', @BIZ_MODULE_ID,
    '/biz/article', 'biz/article/index', 'file-text-outlined', 'bms_article', 1, 'TRUE', 'NOT_DELETE'
) ON DUPLICATE KEY UPDATE 
    TITLE = VALUES(TITLE), 
    CODE = VALUES(CODE), 
    VISIBLE = VALUES(VISIBLE),
    SORT_CODE = VALUES(SORT_CODE),
    DELETE_FLAG = VALUES(DELETE_FLAG);

-- 分类管理
INSERT INTO `sys_resource` (
    `ID`, `TITLE`, `CATEGORY`, `MENU_TYPE`, `PARENT_ID`, `MODULE`,
    `PATH`, `COMPONENT`, `ICON`, `CODE`, `SORT_CODE`, `VISIBLE`, `DELETE_FLAG`
) VALUES (
    '1801000000000000003', '分类管理', 'MENU', 'MENU', '1801000000000000001', @BIZ_MODULE_ID,
    '/biz/category', 'biz/category/index', 'folder-outlined', 'bms_category', 2, 'TRUE', 'NOT_DELETE'
) ON DUPLICATE KEY UPDATE 
    TITLE = VALUES(TITLE), 
    CODE = VALUES(CODE), 
    VISIBLE = VALUES(VISIBLE),
    SORT_CODE = VALUES(SORT_CODE),
    DELETE_FLAG = VALUES(DELETE_FLAG);

-- 标签管理
INSERT INTO `sys_resource` (
    `ID`, `TITLE`, `CATEGORY`, `MENU_TYPE`, `PARENT_ID`, `MODULE`,
    `PATH`, `COMPONENT`, `ICON`, `CODE`, `SORT_CODE`, `VISIBLE`, `DELETE_FLAG`
) VALUES (
    '1801000000000000004', '标签管理', 'MENU', 'MENU', '1801000000000000001', @BIZ_MODULE_ID,
    '/biz/tag', 'biz/tag/index', 'tags-outlined', 'bms_tag', 3, 'TRUE', 'NOT_DELETE'
) ON DUPLICATE KEY UPDATE 
    TITLE = VALUES(TITLE), 
    CODE = VALUES(CODE), 
    VISIBLE = VALUES(VISIBLE),
    SORT_CODE = VALUES(SORT_CODE),
    DELETE_FLAG = VALUES(DELETE_FLAG);

-- 评论管理
INSERT INTO `sys_resource` (
    `ID`, `TITLE`, `CATEGORY`, `MENU_TYPE`, `PARENT_ID`, `MODULE`,
    `PATH`, `COMPONENT`, `ICON`, `CODE`, `SORT_CODE`, `VISIBLE`, `DELETE_FLAG`
) VALUES (
    '1801000000000000005', '评论管理', 'MENU', 'MENU', '1801000000000000001', @BIZ_MODULE_ID,
    '/biz/comment', 'biz/comment/index', 'message-outlined', 'bms_comment', 4, 'TRUE', 'NOT_DELETE'
) ON DUPLICATE KEY UPDATE 
    TITLE = VALUES(TITLE), 
    CODE = VALUES(CODE), 
    VISIBLE = VALUES(VISIBLE),
    SORT_CODE = VALUES(SORT_CODE),
    DELETE_FLAG = VALUES(DELETE_FLAG);

-- 媒体管理
INSERT INTO `sys_resource` (
    `ID`, `TITLE`, `CATEGORY`, `MENU_TYPE`, `PARENT_ID`, `MODULE`,
    `PATH`, `COMPONENT`, `ICON`, `CODE`, `SORT_CODE`, `VISIBLE`, `DELETE_FLAG`
) VALUES (
    '1801000000000000006', '媒体管理', 'MENU', 'MENU', '1801000000000000001', @BIZ_MODULE_ID,
    '/biz/media', 'biz/media/index', 'picture-outlined', 'bms_media', 5, 'TRUE', 'NOT_DELETE'
) ON DUPLICATE KEY UPDATE 
    TITLE = VALUES(TITLE), 
    CODE = VALUES(CODE), 
    VISIBLE = VALUES(VISIBLE),
    SORT_CODE = VALUES(SORT_CODE),
    DELETE_FLAG = VALUES(DELETE_FLAG);

-- ============================================================
-- 2. 按钮资源配置 (sys_resource, CATEGORY='BUTTON')
-- ============================================================

-- -----------------------------------------------------------
-- 2.1 文章管理按钮
-- -----------------------------------------------------------
INSERT INTO `sys_resource` (`ID`, `TITLE`, `CATEGORY`, `MENU_TYPE`, `PARENT_ID`, `MODULE`, `CODE`, `SORT_CODE`) VALUES
('1801010000000000001', '新增', 'BUTTON', 'BUTTON', '1801000000000000002', @BIZ_MODULE_ID, 'bizArticleAdd', 1),
('1801010000000000002', '编辑', 'BUTTON', 'BUTTON', '1801000000000000002', @BIZ_MODULE_ID, 'bizArticleEdit', 2),
('1801010000000000003', '删除', 'BUTTON', 'BUTTON', '1801000000000000002', @BIZ_MODULE_ID, 'bizArticleDelete', 3),
('1801010000000000004', '查看详情', 'BUTTON', 'BUTTON', '1801000000000000002', @BIZ_MODULE_ID, 'bizArticleDetail', 4),
('1801010000000000005', '发布', 'BUTTON', 'BUTTON', '1801000000000000002', @BIZ_MODULE_ID, 'bizArticlePublish', 5),
('1801010000000000006', '取消发布', 'BUTTON', 'BUTTON', '1801000000000000002', @BIZ_MODULE_ID, 'bizArticleUnpublish', 6)
ON DUPLICATE KEY UPDATE TITLE = VALUES(TITLE), CODE = VALUES(CODE);

-- -----------------------------------------------------------
-- 2.2 分类管理按钮
-- -----------------------------------------------------------
INSERT INTO `sys_resource` (`ID`, `TITLE`, `CATEGORY`, `MENU_TYPE`, `PARENT_ID`, `MODULE`, `CODE`, `SORT_CODE`) VALUES
('1801020000000000001', '新增', 'BUTTON', 'BUTTON', '1801000000000000003', @BIZ_MODULE_ID, 'bizCategoryAdd', 1),
('1801020000000000002', '编辑', 'BUTTON', 'BUTTON', '1801000000000000003', @BIZ_MODULE_ID, 'bizCategoryEdit', 2),
('1801020000000000003', '删除', 'BUTTON', 'BUTTON', '1801000000000000003', @BIZ_MODULE_ID, 'bizCategoryDelete', 3),
('1801020000000000004', '查看详情', 'BUTTON', 'BUTTON', '1801000000000000003', @BIZ_MODULE_ID, 'bizCategoryDetail', 4),
('1801020000000000005', '启用', 'BUTTON', 'BUTTON', '1801000000000000003', @BIZ_MODULE_ID, 'bizCategoryEnableStatus', 5),
('1801020000000000006', '禁用', 'BUTTON', 'BUTTON', '1801000000000000003', @BIZ_MODULE_ID, 'bizCategoryDisableStatus', 6)
ON DUPLICATE KEY UPDATE TITLE = VALUES(TITLE), CODE = VALUES(CODE);

-- -----------------------------------------------------------
-- 2.3 标签管理按钮
-- -----------------------------------------------------------
INSERT INTO `sys_resource` (`ID`, `TITLE`, `CATEGORY`, `MENU_TYPE`, `PARENT_ID`, `MODULE`, `CODE`, `SORT_CODE`) VALUES
('1801030000000000001', '新增', 'BUTTON', 'BUTTON', '1801000000000000004', @BIZ_MODULE_ID, 'bizTagAdd', 1),
('1801030000000000002', '编辑', 'BUTTON', 'BUTTON', '1801000000000000004', @BIZ_MODULE_ID, 'bizTagEdit', 2),
('1801030000000000003', '删除', 'BUTTON', 'BUTTON', '1801000000000000004', @BIZ_MODULE_ID, 'bizTagDelete', 3),
('1801030000000000004', '查看详情', 'BUTTON', 'BUTTON', '1801000000000000004', @BIZ_MODULE_ID, 'bizTagDetail', 4)
ON DUPLICATE KEY UPDATE TITLE = VALUES(TITLE), CODE = VALUES(CODE);

-- -----------------------------------------------------------
-- 2.4 评论管理按钮
-- -----------------------------------------------------------
INSERT INTO `sys_resource` (`ID`, `TITLE`, `CATEGORY`, `MENU_TYPE`, `PARENT_ID`, `MODULE`, `CODE`, `SORT_CODE`) VALUES
('1801040000000000001', '新增', 'BUTTON', 'BUTTON', '1801000000000000005', @BIZ_MODULE_ID, 'bizCommentAdd', 1),
('1801040000000000002', '删除', 'BUTTON', 'BUTTON', '1801000000000000005', @BIZ_MODULE_ID, 'bizCommentDelete', 2),
('1801040000000000003', '查看详情', 'BUTTON', 'BUTTON', '1801000000000000005', @BIZ_MODULE_ID, 'bizCommentDetail', 3),
('1801040000000000004', '通过', 'BUTTON', 'BUTTON', '1801000000000000005', @BIZ_MODULE_ID, 'bizCommentApprove', 4),
('1801040000000000005', '拒绝', 'BUTTON', 'BUTTON', '1801000000000000005', @BIZ_MODULE_ID, 'bizCommentReject', 5)
ON DUPLICATE KEY UPDATE TITLE = VALUES(TITLE), CODE = VALUES(CODE);

-- -----------------------------------------------------------
-- 2.5 媒体管理按钮
-- -----------------------------------------------------------
INSERT INTO `sys_resource` (`ID`, `TITLE`, `CATEGORY`, `MENU_TYPE`, `PARENT_ID`, `MODULE`, `CODE`, `SORT_CODE`) VALUES
('1801050000000000001', '新增', 'BUTTON', 'BUTTON', '1801000000000000006', @BIZ_MODULE_ID, 'bizMediaAdd', 1),
('1801050000000000002', '编辑', 'BUTTON', 'BUTTON', '1801000000000000006', @BIZ_MODULE_ID, 'bizMediaEdit', 2),
('1801050000000000003', '删除', 'BUTTON', 'BUTTON', '1801000000000000006', @BIZ_MODULE_ID, 'bizMediaDelete', 3),
('1801050000000000004', '查看详情', 'BUTTON', 'BUTTON', '1801000000000000006', @BIZ_MODULE_ID, 'bizMediaDetail', 4)
ON DUPLICATE KEY UPDATE TITLE = VALUES(TITLE), CODE = VALUES(CODE);

-- ============================================================
-- 3. 角色菜单关系 (SYS_RELATION - SYS_ROLE_HAS_RESOURCE)
-- ============================================================

-- ⚠️ 重要：按钮权限存储在菜单关系的 EXT_JSON 字段的 buttonInfo 数组中
-- 格式: {"menuId":"菜单ID","buttonInfo":["按钮ID1","按钮ID2",...]}

-- 文章管理菜单（含按钮）
INSERT INTO `sys_relation` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`) VALUES
('1802000000000000002', @SUPER_ADMIN_ROLE_ID, '1801000000000000002', 'SYS_ROLE_HAS_RESOURCE', 
 '{"menuId":"1801000000000000002","buttonInfo":["1801010000000000001","1801010000000000002","1801010000000000003","1801010000000000004","1801010000000000005","1801010000000000006"]}')
ON DUPLICATE KEY UPDATE EXT_JSON = VALUES(EXT_JSON);

-- 分类管理菜单（含按钮）
INSERT INTO `sys_relation` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`) VALUES
('1802000000000000003', @SUPER_ADMIN_ROLE_ID, '1801000000000000003', 'SYS_ROLE_HAS_RESOURCE',
 '{"menuId":"1801000000000000003","buttonInfo":["1801020000000000001","1801020000000000002","1801020000000000003","1801020000000000004","1801020000000000005","1801020000000000006"]}')
ON DUPLICATE KEY UPDATE EXT_JSON = VALUES(EXT_JSON);

-- 标签管理菜单（含按钮）
INSERT INTO `sys_relation` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`) VALUES
('1802000000000000004', @SUPER_ADMIN_ROLE_ID, '1801000000000000004', 'SYS_ROLE_HAS_RESOURCE',
 '{"menuId":"1801000000000000004","buttonInfo":["1801030000000000001","1801030000000000002","1801030000000000003","1801030000000000004"]}')
ON DUPLICATE KEY UPDATE EXT_JSON = VALUES(EXT_JSON);

-- 评论管理菜单（含按钮）
INSERT INTO `sys_relation` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`) VALUES
('1802000000000000005', @SUPER_ADMIN_ROLE_ID, '1801000000000000005', 'SYS_ROLE_HAS_RESOURCE',
 '{"menuId":"1801000000000000005","buttonInfo":["1801040000000000001","1801040000000000002","1801040000000000003","1801040000000000004","1801040000000000005"]}')
ON DUPLICATE KEY UPDATE EXT_JSON = VALUES(EXT_JSON);

-- 媒体管理菜单（含按钮）
INSERT INTO `sys_relation` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`) VALUES
('1802000000000000006', @SUPER_ADMIN_ROLE_ID, '1801000000000000006', 'SYS_ROLE_HAS_RESOURCE',
 '{"menuId":"1801000000000000006","buttonInfo":["1801050000000000001","1801050000000000002","1801050000000000003","1801050000000000004"]}')
ON DUPLICATE KEY UPDATE EXT_JSON = VALUES(EXT_JSON);

-- ============================================================
-- 4. API权限配置 (SYS_RELATION - SYS_ROLE_HAS_PERMISSION)
-- ============================================================

-- -----------------------------------------------------------
-- 4.1 文章管理API权限
-- -----------------------------------------------------------
INSERT INTO `sys_relation` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`) VALUES
-- 分页查询
('1803010000000000001', @SUPER_ADMIN_ROLE_ID, '/biz/article/page', 'SYS_ROLE_HAS_PERMISSION', 
 '{"apiUrl":"/biz/article/page","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 新增
('1803010000000000002', @SUPER_ADMIN_ROLE_ID, '/biz/article/add', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/article/add","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 编辑
('1803010000000000003', @SUPER_ADMIN_ROLE_ID, '/biz/article/edit', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/article/edit","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 删除
('1803010000000000004', @SUPER_ADMIN_ROLE_ID, '/biz/article/delete', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/article/delete","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 查看详情
('1803010000000000005', @SUPER_ADMIN_ROLE_ID, '/biz/article/detail', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/article/detail","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 发布
('1803010000000000006', @SUPER_ADMIN_ROLE_ID, '/biz/article/publish', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/article/publish","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 取消发布
('1803010000000000007', @SUPER_ADMIN_ROLE_ID, '/biz/article/unpublish', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/article/unpublish","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}')
ON DUPLICATE KEY UPDATE ID = ID;

-- -----------------------------------------------------------
-- 4.2 分类管理API权限
-- -----------------------------------------------------------
INSERT INTO `sys_relation` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`) VALUES
-- 分页查询
('1803020000000000001', @SUPER_ADMIN_ROLE_ID, '/biz/category/page', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/category/page","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 列表查询
('1803020000000000002', @SUPER_ADMIN_ROLE_ID, '/biz/category/list', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/category/list","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 树形查询
('1803020000000000003', @SUPER_ADMIN_ROLE_ID, '/biz/category/tree', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/category/tree","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 新增
('1803020000000000004', @SUPER_ADMIN_ROLE_ID, '/biz/category/add', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/category/add","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 编辑
('1803020000000000005', @SUPER_ADMIN_ROLE_ID, '/biz/category/edit', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/category/edit","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 删除
('1803020000000000006', @SUPER_ADMIN_ROLE_ID, '/biz/category/delete', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/category/delete","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 查看详情
('1803020000000000007', @SUPER_ADMIN_ROLE_ID, '/biz/category/detail', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/category/detail","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 禁用状态
('1803020000000000008', @SUPER_ADMIN_ROLE_ID, '/biz/category/disableStatus', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/category/disableStatus","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 启用状态
('1803020000000000009', @SUPER_ADMIN_ROLE_ID, '/biz/category/enableStatus', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/category/enableStatus","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}')
ON DUPLICATE KEY UPDATE ID = ID;

-- -----------------------------------------------------------
-- 4.3 标签管理API权限
-- -----------------------------------------------------------
INSERT INTO `sys_relation` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`) VALUES
-- 分页查询
('1803030000000000001', @SUPER_ADMIN_ROLE_ID, '/biz/tag/page', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/tag/page","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 列表查询
('1803030000000000002', @SUPER_ADMIN_ROLE_ID, '/biz/tag/list', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/tag/list","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 新增
('1803030000000000003', @SUPER_ADMIN_ROLE_ID, '/biz/tag/add', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/tag/add","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 编辑
('1803030000000000004', @SUPER_ADMIN_ROLE_ID, '/biz/tag/edit', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/tag/edit","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 删除
('1803030000000000005', @SUPER_ADMIN_ROLE_ID, '/biz/tag/delete', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/tag/delete","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 查看详情
('1803030000000000006', @SUPER_ADMIN_ROLE_ID, '/biz/tag/detail', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/tag/detail","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}')
ON DUPLICATE KEY UPDATE ID = ID;

-- -----------------------------------------------------------
-- 4.4 评论管理API权限
-- -----------------------------------------------------------
INSERT INTO `sys_relation` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`) VALUES
-- 分页查询
('1803040000000000001', @SUPER_ADMIN_ROLE_ID, '/biz/comment/page', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/comment/page","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 列表查询
('1803040000000000002', @SUPER_ADMIN_ROLE_ID, '/biz/comment/list', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/comment/list","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 新增
('1803040000000000003', @SUPER_ADMIN_ROLE_ID, '/biz/comment/add', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/comment/add","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 删除
('1803040000000000004', @SUPER_ADMIN_ROLE_ID, '/biz/comment/delete', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/comment/delete","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 查看详情
('1803040000000000005', @SUPER_ADMIN_ROLE_ID, '/biz/comment/detail', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/comment/detail","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 通过审核
('1803040000000000006', @SUPER_ADMIN_ROLE_ID, '/biz/comment/approve', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/comment/approve","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 拒绝审核
('1803040000000000007', @SUPER_ADMIN_ROLE_ID, '/biz/comment/reject', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/comment/reject","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}')
ON DUPLICATE KEY UPDATE ID = ID;

-- -----------------------------------------------------------
-- 4.5 媒体管理API权限
-- -----------------------------------------------------------
INSERT INTO `sys_relation` (`ID`, `OBJECT_ID`, `TARGET_ID`, `CATEGORY`, `EXT_JSON`) VALUES
-- 分页查询
('1803050000000000001', @SUPER_ADMIN_ROLE_ID, '/biz/media/page', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/media/page","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 列表查询
('1803050000000000002', @SUPER_ADMIN_ROLE_ID, '/biz/media/list', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/media/list","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 新增
('1803050000000000003', @SUPER_ADMIN_ROLE_ID, '/biz/media/add', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/media/add","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 编辑
('1803050000000000004', @SUPER_ADMIN_ROLE_ID, '/biz/media/edit', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/media/edit","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 删除
('1803050000000000005', @SUPER_ADMIN_ROLE_ID, '/biz/media/delete', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/media/delete","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}'),
-- 查看详情
('1803050000000000006', @SUPER_ADMIN_ROLE_ID, '/biz/media/detail', 'SYS_ROLE_HAS_PERMISSION',
 '{"apiUrl":"/biz/media/detail","scopeCategory":"SCOPE_ALL","scopeDefineOrgIdList":[]}')
ON DUPLICATE KEY UPDATE ID = ID;

-- ============================================================
-- 完成
-- ============================================================
-- 菜单资源: 6条 (1个一级目录 + 5个二级菜单)
-- 按钮资源: 25条 (文章6 + 分类6 + 标签4 + 评论5 + 媒体4)
-- 角色菜单关系: 31条 (菜单+按钮)
-- API权限: 36条 (文章7 + 分类9 + 标签6 + 评论7 + 媒体6)
-- ============================================================