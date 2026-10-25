package kr.ac.kumoh.s20220636.repository

import kr.ac.kumoh.s20220636.model.Pokemon

interface PokemonRepository {
    suspend fun getAllPokemon(): List<Pokemon>
    suspend fun getPokemonById(id: Int): Pokemon?
}