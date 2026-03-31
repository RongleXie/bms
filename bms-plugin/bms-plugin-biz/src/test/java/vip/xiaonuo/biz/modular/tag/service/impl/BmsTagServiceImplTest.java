/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 */
package vip.xiaonuo.biz.modular.tag.service.impl;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    @DisplayName("标签状态枚举-启用")
    void testTagStatus_Enable() {
        assertEquals("ENABLE", BmsTagStatusEnum.ENABLE.getValue());
    }

    @Test
    @DisplayName("标签状态枚举-禁用")
    void testTagStatus_Disabled() {
        assertEquals("DISABLED", BmsTagStatusEnum.DISABLED.getValue());
    }

    @Test
    @DisplayName("标签实体-属性设置和获取")
    void testTagEntity_Properties() {
        BmsTag tag = new BmsTag();
        tag.setId("new-id");
        tag.setName("新标签");
        tag.setCode("NEW_TAG");
        tag.setColor("#00FF00");
        tag.setStatus(BmsTagStatusEnum.ENABLE.getValue());
        tag.setSortCode(100);
        tag.setDescription("标签描述");

        assertEquals("new-id", tag.getId());
        assertEquals("新标签", tag.getName());
        assertEquals("NEW_TAG", tag.getCode());
        assertEquals("#00FF00", tag.getColor());
        assertEquals("ENABLE", tag.getStatus());
        assertEquals(100, tag.getSortCode());
        assertEquals("标签描述", tag.getDescription());
    }

    @Test
    @DisplayName("参数对象-分页参数")
    void testPageParam_Properties() {
        BmsTagPageParam pageParam = new BmsTagPageParam();
        pageParam.setName("测试标签");
        pageParam.setCode("TEST_TAG");
        pageParam.setStatus("ENABLE");

        assertEquals("测试标签", pageParam.getName());
        assertEquals("TEST_TAG", pageParam.getCode());
        assertEquals("ENABLE", pageParam.getStatus());
    }

    @Test
    @DisplayName("参数对象-ID参数")
    void testIdParam_Properties() {
        BmsTagIdParam idParam = new BmsTagIdParam();
        idParam.setId("test-id");
        assertEquals("test-id", idParam.getId());
    }

    @Test
    @DisplayName("参数对象-新增参数")
    void testAddParam_Properties() {
        BmsTagAddParam addParam = new BmsTagAddParam();
        addParam.setName("新标签");
        addParam.setCode("NEW_TAG");
        addParam.setColor("#FF0000");
        addParam.setSortCode(1);

        assertEquals("新标签", addParam.getName());
        assertEquals("NEW_TAG", addParam.getCode());
        assertEquals("#FF0000", addParam.getColor());
        assertEquals(1, addParam.getSortCode());
    }

    @Test
    @DisplayName("参数对象-编辑参数")
    void testEditParam_Properties() {
        BmsTagEditParam editParam = new BmsTagEditParam();
        editParam.setId("edit-id");
        editParam.setName("编辑标签");
        editParam.setCode("EDIT_TAG");
        editParam.setColor("#0000FF");

        assertEquals("edit-id", editParam.getId());
        assertEquals("编辑标签", editParam.getName());
        assertEquals("EDIT_TAG", editParam.getCode());
        assertEquals("#0000FF", editParam.getColor());
    }

    @Test
    @DisplayName("分页结果-验证")
    void testPageResult_Validation() {
        Page<BmsTag> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testTag));
        mockPage.setTotal(1);

        assertNotNull(mockPage);
        assertEquals(1, mockPage.getTotal());
        assertEquals(1, mockPage.getRecords().size());
        assertEquals("测试标签", mockPage.getRecords().get(0).getName());
    }

    @Test
    @DisplayName("颜色格式验证-十六进制")
    void testColorFormat_Hex() {
        String[] validColors = {"#FF5733", "#00FF00", "#0000FF", "#FFFFFF", "#000000"};
        for (String color : validColors) {
            assertTrue(color.matches("^#[0-9A-Fa-f]{6}$"));
        }
    }
}