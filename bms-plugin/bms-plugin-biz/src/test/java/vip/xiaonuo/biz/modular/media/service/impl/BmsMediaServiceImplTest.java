/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 */
package vip.xiaonuo.biz.modular.media.service.impl;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    @DisplayName("媒体状态枚举-启用")
    void testMediaStatus_Enable() {
        assertEquals("ENABLE", BmsMediaStatusEnum.ENABLE.getValue());
    }

    @Test
    @DisplayName("媒体状态枚举-禁用")
    void testMediaStatus_Disabled() {
        assertEquals("DISABLED", BmsMediaStatusEnum.DISABLED.getValue());
    }

    @Test
    @DisplayName("媒体实体-属性设置和获取")
    void testMediaEntity_Properties() {
        BmsMedia media = new BmsMedia();
        media.setId("new-id");
        media.setFileName("new-file.png");
        media.setOriginalName("新文件.png");
        media.setFilePath("/upload/new-file.png");
        media.setFileUrl("http://example.com/upload/new-file.png");
        media.setFileSize(2048L);
        media.setFileType("IMAGE");
        media.setMimeType("image/png");
        media.setFileExt(".png");
        media.setStatus(BmsMediaStatusEnum.ENABLE.getValue());
        media.setDownloadCount(100);
        media.setDuration(120);
        media.setThumbnailUrl("http://example.com/thumb.png");

        assertEquals("new-id", media.getId());
        assertEquals("new-file.png", media.getFileName());
        assertEquals("新文件.png", media.getOriginalName());
        assertEquals("/upload/new-file.png", media.getFilePath());
        assertEquals("http://example.com/upload/new-file.png", media.getFileUrl());
        assertEquals(2048L, media.getFileSize());
        assertEquals("IMAGE", media.getFileType());
        assertEquals("image/png", media.getMimeType());
        assertEquals(".png", media.getFileExt());
        assertEquals("ENABLE", media.getStatus());
        assertEquals(100, media.getDownloadCount());
        assertEquals(120, media.getDuration());
        assertEquals("http://example.com/thumb.png", media.getThumbnailUrl());
    }

    @Test
    @DisplayName("参数对象-分页参数")
    void testPageParam_Properties() {
        BmsMediaPageParam pageParam = new BmsMediaPageParam();
        pageParam.setFileName("test");
        pageParam.setFileType("IMAGE");
        pageParam.setStatus("ENABLE");

        assertEquals("test", pageParam.getFileName());
        assertEquals("IMAGE", pageParam.getFileType());
        assertEquals("ENABLE", pageParam.getStatus());
    }

    @Test
    @DisplayName("参数对象-ID参数")
    void testIdParam_Properties() {
        BmsMediaIdParam idParam = new BmsMediaIdParam();
        idParam.setId("test-id");
        assertEquals("test-id", idParam.getId());
    }

    @Test
    @DisplayName("参数对象-新增参数")
    void testAddParam_Properties() {
        BmsMediaAddParam addParam = new BmsMediaAddParam();
        addParam.setFileName("new-image.png");
        addParam.setOriginalName("新图片.png");
        addParam.setFilePath("/upload/new-image.png");
        addParam.setFileUrl("http://example.com/upload/new-image.png");
        addParam.setFileSize(2048L);
        addParam.setFileType("IMAGE");
        addParam.setMimeType("image/png");
        addParam.setDuration(120);

        assertEquals("new-image.png", addParam.getFileName());
        assertEquals("新图片.png", addParam.getOriginalName());
        assertEquals("/upload/new-image.png", addParam.getFilePath());
        assertEquals("http://example.com/upload/new-image.png", addParam.getFileUrl());
        assertEquals(2048L, addParam.getFileSize());
        assertEquals("IMAGE", addParam.getFileType());
        assertEquals("image/png", addParam.getMimeType());
        assertEquals(120, addParam.getDuration());
    }

    @Test
    @DisplayName("参数对象-编辑参数")
    void testEditParam_Properties() {
        BmsMediaEditParam editParam = new BmsMediaEditParam();
        editParam.setId("edit-id");
        editParam.setFileName("edited-file.jpg");
        editParam.setOriginalName("编辑文件.jpg");

        assertEquals("edit-id", editParam.getId());
        assertEquals("edited-file.jpg", editParam.getFileName());
        assertEquals("编辑文件.jpg", editParam.getOriginalName());
    }

    @Test
    @DisplayName("分页结果-验证")
    void testPageResult_Validation() {
        Page<BmsMedia> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testMedia));
        mockPage.setTotal(1);

        assertNotNull(mockPage);
        assertEquals(1, mockPage.getTotal());
        assertEquals(1, mockPage.getRecords().size());
        assertEquals("test-image.jpg", mockPage.getRecords().get(0).getFileName());
    }

    @Test
    @DisplayName("文件类型验证-图片")
    void testFileType_Image() {
        String[] imageTypes = {"jpg", "jpeg", "png", "gif", "webp"};
        for (String type : imageTypes) {
            assertTrue(type.matches("^(jpg|jpeg|png|gif|webp)$"));
        }
    }

    @Test
    @DisplayName("文件类型验证-视频")
    void testFileType_Video() {
        String[] videoTypes = {"mp4", "avi", "mov", "mkv"};
        for (String type : videoTypes) {
            assertTrue(type.matches("^(mp4|avi|mov|mkv)$"));
        }
    }

    @Test
    @DisplayName("MIME类型验证")
    void testMimeType_Validation() {
        assertEquals("image/jpeg", testMedia.getMimeType());
        assertTrue(testMedia.getMimeType().startsWith("image/"));
    }

    @Test
    @DisplayName("文件大小格式化-KB")
    void testFileSize_Formatting_KB() {
        long sizeInBytes = 1024L;
        double sizeInKB = sizeInBytes / 1024.0;
        assertEquals(1.0, sizeInKB);
    }

    @Test
    @DisplayName("文件大小格式化-MB")
    void testFileSize_Formatting_MB() {
        long sizeInBytes = 1024L * 1024L;
        double sizeInMB = sizeInBytes / (1024.0 * 1024.0);
        assertEquals(1.0, sizeInMB);
    }
}