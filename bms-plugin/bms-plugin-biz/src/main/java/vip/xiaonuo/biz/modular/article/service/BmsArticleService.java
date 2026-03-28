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
package vip.xiaonuo.biz.modular.article.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.biz.modular.article.entity.BmsArticle;
import vip.xiaonuo.biz.modular.article.param.BmsArticleAddParam;
import vip.xiaonuo.biz.modular.article.param.BmsArticleEditParam;
import vip.xiaonuo.biz.modular.article.param.BmsArticleIdParam;
import vip.xiaonuo.biz.modular.article.param.BmsArticlePageParam;

import java.util.List;

/**
 * 文章Service接口
 *
 * @author xiaonuo
 * @date 2026/03/24
 **/
public interface BmsArticleService extends IService<BmsArticle> {

    /**
     * 获取文章分页
     *
     * @author xiaonuo
     * @date 2026/03/24
     */
    Page<BmsArticle> page(BmsArticlePageParam bmsArticlePageParam);

    /**
     * 获取文章列表
     *
     * @author xiaonuo
     * @date 2026/03/24
     */
    List<BmsArticle> list(BmsArticlePageParam bmsArticlePageParam);

    /**
     * 添加文章
     *
     * @author xiaonuo
     * @date 2026/03/24
     */
    void add(BmsArticleAddParam bmsArticleAddParam);

    /**
     * 编辑文章
     *
     * @author xiaonuo
     * @date 2026/03/24
     */
    void edit(BmsArticleEditParam bmsArticleEditParam);

    /**
     * 删除文章
     *
     * @author xiaonuo
     * @date 2026/03/24
     */
    void delete(List<BmsArticleIdParam> bmsArticleIdParamList);

    /**
     * 获取文章详情
     *
     * @author xiaonuo
     * @date 2026/03/24
     */
    BmsArticle detail(BmsArticleIdParam bmsArticleIdParam);

    /**
     * 获取文章详情
     *
     * @author xiaonuo
     * @date 2026/03/24
     **/
    BmsArticle queryEntity(String id);

    /**
     * 发布文章
     *
     * @author xiaonuo
     * @date 2026/03/24
     */
    void publish(BmsArticleIdParam bmsArticleIdParam);

    /**
     * 撤回文章
     *
     * @author xiaonuo
     * @date 2026/03/24
     */
    void unpublish(BmsArticleIdParam bmsArticleIdParam);

    /**
     * 定时发布文章
     *
     * @param bmsArticleIdParam 文章ID参数
     * @param scheduledTime 计划发布时间
     * @author xiaonuo
     * @date 2026/03/26
     */
    void scheduledPublish(BmsArticleIdParam bmsArticleIdParam, String scheduledTime);

    /**
     * 取消定时发布
     *
     * @param bmsArticleIdParam 文章ID参数
     * @author xiaonuo
     * @date 2026/03/26
     */
    void cancelScheduled(BmsArticleIdParam bmsArticleIdParam);

    /**
     * 获取待发布文章列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     * @author xiaonuo
     * @date 2026/03/26
     */
    Page<BmsArticle> scheduledList(Integer current, Integer size);
}