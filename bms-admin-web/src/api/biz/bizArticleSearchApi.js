import { request } from '@/utils/request'

const api = {
	search: '/biz/articleSearch/search',
	searchAdvanced: '/biz/articleSearch/searchAdvanced',
	suggest: '/biz/articleSearch/suggest'
}

export function articleSearch(params) {
	return request({
		url: api.search,
		method: 'get',
		params
	})
}

export function articleSearchAdvanced(params) {
	return request({
		url: api.searchAdvanced,
		method: 'get',
		params
	})
}

export function articleSuggest(params) {
	return request({
		url: api.suggest,
		method: 'get',
		params
	})
}