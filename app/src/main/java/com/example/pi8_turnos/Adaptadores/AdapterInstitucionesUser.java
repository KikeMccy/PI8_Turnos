package com.example.pi8_turnos.Adaptadores;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pi8_turnos.Model.Institucion;
import com.example.pi8_turnos.Model.InstitucionUser;
import com.example.pi8_turnos.PrincipalActivity;
import com.example.pi8_turnos.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;


public class AdapterInstitucionesUser /*extends FirebaseRecyclerAdapter<InstitucionUser,AdapterInstitucionesUser.myviewholder>*/ {

   /*public String id;
    public AdapterInstitucionesUser(@NonNull FirebaseRecyclerOptions<InstitucionUser> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull InstitucionUser model) {
        holder.nombreusuario.setText(model.getNombreusuario());
        holder.nombreinstitucion.setText(model.getNombreinstitucion());
        Glide.with(holder.urlimagen.getContext()).load(model.getUrlimagen()).into(holder.urlimagen);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id=getRef(position).getKey();
            }
        });
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_instituciones_user,parent,false);
        return  new AdapterInstitucionesUser.myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{
        ImageView urlimagen;
        TextView nombreinstitucion, nombreusuario;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            urlimagen=(ImageView) itemView.findViewById(R.id.imgPortada_user);
            nombreinstitucion=(TextView) itemView.findViewById(R.id.txtNombre_user);
            nombreusuario=(TextView)itemView.findViewById(R.id.txt_usuario_item_user);

        }
    }*/
}
