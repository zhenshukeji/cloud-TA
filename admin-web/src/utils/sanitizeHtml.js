/**
 * 富文本净化工具（XSS 防护）
 *
 * 背景：后台多处使用 v-html 渲染后端返回的富文本（资讯、评价、作业、活动介绍等），
 * 直接渲染存在 XSS 风险（可注入 script / onerror / javascript: 等）。
 *
 * 说明：项目当前未引入 DOMPurify，这里使用浏览器原生 DOMParser 做白名单过滤：
 *   - 仅保留白名单内的标签；
 *   - 剔除所有不在白名单内的属性（含 on* 事件属性）；
 *   - 拦截 javascript: / vbscript: 等危险协议。
 * 后续如需更强能力，建议引入 DOMPurify 替换本实现（无需改动调用方）。
 */

// 允许保留的标签
const ALLOWED_TAGS = [
  'a', 'b', 'strong', 'i', 'em', 'u', 's', 'p', 'br', 'hr', 'span', 'div', 'font',
  'img', 'ul', 'ol', 'li', 'blockquote', 'pre', 'code',
  'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
  'table', 'thead', 'tbody', 'tr', 'th', 'td', 'sub', 'sup'
]

// 允许保留的属性
const ALLOWED_ATTRS = [
  'href', 'src', 'alt', 'title', 'width', 'height', 'style', 'class',
  'target', 'align', 'colspan', 'rowspan', 'color', 'size'
]

// 危险协议前缀
const DANGEROUS_PROTOCOL = /^\s*(javascript|vbscript|data:text\/html)/i

function isSafeUrl(value) {
  if (!value) {
    return true
  }
  return !DANGEROUS_PROTOCOL.test(String(value))
}

/**
 * 净化 HTML 字符串
 * @param {String} html 原始富文本
 * @returns {String} 净化后的 HTML
 */
export function sanitizeHtml(html) {
  if (html === null || html === undefined || html === '') {
    return ''
  }
  const source = String(html)
  // 非浏览器环境（如 SSR / 单测）下只用正则剔除危险标签块
  if (typeof window === 'undefined' || typeof window.DOMParser === 'undefined') {
    return source.replace(/<\s*(script|style|iframe|object|embed)[^>]*>[\s\S]*?<\s*\/\s*\1\s*>/gi, '')
  }
  const doc = new window.DOMParser().parseFromString(source, 'text/html')
  const walker = doc.createTreeWalker(doc.body, window.NodeFilter.SHOW_ELEMENT, null, false)
  const nodes = []
  let node = walker.currentNode
  while (node) {
    nodes.push(node)
    node = walker.nextNode()
  }
  for (let i = 0; i < nodes.length; i++) {
    const el = nodes[i]
    const tag = el.tagName ? el.tagName.toLowerCase() : ''
    if (ALLOWED_TAGS.indexOf(tag) === -1) {
      // 不在白名单：保留其子节点内容，去掉标签本身
      if (el.parentNode) {
        while (el.firstChild) {
          el.parentNode.insertBefore(el.firstChild, el)
        }
        el.parentNode.removeChild(el)
      }
      continue
    }
    // 清理属性
    const attrs = Array.prototype.slice.call(el.attributes || [])
    for (let j = 0; j < attrs.length; j++) {
      const name = attrs[j].name
      const lower = name.toLowerCase()
      if (ALLOWED_ATTRS.indexOf(lower) === -1) {
        el.removeAttribute(name)
        continue
      }
      if ((lower === 'href' || lower === 'src') && !isSafeUrl(attrs[j].value)) {
        el.removeAttribute(name)
      }
    }
  }
  return doc.body.innerHTML
}

export default sanitizeHtml
