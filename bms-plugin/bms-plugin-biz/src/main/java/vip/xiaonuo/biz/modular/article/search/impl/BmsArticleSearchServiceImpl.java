package vip.xiaonuo.biz.modular.article.search.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.biz.modular.article.entity.BmsArticle;
import vip.xiaonuo.biz.modular.article.mapper.BmsArticleMapper;
import vip.xiaonuo.biz.modular.article.search.BmsArticleSearchService;

import java.util.ArrayList;
import java.util.List;

@Service
public class BmsArticleSearchServiceImpl implements BmsArticleSearchService {

    @Resource
    private BmsArticleMapper articleMapper;

    @Override
    public Page<BmsArticle> search(String keyword, Integer current, Integer size) {
        if (StrUtil.isBlank(keyword)) {
            return new Page<>(current, size);
        }

        LambdaQueryWrapper<BmsArticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BmsArticle::getStatus, "PUBLISHED")
                .and(w -> w.like(BmsArticle::getTitle, keyword)
                        .or().like(BmsArticle::getContent, keyword)
                        .or().like(BmsArticle::getSummary, keyword))
                .orderByDesc(BmsArticle::getPublishTime);

        return articleMapper.selectPage(new Page<>(current, size), queryWrapper);
    }

    @Override
    public Page<BmsArticle> searchAdvanced(String keyword, String categoryId, String status, Integer current, Integer size) {
        LambdaQueryWrapper<BmsArticle> queryWrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(w -> w.like(BmsArticle::getTitle, keyword)
                    .or().like(BmsArticle::getContent, keyword)
                    .or().like(BmsArticle::getSummary, keyword)
                    .or().like(BmsArticle::getSeoKeywords, keyword));
        }

        if (StrUtil.isNotBlank(categoryId)) {
            queryWrapper.eq(BmsArticle::getCategoryId, categoryId);
        }

        if (StrUtil.isNotBlank(status)) {
            queryWrapper.eq(BmsArticle::getStatus, status);
        }

        queryWrapper.orderByDesc(BmsArticle::getPublishTime);

        return articleMapper.selectPage(new Page<>(current, size), queryWrapper);
    }

    @Override
    public List<String> suggest(String prefix, Integer limit) {
        if (StrUtil.isBlank(prefix) || prefix.length() < 2) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<BmsArticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BmsArticle::getStatus, "PUBLISHED")
                .likeRight(BmsArticle::getTitle, prefix)
                .select(BmsArticle::getTitle)
                .last("LIMIT " + limit);

        List<BmsArticle> articles = articleMapper.selectList(queryWrapper);
        List<String> suggestions = new ArrayList<>();
        for (BmsArticle article : articles) {
            if (StrUtil.isNotBlank(article.getTitle())) {
                suggestions.add(article.getTitle());
            }
        }
        return suggestions;
    }

    @Override
    public void rebuildIndex() {
    }
}