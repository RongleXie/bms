<template>
	<xn-form-container title="文章详情" :width="900" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-descriptions :column="2" bordered>
			<a-descriptions-item label="标题" :span="2">
				{{ articleData.title }}
			</a-descriptions-item>
			<a-descriptions-item label="分类">
				<a-tag color="blue">{{ articleData.categoryName }}</a-tag>
			</a-descriptions-item>
			<a-descriptions-item label="标签">
				<a-tag v-for="tag in parsedTags" :key="tag" color="green" style="margin-right: 4px">
					{{ tag }}
				</a-tag>
			</a-descriptions-item>
			<a-descriptions-item label="作者">
				{{ articleData.author || '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="来源">
				{{ articleData.source || '-' }}
			</a-descriptions-item>
			<a-descriptions-item label="封面图" :span="2">
				<a-image v-if="articleData.coverImage" :src="articleData.coverImage" style="max-width: 200px" />
				<span v-else>无封面</span>
			</a-descriptions-item>
			<a-descriptions-item label="阅读量">
				{{ articleData.viewCount || 0 }}
			</a-descriptions-item>
			<a-descriptions-item label="状态">
				<a-tag :color="articleData.status === 'ENABLE' ? 'success' : 'error'">
					{{ articleData.status === 'ENABLE' ? '启用' : '禁用' }}
				</a-tag>
			</a-descriptions-item>
			<a-descriptions-item label="创建时间" :span="2">
				{{ articleData.createTime }}
			</a-descriptions-item>
			<a-descriptions-item label="内容" :span="2">
				<div class="article-content-preview">
					<xn-md-preview :text="articleData.content || ''" />
				</div>
			</a-descriptions-item>
		</a-descriptions>
		<template #footer>
			<a-button @click="onClose">关闭</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="bizArticleDetail">
import bizArticleApi from '@/api/biz/bizArticleApi'
import { XnMdPreview } from '@/components/XnMdEditor/mdEditor'

const visible = ref(false)
const articleData = ref({})

const parsedTags = computed(() => {
	if (!articleData.value.tags) return []
	try {
		return JSON.parse(articleData.value.tags)
	} catch {
		return articleData.value.tags.split(',').filter((t) => t)
	}
})

const onOpen = (record) => {
	visible.value = true
	bizArticleApi.articleDetail({ id: record.id }).then((data) => {
		articleData.value = data
	})
}

const onClose = () => {
	visible.value = false
	articleData.value = {}
}

defineExpose({ onOpen })
</script>

<style scoped>
.article-content-preview {
	max-height: 400px;
	overflow-y: auto;
	padding: 16px;
	background: #fafafa;
	border-radius: 4px;
}

.article-content-preview :deep(.github-markdown-body) {
	background: transparent;
}
</style>