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
package vip.xiaonuo.biz.modular.media.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 媒体文件实体
 *
 * @author yubaoshan
 * @date  2024/07/11 14:46
 **/
@Getter
@Setter
@TableName("BMS_MEDIA")
public class BmsMedia extends CommonEntity {

    /** 主键ID */
    @TableId
    @Schema(description = "主键ID")
    private String id;

    /** 文件名称 */
    @Schema(description = "文件名称")
    private String fileName;

    /** 原始文件名 */
    @Schema(description = "原始文件名")
    private String originalName;

    /** 文件路径 */
    @Schema(description = "文件路径")
    private String filePath;

    /** 文件访问URL */
    @Schema(description = "文件访问URL")
    private String fileUrl;

    /** 文件大小(字节) */
    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    /** 文件类型 */
    @Schema(description = "文件类型 (IMAGE/VIDEO/AUDIO/DOCUMENT/OTHER)")
    private String fileType;

    /** MIME类型 */
    @Schema(description = "MIME类型")
    private String mimeType;

    /** 文件扩展名 */
    @Schema(description = "文件扩展名")
    private String fileExt;

    /** 缩略图URL */
    @Schema(description = "缩略图URL")
    private String thumbnailUrl;

    /** 图片宽度 */
    @Schema(description = "图片宽度")
    private Integer width;

    /** 图片高度 */
    @Schema(description = "图片高度")
    private Integer height;

    /** 音视频时长(秒) */
    @Schema(description = "音视频时长(秒)")
    private Integer duration;

    /** 上传用户ID */
    @Schema(description = "上传用户ID")
    private String uploadUser;

    /** 下载次数 */
    @Schema(description = "下载次数")
    private Integer downloadCount;

    /** 状态 */
    @Schema(description = "状态 (ENABLE-启用, DISABLED-禁用)")
    private String status;

    /** 扩展信息JSON */
    @Schema(description = "扩展信息JSON")
    private String extJson;
}