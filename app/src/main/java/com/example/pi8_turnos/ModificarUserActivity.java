package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ModificarUserActivity extends AppCompatActivity {

    private EditText txt_nombre;
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
        btn_modificar=(Button) findViewById(R.id.btn_update);
        btn_modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String nombre=txt_nombre.getText().toString();
                if (nombre.equals("")) {
                    txt_nombre.setError("Requiere datos");

                }else {
                    mDialog.setMessage("Actualizando...");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombre", nombre);
                    String id = firebaseAuth.getCurrentUser().getUid();
                    databaseReference.child("Usuarios").child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ModificarUserActivity.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                                lb_nombre.setText("");
                                startActivity(new Intent(ModificarUserActivity.this, PrincipalActivity.class));
                            } else {
                                Toast.makeText(ModificarUserActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                            }
                            mDialog.dismiss();
                        }
                    });
                }
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
}