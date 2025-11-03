package com.example.tienda_react

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tienda_react.ui.nav.AppNav
import com.example.tienda_react.ui.theme.TiendaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TiendaTheme { AppNav() } }
    }
}
