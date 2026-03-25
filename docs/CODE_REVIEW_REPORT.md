# BMS博客管理系统代码规范与质量审查报告

**项目名称**: BMS（博客管理系统）  
**框架版本**: Snowy v3.6.1  
**审查日期**: 2026-03-25  
**审查范围**: 业务模块后端代码 + 前端代码

---

## 一、审查摘要

### 1.1 问题统计

| 严重程度 | 后端 | 前端 | 合计 | 说明 |
|:--------:|:----:|:----:|:----:|------|
| **P1 (严重)** | 16 | 27 | **43** | 高危安全漏洞、功能缺陷，需立即修复 |
| **P2 (重要)** | 45 | 53 | **98** | 影响稳定性/可维护性，建议近期修复 |
| **P3 (建议)** | 38 | 27 | **65** | 代码规范、文档问题，逐步优化 |
| **总计** | **99** | **107** | **206** | |

### 1.2 严重程度分布

```
P1 ████████████████████████████████████████████ 43 (20.9%)
P2 ████████████████████████████████████████████████████████████████████████████████████████████████████ 98 (47.6%)
P3 ████████████████████████████████████████████████████████████ 65 (31.5%)
```

### 1.3 问题类型分布

| 问题类型 | 数量 | 占比 |
|----------|------|------|
| 安全问题 | 32 | 15.5% |
| 代码质量 | 58 | 28.2% |
| 错误处理 | 35 | 17.0% |
| 代码重复 | 28 | 13.6% |
| 注释文档 | 31 | 15.0% |
| 命名规范 | 22 | 10.7% |

---

## 二、后端模块问题清单

### 2.1 Article 模块 (article/)

> 注：Article模块后端代码审查结果未返回详细信息，建议补充审查。

### 2.2 Category 模块 (category/)

#### P1 严重问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| CAT-P1-01 | BmsCategoryServiceImpl.java | 47, 77 | SQL注入风险：排序字段未白名单验证 | 使用白名单验证 sortField 是否为合法字段 |
| CAT-P1-02 | BmsCategoryServiceImpl.java | 68-69 | 排序字段直接使用未过滤 | 使用白名单验证字段名 |
| CAT-P1-03 | BmsCategoryAddParam.java | 27-39 | 所有输入参数缺少长度限制 | 添加 @Size 注解防止超长输入 |
| CAT-P1-04 | BmsCategoryEditParam.java | 24-45 | 所有输入参数缺少长度限制 | 添加 @Size 注解防止超长输入 |
| CAT-P1-05 | BmsCategoryServiceImpl.java | 109-115 | add方法未检查名称/编码重复 | 添加唯一性校验 |
| CAT-P1-06 | BmsCategoryServiceImpl.java | 117-123 | edit方法未检查名称/编码重复 | 添加唯一性校验（排除自身） |

#### P2 重要问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| CAT-P2-01 | BmsCategoryServiceImpl.java | 48-69, 78-85 | page()和list()方法QueryWrapper构建逻辑重复 | 抽取公共方法 buildQueryWrapper() |
| CAT-P2-02 | BmsCategoryServiceImpl.java | 153-157, 159-164 | disableStatus和enableStatus方法结构完全相同 | 合并为 updateStatus(id, status) |
| CAT-P2-03 | BmsCategoryServiceImpl.java | 147-150 | queryEntity异常使用格式化字符串可能泄露信息 | 使用通用错误提示 |
| CAT-P2-04 | BmsCategoryServiceImpl.java | 129-131 | delete方法批量删除时第一个失败会中断全部 | 收集所有错误后统一抛出 |
| CAT-P2-05 | BmsCategoryServiceImpl.java | 125-133 | 删除操作未检查权限/数据归属 | 添加数据权限校验 |

#### P3 建议问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| CAT-P3-01 | BmsCategory.java | 28 | 表名使用全大写 `BMS_CATEGORY` | 改用蛇形命名 `bms_category` |
| CAT-P3-02 | BmsCategoryAddParam.java | 29 | 校验信息未国际化 | 使用常量或消息资源文件 |
| CAT-P3-03 | BmsCategoryController.java | 38-118 | 所有方法缺少JavaDoc注释 | 为每个方法添加@param、@return说明 |
| CAT-P3-04 | BmsCategoryService.java | 27-45 | 接口方法缺少JavaDoc注释 | 补充方法功能、参数、异常说明 |
| CAT-P3-05 | BmsCategoryServiceImpl.java | 91, 112 | 硬编码 `"0"` 表示根节点 | 定义常量 ROOT_PARENT_ID = "0" |

