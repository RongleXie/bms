<template>
	<xn-panel>
		<a-card :bordered="false">
			<a-row :gutter="16">
				<a-col :span="24">
					<a-input-search
						v-model:value="searchKeyword"
						placeholder="输入关键词搜索文章标题和内容..."
						enter-button="搜索"
						size="large"
						allow-clear
						@search="handleSearch"
					/>
				</a-col>
			</a-row>
			<a-row :gutter="16" style="margin-top: 16px" v-if="advanced">
				<a-col :span="6">
					<a-form-item label="分类">
						<a-tree-select
							v-model:value="searchParams.categoryId"
							placeholder="请选择分类"
							allow-clear
							:tree-data="categoryTreeData"
							:field-names="treeFieldNames"
							tree-line
						/>
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="状态">
						<a-select v-model:value="searchParams.status" placeholder="请选择状态" allow-clear :options="statusOptions" />
					</a-form-item>
				</a-col>
			</a-row>
			<a style="margin-top: 8px; display: inline-block" @click="advanced = !advanced">
				{{ advanced ? '收起' : '高级搜索' }}
				<component :is="advanced ? 'UpOutlined' : 'DownOutlined'" />
			</a>
		</a-card>

		<a-card :bordered="false" style="margin-top: 16px" v-if="searchResults.length > 0">
			<template #title>
				搜索结果 (共 {{ pagination.total }} 条)
			</template>
			<a-list
				:data-source="searchResults"
				:pagination="pagination"
				@change="handlePageChange"
			>
				<template #renderItem="{ item }">
					<a-list-item>
						<a-list-item-meta>
							<template #title>
								<a @click="handleViewDetail(item)">{{ item.title }}</a>
								<a-tag v-if="item.status === 'PUBLISHED'" color="green" style="margin-left: 8px">已发布</a-tag>
								<a-tag v-else-if="item.status === 'DRAFT'" color="orange">草稿</a-tag>
								<a-tag v-else-if="item.status === 'SCHEDULED'" color="blue">待发布</a-tag>
							</template>
							<template #description>
								<div class="search-summary">{{ item.summary || '暂无摘要' }}</div>
								<div class="search-meta">
									<span><EyeOutlined /> {{ item.viewCount || 0 }}</span>
									<span style="margin-left: 16px"><LikeOutlined /> {{ item.likeCount || 0 }}</span>
									<span style="margin-left: 16px"><ClockCircleOutlined /> {{ formatDate(item.createTime) }}</span>
								</div>
							</template>
						</a-list-item-meta>
					</a-list-item>
				</template>
			</a-list>
		</a-card>

		<a-card :bordered="false" style="margin-top: 16px" v-else-if="searchKeyword && !loading">
			<a-empty description="未找到相关文章" />
		</a-card>

		<a-modal v-model:open="detailVisible" :title="currentArticle.title" :width="800" :footer="null">
			<a-descriptions :column="2" bordered>
				<a-descriptions-item label="状态">
					<a-tag v-if="currentArticle.status === 'PUBLISHED'" color="green">已发布</a-tag>
					<a-tag v-else-if="currentArticle.status === 'DRAFT'" color="orange">草稿</a-tag>
					<a-tag v-else-if="currentArticle.status === 'SCHEDULED'" color="blue">待发布</a-tag>
				</a-descriptions-item>
				<a-descriptions-item label="浏览量">{{ currentArticle.viewCount || 0 }}</a-descriptions-item>
				<a-descriptions-item label="创建时间">{{ formatDate(currentArticle.createTime) }}</a-descriptions-item>
				<a-descriptions-item label="发布时间">{{ formatDate(currentArticle.publishTime) }}</a-descriptions-item>
				<a-descriptions-item label="摘要" :span="2">{{ currentArticle.summary || '暂无' }}</a-descriptions-item>
				<a-descriptions-item label="内容" :span="2">
					<div class="article-content">
						<xn-md-preview :text="currentArticle.content || ''" />
					</div>
				</a-descriptions-item>
			</a-descriptions>
		</a-modal>
	</xn-panel>
</template>

<script setup name="bizArticleSearch">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { EyeOutlined, LikeOutlined, ClockCircleOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import bizArticleSearchApi from '@/api/biz/bizArticleSearchApi'
import bizCategoryApi from '@/api/biz/bizCategoryApi'
import { XnMdPreview } from '@/components/XnMdEditor/mdEditor'

const searchKeyword = ref('')
const advanced = ref(false)
const loading = ref(false)
const searchResults = ref([])
const categoryTreeData = ref([])
const detailVisible = ref(false)
const currentArticle = ref({})

const searchParams = reactive({
	categoryId: null,
	status: null
})

const statusOptions = [
	{ value: 'DRAFT', label: '草稿' },
	{ value: 'PUBLISHED', label: '已发布' },
	{ value: 'SCHEDULED', label: '待发布' }
]

const treeFieldNames = { children: 'children', label: 'name', value: 'id' }

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

const loadCategoryTree = () => {
	bizCategoryApi.categoryTree().then((data) => {
		categoryTreeData.value = data
	})
}

const handleSearch = () => {
	if (!searchKeyword.value) {
		message.warning('请输入搜索关键词')
		return
	}
	pagination.current = 1
	doSearch()
}

const doSearch = () => {
	loading.value = true
	const params = {
		keyword: searchKeyword.value,
		current: pagination.current,
		size: pagination.pageSize
	}

	const apiCall = advanced.value ? bizArticleSearchApi.searchAdvanced : bizArticleSearchApi.search

	if (advanced.value) {
		params.categoryId = searchParams.categoryId
		params.status = searchParams.status
	}

	apiCall(params)
		.then((data) => {
			searchResults.value = data.records || []
			pagination.total = data.total || 0
		})
		.finally(() => {
			loading.value = false
		})
}

const handlePageChange = (pag) => {
	pagination.current = pag.current
	pagination.pageSize = pag.pageSize
	doSearch()
}

const handleViewDetail = (record) => {
	currentArticle.value = record
	detailVisible.value = true
}

onMounted(() => {
	loadCategoryTree()
})
</script>

<style scoped>
.search-summary {
	color: #666;
	margin-bottom: 8px;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.search-meta {
	color: #999;
	font-size: 12px;
}

.article-content {
	max-height: 400px;
	overflow-y: auto;
	padding: 16px;
	background: #fafafa;
	border-radius: 4px;
}

.article-content :deep(.github-markdown-body) {
	background: transparent;
}
</style>