package com.faislll.projectakhir.aktivitas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.faislll.projectakhir.R;
import com.faislll.projectakhir.model.UserData;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class AktivitasEditProfile extends AppCompatActivity {
    private ImageView imageViewClose, imageViewSave, imageViewPp;
    private TextView textViewUbahPp;
    private MaterialEditText editTextFullname, editTextUsername, editTextBio;

    private FirebaseUser user;
    private StorageTask storageTask;
    private StorageReference storageReference;
    private Uri imageUri;
    private ProgressDialog loadingUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktivitas_edit_profile);
        init();


        DocumentReference reference = FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid());
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                UserData userData = value.toObject(UserData.class);
                editTextFullname.setText(userData.getFullname());
                editTextUsername.setText(userData.getUsername());
                editTextBio.setText(userData.getBio());
                Glide.with(getApplicationContext()).load(userData.getImageUrl()).into(imageViewPp);
            }
        });

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textViewUbahPp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(AktivitasEditProfile.this);
            }
        });
        imageViewPp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(AktivitasEditProfile.this);
            }
        });

        imageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bio = editTextBio.getText().toString().trim();
                String fullname = editTextFullname.getText().toString().trim();
                String username = editTextUsername.getText().toString().trim();
                ubahProfil(bio, fullname, username);
            }
        });

    }

    private void ubahProfil(String bio, String fullname, String username) {
        Map<String, Object> dataUpdate = new HashMap<>();
        dataUpdate.put("bio", bio);
        dataUpdate.put("fullname", fullname);
        dataUpdate.put("username", username);
        DocumentReference reference = FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid());
        reference.update(dataUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                uploadGmbar();
            }
        });
    }

    private void uploadGmbar() {
        loadingUpload = new ProgressDialog(AktivitasEditProfile.this);
        loadingUpload.setTitle("harap tunggu, gambar sedang diupload");
        loadingUpload.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + AktivityAdd.ekstensiFile(getApplicationContext(), imageUri));

            storageTask = fileReference.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    loadingUpload.dismiss();

                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String uri = downloadUri.toString();

                        Map<String, Object> dataUrlImage = new HashMap<>();
                        dataUrlImage.put("imageUrl", uri);
                        FirebaseFirestore.getInstance().collection("users")
                                .document(user.getUid()).update(dataUrlImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    finish();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(AktivitasEditProfile.this, "gagal upload gambar ke server", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AktivitasEditProfile.this, "on failure" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "anda tidak memilih gambar", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        imageViewClose = findViewById(R.id.image_view_close_edit_profile);
        imageViewSave = findViewById(R.id.image_view_simpan_edit_profile);
        imageViewPp = findViewById(R.id.image_view_pp_edit_profile);
        textViewUbahPp = findViewById(R.id.text_view_ubah_pp_edit_profile);
        editTextBio = findViewById(R.id.e_text_bio_edit_profile);
        editTextUsername = findViewById(R.id.e_text_username_edit_profile);
        editTextFullname = findViewById(R.id.e_text_full_name_edit_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("photos");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            imageViewPp.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Ada yang salah", Toast.LENGTH_SHORT).show();
        }
    }
}