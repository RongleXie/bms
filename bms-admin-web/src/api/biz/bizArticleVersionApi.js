/**
 * 文章版本历史接口 api
 *
 * @author bms
 * @date 2026-03-26
 */
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/articleVersion/` + url, ...arg)

export default {
	// 获取版本列表
	list(data) {
		return request('list', data, 'get')
	},
	// 获取版本分页
	page(data) {
		return request('page', data, 'get')
	},
	// 获取版本详情
	detail(data) {
		return request('detail', data, 'get')
	},
	// 版本回滚
	rollback(data) {
		return request('rollback', data, 'post')
	},
	// 获取最新版本
	latest(data) {
		return request('latest', data, 'get')
	}
}