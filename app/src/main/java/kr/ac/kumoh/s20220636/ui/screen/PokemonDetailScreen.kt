package kr.ac.kumoh.s20220636.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kr.ac.kumoh.s20220636.repository.PokemonRepository
import kr.ac.kumoh.s20220636.viewmodel.PokemonDetailViewModel

@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    repository: PokemonRepository,
    viewModel: PokemonDetailViewModel = viewModel {
        PokemonDetailViewModel(
            repository = repository,
            pokemonId = pokemonId
        )
    },
) {
    val pokemon by viewModel.pokemon.collectAsStateWithLifecycle()

    Column(

    ) {
        Text(pokemon.name)
        Text(pokemon.info)
    }
}