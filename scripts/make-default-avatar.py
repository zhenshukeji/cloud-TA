#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""生成前端默认头像 `admin-web/src/assets/image/profile.jpg`。

背景（为什么要生成而不是用现成图）：
  改造前这张默认头像是一张**真实人物照片**，来源不可追溯、没有任何授权凭证，
  直接放进开源仓库等于再分发他人肖像。这里改成本脚本**确定性生成**的中性插画头像：
  * 无第三方权利（生成物，不派生自任何外部素材）；
  * 可复现（同样的参数跑出同样的字节）；
  * 不需要联网、不需要设计资源。

用法：
    python community-edition/scripts/make-default-avatar.py [输出路径]
默认输出 198x198（与改造前尺寸一致，布局不变），先按 6 倍超采样绘制再缩小，边缘无锯齿。
"""
import io
import os
import sys

from PIL import Image, ImageDraw

SIZE = 198
SS = 6  # 超采样倍数
BG = (247, 245, 240)       # 品牌浅底 #F7F5F0
RING = (222, 216, 204)     # 描边
BODY = (56, 79, 67, 255)   # 品牌绿 #384F43
SKIN = (240, 224, 208, 255)
HAIR = (56, 79, 67, 255)
SHIRT = (166, 83, 48, 255)  # 品牌橙 #A65330


def render(size=SIZE):
    n = size * SS
    img = Image.new('RGB', (n, n), BG)
    d = ImageDraw.Draw(img, 'RGBA')

    # 圆形裁切提示用描边（视觉上收住「圆形头像」的边界）
    d.ellipse([0, 0, n - 1, n - 1], outline=RING, width=max(2, n // 90))

    # 肩部 / 上衣：一个从底部升起的大圆弧
    d.ellipse([n * 0.10, n * 0.62, n * 0.90, n * 1.42], fill=SHIRT)
    # 衣领
    d.polygon([(n * 0.38, n * 0.70), (n * 0.50, n * 0.90), (n * 0.62, n * 0.70)], fill=BG)

    # 脖子
    d.rounded_rectangle([n * 0.44, n * 0.52, n * 0.56, n * 0.72],
                        radius=n * 0.05, fill=SKIN)

    # 头
    d.ellipse([n * 0.30, n * 0.18, n * 0.70, n * 0.62], fill=SKIN)
    # 头发（上半个圆 + 两侧鬓角）
    d.pieslice([n * 0.28, n * 0.14, n * 0.72, n * 0.62], 180, 360, fill=HAIR)
    d.ellipse([n * 0.29, n * 0.24, n * 0.36, n * 0.46], fill=HAIR)
    d.ellipse([n * 0.64, n * 0.24, n * 0.71, n * 0.46], fill=HAIR)

    # 眼睛
    for cx in (0.415, 0.585):
        d.ellipse([n * (cx - 0.024), n * 0.415, n * (cx + 0.024), n * 0.465], fill=HAIR)
    # 嘴
    d.arc([n * 0.44, n * 0.47, n * 0.56, n * 0.57], 20, 160, fill=(150, 96, 84, 255),
          width=max(2, n // 120))

    return img.resize((size, size), Image.LANCZOS)


def main():
    out = sys.argv[1] if len(sys.argv) > 1 else os.path.join(
        os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
        'admin-web', 'src', 'assets', 'image', 'profile.jpg')
    img = render()
    buf = io.BytesIO()
    # 固定参数 + 不写元数据 → 同样的输入产生同样的字节，可复算
    img.save(buf, format='JPEG', quality=90, optimize=True, subsampling=0)
    data = buf.getvalue()
    with open(out, 'wb') as fh:
        fh.write(data)
    import hashlib
    print('written %s  %d bytes  sha256=%s' % (out, len(data),
                                               hashlib.sha256(data).hexdigest()))


if __name__ == '__main__':
    main()
