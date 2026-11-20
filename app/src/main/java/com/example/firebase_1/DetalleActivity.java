package com.example.firebase_1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class DetalleActivity extends AppCompatActivity {

    private TextView txtNombre, txtTelefono, txtCorreo;
    private Contacto contacto;
    private DocumentReference documento;
    private ListenerRegistration listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        contacto = (Contacto) getIntent().getSerializableExtra(MainActivity.EXTRA_CONTACTO);
        if (contacto == null) {
            finish();
            return;
        }

        txtNombre = findViewById(R.id.detalle_nombre);
        txtTelefono = findViewById(R.id.detalle_telefono);
        txtCorreo = findViewById(R.id.detalle_correo);
        Button btnEditar = findViewById(R.id.btn_editar);
        Button btnEliminar = findViewById(R.id.btn_eliminar);

        documento = FirebaseFirestore.getInstance()
                .collection("contactos").document(contacto.getId());

        mostrar();

        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditarActivity.class);
            intent.putExtra(MainActivity.EXTRA_CONTACTO, contacto);
            startActivity(intent);
        });

        btnEliminar.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Eliminar contacto")
                .setMessage("¿Seguro que deseas eliminar este contacto?")
                .setPositiveButton("Sí", (d, w) -> eliminar())
                .setNegativeButton("No", null)
                .show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (documento == null) {
            return;
        }
        // Refresca los datos al volver de EditarActivity
        if (MainActivity.TIEMPO_REAL) {
            // CON tiempo real
            listener = documento.addSnapshotListener((snapshot, e) -> {
                if (e == null) {
                    actualizarDesde(snapshot);
                }
            });
        } else {
            // SIN tiempo real (por defecto): una sola lectura con get()
            documento.get().addOnSuccessListener(this::actualizarDesde);
        }
    }

    private void actualizarDesde(DocumentSnapshot snapshot) {
        if (snapshot == null || !snapshot.exists()) {
            return;
        }
        Contacto actualizado = snapshot.toObject(Contacto.class);
        if (actualizado != null) {
            actualizado.setId(snapshot.getId());
            contacto = actualizado;
            mostrar();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) {
            listener.remove();
            listener = null;
        }
    }

    private void mostrar() {
        txtNombre.setText(contacto.getNOMBRE());
        txtTelefono.setText(contacto.getTELEFONO());
        txtCorreo.setText(contacto.getCORREO());
    }

    // DELETE
    private void eliminar() {
        documento.delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
