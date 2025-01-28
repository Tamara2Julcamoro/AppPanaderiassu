package com.example.apppanaderia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apppanaderia.GestionVentas.GestionVentasActivity;
import com.example.apppanaderia.Gestiondeinventario.InventarioActivity;
import com.example.apppanaderia.R;

public class MenuOpcionesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_opciones);

        Button btnGestionarInventario = findViewById(R.id.btnGestionarInventario);
        Button btnGestionarVentas = findViewById(R.id.btnGestionarVentas);

        btnGestionarInventario.setOnClickListener(v -> {
            Intent intent = new Intent(MenuOpcionesActivity.this, InventarioActivity.class);
            startActivity(intent);
        });

        btnGestionarVentas.setOnClickListener(v -> {
            Intent intent = new Intent(MenuOpcionesActivity.this, GestionVentasActivity.class);
            startActivity(intent);
        });
    }
}
