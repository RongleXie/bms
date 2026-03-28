/**
 * 文章全文搜索接口 api
 *
 * @author bms
 * @date 2026-03-26
 */
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/articleSearch/` + url, ...arg)

export default {
	// 全文搜索
	search(data) {
		return request('search', data, 'get')
	},
	// 高级搜索
	searchAdvanced(data) {
		return request('searchAdvanced', data, 'get')
	},
	// 搜索建议
	suggest(data) {
		return request('suggest', data, 'get')
	}
}