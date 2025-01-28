package com.example.apppanaderia.Gestiondeinventario;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppanaderia.DBHelper;
import com.example.apppanaderia.GestionVentas.VentaDetalle;
import com.example.apppanaderia.R;

import java.util.ArrayList;
import java.util.List;

public class ListProductoActivity extends AppCompatActivity {
private  DBHelper dbHelper;
    private RecyclerView recyclerViewProductos;
    private ProductoAdapter productoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_producto);

        dbHelper = new DBHelper(this);
        recyclerViewProductos = findViewById(R.id.recyclerViewProductos);
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(this));

        EditText etBuscar = findViewById(R.id.etBuscar);

        // Agregar un listener para el buscador
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarProductos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        cargarProductos();
    }

    private void filtrarProductos(String query) {
        // Si la búsqueda está vacía, cargar todos los productos
        if (query.isEmpty()) {
            cargarProductos(); // Vuelve a cargar la lista completa de productos
        } else {
            List<Producto> productosFiltrados = new ArrayList<>();
            for (Producto producto : productoAdapter.getProductos()) {
                if (producto.getNombre().toLowerCase().contains(query.toLowerCase())) {
                    productosFiltrados.add(producto);
                }
            }
            productoAdapter.actualizarLista(productosFiltrados);
        }
    }

    private void cargarProductos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Productos", null);

        List<Producto> productos = new ArrayList<>();
        while (cursor.moveToNext()) {
            Producto producto = new Producto(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getDouble(4),
                    cursor.getDouble(5),
                    cursor.getInt(6),
                    cursor.getBlob(7)
            );
            productos.add(producto);
        }
        cursor.close();

        if (productos.isEmpty()) {
            Toast.makeText(this, "No se encontraron productos", Toast.LENGTH_SHORT).show();
        }

        productoAdapter = new ProductoAdapter(
                productos,
                this::mostrarMenuOpciones,
                this::mostrarImagenAmpliada // Aquí añadimos el listener para el clic en la imagen
        );
        recyclerViewProductos.setAdapter(productoAdapter);
    }

    private void mostrarMenuOpciones(Producto producto, View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_producto, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_actualizar) {
                Intent intent = new Intent(this, UpdateProductoActivity.class);
                intent.putExtra("producto_id", producto.getId());
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.menu_eliminar) {
                eliminarProducto(producto.getId());
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void mostrarImagenAmpliada(byte[] imagenBytes) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_imagen_ampliada);

        ImageView imageView = dialog.findViewById(R.id.imagenAmpliada);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
        imageView.setImageBitmap(bitmap);

        dialog.show();
    }

    private void eliminarProducto(int id) {
        // Crear un cuadro de diálogo de confirmación
        new AlertDialog.Builder(this)
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro de que deseas eliminar este producto?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // Si el usuario confirma, elimina el producto
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    int rowsDeleted = db.delete("Productos", "id = ?", new String[]{String.valueOf(id)});
                    if (rowsDeleted > 0) {
                        Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al eliminar el producto", Toast.LENGTH_SHORT).show();
                    }
                    cargarProductos(); // Recargar la lista de productos
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    // Si el usuario cancela, cierra el diálogo
                    dialog.dismiss();
                })
                .show();
    }

    public boolean registrarVenta(String cliente, double total, String dni, String metodoPago, List<VentaDetalle> detalles) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        db.beginTransaction(); // Iniciar transacción

        try {
            // Insertar la venta principal
            ContentValues ventaValues = new ContentValues();
            ventaValues.put("fecha", System.currentTimeMillis());
            ventaValues.put("cliente", cliente);
            ventaValues.put("total", total);
            ventaValues.put("dni", dni);
            ventaValues.put("metodo_pago", metodoPago);

            long ventaId = db.insert("Ventas", null, ventaValues);
            if (ventaId == -1) {
                throw new Exception("Error al registrar la venta principal");
            }

            // Insertar detalles de venta y actualizar stock
            for (VentaDetalle detalle : detalles) {
                ContentValues detalleValues = new ContentValues();
                detalleValues.put("venta_id", ventaId);
                detalleValues.put("producto_id", detalle.getProductoId());
                detalleValues.put("cantidad", detalle.getCantidad());
                detalleValues.put("precio", detalle.getPrecio());

                long detalleId = db.insert("VentaDetalle", null, detalleValues);
                if (detalleId == -1) {
                    throw new Exception("Error al registrar un detalle de venta");
                }

                // Actualizar stock en Productos
                String actualizarStockQuery = "UPDATE Productos SET stock = stock - ? WHERE id = ?";
                db.execSQL(actualizarStockQuery, new Object[]{detalle.getCantidad(), detalle.getProductoId()});
            }

            db.setTransactionSuccessful(); // Confirmar transacción
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction(); // Finalizar transacción
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos(); // Recargar productos al regresar a esta actividad
    }
}
