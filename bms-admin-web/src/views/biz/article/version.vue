<template>
	<xn-form-container title="版本历史" :width="1000" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-table
			:columns="columns"
			:data-source="versionList"
			:loading="loading"
			:pagination="pagination"
			row-key="id"
			@change="handleTableChange"
		>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'versionNumber'">
					<a-tag color="blue">v{{ record.versionNumber }}</a-tag>
				</template>
				<template v-if="column.dataIndex === 'createTime'">
					{{ formatDate(record.createTime) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="handlePreview(record)">预览</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要回滚到此版本吗？" @confirm="handleRollback(record)">
							<a>回滚</a>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</a-table>

		<a-modal
			v-model:open="previewVisible"
			title="版本预览"
			:width="800"
			:footer="null"
		>
			<a-descriptions :column="1" bordered>
				<a-descriptions-item label="版本号">v{{ previewData.versionNumber }}</a-descriptions-item>
				<a-descriptions-item label="标题">{{ previewData.title }}</a-descriptions-item>
				<a-descriptions-item label="摘要">{{ previewData.summary || '-' }}</a-descriptions-item>
				<a-descriptions-item label="创建时间">{{ formatDate(previewData.createTime) }}</a-descriptions-item>
				<a-descriptions-item label="内容">
					<div class="version-content-preview">
						<xn-md-preview :text="previewData.content || ''" />
					</div>
				</a-descriptions-item>
			</a-descriptions>
		</a-modal>
	</xn-form-container>
</template>

<script setup name="bizArticleVersion">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import bizArticleVersionApi from '@/api/biz/bizArticleVersionApi'
import { XnMdPreview } from '@/components/XnMdEditor/mdEditor'

const visible = ref(false)
const loading = ref(false)
const articleId = ref('')
const versionList = ref([])
const previewVisible = ref(false)
const previewData = ref({})

const columns = [
	{ title: '版本号', dataIndex: 'versionNumber', width: 100 },
	{ title: '标题', dataIndex: 'title', ellipsis: true },
	{ title: '变更摘要', dataIndex: 'changeSummary', ellipsis: true },
	{ title: '创建时间', dataIndex: 'createTime', width: 180 },
	{ title: '操作', dataIndex: 'action', width: 120 }
]

const pagination = reactive({
	current: 1,
	pageSize: 10,
	total: 0,
	showSizeChanger: true,
	showTotal: (total) => `共 ${total} 条`
})

const formatDate = (date) => {
	return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '-'
}

const loadVersionList = () => {
	loading.value = true
	bizArticleVersionApi
		.page({
			articleId: articleId.value,
			current: pagination.current,
			size: pagination.pageSize
		})
		.then((data) => {
			versionList.value = data.records
			pagination.total = data.total
		})
		.finally(() => {
			loading.value = false
		})
}

const handleTableChange = (pag) => {
	pagination.current = pag.current
	pagination.pageSize = pag.pageSize
	loadVersionList()
}

const handlePreview = (record) => {
	previewData.value = record
	previewVisible.value = true
}

const handleRollback = (record) => {
	bizArticleVersionApi
		.rollback({
			articleId: articleId.value,
			versionNumber: record.versionNumber
		})
		.then(() => {
			message.success('回滚成功')
			emit('successful')
			onClose()
		})
}

const onOpen = (id) => {
	articleId.value = id
	visible.value = true
	pagination.current = 1
	loadVersionList()
}

const onClose = () => {
	visible.value = false
	versionList.value = []
	articleId.value = ''
}

const emit = defineEmits(['successful'])

defineExpose({ onOpen })
</script>

<style scoped>
.version-content-preview {
	max-height: 400px;
	overflow-y: auto;
	padding: 16px;
	background: #fafafa;
	border-radius: 4px;
}

.version-content-preview :deep(.github-markdown-body) {
	background: transparent;
}
</style>