package com.example.pi8_turnos.Model;

public class Turno {
    private String fecha,id_usuario,descripcion;

    public Turno() {
    }

    public Turno(String fecha, String id_usuario, String descripcion) {
        this.fecha = fecha;
        this.id_usuario = id_usuario;
        this.descripcion=descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
