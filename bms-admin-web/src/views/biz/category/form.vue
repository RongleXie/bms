<template>
	<xn-form-container
		:title="formData.id ? '编辑分类' : '新增分类'"
		:width="700"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="分类名称" name="name">
						<a-input v-model:value="formData.name" placeholder="请输入分类名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="编码" name="code">
						<a-input v-model:value="formData.code" placeholder="请输入分类编码" allow-clear />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="上级分类" name="parentId">
						<a-tree-select
							v-model:value="formData.parentId"
							placeholder="请选择上级分类"
							allow-clear
							:tree-data="treeData"
							:field-names="treeFieldNames"
							tree-line
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="图标" name="icon">
						<a-input v-model:value="formData.icon" placeholder="请输入图标类名" allow-clear />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="24">
					<a-form-item label="描述" name="description">
						<a-textarea
							v-model:value="formData.description"
							placeholder="请输入分类描述"
							:auto-size="{ minRows: 3, maxRows: 6 }"
							allow-clear
						/>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="排序" name="sortCode">
						<a-input-number v-model:value="formData.sortCode" placeholder="请输入排序" class="xn-wd" :min="0" />
					</a-form-item>
				</a-col>
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

<script setup name="bizCategory">
	import bizCategoryApi from '@/api/biz/bizCategoryApi'
	import { required } from '@/utils/formRules'
	import tool from '@/utils/tool'

	const visible = ref(false)
	const formRef = ref()
	const formLoading = ref(false)
	const emit = defineEmits(['successful'])
	const formData = ref({})
	const treeData = ref([])
	const treeFieldNames = { children: 'children', label: 'name', value: 'id', key: 'id' }
	const statusOptions = tool.dictList('COMMON_STATUS')

	const formRules = {
		name: [required('请输入分类名称')],
		code: [required('请输入分类编码')]
	}

	const loadTreeData = () => {
		bizCategoryApi.categoryTree().then((res) => {
			if (res) {
				treeData.value = res
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
			loadTreeData()
			if (record) {
				bizCategoryApi.categoryDetail({ id: record.id }).then((data) => {
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
			bizCategoryApi
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
