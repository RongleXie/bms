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
package vip.xiaonuo.biz.modular.category.controller;

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
import vip.xiaonuo.biz.modular.category.entity.BmsCategory;
import vip.xiaonuo.biz.modular.category.param.BmsCategoryAddParam;
import vip.xiaonuo.biz.modular.category.param.BmsCategoryEditParam;
import vip.xiaonuo.biz.modular.category.param.BmsCategoryIdParam;
import vip.xiaonuo.biz.modular.category.param.BmsCategoryPageParam;
import vip.xiaonuo.biz.modular.category.service.BmsCategoryService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

@Tag(name = "博客分类控制器")
@RestController
@Validated
public class BmsCategoryController {

    @Resource
    private BmsCategoryService bmsCategoryService;

    @Operation(summary = "获取博客分类分页")
    @SaCheckPermission("/biz/category/page")
    @GetMapping("/biz/category/page")
    public CommonResult<Page<BmsCategory>> page(BmsCategoryPageParam bmsCategoryPageParam) {
        return CommonResult.data(bmsCategoryService.page(bmsCategoryPageParam));
    }

    @Operation(summary = "获取博客分类列表")
    @SaCheckPermission("/biz/category/list")
    @GetMapping("/biz/category/list")
    public CommonResult<List<BmsCategory>> list(BmsCategoryPageParam bmsCategoryPageParam) {
        return CommonResult.data(bmsCategoryService.list(bmsCategoryPageParam));
    }

    @Operation(summary = "获取博客分类树")
    @SaCheckPermission("/biz/category/tree")
    @GetMapping("/biz/category/tree")
    public CommonResult<List<BmsCategory>> tree(BmsCategoryPageParam bmsCategoryPageParam) {
        return CommonResult.data(bmsCategoryService.tree(bmsCategoryPageParam));
    }

    @Operation(summary = "添加博客分类")
    @CommonLog("添加博客分类")
    @SaCheckPermission("/biz/category/add")
    @PostMapping("/biz/category/add")
    public CommonResult<String> add(@RequestBody @Valid BmsCategoryAddParam bmsCategoryAddParam) {
        bmsCategoryService.add(bmsCategoryAddParam);
        return CommonResult.ok();
    }

    @Operation(summary = "编辑博客分类")
    @CommonLog("编辑博客分类")
    @SaCheckPermission("/biz/category/edit")
    @PostMapping("/biz/category/edit")
    public CommonResult<String> edit(@RequestBody @Valid BmsCategoryEditParam bmsCategoryEditParam) {
        bmsCategoryService.edit(bmsCategoryEditParam);
        return CommonResult.ok();
    }

    @Operation(summary = "删除博客分类")
    @CommonLog("删除博客分类")
    @SaCheckPermission("/biz/category/delete")
    @PostMapping("/biz/category/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BmsCategoryIdParam> bmsCategoryIdParamList) {
        bmsCategoryService.delete(bmsCategoryIdParamList);
        return CommonResult.ok();
    }

    @Operation(summary = "获取博客分类详情")
    @SaCheckPermission("/biz/category/detail")
    @GetMapping("/biz/category/detail")
    public CommonResult<BmsCategory> detail(@Valid BmsCategoryIdParam bmsCategoryIdParam) {
        return CommonResult.data(bmsCategoryService.detail(bmsCategoryIdParam));
    }

    @Operation(summary = "禁用博客分类")
    @CommonLog("禁用博客分类")
    @SaCheckPermission("/biz/category/disableStatus")
    @PostMapping("/biz/category/disableStatus")
    public CommonResult<String> disableStatus(@RequestBody @Valid BmsCategoryIdParam bmsCategoryIdParam) {
        bmsCategoryService.disableStatus(bmsCategoryIdParam);
        return CommonResult.ok();
    }

    @Operation(summary = "启用博客分类")
    @CommonLog("启用博客分类")
    @SaCheckPermission("/biz/category/enableStatus")
    @PostMapping("/biz/category/enableStatus")
    public CommonResult<String> enableStatus(@RequestBody @Valid BmsCategoryIdParam bmsCategoryIdParam) {
        bmsCategoryService.enableStatus(bmsCategoryIdParam);
        return CommonResult.ok();
    }
}