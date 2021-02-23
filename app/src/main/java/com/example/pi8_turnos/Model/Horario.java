package com.example.pi8_turnos.Model;

public class Horario {
    private String estado, hora_fin, hora_inicio,numero;

    public Horario() {
    }

    public Horario(String estado, String hora_fin, String hora_inicio,String numero) {
        this.estado = estado;
        this.hora_fin = hora_fin;
        this.hora_inicio = hora_inicio;
        this.numero=numero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(String hora_fin) {
        this.hora_fin = hora_fin;
    }

    public String getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
