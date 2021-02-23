package com.example.pi8_turnos.Model;

public class PreTurnos {
    String numero;
    String estado;
    String hora_inicio;
    String hora_fin;
    String nombre_usuario;

    public  PreTurnos(){

    }

    public PreTurnos(String numero, String estado, String hora_inicio, String hora_fin, String nombre_usuario) {
        this.numero = numero;
        this.estado = estado;
        this.hora_inicio = hora_inicio;
        this.hora_fin = hora_fin;
        this.nombre_usuario = nombre_usuario;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(String hora_fin) {
        this.hora_fin = hora_fin;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }
}
