<template>
	<xn-panel>
		<a-card :bordered="false" style="margin-bottom: 16px">
			<a-input-search
				v-model:value="globalKeyword"
				placeholder="搜索文章标题、内容..."
				enter-button="全文搜索"
				size="large"
				allow-clear
				@search="handleGlobalSearch"
				@change="handleSearchChange"
			>
				<template #prefix>
					<SearchOutlined />
				</template>
			</a-input-search>
			<div v-if="suggestions.length > 0" class="search-suggestions">
				<a-tag v-for="item in suggestions" :key="item" @click="handleSelectSuggestion(item)" style="cursor: pointer; margin: 4px">
					{{ item }}
				</a-tag>
			</div>
		</a-card>
		<a-form ref="searchFormRef" :model="searchFormState">
			<a-row :gutter="10">
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item label="标题" name="title">
						<a-input v-model:value="searchFormState.title" placeholder="请输入标题" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item label="分类" name="categoryId">
						<a-tree-select
							v-model:value="searchFormState.categoryId"
							placeholder="请选择分类"
							allow-clear
							:tree-data="categoryTreeData"
							:field-names="treeFieldNames"
							tree-line
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item label="标签" name="tagId">
						<a-select v-model:value="searchFormState.tagId" placeholder="请选择标签" allow-clear :options="tagOptions" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6" v-show="advanced">
					<a-form-item label="状态" name="status">
						<a-select v-model:value="searchFormState.status" placeholder="请选择状态" :options="statusOptions" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6" v-show="advanced">
					<a-form-item label="创建时间" name="createTime">
						<a-range-picker v-model:value="searchFormState.createTime" value-format="YYYY-MM-DD" show-time />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item>
						<a-space>
							<a-button type="primary" @click="tableRef.refresh(true)">
								<template #icon><SearchOutlined /></template>
								查询
							</a-button>
							<a-button @click="reset">
								<template #icon><RedoOutlined /></template>
								重置
							</a-button>
							<a @click="toggleAdvanced">
								{{ advanced ? '收起' : '展开' }}
								<component :is="advanced ? 'UpOutlined' : 'DownOutlined'" />
							</a>
						</a-space>
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:row-selection="options.rowSelection"
			:scroll="{ x: 'max-content' }"
		>
			<template #operator>
				<a-space>
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('bizArticleAdd')">
						<template #icon><PlusOutlined /></template>
						新增
					</a-button>
					<xn-batch-button
						v-if="hasPerm('bizArticleBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchBizArticle"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'coverImage'">
					<a-image v-if="record.coverImage" :src="record.coverImage" style="width: 60px; height: 60px" />
					<span v-else>无封面</span>
				</template>
				<template v-if="column.dataIndex === 'categoryName'">
					<a-tag color="blue">{{ record.categoryName }}</a-tag>
				</template>
				<template v-if="column.dataIndex === 'tags'">
					<a-tag v-for="tag in JSON.parse(record.tags || '[]')" :key="tag" color="green">
						{{ tag }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'status'">
					<a-tag v-if="record.status === 'DRAFT'" color="default">草稿</a-tag>
					<a-tag v-else-if="record.status === 'PUBLISHED'" color="success">已发布</a-tag>
					<a-tag v-else-if="record.status === 'SCHEDULED'" color="processing">待发布</a-tag>
					<a-tag v-else color="warning">{{ record.status }}</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="detailRef.onOpen(record)" v-if="hasPerm('bizArticleView')">预览</a>
						<a-divider type="vertical" v-if="hasPerm(['bizArticleView', 'bizArticleEdit'], 'and')" />
						<a @click="formRef.onOpen(record)" v-if="hasPerm('bizArticleEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['bizArticleEdit', 'bizArticleVersion'], 'and')" />
						<a @click="versionRef.onOpen(record.id)" v-if="hasPerm('bizArticleVersion')">版本</a>
						<a-divider type="vertical" v-if="hasPerm(['bizArticleVersion', 'bizArticleDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteBizArticle(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('bizArticleDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</xn-panel>
	<Form ref="formRef" @successful="tableRef.refresh()" />
	<Detail ref="detailRef" />
	<Version ref="versionRef" @successful="tableRef.refresh()" />
</template>

<style scoped>
.search-suggestions {
	margin-top: 8px;
}
</style>

<script setup name="bizArticle">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import Detail from './detail.vue'
	import Version from './version.vue'
	import bizArticleApi from '@/api/biz/bizArticleApi'
	import bizCategoryApi from '@/api/biz/bizCategoryApi'
	import bizTagApi from '@/api/biz/bizTagApi'
	import bizArticleSearchApi from '@/api/biz/bizArticleSearchApi'

	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const formRef = ref()
	const detailRef = ref()
	const versionRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const loading = ref(false)
	const advanced = ref(false)
	const categoryTreeData = ref([])
	const tagOptions = ref([])
	const treeFieldNames = { children: 'children', title: 'name', key: 'id', value: 'id' }
	const globalKeyword = ref('')
	const suggestions = ref([])
	const isGlobalSearch = ref(false)

	const handleGlobalSearch = (value) => {
		if (!value) {
			isGlobalSearch.value = false
			tableRef.value.refresh(true)
			return
		}
		isGlobalSearch.value = true
		searchFormState.value = { title: value }
		tableRef.value.refresh(true)
	}

	const handleSearchChange = (e) => {
		const value = e.target.value
		if (value && value.length >= 2) {
			bizArticleSearchApi.suggest({ prefix: value, limit: 5 }).then((data) => {
				suggestions.value = data || []
			})
		} else {
			suggestions.value = []
		}
	}

	const handleSelectSuggestion = (item) => {
		globalKeyword.value = item
		suggestions.value = []
		handleGlobalSearch(item)
	}

	const toggleAdvanced = () => {
		advanced.value = !advanced.value
	}

	const columns = [
		{ title: '标题', dataIndex: 'title', ellipsis: true },
		{ title: '封面图', dataIndex: 'coverImage' },
		{ title: '分类', dataIndex: 'categoryName' },
		{ title: '标签', dataIndex: 'tags' },
		{ title: '作者', dataIndex: 'author', ellipsis: true },
		{ title: '阅读量', dataIndex: 'viewCount', sorter: true },
		{ title: '排序', dataIndex: 'sortCode', sorter: true },
		{ title: '状态', dataIndex: 'status' },
		{ title: '创建时间', dataIndex: 'createTime' }
	]

	if (hasPerm(['bizArticleView', 'bizArticleEdit', 'bizArticleDelete'])) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			fixed: 'right'
		})
	}

	const selectedRowKeys = ref([])
	const options = {
		alert: {
			show: false,
			clear: () => {
				selectedRowKeys.value = ref([])
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}

	const loadData = (parameter) => {
		const searchFormParam = cloneDeep(searchFormState.value)
		if (searchFormParam.createTime) {
			searchFormParam.startCreateTime = searchFormParam.createTime[0]
			searchFormParam.endCreateTime = searchFormParam.createTime[1]
			delete searchFormParam.createTime
		}
		return bizArticleApi.articlePage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}

	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}

	const deleteBizArticle = (record) => {
		let params = [{ id: record.id }]
		bizArticleApi.articleDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}

	const deleteBatchBizArticle = (params) => {
		bizArticleApi.articleDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}

	const editStatus = (record) => {
		loading.value = true
		if (record.status === 'ENABLE') {
			bizArticleApi.articleDisableStatus(record).finally(() => {
				tableRef.value.refresh()
				loading.value = false
			})
		} else {
			bizArticleApi.articleEnableStatus(record).finally(() => {
				tableRef.value.refresh()
				loading.value = false
			})
		}
	}

	const loadCategoryTree = () => {
		bizCategoryApi.categoryTree().then((res) => {
			if (res) {
				categoryTreeData.value = res
			}
		})
	}

	const loadTagList = () => {
		bizTagApi.tagList().then((res) => {
			if (res) {
				tagOptions.value = res.map((item) => ({
					value: item.id,
					label: item.name
				}))
			}
		})
	}

	const statusOptions = tool.dictList('COMMON_STATUS')

	onMounted(() => {
		loadCategoryTree()
		loadTagList()
	})
</script>
