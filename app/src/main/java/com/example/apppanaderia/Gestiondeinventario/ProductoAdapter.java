package com.example.apppanaderia.Gestiondeinventario;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppanaderia.DBHelper;
import com.example.apppanaderia.GestionVentas.VentaDetalle;
import com.example.apppanaderia.R;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> productos;
    public List<Producto> getProductos() {
        return productos;
    }
    private final OnItemClickListener menuOpcionesListener;
    private final OnImageClickListener imagenClickListener;

    public interface OnItemClickListener {
        void onItemClick(Producto producto, View view);
    }

    public interface OnImageClickListener {
        void onImageClick(byte[] imagenBytes);
    }

    public ProductoAdapter(List<Producto> productos,
                           OnItemClickListener menuOpcionesListener,
                           OnImageClickListener imagenClickListener) {
        this.productos = productos;
        this.menuOpcionesListener = menuOpcionesListener;
        this.imagenClickListener = imagenClickListener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);

        holder.tvNombre.setText(producto.getNombre());
        holder.tvDescripcion.setText(producto.getDescripcion());
        holder.tvPrecio25.setText("25u: S/" + producto.getPrecio25());
        holder.tvPrecio50.setText("50u: S/" + producto.getPrecio50());
        holder.tvPrecio100.setText("100u: S/" + producto.getPrecio100());
        holder.tvStock.setText("Stock: " + producto.getStock());

        // Decodificar imagen BLOB
        byte[] imagenBytes = producto.getImagen();
        if (imagenBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
            holder.imgProducto.setImageBitmap(bitmap);
        } else {
            holder.imgProducto.setImageResource(R.drawable.placeholder_image);
        }

        // Listener para el menú de opciones
        holder.btnMenuOpciones.setOnClickListener(v -> menuOpcionesListener.onItemClick(producto, v));
        // Listener para ampliar imagen
        holder.imgProducto.setOnClickListener(v -> {
            if (imagenBytes != null) {
                imagenClickListener.onImageClick(imagenBytes);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public void actualizarLista(List<Producto> nuevosProductos) {
        this.productos = nuevosProductos;
        notifyDataSetChanged(); // Se debe llamar en la instancia
    }

    public void disminuirStock(int productoId, int cantidadVendida) {
        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            if (producto.getId() == productoId) {
                int nuevoStock = producto.getStock() - cantidadVendida;

                if (nuevoStock >= 0) {
                    producto.setStock(nuevoStock); // Actualizar el stock en el objeto
                    notifyItemChanged(i); // Notificar que el elemento cambió
                } else {
                    throw new IllegalArgumentException("No hay suficiente stock para realizar la venta.");
                }
                break;
            }
        }
    }

    public void actualizarStockProducto(int productoId, int nuevoStock) {
        for (Producto producto : productos) {
            if (producto.getId() == productoId) {
                producto.setStock(nuevoStock);
                notifyDataSetChanged(); // Refrescar la lista
                break;
            }
        }
    }


    public void registrarVenta(String cliente, double total, String dni, String metodoPago, List<VentaDetalle> detalles, ProductoAdapter productoAdapter, Context context) {
        DBHelper dbHelper = new DBHelper(context);

        // Registrar la venta y obtener los productos actualizados
        List<Producto> productosActualizados = dbHelper.registrarVenta(cliente, total, dni, metodoPago, detalles);

        if (productosActualizados != null) {
            // Actualizar la lista en el adaptador
            productoAdapter.actualizarLista(productosActualizados);

            // Notificar cambios al adaptador
            productoAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(context, "Error al registrar la venta", Toast.LENGTH_SHORT).show();
        }
    }


    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio25, tvPrecio50, tvPrecio100, tvStock;
        ImageView imgProducto, btnMenuOpciones;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvPrecio25 = itemView.findViewById(R.id.tvPrecio25);
            tvPrecio50 = itemView.findViewById(R.id.tvPrecio50);
            tvPrecio100 = itemView.findViewById(R.id.tvPrecio100);
            tvStock = itemView.findViewById(R.id.tvStock);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            btnMenuOpciones = itemView.findViewById(R.id.btnMenuOpciones);
        }
    }
}
