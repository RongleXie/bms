<template>
	<xn-form-container
		:title="formData.id ? '编辑评论' : '新增评论'"
		:width="700"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="24">
					<a-form-item label="评论内容" name="content">
						<a-textarea
							v-model:value="formData.content"
							placeholder="请输入评论内容"
							:auto-size="{ minRows: 4, maxRows: 8 }"
							allow-clear
						/>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="用户 ID" name="userId">
						<a-input v-model:value="formData.userId" placeholder="请输入用户 ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="文章 ID" name="articleId">
						<a-input v-model:value="formData.articleId" placeholder="请输入文章 ID" allow-clear />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="父评论 ID" name="parentId">
						<a-input v-model:value="formData.parentId" placeholder="请输入父评论 ID" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="审核状态" name="auditStatus">
						<a-select v-model:value="formData.auditStatus" placeholder="请选择审核状态" :options="auditStatusOptions" />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="12">
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

<script setup name="bizComment">
	import bizCommentApi from '@/api/biz/bizCommentApi'
	import { required } from '@/utils/formRules'
	import tool from '@/utils/tool'

	const visible = ref(false)
	const formRef = ref()
	const formLoading = ref(false)
	const emit = defineEmits(['successful'])
	const formData = ref({})
	const statusOptions = tool.dictList('COMMON_STATUS')
	const auditStatusOptions = [
		{ value: 'PENDING', label: '待审核' },
		{ value: 'PASS', label: '通过' },
		{ value: 'REJECT', label: '拒绝' }
	]

	const formRules = {
		content: [required('请输入评论内容')]
	}

	const onOpen = (record) => {
		visible.value = true
		formData.value = {
			status: 'ENABLE',
			auditStatus: 'PENDING'
		}
		nextTick(() => {
			if (record) {
				bizCommentApi.commentDetail({ id: record.id }).then((data) => {
					formData.value = Object.assign(formData.value, data)
				})
			}
		})
	}

	const onClose = () => {
		visible.value = false
		formData.value = {}
	}

	const onSubmit = () => {
		formRef.value.validate().then(() => {
			formLoading.value = true
			bizCommentApi
				.submitForm(formData.value, !!formData.value.id)
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
