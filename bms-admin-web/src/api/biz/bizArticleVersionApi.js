import { request } from '@/utils/request'

const api = {
	list: '/biz/articleVersion/list',
	page: '/biz/articleVersion/page',
	detail: '/biz/articleVersion/detail',
	rollback: '/biz/articleVersion/rollback',
	latest: '/biz/articleVersion/latest'
}

export function versionList(params) {
	return request({
		url: api.list,
		method: 'get',
		params
	})
}

export function versionPage(params) {
	return request({
		url: api.page,
		method: 'get',
		params
	})
}

export function versionDetail(params) {
	return request({
		url: api.detail,
		method: 'get',
		params
	})
}

export function versionRollback(params) {
	return request({
		url: api.rollback,
		method: 'post',
		params
	})
}

export function versionLatest(params) {
	return request({
		url: api.latest,
		method: 'get',
		params
	})
}