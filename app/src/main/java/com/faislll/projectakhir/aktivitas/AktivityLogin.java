package com.faislll.projectakhir.aktivitas;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.faislll.projectakhir.R;
import com.faislll.projectakhir.model.UserData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class AktivityLogin extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegist;
    private ProgressBar loadingLogin;
    private SignInButton signInButton;
    //private ImageView imageViewLoginGoogle;

    //    firebase
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private GoogleSignInClient client;

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktivity_login);
        init();

        textViewRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AktivityRegist.class));
               finish();

            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingLogin.setVisibility(View.VISIBLE);

                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (validasi()) {
                    if (AktivityRegist.isValidEmail(email)) {
                        loginEmail(email, password);
                    } else {
                        editTextEmail.setError("email tidak valid");
                        editTextEmail.requestFocus();
                    }
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(),MainActivity.class));
                logiGoogle();
                //finish();
            }
        });


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(AktivityLogin.this, gso);
    }

    private void init() {
        editTextEmail = findViewById(R.id.e_text_email_login);
        editTextPassword = findViewById(R.id.e_text_password_login);
        buttonLogin = findViewById(R.id.btn_login);
        textViewRegist = findViewById(R.id.txt_view_buat_akun);
        loadingLogin = findViewById(R.id.loading_login);
       signInButton = findViewById(R.id.login_dengan_google);
        //imageViewLoginGoogle = findViewById(R.id.login_dengan_google);

//        firebase
        auth = FirebaseAuth.getInstance();

    }

    private boolean validasi() {
        boolean valid = true;
        if (editTextPassword.getText().toString().matches("")) {
            editTextPassword.setError("harap isi password");
            editTextPassword.requestFocus();
            valid = false;
        }
        if (editTextEmail.getText().toString().matches("")) {
            editTextEmail.setError("email tidak boleh kosong");
            editTextEmail.requestFocus();
            valid = false;
        }
        loadingLogin.setVisibility(View.GONE);

        return valid;
    }

    private void loginEmail(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(AktivityLogin.this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            db = FirebaseFirestore.getInstance();
                            DocumentReference reference = db.collection("users").document(auth.getCurrentUser().getUid());
                            reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    loadingLogin.setVisibility(View.GONE);
                                    Intent intent = new Intent(AktivityLogin.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            String errorcode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                            switch (errorcode) {
                                case "ERROR_WRONG_PASSWORD":
                                    loadingLogin.setVisibility(View.GONE);
                                    editTextPassword.setError("password salah");
                                    editTextPassword.requestFocus();
                                    break;
                                case "ERROR_USER_NOT_FOUND":
                                    loadingLogin.setVisibility(View.GONE);
                                    editTextEmail.setError("email belum terdaftar");
                                    editTextEmail.requestFocus();
                                    break;
                            }
                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                assert account != null;
                firebaseAuthDenganGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, "Status Code " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthDenganGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    db = FirebaseFirestore.getInstance();
                    DocumentReference reference = db.collection("users").document(auth.getCurrentUser().getUid());
                    reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            loadingLogin.setVisibility(View.GONE);
                            Intent intent = new Intent(AktivityLogin.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

                    String userId = auth.getCurrentUser().getUid();
                    String nama = auth.getCurrentUser().getDisplayName().toLowerCase();
                    String username = nama.replace(" ", ".");
                    String fullname = auth.getCurrentUser().getDisplayName();
                    String urlImage = "https://firebasestorage.googleapis.com/v0/b/projectakhir-b44ba.appspot.com/o/user.png?alt=media&token=ef846caf-58a2-452f-996d-769be3f19da1";
                    UserData userData = new UserData(userId, username, fullname, "", urlImage);


                    db.collection("users").document(userId).set(userData);
                }
            }
        });
    }

    private void logiGoogle() {
        Intent intent = client.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Toast.makeText(this, String.valueOf(user.getDisplayName()), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AktivityLogin.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}