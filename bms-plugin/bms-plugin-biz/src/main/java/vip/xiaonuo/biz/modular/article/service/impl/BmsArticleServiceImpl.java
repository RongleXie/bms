/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.biz.modular.article.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.biz.modular.article.entity.BmsArticle;
import vip.xiaonuo.biz.modular.article.enums.BmsArticleStatusEnum;
import vip.xiaonuo.biz.modular.article.mapper.BmsArticleMapper;
import vip.xiaonuo.biz.modular.article.param.BmsArticleAddParam;
import vip.xiaonuo.biz.modular.article.param.BmsArticleEditParam;
import vip.xiaonuo.biz.modular.article.param.BmsArticleIdParam;
import vip.xiaonuo.biz.modular.article.param.BmsArticlePageParam;
import vip.xiaonuo.biz.modular.article.service.BmsArticleService;
import vip.xiaonuo.biz.modular.article.version.service.BmsArticleVersionService;
import cn.dev33.satoken.stp.StpUtil;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;

import java.util.Date;
import java.util.List;

@Service
public class BmsArticleServiceImpl extends ServiceImpl<BmsArticleMapper, BmsArticle> implements BmsArticleService {

    @Resource
    private BmsArticleVersionService articleVersionService;

    @Override
    public Page<BmsArticle> page(BmsArticlePageParam bmsArticlePageParam) {
        QueryWrapper<BmsArticle> queryWrapper = new QueryWrapper<BmsArticle>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bmsArticlePageParam.getTitle())) {
            queryWrapper.lambda().like(BmsArticle::getTitle, bmsArticlePageParam.getTitle());
        }
        if(ObjectUtil.isNotEmpty(bmsArticlePageParam.getCategoryId())) {
            queryWrapper.lambda().eq(BmsArticle::getCategoryId, bmsArticlePageParam.getCategoryId());
        }
        if(ObjectUtil.isNotEmpty(bmsArticlePageParam.getStatus())) {
            queryWrapper.lambda().eq(BmsArticle::getStatus, bmsArticlePageParam.getStatus());
        }
        if(ObjectUtil.isNotEmpty(bmsArticlePageParam.getIsTop())) {
            queryWrapper.lambda().eq(BmsArticle::getIsTop, bmsArticlePageParam.getIsTop());
        }
        if(ObjectUtil.isNotEmpty(bmsArticlePageParam.getIsRecommend())) {
            queryWrapper.lambda().eq(BmsArticle::getIsRecommend, bmsArticlePageParam.getIsRecommend());
        }
        if(ObjectUtil.isNotEmpty(bmsArticlePageParam.getStartCreateTime()) && ObjectUtil.isNotEmpty(bmsArticlePageParam.getEndCreateTime())) {
            queryWrapper.lambda().between(BmsArticle::getCreateTime, bmsArticlePageParam.getStartCreateTime(), bmsArticlePageParam.getEndCreateTime());
        }
        if(ObjectUtil.isAllNotEmpty(bmsArticlePageParam.getSortField(), bmsArticlePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bmsArticlePageParam.getSortOrder());
            queryWrapper.orderBy(true, bmsArticlePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bmsArticlePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(BmsArticle::getIsTop).orderByDesc(BmsArticle::getPublishTime).orderByDesc(BmsArticle::getCreateTime);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<BmsArticle> list(BmsArticlePageParam bmsArticlePageParam) {
        QueryWrapper<BmsArticle> queryWrapper = new QueryWrapper<BmsArticle>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bmsArticlePageParam.getTitle())) {
            queryWrapper.lambda().like(BmsArticle::getTitle, bmsArticlePageParam.getTitle());
        }
        if(ObjectUtil.isNotEmpty(bmsArticlePageParam.getCategoryId())) {
            queryWrapper.lambda().eq(BmsArticle::getCategoryId, bmsArticlePageParam.getCategoryId());
        }
        if(ObjectUtil.isNotEmpty(bmsArticlePageParam.getStatus())) {
            queryWrapper.lambda().eq(BmsArticle::getStatus, bmsArticlePageParam.getStatus());
        }
        queryWrapper.lambda().orderByDesc(BmsArticle::getIsTop).orderByDesc(BmsArticle::getPublishTime).orderByDesc(BmsArticle::getCreateTime);
        return this.list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BmsArticleAddParam bmsArticleAddParam) {
        BmsArticle bmsArticle = BeanUtil.toBean(bmsArticleAddParam, BmsArticle.class);
        bmsArticle.setAuthorId(StpUtil.getLoginIdAsString());
        bmsArticle.setStatus(BmsArticleStatusEnum.DRAFT.getValue());
        bmsArticle.setViewCount(0);
        bmsArticle.setLikeCount(0);
        bmsArticle.setCommentCount(0);
        this.save(bmsArticle);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BmsArticleEditParam bmsArticleEditParam) {
        BmsArticle bmsArticle = this.queryEntity(bmsArticleEditParam.getId());
        
        articleVersionService.saveVersion(
            bmsArticle.getId(),
            bmsArticle.getTitle(),
            bmsArticle.getContent(),
            bmsArticle.getSummary(),
            "编辑前自动保存"
        );
        
        bmsArticle.setTitle(bmsArticleEditParam.getTitle());
        bmsArticle.setSummary(bmsArticleEditParam.getSummary());
        bmsArticle.setContent(bmsArticleEditParam.getContent());
        bmsArticle.setCoverImage(bmsArticleEditParam.getCoverImage());
        bmsArticle.setCategoryId(bmsArticleEditParam.getCategoryId());
        bmsArticle.setIsTop(bmsArticleEditParam.getIsTop());
        bmsArticle.setIsRecommend(bmsArticleEditParam.getIsRecommend());
        bmsArticle.setAllowComment(bmsArticleEditParam.getAllowComment());
        bmsArticle.setSeoKeywords(bmsArticleEditParam.getSeoKeywords());
        bmsArticle.setSeoDescription(bmsArticleEditParam.getSeoDescription());
        bmsArticle.setSortCode(bmsArticleEditParam.getSortCode());
        bmsArticle.setExtJson(bmsArticleEditParam.getExtJson());
        
        if (StrUtil.isNotBlank(bmsArticleEditParam.getStatus())) {
            bmsArticle.setStatus(bmsArticleEditParam.getStatus());
            if ("PUBLISHED".equals(bmsArticleEditParam.getStatus()) && bmsArticle.getPublishTime() == null) {
                bmsArticle.setPublishTime(new Date());
            }
        }
        
        if (StrUtil.isNotBlank(bmsArticleEditParam.getScheduledPublishTime())) {
            bmsArticle.setScheduledPublishTime(
                cn.hutool.core.date.DateUtil.parse(bmsArticleEditParam.getScheduledPublishTime())
            );
        } else if ("PUBLISHED".equals(bmsArticle.getStatus()) || "DRAFT".equals(bmsArticle.getStatus())) {
            bmsArticle.setScheduledPublishTime(null);
        }
        
        this.updateById(bmsArticle);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BmsArticleIdParam> bmsArticleIdParamList) {
        this.removeByIds(CollStreamUtil.toList(bmsArticleIdParamList, BmsArticleIdParam::getId));
    }

    @Override
    public BmsArticle detail(BmsArticleIdParam bmsArticleIdParam) {
        return this.queryEntity(bmsArticleIdParam.getId());
    }

    @Override
    public BmsArticle queryEntity(String id) {
        BmsArticle bmsArticle = this.getById(id);
        if(ObjectUtil.isEmpty(bmsArticle)) {
            throw new CommonException("文章不存在，id值为：{}", id);
        }
        return bmsArticle;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void publish(BmsArticleIdParam bmsArticleIdParam) {
        BmsArticle bmsArticle = this.queryEntity(bmsArticleIdParam.getId());
        if(BmsArticleStatusEnum.PUBLISHED.getValue().equals(bmsArticle.getStatus())) {
            throw new CommonException("文章已发布，无需重复发布");
        }
        this.update(new LambdaUpdateWrapper<BmsArticle>().eq(BmsArticle::getId,
                bmsArticleIdParam.getId())
                .set(BmsArticle::getStatus, BmsArticleStatusEnum.PUBLISHED.getValue())
                .set(BmsArticle::getPublishTime, new Date()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unpublish(BmsArticleIdParam bmsArticleIdParam) {
        BmsArticle bmsArticle = this.queryEntity(bmsArticleIdParam.getId());
        if(BmsArticleStatusEnum.DRAFT.getValue().equals(bmsArticle.getStatus())) {
            throw new CommonException("文章未发布，无需撤回");
        }
        this.update(new LambdaUpdateWrapper<BmsArticle>().eq(BmsArticle::getId,
                bmsArticleIdParam.getId())
                .set(BmsArticle::getStatus, BmsArticleStatusEnum.DRAFT.getValue())
                .set(BmsArticle::getPublishTime, null));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void scheduledPublish(BmsArticleIdParam bmsArticleIdParam, String scheduledTime) {
        BmsArticle bmsArticle = this.queryEntity(bmsArticleIdParam.getId());
        if(BmsArticleStatusEnum.PUBLISHED.getValue().equals(bmsArticle.getStatus())) {
            throw new CommonException("文章已发布，无法设置定时发布");
        }
        Date scheduledDate = cn.hutool.core.date.DateUtil.parse(scheduledTime);
        if(scheduledDate.before(new Date())) {
            throw new CommonException("计划发布时间不能早于当前时间");
        }
        this.update(new LambdaUpdateWrapper<BmsArticle>()
                .eq(BmsArticle::getId, bmsArticleIdParam.getId())
                .set(BmsArticle::getStatus, BmsArticleStatusEnum.SCHEDULED.getValue())
                .set(BmsArticle::getScheduledPublishTime, scheduledDate));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelScheduled(BmsArticleIdParam bmsArticleIdParam) {
        BmsArticle bmsArticle = this.queryEntity(bmsArticleIdParam.getId());
        if(!BmsArticleStatusEnum.SCHEDULED.getValue().equals(bmsArticle.getStatus())) {
            throw new CommonException("文章未设置定时发布");
        }
        this.update(new LambdaUpdateWrapper<BmsArticle>()
                .eq(BmsArticle::getId, bmsArticleIdParam.getId())
                .set(BmsArticle::getStatus, BmsArticleStatusEnum.DRAFT.getValue())
                .set(BmsArticle::getScheduledPublishTime, null));
    }

    @Override
    public Page<BmsArticle> scheduledList(Integer current, Integer size) {
        QueryWrapper<BmsArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(BmsArticle::getStatus, BmsArticleStatusEnum.SCHEDULED.getValue())
                .isNotNull(BmsArticle::getScheduledPublishTime)
                .orderByAsc(BmsArticle::getScheduledPublishTime);
        Page<BmsArticle> page = new Page<>(current, size);
        return this.page(page, queryWrapper);
    }
}