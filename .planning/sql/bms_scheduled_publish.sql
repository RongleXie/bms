-- ============================================================
-- 博客管理系统 (BMS) - 定时发布功能字段扩展
-- 版本: v2.0.0
-- 日期: 2026-03-25
-- 说明: 添加计划发布时间字段，支持文章定时发布
-- ============================================================

-- 1. 添加计划发布时间字段
ALTER TABLE `BMS_ARTICLE` 
ADD COLUMN `SCHEDULED_PUBLISH_TIME` DATETIME DEFAULT NULL COMMENT '计划发布时间（用于定时发布）' AFTER `PUBLISH_TIME`;

-- 2. 添加索引优化查询性能
CREATE INDEX `IDX_ARTICLE_SCHEDULED_PUBLISH` ON `BMS_ARTICLE` (`STATUS`, `SCHEDULED_PUBLISH_TIME`);

-- 3. 更新文章状态说明
-- DRAFT: 草稿
-- PUBLISHED: 已发布
-- SCHEDULED: 待发布（定时发布中）
-- WITHDRAWN: 已撤回