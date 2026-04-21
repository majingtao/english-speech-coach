import type { CreateOrderParams, MockOrder, PayChannel } from "@/lib/types/payment"

export async function createMockOrder(
  params: CreateOrderParams,
): Promise<MockOrder> {
  await new Promise((resolve) => setTimeout(resolve, 400))
  return {
    orderId: `MOCK-${Date.now()}`,
    planName: params.planName,
    period: params.period,
    amount: params.amount,
    createdAt: new Date().toISOString(),
  }
}

export async function payByMock(
  orderId: string,
  channel: PayChannel,
): Promise<{ success: boolean; message: string }> {
  await new Promise((resolve) => setTimeout(resolve, 800))
  return {
    success: true,
    message: `${channel === "alipay" ? "支付宝" : "微信支付"}模拟支付成功（订单 ${orderId}）`,
  }
}
