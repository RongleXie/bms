-- ============================================================
-- 博客管理系统 (BMS) - 计数器同步定时任务配置
-- 版本: v2.0.0
-- 日期: 2026-03-31
-- 说明: 配置Redis计数器同步到数据库的定时任务
-- ============================================================

INSERT INTO `dev_job` (
    `ID`, 
    `NAME`, 
    `CODE`, 
    `CATEGORY`, 
    `ACTION_CLASS`, 
    `CRON_EXPRESSION`, 
    `JOB_STATUS`, 
    `SORT_CODE`, 
    `DELETE_FLAG`
) VALUES (
    'BMS_COUNTER_SYNC_TIMER_001',
    'BMS文章计数器同步任务',
    'bms_counter_sync',
    'BMS',
    'vip.xiaonuo.biz.modular.article.task.BmsArticleCounterSyncTimerTaskRunner',
    '0 0/5 * * * ?',
    'RUNNING',
    100,
    'NOT_DELETE'
);

SELECT '计数器同步定时任务配置完成' AS RESULT;