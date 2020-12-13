package com.example.infotransito;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailWrapper, passwordWrapper;
    private Button loginBtn;
    private TextView signupLink;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailWrapper = findViewById(R.id.emailWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);
        loginBtn = findViewById(R.id.loginBtn);
        signupLink = findViewById(R.id.signupLink);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginBtn.setOnClickListener(this::login);
        signupLink.setOnClickListener(this::goToSignUp);
        findViewById(R.id.sign_in_button).setOnClickListener(this::signIn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 6);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 6) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                // Sign in success, update UI with the signed-in user's information
                                HashMap<String, String> data = new HashMap<>();
                                data.put("id", user.getUid());
                                data.put("name", user.getDisplayName());
                                data.put("email", user.getEmail());
                                data.put("img", user.getPhotoUrl().toString());
                                db.collection("users")
                                        .document(user.getUid()).set(data).addOnCompleteListener(
                                        dataTask -> {
                                            if(dataTask.isSuccessful()){
                                                goToMain();
                                            }
                                        }
                                );
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                //                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                                //                            Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                                //                            updateUI(null);
                            }
                        });
    }

    private void login(View v){
        String email = emailWrapper.getEditText().getText().toString();
        String password = passwordWrapper.getEditText().getText().toString();

        // Validate data
        if(email.trim().isEmpty()){
            emailWrapper.setError("Escriba un correo.");
            return;
        } else emailWrapper.setError(null);

        if(!TextUtil.isValidEmailAddress(email)){
            emailWrapper.setError("Escriba un correo válido");
            return;
        } else emailWrapper.setError(null);

        if(password.trim().isEmpty()){
            passwordWrapper.setError("Escriba una contraseña");
            return;
        } else passwordWrapper.setError(null);

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                task -> {
                    if(task.isSuccessful()){
                        goToMain();
                    } else {
                        Toast.makeText(this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void goToSignUp(View v){
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
        finish();
    }
}