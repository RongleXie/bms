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
package vip.xiaonuo.biz.modular.comment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@TableName("BMS_COMMENT")
public class BmsComment implements Serializable {

    @TableId
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "文章ID")
    private String articleId;

    @Schema(description = "父评论ID (0为顶级评论)")
    private String parentId;

    @Schema(description = "回复用户ID")
    private String replyUserId;

    @Schema(description = "回复用户昵称")
    private String replyUserName;

    @Schema(description = "评论者昵称")
    private String nickname;

    @Schema(description = "评论者邮箱")
    private String email;

    @Schema(description = "评论者网站")
    private String website;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "状态 (PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝)")
    private String status;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "用户代理")
    private String userAgent;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "扩展信息JSON")
    private String extJson;

    @JsonIgnore
    @TableLogic
    @Schema(description = "删除标志")
    @TableField(fill = FieldFill.INSERT)
    private String deleteFlag;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
}