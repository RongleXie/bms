-- ============================================================
-- 博客管理系统 (BMS) - 索引优化脚本
-- 版本: v1.0.0
-- 日期: 2026-03-31
-- 说明: M3性能优化 Phase 3.3 - 复合索引优化
-- 
-- 问题清单:
-- 1. LIKE模糊查询索引失效
-- 2. 缺少复合索引(DELETE_FLAG等)
-- 3. 计数器并发更新问题
-- 4. 大字段冗余查询
-- 
-- 优化目标:
-- - 慢查询减少90%+
-- - 所有查询使用索引
-- - 查询性能提升30%-50%
-- ============================================================

SET NAMES utf8mb4;

-- ============================================================
-- 1. 文章表 (BMS_ARTICLE) 索引优化
-- ============================================================

-- 1.1 状态+删除标志复合索引（MyBatis-Plus自动添加DELETE_FLAG条件）
-- 查询场景: WHERE STATUS = 'PUBLISHED' AND DELETE_FLAG = 'NOT_DELETE'
ALTER TABLE `BMS_ARTICLE` ADD INDEX `IDX_ARTICLE_STATUS_DELETE` (`STATUS`, `DELETE_FLAG`);

-- 1.2 状态+发布时间复合索引（状态筛选+时间排序）
-- 查询场景: WHERE STATUS = 'PUBLISHED' ORDER BY PUBLISH_TIME DESC
ALTER TABLE `BMS_ARTICLE` ADD INDEX `IDX_ARTICLE_STATUS_PUBLISH_TIME` (`STATUS`, `PUBLISH_TIME`, `DELETE_FLAG`);

-- 1.3 分类+状态复合索引（分类文章列表查询）
-- 查询场景: WHERE CATEGORY_ID = 'xxx' AND STATUS = 'PUBLISHED' AND DELETE_FLAG = 'NOT_DELETE'
ALTER TABLE `BMS_ARTICLE` ADD INDEX `IDX_ARTICLE_CATEGORY_STATUS` (`CATEGORY_ID`, `STATUS`, `DELETE_FLAG`);

-- 1.4 作者+状态复合索引（作者文章列表查询）
-- 查询场景: WHERE AUTHOR_ID = 'xxx' AND STATUS = 'PUBLISHED' AND DELETE_FLAG = 'NOT_DELETE'
ALTER TABLE `BMS_ARTICLE` ADD INDEX `IDX_ARTICLE_AUTHOR_STATUS` (`AUTHOR_ID`, `STATUS`, `DELETE_FLAG`);

-- 1.5 置顶文章优化索引（置顶+状态+发布时间+删除标志）
-- 查询场景: WHERE IS_TOP = 1 AND STATUS = 'PUBLISHED' ORDER BY PUBLISH_TIME DESC
ALTER TABLE `BMS_ARTICLE` ADD INDEX `IDX_ARTICLE_TOP_STATUS_TIME` (`IS_TOP`, `STATUS`, `PUBLISH_TIME`, `DELETE_FLAG`);

-- 1.6 推荐文章优化索引（推荐+状态+发布时间+删除标志）
-- 查询场景: WHERE IS_RECOMMEND = 1 AND STATUS = 'PUBLISHED' ORDER BY PUBLISH_TIME DESC
ALTER TABLE `BMS_ARTICLE` ADD INDEX `IDX_ARTICLE_RECOMMEND_STATUS_TIME` (`IS_RECOMMEND`, `STATUS`, `PUBLISH_TIME`, `DELETE_FLAG`);

-- 1.7 定时发布查询索引（定时发布状态+发布时间）
-- 查询场景: WHERE STATUS = 'SCHEDULED' AND SCHEDULED_PUBLISH_TIME IS NOT NULL ORDER BY SCHEDULED_PUBLISH_TIME ASC
ALTER TABLE `BMS_ARTICLE` ADD INDEX `IDX_ARTICLE_SCHEDULED` (`STATUS`, `SCHEDULED_PUBLISH_TIME`, `DELETE_FLAG`);

