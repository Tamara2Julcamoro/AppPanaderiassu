package com.example.apppanaderia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UsuarioDAO {
    private DBHelper dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean registrarUsuario(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", usuario.getNombre());
        values.put("correo", usuario.getCorreo());
        values.put("contraseña", usuario.getContraseña());

        long result = db.insert("usuarios", null, values);
        db.close();

        if (result != -1) {
            Log.d("UsuarioDAO", "Usuario registrado: " + usuario.getCorreo());
            return true;
        } else {
            Log.d("UsuarioDAO", "Error al registrar el usuario: " + usuario.getCorreo());
            return false;
        }
    }


    public boolean iniciarSesion(String correo, String contraseña) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE correo = ? AND contraseña = ?",
                new String[]{correo, contraseña});

        if (cursor.moveToFirst()) {
            Log.d("UsuarioDAO", "Usuario encontrado: " + correo);
            cursor.close();
            db.close();
            return true;
        } else {
            Log.d("UsuarioDAO", "Usuario no encontrado: " + correo);
            cursor.close();
            db.close();
            return false;
        }
    }

}
