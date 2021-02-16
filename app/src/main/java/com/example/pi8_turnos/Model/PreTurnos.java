package com.example.pi8_turnos.Model;

public class PreTurnos {
    int numero;
    String horaInicio;
    String horaFin;

    public PreTurnos(int numero, String horaInicio, String horaFin) {
        this.numero = numero;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }
}