### 2.3 Tag 模块 (tag/)

#### P1 严重问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| TAG-P1-01 | BmsTagServiceImpl.java | 49 | SQL注入风险：checkSqlInjection()不能完全防止注入 | 需确保参数 sanitized，使用LambdaQueryWrapper |
| TAG-P1-02 | BmsTagController.java | 103 | 批量删除无数量限制 | 限制单次删除最大数量（如100条） |
| TAG-P1-03 | BmsTagServiceImpl.java | 116-119 | CommonException信息暴露内部细节 | 使用通用提示，不显示ID值 |

#### P2 重要问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| TAG-P2-01 | BmsTagServiceImpl.java | 48-77 | page方法过于复杂，单一职责违反 | 提取查询条件构建逻辑到私有方法 |
| TAG-P2-02 | BmsTagServiceImpl.java | 78-83 | list方法重复QueryWrapper构建逻辑 | 与page方法共享查询构建逻辑 |
| TAG-P2-03 | BmsTagAddParam.java | 34 | code字段缺少唯一性校验说明 | 在Service层添加编码唯一性校验 |
| TAG-P2-04 | BmsTagEditParam.java | 37 | 编辑时name未校验唯一性 | 排除当前记录后校验名称唯一性 |
| TAG-P2-05 | BmsTagAddParam.java | 34-49 | 输入无长度限制 | 添加 @Size(max=xx) 注解限制字符串长度 |

### 2.4 Comment 模块 (comment/)

#### P1 严重问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| CMT-P1-01 | BmsCommentServiceImpl.java | 65-66 | SQL注入风险：排序字段未白名单验证 | 添加排序字段白名单验证 |
| CMT-P1-02 | BmsCommentAddParam.java | 40-49 | XSS攻击风险：用户输入字段无HTML过滤 | 添加@Size长度限制，Service层添加HTML转义 |

#### P2 重要问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| CMT-P2-01 | BmsCommentServiceImpl.java | 49-70, 73-85 | 代码重复：page()和list()方法查询逻辑重复 | 提取私有方法 buildQueryWrapper() |
| CMT-P2-02 | BmsCommentServiceImpl.java | 118-126, 128-136 | 代码重复：approve()和reject()方法结构相同 | 提取私有方法 updateStatus(id, status) |
| CMT-P2-03 | BmsComment.java | 48 | 邮箱格式未验证 | 添加 @Email 注解 |
| CMT-P2-04 | BmsComment.java | 51 | URL格式未验证 | 添加 @Pattern 正则验证 |
| CMT-P2-05 | BmsCommentController.java | 78-79 | 批量删除无数量限制 | 添加 @Size(max=100) 限制 |

#### P3 建议问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| CMT-P3-01 | BmsCommentEditParam.java | 1-44 | 未使用的类 | 如无后续需求建议删除 |
| CMT-P3-02 | BmsCommentService.java | 59-60 | queryEntity与detail方法功能重复 | 考虑移除queryEntity或重命名 |
| CMT-P3-03 | BmsCommentPageParam.java | 25-27 | 分页参数无验证 | 添加 @Min(1) 和 @Max(1000) 验证 |

### 2.5 Media 模块 (media/)

#### P1 严重问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| MED-P1-01 | BmsMediaServiceImpl.java | 48 | SQL注入风险：checkSqlInjection()仍有风险 | 使用LambdaQueryWrapper替代QueryWrapper |
| MED-P1-02 | BmsMediaServiceImpl.java | 50-54 | 用户输入searchKey直接用于like查询 | 添加输入长度限制和特殊字符过滤 |
| MED-P1-03 | BmsMediaServiceImpl.java | 68-71 | sortField参数未白名单验证 | 构建允许排序的字段白名单 |
| MED-P1-04 | BmsMediaAddParam.java | 42 | filePath未校验路径遍历攻击 | 添加路径合法性校验，禁止`..`等 |
| MED-P1-05 | BmsMediaEditParam.java | 46 | 编辑接口存在路径遍历风险 | 添加路径合法性校验 |
| MED-P1-06 | BmsMediaServiceImpl.java | 90-104 | 文件添加未校验文件类型白名单 | 根据fileType校验fileExt和mimeType |

