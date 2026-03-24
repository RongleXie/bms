<template>
	<xn-panel>
		<a-form ref="searchFormRef" :model="searchFormState">
			<a-row :gutter="10">
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item label="评论内容" name="content">
						<a-input v-model:value="searchFormState.content" placeholder="请输入评论内容" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item label="用户 ID" name="userId">
						<a-input v-model:value="searchFormState.userId" placeholder="请输入用户 ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item label="状态" name="status">
						<a-select v-model:value="searchFormState.status" placeholder="请选择状态" :options="statusOptions" />
					</a-form-item>
				</a-col>
				<a-col :xs="24" :sm="24" :md="24" :lg="6" :xl="6">
					<a-form-item label="审核状态" name="auditStatus">
						<a-select v-model:value="searchFormState.auditStatus" placeholder="请选择审核状态" :options="auditStatusOptions" />
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
					<xn-batch-button
						v-if="hasPerm('bizCommentBatchDelete')"
						buttonName="批量删除"
						icon="DeleteOutlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchBizComment"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'content'">
					<a-typography-paragraph :ellipsis="{ rows: 2, expandable: true }">
						{{ record.content }}
					</a-typography-paragraph>
				</template>
				<template v-if="column.dataIndex === 'auditStatus'">
					<a-tag :color="getAuditStatusColor(record.auditStatus)">
						{{ $TOOL.dictTypeData('BIZ_COMMENT_AUDIT_STATUS', record.auditStatus) || record.auditStatus }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'status'">
					<a-switch
						:loading="loading"
						:checked="record.status === 'ENABLE'"
						@change="editStatus(record)"
						v-if="hasPerm('bizCommentUpdateStatus')"
					/>
					<span v-else>{{ $TOOL.dictTypeData('COMMON_STATUS', record.status) }}</span>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="auditComment(record, 'PASS')" v-if="hasPerm('bizCommentAudit') && record.auditStatus !== 'PASS'">通过</a>
						<a-divider type="vertical" v-if="hasPerm('bizCommentAudit')" />
						<a @click="auditComment(record, 'REJECT')" v-if="hasPerm('bizCommentAudit') && record.auditStatus !== 'REJECT'">拒绝</a>
						<a-divider type="vertical" v-if="hasPerm(['bizCommentAudit', 'bizCommentDelete'], 'and')" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteBizComment(record)">
							<a-button type="link" danger size="small" v-if="hasPerm('bizCommentDelete')">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</xn-panel>
</template>

<script setup name="bizComment">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import bizCommentApi from '@/api/biz/bizCommentApi'

	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const loading = ref(false)

	const columns = [
		{ title: '评论内容', dataIndex: 'content', ellipsis: true },
		{ title: '用户 ID', dataIndex: 'userId' },
		{ title: '文章 ID', dataIndex: 'articleId' },
		{ title: '父评论 ID', dataIndex: 'parentId' },
		{ title: '审核状态', dataIndex: 'auditStatus' },
		{ title: '状态', dataIndex: 'status' },
		{ title: '创建时间', dataIndex: 'createTime' }
	]

	if (hasPerm(['bizCommentAudit', 'bizCommentDelete'])) {
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
		return bizCommentApi.commentPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}

	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}

	const deleteBizComment = (record) => {
		let params = [{ id: record.id }]
		bizCommentApi.commentDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}

	const deleteBatchBizComment = (params) => {
		bizCommentApi.commentDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}

	const editStatus = (record) => {
		loading.value = true
		if (record.status === 'ENABLE') {
			bizCommentApi.commentDisableStatus(record).finally(() => {
				tableRef.value.refresh()
				loading.value = false
			})
		} else {
			bizCommentApi.commentEnableStatus(record).finally(() => {
				tableRef.value.refresh()
				loading.value = false
			})
		}
	}

	const auditComment = (record, auditStatus) => {
		bizCommentApi.commentAudit({ id: record.id, auditStatus }).then(() => {
			tableRef.value.refresh()
		})
	}

	const getAuditStatusColor = (status) => {
		const colorMap = {
			PENDING: 'orange',
			PASS: 'green',
			REJECT: 'red'
		}
		return colorMap[status] || 'default'
	}

	const statusOptions = tool.dictList('COMMON_STATUS')
	const auditStatusOptions = [
		{ value: 'PENDING', label: '待审核' },
		{ value: 'PASS', label: '通过' },
		{ value: 'REJECT', label: '拒绝' }
	]
</script>
