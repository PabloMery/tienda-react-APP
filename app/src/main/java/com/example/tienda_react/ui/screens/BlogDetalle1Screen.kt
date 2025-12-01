package com.example.tienda_react.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
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
fun BlogDetalle1Screen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.rodandochile2),
            contentDescription = "Ganador Red Bull",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "Ganador Red Bull Rodando en Callampark",
            color = Color.Black,
            fontSize = 24.sp,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Callampark, La Florida — Red Bull Rodando Chile — 2025",
            color = Color.DarkGray,
            fontSize = 14.sp,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "El Red Bull Rodando Chile se tomó el Callampark (abajo de La Florida) con una jornada que reunió lo mejor del ciclismo urbano y competencias de diversas categorías.\n",
            color = Color.Black,
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Tras mangas clasificatorias y una final de infarto, el ganador Pablo Sánchez se impuso con técnica, control y velocidad, destacando entre riders de todo Chile. La energía del público convirtió la pista en una verdadera fiesta del deporte urbano.\n",
            color = Color.Black,
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Agradecemos a la comunidad que asistió y a quienes apoyaron el evento. ¡Nos vemos en la próxima fecha!\n",
            color = Color.Black,
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Productos mencionados",
            color = Color.Black,
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Text(
            text = "Explora equipamiento similar al usado por los competidores:",
            color = Color.Black,
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        TextButton(onClick = { /* abrir URL o navegar a productos */ }) {
            Text(
                text = "Bicicleta BMX Wtp Trust Cs Rsd Matt Black",
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                fontSize = 14.sp
            )
        }

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Volver al blog", color = Color.White)
        }
    }
}

