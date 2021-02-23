package com.example.pi8_turnos.Model;

public class TurnoAsignado {
    private String fecha,hora_inicio,hora_fin,nombre_institucion,id_turno,id_horario;

    public TurnoAsignado() {
    }

    public TurnoAsignado(String fecha, String hora_inicio, String hora_fin, String nombre_institucion, String id_turno, String id_horario) {
        this.fecha = fecha;
        this.hora_inicio = hora_inicio;
        this.hora_fin = hora_fin;
        this.nombre_institucion = nombre_institucion;
        this.id_turno = id_turno;
        this.id_horario = id_horario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

    public String getNombre_institucion() {
        return nombre_institucion;
    }

    public void setNombre_institucion(String nombre_institucion) {
        this.nombre_institucion = nombre_institucion;
    }

    public String getId_turno() {
        return id_turno;
    }

    public void setId_turno(String id_turno) {
        this.id_turno = id_turno;
    }

    public String getId_horario() {
        return id_horario;
    }

    public void setId_horario(String id_horario) {
        this.id_horario = id_horario;
    }
}
