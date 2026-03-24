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
package vip.xiaonuo.biz.modular.article.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

import java.util.Date;

/**
 * 文章实体
 *
 * @author xiaonuo
 * @date 2026/03/24
 **/
@Getter
@Setter
@TableName("BMS_ARTICLE")
public class BmsArticle extends CommonEntity {

    /** 主键 */
    @TableId
    @Schema(description = "主键")
    private String id;

    /** 文章标题 */
    @Schema(description = "文章标题")
    private String title;

    /** 文章摘要 */
    @Schema(description = "文章摘要")
    private String summary;

    /** 文章内容 */
    @Schema(description = "文章内容")
    private String content;

    /** 封面图片URL */
    @Schema(description = "封面图片URL")
    private String coverImage;

    /** 分类ID */
    @Schema(description = "分类ID")
    private String categoryId;

    /** 作者ID */
    @Schema(description = "作者ID")
    private String authorId;

    /** 状态 (DRAFT-草稿, PUBLISHED-已发布) */
    @Schema(description = "状态 (DRAFT-草稿, PUBLISHED-已发布)")
    private String status;

    /** 阅读量 */
    @Schema(description = "阅读量")
    private Integer viewCount;

    /** 点赞数 */
    @Schema(description = "点赞数")
    private Integer likeCount;

    /** 评论数 */
    @Schema(description = "评论数")
    private Integer commentCount;

    /** 是否置顶 (0-否, 1-是) */
    @Schema(description = "是否置顶 (0-否, 1-是)")
    private Integer isTop;

    /** 是否推荐 (0-否, 1-是) */
    @Schema(description = "是否推荐 (0-否, 1-是)")
    private Integer isRecommend;

    /** 允许评论 (0-否, 1-是) */
    @Schema(description = "允许评论 (0-否, 1-是)")
    private Integer allowComment;

    /** 发布时间 */
    @Schema(description = "发布时间")
    private Date publishTime;

    /** SEO关键词 */
    @Schema(description = "SEO关键词")
    private String seoKeywords;

    /** SEO描述 */
    @Schema(description = "SEO描述")
    private String seoDescription;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;
}