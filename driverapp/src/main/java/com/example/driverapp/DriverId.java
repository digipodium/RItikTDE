package com.example.driverapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DriverId extends AppCompatActivity {
    private Button btn_choose, btn_upload;
    private ImageView imageView;
    private ProgressBar progressBar;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("drivers");
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;
    private FirebaseAuth auth;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK & data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_id);

        btn_choose = findViewById(R.id.btn_choose);
        btn_upload = findViewById(R.id.btn_upload);
        imageView = findViewById(R.id.imageDL);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        btn_choose.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 2);
        });

        btn_upload.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadToFirebase(imageUri);
            } else {
                Toast.makeText(DriverId.this, "please select image", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void uploadToFirebase(Uri uri) {

        auth = FirebaseAuth.getInstance();
        StorageReference fileRef = reference.child(auth.getCurrentUser().getUid()).child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
            root.child(auth.getCurrentUser().getUid()).child("image").setValue(uri1.toString()).addOnSuccessListener(unused -> {
                Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                startActivity(intent);
            }).addOnFailureListener(e -> {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            });

        })).addOnProgressListener(snapshot -> progressBar.setVisibility(View.VISIBLE)).addOnFailureListener(e -> {
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(DriverId.this, "Uploading failed", Toast.LENGTH_SHORT).show();
        });
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

}
