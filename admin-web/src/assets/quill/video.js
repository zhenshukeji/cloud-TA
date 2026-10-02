import { Quill } from 'vue-quill-editor'

// 源码中是import直接倒入，这里要用Quill.import引入
const BlockEmbed = Quill.import('blots/block/embed')
const Link = Quill.import('formats/link')

const ATTRIBUTES = ['height', 'width']

class Video extends BlockEmbed {
  static create(value) {
    const node = super.create(value)
    // 添加video标签所需的属性
    node.setAttribute('controls', 'controls')
    node.setAttribute('type', 'video/mp4')
    node.setAttribute('src', this.sanitize(value))
    return node
  }

  static formats(domNode) {
    return ATTRIBUTES.reduce((formats, attribute) => {
      if (domNode.hasAttribute(attribute)) {
        formats[attribute] = domNode.getAttribute(attribute)
      }
      return formats
    }, {})
  }

  static sanitize(url) {
    return Link.sanitize(url) // eslint-disable-line import/no-named-as-default-member
  }

  static value(domNode) {
    return domNode.getAttribute('src')
  }

  format(name, value) {
    if (ATTRIBUTES.indexOf(name) > -1) {
      if (value) {
        this.domNode.setAttribute(name, value)
      } else {
        this.domNode.removeAttribute(name)
      }
    } else {
      super.format(name, value)
    }
  }

  html() {
    const { video } = this.value()
    return `<a href="${video}">${video}</a>`
  }
}

class Audio extends BlockEmbed {
  static create(value) {
    let node = super.create();
    node.setAttribute('src', this.sanitize(value));   //设置audio的src属性
    node.setAttribute('controls', true);   //设置audio的controls，否则他将不会显示
    node.setAttribute('controlsList', 'nodownload');   //设置audio的下载功能为不能下载
    node.setAttribute('id', 'voice');     //设置一个id
    return node;
  }
  static formats(domNode) {
    return ATTRIBUTES.reduce((formats, attribute) => {
      if (domNode.hasAttribute(attribute)) {
        formats[attribute] = domNode.getAttribute(attribute)
      }
      return formats
    }, {})
  }
  static sanitize(url) {
    return Link.sanitize(url) // eslint-disable-line import/no-named-as-default-member
  }
}
Audio.blotName = 'audio';
Audio.tagName = 'audio';

Video.blotName = 'video' // 这里不用改，楼主不用iframe，直接替换掉原来，如果需要也可以保留原来的，这里用个新的blot
Video.className = 'ql-video'
Video.tagName = 'video' // 用video标签替换iframe

export {
  Video,
  Audio
}