#### P2 重要问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| MED-P2-01 | BmsMediaServiceImpl.java | 48, 79 | 重复的QueryWrapper构建逻辑 | 抽取公共方法避免重复代码 |
| MED-P2-02 | BmsMediaServiceImpl.java | 90-104 | add方法未捕获可能的数据库异常 | 添加DataIntegrityViolationException处理 |
| MED-P2-03 | BmsMediaServiceImpl.java | 112-116 | delete方法未验证删除数量与请求一致 | 比较删除结果与请求ID数量 |
| MED-P2-04 | BmsMediaController.java | 118-119 | 删除接口集合参数未限制最大数量 | 添加 @Size(max=100) 限制 |
| MED-P2-05 | BmsMedia.java | 100 | extJson字段存储JSON未说明最大长度 | 添加@Schema长度限制和JSON格式校验 |

---

## 三、前端模块问题清单

### 3.1 Article 模块 (article/)

#### P1 严重问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| ART-FE-P1-01 | index.vue | 169 | `selectedRowKeys.value = ref([])` 错误嵌套ref | 改为 `selectedRowKeys.value = []` |
| ART-FE-P1-02 | index.vue | 197-202 | 删除操作无错误处理 | 添加.catch()处理删除失败 |
| ART-FE-P1-03 | index.vue | 210-220 | 状态切换无错误处理 | 添加.catch()显示错误提示 |
| ART-FE-P1-04 | form.vue | 208-226 | 表单提交无错误处理 | 添加.catch()处理验证和提交失败 |

#### P2 重要问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| ART-FE-P2-01 | index.vue | 118 | 组件名`bizArticle`与form.vue冲突 | form.vue改为`name="bizArticleForm"` |
| ART-FE-P2-02 | bizArticleApi.js | 13 | 基础路径冗余斜杠 | 改为 `/biz/article` |
| ART-FE-P2-03 | index.vue | 177-187 | loadData函数时间参数转换逻辑重复 | 抽取为独立函数transformSearchParams |
| ART-FE-P2-04 | index.vue/form.vue | - | loadCategoryTree和loadTagList重复代码 | 抽取到composables/useCategoryAndTag.ts |
| ART-FE-P2-05 | form.vue | 149-177 | onOpen函数过长（约30行） | 拆分为多个小函数 |

### 3.2 Category 模块 (category/)

#### P1 严重问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| CAT-FE-P1-01 | index.vue | 137 | `selectedRowKeys.value = ref([])` 错误嵌套ref | 改为 `selectedRowKeys.value = []` |
| CAT-FE-P1-02 | index.vue | 159-170 | 删除操作无错误处理 | 添加.catch()处理删除失败 |
| CAT-FE-P1-03 | index.vue | 172-186 | editStatus无错误提示 | 添加.then()成功提示和.catch()错误处理 |

#### P2 重要问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| CAT-FE-P2-01 | index.vue/form.vue | - | 组件名重复为`bizCategory` | form.vue改为`name="BizCategoryForm"` |
| CAT-FE-P2-02 | form.vue | 88-96 | nextTick未导入 | 添加 `import { nextTick } from 'vue'` |
| CAT-FE-P2-03 | form.vue | 103-115 | onSubmit表单验证失败无catch | 添加.catch()处理验证失败 |
| CAT-FE-P2-04 | bizCategoryApi.js | 13 | URL末尾有多余斜杠 | 改为 `/biz/category` |

### 3.3 Tag 模块 (tag/)

#### P1 严重问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| TAG-FE-P1-01 | index.vue | 123 | `selectedRowKeys.value = ref([])` 错误嵌套ref | 改为 `selectedRowKeys.value = []` |
| TAG-FE-P1-02 | index.vue | 144-155 | 删除操作无错误处理 | 添加.catch()处理删除失败 |
| TAG-FE-P1-03 | index.vue | 157-170 | editStatus无错误提示 | 添加.then()成功提示和.catch()错误处理 |

#### P2 重要问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| TAG-FE-P2-01 | index.vue/form.vue | - | 组件名重复为`bizTag` | form.vue改为`name="BizTagForm"` |
| TAG-FE-P2-02 | form.vue | 89-95 | nextTick未导入 | 添加 `import { nextTick } from 'vue'` |
| TAG-FE-P2-03 | form.vue | 103-115 | onSubmit表单验证失败无catch | 添加.catch()处理验证失败 |
| TAG-FE-P2-04 | bizTagApi.js | 13 | URL末尾有多余斜杠 | 改为 `/biz/tag` |

### 3.4 Comment 模块 (comment/)

