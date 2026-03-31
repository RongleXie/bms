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
package vip.xiaonuo.biz.modular.comment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.biz.modular.comment.entity.BmsComment;
import vip.xiaonuo.biz.modular.comment.enums.BmsCommentStatusEnum;
import vip.xiaonuo.biz.modular.comment.mapper.BmsCommentMapper;
import vip.xiaonuo.biz.modular.comment.param.BmsCommentAddParam;
import vip.xiaonuo.biz.modular.comment.param.BmsCommentIdParam;
import vip.xiaonuo.biz.modular.comment.param.BmsCommentPageParam;
import vip.xiaonuo.biz.modular.comment.service.BmsCommentService;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.common.util.CommonSqlUtil;

import java.util.Date;
import java.util.List;

/**
 * 评论Service接口实现类
 *
 * @author xiaonuo
 * @date 2026/03/24
 **/
@Service
public class BmsCommentServiceImpl extends ServiceImpl<BmsCommentMapper, BmsComment> implements BmsCommentService {

    @Override
    public Page<BmsComment> page(BmsCommentPageParam bmsCommentPageParam) {
        QueryWrapper<BmsComment> queryWrapper = new QueryWrapper<BmsComment>().checkSqlInjection();
        queryWrapper.select("ID", "ARTICLE_ID", "PARENT_ID", "NICKNAME", "CONTENT", "STATUS", "LIKE_COUNT", "CREATE_TIME", "UPDATE_TIME");
        if(ObjectUtil.isNotEmpty(bmsCommentPageParam.getSearchKey())) {
            queryWrapper.lambda().like(BmsComment::getContent, bmsCommentPageParam.getSearchKey());
        }
        if(ObjectUtil.isNotEmpty(bmsCommentPageParam.getArticleId())) {
            queryWrapper.lambda().eq(BmsComment::getArticleId, bmsCommentPageParam.getArticleId());
        }
        if(ObjectUtil.isNotEmpty(bmsCommentPageParam.getStatus())) {
            queryWrapper.lambda().eq(BmsComment::getStatus, bmsCommentPageParam.getStatus());
        }
        if(ObjectUtil.isNotEmpty(bmsCommentPageParam.getStartCreateTime()) && ObjectUtil.isNotEmpty(bmsCommentPageParam.getEndCreateTime())) {
            queryWrapper.lambda().between(BmsComment::getCreateTime, bmsCommentPageParam.getStartCreateTime(), bmsCommentPageParam.getEndCreateTime());
        }
        if(ObjectUtil.isAllNotEmpty(bmsCommentPageParam.getSortField(), bmsCommentPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bmsCommentPageParam.getSortOrder());
            CommonSqlUtil.validateSortField(bmsCommentPageParam.getSortField());
            queryWrapper.orderBy(true, bmsCommentPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bmsCommentPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(BmsComment::getCreateTime);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<BmsComment> list(BmsCommentPageParam bmsCommentPageParam) {
        QueryWrapper<BmsComment> queryWrapper = new QueryWrapper<BmsComment>().checkSqlInjection();
        queryWrapper.select("ID", "ARTICLE_ID", "PARENT_ID", "NICKNAME", "CONTENT", "STATUS", "LIKE_COUNT", "CREATE_TIME");
        if(ObjectUtil.isNotEmpty(bmsCommentPageParam.getArticleId())) {
            queryWrapper.lambda().eq(BmsComment::getArticleId, bmsCommentPageParam.getArticleId());
        }
        if(ObjectUtil.isNotEmpty(bmsCommentPageParam.getStatus())) {
            queryWrapper.lambda().eq(BmsComment::getStatus, bmsCommentPageParam.getStatus());
        }
        queryWrapper.lambda().orderByDesc(BmsComment::getCreateTime);
        queryWrapper.last("LIMIT 1000");
        return this.list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BmsCommentAddParam bmsCommentAddParam) {
        BmsComment bmsComment = BeanUtil.toBean(bmsCommentAddParam, BmsComment.class);
        bmsComment.setParentId(ObjectUtil.isEmpty(bmsCommentAddParam.getParentId()) ? "0" : bmsCommentAddParam.getParentId());
        bmsComment.setStatus(BmsCommentStatusEnum.PENDING.getValue());
        bmsComment.setLikeCount(0);
        this.save(bmsComment);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BmsCommentIdParam> bmsCommentIdParamList) {
        this.removeByIds(CollStreamUtil.toList(bmsCommentIdParamList, BmsCommentIdParam::getId));
    }

    @Override
    public BmsComment detail(BmsCommentIdParam bmsCommentIdParam) {
        return this.queryEntity(bmsCommentIdParam.getId());
    }

    @Override
    public BmsComment queryEntity(String id) {
        BmsComment bmsComment = this.getById(id);
        if(ObjectUtil.isEmpty(bmsComment)) {
            throw new CommonException("评论不存在，id值为：{}", id);
        }
        return bmsComment;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approve(BmsCommentIdParam bmsCommentIdParam) {
        BmsComment bmsComment = this.queryEntity(bmsCommentIdParam.getId());
        if(BmsCommentStatusEnum.APPROVED.getValue().equals(bmsComment.getStatus())) {
            throw new CommonException("评论已审核通过，无需重复操作");
        }
        this.update(new LambdaUpdateWrapper<BmsComment>().eq(BmsComment::getId,
                bmsCommentIdParam.getId())
                .set(BmsComment::getStatus, BmsCommentStatusEnum.APPROVED.getValue()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reject(BmsCommentIdParam bmsCommentIdParam) {
        BmsComment bmsComment = this.queryEntity(bmsCommentIdParam.getId());
        if(BmsCommentStatusEnum.REJECTED.getValue().equals(bmsComment.getStatus())) {
            throw new CommonException("评论已拒绝，无需重复操作");
        }
        this.update(new LambdaUpdateWrapper<BmsComment>().eq(BmsComment::getId,
                bmsCommentIdParam.getId())
                .set(BmsComment::getStatus, BmsCommentStatusEnum.REJECTED.getValue()));
    }
}