-- 1.8 创建时间范围查询索引（时间范围筛选+删除标志）
-- 查询场景: WHERE CREATE_TIME BETWEEN 'xxx' AND 'xxx' AND DELETE_FLAG = 'NOT_DELETE'
ALTER TABLE `BMS_ARTICLE` ADD INDEX `IDX_ARTICLE_CREATE_TIME_DELETE` (`CREATE_TIME`, `DELETE_FLAG`);

-- 1.9 标题前缀索引（优化 LIKE '关键词%' 查询）
-- 注意: LIKE '%关键词%' 无法使用索引，建议使用全文索引
ALTER TABLE `BMS_ARTICLE` ADD INDEX `IDX_ARTICLE_TITLE_PREFIX` (`TITLE`(20));

-- ============================================================
-- 2. 评论表 (BMS_COMMENT) 索引优化
-- ============================================================

-- 2.1 文章+状态复合索引（文章评论列表查询）
-- 查询场景: WHERE ARTICLE_ID = 'xxx' AND STATUS = 'APPROVED' AND DELETE_FLAG = 'NOT_DELETE'
ALTER TABLE `BMS_COMMENT` ADD INDEX `IDX_COMMENT_ARTICLE_STATUS` (`ARTICLE_ID`, `STATUS`, `DELETE_FLAG`);

-- 2.2 文章+创建时间复合索引（文章评论时间排序）
-- 查询场景: WHERE ARTICLE_ID = 'xxx' AND DELETE_FLAG = 'NOT_DELETE' ORDER BY CREATE_TIME DESC
ALTER TABLE `BMS_COMMENT` ADD INDEX `IDX_COMMENT_ARTICLE_TIME` (`ARTICLE_ID`, `CREATE_TIME`, `DELETE_FLAG`);

-- 2.3 状态+创建时间复合索引（评论审核列表查询）
-- 查询场景: WHERE STATUS = 'PENDING' AND DELETE_FLAG = 'NOT_DELETE' ORDER BY CREATE_TIME DESC
ALTER TABLE `BMS_COMMENT` ADD INDEX `IDX_COMMENT_STATUS_TIME` (`STATUS`, `CREATE_TIME`, `DELETE_FLAG`);

-- 2.4 父评论+状态复合索引（回复评论查询）
-- 查询场景: WHERE PARENT_ID = 'xxx' AND STATUS = 'APPROVED' AND DELETE_FLAG = 'NOT_DELETE'
ALTER TABLE `BMS_COMMENT` ADD INDEX `IDX_COMMENT_PARENT_STATUS` (`PARENT_ID`, `STATUS`, `DELETE_FLAG`);

-- 2.5 评论内容前缀索引（优化 LIKE '关键词%' 查询）
-- 注意: 完全模糊查询建议使用全文搜索，此处仅优化前缀匹配
ALTER TABLE `BMS_COMMENT` ADD INDEX `IDX_COMMENT_CONTENT_PREFIX` (`CONTENT`(50));

-- ============================================================
-- 3. 媒体表 (BMS_MEDIA) 索引优化
-- ============================================================

-- 3.1 类型+状态复合索引（按类型筛选媒体）
-- 查询场景: WHERE FILE_TYPE = 'IMAGE' AND STATUS = 'ENABLE' AND DELETE_FLAG = 'NOT_DELETE'
ALTER TABLE `BMS_MEDIA` ADD INDEX `IDX_MEDIA_TYPE_STATUS` (`FILE_TYPE`, `STATUS`, `DELETE_FLAG`);

-- 3.2 类型+创建时间复合索引（按类型排序）
-- 查询场景: WHERE FILE_TYPE = 'IMAGE' AND DELETE_FLAG = 'NOT_DELETE' ORDER BY CREATE_TIME DESC
ALTER TABLE `BMS_MEDIA` ADD INDEX `IDX_MEDIA_TYPE_TIME` (`FILE_TYPE`, `CREATE_TIME`, `DELETE_FLAG`);

-- 3.3 上传用户+类型复合索引（用户媒体筛选）
-- 查询场景: WHERE UPLOAD_USER = 'xxx' AND FILE_TYPE = 'IMAGE' AND DELETE_FLAG = 'NOT_DELETE'
ALTER TABLE `BMS_MEDIA` ADD INDEX `IDX_MEDIA_USER_TYPE` (`UPLOAD_USER`, `FILE_TYPE`, `DELETE_FLAG`);

