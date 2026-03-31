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
package vip.xiaonuo.biz.modular.comment.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import vip.xiaonuo.common.util.CommonXssUtil;

@Getter
public class BmsCommentAddParam {

    @Schema(description = "文章ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "articleId不能为空")
    @Size(max = 50, message = "文章ID长度不能超过50字符")
    private String articleId;

    @Schema(description = "父评论ID")
    private String parentId;

    @Schema(description = "回复用户ID")
    private String replyUserId;

    @Schema(description = "回复用户昵称")
    private String replyUserName;

    @Schema(description = "评论者昵称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "nickname不能为空")
    @Size(max = 50, message = "昵称长度不能超过50字符")
    private String nickname;

    @Schema(description = "评论者邮箱")
    @Size(max = 100, message = "邮箱长度不能超过100字符")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "评论者网站")
    @Size(max = 200, message = "网站URL长度不能超过200字符")
    private String website;

    @Schema(description = "评论内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "content不能为空")
    @Size(max = 2000, message = "评论内容长度不能超过2000字符")
    private String content;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "用户代理")
    private String userAgent;

    // XSS过滤的setter方法
    public void setNickname(String nickname) {
        this.nickname = CommonXssUtil.filterNickname(nickname);
    }

    public void setEmail(String email) {
        this.email = CommonXssUtil.filterEmail(email);
    }

    public void setWebsite(String website) {
        this.website = CommonXssUtil.filterWebsite(website);
    }

    public void setContent(String content) {
        this.content = CommonXssUtil.filterPlainText(content);
    }

    public void setReplyUserName(String replyUserName) {
        this.replyUserName = CommonXssUtil.filterNickname(replyUserName);
    }

    // 其他字段的普通setter
    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setReplyUserId(String replyUserId) {
        this.replyUserId = replyUserId;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}