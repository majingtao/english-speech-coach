/** Per-session drafts, including audio. A session is authorized before loading its draft. */
const DATABASE = "english-coach-drafts"
async function open() {
  return new Promise<IDBDatabase>((resolve, reject) => {
    const request = indexedDB.open(DATABASE, 1)
    request.onupgradeneeded = () => request.result.createObjectStore("drafts")
    request.onsuccess = () => resolve(request.result)
    request.onerror = () => reject(request.error)
  })
}
export async function readCoachDraft<T>(id: string): Promise<T | null> {
  const db = await open()
  try {
    return await new Promise<T | null>((resolve, reject) => {
      const request = db.transaction("drafts").objectStore("drafts").get(id)
      request.onsuccess = () => resolve(request.result || null)
      request.onerror = () => reject(request.error)
    })
  } finally { db.close() }
}
export async function writeCoachDraft(id: string, value: unknown) {
  const db = await open()
  try {
    await new Promise<void>((resolve, reject) => {
      const transaction = db.transaction("drafts", "readwrite")
      if (value === null) transaction.objectStore("drafts").delete(id)
      else transaction.objectStore("drafts").put(value, id)
      transaction.oncomplete = () => resolve()
      transaction.onerror = () => reject(transaction.error)
    })
  } finally { db.close() }
}
