package com.example.pi8_turnos.Model;

public class Comentario {
    private String puntuacion;
    private String nombre;
    private String comentario;
    private String fecha;

    public Comentario() {
    }

    public Comentario(String puntuacion, String nombre, String comentario, String fecha) {
        this.puntuacion = puntuacion;
        this.nombre = nombre;
        this.comentario = comentario;
        this.fecha=fecha;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(String puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
