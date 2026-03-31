package vip.xiaonuo.biz.modular.article.task;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.xiaonuo.biz.modular.article.service.BmsArticleService;
import vip.xiaonuo.common.timer.CommonTimerTaskRunner;

@Slf4j
@Component
public class BmsArticleCounterSyncTimerTaskRunner implements CommonTimerTaskRunner {

    @Resource
    private BmsArticleService articleService;

    @Override
    public void action(String extJson) {
        try {
            articleService.syncCountersToDatabase();
            log.info("文章计数器同步完成");
        } catch (Exception e) {
            log.error("文章计数器同步失败: {}", e.getMessage());
        }
    }
}