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
package vip.xiaonuo.biz.modular.tag.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 标签编辑参数
 *
 * @author xiaonuo
 * @date 2024/01/01
 **/
@Getter
@Setter
public class BmsTagEditParam {

    /** 主键ID */
    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 标签名称 */
    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "name不能为空")
    @Size(max = 50, message = "标签名称长度不能超过50字符")
    private String name;

    /** 标签编码 */
    @Schema(description = "标签编码")
    @Size(max = 50, message = "标签编码长度不能超过50字符")
    private String code;

    /** 标签颜色 */
    @Schema(description = "标签颜色")
    @Size(max = 20, message = "标签颜色长度不能超过20字符")
    private String color;

    /** 标签描述 */
    @Schema(description = "标签描述")
    @Size(max = 500, message = "标签描述长度不能超过500字符")
    private String description;

    /** 排序码 */
    @Schema(description = "排序码")
    @Min(value = 0, message = "排序码不能小于0")
    @Max(value = 99999, message = "排序码不能大于99999")
    private Integer sortCode;
}