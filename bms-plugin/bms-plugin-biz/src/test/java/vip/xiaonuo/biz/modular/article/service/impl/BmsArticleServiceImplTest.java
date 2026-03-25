/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 */
package vip.xiaonuo.biz.modular.article.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import vip.xiaonuo.biz.modular.article.entity.BmsArticle;
import vip.xiaonuo.biz.modular.article.enums.BmsArticleStatusEnum;
import vip.xiaonuo.biz.modular.article.param.BmsArticleAddParam;
import vip.xiaonuo.biz.modular.article.param.BmsArticleEditParam;
import vip.xiaonuo.biz.modular.article.param.BmsArticleIdParam;
import vip.xiaonuo.biz.modular.article.param.BmsArticlePageParam;
import vip.xiaonuo.common.exception.CommonException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BmsArticleServiceImplTest {

    @Spy
    private BmsArticleServiceImpl bmsArticleService;

    private BmsArticle testArticle;

    @BeforeEach
    void setUp() {
        testArticle = new BmsArticle();
        testArticle.setId("test-article-id");
        testArticle.setTitle("测试文章标题");
        testArticle.setContent("测试文章内容");
        testArticle.setSummary("测试摘要");
        testArticle.setStatus(BmsArticleStatusEnum.DRAFT.getValue());
        testArticle.setViewCount(0);
        testArticle.setLikeCount(0);
        testArticle.setCommentCount(0);
    }

    @Test
    @DisplayName("分页查询文章-成功")
    void testPage_Success() {
        Page<BmsArticle> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testArticle));
        mockPage.setTotal(1);
        doReturn(mockPage).when(bmsArticleService).page(any(), any());

        BmsArticlePageParam pageParam = new BmsArticlePageParam();
        pageParam.setTitle("测试");
        Page<BmsArticle> result = bmsArticleService.page(pageParam);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("新增文章-成功")
    void testAdd_Success() {
        BmsArticleAddParam addParam = new BmsArticleAddParam();
        addParam.setTitle("新文章标题");
        addParam.setContent("新文章内容");
        doReturn(true).when(bmsArticleService).save(any(BmsArticle.class));

        bmsArticleService.add(addParam);
        verify(bmsArticleService).save(any(BmsArticle.class));
    }

    @Test
    @DisplayName("编辑文章-成功")
    void testEdit_Success() {
        BmsArticleEditParam editParam = new BmsArticleEditParam();
        editParam.setId("test-article-id");
        editParam.setTitle("更新后的标题");
        doReturn(testArticle).when(bmsArticleService).getById("test-article-id");
        doReturn(true).when(bmsArticleService).updateById(any(BmsArticle.class));

        bmsArticleService.edit(editParam);
        verify(bmsArticleService).updateById(any(BmsArticle.class));
    }

    @Test
    @DisplayName("编辑文章-文章不存在")
    void testEdit_ArticleNotFound() {
        BmsArticleEditParam editParam = new BmsArticleEditParam();
        editParam.setId("non-existent-id");
        editParam.setTitle("更新后的标题");
        doReturn(null).when(bmsArticleService).getById("non-existent-id");

        assertThrows(CommonException.class, () -> bmsArticleService.edit(editParam));
    }

    @Test
    @DisplayName("删除文章-成功")
    void testDelete_Success() {
        List<BmsArticleIdParam> idParamList = new ArrayList<>();
        BmsArticleIdParam idParam = new BmsArticleIdParam();
        idParam.setId("test-article-id");
        idParamList.add(idParam);
        doReturn(true).when(bmsArticleService).removeByIds(anyList());

        bmsArticleService.delete(idParamList);
        verify(bmsArticleService).removeByIds(anyList());
    }

    @Test
    @DisplayName("查询文章详情-成功")
    void testDetail_Success() {
        BmsArticleIdParam idParam = new BmsArticleIdParam();
        idParam.setId("test-article-id");
        doReturn(testArticle).when(bmsArticleService).getById("test-article-id");

        BmsArticle result = bmsArticleService.detail(idParam);
        assertNotNull(result);
        assertEquals("测试文章标题", result.getTitle());
    }

    @Test
    @DisplayName("查询文章详情-文章不存在")
    void testDetail_ArticleNotFound() {
        BmsArticleIdParam idParam = new BmsArticleIdParam();
        idParam.setId("non-existent-id");
        doReturn(null).when(bmsArticleService).getById("non-existent-id");

        assertThrows(CommonException.class, () -> bmsArticleService.detail(idParam));
    }

    @Test
    @DisplayName("发布文章-成功")
    void testPublish_Success() {
        BmsArticleIdParam idParam = new BmsArticleIdParam();
        idParam.setId("test-article-id");
        doReturn(testArticle).when(bmsArticleService).getById("test-article-id");
        doReturn(true).when(bmsArticleService).update(any(), any());

        bmsArticleService.publish(idParam);
        verify(bmsArticleService).update(any(), any());
    }

    @Test
    @DisplayName("发布文章-已发布状态")
    void testPublish_AlreadyPublished() {
        testArticle.setStatus(BmsArticleStatusEnum.PUBLISHED.getValue());
        BmsArticleIdParam idParam = new BmsArticleIdParam();
        idParam.setId("test-article-id");
        doReturn(testArticle).when(bmsArticleService).getById("test-article-id");

        assertThrows(CommonException.class, () -> bmsArticleService.publish(idParam));
    }

    @Test
    @DisplayName("撤回文章-成功")
    void testUnpublish_Success() {
        testArticle.setStatus(BmsArticleStatusEnum.PUBLISHED.getValue());
        BmsArticleIdParam idParam = new BmsArticleIdParam();
        idParam.setId("test-article-id");
        doReturn(testArticle).when(bmsArticleService).getById("test-article-id");
        doReturn(true).when(bmsArticleService).update(any(), any());

        bmsArticleService.unpublish(idParam);
        verify(bmsArticleService).update(any(), any());
    }

    @Test
    @DisplayName("撤回文章-未发布状态")
    void testUnpublish_NotPublished() {
        testArticle.setStatus(BmsArticleStatusEnum.DRAFT.getValue());
        BmsArticleIdParam idParam = new BmsArticleIdParam();
        idParam.setId("test-article-id");
        doReturn(testArticle).when(bmsArticleService).getById("test-article-id");

        assertThrows(CommonException.class, () -> bmsArticleService.unpublish(idParam));
    }

    @Test
    @DisplayName("查询实体-成功")
    void testQueryEntity_Success() {
        doReturn(testArticle).when(bmsArticleService).getById("test-article-id");
        BmsArticle result = bmsArticleService.queryEntity("test-article-id");
        assertNotNull(result);
        assertEquals("测试文章标题", result.getTitle());
    }

    @Test
    @DisplayName("查询实体-文章不存在")
    void testQueryEntity_ArticleNotFound() {
        doReturn(null).when(bmsArticleService).getById("non-existent-id");
        assertThrows(CommonException.class, () -> bmsArticleService.queryEntity("non-existent-id"));
    }
}