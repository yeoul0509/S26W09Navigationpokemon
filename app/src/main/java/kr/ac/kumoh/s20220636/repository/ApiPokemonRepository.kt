package kr.ac.kumoh.s20220636.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.json.Json
import kr.ac.kumoh.s20220636.model.Pokemon
import kr.ac.kumoh.s20220636.model.PokemonStat
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
private const val KOREAN = "ko"

class ApiPokemonRepository(
    private val api: PokeApi = pokeService
) : PokemonRepository {

    companion object {
        private val jsonConfig = Json {
            ignoreUnknownKeys = true
        }

        private val pokeService: PokeApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(
                    jsonConfig.asConverterFactory("application/json".toMediaType())
                )
                .build()
                .create(PokeApi::class.java)
        }
    }

    override suspend fun getAllPokemon(): List<Pokemon> = coroutineScope {
        api.getPokemonList().results
            .mapNotNull { result ->
                val id = result.url
                    .trimEnd('/')
                    .substringAfterLast('/')
                    .toIntOrNull()
                    ?: return@mapNotNull null
                async {
                    runCatching { fetchPokemon(id) }.getOrNull()
                }
            }
            .awaitAll()
            .filterNotNull()
    }

    override suspend fun getPokemonById(id: Int): Pokemon? =
        runCatching { fetchPokemon(id) }.getOrNull()

    private suspend fun fetchPokemon(id: Int): Pokemon = coroutineScope {
        val detail = async { api.getPokemonDetail(id) }
        val species = async { runCatching { api.getPokemonSpecies(id) }.getOrNull() }

        val dto = detail.await()
        val speciesDto = species.await()

        val types = dto.types
            .sortedBy { it.slot }
            .map { translateTypeToKorean(it.type.name) }
            .joinToString(", ")
            .ifEmpty { "알 수 없음" }

        val stats = dto.stats.map { statSlot ->
            PokemonStat(
                name = translateStatToKorean(statSlot.stat.name),
                value = statSlot.baseStat
            )
        }

        // 고화질 공식 일러스트가 있으면 우선 사용하고, 없으면 기본 스프라이트 사용
        val imageUrl = dto.sprites.other?.officialArtwork?.frontDefault
            ?: dto.sprites.frontDefault
            ?: ""

        Pokemon(
            id = dto.id,
            name = speciesDto?.koreanName ?: dto.name,
            type = types,
            info = speciesDto?.koreanInfo.orEmpty().ifEmpty { "등록된 도감 설명이 없습니다." },
            image = imageUrl,
            genus = speciesDto?.koreanGenus ?: "미분류 포켓몬",
            height = dto.height / 10f, // dm -> m 변환
            weight = dto.weight / 10f, // hg -> kg 변환
            stats = stats
        )
    }

    private fun translateTypeToKorean(type: String): String = when (type.lowercase()) {
        "normal" -> "노말"
        "fire" -> "불꽃"
        "water" -> "물"
        "grass" -> "풀"
        "electric" -> "전기"
        "ice" -> "얼음"
        "fighting" -> "격투"
        "poison" -> "독"
        "ground" -> "땅"
        "flying" -> "비행"
        "psychic" -> "에스퍼"
        "bug" -> "벌레"
        "rock" -> "바위"
        "ghost" -> "고스트"
        "dragon" -> "드래곤"
        "dark" -> "악"
        "steel" -> "강철"
        "fairy" -> "페어리"
        else -> type
    }

    private fun translateStatToKorean(stat: String): String = when (stat.lowercase()) {
        "hp" -> "HP"
        "attack" -> "공격"
        "defense" -> "방어"
        "special-attack" -> "특수공격"
        "special-defense" -> "특수방어"
        "speed" -> "스피드"
        else -> stat
    }
}

private val PokemonSpeciesDto.koreanName: String?
    get() = names.firstOrNull { it.language.name == KOREAN }?.name

private val PokemonSpeciesDto.koreanInfo: String?
    get() = flavorTextEntries
        .firstOrNull { it.language.name == KOREAN }
        ?.flavorText
        ?.replace(Regex("\\s+"), " ")
        ?.trim()

private val PokemonSpeciesDto.koreanGenus: String?
    get() = genera.firstOrNull { it.language.name == KOREAN }?.genus