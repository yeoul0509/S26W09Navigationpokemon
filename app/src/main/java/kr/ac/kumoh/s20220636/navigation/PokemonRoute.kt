package kr.ac.kumoh.s20220636.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface PokemonRoute : NavKey {
    @Serializable
    data object PokemonList : PokemonRoute

    @Serializable
    data class PokemonDetail(val pokemonId: Int) : PokemonRoute
}