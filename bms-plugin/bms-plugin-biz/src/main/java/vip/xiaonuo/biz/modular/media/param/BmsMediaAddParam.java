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
package vip.xiaonuo.biz.modular.media.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 媒体文件添加参数
 *
 * @author yubaoshan
 * @date  2024/07/11 14:46
 **/
@Getter
@Setter
public class BmsMediaAddParam {

    /** 文件名称 */
    @Schema(description = "文件名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "fileName不能为空")
    @Size(max = 200, message = "文件名长度不能超过200字符")
    private String fileName;

    /** 原始文件名 */
    @Schema(description = "原始文件名")
    @Size(max = 200, message = "原始文件名长度不能超过200字符")
    private String originalName;

    /** 文件路径 */
    @Schema(description = "文件路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "filePath不能为空")
    @Size(max = 500, message = "文件路径长度不能超过500字符")
    private String filePath;

    /** 文件访问URL */
    @Schema(description = "文件访问URL")
    @Size(max = 500, message = "文件URL长度不能超过500字符")
    private String fileUrl;

    /** 文件大小(字节) */
    @Schema(description = "文件大小(字节)")
    @Min(value = 0, message = "文件大小不能小于0")
    private Long fileSize;

    /** 文件类型 */
    @Schema(description = "文件类型 (IMAGE/VIDEO/AUDIO/DOCUMENT/OTHER)")
    @Size(max = 20, message = "文件类型长度不能超过20字符")
    private String fileType;

    /** MIME类型 */
    @Schema(description = "MIME类型")
    @Size(max = 100, message = "MIME类型长度不能超过100字符")
    private String mimeType;

    /** 文件扩展名 */
    @Schema(description = "文件扩展名")
    @Size(max = 10, message = "文件扩展名长度不能超过10字符")
    private String fileExt;

    /** 缩略图URL */
    @Schema(description = "缩略图URL")
    @Size(max = 500, message = "缩略图URL长度不能超过500字符")
    private String thumbnailUrl;

    /** 图片宽度 */
    @Schema(description = "图片宽度")
    @Min(value = 0, message = "图片宽度不能小于0")
    @Max(value = 99999, message = "图片宽度不能超过99999")
    private Integer width;

    /** 图片高度 */
    @Schema(description = "图片高度")
    @Min(value = 0, message = "图片高度不能小于0")
    @Max(value = 99999, message = "图片高度不能超过99999")
    private Integer height;

    /** 音视频时长(秒) */
    @Schema(description = "音视频时长(秒)")
    @Min(value = 0, message = "时长不能小于0")
    private Integer duration;

    /** 上传用户ID */
    @Schema(description = "上传用户ID")
    @Size(max = 32, message = "上传用户ID长度不能超过32字符")
    private String uploadUser;

    /** 扩展信息JSON */
    @Schema(description = "扩展信息JSON")
    @Size(max = 2000, message = "扩展信息JSON长度不能超过2000字符")
    private String extJson;
}