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
package vip.xiaonuo.biz.modular.category.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

import java.util.List;

@Getter
@Setter
@TableName("BMS_CATEGORY")
public class BmsCategory extends CommonEntity {

    @TableId
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "父分类ID")
    @TableField(insertStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.ALWAYS, updateStrategy = com.baomidou.mybatisplus.annotation.FieldStrategy.ALWAYS)
    private String parentId;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类编码")
    private String code;

    @Schema(description = "分类描述")
    private String description;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "排序码")
    private Integer sortCode;

    @Schema(description = "状态 (ENABLE-启用, DISABLED-禁用)")
    private String status;

    @Schema(description = "扩展信息JSON")
    private String extJson;

    @Schema(description = "子分类列表")
    @TableField(exist = false)
    private List<BmsCategory> children;
}