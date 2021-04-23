package com.example.firebasep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasep.models.Upload;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadActivity extends AppCompatActivity {


    private Button btnUpload, btnChoseFile;
    private TextView txtShowUploads;
    private EditText edtFileName;
    private ImageView imgMain;
    private ProgressBar progressBar;

    private final static int PIC_IMAGE_REQUEST_CODE = 1001;

    private Uri imageUri;


    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        init();

        btnChoseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChoser();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(UploadActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
                } else {

                    uploadImage();
                }
            }
        });

        txtShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(UploadActivity.this,ImagesActivity.class));
            }
        });


    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
    }

    private void uploadImage() {

        if (imageUri != null) {
            btnChoseFile.setEnabled(false);
            StorageReference fileStorageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = fileStorageReference.putFile(imageUri);


            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                }
            });

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {


                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileStorageReference.getDownloadUrl();

                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {


                            if (task.isSuccessful()) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress(0);
                                        Toast.makeText(UploadActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();

                                    }
                                }, 500);
                            }


                            Uri downloadUri = task.getResult();
                            Upload upload = new Upload(edtFileName.getText().toString(), downloadUri.toString());
                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(uploadId).setValue(upload);


                            edtFileName.setText("");
                            imgMain.setImageDrawable(null);
                            imageUri = null;
                            btnChoseFile.setEnabled(true);


                        }
                    });

        } else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();

        }

    }


    private void openFileChoser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PIC_IMAGE_REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PIC_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imgMain);

        }


    }


    private void init() {
        btnUpload = findViewById(R.id.upload_btnUpload);
        btnChoseFile = findViewById(R.id.upload_btnChoseFile);
        txtShowUploads = findViewById(R.id.upload_txtShowUploads);
        edtFileName = findViewById(R.id.upload_edtFileName);
        imgMain = findViewById(R.id.upload_imgMain);
        progressBar = findViewById(R.id.upload_progressBar);
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");


    }
}