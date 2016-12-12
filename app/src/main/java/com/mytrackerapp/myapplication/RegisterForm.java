package com.mytrackerapp.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterForm extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "EmailPassword";
    private EditText emailInputText;
    private EditText passwordInputText;
    private TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_form);

        emailInputText = (EditText)findViewById(R.id.email_input_Text);
        passwordInputText = (EditText)findViewById(R.id.password_input_text);
        emailTextView = (TextView)findViewById(R.id.email_text_view);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
             }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // Validation Form: checking if username and password text field isn't empty
    private boolean validateForm() {
        boolean valid = true;
        String email = emailInputText.getText().toString();
        if (email.isEmpty()) {
            valid = false;
        }

        String password = passwordInputText.getText().toString();
        if (password.isEmpty()) {
            valid = false;
        }
        return valid;
    }

    public void xxx(View v) {

       if (validateForm()) {
            String email = emailInputText.getText().toString();
            String pass = passwordInputText.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterForm.this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}


