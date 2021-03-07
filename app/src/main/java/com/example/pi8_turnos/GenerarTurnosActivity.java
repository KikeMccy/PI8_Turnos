package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GenerarTurnosActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String nombre,email, id;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private NavigationView navView;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_turnos);

        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout_generar_turnos);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_generar_turnos);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icono_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView = (NavigationView)findViewById(R.id.nav_view_generar_turnos);
        navView.setNavigationItemSelectedListener(this);
        navView.setItemIconTintList(null);

        getInfoUser();

    }

    public void mis_instituciones(View view){
        Intent intent=new Intent(GenerarTurnosActivity.this, InstitucionesUserActivity.class);
        startActivity(intent);
    }

    public void add_instituciones(View view){

        startActivity(new Intent(GenerarTurnosActivity.this, InstitucionesActivity.class));

    }

    private void getInfoUser(){
        id=firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    nombre=snapshot.child("nombre").getValue().toString();
                    email=snapshot.child("email").getValue().toString();
                    View header = ((NavigationView)findViewById(R.id.nav_view_generar_turnos)).getHeaderView(0);
                    ((TextView) header.findViewById(R.id.txt_nav_usuario)).setText(nombre);
                    ((TextView) header.findViewById(R.id.txt_nav_correo)).setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_inicio:
                startActivity(new Intent(GenerarTurnosActivity.this,PrincipalActivity.class));
                break;
            case R.id.item_perfil:
                startActivity(new Intent(GenerarTurnosActivity.this,ModificarUserActivity.class));
                break;
            case R.id.item_informacion:
                startActivity(new Intent(GenerarTurnosActivity.this,AboutActivity.class));
                break;
            case R.id.item_escanear:
                startActivity(new Intent(GenerarTurnosActivity.this,LeerQRActivity.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}