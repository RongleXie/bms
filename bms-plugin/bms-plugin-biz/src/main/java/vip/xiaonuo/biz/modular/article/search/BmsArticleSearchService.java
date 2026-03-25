package vip.xiaonuo.biz.modular.article.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import vip.xiaonuo.biz.modular.article.entity.BmsArticle;

import java.util.List;

public interface BmsArticleSearchService {

    Page<BmsArticle> search(String keyword, Integer current, Integer size);

    Page<BmsArticle> searchAdvanced(String keyword, String categoryId, String status, Integer current, Integer size);

    List<String> suggest(String prefix, Integer limit);

    void rebuildIndex();
}