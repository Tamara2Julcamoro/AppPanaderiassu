package com.example.apppanaderia.GestionVentas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.apppanaderia.DBHelper;
import com.example.apppanaderia.R;

import java.util.List;

public class VentaAdapter extends ArrayAdapter<Venta> {

    private final Context context;
    private final List<Venta> ventas;
    private final DBHelper dbHelper;

    public VentaAdapter(@NonNull Context context, @NonNull List<Venta> ventas, DBHelper dbHelper) {
        super(context, R.layout.item_venta, ventas);
        this.context = context;
        this.ventas = ventas;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_venta, parent, false);
        }

        Venta venta = ventas.get(position);

        TextView tvCliente = convertView.findViewById(R.id.tvCliente);
        TextView tvFecha = convertView.findViewById(R.id.tvFechaVenta);
        TextView tvTotal = convertView.findViewById(R.id.tvTotalVenta);
        ImageButton btnEliminar = convertView.findViewById(R.id.btnEliminar);

        tvCliente.setText(String.format("Cliente: %s", venta.getCliente()));
        tvFecha.setText(String.format("Fecha: %s", venta.getFecha()));
        tvTotal.setText(String.format("Total: $%.2f", venta.getTotal()));



        // Botón de Eliminar
        btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmar Eliminación")
                    .setMessage("¿Estás seguro de que deseas eliminar esta venta?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        boolean eliminado = dbHelper.eliminarVenta(venta.getId());
                        if (eliminado) {
                            ventas.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Venta eliminada correctamente.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al eliminar la venta.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return convertView;
    }
}
