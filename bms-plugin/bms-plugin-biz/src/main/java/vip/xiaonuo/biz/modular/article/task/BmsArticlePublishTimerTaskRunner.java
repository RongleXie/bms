package vip.xiaonuo.biz.modular.article.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.xiaonuo.biz.modular.article.entity.BmsArticle;
import vip.xiaonuo.biz.modular.article.mapper.BmsArticleMapper;
import vip.xiaonuo.common.timer.CommonTimerTaskRunner;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class BmsArticlePublishTimerTaskRunner implements CommonTimerTaskRunner {

    @Resource
    private BmsArticleMapper articleMapper;

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_PUBLISHED = "PUBLISHED";
    private static final String STATUS_SCHEDULED = "SCHEDULED";

    @Override
    public void action(String extJson) {
        Date now = DateUtil.date();
        
        LambdaQueryWrapper<BmsArticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BmsArticle::getStatus, STATUS_SCHEDULED)
                .isNotNull(BmsArticle::getScheduledPublishTime)
                .le(BmsArticle::getScheduledPublishTime, now);
        
        List<BmsArticle> articles = articleMapper.selectList(queryWrapper);
        
        if (CollectionUtil.isEmpty(articles)) {
            return;
        }
        
        for (BmsArticle article : articles) {
            try {
                article.setStatus(STATUS_PUBLISHED);
                article.setPublishTime(now);
                article.setScheduledPublishTime(null);
                articleMapper.updateById(article);
                log.info("文章定时发布成功: id={}, title={}", article.getId(), article.getTitle());
            } catch (Exception e) {
                log.error("文章定时发布失败: id={}, error={}", article.getId(), e.getMessage());
            }
        }
    }
}