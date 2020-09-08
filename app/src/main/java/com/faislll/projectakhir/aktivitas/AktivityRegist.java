package com.faislll.projectakhir.aktivitas;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.faislll.projectakhir.R;
import com.faislll.projectakhir.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import static android.view.View.GONE;

public class AktivityRegist extends AppCompatActivity {
    private static final String TAG = "AktivityRegist";
    private EditText editTextNama, editTextFullNama, editTextPassword, editTextKonfirmPassword, editTextEmail;
    private Button buttonBuatAkun;
    private TextView textViewLogin;
    private ProgressBar loading;

    //    firebase
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktivity_regist);
        init();

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AktivityRegist.this, AktivityLogin.class));
                finish();
            }
        });

        buttonBuatAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);


                String username = editTextNama.getText().toString().trim().toLowerCase();
                String fullname = editTextFullNama.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (isValidEmail(email)) {
                    if (validasi()) {
                        regist(username, password, email, fullname);
                        Intent intentLogin = new Intent(AktivityRegist.this, AktivityLogin.class);
                        startActivity(intentLogin);
                        finish();
                    }
                } else {
                    if (!editTextEmail.getText().toString().isEmpty()) {
                        editTextEmail.setError("email tidak valid");
                    } else {
                        editTextEmail.setError("email tidak boleh kosong");
                    }
                    editTextEmail.requestFocus();
                    loading.setVisibility(GONE);

                }
            }
        });
    }

    private void init() {
        editTextEmail = findViewById(R.id.e_text_email_regist);
        editTextNama = findViewById(R.id.e_text_username);
        editTextFullNama = findViewById(R.id.e_text_full_name);
        editTextPassword = findViewById(R.id.e_text_password_regist);
        editTextKonfirmPassword = findViewById(R.id.e_text_password_konfirmasi);
        buttonBuatAkun = findViewById(R.id.btn_buat_akun);
        textViewLogin = findViewById(R.id.t_view_login);
        loading = findViewById(R.id.loading_buat_akun);

//        firebase
        auth = FirebaseAuth.getInstance();

    }

    private boolean validasi() {
        boolean valid = true;
        if (editTextPassword.getText().toString().matches("")) {
            editTextPassword.setError("password tidak boleh kosong");
            editTextPassword.requestFocus();
            valid = false;
        } else if (editTextPassword.getText().toString().length() < 6) {
            editTextPassword.setError("password harus berisi minimal 6 karakter");
            editTextPassword.requestFocus();
            valid = false;
        }

        if (!editTextKonfirmPassword.getText().toString().equalsIgnoreCase(editTextPassword.getText().toString())) {
            editTextKonfirmPassword.setError("password tidak sama");
            editTextKonfirmPassword.requestFocus();
            valid = false;
        } else if (editTextKonfirmPassword.getText().toString().matches("")) {
            editTextKonfirmPassword.setError("harap isi konfirmasi password");
            editTextKonfirmPassword.requestFocus();
            valid = false;
        }

        if (editTextFullNama.getText().toString().matches("")) {
            editTextPassword.setError("harap isi field ini");
            editTextPassword.requestFocus();
            valid = false;
        }
        if (editTextNama.getText().toString().matches("")) {
            editTextPassword.setError("harap isi field ini");
            editTextPassword.requestFocus();
            valid = false;
        }

        return valid;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void regist(final String username, String password, String email, final String fullname) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(AktivityRegist.this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loading.setVisibility(GONE);
                            FirebaseUser user = auth.getCurrentUser();
                            String userId = user.getUid();
                            String urlImage = "https://firebasestorage.googleapis.com/v0/b/projectakhir-b44ba.appspot.com/o/user.png?alt=media&token=ef846caf-58a2-452f-996d-769be3f19da1";
                            UserData userData = new UserData(userId, username, fullname, "", urlImage);

                            db = FirebaseFirestore.getInstance();
                            db.collection("users").document(userId).set(userData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                loading.setVisibility(GONE);
                                                Toast.makeText(AktivityRegist.this, "Akun berhasil dibuat", Toast.LENGTH_SHORT).show();
                                                Toast.makeText(AktivityRegist.this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            String errorcode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                            if ("ERROR_EMAIL_ALREADY_IN_USE".equals(errorcode)) {
                                loading.setVisibility(View.GONE);
                                editTextEmail.setError("email sudah terdaftar, harap login saja");
                                editTextEmail.requestFocus();
                            }
                        }
                    }
                });
    }
}