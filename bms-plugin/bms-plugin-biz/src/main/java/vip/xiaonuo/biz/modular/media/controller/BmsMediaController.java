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
package vip.xiaonuo.biz.modular.media.controller;

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
import vip.xiaonuo.biz.modular.media.entity.BmsMedia;
import vip.xiaonuo.biz.modular.media.param.BmsMediaAddParam;
import vip.xiaonuo.biz.modular.media.param.BmsMediaEditParam;
import vip.xiaonuo.biz.modular.media.param.BmsMediaIdParam;
import vip.xiaonuo.biz.modular.media.param.BmsMediaPageParam;
import vip.xiaonuo.biz.modular.media.service.BmsMediaService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

/**
 * 媒体文件控制器
 *
 * @author yubaoshan
 * @date  2024/07/11 14:46
 */
@Tag(name = "媒体文件控制器")
@RestController
@Validated
public class BmsMediaController {

    @Resource
    private BmsMediaService bmsMediaService;

    /**
     * 获取媒体文件分页
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    @Operation(summary = "获取媒体文件分页")
    @SaCheckPermission("/biz/media/page")
    @GetMapping("/biz/media/page")
    public CommonResult<Page<BmsMedia>> page(BmsMediaPageParam bmsMediaPageParam) {
        return CommonResult.data(bmsMediaService.page(bmsMediaPageParam));
    }

    /**
     * 获取媒体文件列表
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    @Operation(summary = "获取媒体文件列表")
    @SaCheckPermission("/biz/media/list")
    @GetMapping("/biz/media/list")
    public CommonResult<List<BmsMedia>> list(BmsMediaPageParam bmsMediaPageParam) {
        return CommonResult.data(bmsMediaService.list(bmsMediaPageParam));
    }

    /**
     * 添加媒体文件
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    @Operation(summary = "添加媒体文件")
    @CommonLog("添加媒体文件")
    @SaCheckPermission("/biz/media/add")
    @PostMapping("/biz/media/add")
    public CommonResult<String> add(@RequestBody @Valid BmsMediaAddParam bmsMediaAddParam) {
        bmsMediaService.add(bmsMediaAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑媒体文件
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    @Operation(summary = "编辑媒体文件")
    @CommonLog("编辑媒体文件")
    @SaCheckPermission("/biz/media/edit")
    @PostMapping("/biz/media/edit")
    public CommonResult<String> edit(@RequestBody @Valid BmsMediaEditParam bmsMediaEditParam) {
        bmsMediaService.edit(bmsMediaEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除媒体文件
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    @Operation(summary = "删除媒体文件")
    @CommonLog("删除媒体文件")
    @SaCheckPermission("/biz/media/delete")
    @PostMapping("/biz/media/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BmsMediaIdParam> bmsMediaIdParamList) {
        bmsMediaService.delete(bmsMediaIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取媒体文件详情
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    @Operation(summary = "获取媒体文件详情")
    @SaCheckPermission("/biz/media/detail")
    @GetMapping("/biz/media/detail")
    public CommonResult<BmsMedia> detail(@Valid BmsMediaIdParam bmsMediaIdParam) {
        return CommonResult.data(bmsMediaService.detail(bmsMediaIdParam));
    }
}