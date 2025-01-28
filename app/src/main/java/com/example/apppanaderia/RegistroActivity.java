package com.example.apppanaderia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.apppanaderia.R;
import com.example.apppanaderia.UsuarioDAO;

public class RegistroActivity extends AppCompatActivity {
    private EditText etNombre, etCorreo, etContraseña;
    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreoRegistro);
        etContraseña = findViewById(R.id.etContraseñaRegistro);
        Button btnRegistrar = findViewById(R.id.btnRegistrar);

        usuarioDAO = new UsuarioDAO(this);

        btnRegistrar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String contraseña = etContraseña.getText().toString().trim();

            if (nombre.isEmpty() || correo.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            } else {
                if (usuarioDAO.registrarUsuario(new Usuario(nombre, correo, contraseña))) {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
