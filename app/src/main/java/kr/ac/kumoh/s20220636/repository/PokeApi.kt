package kr.ac.kumoh.s20220636.repository

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 100
    ): PokemonListDto

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(
        @Path("id") id: Int
    ): PokemonDetailDto

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(
        @Path("id") id: Int
    ): PokemonSpeciesDto
}

@Serializable
data class PokemonListDto(
    @SerialName("results") val results: List<ResultDto>
) {
    @Serializable
    data class ResultDto(
        @SerialName("url") val url: String
    )
}

@Serializable
data class PokemonDetailDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("height") val height: Int = 0, // 단위: 데시미터 (10dm = 1m)
    @SerialName("weight") val weight: Int = 0, // 단위: 헥토그램 (10hg = 1kg)
    @SerialName("types") val types: List<TypeSlotDto> = emptyList(),
    @SerialName("sprites") val sprites: SpritesDto,
    @SerialName("stats") val stats: List<StatSlotDto> = emptyList()
) {
    @Serializable
    data class TypeSlotDto(
        @SerialName("slot") val slot: Int,
        @SerialName("type") val type: TypeDto
    )

    @Serializable
    data class TypeDto(
        @SerialName("name") val name: String
    )

    @Serializable
    data class SpritesDto(
        @SerialName("front_default") val frontDefault: String? = null,
        @SerialName("other") val other: OtherSpritesDto? = null
    )

    @Serializable
    data class OtherSpritesDto(
        @SerialName("official-artwork") val officialArtwork: OfficialArtworkDto? = null
    )

    @Serializable
    data class OfficialArtworkDto(
        @SerialName("front_default") val frontDefault: String? = null
    )

    @Serializable
    data class StatSlotDto(
        @SerialName("base_stat") val baseStat: Int,
        @SerialName("stat") val stat: StatDto
    )

    @Serializable
    data class StatDto(
        @SerialName("name") val name: String
    )
}

@Serializable
data class LanguageDto(
    @SerialName("name") val name: String
)

@Serializable
data class PokemonSpeciesDto(
    @SerialName("names") val names: List<NameDto> = emptyList(),
    @SerialName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntryDto> = emptyList(),
    @SerialName("genera") val genera: List<GenusDto> = emptyList()
) {
    @Serializable
    data class NameDto(
        @SerialName("name") val name: String,
        @SerialName("language") val language: LanguageDto
    )

    @Serializable
    data class FlavorTextEntryDto(
        @SerialName("flavor_text") val flavorText: String,
        @SerialName("language") val language: LanguageDto
    )

    @Serializable
    data class GenusDto(
        @SerialName("genus") val genus: String,
        @SerialName("language") val language: LanguageDto
    )
}