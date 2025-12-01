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
fun BlogDetalle2Screen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.bannerblogsorteonoticia2),
            contentDescription = "Banner sorteo/notice",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "¡Prepárate para ser ciclista!",
            color = Color.Black,
            fontSize = 24.sp,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Anuncio de tienda — 2025",
            color = Color.DarkGray,
            fontSize = 14.sp,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "¡Seguimos celebrando la victoria de Pablo Sánchez en Red Bull Rodando en Callampark! Para impulsar a más personas a subirse a la bicicleta, anunciaremos descuentos próximos en la bicicleta que utilizó en el evento:\n",
            color = Color.Black,
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            TextButton(onClick = { /* abrir URL o navegar a productos */ }) {
                Text(
                    text = "Bicicleta BMX Wtp Trust Cs Rsd Matt Black",
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Rendimiento probado.",
                color = Color.DarkGray,
                fontSize = 14.sp
            )
        }
        Text(
            text = "\nMantente atento a los anuncios en tienda, nuestra página web y redes sociales, donde liberaremos toda la información de la campaña. ¡Es tu momento para dar el primer pedaleo!\n",
            color = Color.Black,
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "*Los descuentos y condiciones serán publicados oficialmente en nuestros canales. Stock sujeto a disponibilidad.",
            color = Color.Black,
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 8.dp)
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
