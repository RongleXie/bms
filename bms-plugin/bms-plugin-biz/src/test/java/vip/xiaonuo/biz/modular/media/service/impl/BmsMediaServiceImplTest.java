/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 */
package vip.xiaonuo.biz.modular.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import vip.xiaonuo.biz.modular.media.entity.BmsMedia;
import vip.xiaonuo.biz.modular.media.enums.BmsMediaStatusEnum;
import vip.xiaonuo.biz.modular.media.param.BmsMediaAddParam;
import vip.xiaonuo.biz.modular.media.param.BmsMediaEditParam;
import vip.xiaonuo.biz.modular.media.param.BmsMediaIdParam;
import vip.xiaonuo.biz.modular.media.param.BmsMediaPageParam;
import vip.xiaonuo.common.exception.CommonException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BmsMediaServiceImplTest {

    @Spy
    private BmsMediaServiceImpl bmsMediaService;

    private BmsMedia testMedia;

    @BeforeEach
    void setUp() {
        testMedia = new BmsMedia();
        testMedia.setId("test-media-id");
        testMedia.setFileName("test-image.jpg");
        testMedia.setOriginalName("原始图片.jpg");
        testMedia.setFilePath("/upload/2024/03/test-image.jpg");
        testMedia.setFileUrl("http://example.com/upload/2024/03/test-image.jpg");
        testMedia.setFileSize(1024L);
        testMedia.setFileType("IMAGE");
        testMedia.setMimeType("image/jpeg");
        testMedia.setFileExt(".jpg");
        testMedia.setStatus(BmsMediaStatusEnum.ENABLE.getValue());
        testMedia.setDownloadCount(0);
    }

    @Test
    @DisplayName("分页查询媒体-成功")
    void testPage_Success() {
        Page<BmsMedia> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testMedia));
        mockPage.setTotal(1);
        doReturn(mockPage).when(bmsMediaService).page(any(), any());

        BmsMediaPageParam pageParam = new BmsMediaPageParam();
        pageParam.setFileName("test");
        Page<BmsMedia> result = bmsMediaService.page(pageParam);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("分页查询媒体-按文件类型筛选")
    void testPage_FilterByFileType() {
        Page<BmsMedia> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testMedia));
        mockPage.setTotal(1);
        doReturn(mockPage).when(bmsMediaService).page(any(), any());

        BmsMediaPageParam pageParam = new BmsMediaPageParam();
        pageParam.setFileType("IMAGE");
        Page<BmsMedia> result = bmsMediaService.page(pageParam);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("分页查询媒体-搜索关键词")
    void testPage_SearchKey() {
        Page<BmsMedia> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testMedia));
        mockPage.setTotal(1);
        doReturn(mockPage).when(bmsMediaService).page(any(), any());

        BmsMediaPageParam pageParam = new BmsMediaPageParam();
        pageParam.setSearchKey("图片");
        Page<BmsMedia> result = bmsMediaService.page(pageParam);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("新增媒体-成功")
    void testAdd_Success() {
        BmsMediaAddParam addParam = new BmsMediaAddParam();
        addParam.setFileName("new-image.png");
        addParam.setOriginalName("新图片.png");
        addParam.setFilePath("/upload/2024/03/new-image.png");
        addParam.setFileUrl("http://example.com/upload/2024/03/new-image.png");
        addParam.setFileSize(2048L);
        addParam.setFileType("IMAGE");
        addParam.setMimeType("image/png");
        doReturn(true).when(bmsMediaService).save(any(BmsMedia.class));

        bmsMediaService.add(addParam);
        verify(bmsMediaService).save(any(BmsMedia.class));
    }

    @Test
    @DisplayName("新增媒体-视频文件")
    void testAdd_VideoFile() {
        BmsMediaAddParam addParam = new BmsMediaAddParam();
        addParam.setFileName("video.mp4");
        addParam.setOriginalName("测试视频.mp4");
        addParam.setFilePath("/upload/2024/03/video.mp4");
        addParam.setFileUrl("http://example.com/upload/2024/03/video.mp4");
        addParam.setFileSize(10240L);
        addParam.setFileType("VIDEO");
        addParam.setMimeType("video/mp4");
        addParam.setDuration(120);
        doReturn(true).when(bmsMediaService).save(any(BmsMedia.class));

        bmsMediaService.add(addParam);
        verify(bmsMediaService).save(any(BmsMedia.class));
    }

    @Test
    @DisplayName("编辑媒体-成功")
    void testEdit_Success() {
        BmsMediaEditParam editParam = new BmsMediaEditParam();
        editParam.setId("test-media-id");
        editParam.setFileName("updated-image.jpg");
        editParam.setOriginalName("更新图片.jpg");
        doReturn(testMedia).when(bmsMediaService).getById("test-media-id");
        doReturn(true).when(bmsMediaService).updateById(any(BmsMedia.class));

        bmsMediaService.edit(editParam);
        verify(bmsMediaService).updateById(any(BmsMedia.class));
    }

    @Test
    @DisplayName("编辑媒体-媒体不存在")
    void testEdit_MediaNotFound() {
        BmsMediaEditParam editParam = new BmsMediaEditParam();
        editParam.setId("non-existent-id");
        editParam.setFileName("updated-image.jpg");
        doReturn(null).when(bmsMediaService).getById("non-existent-id");

        assertThrows(CommonException.class, () -> bmsMediaService.edit(editParam));
    }

    @Test
    @DisplayName("删除媒体-成功")
    void testDelete_Success() {
        List<BmsMediaIdParam> idParamList = new ArrayList<>();
        BmsMediaIdParam idParam = new BmsMediaIdParam();
        idParam.setId("test-media-id");
        idParamList.add(idParam);
        doReturn(true).when(bmsMediaService).removeByIds(anyList());

        bmsMediaService.delete(idParamList);
        verify(bmsMediaService).removeByIds(anyList());
    }

    @Test
    @DisplayName("删除媒体-批量删除")
    void testDelete_Batch() {
        List<BmsMediaIdParam> idParamList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            BmsMediaIdParam idParam = new BmsMediaIdParam();
            idParam.setId("media-id-" + i);
            idParamList.add(idParam);
        }
        doReturn(true).when(bmsMediaService).removeByIds(anyList());

        bmsMediaService.delete(idParamList);
        verify(bmsMediaService).removeByIds(anyList());
    }

    @Test
    @DisplayName("查询媒体详情-成功")
    void testDetail_Success() {
        BmsMediaIdParam idParam = new BmsMediaIdParam();
        idParam.setId("test-media-id");
        doReturn(testMedia).when(bmsMediaService).getById("test-media-id");

        BmsMedia result = bmsMediaService.detail(idParam);
        assertNotNull(result);
        assertEquals("test-image.jpg", result.getFileName());
        assertEquals("IMAGE", result.getFileType());
    }

    @Test
    @DisplayName("查询媒体详情-媒体不存在")
    void testDetail_MediaNotFound() {
        BmsMediaIdParam idParam = new BmsMediaIdParam();
        idParam.setId("non-existent-id");
        doReturn(null).when(bmsMediaService).getById("non-existent-id");

        assertThrows(CommonException.class, () -> bmsMediaService.detail(idParam));
    }

    @Test
    @DisplayName("查询媒体列表-成功")
    void testList_Success() {
        doReturn(List.of(testMedia)).when(bmsMediaService).list(any(LambdaQueryWrapper.class));
        BmsMediaPageParam pageParam = new BmsMediaPageParam();
        pageParam.setFileType("IMAGE");

        List<BmsMedia> result = bmsMediaService.list(pageParam);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-image.jpg", result.get(0).getFileName());
    }

    @Test
    @DisplayName("查询实体-成功")
    void testQueryEntity_Success() {
        doReturn(testMedia).when(bmsMediaService).getById("test-media-id");
        BmsMedia result = bmsMediaService.queryEntity("test-media-id");
        assertNotNull(result);
        assertEquals("test-image.jpg", result.getFileName());
    }

    @Test
    @DisplayName("查询实体-媒体不存在")
    void testQueryEntity_MediaNotFound() {
        doReturn(null).when(bmsMediaService).getById("non-existent-id");
        assertThrows(CommonException.class, () -> bmsMediaService.queryEntity("non-existent-id"));
    }
}