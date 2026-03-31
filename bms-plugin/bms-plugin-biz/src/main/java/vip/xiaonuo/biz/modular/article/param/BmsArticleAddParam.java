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
package vip.xiaonuo.biz.modular.article.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import vip.xiaonuo.common.util.CommonXssUtil;

/**
 * 文章新增参数
 *
 * @author xiaonuo
 * @date 2026/03/24
 **/
@Getter
public class BmsArticleAddParam {

    @Schema(description = "文章标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "title不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;

    @Schema(description = "文章摘要")
    @Size(max = 500, message = "摘要长度不能超过500字符")
    private String summary;

    @Schema(description = "文章内容")
    @Size(max = 100000, message = "内容长度不能超过100000字符")
    private String content;

    @Schema(description = "封面图片URL")
    @Size(max = 500, message = "封面图片URL长度不能超过500字符")
    private String coverImage;

    @Schema(description = "分类ID")
    private String categoryId;

    @Schema(description = "是否置顶 (0-否, 1-是)")
    @Min(value = 0, message = "是否置顶值必须为0或1")
    @Max(value = 1, message = "是否置顶值必须为0或1")
    private Integer isTop;

    @Schema(description = "是否推荐 (0-否, 1-是)")
    @Min(value = 0, message = "是否推荐值必须为0或1")
    @Max(value = 1, message = "是否推荐值必须为0或1")
    private Integer isRecommend;

    @Schema(description = "允许评论 (0-否, 1-是)")
    @Min(value = 0, message = "允许评论值必须为0或1")
    @Max(value = 1, message = "允许评论值必须为0或1")
    private Integer allowComment;

    @Schema(description = "SEO关键词")
    @Size(max = 200, message = "SEO关键词长度不能超过200字符")
    private String seoKeywords;

    @Schema(description = "SEO描述")
    @Size(max = 500, message = "SEO描述长度不能超过500字符")
    private String seoDescription;

    @Schema(description = "排序码")
    @Min(value = 0, message = "排序码不能小于0")
    @Max(value = 999999, message = "排序码不能大于999999")
    private Integer sortCode;

    @Schema(description = "扩展信息")
    private String extJson;

    @Schema(description = "标签ID列表，逗号分隔")
    private String tagIds;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "来源")
    private String source;

    // XSS过滤的setter方法
    public void setTitle(String title) {
        this.title = CommonXssUtil.filterPlainText(title);
    }

    public void setSummary(String summary) {
        this.summary = CommonXssUtil.filterPlainText(summary);
    }

    public void setContent(String content) {
        this.content = CommonXssUtil.filterMarkdown(content);
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = CommonXssUtil.filterUrl(coverImage);
    }

    public void setSeoKeywords(String seoKeywords) {
        this.seoKeywords = CommonXssUtil.filterPlainText(seoKeywords);
    }

    public void setSeoDescription(String seoDescription) {
        this.seoDescription = CommonXssUtil.filterPlainText(seoDescription);
    }

    // 其他字段的普通setter
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    public void setIsRecommend(Integer isRecommend) {
        this.isRecommend = isRecommend;
    }

    public void setAllowComment(Integer allowComment) {
        this.allowComment = allowComment;
    }

    public void setSortCode(Integer sortCode) {
        this.sortCode = sortCode;
    }

    public void setExtJson(String extJson) {
        this.extJson = extJson;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }

    public void setAuthor(String author) {
        this.author = CommonXssUtil.filterPlainText(author);
    }

    public void setSource(String source) {
        this.source = CommonXssUtil.filterPlainText(source);
    }
}