package com.example.apppanaderia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.apppanaderia.R;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etCorreo, etContraseña;
    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreo = findViewById(R.id.etCorreo);
        etContraseña = findViewById(R.id.etContraseña);
        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        TextView tvRegistrar = findViewById(R.id.tvRegistrar);

        usuarioDAO = new UsuarioDAO(this);

        btnIniciarSesion.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String contraseña = etContraseña.getText().toString().trim();

            if (correo.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa ambos campos", Toast.LENGTH_SHORT).show();
            } else {
                if (usuarioDAO.iniciarSesion(correo, contraseña)) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MenuOpcionesActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroActivity.class);
            startActivity(intent);
        });
    }
}
