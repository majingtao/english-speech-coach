import type { ExamStep, QuestionBank } from "@/lib/types/speech"

export function buildSteps(
  bank: QuestionBank,
  testId: string,
  startPart: string,
): ExamStep[] {
  const steps: ExamStep[] = []
  const test = bank.tests[testId] as Record<string, unknown> | undefined
  if (!test) return steps

  const doWarmup = startPart === "all"
  const parts = startPart === "all" ? ["1", "2", "3", "4"] : [startPart]

  if (doWarmup && bank.warmup?.length) {
    bank.warmup.forEach((w, i) => {
      steps.push({ type: "speak", text: w.examiner, label: "Warm-up" })
      steps.push({
        type: "judge",
        question: w.examiner,
        expected: "",
        sample: w.sample || "",
        open_ended: true,
        label: `Warm-up ${i + 1}/${bank.warmup!.length}`,
      })
    })
  }

  for (const pn of parts) {
    const part = test[`part${pn}`] as Record<string, unknown> | undefined
    if (!part) continue

    if (pn === "1") {
      const imgA = part.imageA as string | undefined
      const imgB = part.imageB as string | undefined
      if (imgA || imgB) {
        const imgs = [imgA, imgB].filter(Boolean) as string[]
        steps.push({ type: "show-image", images: imgs, label: "Part 1" })
      }
      if (part.intro) steps.push({ type: "speak", text: part.intro as string, label: `Part 1 — ${(part.title as string) || ""}` })
      if (part.example) steps.push({ type: "speak", text: part.example as string })
      if (part.instruction) steps.push({ type: "speak", text: part.instruction as string })
      const qs = (part.questions || []) as Array<Record<string, string>>
      qs.forEach((q, i) => {
        const label = `Part 1: ${i + 1}/${qs.length}`
        steps.push({ type: "speak", text: q.examiner, label })
        steps.push({ type: "judge", question: q.examiner, expected: q.expected, label })
      })
    }

    if (pn === "2") {
      const imgA = part.imageA as string | undefined
      const imgB = part.imageB as string | undefined
      if (imgA) {
        steps.push({ type: "show-image", images: [imgA], label: "Part 2A" })
      }
      if (part.intro) steps.push({ type: "speak", text: part.intro as string, label: `Part 2 — ${(part.title as string) || ""}` })
      const phaseA = (part.phaseA || []) as Array<Record<string, string>>
      phaseA.forEach((q, i) => {
        const label = `Part 2A: ${i + 1}/${phaseA.length}`
        steps.push({ type: "speak", text: q.examiner, label })
        steps.push({ type: "judge", question: q.examiner, expected: q.expected, label })
      })
      if (imgB) {
        steps.push({ type: "show-image", images: [imgB], label: "Part 2B" })
      }
      if (part.transition) steps.push({ type: "speak", text: part.transition as string, label: "Part 2 — Now you ask" })
      const phaseB = (part.phaseB || []) as Array<Record<string, string>>
      phaseB.forEach((q, i) => {
        const label = `Part 2B: ${i + 1}/${phaseB.length}`
        steps.push({ type: "show-hint", hint: q.hint })
        steps.push({ type: "judge-question", expected_question: q.expected_question, hint: q.hint, label })
        if (q.answer) steps.push({ type: "speak", text: q.answer, label: "Part 2B: answer" })
      })
    }

    if (pn === "3") {
      const rawImgs = part.images as string | string[] | undefined
      if (rawImgs) {
        const imgs = Array.isArray(rawImgs) ? rawImgs : [rawImgs]
        steps.push({ type: "show-image", images: imgs, label: "Part 3" })
      }
      if (part.intro) steps.push({ type: "speak", text: part.intro as string, label: `Part 3 — ${(part.title as string) || ""}` })
      if (part.pic1) steps.push({ type: "speak", text: part.pic1 as string })
      if (part.instruction) steps.push({ type: "speak", text: part.instruction as string })
      const pics = (part.pictures || []) as Array<{ pic: string; prompt: string; sentences: Array<string | { text: string; hint?: string }> }>
      pics.forEach((pic) => {
        steps.push({ type: "speak", text: `Picture ${pic.pic}: ${pic.prompt}`, label: `Part 3: Picture ${pic.pic}` })
        pic.sentences.forEach((s, si) => {
          const sText = typeof s === "string" ? s : s.text
          const sHint = typeof s === "string" ? pic.prompt : (s.hint || pic.prompt)
          steps.push({ type: "judge", question: `Picture ${pic.pic} — ${sHint}`, expected: sText, hint_text: sHint, label: `Part 3: Pic ${pic.pic} (${si + 1}/${pic.sentences.length})` })
        })
      })
    }

    if (pn === "4") {
      steps.push({ type: "show-image", images: [], label: "Part 4" })
      if (part.intro) steps.push({ type: "speak", text: part.intro as string, label: `Part 4 — ${(part.title as string) || ""}` })
      const qs = (part.questions || []) as Array<Record<string, string>>
      qs.forEach((q, i) => {
        const label = `Part 4: ${i + 1}/${qs.length}`
        steps.push({ type: "speak", text: q.examiner, label })
        steps.push({ type: "judge", question: q.examiner, expected: "", sample: q.sample || "", open_ended: true, label })
      })
      const fups = (part.followups || []) as Array<Record<string, string>>
      fups.forEach((q, i) => {
        const label = `Part 4+: ${i + 1}/${fups.length}`
        steps.push({ type: "speak", text: q.examiner, label })
        steps.push({ type: "judge", question: q.examiner, expected: "", sample: q.sample || "", open_ended: true, label })
      })
    }
  }

  steps.push({ type: "speak", text: "OK. Thank you. Goodbye.", label: "End" })
  steps.push({ type: "end" })
  return steps
}
