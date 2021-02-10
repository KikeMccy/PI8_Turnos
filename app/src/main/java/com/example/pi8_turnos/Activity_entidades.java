package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pi8_turnos.Model.Entidades;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class Activity_entidades extends AppCompatActivity {

     EditText nombre_ent,nombre_cread,url_logo,id;
     ListView listView_entidades;

     FirebaseDatabase firebaseDatabase;
     DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entidades);


        nombre_cread=findViewById(R.id.txt_creador);
        nombre_ent=findViewById(R.id.txt_entidad);
        url_logo=findViewById(R.id.txt_url_logo);
        id=findViewById(R.id.txt_id);
        listView_entidades=findViewById(R.id.list_entidades);
        iniciaizarFirebase();


    }

    private void iniciaizarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=  firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entidades,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String nombre=nombre_ent.getText().toString();
        String creador=nombre_cread.getText().toString();
        String logo=url_logo.getText().toString();
        String identificador=id.getText().toString();

        switch (item.getItemId()){
            case R.id.icon_add:{
                if (nombre.equals("")||creador.equals("")||logo.equals("")){
                    validacion();
                }else{
                    Entidades e=new Entidades();
                    e.setId(UUID.randomUUID().toString());
                    e.setNombre_creador(creador);
                    e.setNombre_entidad(nombre);
                    e.setUrl_logo(logo);

                    databaseReference.child("Entidades").child("Usuarios").child(e.getId()).setValue(e);
                    limpiarcajas();
                    Toast.makeText(this, "Agregar", Toast.LENGTH_LONG).show();
                }

                break;
            }
            case R.id.icon_save:{
                Toast.makeText(this, "Guardar", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.icon_delete:{
                Toast.makeText(this, "Eliminar", Toast.LENGTH_LONG).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void limpiarcajas() {
        id.setText("");
        nombre_ent.setText("");
        nombre_cread.setText("");
        url_logo.setText("");
    }

    private void validacion() {
        String nombre=nombre_ent.getText().toString();
        String creador=nombre_cread.getText().toString();
        String logo=url_logo.getText().toString();
        if(nombre.equals("")){
            nombre_ent.setError("Requiered");
        }else if(creador.equals("")){
            nombre_cread.setError("Requiered");
        }else if(logo.equals("")){
            url_logo.setError("Requiered");
        }
    }
}