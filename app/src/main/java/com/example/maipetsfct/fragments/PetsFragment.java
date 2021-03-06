package com.example.maipetsfct.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.maipetsfct.AddMascActivity;
import com.example.maipetsfct.CitasActivity;
import com.example.maipetsfct.popups.PopPet;
import com.example.maipetsfct.R;
import com.example.maipetsfct.adapters.MascotaAdapter;
import com.example.maipetsfct.models.mascota;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class PetsFragment extends Fragment {

    private Button btnAdd;
    public final int COD_REGISTRO=000;

    private FirebaseAuth fbauth ;
    private FirebaseDatabase fbdatabase;
    DatabaseReference reference,ref;


    // Colección de mascotas
    ArrayList<mascota> mascotas;
    RecyclerView recyclerView;
    MascotaAdapter mascotaAdapter;

    public PetsFragment() {
        // Constructor vacio requerido
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pets, container, false);

        btnAdd = view.findViewById(R.id.addMasc);

        //Obtenemos la instancia de FirebaseAuth
        fbauth = FirebaseAuth.getInstance() ;

        //Obtenemos la instancia de FirebaseDatabase
        fbdatabase =  FirebaseDatabase.getInstance();

        String uid = fbauth.getCurrentUser().getUid();

        Activity activity = getActivity();


        // ZONA PARA EL CARDVIEW                      ######################################

        recyclerView = view.findViewById(R.id.mascShows);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        mascotas = new ArrayList<mascota>();

        ref = FirebaseDatabase.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference().child("mascotas").child(uid);

        // El siguiente evento se lanza cada vez que se modifica la base de datos.
        // Si queremos que la consulta se realice UNA ÚNICA VEZ tenemos que utilizar:
        // addListenerForSingleValueEvent
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mascotas.clear() ;
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    mascota m = dataSnapshot1.getValue(mascota.class);
                    mascotas.add(m);
                }
                mascotaAdapter = new MascotaAdapter(activity,mascotas);
                mascotaAdapter.setMascotas(mascotas) ;
                mascotaAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(mascotaAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(activity, R.string.no_add, Toast.LENGTH_LONG).show();
            }
        });

        registerForContextMenu(recyclerView);


        // Defino escuchador para el botón AÑADIR
        btnAdd.setOnClickListener(viewAdd -> {

            // Intencion para proceder a añadir mascota
            Intent add = new Intent(activity, AddMascActivity.class);

            // Empezar la intención
            startActivityForResult(add, COD_REGISTRO);
        });
        return view;
    }

    // Menú contextual para las tarjetas de las mascotas
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context2, menu);

    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ctxEdd:

                mascota pet = mascotas.get(mascotaAdapter.getIndex());

                Intent irAEditar = new Intent(getActivity().getApplicationContext(), PopPet.class);
                irAEditar.putExtra("urlImage",pet.getUrlImage());
                irAEditar.putExtra("codigo",pet.getCodigo());
                irAEditar.putExtra("nombre",pet.getNombre());
                irAEditar.putExtra("especie",pet.getTipo());
                irAEditar.putExtra("raza",pet.getRaza());
                irAEditar.putExtra("color",pet.getColor());
                irAEditar.putExtra("fecha",pet.getFechaNac());
                startActivity(irAEditar);
                break;

            case R.id.ctxCita:

                mascota masco = mascotas.get(mascotaAdapter.getIndex());

                Intent irACita = new Intent(getActivity().getApplicationContext(), CitasActivity.class);
                irACita.putExtra("codigo",masco.getCodigo());
                irACita.putExtra("nombre",masco.getNombre());
                startActivity(irACita);
                break;

            case R.id.ctxDel:

                AlertDialog.Builder myBuild = new AlertDialog.Builder(getContext());
                myBuild.setTitle(R.string.cDel);
                myBuild.setMessage(R.string.delPet);
                myBuild.setPositiveButton(R.string.afirmative, (dialogInterface, i) -> {

                    mascota masc = mascotas.get(mascotaAdapter.getIndex());
                    String UUID = masc.getCodigo();
                    String uid = fbauth.getCurrentUser().getUid();

                    ref.child("mascotas").child(uid).child(UUID).removeValue();
                    Toast.makeText(getActivity().getApplicationContext(),R.string.ficDel, Toast.LENGTH_LONG).show();

                });
                myBuild.setNegativeButton("No", (dialogInterface, i) ->
                        dialogInterface.cancel());

                AlertDialog dialog = myBuild.create();
                dialog.show();
                break;
        }
        return super.onContextItemSelected(item);
    }
}
