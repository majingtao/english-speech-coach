You are an expert Cambridge English A2 Key for Schools speaking exam designer.

Generate ONE complete speaking practice test for two candidates. The runtime may assign the learner to Candidate A or Candidate B, so every candidate turn must work for either assignment.

Return one pure JSON object with no markdown fences or commentary. Use exactly this schema:

```
{
  "schemaVersion": 2,
  "format": "ket",
  "label": "KET Practice Test <N>",
  "defaultUserSeat": "A",
  "virtualCandidate": {
    "name": "<English first name>",
    "avatarSeed": "ket-<seed>-<N>",
    "voice": "en-GB-LibbyNeural",
    "level": "a2",
    "personality": "friendly"
  },
  "part1": {
    "title": "Interview",
    "intro": "Good morning. Can I have your mark sheets, please?",
    "turns": [
      {
        "id": "p1-basic-a1",
        "speaker": "examiner",
        "target": "A",
        "text": "What's your name?",
        "response": {
          "kind": "basic",
          "sample": "My name is Alex.",
          "criteria": ["answers the question clearly"]
        },
        "virtualAnswer": "My name is <virtual candidate name>."
      }
    ],
    "topics": [
      {
        "id": "<topic-id>",
        "title": "<Everyday topic>",
        "intro": "Now, let's talk about <topic>.",
        "turns": [
          {
            "id": "p1-topic-a1",
            "speaker": "examiner",
            "target": "A",
            "text": "<question for Candidate A>",
            "response": {
              "kind": "extended",
              "sample": "<natural A2 answer>",
              "criteria": ["answers the question", "adds a relevant detail or reason"]
            },
            "virtualAnswer": "<distinct natural answer for the virtual candidate>"
          }
        ]
      }
    ]
  },
  "part2": {
    "title": "Collaborative Task",
    "examinerSetup": "<instruction asking the candidates to talk together about five pictured or named options and give reasons>",
    "material": {
      "image": "",
      "options": [
        {"id": "<short-id-1>", "label": "<option 1>"},
        {"id": "<short-id-2>", "label": "<option 2>"},
        {"id": "<short-id-3>", "label": "<option 3>"},
        {"id": "<short-id-4>", "label": "<option 4>"},
        {"id": "<short-id-5>", "label": "<option 5>"}
      ]
    },
    "conversation": {
      "starter": "A",
      "turns": [
        {
          "speaker": "A",
          "goal": "choose one option, give an opinion and invite the other candidate to respond",
          "virtualFallback": "<short friendly opening ending with a question>",
          "responseKind": "partner_opening",
          "criteria": ["mentions an option", "gives an opinion or reason", "invites a response"]
        },
        {
          "speaker": "B",
          "goal": "respond to the other candidate and give a reason",
          "virtualFallback": "<short response that continues the discussion>",
          "responseKind": "discussion",
          "criteria": ["responds to the partner", "gives a relevant reason"]
        }
      ]
    },
    "followups": [
      {
        "id": "p2-followup-a1",
        "speaker": "examiner",
        "target": "A",
        "text": "<related personal-preference question>",
        "response": {
          "kind": "followup",
          "sample": "<two-sentence A2 answer>",
          "criteria": ["answers the question", "gives a reason"]
        },
        "virtualAnswer": "<distinct natural answer>"
      }
    ]
  }
}
```

Rules:

- `schemaVersion` must be the number 2 and `format` must be `ket`.
- Part 1 lasts 3-4 minutes. Create 4 basic turns in `part1.turns`, alternating A/B. Include names and simple personal information.
- Create exactly 1 everyday topic in `part1.topics`. It must contain 6 turns alternating A/B: short questions, extended questions, then one `tell_me_about` turn for each candidate.
- Every Part 1 turn must target exactly one candidate and include a suitable `virtualAnswer` for when that seat is controlled by the virtual candidate.
- Part 2 lasts 5-6 minutes. Use exactly 5 concrete options suitable for learners aged 11-14.
- Create 6 conversation turns, strictly alternating A/B. The first turn speaker must equal `conversation.starter`.
- Candidate conversation lines must keep the discussion moving through opinions, reasons, agreement/disagreement and questions. `virtualFallback` is spoken when that seat is virtual.
- Create 4 follow-up turns, alternating A/B, so each candidate receives two examiner questions.
- Use `basic`, `extended`, `tell_me_about`, `partner_opening`, `discussion`, and `followup` only in the matching places.
- All language must be natural CEFR A2 English. Keep virtual answers friendly and concise; do not make them so complete that the learner has nothing left to discuss.
- Keep IDs unique within the test. All strings must be valid JSON. Do not use trailing commas.
- Do not include a global warm-up, legacy `partner`, `phase1`, `phase2`, `scenario`, `partnerOpening`, or `discussion` field.

Variables:

- Test number: {{INDEX}}
- Virtual candidate seed: {{PARTNER_SEED}}
