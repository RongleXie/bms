/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 */
package vip.xiaonuo.biz.modular.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import vip.xiaonuo.biz.modular.category.entity.BmsCategory;
import vip.xiaonuo.biz.modular.category.enums.BmsCategoryStatusEnum;
import vip.xiaonuo.biz.modular.category.param.BmsCategoryAddParam;
import vip.xiaonuo.biz.modular.category.param.BmsCategoryEditParam;
import vip.xiaonuo.biz.modular.category.param.BmsCategoryIdParam;
import vip.xiaonuo.biz.modular.category.param.BmsCategoryPageParam;
import vip.xiaonuo.common.exception.CommonException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BmsCategoryServiceImplTest {

    @Spy
    private BmsCategoryServiceImpl bmsCategoryService;

    private BmsCategory testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new BmsCategory();
        testCategory.setId("test-category-id");
        testCategory.setName("测试分类");
        testCategory.setCode("TEST_CAT");
        testCategory.setParentId("0");
        testCategory.setStatus(BmsCategoryStatusEnum.ENABLE.getValue());
        testCategory.setSortCode(1);
    }

    @Test
    @DisplayName("分页查询分类-成功")
    void testPage_Success() {
        Page<BmsCategory> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testCategory));
        mockPage.setTotal(1);
        doReturn(mockPage).when(bmsCategoryService).page(any(), any());

        BmsCategoryPageParam pageParam = new BmsCategoryPageParam();
        pageParam.setName("测试");
        Page<BmsCategory> result = bmsCategoryService.page(pageParam);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("新增分类-成功")
    void testAdd_Success() {
        BmsCategoryAddParam addParam = new BmsCategoryAddParam();
        addParam.setName("新分类");
        addParam.setCode("NEW_CAT");
        addParam.setParentId("0");
        doReturn(true).when(bmsCategoryService).save(any(BmsCategory.class));

        bmsCategoryService.add(addParam);
        verify(bmsCategoryService).save(any(BmsCategory.class));
    }

    @Test
    @DisplayName("新增分类-父级ID为空时默认为根级")
    void testAdd_ParentIdNull_DefaultToRoot() {
        BmsCategoryAddParam addParam = new BmsCategoryAddParam();
        addParam.setName("根级分类");
        addParam.setCode("ROOT_CAT");
        doReturn(true).when(bmsCategoryService).save(any(BmsCategory.class));

        bmsCategoryService.add(addParam);
        verify(bmsCategoryService).save(any(BmsCategory.class));
    }

    @Test
    @DisplayName("编辑分类-成功")
    void testEdit_Success() {
        BmsCategoryEditParam editParam = new BmsCategoryEditParam();
        editParam.setId("test-category-id");
        editParam.setName("更新后的分类");
        doReturn(testCategory).when(bmsCategoryService).getById("test-category-id");
        doReturn(true).when(bmsCategoryService).updateById(any(BmsCategory.class));

        bmsCategoryService.edit(editParam);
        verify(bmsCategoryService).updateById(any(BmsCategory.class));
    }

    @Test
    @DisplayName("编辑分类-分类不存在")
    void testEdit_CategoryNotFound() {
        BmsCategoryEditParam editParam = new BmsCategoryEditParam();
        editParam.setId("non-existent-id");
        editParam.setName("更新后的分类");
        doReturn(null).when(bmsCategoryService).getById("non-existent-id");

        assertThrows(CommonException.class, () -> bmsCategoryService.edit(editParam));
    }

    @Test
    @DisplayName("删除分类-成功")
    void testDelete_Success() {
        List<BmsCategoryIdParam> idParamList = new ArrayList<>();
        BmsCategoryIdParam idParam = new BmsCategoryIdParam();
        idParam.setId("test-category-id");
        idParamList.add(idParam);
        doReturn(0L).when(bmsCategoryService).count(any(LambdaQueryWrapper.class));
        doReturn(true).when(bmsCategoryService).removeByIds(anyList());

        bmsCategoryService.delete(idParamList);
        verify(bmsCategoryService).removeByIds(anyList());
    }

    @Test
    @DisplayName("删除分类-存在子分类")
    void testDelete_HasChildren() {
        List<BmsCategoryIdParam> idParamList = new ArrayList<>();
        BmsCategoryIdParam idParam = new BmsCategoryIdParam();
        idParam.setId("test-category-id");
        idParamList.add(idParam);
        doReturn(2L).when(bmsCategoryService).count(any(LambdaQueryWrapper.class));

        assertThrows(CommonException.class, () -> bmsCategoryService.delete(idParamList));
    }

    @Test
    @DisplayName("查询分类详情-成功")
    void testDetail_Success() {
        BmsCategoryIdParam idParam = new BmsCategoryIdParam();
        idParam.setId("test-category-id");
        doReturn(testCategory).when(bmsCategoryService).getById("test-category-id");

        BmsCategory result = bmsCategoryService.detail(idParam);
        assertNotNull(result);
        assertEquals("测试分类", result.getName());
    }

    @Test
    @DisplayName("查询分类详情-分类不存在")
    void testDetail_CategoryNotFound() {
        BmsCategoryIdParam idParam = new BmsCategoryIdParam();
        idParam.setId("non-existent-id");
        doReturn(null).when(bmsCategoryService).getById("non-existent-id");

        assertThrows(CommonException.class, () -> bmsCategoryService.detail(idParam));
    }

    @Test
    @DisplayName("禁用分类-成功")
    void testDisableStatus_Success() {
        BmsCategoryIdParam idParam = new BmsCategoryIdParam();
        idParam.setId("test-category-id");
        doReturn(true).when(bmsCategoryService).update(any(), any());

        bmsCategoryService.disableStatus(idParam);
        verify(bmsCategoryService).update(any(), any());
    }

    @Test
    @DisplayName("启用分类-成功")
    void testEnableStatus_Success() {
        BmsCategoryIdParam idParam = new BmsCategoryIdParam();
        idParam.setId("test-category-id");
        doReturn(true).when(bmsCategoryService).update(any(), any());

        bmsCategoryService.enableStatus(idParam);
        verify(bmsCategoryService).update(any(), any());
    }

    @Test
    @DisplayName("获取分类树-成功")
    void testTree_Success() {
        BmsCategoryPageParam pageParam = new BmsCategoryPageParam();
        BmsCategory parent = new BmsCategory();
        parent.setId("parent-id");
        parent.setName("父分类");
        parent.setParentId("0");
        BmsCategory child = new BmsCategory();
        child.setId("child-id");
        child.setName("子分类");
        child.setParentId("parent-id");
        doReturn(List.of(parent, child)).when(bmsCategoryService).list(any(LambdaQueryWrapper.class));

        List<BmsCategory> result = bmsCategoryService.tree(pageParam);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("父分类", result.get(0).getName());
        assertNotNull(result.get(0).getChildren());
        assertEquals(1, result.get(0).getChildren().size());
    }

    @Test
    @DisplayName("查询实体-成功")
    void testQueryEntity_Success() {
        doReturn(testCategory).when(bmsCategoryService).getById("test-category-id");
        BmsCategory result = bmsCategoryService.queryEntity("test-category-id");
        assertNotNull(result);
        assertEquals("测试分类", result.getName());
    }

    @Test
    @DisplayName("查询实体-分类不存在")
    void testQueryEntity_CategoryNotFound() {
        doReturn(null).when(bmsCategoryService).getById("non-existent-id");
        assertThrows(CommonException.class, () -> bmsCategoryService.queryEntity("non-existent-id"));
    }
}