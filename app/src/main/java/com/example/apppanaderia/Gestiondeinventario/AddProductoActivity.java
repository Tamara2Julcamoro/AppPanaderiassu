package com.example.apppanaderia.Gestiondeinventario;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apppanaderia.DBHelper;
import com.example.apppanaderia.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddProductoActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private ImageView imgProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_producto);

        EditText etNombre = findViewById(R.id.etNombre);
        EditText etDescripcion = findViewById(R.id.etDescripcion);
        EditText etPrecio25 = findViewById(R.id.etPrecio25);
        EditText etPrecio50 = findViewById(R.id.etPrecio50);
        EditText etPrecio100 = findViewById(R.id.etPrecio100);
        EditText etStock = findViewById(R.id.etStock);
        imgProducto = findViewById(R.id.imgProducto);
        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        Button btnGuardarProducto = findViewById(R.id.btnGuardarProducto);

        btnSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        });

        btnGuardarProducto.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String descripcion = etDescripcion.getText().toString();
            double precio25 = Double.parseDouble(etPrecio25.getText().toString());
            double precio50 = Double.parseDouble(etPrecio50.getText().toString());
            double precio100 = Double.parseDouble(etPrecio100.getText().toString());
            int stock = Integer.parseInt(etStock.getText().toString());

            if (imgProducto.getDrawable() == null) {
                Toast.makeText(this, "Seleccione una imagen", Toast.LENGTH_SHORT).show();
                return;
            }


            Bitmap bitmap = ((BitmapDrawable) imgProducto.getDrawable()).getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] imagenBytes = outputStream.toByteArray();

            DBHelper dbHelper = new DBHelper(this);
            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            values.put("descripcion", descripcion);
            values.put("precio_25", precio25);
            values.put("precio_50", precio50);
            values.put("precio_100", precio100);
            values.put("stock", stock);
            values.put("imagen", imagenBytes);

            dbHelper.getWritableDatabase().insert("Productos", null, values);
            Toast.makeText(this, "Producto agregado con éxito", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imgProducto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
