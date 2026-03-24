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
package vip.xiaonuo.biz.modular.comment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.biz.modular.comment.entity.BmsComment;
import vip.xiaonuo.biz.modular.comment.param.BmsCommentAddParam;
import vip.xiaonuo.biz.modular.comment.param.BmsCommentIdParam;
import vip.xiaonuo.biz.modular.comment.param.BmsCommentPageParam;

import java.util.List;

/**
 * 评论Service接口
 *
 * @author xiaonuo
 * @date 2026/03/24
 **/
public interface BmsCommentService extends IService<BmsComment> {

    /**
     * 获取评论分页
     */
    Page<BmsComment> page(BmsCommentPageParam bmsCommentPageParam);

    /**
     * 获取评论列表
     */
    List<BmsComment> list(BmsCommentPageParam bmsCommentPageParam);

    /**
     * 添加评论
     */
    void add(BmsCommentAddParam bmsCommentAddParam);

    /**
     * 删除评论
     */
    void delete(List<BmsCommentIdParam> bmsCommentIdParamList);

    /**
     * 获取评论详情
     */
    BmsComment detail(BmsCommentIdParam bmsCommentIdParam);

    /**
     * 获取评论详情
     */
    BmsComment queryEntity(String id);

    /**
     * 审核通过
     */
    void approve(BmsCommentIdParam bmsCommentIdParam);

    /**
     * 审核拒绝
     */
    void reject(BmsCommentIdParam bmsCommentIdParam);
}