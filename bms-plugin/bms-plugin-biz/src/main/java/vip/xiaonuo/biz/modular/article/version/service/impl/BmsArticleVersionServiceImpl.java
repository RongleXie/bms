package vip.xiaonuo.biz.modular.article.version.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.biz.modular.article.entity.BmsArticle;
import vip.xiaonuo.biz.modular.article.mapper.BmsArticleMapper;
import vip.xiaonuo.biz.modular.article.version.entity.BmsArticleVersion;
import vip.xiaonuo.biz.modular.article.version.mapper.BmsArticleVersionMapper;
import vip.xiaonuo.biz.modular.article.version.service.BmsArticleVersionService;
import vip.xiaonuo.common.exception.CommonException;

import java.util.List;

@Service
public class BmsArticleVersionServiceImpl extends ServiceImpl<BmsArticleVersionMapper, BmsArticleVersion> implements BmsArticleVersionService {

    @Resource
    private BmsArticleMapper articleMapper;

    @Override
    public Page<BmsArticleVersion> page(String articleId, Integer current, Integer size) {
        LambdaQueryWrapper<BmsArticleVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BmsArticleVersion::getArticleId, articleId)
                .orderByDesc(BmsArticleVersion::getVersionNumber);
        return this.page(new Page<>(current, size), queryWrapper);
    }

    @Override
    public List<BmsArticleVersion> list(String articleId) {
        LambdaQueryWrapper<BmsArticleVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BmsArticleVersion::getArticleId, articleId)
                .orderByDesc(BmsArticleVersion::getVersionNumber);
        return this.list(queryWrapper);
    }

    @Override
    public BmsArticleVersion detail(String id) {
        BmsArticleVersion version = this.getById(id);
        if (ObjectUtil.isEmpty(version)) {
            throw new CommonException("版本不存在，id值为：{}", id);
        }
        return version;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVersion(String articleId, String title, String content, String summary, String changeSummary) {
        BmsArticleVersion latestVersion = this.getLatestVersion(articleId);
        int nextVersionNumber = (latestVersion == null) ? 1 : latestVersion.getVersionNumber() + 1;

        BmsArticleVersion version = new BmsArticleVersion();
        version.setId(IdUtil.fastSimpleUUID());
        version.setArticleId(articleId);
        version.setVersionNumber(nextVersionNumber);
        version.setTitle(title);
        version.setContent(content);
        version.setSummary(summary);
        version.setChangeSummary(changeSummary);
        version.setStatus("ACTIVE");

        this.save(version);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollback(String articleId, Integer versionNumber) {
        LambdaQueryWrapper<BmsArticleVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BmsArticleVersion::getArticleId, articleId)
                .eq(BmsArticleVersion::getVersionNumber, versionNumber);
        BmsArticleVersion version = this.getOne(queryWrapper);

        if (ObjectUtil.isEmpty(version)) {
            throw new CommonException("版本不存在，版本号：{}", versionNumber);
        }

        BmsArticle article = articleMapper.selectById(articleId);
        if (ObjectUtil.isEmpty(article)) {
            throw new CommonException("文章不存在，id值为：{}", articleId);
        }

        this.saveVersion(articleId, article.getTitle(), article.getContent(), article.getSummary(), "回滚前自动保存");

        article.setTitle(version.getTitle());
        article.setContent(version.getContent());
        article.setSummary(version.getSummary());
        articleMapper.updateById(article);
    }

    @Override
    public BmsArticleVersion getLatestVersion(String articleId) {
        LambdaQueryWrapper<BmsArticleVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BmsArticleVersion::getArticleId, articleId)
                .orderByDesc(BmsArticleVersion::getVersionNumber)
                .last("LIMIT 1");
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByArticleId(String articleId) {
        LambdaQueryWrapper<BmsArticleVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BmsArticleVersion::getArticleId, articleId);
        this.remove(queryWrapper);
    }
}