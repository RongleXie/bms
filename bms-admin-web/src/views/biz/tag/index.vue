<template>
	<xn-panel>
		<a-form ref="searchFormRef" :model="searchFormState">
			<a-row :gutter="10">
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item label="标签名称" name="name">
						<a-input v-model:value="searchFormState.name" placeholder="请输入标签名称" allow-clear />
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('bizTagAdd')">
						<template #icon><PlusOutlined /></template>
						新增
					</a-button>
					<xn-batch-button
						v-if="hasPerm('bizTagBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchBizTag"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'color'">
					<a-tag :color="record.color">{{ record.color }}</a-tag>
				</template>
				<template v-if="column.dataIndex === 'status'">
					<a-switch
						:loading="loading"
						:checked="record.status === 'ENABLE'"
						@change="editStatus(record)"
						v-if="hasPerm('bizTagUpdateStatus')"
					/>
					<span v-else>{{ $TOOL.dictTypeData('COMMON_STATUS', record.status) }}</span>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('bizTagEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['bizTagEdit', 'bizTagDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteBizTag(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('bizTagDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</xn-panel>
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="bizTag">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import bizTagApi from '@/api/biz/bizTagApi'

	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const loading = ref(false)

	const columns = [
		{ title: '标签名称', dataIndex: 'name' },
		{ title: '编码', dataIndex: 'code' },
		{ title: '颜色', dataIndex: 'color' },
		{ title: '排序', dataIndex: 'sortCode', sorter: true },
		{ title: '状态', dataIndex: 'status' },
		{ title: '创建时间', dataIndex: 'createTime' }
	]

	if (hasPerm(['bizTagEdit', 'bizTagDelete'])) {
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
		return bizTagApi.tagPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}

	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}

	const deleteBizTag = (record) => {
		let params = [{ id: record.id }]
		bizTagApi.tagDelete(params).then(() => {
			message.success('删除成功')
			tableRef.value.refresh(true)
		}).catch((error) => {
			message.error('删除失败：' + (error.message || '未知错误'))
		})
	}

	const deleteBatchBizTag = (params) => {
		bizTagApi.tagDelete(params).then(() => {
			message.success('批量删除成功')
			tableRef.value.clearRefreshSelected()
		}).catch((error) => {
			message.error('批量删除失败：' + (error.message || '未知错误'))
		})
	}

	const editStatus = (record) => {
		loading.value = true
		if (record.status === 'ENABLE') {
			bizTagApi.tagDisableStatus(record).finally(() => {
				tableRef.value.refresh()
				loading.value = false
			})
		} else {
			bizTagApi.tagEnableStatus(record).finally(() => {
				tableRef.value.refresh()
				loading.value = false
			})
		}
	}

	const statusOptions = tool.dictList('COMMON_STATUS')
</script>
