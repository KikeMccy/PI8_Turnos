package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ModificarUserActivity extends AppCompatActivity {

    private EditText txt_nombre, txt_cedula, txt_email;
    private Button btn_modificar;
    private TextView lb_nombre;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_user);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        mDialog=new ProgressDialog(this);
        //lb_nombre=(TextView) findViewById(R.id.txt_nombre_update);
        txt_nombre= (EditText) findViewById(R.id.txt_nombre_update);
        txt_cedula=(EditText) findViewById(R.id.txt_cedula_update);
        txt_email=(EditText) findViewById(R.id.txt_email_update);
        btn_modificar=(Button) findViewById(R.id.btn_update);

        String id=firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String cedula=snapshot.child("cedula").getValue().toString().trim();
                    String nombre=snapshot.child("nombre").getValue().toString().trim();
                    String email=snapshot.child("email").getValue().toString().trim();

                    txt_cedula.setText(cedula);
                    txt_nombre.setText(nombre);
                    txt_email.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });


        btn_modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ModificarUserActivity.this);
                builder.setTitle("!Alerta¡");
                builder.setMessage("¿Desea modificar sus datos?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String nombre = txt_nombre.getText().toString().trim();
                                        String cedula=txt_cedula.getText().toString().trim();
                                        if (nombre.equals("")) {
                                            txt_nombre.setError("Requiere nombre");

                                        }else if(cedula.equals("")){
                                            txt_cedula.setError("Requiere cedula");
                                        }else {

                                            if(validadorDeCedula(cedula)){
                                                mDialog.setMessage("Actualizando...");
                                                mDialog.setCanceledOnTouchOutside(false);
                                                mDialog.show();
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("nombre", nombre);
                                                map.put("cedula", cedula);
                                                String id = firebaseAuth.getCurrentUser().getUid();
                                                databaseReference.child("Usuarios").child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            String id=firebaseAuth.getCurrentUser().getUid();
                                                            Query query=FirebaseDatabase.getInstance().getReference("Instituciones").orderByChild("idusuario").equalTo(id);
                                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                                                                        Map<String, Object> map2 = new HashMap<>();
                                                                        map2.put("nombreusuario", nombre);
                                                                        snapshot1.getRef().updateChildren(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task2) {
                                                                                if(task2.isSuccessful()){
                                                                                    //Toast.makeText(ModificarUserActivity.this, "Nombre actualizado en institución", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                            Toast.makeText(ModificarUserActivity.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();

                                                            startActivity(new Intent(ModificarUserActivity.this, PrincipalActivity.class));
                                                        } else {
                                                            Toast.makeText(ModificarUserActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                                                        }
                                                        mDialog.dismiss();
                                                    }
                                                });
                                            }

                                        }
                                    }
                                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
            }
        });


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK){
            startActivity(new Intent(ModificarUserActivity.this,PrincipalActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean validadorDeCedula(String cedula) {
        boolean cedulaCorrecta = false;

        try {

            if (cedula.length() == 10)
            {
                int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
                if (tercerDigito < 6) {
                    int[] coefValCedula = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };
                    int verificador = Integer.parseInt(cedula.substring(9,10));
                    int suma = 0;
                    int digito = 0;
                    for (int i = 0; i < (cedula.length() - 1); i++) {
                        digito = Integer.parseInt(cedula.substring(i, i + 1))* coefValCedula[i];
                        suma += ((digito % 10) + (digito / 10));
                    }

                    if ((suma % 10 == 0) && (suma % 10 == verificador)) {
                        cedulaCorrecta = true;
                    }
                    else if ((10 - (suma % 10)) == verificador) {
                        cedulaCorrecta = true;
                    } else {
                        cedulaCorrecta = false;
                    }
                } else {
                    cedulaCorrecta = false;
                }
            } else {
                cedulaCorrecta = false;
            }
        } catch (NumberFormatException nfe) {
            cedulaCorrecta = false;
        } catch (Exception err) {

            Toast.makeText(ModificarUserActivity.this, "Una excepcion ocurrio en el proceso de validacion", Toast.LENGTH_SHORT).show();
            cedulaCorrecta = false;
        }

        if (!cedulaCorrecta) {

            Toast.makeText(ModificarUserActivity.this, "La Cédula ingresada es Incorrecta", Toast.LENGTH_SHORT).show();
        }
        return cedulaCorrecta;
    }
}