<template>
	<xn-form-container
		:title="formData.id ? '编辑媒体' : '新增媒体'"
		:width="700"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="24">
					<a-form-item label="媒体名称" name="name">
						<a-input v-model:value="formData.name" placeholder="请输入媒体名称" allow-clear />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="媒体类型" name="mediaType">
						<a-select v-model:value="formData.mediaType" placeholder="请选择媒体类型" :options="mediaTypeOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="排序" name="sortCode">
						<a-input-number v-model:value="formData.sortCode" placeholder="请输入排序" class="xn-wd" :min="0" />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16" v-if="formData.mediaType === 'IMAGE'">
				<a-col :span="24">
					<a-form-item label="上传图片" name="url">
						<a-upload
							v-model:file-list="fileList"
							list-type="picture-card"
							:max-count="1"
							:before-upload="beforeUpload"
							@change="handleFileChange"
						>
							<div v-if="fileList.length === 0">
								<PlusOutlined />
								<div style="margin-top: 8px">上传图片</div>
							</div>
						</a-upload>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16" v-else>
				<a-col :span="24">
					<a-form-item label="媒体 URL" name="url">
						<a-input v-model:value="formData.url" placeholder="请输入媒体 URL 或上传文件" allow-clear />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="24">
					<a-form-item label="描述" name="remark">
						<a-textarea
							v-model:value="formData.remark"
							placeholder="请输入描述"
							:auto-size="{ minRows: 3, maxRows: 6 }"
							allow-clear
						/>
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

<script setup name="bizMedia">
	import { message } from 'ant-design-vue'
	import { PlusOutlined } from '@ant-design/icons-vue'
	import bizMediaApi from '@/api/biz/bizMediaApi'
	import { required } from '@/utils/formRules'
	import tool from '@/utils/tool'

	const visible = ref(false)
	const formRef = ref()
	const formLoading = ref(false)
	const emit = defineEmits(['successful'])
	const formData = ref({})
	const fileList = ref([])
	const statusOptions = tool.dictList('COMMON_STATUS')

	const mediaTypeOptions = [
		{ value: 'IMAGE', label: '图片' },
		{ value: 'VIDEO', label: '视频' },
		{ value: 'AUDIO', label: '音频' },
		{ value: 'FILE', label: '文件' }
	]

	const formRules = {
		name: [required('请输入媒体名称')],
		mediaType: [required('请选择媒体类型')],
		url: [required('请上传或输入媒体 URL')]
	}

	const onOpen = (record) => {
		visible.value = true
		formData.value = {
			status: 'ENABLE',
			sortCode: 0,
			mediaType: 'IMAGE'
		}
		nextTick(() => {
			if (record) {
				bizMediaApi.mediaDetail({ id: record.id }).then((data) => {
					formData.value = Object.assign(formData.value, data)
					if (formData.value.url && formData.value.mediaType === 'IMAGE') {
						fileList.value = [
							{
								uid: '-1',
								name: 'media-image.png',
								status: 'done',
								url: formData.value.url
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
		fileList.value = []
	}

	const beforeUpload = (file) => {
		const isImage = file.type.startsWith('image/')
		if (!isImage) {
			message.error('只能上传图片文件!')
		}
		const isLt5M = file.size / 1024 / 1024 < 5
		if (!isLt5M) {
			message.error('图片大小不能超过 5MB!')
		}
		return isImage && isLt5M
	}

	const handleFileChange = ({ fileList: newFileList }) => {
		fileList.value = newFileList
		if (newFileList.length > 0 && newFileList[0].originFileObj) {
			const reader = new FileReader()
			reader.onload = (e) => {
				formData.value.url = e.target.result
			}
			reader.readAsDataURL(newFileList[0].originFileObj)
		}
	}

	const onSubmit = () => {
		formRef.value.validate().then(() => {
			formLoading.value = true
			bizMediaApi
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
