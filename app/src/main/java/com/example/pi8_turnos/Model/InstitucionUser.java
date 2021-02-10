package com.example.pi8_turnos.Model;

public class InstitucionUser {
    String nombreinstitucion, nombreusuario, urlimagen;


    InstitucionUser() {

    }

    public InstitucionUser(String nombreinstitucion, String nombreusuario, String urlimagen) {
        this.nombreinstitucion = nombreinstitucion;
        this.nombreusuario = nombreusuario;
        this.urlimagen = urlimagen;
    }

    public String getNombreinstitucion() {
        return nombreinstitucion;
    }

    public void setNombreinstitucion(String nombreinstitucion) {
        this.nombreinstitucion = nombreinstitucion;
    }

    public String getNombreusuario() {
        return nombreusuario;
    }

    public void setNombreusuario(String nombreusuario) {
        this.nombreusuario = nombreusuario;
    }

    public String getUrlimagen() {
        return urlimagen;
    }

    public void setUrlimagen(String urlimagen) {
        this.urlimagen = urlimagen;
    }
}
