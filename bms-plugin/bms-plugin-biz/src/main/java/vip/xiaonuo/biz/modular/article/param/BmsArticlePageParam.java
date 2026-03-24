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
import lombok.Getter;
import lombok.Setter;

/**
 * 文章分页查询参数
 *
 * @author xiaonuo
 * @date 2026/03/24
 **/
@Getter
@Setter
public class BmsArticlePageParam {

    @Schema(description = "当前页码")
    private Integer current;

    @Schema(description = "每页条数")
    private Integer size;

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "排序方式")
    private String sortOrder;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "分类ID")
    private String categoryId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "是否置顶")
    private Integer isTop;

    @Schema(description = "是否推荐")
    private Integer isRecommend;

    @Schema(description = "创建时间开始")
    private String startCreateTime;

    @Schema(description = "创建时间结束")
    private String endCreateTime;
}