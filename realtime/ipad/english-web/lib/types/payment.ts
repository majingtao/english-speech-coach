export interface CreateOrderParams {
  planName: string
  period: string
  amount: number
}

export interface MockOrder {
  orderId: string
  planName: string
  period: string
  amount: number
  createdAt: string
}

export type PayChannel = "alipay" | "wechat"
