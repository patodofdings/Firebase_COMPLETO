package com.example.firebase_1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACTO = "contacto";

    // false = lectura una sola vez con get() (por defecto)
    // true  = lectura en tiempo real con addSnapshotListener()
    public static final boolean TIEMPO_REAL = false;

    private EditText inputNombre, inputTelefono, inputCorreo;
    private ListView listaContactos;
    private ArrayAdapter<String> adapter;
    private final List<Contacto> contactos = new ArrayList<>();
    private final List<String> textos = new ArrayList<>();

    private FirebaseFirestore db;
    private ListenerRegistration listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputNombre = findViewById(R.id.input_nombre);
        inputTelefono = findViewById(R.id.input_telefono);
        inputCorreo = findViewById(R.id.input_correo);
        Button btnEnviar = findViewById(R.id.btn_enviar);
        listaContactos = findViewById(R.id.lista_contactos);
        listaContactos.setEmptyView(findViewById(R.id.texto_vacio));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, textos);
        listaContactos.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        btnEnviar.setOnClickListener(v -> guardarContacto());

        listaContactos.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, DetalleActivity.class);
            intent.putExtra(EXTRA_CONTACTO, contactos.get(position));
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (TIEMPO_REAL) {
            escucharTiempoReal();
        } else {
            cargarUnaVez();
        }
    }

    // READ (SIN tiempo real - por defecto): consulta una sola vez con get()
    // Se ejecuta en onStart (al abrir y al volver de otra pantalla) y tras guardar.
    private void cargarUnaVez() {
        db.collection("contactos")
                .orderBy("NOMBRE", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(this::mostrarContactos)
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // READ (CON tiempo real): la lista se actualiza sola ante cualquier cambio
    private void escucharTiempoReal() {
        listener = db.collection("contactos")
                .orderBy("NOMBRE", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    mostrarContactos(snapshots);
                });
    }

    // Convierte los documentos en objetos Contacto y refresca el ListView
    private void mostrarContactos(QuerySnapshot snapshots) {
        contactos.clear();
        textos.clear();
        if (snapshots != null) {
            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                Contacto c = doc.toObject(Contacto.class);
                if (c != null) {
                    c.setId(doc.getId());
                    contactos.add(c);
                    textos.add(c.getNOMBRE() + "\n" + c.getTELEFONO()
                            + "\n" + c.getCORREO());
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) {
            listener.remove();
            listener = null;
        }
    }

    // CREATE
    private void guardarContacto() {
        String nombre = inputNombre.getText().toString().trim();
        String telefono = inputTelefono.getText().toString().trim();
        String correo = inputCorreo.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(telefono) || TextUtils.isEmpty(correo)) {
            Toast.makeText(this, "Completa nombre, teléfono y correo", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("contactos")
                .add(new Contacto(nombre, telefono, correo))
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show();
                    inputNombre.setText("");
                    inputTelefono.setText("");
                    inputCorreo.setText("");
                    inputNombre.requestFocus();
                    if (!TIEMPO_REAL) {
                        cargarUnaVez();   // sin tiempo real hay que recargar a mano
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
