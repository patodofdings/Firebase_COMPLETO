package com.example.firebase_1;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditarActivity extends AppCompatActivity {

    private EditText inputNombre, inputTelefono, inputCorreo;
    private Contacto contacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        contacto = (Contacto) getIntent().getSerializableExtra(MainActivity.EXTRA_CONTACTO);
        if (contacto == null) {
            finish();
            return;
        }

        inputNombre = findViewById(R.id.input_nombre);
        inputTelefono = findViewById(R.id.input_telefono);
        inputCorreo = findViewById(R.id.input_correo);
        Button btnEditar = findViewById(R.id.btn_editar);

        inputNombre.setText(contacto.getNOMBRE());
        inputTelefono.setText(contacto.getTELEFONO());
        inputCorreo.setText(contacto.getCORREO());

        btnEditar.setOnClickListener(v -> actualizar());
    }

    // UPDATE
    private void actualizar() {
        String nombre = inputNombre.getText().toString().trim();
        String telefono = inputTelefono.getText().toString().trim();
        String correo = inputCorreo.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(telefono) || TextUtils.isEmpty(correo)) {
            Toast.makeText(this, "Completa nombre, teléfono y correo", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("contactos").document(contacto.getId())
                .set(new Contacto(nombre, telefono, correo))
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
