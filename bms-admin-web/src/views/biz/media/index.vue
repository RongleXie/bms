<template>
	<xn-panel>
		<a-form ref="searchFormRef" :model="searchFormState">
			<a-row :gutter="10">
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item label="媒体名称" name="name">
						<a-input v-model:value="searchFormState.name" placeholder="请输入媒体名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item label="媒体类型" name="mediaType">
						<a-select v-model:value="searchFormState.mediaType" placeholder="请选择媒体类型" :options="mediaTypeOptions" />
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
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('bizMediaAdd')">
						<template #icon><PlusOutlined /></template>
						新增
					</a-button>
					<xn-batch-button
						v-if="hasPerm('bizMediaBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchBizMedia"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'url'">
					<a-image v-if="record.mediaType === 'IMAGE'" :src="record.url" style="width: 80px; height: 60px; object-fit: cover" />
					<a-video v-else-if="record.mediaType === 'VIDEO'" :src="record.url" style="width: 120px; height: 60px" controls />
					<a v-else :href="record.url" target="_blank">下载</a>
				</template>
				<template v-if="column.dataIndex === 'mediaType'">
					<a-tag :color="getMediaTypeColor(record.mediaType)">
						{{ getMediaTypeLabel(record.mediaType) }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'status'">
					<a-switch
						:loading="loading"
						:checked="record.status === 'ENABLE'"
						@change="editStatus(record)"
						v-if="hasPerm('bizMediaUpdateStatus')"
					/>
					<span v-else>{{ $TOOL.dictTypeData('COMMON_STATUS', record.status) }}</span>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)" v-if="hasPerm('bizMediaEdit')">编辑</a>
						<a-divider type="vertical" v-if="hasPerm(['bizMediaEdit', 'bizMediaDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteBizMedia(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('bizMediaDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</xn-panel>
	<Form ref="formRef" @successful="tableRef.refresh()" />
</template>

<script setup name="bizMedia">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import bizMediaApi from '@/api/biz/bizMediaApi'

	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const loading = ref(false)

	const columns = [
		{ title: '媒体名称', dataIndex: 'name' },
		{ title: '预览', dataIndex: 'url' },
		{ title: '媒体类型', dataIndex: 'mediaType' },
		{ title: '文件大小', dataIndex: 'fileSize' },
		{ title: '排序', dataIndex: 'sortCode', sorter: true },
		{ title: '状态', dataIndex: 'status' },
		{ title: '创建时间', dataIndex: 'createTime' }
	]

	if (hasPerm(['bizMediaEdit', 'bizMediaDelete'])) {
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
		return bizMediaApi.mediaPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}

	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}

	const deleteBizMedia = (record) => {
		let params = [{ id: record.id }]
		bizMediaApi.mediaDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}

	const deleteBatchBizMedia = (params) => {
		bizMediaApi.mediaDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}

	const editStatus = (record) => {
		loading.value = true
		if (record.status === 'ENABLE') {
			bizMediaApi.mediaDisableStatus(record).finally(() => {
				tableRef.value.refresh()
				loading.value = false
			})
		} else {
			bizMediaApi.mediaEnableStatus(record).finally(() => {
				tableRef.value.refresh()
				loading.value = false
			})
		}
	}

	const getMediaTypeLabel = (type) => {
		const typeMap = {
			IMAGE: '图片',
			VIDEO: '视频',
			AUDIO: '音频',
			FILE: '文件'
		}
		return typeMap[type] || type
	}

	const getMediaTypeColor = (type) => {
		const colorMap = {
			IMAGE: 'blue',
			VIDEO: 'purple',
			AUDIO: 'green',
			FILE: 'orange'
		}
		return colorMap[type] || 'default'
	}

	const mediaTypeOptions = [
		{ value: 'IMAGE', label: '图片' },
		{ value: 'VIDEO', label: '视频' },
		{ value: 'AUDIO', label: '音频' },
		{ value: 'FILE', label: '文件' }
	]

	const statusOptions = tool.dictList('COMMON_STATUS')
</script>
