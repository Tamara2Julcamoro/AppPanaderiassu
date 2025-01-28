package com.example.apppanaderia.GestionVentas;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apppanaderia.DBHelper;
import com.example.apppanaderia.R;

import java.util.ArrayList;

public class ListarVentasActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ArrayList<Venta> ventas;
    private ListView lvVentas;
    private VentaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_ventas);

        dbHelper = new DBHelper(this);
        lvVentas = findViewById(R.id.lvVentas);

        cargarVentas();
    }

    private void cargarVentas() {
        ventas = dbHelper.listarVentasDetalles(); // Método que devuelve objetos Venta
        adapter = new VentaAdapter(this, ventas, dbHelper);
        lvVentas.setAdapter(adapter);
    }
}
