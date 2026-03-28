<template>
	<xn-panel>
		<a-card :bordered="false">
			<a-form ref="searchFormRef" :model="searchFormState">
				<a-row :gutter="24">
					<a-col :span="6">
						<a-form-item label="文章" name="articleId">
							<a-select
								v-model:value="searchFormState.articleId"
								placeholder="请选择文章"
								allow-clear
								show-search
								:filter-option="filterOption"
								:options="articleOptions"
								@change="handleArticleChange"
							/>
						</a-form-item>
					</a-col>
					<a-col :span="6">
						<a-form-item>
							<a-space>
								<a-button type="primary" @click="loadVersionList">
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

		<a-card :bordered="false" style="margin-top: 16px">
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
							<a @click="handlePreview(record)" v-if="hasPerm('bizArticleVersionList')">预览</a>
							<a-divider type="vertical" />
							<a-popconfirm title="确定要回滚到此版本吗？" @confirm="handleRollback(record)" v-if="hasPerm('bizArticleVersionRollback')">
								<a>回滚</a>
							</a-popconfirm>
						</a-space>
					</template>
				</template>
			</a-table>
		</a-card>

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
				<a-descriptions-item label="变更摘要">{{ previewData.changeSummary || '-' }}</a-descriptions-item>
				<a-descriptions-item label="创建时间">{{ formatDate(previewData.createTime) }}</a-descriptions-item>
				<a-descriptions-item label="内容">
					<div class="version-content-preview">
						<xn-md-preview :text="previewData.content || ''" />
					</div>
				</a-descriptions-item>
			</a-descriptions>
		</a-modal>
	</xn-panel>
</template>

<script setup name="bizArticleVersion">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { SearchOutlined, RedoOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import bizArticleVersionApi from '@/api/biz/bizArticleVersionApi'
import bizArticleApi from '@/api/biz/bizArticleApi'
import { XnMdPreview } from '@/components/XnMdEditor/mdEditor'
import tool from '@/utils/tool'

const loading = ref(false)
const versionList = ref([])
const previewVisible = ref(false)
const previewData = ref({})
const articleOptions = ref([])

const searchFormState = reactive({
	articleId: null
})

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

const filterOption = (input, option) => {
	return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

const loadArticleList = () => {
	bizArticleApi.articleList({}).then((data) => {
		articleOptions.value = (data || []).map((item) => ({
			value: item.id,
			label: item.title
		}))
	})
}

const loadVersionList = () => {
	if (!searchFormState.articleId) {
		message.warning('请选择文章')
		return
	}
	loading.value = true
	bizArticleVersionApi
		.page({
			articleId: searchFormState.articleId,
			current: pagination.current,
			size: pagination.pageSize
		})
		.then((data) => {
			versionList.value = data.records || []
			pagination.total = data.total || 0
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

const handleArticleChange = () => {
	pagination.current = 1
	if (searchFormState.articleId) {
		loadVersionList()
	} else {
		versionList.value = []
		pagination.total = 0
	}
}

const handlePreview = (record) => {
	previewData.value = record
	previewVisible.value = true
}

const handleRollback = (record) => {
	bizArticleVersionApi
		.rollback({
			articleId: searchFormState.articleId,
			versionNumber: record.versionNumber
		})
		.then(() => {
			message.success('回滚成功')
			loadVersionList()
		})
}

const reset = () => {
	searchFormState.articleId = null
	versionList.value = []
	pagination.total = 0
	pagination.current = 1
}

const hasPerm = (perm) => {
	return tool.data.get('PERMISSIONS')?.includes(perm)
}

onMounted(() => {
	loadArticleList()
})
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