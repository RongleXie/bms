<template>
	<xn-form-container
		:title="formData.id ? '编辑标签' : '新增标签'"
		:width="700"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="标签名称" name="name">
						<a-input v-model:value="formData.name" placeholder="请输入标签名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="编码" name="code">
						<a-input v-model:value="formData.code" placeholder="请输入标签编码" allow-clear />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="颜色" name="color">
						<a-input v-model:value="formData.color" placeholder="请输入颜色值" allow-clear>
							<template #addonAfter>
								<input type="color" v-model="formData.color" style="border: none; width: 30px; height: 28px" />
							</template>
						</a-input>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="排序" name="sortCode">
						<a-input-number v-model:value="formData.sortCode" placeholder="请输入排序" class="xn-wd" :min="0" />
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

<script setup name="bizTag">
	import bizTagApi from '@/api/biz/bizTagApi'
	import { required } from '@/utils/formRules'
	import tool from '@/utils/tool'

	const visible = ref(false)
	const formRef = ref()
	const formLoading = ref(false)
	const emit = defineEmits(['successful'])
	const formData = ref({})
	const statusOptions = tool.dictList('COMMON_STATUS')

	const formRules = {
		name: [required('请输入标签名称')],
		code: [required('请输入标签编码')]
	}

	const onOpen = (record) => {
		visible.value = true
		formData.value = {
			status: 'ENABLE',
			sortCode: 0,
			color: '#1890ff'
		}
		nextTick(() => {
			if (record) {
				bizTagApi.tagDetail({ id: record.id }).then((data) => {
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
			bizTagApi
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
