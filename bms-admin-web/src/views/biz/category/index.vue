<template>
	<xn-panel>
		<a-form ref="searchFormRef" :model="searchFormState">
			<a-row :gutter="10">
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item label="分类名称" name="name">
						<a-input v-model:value="searchFormState.name" placeholder="请输入分类名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item label="上级分类" name="parentId">
						<a-tree-select
							v-model:value="searchFormState.parentId"
							placeholder="请选择上级分类"
							allow-clear
							:tree-data="treeData"
							:field-names="treeFieldNames"
							tree-line
						/>
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item label="状态" name="status">
						<a-select v-model:value="searchFormState.status" placeholder="请选择状态" :options="statusOptions" />
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('bizCategoryAdd')">
						<template #icon><PlusOutlined /></template>
						新增
					</a-button>
					<xn-batch-button
						v-if="hasPerm('bizCategoryBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchBizCategory"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'icon'">
					<span v-if="record.icon">{{ record.icon }}</span>
					<span v-else>-</span>
				</template>
				<template v-if="column.dataIndex === 'status'">
					<a-switch
						:loading="loading"
						:checked="record.status === 'ENABLE'"
						@change="editStatus(record)"
						v-if="hasPerm('bizCategoryUpdateStatus')"
					/>
					<span v-else>{{ $TOOL.dictTypeData('COMMON_STATUS', record.status) }}</span>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('bizCategoryEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['bizCategoryEdit', 'bizCategoryDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteBizCategory(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('bizCategoryDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</xn-panel>
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="bizCategory">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import bizCategoryApi from '@/api/biz/bizCategoryApi'

	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const loading = ref(false)
	const treeData = ref([])
	const treeFieldNames = { children: 'children', title: 'name', key: 'id', value: 'id' }

	const columns = [
		{ title: '分类名称', dataIndex: 'name' },
		{ title: '编码', dataIndex: 'code' },
		{ title: '图标', dataIndex: 'icon' },
		{ title: '排序', dataIndex: 'sortCode', sorter: true },
		{ title: '状态', dataIndex: 'status' },
		{ title: '创建时间', dataIndex: 'createTime' }
	]

	if (hasPerm(['bizCategoryEdit', 'bizCategoryDelete'])) {
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
				selectedRowKeys.value = []
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
		return bizCategoryApi.categoryPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}

	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}

	const deleteBizCategory = (record) => {
		let params = [{ id: record.id }]
		bizCategoryApi.categoryDelete(params).then(() => {
			message.success('删除成功')
			tableRef.value.refresh(true)
		}).catch((error) => {
			message.error('删除失败：' + (error.message || '未知错误'))
		})
	}

	const deleteBatchBizCategory = (params) => {
		bizCategoryApi.categoryDelete(params).then(() => {
			message.success('批量删除成功')
			tableRef.value.clearRefreshSelected()
		}).catch((error) => {
			message.error('批量删除失败：' + (error.message || '未知错误'))
		})
	}

	const editStatus = (record) => {
		loading.value = true
		if (record.status === 'ENABLE') {
			bizCategoryApi.categoryDisableStatus(record).finally(() => {
				tableRef.value.refresh()
				loading.value = false
			})
		} else {
			bizCategoryApi.categoryEnableStatus(record).finally(() => {
				tableRef.value.refresh()
				loading.value = false
			})
		}
	}

	const loadTreeData = () => {
		bizCategoryApi.categoryTree().then((res) => {
			if (res) {
				treeData.value = res
			}
		})
	}

	const statusOptions = tool.dictList('COMMON_STATUS')

	onMounted(() => {
		loadTreeData()
	})
</script>
