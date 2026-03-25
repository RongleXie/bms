/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 */
package vip.xiaonuo.biz.modular.tag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import vip.xiaonuo.biz.modular.tag.entity.BmsTag;
import vip.xiaonuo.biz.modular.tag.enums.BmsTagStatusEnum;
import vip.xiaonuo.biz.modular.tag.param.BmsTagAddParam;
import vip.xiaonuo.biz.modular.tag.param.BmsTagEditParam;
import vip.xiaonuo.biz.modular.tag.param.BmsTagIdParam;
import vip.xiaonuo.biz.modular.tag.param.BmsTagPageParam;
import vip.xiaonuo.common.exception.CommonException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BmsTagServiceImplTest {

    @Spy
    private BmsTagServiceImpl bmsTagService;

    private BmsTag testTag;

    @BeforeEach
    void setUp() {
        testTag = new BmsTag();
        testTag.setId("test-tag-id");
        testTag.setName("测试标签");
        testTag.setCode("TEST_TAG");
        testTag.setColor("#FF5733");
        testTag.setStatus(BmsTagStatusEnum.ENABLE.getValue());
        testTag.setSortCode(1);
    }

    @Test
    @DisplayName("分页查询标签-成功")
    void testPage_Success() {
        Page<BmsTag> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testTag));
        mockPage.setTotal(1);
        doReturn(mockPage).when(bmsTagService).page(any(), any());

        BmsTagPageParam pageParam = new BmsTagPageParam();
        pageParam.setName("测试");
        Page<BmsTag> result = bmsTagService.page(pageParam);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("分页查询标签-按编码搜索")
    void testPage_SearchByCode() {
        Page<BmsTag> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testTag));
        mockPage.setTotal(1);
        doReturn(mockPage).when(bmsTagService).page(any(), any());

        BmsTagPageParam pageParam = new BmsTagPageParam();
        pageParam.setCode("TEST_TAG");
        Page<BmsTag> result = bmsTagService.page(pageParam);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("新增标签-成功")
    void testAdd_Success() {
        BmsTagAddParam addParam = new BmsTagAddParam();
        addParam.setName("新标签");
        addParam.setCode("NEW_TAG");
        addParam.setColor("#00FF00");
        doReturn(true).when(bmsTagService).save(any(BmsTag.class));

        bmsTagService.add(addParam);
        verify(bmsTagService).save(any(BmsTag.class));
    }

    @Test
    @DisplayName("编辑标签-成功")
    void testEdit_Success() {
        BmsTagEditParam editParam = new BmsTagEditParam();
        editParam.setId("test-tag-id");
        editParam.setName("更新后的标签");
        editParam.setCode("UPDATED_TAG");
        editParam.setColor("#0000FF");
        doReturn(testTag).when(bmsTagService).getById("test-tag-id");
        doReturn(true).when(bmsTagService).updateById(any(BmsTag.class));

        bmsTagService.edit(editParam);
        verify(bmsTagService).updateById(any(BmsTag.class));
    }

    @Test
    @DisplayName("编辑标签-标签不存在")
    void testEdit_TagNotFound() {
        BmsTagEditParam editParam = new BmsTagEditParam();
        editParam.setId("non-existent-id");
        editParam.setName("更新后的标签");
        doReturn(null).when(bmsTagService).getById("non-existent-id");

        assertThrows(CommonException.class, () -> bmsTagService.edit(editParam));
    }

    @Test
    @DisplayName("删除标签-成功")
    void testDelete_Success() {
        List<BmsTagIdParam> idParamList = new ArrayList<>();
        BmsTagIdParam idParam = new BmsTagIdParam();
        idParam.setId("test-tag-id");
        idParamList.add(idParam);
        doReturn(true).when(bmsTagService).removeByIds(anyList());

        bmsTagService.delete(idParamList);
        verify(bmsTagService).removeByIds(anyList());
    }

    @Test
    @DisplayName("删除标签-批量删除")
    void testDelete_Batch() {
        List<BmsTagIdParam> idParamList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            BmsTagIdParam idParam = new BmsTagIdParam();
            idParam.setId("tag-id-" + i);
            idParamList.add(idParam);
        }
        doReturn(true).when(bmsTagService).removeByIds(anyList());

        bmsTagService.delete(idParamList);
        verify(bmsTagService).removeByIds(anyList());
    }

    @Test
    @DisplayName("查询标签详情-成功")
    void testDetail_Success() {
        BmsTagIdParam idParam = new BmsTagIdParam();
        idParam.setId("test-tag-id");
        doReturn(testTag).when(bmsTagService).getById("test-tag-id");

        BmsTag result = bmsTagService.detail(idParam);
        assertNotNull(result);
        assertEquals("测试标签", result.getName());
        assertEquals("#FF5733", result.getColor());
    }

    @Test
    @DisplayName("查询标签详情-标签不存在")
    void testDetail_TagNotFound() {
        BmsTagIdParam idParam = new BmsTagIdParam();
        idParam.setId("non-existent-id");
        doReturn(null).when(bmsTagService).getById("non-existent-id");

        assertThrows(CommonException.class, () -> bmsTagService.detail(idParam));
    }

    @Test
    @DisplayName("查询标签列表-成功")
    void testList_Success() {
        doReturn(List.of(testTag)).when(bmsTagService).list(any(LambdaQueryWrapper.class));
        BmsTagPageParam pageParam = new BmsTagPageParam();

        List<BmsTag> result = bmsTagService.list(pageParam);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试标签", result.get(0).getName());
    }

    @Test
    @DisplayName("查询实体-成功")
    void testQueryEntity_Success() {
        doReturn(testTag).when(bmsTagService).getById("test-tag-id");
        BmsTag result = bmsTagService.queryEntity("test-tag-id");
        assertNotNull(result);
        assertEquals("测试标签", result.getName());
    }

    @Test
    @DisplayName("查询实体-标签不存在")
    void testQueryEntity_TagNotFound() {
        doReturn(null).when(bmsTagService).getById("non-existent-id");
        assertThrows(CommonException.class, () -> bmsTagService.queryEntity("non-existent-id"));
    }
}