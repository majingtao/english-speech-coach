/**
 * 联系方式配置 —— 改这里即可，页面会自动更新。
 * 值留空（""）的渠道不会显示。
 *
 * 二维码图片放到 public/contact/ 目录下（文件名见 qr 字段），图片不存在时页面显示“二维码待上传”。
 * 注意：微信群二维码 7 天过期，群满 200 人后也不能再扫码进群；
 * 建议改放个人微信二维码（长期有效）或企业微信“活码”。
 */
export const CONTACT = {
  wechat: {
    id: "majingtao",
    groupName: "AI落地交流",
    /** 微信群二维码（7 天过期，记得定期替换这张图） */
    groupQr: "/contact/wechat-group.png",
    /** 个人微信二维码（可选，长期有效）；留空不显示 */
    personalQr: "",
  },
  x: {
    handle: "finch010101",
  },
  telegram: {
    groupName: "AI落地交流",
    /** 群二维码 */
    groupQr: "/contact/telegram-group.png",
    /** 群邀请链接，如 https://t.me/xxxx；填了会显示“加入群组”按钮 */
    groupUrl: "https://t.me/+SjCr-0ubzqhmOTE0",
  },
  /** 海外常用，填了才显示 */
  email: "xiaoxiao520@gmail.com",
  discordUrl: "",
  whatsappUrl: "",
} as const
