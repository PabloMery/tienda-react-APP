package com.example.tienda_react.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tienda_react.R   // ← IMPORTA TU R

@Composable
fun QuienesSomosScreen() {
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TituloQuienesSomos()
            Spacer(Modifier.height(12.dp))
            FilaImagenTexto()
            Spacer(Modifier.height(16.dp))
            Historia()
            Spacer(Modifier.height(16.dp))
            IntegrantesRow()
            Spacer(Modifier.height(16.dp))
            Contacto()
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun TituloQuienesSomos() {
    Text(
        text = "Quiénes somos",
        color = Color.Black,
        textAlign = TextAlign.Left,
        textDecoration = TextDecoration.Underline,
        fontFamily = FontFamily.SansSerif,
        fontSize = 24.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
}

@Composable
fun FilaImagenTexto() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImagenCuadrada()
        Spacer(Modifier.width(16.dp))
        // Aplica el weight AQUÍ (en scope de Row)
        TextoAlLado(modifier = Modifier.weight(1f, fill = true))
    }
}

@Composable
fun ImagenCuadrada() {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo Tangana",
        modifier = Modifier.size(150.dp)
    )
}

@Composable
fun TextoAlLado(modifier: Modifier = Modifier) {
    Text(
        text = "Somos una empresa dedicada a la venta de patines, monopatines, skateboards y bicicletas BMX, " +
                "pensada especialmente para jóvenes que buscan expresar su estilo, energía y libertad.",
        color = Color.Black,
        fontSize = 16.sp,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFA9A9A9)) // Gris (A9A9A9) en ARGB
            .padding(12.dp)
    )
}

@Composable
fun Historia() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFA9A9A9))
                .padding(12.dp)
        ) {
            Text(
                text = "Pablo Mery, Freddy Galarza y Nicolás Sánchez crean la empresa Tangana en 2023 " +
                        "dedicándose principalmente a la venta de Skate, BMX, patines y monopatines.\n\n" +
                        "En el año 2024 la empresa invirtió en la creación de un sitio web, para así expandir " +
                        "sus ventas y hacerse conocida, especialmente por los jóvenes. Debido al éxito alcanzado " +
                        "por la página, la empresa Tangana alcanzó un gran éxito, haciéndose conocida por todo " +
                        "Santiago de Chile como la mejor empresa deportiva de la ciudad.\n\n" +
                        "Desde entonces y hasta ahora la empresa nunca más se detuvo y continuó vendiendo sus " +
                        "vehículos deportivos. Actualmente la empresa está considerando aplicar mejoras en su sitio web.",
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun IntegrantesRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        IntegranteCard(
            imagen = R.drawable.pablo_mery1,
            nombre = "Pablo Mery",
            descripcion = "Director general\nSocio y Fundador\nTangana"
        )
        IntegranteCard(
            imagen = R.drawable.nicolas_sanchez1,
            nombre = "Nicolás Sánchez",
            descripcion = "Programador\nSocio y Fundador\nTangana"
        )
        IntegranteCard(
            imagen = R.drawable.freddy_galarza1,
            nombre = "Freddy Galarza",
            descripcion = "Experto en deporte\nSocio y Fundador\nTangana"
        )
    }
}

@Composable
fun IntegranteCard(imagen: Int, nombre: String, descripcion: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(120.dp)
    ) {
        Image(
            painter = painterResource(id = imagen),
            contentDescription = nombre,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = nombre,
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Text(
            text = descripcion,
            fontSize = 12.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Contacto() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFA9A9A9))
                .padding(12.dp)
        ) {
            Text(
                text = "Pablo Mery:       pablomery2002@gmail.com\n\n" +
                        "Freddy Galarza: freddy.gabriel.galarza@gmail.com\n\n" +
                        "Nicolás Sánchez: nico.sanchezb@duocuc.cl",
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuienesSomosPreview() {
    Surface { QuienesSomosScreen() }
}
