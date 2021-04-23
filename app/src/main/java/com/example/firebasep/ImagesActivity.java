package com.example.firebasep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.firebasep.models.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImagesActivity extends AppCompatActivity implements ImagesAdapter.OnItemClickListener {

     RecyclerView recyclerView;
    ImagesAdapter imagesAdapter;
    ProgressBar progressBar;


    List<Upload> uploads;

    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;

    private ValueEventListener eventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        getDataFromFireBase();
        init();


    }

    private void getDataFromFireBase() {
        uploads=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("uploads");

       eventListener= databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploads.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Upload upload=dataSnapshot.getValue(Upload.class);
                    upload.setKey(dataSnapshot.getKey());
                    uploads.add(upload);

                }

                imagesAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ImagesActivity.this,error.getMessage()+ "", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void init() {
        progressBar=findViewById(R.id.images_progressBar);
        recyclerView=findViewById(R.id.images_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imagesAdapter=new ImagesAdapter(this,uploads);
        recyclerView.setAdapter(imagesAdapter);
        imagesAdapter.setOnItemClickListener(this);
        firebaseStorage=FirebaseStorage.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("uploads");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(eventListener);
    }

    @Override
    public void onDeleteClicked(int position) {

        Upload upload=uploads.get(position);
        String selectedKey=upload.getKey();

        StorageReference storageReference=firebaseStorage.getReferenceFromUrl(upload.getImgUrl());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(ImagesActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ImagesActivity.this,e.getMessage()+ "", Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public void onEditClicked(int position) {
        Upload upload=uploads.get(position);
        String selectedKey=upload.getKey();

        final EditText input = new EditText(ImagesActivity.this);
        input.setText(uploads.get(position).getName());
       input.setSelectAllOnFocus(true);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ImagesActivity.this);
        alertDialog.setTitle("Edit Name");
        alertDialog.setMessage("Please enter new name: ");

        alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String,Object> map=new HashMap();
                map.put("name",input.getText().toString());

                databaseReference.child(selectedKey).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ImagesActivity.this, "Name Updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ImagesActivity.this,e.getMessage()+ "", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setView(input);
        alertDialog.show();

    }
}