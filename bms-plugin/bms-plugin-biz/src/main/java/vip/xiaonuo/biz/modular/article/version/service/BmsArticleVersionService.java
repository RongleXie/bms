package vip.xiaonuo.biz.modular.article.version.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import vip.xiaonuo.biz.modular.article.version.entity.BmsArticleVersion;

import java.util.List;

public interface BmsArticleVersionService {

    Page<BmsArticleVersion> page(String articleId, Integer current, Integer size);

    List<BmsArticleVersion> list(String articleId);

    BmsArticleVersion detail(String id);

    void saveVersion(String articleId, String title, String content, String summary, String changeSummary);

    void rollback(String articleId, Integer versionNumber);

    BmsArticleVersion getLatestVersion(String articleId);

    void deleteByArticleId(String articleId);
}