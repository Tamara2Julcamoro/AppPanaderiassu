package com.example.apppanaderia.GestionVentas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.example.apppanaderia.R;
import androidx.appcompat.app.AppCompatActivity;

public class GestionVentasActivity  extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gestion_ventas);

            Button btnRegistrarVenta = findViewById(R.id.btnRegistrarVenta);
            Button btnListarVentas = findViewById(R.id.btnListarVentas);

            // Ir a la pantalla de registrar ventas
            btnRegistrarVenta.setOnClickListener(view -> {
                Intent intent = new Intent(GestionVentasActivity.this, RegistrarVentaActivity.class);
                startActivity(intent);
            });

            // Ir a la pantalla de listar ventas
            btnListarVentas.setOnClickListener(view -> {
                Intent intent = new Intent(GestionVentasActivity.this, ListarVentasActivity.class);
                startActivity(intent);
            });
        }
    }
