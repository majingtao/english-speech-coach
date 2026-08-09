import type {
  CandidateSeat,
  ExamStep,
  KetExaminerTurn,
  KetPart1V2,
  KetPart2V2,
  QuestionBank,
} from "@/lib/types/speech"

export function buildSteps(
  bank: QuestionBank,
  testId: string,
  startPart: string,
  options: { userSeat?: CandidateSeat } = {},
): ExamStep[] {
  const test = bank.tests[testId] as Record<string, unknown> | undefined
  if (!test) return []

  if (test.format === "ket") {
    return buildKetSteps(test, startPart, options.userSeat || "A")
  }

  const steps: ExamStep[] = []

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

function buildKetSteps(test: Record<string, unknown>, startPart: string, userSeat: CandidateSeat): ExamStep[] {
  const steps: ExamStep[] = []
  const parts = startPart === "all" ? ["1", "2"] : [startPart]

  if (test.schemaVersion !== 2) {
    return []
  }

  const addExaminerTurn = (turn: KetExaminerTurn, label: string) => {
    steps.push({ type: "speak", text: turn.text, target: turn.target, label })
    if (turn.target === userSeat) {
      const criteria = turn.response.criteria?.length
        ? ` Communication goals: ${turn.response.criteria.join("; ")}.`
        : ""
      steps.push({
        type: "judge",
        question: `${turn.text}${criteria}`,
        expected: "",
        sample: turn.response.sample || "",
        open_ended: true,
        kind: turn.response.kind,
        target: turn.target,
        label,
      })
    } else {
      steps.push({ type: "candidate-speak", seat: turn.target, text: turn.virtualAnswer, label })
    }
  }

  for (const pn of parts) {
    if (pn === "1") {
      const part1 = test.part1 as KetPart1V2 | undefined
      if (!part1) continue

      if (part1.intro) {
        steps.push({ type: "speak", text: part1.intro, label: "Part 1 — Interview" })
      }
      part1.turns.forEach((turn, i) => {
        addExaminerTurn(turn, `Part 1 Phase 1: ${i + 1}/${part1.turns.length}`)
      })
      part1.topics.forEach((topic) => {
        const topicLabel = `Part 1 Phase 2 — ${topic.title}`
        if (topic.intro) {
          steps.push({ type: "speak", text: topic.intro, label: topicLabel })
        }
        topic.turns.forEach((turn, i) => {
          addExaminerTurn(turn, `${topicLabel} (${i + 1}/${topic.turns.length})`)
        })
      })
    }

    if (pn === "2") {
      const part2 = test.part2 as KetPart2V2 | undefined
      if (!part2) continue

      steps.push({
        type: "show-ket-material",
        image: part2.material.image,
        options: part2.material.options,
        label: "Part 2",
      })
      steps.push({
        type: "speak",
        text: part2.examinerSetup,
        label: `Part 2 — ${part2.title || "Collaborative Task"}`,
      })

      part2.conversation.turns.forEach((turn, i) => {
        const label = `Part 2 discussion ${i + 1}/${part2.conversation.turns.length}`
        if (turn.speaker === userSeat) {
          const criteria = turn.criteria?.length ? ` Criteria: ${turn.criteria.join("; ")}.` : ""
          steps.push({
            type: "judge",
            question: `${part2.examinerSetup} Your communication goal: ${turn.goal}.${criteria}`,
            expected: "",
            sample: "",
            open_ended: true,
            kind: turn.responseKind,
            target: turn.speaker,
            label,
          })
        } else {
          steps.push({ type: "candidate-speak", seat: turn.speaker, text: turn.virtualFallback, label })
        }
      })

      part2.followups.forEach((turn, i) => {
        addExaminerTurn(turn, `Part 2 follow-up ${i + 1}/${part2.followups.length}`)
      })
    }
  }

  steps.push({ type: "speak", text: "OK. Thank you. Goodbye.", label: "End" })
  steps.push({ type: "end" })
  return steps
}
