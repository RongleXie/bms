-- ============================================================
-- 博客管理系统 (BMS) - 全文搜索索引优化
-- 版本: v2.0.0
-- 日期: 2026-03-25
-- 说明: 使用 MySQL FULLTEXT 索引实现全文搜索
-- ============================================================

-- 1. 添加全文索引
ALTER TABLE `BMS_ARTICLE` ADD FULLTEXT INDEX `FT_ARTICLE_TITLE_CONTENT` (`TITLE`, `CONTENT`) WITH PARSER ngram;

-- 2. 添加摘要全文索引（可选）
ALTER TABLE `BMS_ARTICLE` ADD FULLTEXT INDEX `FT_ARTICLE_SUMMARY` (`SUMMARY`) WITH PARSER ngram;

-- 3. 使用示例
-- 自然语言搜索
-- SELECT * FROM BMS_ARTICLE WHERE MATCH(TITLE, CONTENT) AGAINST('搜索关键词' IN NATURAL LANGUAGE MODE);

-- 布尔模式搜索（支持 +必须包含 -排除 *通配符）
-- SELECT * FROM BMS_ARTICLE WHERE MATCH(TITLE, CONTENT) AGAINST('+Java +Spring' IN BOOLEAN MODE);

-- 查询扩展搜索
-- SELECT * FROM BMS_ARTICLE WHERE MATCH(TITLE, CONTENT) AGAINST('博客' WITH QUERY EXPANSION);