-- 3.4 状态+创建时间复合索引（媒体列表查询）
-- 查询场景: WHERE STATUS = 'ENABLE' AND DELETE_FLAG = 'NOT_DELETE' ORDER BY CREATE_TIME DESC
ALTER TABLE `BMS_MEDIA` ADD INDEX `IDX_MEDIA_STATUS_TIME` (`STATUS`, `CREATE_TIME`, `DELETE_FLAG`);

-- 3.5 文件名前缀索引（优化 LIKE '关键词%' 查询）
ALTER TABLE `BMS_MEDIA` ADD INDEX `IDX_MEDIA_FILENAME_PREFIX` (`FILE_NAME`(50));

-- ============================================================
-- 4. 分类表 (BMS_CATEGORY) 索引优化
-- ============================================================

-- 4.1 状态+删除标志复合索引（分类列表查询）
-- 查询场景: WHERE STATUS = 'ENABLE' AND DELETE_FLAG = 'NOT_DELETE'
ALTER TABLE `BMS_CATEGORY` ADD INDEX `IDX_CATEGORY_STATUS_DELETE` (`STATUS`, `DELETE_FLAG`);

-- 4.2 父分类+状态复合索引（子分类查询）
-- 查询场景: WHERE PARENT_ID = 'xxx' AND STATUS = 'ENABLE' AND DELETE_FLAG = 'NOT_DELETE'
ALTER TABLE `BMS_CATEGORY` ADD INDEX `IDX_CATEGORY_PARENT_STATUS` (`PARENT_ID`, `STATUS`, `DELETE_FLAG`);

-- ============================================================
-- 5. 标签表 (BMS_TAG) 索引优化
-- ============================================================

-- 5.1 状态+删除标志复合索引（标签列表查询）
-- 查询场景: WHERE STATUS = 'ENABLE' AND DELETE_FLAG = 'NOT_DELETE'
ALTER TABLE `BMS_TAG` ADD INDEX `IDX_TAG_STATUS_DELETE` (`STATUS`, `DELETE_FLAG`);

-- ============================================================
-- 6. 文章标签关联表 (BMS_ARTICLE_TAG) 索引优化
-- ============================================================

-- 6.1 标签+文章复合索引（按标签查询文章）
-- 查询场景: WHERE TAG_ID = 'xxx'
ALTER TABLE `BMS_ARTICLE_TAG` ADD INDEX `IDX_ARTICLE_TAG_TAG_FIRST` (`TAG_ID`, `ARTICLE_ID`);

-- ============================================================
-- 7. 删除冗余索引（单列索引被复合索引覆盖的情况）
-- ============================================================

-- 注意: 以下索引可能被新添加的复合索引覆盖，需要评估是否删除
-- 在生产环境执行前，请先使用 EXPLAIN 分析查询计划

-- 7.1 文章表：IDX_ARTICLE_STATUS 可能被 IDX_ARTICLE_STATUS_DELETE 覆盖
-- ALTER TABLE `BMS_ARTICLE` DROP INDEX `IDX_ARTICLE_STATUS`;

-- 7.2 评论表：IDX_COMMENT_STATUS 可能被 IDX_COMMENT_STATUS_TIME 覆盖
-- ALTER TABLE `BMS_COMMENT` DROP INDEX `IDX_COMMENT_STATUS`;

-- 7.3 媒体表：IDX_MEDIA_TYPE 可能被 IDX_MEDIA_TYPE_STATUS 覆盖
-- ALTER TABLE `BMS_MEDIA` DROP INDEX `IDX_MEDIA_TYPE`;

-- ============================================================
-- 8. LIKE查询优化说明
-- ============================================================

-- 8.1 前缀匹配优化
-- LIKE '关键词%' 可以使用前缀索引 IDX_ARTICLE_TITLE_PREFIX
-- LIKE '%关键词' 无法使用索引，建议改用反向存储或全文搜索

