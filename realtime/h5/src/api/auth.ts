export interface LoginParams {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  expiresIn: number
}

export async function loginByMock(params: LoginParams): Promise<LoginResult> {
  const username = params.username.trim()
  const password = params.password.trim()
  await new Promise(resolve => setTimeout(resolve, 500))

  if (!username || !password)
    throw new Error('请输入用户名和密码')

  if (username !== 'demo' || password !== '123456')
    throw new Error('账号或密码错误（模拟账号: demo / 123456）')

  return {
    token: `mock-token-${Date.now()}`,
    expiresIn: 7200,
  }
}

export async function logoutByMock(): Promise<void> {
  await new Promise(resolve => setTimeout(resolve, 300))
}
