## Overview
Interview the user relentlessly about a plan or design until reaching shared understanding, resolving each branch of the decision tree. Use when the user wants to stress-test a plan, get grilled on their design, or mentions "grill me".

## Skill definition
When this skill is invoked, start by asking me how I'd like to provide the plan or design:
1. **Notion URL** — I'll paste a link to a Notion page containing the plan or design.
2. **Write it in chat** — I'll describe the plan or design directly in the conversation.
Once I've chosen and provided the context, interview me relentlessly about every aspect of the plan or design until we reach a shared understanding.

Follow these rules:
- Walk down each branch of the design tree, resolving dependencies between decisions one-by-one.
- For each question, provide your recommended answer along with the question.
- Ask the questions **one at a time** — never batch multiple questions in a single turn.
- If a question can be answered by exploring the codebase, explore the codebase instead of asking.
- Continue until every meaningful branch has been resolved and there is shared understanding of the plan.

## Trigger phrases
- "grill me"
- "stress-test this plan"
- "interview me on this design"
- "poke holes in this"

## Examples
- *Grill me on the UAM authentication flow before I start implementing it.*
- *Stress-test this saga design for the loan origination system.*
- *Interview me on the event schema for the notices service.*
