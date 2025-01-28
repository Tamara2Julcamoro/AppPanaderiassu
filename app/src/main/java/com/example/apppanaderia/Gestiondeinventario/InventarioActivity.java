package com.example.apppanaderia.Gestiondeinventario;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import com.example.apppanaderia.R;

public class InventarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        findViewById(R.id.btnAddProducto).setOnClickListener(view -> {
            Intent intent = new Intent(InventarioActivity.this, AddProductoActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnListProducto).setOnClickListener(view -> {
            Intent intent = new Intent(InventarioActivity.this, ListProductoActivity.class);
            startActivity(intent);
        });
    }
}