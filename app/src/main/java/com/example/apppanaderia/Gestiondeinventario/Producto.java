package com.example.apppanaderia.Gestiondeinventario;

public class Producto {
    private int id;
    private String nombre;
    private String descripcion;
    private double precio25;
    private double precio50;
    private double precio100;
    private int stock;
    private byte[] imagen;
    public Producto(int id, String nombre, String descripcion,  double precio25, double precio50, double precio100, int stock, byte[] imagen) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio25 = precio25;
        this.precio50 = precio50;
        this.precio100 = precio100;
        this.stock = stock;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio25() {
        return precio25;
    }

    public void setPrecio25(double precio25) {
        this.precio25 = precio25;
    }

    public double getPrecio50() {
        return precio50;
    }

    public void setPrecio50(double precio50) {
        this.precio50 = precio50;
    }

    public double getPrecio100() {
        return precio100;
    }

    public void setPrecio100(double precio100) {
        this.precio100 = precio100;
    }

    public int getStock() {
        return stock;
    }



    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }



    public void setStock(int stock) {
        this.stock = stock;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }
}
