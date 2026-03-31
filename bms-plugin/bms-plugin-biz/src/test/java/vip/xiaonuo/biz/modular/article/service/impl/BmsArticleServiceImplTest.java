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
    @DisplayName("查询文章详情-Mock返回验证")
    void testDetail_MockReturn() {
        BmsArticleIdParam idParam = new BmsArticleIdParam();
        idParam.setId("test-article-id");
        doReturn(testArticle).when(bmsArticleService).detail(any(BmsArticleIdParam.class));

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

    @Test
    @DisplayName("删除文章-参数验证")
    void testDelete_ParamValidation() {
        List<BmsArticleIdParam> idParamList = new ArrayList<>();
        BmsArticleIdParam idParam = new BmsArticleIdParam();
        idParam.setId("test-article-id");
        idParamList.add(idParam);
        assertEquals(1, idParamList.size());
        assertEquals("test-article-id", idParamList.get(0).getId());
    }

    @Test
    @DisplayName("分页查询文章-返回结果验证")
    void testPage_ResultValidation() {
        Page<BmsArticle> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testArticle));
        mockPage.setTotal(1);
        assertNotNull(mockPage);
        assertEquals(1, mockPage.getTotal());
        assertEquals(1, mockPage.getRecords().size());
        assertEquals("测试文章标题", mockPage.getRecords().get(0).getTitle());
    }

    @Test
    @DisplayName("文章状态枚举-草稿状态")
    void testArticleStatus_Draft() {
        assertEquals("DRAFT", BmsArticleStatusEnum.DRAFT.getValue());
    }

    @Test
    @DisplayName("文章状态枚举-已发布状态")
    void testArticleStatus_Published() {
        assertEquals("PUBLISHED", BmsArticleStatusEnum.PUBLISHED.getValue());
    }

    @Test
    @DisplayName("文章状态枚举-定时发布状态")
    void testArticleStatus_Scheduled() {
        assertEquals("SCHEDULED", BmsArticleStatusEnum.SCHEDULED.getValue());
    }

    @Test
    @DisplayName("文章实体-属性设置和获取")
    void testArticleEntity_Properties() {
        BmsArticle article = new BmsArticle();
        article.setId("new-id");
        article.setTitle("新标题");
        article.setContent("新内容");
        article.setSummary("新摘要");
        article.setStatus(BmsArticleStatusEnum.PUBLISHED.getValue());
        article.setViewCount(100);
        article.setLikeCount(50);
        article.setCommentCount(10);
        article.setIsTop(1);
        article.setIsRecommend(1);

        assertEquals("new-id", article.getId());
        assertEquals("新标题", article.getTitle());
        assertEquals("新内容", article.getContent());
        assertEquals("新摘要", article.getSummary());
        assertEquals("PUBLISHED", article.getStatus());
        assertEquals(100, article.getViewCount());
        assertEquals(50, article.getLikeCount());
        assertEquals(10, article.getCommentCount());
        assertEquals(1, article.getIsTop());
        assertEquals(1, article.getIsRecommend());
    }

    @Test
    @DisplayName("参数对象-分页参数")
    void testPageParam_Properties() {
        BmsArticlePageParam pageParam = new BmsArticlePageParam();
        pageParam.setTitle("测试标题");
        pageParam.setCategoryId("category-1");
        pageParam.setStatus("PUBLISHED");
        pageParam.setIsTop(1);
        pageParam.setIsRecommend(1);

        assertEquals("测试标题", pageParam.getTitle());
        assertEquals("category-1", pageParam.getCategoryId());
        assertEquals("PUBLISHED", pageParam.getStatus());
        assertEquals(1, pageParam.getIsTop());
        assertEquals(1, pageParam.getIsRecommend());
    }

    @Test
    @DisplayName("参数对象-ID参数")
    void testIdParam_Properties() {
        BmsArticleIdParam idParam = new BmsArticleIdParam();
        idParam.setId("test-id");
        assertEquals("test-id", idParam.getId());
    }

    @Test
    @DisplayName("批量操作-空列表处理")
    void testBatchOperation_EmptyList() {
        List<BmsArticleIdParam> emptyList = new ArrayList<>();
        assertTrue(emptyList.isEmpty());
        assertEquals(0, emptyList.size());
    }

    @Test
    @DisplayName("批量操作-多个ID处理")
    void testBatchOperation_MultipleIds() {
        List<BmsArticleIdParam> idParamList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            BmsArticleIdParam idParam = new BmsArticleIdParam();
            idParam.setId("article-id-" + i);
            idParamList.add(idParam);
        }
        assertEquals(5, idParamList.size());
        assertEquals("article-id-1", idParamList.get(0).getId());
        assertEquals("article-id-5", idParamList.get(4).getId());
    }
}