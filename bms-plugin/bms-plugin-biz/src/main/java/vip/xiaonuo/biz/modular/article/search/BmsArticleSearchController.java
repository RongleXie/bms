package vip.xiaonuo.biz.modular.article.search;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.biz.modular.article.entity.BmsArticle;
import vip.xiaonuo.biz.modular.article.search.BmsArticleSearchService;
import vip.xiaonuo.common.pojo.CommonResult;

import java.util.List;

@Tag(name = "文章搜索控制器")
@RestController
@Validated
@RequestMapping("/biz/articleSearch")
public class BmsArticleSearchController {

    @Resource
    private BmsArticleSearchService articleSearchService;

    @Operation(summary = "全文搜索")
    @GetMapping("/search")
    public CommonResult<Page<BmsArticle>> search(@RequestParam String keyword,
                                                  @RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return CommonResult.data(articleSearchService.search(keyword, current, size));
    }

    @Operation(summary = "高级搜索")
    @GetMapping("/searchAdvanced")
    public CommonResult<Page<BmsArticle>> searchAdvanced(@RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false) String categoryId,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(defaultValue = "1") Integer current,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        return CommonResult.data(articleSearchService.searchAdvanced(keyword, categoryId, status, current, size));
    }

    @Operation(summary = "搜索建议")
    @GetMapping("/suggest")
    public CommonResult<List<String>> suggest(@RequestParam String prefix,
                                               @RequestParam(defaultValue = "10") Integer limit) {
        return CommonResult.data(articleSearchService.suggest(prefix, limit));
    }
}