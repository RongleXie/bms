/**
 *  Copyright [2022] [https://www.xiaonuo.vip]
 *	Snowy 采用 APACHE LICENSE 2.0 开源协议，您在使用过程中，需要注意以下几点：
 *	1.请不要删除和修改根目录下的 LICENSE 文件。
 *	2.请不要删除和修改 Snowy 源码头部的版权声明。
 *	3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 *	4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 *	5.不可二次分发开源参与同类竞品，如有想法可联系团队 xiaonuobase@qq.com 商议合作。
 *	6.若您的项目无法满足以上几点，需要更多功能代码，获取 Snowy 商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/comment/` + url, ...arg)

/**
 * 评论管理接口 api
 *
 * @author bms
 * @date 2026-03-24
 */
export default {
	// 获取评论分页
	commentPage(data) {
		return request('page', data, 'get')
	},
	// 获取评论列表
	commentList(data) {
		return request('list', data, 'get')
	},
	// 获取评论详情
	commentDetail(data) {
		return request('detail', data, 'get')
	},
	// 提交表单 edit 为 true 时为编辑，默认为新增
	submitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除评论
	commentDelete(data) {
		return request('delete', data)
	},
	// 禁用评论
	commentDisableStatus(data) {
		return request('disableStatus', data)
	},
	// 启用评论
	commentEnableStatus(data) {
		return request('enableStatus', data)
	},
	// 审核评论
	commentAudit(data) {
		return request('audit', data)
	}
}
