/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 */
package vip.xiaonuo.biz.modular.comment.service.impl;

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

    @Test
    @DisplayName("评论状态枚举-待审核")
    void testCommentStatus_Pending() {
        assertEquals("PENDING", BmsCommentStatusEnum.PENDING.getValue());
    }

    @Test
    @DisplayName("评论状态枚举-已通过")
    void testCommentStatus_Approved() {
        assertEquals("APPROVED", BmsCommentStatusEnum.APPROVED.getValue());
    }

    @Test
    @DisplayName("评论状态枚举-已拒绝")
    void testCommentStatus_Rejected() {
        assertEquals("REJECTED", BmsCommentStatusEnum.REJECTED.getValue());
    }

    @Test
    @DisplayName("评论实体-属性设置和获取")
    void testCommentEntity_Properties() {
        BmsComment comment = new BmsComment();
        comment.setId("new-id");
        comment.setArticleId("article-id");
        comment.setParentId("parent-id");
        comment.setNickname("用户名");
        comment.setEmail("user@example.com");
        comment.setContent("评论内容");
        comment.setStatus(BmsCommentStatusEnum.APPROVED.getValue());
        comment.setLikeCount(10);
        comment.setReplyUserId("reply-user-id");
        comment.setReplyUserName("被回复者");

        assertEquals("new-id", comment.getId());
        assertEquals("article-id", comment.getArticleId());
        assertEquals("parent-id", comment.getParentId());
        assertEquals("用户名", comment.getNickname());
        assertEquals("user@example.com", comment.getEmail());
        assertEquals("评论内容", comment.getContent());
        assertEquals("APPROVED", comment.getStatus());
        assertEquals(10, comment.getLikeCount());
        assertEquals("reply-user-id", comment.getReplyUserId());
        assertEquals("被回复者", comment.getReplyUserName());
    }

    @Test
    @DisplayName("参数对象-分页参数")
    void testPageParam_Properties() {
        BmsCommentPageParam pageParam = new BmsCommentPageParam();
        pageParam.setArticleId("article-id");
        pageParam.setStatus("APPROVED");

        assertEquals("article-id", pageParam.getArticleId());
        assertEquals("APPROVED", pageParam.getStatus());
    }

    @Test
    @DisplayName("参数对象-ID参数")
    void testIdParam_Properties() {
        BmsCommentIdParam idParam = new BmsCommentIdParam();
        idParam.setId("test-id");
        assertEquals("test-id", idParam.getId());
    }

    @Test
    @DisplayName("参数对象-新增参数")
    void testAddParam_Properties() {
        BmsCommentAddParam addParam = new BmsCommentAddParam();
        addParam.setArticleId("article-id");
        addParam.setNickname("评论者");
        addParam.setEmail("user@example.com");
        addParam.setContent("评论内容");
        addParam.setParentId("parent-id");

        assertEquals("article-id", addParam.getArticleId());
        assertEquals("评论者", addParam.getNickname());
        assertEquals("user@example.com", addParam.getEmail());
        assertEquals("评论内容", addParam.getContent());
        assertEquals("parent-id", addParam.getParentId());
    }

    @Test
    @DisplayName("分页结果-验证")
    void testPageResult_Validation() {
        Page<BmsComment> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testComment));
        mockPage.setTotal(1);

        assertNotNull(mockPage);
        assertEquals(1, mockPage.getTotal());
        assertEquals(1, mockPage.getRecords().size());
        assertEquals("这是一条测试评论", mockPage.getRecords().get(0).getContent());
    }

    @Test
    @DisplayName("评论层级-顶级评论")
    void testCommentLevel_TopLevel() {
        BmsComment topComment = new BmsComment();
        topComment.setParentId("0");
        assertEquals("0", topComment.getParentId());
    }

    @Test
    @DisplayName("评论层级-回复评论")
    void testCommentLevel_Reply() {
        BmsComment replyComment = new BmsComment();
        replyComment.setParentId("parent-comment-id");
        replyComment.setReplyUserId("user-123");
        replyComment.setReplyUserName("被回复者");

        assertEquals("parent-comment-id", replyComment.getParentId());
        assertEquals("user-123", replyComment.getReplyUserId());
        assertEquals("被回复者", replyComment.getReplyUserName());
    }
}