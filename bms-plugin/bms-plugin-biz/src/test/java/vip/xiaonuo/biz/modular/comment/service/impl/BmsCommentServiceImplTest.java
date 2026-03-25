/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 */
package vip.xiaonuo.biz.modular.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import vip.xiaonuo.biz.modular.comment.entity.BmsComment;
import vip.xiaonuo.biz.modular.comment.enums.BmsCommentStatusEnum;
import vip.xiaonuo.biz.modular.comment.param.BmsCommentAddParam;
import vip.xiaonuo.biz.modular.comment.param.BmsCommentIdParam;
import vip.xiaonuo.biz.modular.comment.param.BmsCommentPageParam;
import vip.xiaonuo.common.exception.CommonException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BmsCommentServiceImplTest {

    @Spy
    private BmsCommentServiceImpl bmsCommentService;

    private BmsComment testComment;

    @BeforeEach
    void setUp() {
        testComment = new BmsComment();
        testComment.setId("test-comment-id");
        testComment.setArticleId("test-article-id");
        testComment.setParentId("0");
        testComment.setNickname("测试用户");
        testComment.setEmail("test@example.com");
        testComment.setContent("这是一条测试评论");
        testComment.setStatus(BmsCommentStatusEnum.PENDING.getValue());
        testComment.setLikeCount(0);
    }

    @Test
    @DisplayName("分页查询评论-成功")
    void testPage_Success() {
        Page<BmsComment> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testComment));
        mockPage.setTotal(1);
        doReturn(mockPage).when(bmsCommentService).page(any(), any());

        BmsCommentPageParam pageParam = new BmsCommentPageParam();
        pageParam.setArticleId("test-article-id");
        Page<BmsComment> result = bmsCommentService.page(pageParam);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("分页查询评论-按状态筛选")
    void testPage_FilterByStatus() {
        Page<BmsComment> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testComment));
        mockPage.setTotal(1);
        doReturn(mockPage).when(bmsCommentService).page(any(), any());

        BmsCommentPageParam pageParam = new BmsCommentPageParam();
        pageParam.setStatus(BmsCommentStatusEnum.APPROVED.getValue());
        Page<BmsComment> result = bmsCommentService.page(pageParam);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("新增评论-成功")
    void testAdd_Success() {
        BmsCommentAddParam addParam = new BmsCommentAddParam();
        addParam.setArticleId("article-id");
        addParam.setNickname("评论者");
        addParam.setEmail("user@example.com");
        addParam.setContent("这是一条新评论");
        doReturn(true).when(bmsCommentService).save(any(BmsComment.class));

        bmsCommentService.add(addParam);
        verify(bmsCommentService).save(any(BmsComment.class));
    }

    @Test
    @DisplayName("新增评论-回复评论")
    void testAdd_ReplyComment() {
        BmsCommentAddParam addParam = new BmsCommentAddParam();
        addParam.setArticleId("article-id");
        addParam.setParentId("parent-comment-id");
        addParam.setReplyUserId("user-123");
        addParam.setReplyUserName("被回复者");
        addParam.setNickname("回复者");
        addParam.setContent("这是一条回复");
        doReturn(true).when(bmsCommentService).save(any(BmsComment.class));

        bmsCommentService.add(addParam);
        verify(bmsCommentService).save(any(BmsComment.class));
    }

    @Test
    @DisplayName("删除评论-成功")
    void testDelete_Success() {
        List<BmsCommentIdParam> idParamList = new ArrayList<>();
        BmsCommentIdParam idParam = new BmsCommentIdParam();
        idParam.setId("test-comment-id");
        idParamList.add(idParam);
        doReturn(true).when(bmsCommentService).removeByIds(anyList());

        bmsCommentService.delete(idParamList);
        verify(bmsCommentService).removeByIds(anyList());
    }

    @Test
    @DisplayName("删除评论-批量删除")
    void testDelete_Batch() {
        List<BmsCommentIdParam> idParamList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            BmsCommentIdParam idParam = new BmsCommentIdParam();
            idParam.setId("comment-id-" + i);
            idParamList.add(idParam);
        }
        doReturn(true).when(bmsCommentService).removeByIds(anyList());

        bmsCommentService.delete(idParamList);
        verify(bmsCommentService).removeByIds(anyList());
    }

    @Test
    @DisplayName("查询评论详情-成功")
    void testDetail_Success() {
        BmsCommentIdParam idParam = new BmsCommentIdParam();
        idParam.setId("test-comment-id");
        doReturn(testComment).when(bmsCommentService).getById("test-comment-id");

        BmsComment result = bmsCommentService.detail(idParam);
        assertNotNull(result);
        assertEquals("这是一条测试评论", result.getContent());
        assertEquals("测试用户", result.getNickname());
    }

    @Test
    @DisplayName("查询评论详情-评论不存在")
    void testDetail_CommentNotFound() {
        BmsCommentIdParam idParam = new BmsCommentIdParam();
        idParam.setId("non-existent-id");
        doReturn(null).when(bmsCommentService).getById("non-existent-id");

        assertThrows(CommonException.class, () -> bmsCommentService.detail(idParam));
    }

    @Test
    @DisplayName("审核通过评论-成功")
    void testApprove_Success() {
        BmsCommentIdParam idParam = new BmsCommentIdParam();
        idParam.setId("test-comment-id");
        doReturn(testComment).when(bmsCommentService).getById("test-comment-id");
        doReturn(true).when(bmsCommentService).update(any(), any());

        bmsCommentService.approve(idParam);
        verify(bmsCommentService).update(any(), any());
    }

    @Test
    @DisplayName("审核通过评论-已审核状态")
    void testApprove_AlreadyApproved() {
        testComment.setStatus(BmsCommentStatusEnum.APPROVED.getValue());
        BmsCommentIdParam idParam = new BmsCommentIdParam();
        idParam.setId("test-comment-id");
        doReturn(testComment).when(bmsCommentService).getById("test-comment-id");

        assertThrows(CommonException.class, () -> bmsCommentService.approve(idParam));
    }

    @Test
    @DisplayName("拒绝评论-成功")
    void testReject_Success() {
        BmsCommentIdParam idParam = new BmsCommentIdParam();
        idParam.setId("test-comment-id");
        doReturn(testComment).when(bmsCommentService).getById("test-comment-id");
        doReturn(true).when(bmsCommentService).update(any(), any());

        bmsCommentService.reject(idParam);
        verify(bmsCommentService).update(any(), any());
    }

    @Test
    @DisplayName("拒绝评论-已拒绝状态")
    void testReject_AlreadyRejected() {
        testComment.setStatus(BmsCommentStatusEnum.REJECTED.getValue());
        BmsCommentIdParam idParam = new BmsCommentIdParam();
        idParam.setId("test-comment-id");
        doReturn(testComment).when(bmsCommentService).getById("test-comment-id");

        assertThrows(CommonException.class, () -> bmsCommentService.reject(idParam));
    }

    @Test
    @DisplayName("查询评论列表-成功")
    void testList_Success() {
        doReturn(List.of(testComment)).when(bmsCommentService).list(any(LambdaQueryWrapper.class));
        BmsCommentPageParam pageParam = new BmsCommentPageParam();
        pageParam.setArticleId("test-article-id");

        List<BmsComment> result = bmsCommentService.list(pageParam);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("这是一条测试评论", result.get(0).getContent());
    }

    @Test
    @DisplayName("查询实体-成功")
    void testQueryEntity_Success() {
        doReturn(testComment).when(bmsCommentService).getById("test-comment-id");
        BmsComment result = bmsCommentService.queryEntity("test-comment-id");
        assertNotNull(result);
        assertEquals("这是一条测试评论", result.getContent());
    }

    @Test
    @DisplayName("查询实体-评论不存在")
    void testQueryEntity_CommentNotFound() {
        doReturn(null).when(bmsCommentService).getById("non-existent-id");
        assertThrows(CommonException.class, () -> bmsCommentService.queryEntity("non-existent-id"));
    }
}