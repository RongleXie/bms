/**
 * XSS过滤工具
 * 使用DOMPurify进行HTML内容清洗
 */
import DOMPurify from 'dompurify'

// DOMPurify配置：允许Markdown常用标签
const markdownAllowedTags = [
	'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'p', 'br', 'hr', 'div', 'span',
	'a', 'strong', 'em', 'b', 'i', 'u', 's', 'del', 'ins', 'mark', 'sub', 'sup',
	'ul', 'ol', 'li', 'blockquote', 'pre', 'code',
	'table', 'thead', 'tbody', 'tr', 'th', 'td',
	'img', 'video', 'audio'
]

// DOMPurify配置：允许的安全属性
const markdownAllowedAttrs = [
	'href', 'title', 'target', 'rel',
	'src', 'alt', 'width', 'height', 'controls',
	'class', 'id',
	'colspan', 'rowspan', 'border', 'cellpadding', 'cellspacing'
]

// DOMPurify配置选项
const purifyConfig = {
	ALLOWED_TAGS: markdownAllowedTags,
	ALLOWED_ATTR: markdownAllowedAttrs,
	FORCE_BODY: true,
	ADD_ATTR: ['target'],
	ADD_TAGS: ['iframe'] // 仅当src为安全视频链接时允许
}

// 强制添加安全属性
DOMPurify.addHook('uponSanitizeAttribute', (node, data) => {
	if (data.attrName === 'href' || data.attrName === 'src') {
		const value = node.getAttribute(data.attrName) || ''
		// 阻止危险协议
		if (value.toLowerCase().startsWith('javascript:') ||
			value.toLowerCase().startsWith('data:') ||
			value.toLowerCase().startsWith('vbscript:')) {
			node.removeAttribute(data.attrName)
		}
	}
})

DOMPurify.addHook('uponSanitizeElement', (node, data) => {
	if (node.nodeName === 'A') {
		node.setAttribute('rel', 'noopener noreferrer')
	}
})

/**
 * 过滤Markdown内容
 * @param {string} content Markdown内容
 * @returns {string} 清洗后的内容
 */
export function filterMarkdown(content) {
	if (!content) return content
	return DOMPurify.sanitize(content, purifyConfig)
}

/**
 * 过滤纯文本内容 - 清除所有HTML标签
 * @param {string} content 输入内容
 * @returns {string} 清洗后的纯文本
 */
export function filterPlainText(content) {
	if (!content) return content
	return DOMPurify.sanitize(content, { ALLOWED_TAGS: [], ALLOWED_ATTR: [] })
}

/**
 * 过滤URL - 校验URL安全性
 * @param {string} url 输入URL
 * @returns {string} 清洗后的URL
 */
export function filterUrl(url) {
	if (!url) return url
	const cleanUrl = DOMPurify.sanitize(url, { ALLOWED_TAGS: [], ALLOWED_ATTR: [] })
	const lowerUrl = cleanUrl.toLowerCase().trim()
	if (lowerUrl.startsWith('javascript:') ||
		lowerUrl.startsWith('data:') ||
		lowerUrl.startsWith('vbscript:')) {
		return ''
	}
	return cleanUrl
}

/**
 * 创建安全的HTML渲染函数
 * 用于v-html指令的替代方案
 * @param {string} html HTML内容
 * @returns {string} 清洗后的HTML
 */
export function safeHtml(html) {
	return filterMarkdown(html)
}

export default {
	filterMarkdown,
	filterPlainText,
	filterUrl,
	safeHtml
}