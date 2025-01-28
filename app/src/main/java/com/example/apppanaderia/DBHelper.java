package com.example.apppanaderia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.apppanaderia.GestionVentas.Venta;
import com.example.apppanaderia.GestionVentas.VentaDetalle;
import com.example.apppanaderia.Gestiondeinventario.Producto;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PanaderiafinalcpDB";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("CREATE TABLE usuarios (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "correo TEXT NOT NULL UNIQUE," +
                "contraseña TEXT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "descripcion TEXT, " +
                "precio_25 REAL, " +
                "precio_50 REAL, " +
                "precio_100 REAL, " +
                "stock INTEGER DEFAULT 0, " +
                "imagen BLOB)");


        db.execSQL("CREATE TABLE IF NOT EXISTS Ventas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fecha TEXT, " +
                "total REAL NOT NULL, " +
                "cliente TEXT, " +
                "dni TEXT, " +
                "metodo_pago TEXT)");


        db.execSQL("CREATE TABLE IF NOT EXISTS VentaDetalle (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "venta_id INTEGER NOT NULL, " +
                "producto_id INTEGER NOT NULL, " +
                "cantidad INTEGER NOT NULL, " +
                "precio REAL NOT NULL, " +
                "FOREIGN KEY (venta_id) REFERENCES Ventas(id) ON DELETE CASCADE ON UPDATE CASCADE, " +
                "FOREIGN KEY (producto_id) REFERENCES Productos(id) ON DELETE CASCADE ON UPDATE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS usuarios;");
            db.execSQL("DROP TABLE IF EXISTS VentaDetalle");
            db.execSQL("DROP TABLE IF EXISTS Ventas");
            db.execSQL("DROP TABLE IF EXISTS Productos");
            onCreate(db); // Recrear las tablas
        }
    }

    public List<Producto> registrarVenta(String cliente, double total, String dni, String metodoPago, List<VentaDetalle> detalles) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        List<Producto> productosActualizados = new ArrayList<>();

        try {

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

                int cantidadVendida = detalle.getCantidad();
                String queryActualizarStock = "UPDATE Productos SET stock = stock - ? WHERE id = ?";
                db.execSQL(queryActualizarStock, new Object[]{cantidadVendida, detalle.getProductoId()});


                String queryProducto = "SELECT id, nombre, descripcion, precio_25, precio_50, precio_100, stock, imagen FROM Productos WHERE id = ?";
                Cursor cursor = db.rawQuery(queryProducto, new String[]{String.valueOf(detalle.getProductoId())});

                if (cursor.moveToFirst()) {
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
                    productosActualizados.add(producto);
                }
                cursor.close();
            }

            db.setTransactionSuccessful();
            return productosActualizados;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            db.endTransaction();
        }
    }



    public ArrayList<String> listarProductosDeVenta(int ventaId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> productos = new ArrayList<>();

        String query = "SELECT p.nombre, vd.precio " +
                "FROM VentaDetalle vd " +
                "INNER JOIN Productos p ON vd.producto_id = p.id " +
                "WHERE vd.venta_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(ventaId)});

        while (cursor.moveToNext()) {
            String nombre = cursor.getString(0);
            double precio = cursor.getDouble(1);
            productos.add(nombre + " - S/" + precio);
        }

        cursor.close();
        return productos;
    }

    public boolean eliminarVenta(int ventaId) {
        SQLiteDatabase db = this.getWritableDatabase();


        db.delete("VentaDetalle", "venta_id = ?", new String[]{String.valueOf(ventaId)});


        int filasAfectadas = db.delete("Ventas", "id = ?", new String[]{String.valueOf(ventaId)});

        return filasAfectadas > 0;
    }

    public ArrayList<Venta> listarVentasDetalles() {
        ArrayList<Venta> ventas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT id, fecha, total, cliente FROM Ventas";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String fecha = cursor.getString(1);
            double total = cursor.getDouble(2);
            String cliente = cursor.getString(3);

            Venta venta = new Venta(id, fecha, total, cliente);
            ventas.add(venta);
        }

        cursor.close();
        return ventas;
    }

    public Venta obtenerVentaPorId(int ventaId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Venta venta = null;

        String query = "SELECT * FROM Ventas WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(ventaId)});

        if (cursor != null && cursor.moveToFirst()) {
            String cliente = cursor.getString(cursor.getColumnIndexOrThrow("cliente"));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));

            venta = new Venta(ventaId, fecha, total, cliente);
            cursor.close();
        }

        db.close();
        return venta;
    }



    public boolean eliminarProductoDeVenta(int ventaId, String productoNombre) {
        SQLiteDatabase db = this.getWritableDatabase();


        String queryProducto = "SELECT id FROM Productos WHERE nombre = ?";
        Cursor cursor = db.rawQuery(queryProducto, new String[]{productoNombre});

        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }

        int productoId = cursor.getInt(0);
        cursor.close();

        // Eliminar el producto de la tabla VentaDetalle
        int filasEliminadas = db.delete("VentaDetalle", "venta_id = ? AND producto_id = ?",
                new String[]{String.valueOf(ventaId), String.valueOf(productoId)});

        if (filasEliminadas > 0) {
            // Incrementar el stock del producto
            String queryActualizarStock = "UPDATE Productos SET stock = stock + (SELECT cantidad FROM VentaDetalle WHERE venta_id = ? AND producto_id = ?) WHERE id = ?";
            db.execSQL(queryActualizarStock, new Object[]{ventaId, productoId, productoId});
        }

        db.close();
        return filasEliminadas > 0;
    }
}
