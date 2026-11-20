package com.example.firebase_1;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class Contacto implements Serializable {

    private String id;
    private String NOMBRE;
    private String TELEFONO;
    private String CORREO;

    // Requerido por Firestore
    public Contacto() {
    }

    public Contacto(String nombre, String telefono, String correo) {
        this.NOMBRE = nombre;
        this.TELEFONO = telefono;
        this.CORREO = correo;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    // @PropertyName fuerza que el campo en Firestore se llame exactamente NOMBRE, TELEFONO, CORREO
    @PropertyName("NOMBRE")
    public String getNOMBRE() {
        return NOMBRE;
    }

    @PropertyName("NOMBRE")
    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    @PropertyName("TELEFONO")
    public String getTELEFONO() {
        return TELEFONO;
    }

    @PropertyName("TELEFONO")
    public void setTELEFONO(String TELEFONO) {
        this.TELEFONO = TELEFONO;
    }

    @PropertyName("CORREO")
    public String getCORREO() {
        return CORREO;
    }

    @PropertyName("CORREO")
    public void setCORREO(String CORREO) {
        this.CORREO = CORREO;
    }
}
