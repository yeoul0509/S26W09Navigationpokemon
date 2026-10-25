package kr.ac.kumoh.s20220636.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kr.ac.kumoh.s20220636.model.Pokemon
import kr.ac.kumoh.s20220636.repository.PokemonRepository
import kr.ac.kumoh.s20220636.viewmodel.PokemonListViewModel

// 포켓몬 타입에 따른 대표 색상 반환
fun getPokemonTypeColor(type: String): Color {
    val primaryType = type.split(",", "/").firstOrNull()?.trim()?.lowercase() ?: ""
    return when (primaryType) {
        "풀", "grass", "벌레", "bug" -> Color(0xFF4CAF50)
        "불꽃", "fire" -> Color(0xFFFF7043)
        "물", "water", "얼음", "ice" -> Color(0xFF42A5F5)
        "전기", "electric" -> Color(0xFFFFCA28)
        "독", "poison", "고스트", "ghost" -> Color(0xFFAB47BC)
        "에스퍼", "psychic", "페어리", "fairy" -> Color(0xFFEC407A)
        "땅", "ground", "바위", "rock", "격투", "fighting" -> Color(0xFF8D6E63)
        "드래곤", "dragon" -> Color(0xFF5C6BC0)
        "악", "dark", "강철", "steel" -> Color(0xFF78909C)
        else -> Color(0xFF9E9E9E)
    }
}

@Composable
fun PokemonListScreen(
    repository: PokemonRepository,
    viewModel: PokemonListViewModel = viewModel {
        PokemonListViewModel(repository)
    },
    onNavigateToDetail: (Int) -> Unit,
) {
    val pokemonList by viewModel.pokemonList.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pokemonList, key = { it.id }) { pokemon ->
            PokemonCard(pokemon) {
                onNavigateToDetail(pokemon.id)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PokemonCard(
    pokemon: Pokemon,
    onClick: (Int) -> Unit
) {
    val typeColor = getPokemonTypeColor(pokemon.type)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = { onClick(pokemon.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            typeColor.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 포켓몬 이미지 영역
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(typeColor.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = pokemon.image,
                    contentDescription = "${pokemon.name} 이미지",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(74.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // 텍스트 및 타입 뱃지 영역
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = pokemon.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "#${pokemon.id.toString().padStart(3, '0')}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                if (pokemon.genus.isNotEmpty()) {
                    Text(
                        text = pokemon.genus,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // 콤마(,) 또는 슬래시(/)로 구분된 타입을 개별 Chip으로 분리하여 출력
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    pokemon.type.split(",", "/").map { it.trim() }.filter { it.isNotEmpty() }.forEach { singleType ->
                        val badgeColor = getPokemonTypeColor(singleType)
                        Surface(
                            color = badgeColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50),
                        ) {
                            Text(
                                text = singleType,
                                color = badgeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}