package com.liber.book_read_management.dto.semantic

/*
{
  "type": "object",
  "properties": {
    "abstraction": { "type": "number", "description": "추상화 정도" },
    "emotion": { "type": "number", "description": "감정적 깊이" },
    "challenge": { "type": "number", "description": "도전 과제" },
    "narrative": { "type": "number", "description": "서사성" },
    "confidence": { "type": "number", "description": "신뢰도" }
  },
  "required": ["abstraction", "emotion", "challenge", "narrative", "confidence"]
}
 */
class SemanticScoreSchema(
    val type: String = "object",
    val properties: Map<String, InfoSchema> = mapOf(
        "abstraction" to InfoSchema(description = "추상화 정도"),
        "emotion" to InfoSchema(description = "감정적 깊이"),
        "challenge" to InfoSchema(description = "도전 과제"),
        "narrative" to InfoSchema(description = "서사성"),
        "confidence" to InfoSchema(description = "신뢰도")
    ),
    val required: List<String> = listOf(
        "abstraction",
        "emotion",
        "challenge",
        "narrative",
        "confidence"
    )
)

class InfoSchema(
    val type: String = "number",
    val description: String
)