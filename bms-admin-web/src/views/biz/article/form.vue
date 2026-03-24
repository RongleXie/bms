<template>
	<xn-form-container
		:title="formData.id ? '编辑文章' : '新增文章'"
		:width="900"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="24">
					<a-form-item label="标题" name="title">
						<a-input v-model:value="formData.title" placeholder="请输入文章标题" allow-clear />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="分类" name="categoryId">
						<a-tree-select
							v-model:value="formData.categoryId"
							placeholder="请选择分类"
							allow-clear
							:tree-data="categoryTreeData"
							:field-names="treeFieldNames"
							tree-line
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="标签" name="tagIds">
						<a-select
							v-model:value="formData.tagIds"
							placeholder="请选择标签"
							mode="multiple"
							allow-clear
							:options="tagOptions"
						/>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="作者" name="author">
						<a-input v-model:value="formData.author" placeholder="请输入作者" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="来源" name="source">
						<a-input v-model:value="formData.source" placeholder="请输入来源" allow-clear />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="24">
					<a-form-item label="封面图" name="coverImage">
						<a-upload
							v-model:file-list="coverImageFileList"
							list-type="picture-card"
							:max-count="1"
							:before-upload="beforeUpload"
							@change="handleCoverImageChange"
						>
							<div v-if="coverImageFileList.length === 0">
								<PlusOutlined />
								<div style="margin-top: 8px">上传封面图</div>
							</div>
						</a-upload>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="24">
					<a-form-item label="内容" name="content">
						<a-textarea
							v-model:value="formData.content"
							placeholder="请输入文章内容"
							:auto-size="{ minRows: 10, maxRows: 20 }"
							allow-clear
						/>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="8">
					<a-form-item label="排序" name="sortCode">
						<a-input-number v-model:value="formData.sortCode" placeholder="请输入排序" class="xn-wd" :min="0" />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="状态" name="status">
						<a-radio-group v-model:value="formData.status" :options="statusOptions" />
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button class="xn-mr8" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="formLoading" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="bizArticle">
	import { message } from 'ant-design-vue'
	import { PlusOutlined } from '@ant-design/icons-vue'
	import bizArticleApi from '@/api/biz/bizArticleApi'
	import bizCategoryApi from '@/api/biz/bizCategoryApi'
	import bizTagApi from '@/api/biz/bizTagApi'
	import { required } from '@/utils/formRules'
	import tool from '@/utils/tool'

	const visible = ref(false)
	const formRef = ref()
	const formLoading = ref(false)
	const emit = defineEmits(['successful'])
	const formData = ref({})
	const categoryTreeData = ref([])
	const tagOptions = ref([])
	const treeFieldNames = { children: 'children', label: 'name', value: 'id', key: 'id' }
	const coverImageFileList = ref([])
	const statusOptions = tool.dictList('COMMON_STATUS')

	const formRules = {
		title: [required('请输入标题')],
		categoryId: [required('请选择分类')],
		content: [required('请输入内容')]
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

	const onOpen = (record) => {
		visible.value = true
		formData.value = {
			status: 'ENABLE',
			sortCode: 0
		}
		nextTick(() => {
			loadCategoryTree()
			loadTagList()
			if (record) {
				bizArticleApi.articleDetail({ id: record.id }).then((data) => {
					formData.value = Object.assign(formData.value, data)
					if (formData.value.tagIds) {
						formData.value.tagIds = formData.value.tagIds.split(',').map((id) => id.trim())
					}
					if (formData.value.coverImage) {
						coverImageFileList.value = [
							{
								uid: '-1',
								name: 'cover-image.png',
								status: 'done',
								url: formData.value.coverImage
							}
						]
					}
				})
			}
		})
	}

	const onClose = () => {
		visible.value = false
		formData.value = {}
		coverImageFileList.value = []
	}

	const beforeUpload = (file) => {
		const isImage = file.type.startsWith('image/')
		if (!isImage) {
			message.error('只能上传图片文件!')
		}
		const isLt2M = file.size / 1024 / 1024 < 2
		if (!isLt2M) {
			message.error('图片大小不能超过 2MB!')
		}
		return isImage && isLt2M
	}

	const handleCoverImageChange = ({ fileList }) => {
		coverImageFileList.value = fileList
		if (fileList.length > 0 && fileList[0].originFileObj) {
			const reader = new FileReader()
			reader.onload = (e) => {
				formData.value.coverImage = e.target.result
			}
			reader.readAsDataURL(fileList[0].originFileObj)
		}
	}

	const onSubmit = () => {
		formRef.value.validate().then(() => {
			formLoading.value = true
			const submitData = { ...formData.value }
			if (submitData.tagIds && Array.isArray(submitData.tagIds)) {
				submitData.tagIds = submitData.tagIds.join(',')
			}
			bizArticleApi
				.submitForm(submitData, !!submitData.id)
				.then(() => {
					onClose()
					emit('successful')
				})
				.finally(() => {
					formLoading.value = false
				})
		})
	}

	defineExpose({ onOpen })
</script>
