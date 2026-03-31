/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 */
package vip.xiaonuo.biz.modular.category.service.impl;

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

    @Test
    @DisplayName("分类状态枚举-启用")
    void testCategoryStatus_Enable() {
        assertEquals("ENABLE", BmsCategoryStatusEnum.ENABLE.getValue());
    }

    @Test
    @DisplayName("分类状态枚举-禁用")
    void testCategoryStatus_Disabled() {
        assertEquals("DISABLED", BmsCategoryStatusEnum.DISABLED.getValue());
    }

    @Test
    @DisplayName("分类实体-属性设置和获取")
    void testCategoryEntity_Properties() {
        BmsCategory category = new BmsCategory();
        category.setId("new-id");
        category.setName("新分类");
        category.setCode("NEW_CAT");
        category.setParentId("parent-id");
        category.setStatus(BmsCategoryStatusEnum.ENABLE.getValue());
        category.setSortCode(100);
        category.setDescription("分类描述");
        category.setIcon("icon-class");

        assertEquals("new-id", category.getId());
        assertEquals("新分类", category.getName());
        assertEquals("NEW_CAT", category.getCode());
        assertEquals("parent-id", category.getParentId());
        assertEquals("ENABLE", category.getStatus());
        assertEquals(100, category.getSortCode());
        assertEquals("分类描述", category.getDescription());
        assertEquals("icon-class", category.getIcon());
    }

    @Test
    @DisplayName("参数对象-分页参数")
    void testPageParam_Properties() {
        BmsCategoryPageParam pageParam = new BmsCategoryPageParam();
        pageParam.setName("测试分类");
        pageParam.setCode("TEST_CAT");
        pageParam.setStatus("ENABLE");
        pageParam.setParentId("parent-id");

        assertEquals("测试分类", pageParam.getName());
        assertEquals("TEST_CAT", pageParam.getCode());
        assertEquals("ENABLE", pageParam.getStatus());
        assertEquals("parent-id", pageParam.getParentId());
    }

    @Test
    @DisplayName("参数对象-ID参数")
    void testIdParam_Properties() {
        BmsCategoryIdParam idParam = new BmsCategoryIdParam();
        idParam.setId("test-id");
        assertEquals("test-id", idParam.getId());
    }

    @Test
    @DisplayName("参数对象-新增参数")
    void testAddParam_Properties() {
        BmsCategoryAddParam addParam = new BmsCategoryAddParam();
        addParam.setName("新分类");
        addParam.setCode("NEW_CAT");
        addParam.setParentId("0");
        addParam.setSortCode(1);

        assertEquals("新分类", addParam.getName());
        assertEquals("NEW_CAT", addParam.getCode());
        assertEquals("0", addParam.getParentId());
        assertEquals(1, addParam.getSortCode());
    }

    @Test
    @DisplayName("参数对象-编辑参数")
    void testEditParam_Properties() {
        BmsCategoryEditParam editParam = new BmsCategoryEditParam();
        editParam.setId("edit-id");
        editParam.setName("编辑分类");
        editParam.setCode("EDIT_CAT");
        editParam.setParentId("parent-id");

        assertEquals("edit-id", editParam.getId());
        assertEquals("编辑分类", editParam.getName());
        assertEquals("EDIT_CAT", editParam.getCode());
        assertEquals("parent-id", editParam.getParentId());
    }

    @Test
    @DisplayName("树形结构-子节点设置")
    void testCategoryTree_Children() {
        BmsCategory parent = new BmsCategory();
        parent.setId("parent-id");
        parent.setName("父分类");
        parent.setParentId("0");

        BmsCategory child = new BmsCategory();
        child.setId("child-id");
        child.setName("子分类");
        child.setParentId("parent-id");

        List<BmsCategory> children = new ArrayList<>();
        children.add(child);
        parent.setChildren(children);

        assertNotNull(parent.getChildren());
        assertEquals(1, parent.getChildren().size());
        assertEquals("子分类", parent.getChildren().get(0).getName());
    }

    @Test
    @DisplayName("分页结果-验证")
    void testPageResult_Validation() {
        Page<BmsCategory> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testCategory));
        mockPage.setTotal(1);

        assertNotNull(mockPage);
        assertEquals(1, mockPage.getTotal());
        assertEquals(1, mockPage.getRecords().size());
        assertEquals("测试分类", mockPage.getRecords().get(0).getName());
    }
}