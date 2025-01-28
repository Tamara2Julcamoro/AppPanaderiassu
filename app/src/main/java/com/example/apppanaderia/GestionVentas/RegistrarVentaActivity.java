package com.example.apppanaderia.GestionVentas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apppanaderia.DBHelper;
import com.example.apppanaderia.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegistrarVentaActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ArrayList<String> productosSeleccionados = new ArrayList<>();
    private double total = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_venta);

        dbHelper = new DBHelper(this);

        // Declaración de vistas
        EditText edtDni = findViewById(R.id.edtDni);
        EditText edtCliente = findViewById(R.id.edtCliente);
        Spinner spinnerProductos = findViewById(R.id.spinnerProductos);
        Spinner spinnerPrecios = findViewById(R.id.spinnerPrecios);
        Spinner spinnerMetodoPago = findViewById(R.id.spinnerMetodoPago);
        Button btnAgregarProducto = findViewById(R.id.btnAgregarProducto);
        Button btnRegistrarVenta = findViewById(R.id.btnRegistrarVenta);
        ListView listViewProductos = findViewById(R.id.listViewProductos);
        TextView txtTotal = findViewById(R.id.txtTotal);

        // Configurar adaptadores
        ArrayAdapter<String> productosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getProductos());
        productosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductos.setAdapter(productosAdapter);

        ArrayAdapter<String> metodoPagoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Efectivo", "Tarjeta", "Yape", "Plin"});
        metodoPagoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetodoPago.setAdapter(metodoPagoAdapter);

        // Evento de selección de producto
        spinnerProductos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePrecios(spinnerProductos.getSelectedItem().toString(), spinnerPrecios);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Botón para agregar producto
        btnAgregarProducto.setOnClickListener(v -> {
            String producto = spinnerProductos.getSelectedItem().toString();
            String precio = spinnerPrecios.getSelectedItem().toString();

            // Agregar producto seleccionado a la lista
            productosSeleccionados.add(producto + " - " + precio);
            total += Double.parseDouble(precio.split(" ")[0]); // Extraer el precio numérico
            txtTotal.setText("Total: S/" + total);

            // Actualizar ListView
            ArrayAdapter<String> listAdapter = new ArrayAdapter<>(RegistrarVentaActivity.this, android.R.layout.simple_list_item_1, productosSeleccionados);
            listViewProductos.setAdapter(listAdapter);
        });

        // Botón para registrar venta
        btnRegistrarVenta.setOnClickListener(v -> {
            String dni = edtDni.getText().toString();
            String cliente = edtCliente.getText().toString();
            String metodoPago = spinnerMetodoPago.getSelectedItem().toString();

            if (dni.isEmpty() || cliente.isEmpty() || productosSeleccionados.isEmpty()) {
                Toast.makeText(RegistrarVentaActivity.this, "Complete todos los campos y seleccione al menos un producto.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener detalles de la venta
            List<VentaDetalle> detallesVenta = obtenerDetallesDeVenta();
            double totalVenta = calcularTotal(detallesVenta);

            // Registrar la venta
            registrarVenta(cliente, totalVenta, dni, metodoPago, detallesVenta);
            Toast.makeText(RegistrarVentaActivity.this, "Venta registrada correctamente.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // Obtener lista de productos
    private ArrayList<String> getProductos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> productos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT nombre FROM Productos", null);
        while (cursor.moveToNext()) {
            productos.add(cursor.getString(0));
        }
        cursor.close();
        return productos;
    }

    // Actualizar precios según el producto seleccionado
    private void updatePrecios(String producto, Spinner spinnerPrecios) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT precio_25, precio_50, precio_100 FROM Productos WHERE nombre = ?", new String[]{producto});
        if (cursor.moveToFirst()) {
            ArrayList<String> precios = new ArrayList<>();
            precios.add(cursor.getDouble(0) + " (25 unidades)");
            precios.add(cursor.getDouble(1) + " (50 unidades)");
            precios.add(cursor.getDouble(2) + " (100 unidades)");

            ArrayAdapter<String> preciosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, precios);
            preciosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPrecios.setAdapter(preciosAdapter);
        }
        cursor.close();
    }

    // Calcular el total de la venta
    private double calcularTotal(List<VentaDetalle> detallesVenta) {
        double total = 0.0;
        for (VentaDetalle detalle : detallesVenta) {
            total += detalle.getPrecio() * detalle.getCantidad();
        }
        return total;
    }

    // Obtener detalles de la venta
    private List<VentaDetalle> obtenerDetallesDeVenta() {
        List<VentaDetalle> detalles = new ArrayList<>();
        for (String producto : productosSeleccionados) {
            String[] partes = producto.split("-");
            String nombreProducto = partes[0].trim();
            double precio = Double.parseDouble(partes[1].trim().split(" ")[0]);

            VentaDetalle detalle = new VentaDetalle(getProductoId(nombreProducto), nombreProducto, 1, precio);
            detalles.add(detalle);
        }
        return detalles;
    }

    // Obtener el ID del producto
    private int getProductoId(String producto) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM Productos WHERE nombre = ?", new String[]{producto.split("-")[0].trim()});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }

    // Registrar la venta en la base de datos
    private void registrarVenta(String cliente, double total, String dni, String metodoPago, List<VentaDetalle> detallesVenta) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insertar venta principal
        ContentValues ventaValues = new ContentValues();
        ventaValues.put("fecha", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        ventaValues.put("total", total);
        ventaValues.put("cliente", cliente);
        ventaValues.put("dni", dni);
        ventaValues.put("metodo_pago", metodoPago);

        long ventaId = db.insert("Ventas", null, ventaValues);

        // Insertar detalles de venta
        for (VentaDetalle detalle : detallesVenta) {
            ContentValues detalleValues = new ContentValues();
            detalleValues.put("venta_id", ventaId);
            detalleValues.put("producto_id", detalle.getProductoId());
            detalleValues.put("cantidad", detalle.getCantidad());
            detalleValues.put("precio", detalle.getPrecio());
            db.insert("VentaDetalle", null, detalleValues);
        }
        db.close();
    }
}
