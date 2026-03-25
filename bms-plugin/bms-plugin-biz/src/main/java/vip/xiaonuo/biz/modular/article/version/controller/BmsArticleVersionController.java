package vip.xiaonuo.biz.modular.article.version.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.biz.modular.article.version.entity.BmsArticleVersion;
import vip.xiaonuo.biz.modular.article.version.service.BmsArticleVersionService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

@Tag(name = "文章版本历史控制器")
@RestController
@Validated
@RequestMapping("/biz/articleVersion")
public class BmsArticleVersionController {

    @Resource
    private BmsArticleVersionService articleVersionService;

    @Operation(summary = "获取版本历史分页列表")
    @GetMapping("/page")
    public CommonResult<Page<BmsArticleVersion>> page(@RequestParam String articleId,
                                                       @RequestParam(defaultValue = "1") Integer current,
                                                       @RequestParam(defaultValue = "10") Integer size) {
        return CommonResult.data(articleVersionService.page(articleId, current, size));
    }

    @Operation(summary = "获取版本历史列表")
    @GetMapping("/list")
    public CommonResult<List<BmsArticleVersion>> list(@RequestParam String articleId) {
        return CommonResult.data(articleVersionService.list(articleId));
    }

    @Operation(summary = "获取版本详情")
    @GetMapping("/detail")
    public CommonResult<BmsArticleVersion> detail(@RequestParam String id) {
        return CommonResult.data(articleVersionService.detail(id));
    }

    @Operation(summary = "回滚到指定版本")
    @CommonLog("回滚文章版本")
    @PostMapping("/rollback")
    public CommonResult<String> rollback(@RequestParam String articleId, @RequestParam Integer versionNumber) {
        articleVersionService.rollback(articleId, versionNumber);
        return CommonResult.ok("回滚成功");
    }

    @Operation(summary = "获取最新版本")
    @GetMapping("/latest")
    public CommonResult<BmsArticleVersion> getLatest(@RequestParam String articleId) {
        return CommonResult.data(articleVersionService.getLatestVersion(articleId));
    }
}