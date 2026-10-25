package kr.ac.kumoh.s20220636.model

// 능력치 이름(예: HP, 공격)과 수치(예: 45, 49)를 담는 데이터 클래스
data class PokemonStat(
    val name: String,
    val value: Int
)
data class Pokemon(
    val id: Int,
    val name: String,
    val type: String,
    val info: String,
    val image: String,
    val genus: String = "", // 포켓몬 분류
    val height: Float = 0f, // 단위: m
    val weight: Float = 0f, // 단위: kg
    val stats: List<PokemonStat> = emptyList(), // 종족값
)