-- 8.2 完全模糊查询优化（建议使用全文索引）
-- LIKE '%关键词%' 无法使用普通索引
-- 解决方案:
-- 1. 使用已有的全文索引 FT_ARTICLE_TITLE_CONTENT
-- 2. 查询语句: 
--    SELECT * FROM BMS_ARTICLE 
--    WHERE MATCH(TITLE, CONTENT) AGAINST('关键词' IN NATURAL LANGUAGE MODE);

-- ============================================================
-- 9. 计数器并发更新问题解决方案
-- ============================================================

-- 方案1: 使用 MySQL 原子操作（推荐）
-- UPDATE BMS_ARTICLE SET VIEW_COUNT = VIEW_COUNT + 1 WHERE ID = 'xxx';

-- 方案2: 使用 Redis 缓存计数器（高并发场景推荐）
-- 1. Redis 存储实时计数: article:view:xxx = count
-- 2. 定时同步到数据库（每小时或每天）

-- 方案3: 使用乐观锁（适用于低并发场景）
-- UPDATE BMS_ARTICLE 
-- SET VIEW_COUNT = VIEW_COUNT + 1, VERSION = VERSION + 1 
-- WHERE ID = 'xxx' AND VERSION = old_version;

-- ============================================================
-- 10. 大字段查询优化建议
-- ============================================================

-- 已在Service层优化: queryWrapper.select() 指定查询字段
-- 避免 SELECT * 查询大字段 CONTENT

-- 示例（已在代码中实现）:
-- queryWrapper.select("ID", "TITLE", "SUMMARY", "COVER_IMAGE", 
--     "CATEGORY_ID", "AUTHOR_ID", "STATUS", "VIEW_COUNT", 
--     "LIKE_COUNT", "COMMENT_COUNT", "IS_TOP", "IS_RECOMMEND", 
--     "PUBLISH_TIME", "CREATE_TIME");

-- ============================================================
-- 11. 索引验证脚本
-- ============================================================

-- 验证文章列表查询是否使用索引
-- EXPLAIN SELECT ID, TITLE, STATUS FROM BMS_ARTICLE 
-- WHERE STATUS = 'PUBLISHED' AND DELETE_FLAG = 'NOT_DELETE' 
-- ORDER BY PUBLISH_TIME DESC LIMIT 10;

-- 验证分类文章查询是否使用索引
-- EXPLAIN SELECT ID, TITLE FROM BMS_ARTICLE 
-- WHERE CATEGORY_ID = '1800000000000000001' AND STATUS = 'PUBLISHED' 
-- AND DELETE_FLAG = 'NOT_DELETE' ORDER BY PUBLISH_TIME DESC;

-- 验证文章评论查询是否使用索引
-- EXPLAIN SELECT * FROM BMS_COMMENT 
-- WHERE ARTICLE_ID = 'xxx' AND STATUS = 'APPROVED' 
-- AND DELETE_FLAG = 'NOT_DELETE' ORDER BY CREATE_TIME DESC;

-- 验证媒体类型查询是否使用索引
-- EXPLAIN SELECT * FROM BMS_MEDIA 
-- WHERE FILE_TYPE = 'IMAGE' AND STATUS = 'ENABLE' 
-- AND DELETE_FLAG = 'NOT_DELETE' ORDER BY CREATE_TIME DESC;

-- ============================================================
-- 12. 索引统计信息更新（执行后建议运行）
-- ============================================================

-- 更新表统计信息，确保优化器正确选择索引
-- ANALYZE TABLE BMS_ARTICLE;
-- ANALYZE TABLE BMS_COMMENT;
-- ANALYZE TABLE BMS_MEDIA;
-- ANALYZE TABLE BMS_CATEGORY;
-- ANALYZE TABLE BMS_TAG;
-- ANALYZE TABLE BMS_ARTICLE_TAG;

-- ============================================================
-- 完成
-- ============================================================

SELECT '索引优化脚本执行完成!' AS result;
SELECT '请执行 EXPLAIN 验证索引使用情况' AS suggestion;
SELECT '建议运行 ANALYZE TABLE 更新统计信息' AS suggestion;