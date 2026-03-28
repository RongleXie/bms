<template>
	<xn-panel>
		<a-card :bordered="false">
			<a-form ref="searchFormRef" :model="searchFormState">
				<a-row :gutter="24">
					<a-col :span="6">
						<a-form-item label="文章标题" name="title">
							<a-input v-model:value="searchFormState.title" placeholder="请输入文章标题" allow-clear />
						</a-form-item>
					</a-col>
					<a-col :span="6">
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
		</a-card>

		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:row-selection="options.rowSelection"
		>
			<template #operator>
				<a-space>
					<a-button type="primary" @click="publishAllSelected" v-if="hasPerm('bizScheduledPublishNow')">
						<template #icon><RocketOutlined /></template>
						批量立即发布
					</a-button>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'status'">
					<a-tag color="blue">待发布</a-tag>
				</template>
				<template v-if="column.dataIndex === 'scheduledPublishTime'">
					<span :class="{ 'text-danger': isOverdue(record.scheduledPublishTime) }">
						{{ formatDate(record.scheduledPublishTime) }}
					</span>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="publishNow(record)" v-if="hasPerm('bizScheduledPublishNow')">立即发布</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要取消定时发布吗？" @confirm="cancelScheduled(record)">
							<a v-if="hasPerm('bizScheduledCancel')">取消定时</a>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>

		<a-modal v-model:open="publishVisible" title="定时发布设置" :width="500" @ok="handleScheduledPublish">
			<a-form :model="publishForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
				<a-form-item label="选择文章" required>
					<a-input :value="currentArticle.title" disabled />
				</a-form-item>
				<a-form-item label="计划发布时间" required>
					<a-date-picker
						v-model:value="publishForm.scheduledTime"
						show-time
						format="YYYY-MM-DD HH:mm:ss"
						placeholder="请选择发布时间"
						style="width: 100%"
						:disabled-date="disabledDate"
					/>
				</a-form-item>
			</a-form>
		</a-modal>
	</xn-panel>
</template>

<script setup name="bizScheduledPublish">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { SearchOutlined, RedoOutlined, RocketOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import bizArticleApi from '@/api/biz/bizArticleApi'
import tool from '@/utils/tool'

const tableRef = ref(null)
const searchFormRef = ref(null)
const searchFormState = reactive({})
const publishVisible = ref(false)
const currentArticle = ref({})
const publishForm = reactive({
	scheduledTime: null
})
const selectedRowKeys = ref([])

const columns = [
	{ title: '文章标题', dataIndex: 'title', ellipsis: true },
	{ title: '状态', dataIndex: 'status', width: 100 },
	{ title: '计划发布时间', dataIndex: 'scheduledPublishTime', width: 180 },
	{ title: '创建时间', dataIndex: 'createTime', width: 180 },
	{ title: '操作', dataIndex: 'action', width: 160, fixed: 'right' }
]

const toolConfig = {
	refresh: true,
	height: true,
	columnSetting: true,
	striped: false
}

const options = {
	alert: { show: false },
	rowSelection: {
		onChange: (keys) => {
			selectedRowKeys.value = keys
		}
	}
}

const formatDate = (date) => {
	return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '-'
}

const isOverdue = (scheduledTime) => {
	if (!scheduledTime) return false
	return dayjs(scheduledTime).isBefore(dayjs())
}

const disabledDate = (current) => {
	return current && current < dayjs().startOf('day')
}

const loadData = (parameter) => {
	const params = {
		current: parameter.current,
		size: parameter.size,
		title: searchFormState.title
	}
	return bizArticleApi.articleScheduledList(params).then((data) => {
		return {
			records: data.records || [],
			total: data.total || 0
		}
	})
}

const reset = () => {
	searchFormRef.value?.resetFields()
	tableRef.value?.refresh(true)
}

const publishNow = (record) => {
	bizArticleApi.articlePublish({ id: record.id }).then(() => {
		message.success('发布成功')
		tableRef.value?.refresh()
	})
}

const cancelScheduled = (record) => {
	bizArticleApi.articleCancelScheduled({ id: record.id }).then(() => {
		message.success('已取消定时发布')
		tableRef.value?.refresh()
	})
}

const publishAllSelected = () => {
	if (selectedRowKeys.value.length === 0) {
		message.warning('请选择要发布的文章')
		return
	}
	
	let successCount = 0
	const promises = selectedRowKeys.value.map((id) => 
		bizArticleApi.articlePublish({ id }).then(() => {
			successCount++
		})
	)
	
	Promise.all(promises).then(() => {
		message.success(`成功发布 ${successCount} 篇文章`)
		selectedRowKeys.value = []
		tableRef.value?.refresh()
	})
}

const handleScheduledPublish = () => {
	if (!publishForm.scheduledTime) {
		message.warning('请选择计划发布时间')
		return
	}
	
	const scheduledTimeStr = publishForm.scheduledTime.format('YYYY-MM-DD HH:mm:ss')
	bizArticleApi.articleScheduledPublish({ id: currentArticle.value.id }, scheduledTimeStr).then(() => {
		message.success('定时发布设置成功')
		publishVisible.value = false
		tableRef.value?.refresh()
	})
}

const hasPerm = (perm) => {
	return tool.data.get('PERMISSIONS')?.includes(perm)
}
</script>

<style scoped>
.text-danger {
	color: #ff4d4f;
}
</style>