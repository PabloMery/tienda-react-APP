package com.example.tienda_react.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tienda_react.R

@Composable
fun BlogScreen(
    onOpenDetalle1: () -> Unit,
    onOpenDetalle2: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(bottom = 24.dp)
    ) {
        // Título
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Noticias importantes",
                color = Color.Black,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                fontFamily = FontFamily.SansSerif,
                fontSize = 24.sp,
                modifier = Modifier
                    .width(270.dp)
                    .height(40.dp)
            )
            Text(
                text = "Últimas novedades y curiosidades de la tienda",
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(270.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // --------- Tarjeta 1 ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Noticias importantes",
                color = Color.Black,
                fontSize = 24.sp,
                fontFamily = FontFamily.SansSerif,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            Text(
                text = "02 de noviembre de 2025",
                color = Color.DarkGray,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
            Text(
                text = "En esta sección podrás encontrar las noticias más relevantes de la semana. Mantente informado sobre los últimos eventos y novedades.",
                color = Color.Black,
                fontSize = 16.sp,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Button(onClick = onOpenDetalle1) { Text("Ver más") }
            }
            Image(
                painter = painterResource(id = R.drawable.rodandochile2),
                contentDescription = "Ganador Red Bull",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(20.dp))

        // --------- Tarjeta 2 ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Noticias importantes",
                color = Color.Black,
                fontSize = 24.sp,
                fontFamily = FontFamily.SansSerif,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            Text(
                text = "02 de noviembre de 2025",
                color = Color.DarkGray,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
            Text(
                text = "¡Prepárate para ser ciclista! Descuentos por victoria de Pablo Sánchez en Red Bull Rodando Chile.",
                color = Color.Black,
                fontSize = 16.sp,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Button(onClick = onOpenDetalle2) { Text("Ver más") }
            }
            Image(
                painter = painterResource(id = R.drawable.blog1),
                contentDescription = "Bicicleta con botella",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
