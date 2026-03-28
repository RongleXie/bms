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
package vip.xiaonuo.biz.modular.article.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.biz.modular.article.entity.BmsArticle;
import vip.xiaonuo.biz.modular.article.param.BmsArticleAddParam;
import vip.xiaonuo.biz.modular.article.param.BmsArticleEditParam;
import vip.xiaonuo.biz.modular.article.param.BmsArticleIdParam;
import vip.xiaonuo.biz.modular.article.param.BmsArticlePageParam;
import vip.xiaonuo.biz.modular.article.service.BmsArticleService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 文章控制器
 *
 * @author xiaonuo
 * @date 2026/03/24
 **/
@Tag(name = "文章控制器")
@RestController
@Validated
public class BmsArticleController {

    @Resource
    private BmsArticleService bmsArticleService;

    @Operation(summary = "获取文章分页")
    @SaCheckPermission("/biz/article/page")
    @GetMapping("/biz/article/page")
    public CommonResult<Page<BmsArticle>> page(BmsArticlePageParam bmsArticlePageParam) {
        return CommonResult.data(bmsArticleService.page(bmsArticlePageParam));
    }

    @Operation(summary = "获取文章列表")
    @SaCheckPermission("/biz/article/list")
    @GetMapping("/biz/article/list")
    public CommonResult<List<BmsArticle>> list(BmsArticlePageParam bmsArticlePageParam) {
        return CommonResult.data(bmsArticleService.list(bmsArticlePageParam));
    }

    @Operation(summary = "添加文章")
    @CommonLog("添加文章")
    @SaCheckPermission("/biz/article/add")
    @PostMapping("/biz/article/add")
    public CommonResult<String> add(@RequestBody @Valid BmsArticleAddParam bmsArticleAddParam) {
        bmsArticleService.add(bmsArticleAddParam);
        return CommonResult.ok();
    }

    @Operation(summary = "编辑文章")
    @CommonLog("编辑文章")
    @SaCheckPermission("/biz/article/edit")
    @PostMapping("/biz/article/edit")
    public CommonResult<String> edit(@RequestBody @Valid BmsArticleEditParam bmsArticleEditParam) {
        bmsArticleService.edit(bmsArticleEditParam);
        return CommonResult.ok();
    }

    @Operation(summary = "删除文章")
    @CommonLog("删除文章")
    @SaCheckPermission("/biz/article/delete")
    @PostMapping("/biz/article/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               List<BmsArticleIdParam> bmsArticleIdParamList) {
        bmsArticleService.delete(bmsArticleIdParamList);
        return CommonResult.ok();
    }

    @Operation(summary = "获取文章详情")
    @SaCheckPermission("/biz/article/detail")
    @GetMapping("/biz/article/detail")
    public CommonResult<BmsArticle> detail(@Valid BmsArticleIdParam bmsArticleIdParam) {
        return CommonResult.data(bmsArticleService.detail(bmsArticleIdParam));
    }

    @Operation(summary = "发布文章")
    @CommonLog("发布文章")
    @SaCheckPermission("/biz/article/publish")
    @PostMapping("/biz/article/publish")
    public CommonResult<String> publish(@RequestBody @Valid BmsArticleIdParam bmsArticleIdParam) {
        bmsArticleService.publish(bmsArticleIdParam);
        return CommonResult.ok();
    }

    @Operation(summary = "撤回文章")
    @CommonLog("撤回文章")
    @SaCheckPermission("/biz/article/unpublish")
    @PostMapping("/biz/article/unpublish")
    public CommonResult<String> unpublish(@RequestBody @Valid BmsArticleIdParam bmsArticleIdParam) {
        bmsArticleService.unpublish(bmsArticleIdParam);
        return CommonResult.ok();
    }

    @Operation(summary = "定时发布文章")
    @CommonLog("定时发布文章")
    @SaCheckPermission("/biz/article/scheduledPublish")
    @PostMapping("/biz/article/scheduledPublish")
    public CommonResult<String> scheduledPublish(@RequestBody @Valid BmsArticleIdParam bmsArticleIdParam,
                                                  @RequestParam String scheduledTime) {
        bmsArticleService.scheduledPublish(bmsArticleIdParam, scheduledTime);
        return CommonResult.ok();
    }

    @Operation(summary = "取消定时发布")
    @CommonLog("取消定时发布")
    @SaCheckPermission("/biz/article/cancelScheduled")
    @PostMapping("/biz/article/cancelScheduled")
    public CommonResult<String> cancelScheduled(@RequestBody @Valid BmsArticleIdParam bmsArticleIdParam) {
        bmsArticleService.cancelScheduled(bmsArticleIdParam);
        return CommonResult.ok();
    }

    @Operation(summary = "获取待发布文章列表")
    @SaCheckPermission("/biz/article/scheduledList")
    @GetMapping("/biz/article/scheduledList")
    public CommonResult<Page<BmsArticle>> scheduledList(@RequestParam(defaultValue = "1") Integer current,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        return CommonResult.data(bmsArticleService.scheduledList(current, size));
    }
}