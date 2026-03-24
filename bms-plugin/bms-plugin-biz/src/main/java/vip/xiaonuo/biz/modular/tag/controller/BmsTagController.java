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
package vip.xiaonuo.biz.modular.tag.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.biz.modular.tag.entity.BmsTag;
import vip.xiaonuo.biz.modular.tag.param.BmsTagAddParam;
import vip.xiaonuo.biz.modular.tag.param.BmsTagEditParam;
import vip.xiaonuo.biz.modular.tag.param.BmsTagIdParam;
import vip.xiaonuo.biz.modular.tag.param.BmsTagPageParam;
import vip.xiaonuo.biz.modular.tag.service.BmsTagService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 标签控制器
 *
 * @author xiaonuo
 * @date 2024/01/01
 */
@Tag(name = "标签控制器")
@RestController
@Validated
public class BmsTagController {

    @Resource
    private BmsTagService bmsTagService;

    /**
     * 获取标签分页
     */
    @Operation(summary = "获取标签分页")
    @SaCheckPermission("/biz/tag/page")
    @GetMapping("/biz/tag/page")
    public CommonResult<Page<BmsTag>> page(BmsTagPageParam bmsTagPageParam) {
        return CommonResult.data(bmsTagService.page(bmsTagPageParam));
    }

    /**
     * 获取标签列表
     */
    @Operation(summary = "获取标签列表")
    @SaCheckPermission("/biz/tag/list")
    @GetMapping("/biz/tag/list")
    public CommonResult<List<BmsTag>> list(BmsTagPageParam bmsTagPageParam) {
        return CommonResult.data(bmsTagService.list(bmsTagPageParam));
    }

    /**
     * 添加标签
     */
    @Operation(summary = "添加标签")
    @CommonLog("添加标签")
    @SaCheckPermission("/biz/tag/add")
    @PostMapping("/biz/tag/add")
    public CommonResult<String> add(@RequestBody @Valid BmsTagAddParam bmsTagAddParam) {
        bmsTagService.add(bmsTagAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑标签
     */
    @Operation(summary = "编辑标签")
    @CommonLog("编辑标签")
    @SaCheckPermission("/biz/tag/edit")
    @PostMapping("/biz/tag/edit")
    public CommonResult<String> edit(@RequestBody @Valid BmsTagEditParam bmsTagEditParam) {
        bmsTagService.edit(bmsTagEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除标签
     */
    @Operation(summary = "删除标签")
    @CommonLog("删除标签")
    @SaCheckPermission("/biz/tag/delete")
    @PostMapping("/biz/tag/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               List<BmsTagIdParam> bmsTagIdParamList) {
        bmsTagService.delete(bmsTagIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取标签详情
     */
    @Operation(summary = "获取标签详情")
    @SaCheckPermission("/biz/tag/detail")
    @GetMapping("/biz/tag/detail")
    public CommonResult<BmsTag> detail(@Valid BmsTagIdParam bmsTagIdParam) {
        return CommonResult.data(bmsTagService.detail(bmsTagIdParam));
    }
}