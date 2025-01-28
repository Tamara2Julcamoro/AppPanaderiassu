package com.example.apppanaderia.Gestiondeinventario;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apppanaderia.DBHelper;
import com.example.apppanaderia.R;

import java.io.ByteArrayOutputStream;

public class UpdateProductoActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private EditText etNombre, etDescripcion, etPrecio25, etPrecio50, etPrecio100, etStock;
    private ImageView imgProducto;
    private Uri imageUri; // Variable para almacenar la URI de la imagen seleccionada
    private DBHelper dbHelper;
    private int productoId; // ID del producto a actualizar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_producto);

        dbHelper = new DBHelper(this);

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio25 = findViewById(R.id.etPrecio25);
        etPrecio50 = findViewById(R.id.etPrecio50);
        etPrecio100 = findViewById(R.id.etPrecio100);
        etStock = findViewById(R.id.etStock);
        imgProducto = findViewById(R.id.imgProducto);

        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        Button btnActualizarProducto = findViewById(R.id.btnActualizarProducto);

        // Obtener ID del producto desde el Intent
        productoId = getIntent().getIntExtra("producto_id", -1);
        Log.d("UpdateProductoActivity", "ID del producto recibido: " + productoId);
        if (productoId == -1) {
            Toast.makeText(this, "Error al cargar el producto", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad si no se recibe un ID válido
        }

        // Cargar los datos del producto en la pantalla
        cargarProducto();

        // Evento para seleccionar una nueva imagen
        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());

        // Evento para actualizar el producto
        btnActualizarProducto.setOnClickListener(v -> actualizarProducto());
    }

    private void abrirGaleria() {
        // Abrir la galería para seleccionar una imagen
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgProducto.setImageURI(imageUri);
        }
    }

    private void cargarProducto() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Productos", null, "id = ?", new String[]{String.valueOf(productoId)}, null, null, null);

        if (cursor.moveToFirst()) {
            etNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
            etDescripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
            etPrecio25.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("precio_25"))));
            etPrecio50.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("precio_50"))));
            etPrecio100.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("precio_100"))));
            etStock.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("stock"))));

            byte[] blob = cursor.getBlob(cursor.getColumnIndexOrThrow("imagen"));
            if (blob != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                imgProducto.setImageBitmap(bitmap);
            }
        }
        cursor.close();
    }

    private void actualizarProducto() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precio25Str = etPrecio25.getText().toString().trim();
        String precio50Str = etPrecio50.getText().toString().trim();
        String precio100Str = etPrecio100.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();

        if (nombre.isEmpty() || descripcion.isEmpty() || precio25Str.isEmpty() || precio50Str.isEmpty() || precio100Str.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio25 = Double.parseDouble(precio25Str);
        double precio50 = Double.parseDouble(precio50Str);
        double precio100 = Double.parseDouble(precio100Str);
        int stock = Integer.parseInt(stockStr);

        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("descripcion", descripcion);
        values.put("precio_25", precio25);
        values.put("precio_50", precio50);
        values.put("precio_100", precio100);
        values.put("stock", stock);

        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                byte[] imagenBytes = convertirBitmapABytes(bitmap);
                values.put("imagen", imagenBytes);
            } catch (Exception e) {
                Toast.makeText(this, "Error al procesar la imagen seleccionada", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated = db.update("Productos", values, "id = ?", new String[]{String.valueOf(productoId)});

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Producto actualizado con éxito", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar el producto", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] convertirBitmapABytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