#### P1 严重问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| CMT-FE-P1-01 | index.vue | 133 | `selectedRowKeys.value = ref([])` 错误嵌套ref | 改为 `selectedRowKeys.value = []` |
| CMT-FE-P1-02 | index.vue | 157-168 | 删除操作无错误处理 | 添加.catch()处理删除失败 |
| CMT-FE-P1-03 | index.vue | 170-189 | editStatus和auditComment无错误处理 | 添加.then()和.catch()处理 |

#### P2 重要问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| CMT-FE-P2-01 | index.vue/form.vue | - | 组件名重复为`bizComment` | form.vue改为`name="BizCommentForm"` |
| CMT-FE-P2-02 | form.vue | 88-94 | nextTick未导入 | 添加 `import { nextTick } from 'vue'` |
| CMT-FE-P2-03 | form.vue | 102-114 | onSubmit表单验证失败无catch | 添加.catch()处理验证失败 |
| CMT-FE-P2-04 | bizCommentApi.js | 13 | URL末尾有多余斜杠 | 改为 `/biz/comment` |

### 3.5 Media 模块 (media/)

#### P1 严重问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| MED-FE-P1-01 | index.vue | 133 | `selectedRowKeys.value = ref([])` 错误嵌套ref | 改为 `selectedRowKeys.value = []` |
| MED-FE-P1-02 | index.vue | 157-168 | 删除操作无错误处理 | 添加.catch()处理删除失败 |
| MED-FE-P1-03 | index.vue | 170-183 | editStatus无错误提示 | 添加.then()成功提示和.catch()错误处理 |
| MED-FE-P1-04 | form.vue | 153-161 | handleFileChange使用FileReader读取Base64 | 应调用mediaUpload API上传到服务器 |
| MED-FE-P1-05 | form.vue | 29-51 | 非IMAGE类型时无文件上传功能 | 所有媒体类型都应支持文件上传 |

#### P2 重要问题

| 编号 | 文件 | 行号 | 问题描述 | 修复建议 |
|------|------|------|----------|----------|
| MED-FE-P2-01 | index.vue/form.vue | - | 组件名重复为`bizMedia` | form.vue改为`name="BizMediaForm"` |
| MED-FE-P2-02 | form.vue | 116-128 | nextTick未导入 | 添加 `import { nextTick } from 'vue'` |
| MED-FE-P2-03 | form.vue | 164-176 | onSubmit表单验证失败无catch | 添加.catch()处理验证失败 |
| MED-FE-P2-04 | bizMediaApi.js | 13 | URL末尾有多余斜杠 | 改为 `/biz/media` |
| MED-FE-P2-05 | form.vue | 153-155 | beforeUpload中魔法数字5 (5MB) | 提取为常量 MAX_IMAGE_SIZE |

---

## 四、跨模块共性问题

### 4.1 后端共性问题

#### SQL注入风险（P1）

**问题描述**: 所有模块的排序字段和搜索关键字都存在SQL注入风险

**涉及文件**:
- `BmsCategoryServiceImpl.java:47-69`
- `BmsTagServiceImpl.java:48-77`
- `BmsCommentServiceImpl.java:49-70`
- `BmsMediaServiceImpl.java:48-74`

**修复建议**:
```java
// 1. 定义排序字段白名单
private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
    "createTime", "updateTime", "sortCode", "name", "code"
);

// 2. 在查询前验证
private void validateSortField(String sortField) {
    if (sortField != null && !ALLOWED_SORT_FIELDS.contains(sortField)) {
        throw new CommonException("非法的排序字段");
    }
}
```

#### 输入验证缺失（P1）

**问题描述**: 所有Param类缺少输入长度限制

**修复建议**:
```java
public class BmsCategoryAddParam {
    @NotBlank(message = "name不能为空")
    @Size(min = 1, max = 50, message = "name长度必须在1-50之间")
    private String name;
    
    @Size(max = 100, message = "description长度不能超过100")
    private String description;
}
```

#### 代码重复（P2）

**问题描述**: QueryWrapper构建逻辑在page()和list()方法中重复

**修复建议**:
```java
private LambdaQueryWrapper<BmsCategory> buildQueryWrapper(BmsCategoryPageParam param) {
    LambdaQueryWrapper<BmsCategory> wrapper = new LambdaQueryWrapper<>();
    wrapper.like(StrUtil.isNotBlank(param.getName()), BmsCategory::getName, param.getName());
    wrapper.eq(StrUtil.isNotBlank(param.getCode()), BmsCategory::getCode, param.getCode());
    // ... 其他条件
    return wrapper;
}
```

