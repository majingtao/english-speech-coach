export type YudaoResponse<T> = {
  code: number
  msg: string
  data: T
}

export class ApiBusinessError extends Error {
  code?: number

  constructor(message: string, code?: number) {
    super(message)
    this.name = "ApiBusinessError"
    this.code = code
  }
}

