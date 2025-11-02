package com.example.tienda_react.data

import com.example.tienda_react.domain.Product

object FakeData {
    val PRODUCTS = listOf(
        // ------------------ BMX ------------------
        Product(
            id = 1,
            name = "Bicicleta BMX Wtp Arcade Candy Red",
            price = 594_992,
            category = "BMX",
            stock = 20,
            images = listOf(
                "file:///android_asset/IMG/BMX/bmx1a.jpg",
                "file:///android_asset/IMG/BMX/bmx1b.jpg",
                "file:///android_asset/IMG/BMX/bmx1c.jpg"
            )
        ),
        Product(
            id = 2,
            name = "Bicicleta BMX Wtp Trust Cs Rsd Matt Black",
            price = 1_149_990,
            category = "BMX",
            stock = 8,
            images = listOf(
                "file:///android_asset/IMG/BMX/bmx2a.jpg",
                "file:///android_asset/IMG/BMX/bmx2b.jpg"
            )
        ),
        Product(
            id = 3,
            name = "Bicicleta BMX Wtp Trust Fc Rsd Matt Trans Violet",
            price = 1_149_990,
            category = "BMX",
            stock = 15,
            images = listOf("file:///android_asset/IMG/BMX/bmx3a.jpg")
        ),

        // ------------------ MONOPATINES ------------------
        Product(
            id = 4,
            name = "Scooter Monopatín De Pie Plegable Ajustable Jóvenes Adultos",
            price = 36_990,
            category = "Monopatín",
            stock = 12,
            images = listOf("file:///android_asset/IMG/Monopatines/mono1a.jpg")
        ),
        Product(
            id = 5,
            name = "Scooter Lucky Crew Black Neo",
            price = 215_992,
            category = "Monopatín",
            stock = 10,
            images = listOf("file:///android_asset/IMG/Monopatines/mono2a.jpg")
        ),
        Product(
            id = 6,
            name = "SCOOTER DC Comics Infantil Reforzado Mujer Maravilla 4 Ruedas",
            price = 37_990,
            category = "Monopatín",
            stock = 15,
            images = listOf("file:///android_asset/IMG/Monopatines/mono3a.jpg")
        ),

        // ------------------ PATINETAS ------------------
        Product(
            id = 7,
            name = "Tabla de Skate Niños 8–12 Años CP100 MID Cosmic Tamaño 7,6",
            price = 50_000,
            category = "Patinetas",
            stock = 20,
            images = listOf("file:///android_asset/IMG/Patinetas/skate1a.jpg")
        ),
        Product(
            id = 8,
            name = "Skateboard Completo FSC CP100 Talla 8",
            price = 50_000,
            category = "Patinetas",
            stock = 25,
            images = listOf("file:///android_asset/IMG/Patinetas/skate2a.jpg")
        ),
        Product(
            id = 9,
            name = "Skateboard Niños PLAY100",
            price = 25_000,
            category = "Patinetas",
            stock = 30,
            images = listOf("file:///android_asset/IMG/Patinetas/skate3a.jpg")
        ),

        // ------------------ PATINES ------------------
        Product(
            id = 10,
            name = "Patines en Línea Niños FIT3",
            price = 45_000,
            category = "Patines",
            stock = 18,
            images = listOf("file:///android_asset/IMG/Patines/patines1a.jpg")
        ),
        Product(
            id = 11,
            name = "Patines en Línea Adultos FIT500",
            price = 80_000,
            category = "Patines",
            stock = 12,
            images = listOf("file:///android_asset/IMG/Patines/patines2a.jpg")
        ),
        Product(
            id = 12,
            name = "Patines Freeskate Adultos MF900 HB 3x110 mm Verde",
            price = 120_000,
            category = "Patines",
            stock = 10,
            images = listOf("file:///android_asset/IMG/Patines/patines3a.jpg")
        )
    )

    fun byId(id: Int) = PRODUCTS.first { it.id == id }
}
