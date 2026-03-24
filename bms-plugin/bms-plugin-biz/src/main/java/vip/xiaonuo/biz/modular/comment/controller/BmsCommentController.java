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
package vip.xiaonuo.biz.modular.comment.controller;

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
import vip.xiaonuo.biz.modular.comment.entity.BmsComment;
import vip.xiaonuo.biz.modular.comment.param.BmsCommentAddParam;
import vip.xiaonuo.biz.modular.comment.param.BmsCommentIdParam;
import vip.xiaonuo.biz.modular.comment.param.BmsCommentPageParam;
import vip.xiaonuo.biz.modular.comment.service.BmsCommentService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 评论控制器
 *
 * @author xiaonuo
 * @date 2026/03/24
 **/
@Tag(name = "评论控制器")
@RestController
@Validated
public class BmsCommentController {

    @Resource
    private BmsCommentService bmsCommentService;

    @Operation(summary = "获取评论分页")
    @SaCheckPermission("/biz/comment/page")
    @GetMapping("/biz/comment/page")
    public CommonResult<Page<BmsComment>> page(BmsCommentPageParam bmsCommentPageParam) {
        return CommonResult.data(bmsCommentService.page(bmsCommentPageParam));
    }

    @Operation(summary = "获取评论列表")
    @SaCheckPermission("/biz/comment/list")
    @GetMapping("/biz/comment/list")
    public CommonResult<List<BmsComment>> list(BmsCommentPageParam bmsCommentPageParam) {
        return CommonResult.data(bmsCommentService.list(bmsCommentPageParam));
    }

    @Operation(summary = "添加评论")
    @CommonLog("添加评论")
    @SaCheckPermission("/biz/comment/add")
    @PostMapping("/biz/comment/add")
    public CommonResult<String> add(@RequestBody @Valid BmsCommentAddParam bmsCommentAddParam) {
        bmsCommentService.add(bmsCommentAddParam);
        return CommonResult.ok();
    }

    @Operation(summary = "删除评论")
    @CommonLog("删除评论")
    @SaCheckPermission("/biz/comment/delete")
    @PostMapping("/biz/comment/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               List<BmsCommentIdParam> bmsCommentIdParamList) {
        bmsCommentService.delete(bmsCommentIdParamList);
        return CommonResult.ok();
    }

    @Operation(summary = "获取评论详情")
    @SaCheckPermission("/biz/comment/detail")
    @GetMapping("/biz/comment/detail")
    public CommonResult<BmsComment> detail(@Valid BmsCommentIdParam bmsCommentIdParam) {
        return CommonResult.data(bmsCommentService.detail(bmsCommentIdParam));
    }

    @Operation(summary = "审核通过评论")
    @CommonLog("审核通过评论")
    @SaCheckPermission("/biz/comment/approve")
    @PostMapping("/biz/comment/approve")
    public CommonResult<String> approve(@RequestBody @Valid BmsCommentIdParam bmsCommentIdParam) {
        bmsCommentService.approve(bmsCommentIdParam);
        return CommonResult.ok();
    }

    @Operation(summary = "审核拒绝评论")
    @CommonLog("审核拒绝评论")
    @SaCheckPermission("/biz/comment/reject")
    @PostMapping("/biz/comment/reject")
    public CommonResult<String> reject(@RequestBody @Valid BmsCommentIdParam bmsCommentIdParam) {
        bmsCommentService.reject(bmsCommentIdParam);
        return CommonResult.ok();
    }
}