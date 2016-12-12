package com.mytrackerapp.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "EmailPassword";
    private EditText usernameInputText;
    private EditText emailInputText;
    private EditText passwordInputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInputText = (EditText) findViewById(R.id.email_input_Text);
        passwordInputText = (EditText) findViewById(R.id.password_input_text);
        usernameInputText = (EditText) findViewById(R.id.username_input_text);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // Validation Form: checking if username and password text field isn't empty
    private boolean validateForm() {
        boolean valid = true;
        String username = usernameInputText.getText().toString();
        if(username.isEmpty()){
            valid = false;
        }
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

    public void loginBtnClicked(View v){
        if(validateForm()) {
            String email = emailInputText.getText().toString();
            String password = passwordInputText.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "You are loged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MenuActivity.class));
                    }
                }
            });
        }
    }

    public void registerBtnClicked(View v) {
        if (validateForm()) {
            String email = emailInputText.getText().toString();
            String pass = passwordInputText.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Cannot connect to server",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        newUser temp = new newUser();
                        temp.setName(usernameInputText.getText().toString());
                        temp.setEmail(emailInputText.getText().toString());
                        temp.setLocation("0,0");
                        mDatabase.child("Users").push().setValue(temp);
                    }
                }
            });
        }
    }
}
