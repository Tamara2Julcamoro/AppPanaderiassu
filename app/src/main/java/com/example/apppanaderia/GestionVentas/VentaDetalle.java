package com.example.apppanaderia.GestionVentas;

public class VentaDetalle {
    private int productoId;
    private String nombreProducto; // Nuevo atributo
    private int cantidad;
    private double precio;

    public VentaDetalle(int productoId, String nombreProducto, int cantidad, double precio) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto; // Inicialización
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public int getProductoId() {
        return productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecio() {
        return precio;
    }
}

