package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pi8_turnos.Adaptadores.AdapterInstituciones;
import com.example.pi8_turnos.Model.Institucion;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListInstitucionesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterInstituciones adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_instituciones);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_buscar);
        setSupportActionBar(toolbar);
        recyclerView=(RecyclerView) findViewById(R.id.rcl_listado_instituciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        FirebaseRecyclerOptions<Institucion> options =
                new FirebaseRecyclerOptions.Builder<Institucion>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Instituciones"), Institucion.class)
                        .build();
        adapter=new AdapterInstituciones(options);
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_buscar_instituciones,menu);
        MenuItem item=menu.findItem(R.id.buscar);

        SearchView searchView=(SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                prosesssearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                prosesssearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void prosesssearch(String s) {

        FirebaseRecyclerOptions<Institucion> options =
                new FirebaseRecyclerOptions.Builder<Institucion>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Instituciones").orderByChild("nombreinstitucion").startAt(s).endAt(s+"\uf8ff"), Institucion.class)
                        .build();
        adapter=new AdapterInstituciones(options);
        adapter.startListening();
        recyclerView.setAdapter(adapter);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}