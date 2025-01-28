package com.example.apppanaderia.GestionVentas;

public class Venta {
    private int id;
    private String fecha;
    private double total;
    private String cliente;

    public Venta(int id, String fecha, double total, String cliente) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.cliente = cliente;
    }

    public int getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public double getTotal() {
        return total;
    }

    public String getCliente() {
        return cliente;
    }

    @Override
    public String toString() {
        return "Venta ID: " + id + "\nCliente: " + cliente + "\nFecha: " + fecha + "\nTotal: S/" + total;
    }
}