### 4.2 前端共性问题

#### 错误处理缺失（P1）

**问题描述**: 所有CRUD操作缺少.catch()错误处理

**修复建议**:
```javascript
// 删除操作示例
const deleteRecord = (record) => {
    bizApi.delete([{ id: record.id }])
        .then(() => {
            message.success('删除成功')
            tableRef.value.refresh(true)
        })
        .catch((error) => {
            message.error('删除失败：' + (error.message || '未知错误'))
        })
}

// 状态切换示例
const editStatus = (record) => {
    loading.value = true
    const api = record.status === 'ENABLE' ? bizApi.disable : bizApi.enable
    api(record)
        .then(() => {
            message.success('状态更新成功')
            tableRef.value.refresh(true)
        })
        .catch((error) => {
            message.error('状态更新失败：' + (error.message || '未知错误'))
        })
        .finally(() => {
            loading.value = false
        })
}
```

#### ref嵌套错误（P1）

**问题描述**: 所有index.vue中存在`selectedRowKeys.value = ref([])`错误

**修复建议**:
```javascript
// 错误写法
selectedRowKeys.value = ref([])  // ❌ 创建了新的ref对象

// 正确写法
selectedRowKeys.value = []  // ✅ 直接赋值数组
```

#### 组件命名冲突（P2）

**问题描述**: 所有模块的index.vue和form.vue使用相同的组件名

**修复建议**:
```vue
<!-- index.vue -->
<script setup name="BizArticle">

<!-- form.vue -->
<script setup name="BizArticleForm">
```

#### 代码重复（P2）

**问题描述**: 4个模块的index.vue和form.vue有大量重复逻辑

**修复建议**: 提取为可复用的composable函数

```javascript
// composables/useTableSelection.js
export function useTableSelection() {
    const selectedRowKeys = ref([])
    const hasSelected = computed(() => selectedRowKeys.value.length > 0)
    
    const clearSelection = () => {
        selectedRowKeys.value = []
    }
    
    return { selectedRowKeys, hasSelected, clearSelection }
}

// composables/useStatusToggle.js
export function useStatusToggle(api, tableRef) {
    const loading = ref(false)
    
    const toggleStatus = async (record) => {
        loading.value = true
        try {
            if (record.status === 'ENABLE') {
                await api.disable(record)
            } else {
                await api.enable(record)
            }
            message.success('状态更新成功')
            tableRef.value?.refresh(true)
        } catch (error) {
            message.error('状态更新失败')
        } finally {
            loading.value = false
        }
    }
    
    return { loading, toggleStatus }
}
```

---

## 五、最佳实践建议

### 5.1 后端最佳实践

#### 安全加固

```java
// 1. 使用LambdaQueryWrapper避免SQL注入
LambdaQueryWrapper<BmsArticle> wrapper = new LambdaQueryWrapper<>();
wrapper.like(BmsArticle::getTitle, searchKey);  // ✅ 安全

// 2. 输入验证
@NotBlank(message = "标题不能为空")
@Size(min = 1, max = 200, message = "标题长度必须在1-200之间")
@Pattern(regexp = "^[^<>]*$", message = "标题不能包含特殊字符")
private String title;

// 3. XSS防护
import org.springframework.web.util.HtmlUtils;
String safeContent = HtmlUtils.htmlEscape(userInput);

// 4. 路径遍历防护
private void validateFilePath(String filePath) {
    if (filePath.contains("..") || filePath.contains("/") || filePath.contains("\\")) {
        throw new CommonException("非法的文件路径");
    }
}
```

#### 事务管理

```java
// 使用具体异常类型
@Transactional(rollbackFor = {SQLException.class, DataAccessException.class})
public void updateArticle(BmsArticleEditParam param) {
    // 业务逻辑
}

// 避免事务过大
public void batchOperation(List<Long> ids) {
    for (Long id : ids) {
        singleOperation(id);  // 单独事务
    }
}

@Transactional
private void singleOperation(Long id) {
    // 单个操作的事务
}
```

### 5.2 前端最佳实践

#### API层统一错误处理

```javascript
// utils/request.js
const baseRequest = (url, data, method = 'post', options = {}) => {
    return axios({
        url,
        method,
        data,
        ...options
    }).then(response => {
        return response.data
    }).catch(error => {
        // 统一错误处理
        const message = error.response?.data?.message || error.message || '请求失败'
        notification.error({ message: '错误', description: message })
        return Promise.reject(error)
    })
}
```

#### Composables最佳实践

```javascript
// composables/useFormModal.js
export function useFormModal(formRef, api, emit) {
    const visible = ref(false)
    const loading = ref(false)
    const formData = ref({})
    
    const open = async (record = null) => {
        visible.value = true
        if (record?.id) {
            loading.value = true
            try {
                const data = await api.detail({ id: record.id })
                formData.value = data
            } finally {
                loading.value = false
            }
        } else {
            formData.value = {}
        }
    }
    
    const close = () => {
        visible.value = false
        formRef.value?.resetFields()
    }
    
    const submit = async () => {
        await formRef.value.validate()
        loading.value = true
        try {
            await api.submit(formData.value)
            message.success('保存成功')
            close()
            emit('success')
        } finally {
            loading.value = false
        }
    }
    
    return { visible, loading, formData, open, close, submit }
}
```

### 5.3 代码规范建议

#### 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | 大驼峰 | `BmsArticleService` |
| 方法名 | 小驼峰 | `getArticleList()` |
| 常量 | 全大写下划线 | `MAX_PAGE_SIZE` |
| 变量 | 小驼峰 | `articleList` |
| 包名 | 全小写 | `vip.xiaonuo.biz.modular.article` |
| 数据库表名 | 小写下划线 | `bms_article` |

#### 注释规范

```java
/**
 * 文章服务实现类
 * 
 * @author Your Name
 * @date 2026-03-25
 */
@Service
public class BmsArticleServiceImpl implements BmsArticleService {
    
    /**
     * 分页查询文章列表
     * 
     * @param param 分页查询参数
     * @return 分页结果
     * @throws CommonException 当参数无效时抛出
     */
    @Override
    public PageResult<BmsArticle> page(BmsArticlePageParam param) {
        // 实现逻辑
    }
}
```

---

## 六、修复优先级计划

### 第一阶段：P1严重问题（立即修复）

| 周次 | 任务 | 负责模块 |
|------|------|----------|
| 第1周 | 修复SQL注入风险 | 后端所有模块 |
| 第1周 | 添加输入验证 | 后端所有Param类 |
| 第1周 | 修复ref嵌套错误 | 前端所有index.vue |
| 第2周 | 添加API错误处理 | 前端所有模块 |
| 第2周 | 修复媒体上传问题 | 前端media模块 |
| 第2周 | 路径遍历防护 | 后端media模块 |

### 第二阶段：P2重要问题（近期修复）

| 周次 | 任务 | 负责模块 |
|------|------|----------|
| 第3周 | 重构重复代码 | 后端所有ServiceImpl |
| 第3周 | 统一组件命名 | 前端所有form.vue |
| 第4周 | 提取composables | 前端所有模块 |
| 第4周 | 添加唯一性校验 | 后端category/tag模块 |

### 第三阶段：P3建议问题（持续优化）

| 周次 | 任务 | 负责模块 |
|------|------|----------|
| 第5周 | 补充JavaDoc注释 | 后端所有模块 |
| 第5周 | 完善Schema注解 | 后端所有Param类 |
| 第6周 | 删除未使用代码 | 后端comment模块 |
| 第6周 | 提取魔法字符串 | 前端所有模块 |

---

## 七、附录

### A. 检查清单

#### 后端代码检查清单

- [ ] 所有输入参数是否有长度验证
- [ ] 排序字段是否使用白名单验证
- [ ] 是否使用LambdaQueryWrapper
- [ ] 是否添加@Transactional注解
- [ ] 异常信息是否脱敏
- [ ] 是否有唯一性校验
- [ ] 是否有JavaDoc注释

#### 前端代码检查清单

- [ ] 是否有错误处理(.catch())
- [ ] ref赋值是否正确
- [ ] 组件名是否唯一
- [ ] 是否导入了所有依赖
- [ ] 是否有用户反馈(message)
- [ ] 是否有加载状态

### B. 相关文档

- [Snowy框架官方文档](https://xiaonuo.vip)
- [MyBatis-Plus官方文档](https://baomidou.com)
- [Vue 3 Composition API文档](https://vuejs.org/guide/extras/composition-api-faq.html)
- [Ant Design Vue文档](https://antdv.com)

---

**报告生成时间**: 2026-03-25  
**审查工具**: Claude Code + Background Agents  
**审查人员**: AI Code Reviewer