package com.mytrackerapp.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "EmailPassword";
    private EditText usernameInputText;
    private EditText emailInputText;
    private EditText passwordInputText;
    private ArrayList<newUser> modelItems;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        modelItems = new ArrayList<newUser>();
        emailInputText = (EditText) findViewById(R.id.email_input_Text);
        passwordInputText = (EditText) findViewById(R.id.password_input_text);
        usernameInputText = (EditText) findViewById(R.id.username_input_text);
        loginBtn = (Button)findViewById(R.id.login_button);
        loginBtn.setEnabled(false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.getRoot().child("Users");

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
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    newUser post = postSnapshot.getValue(newUser.class);
                    modelItems.add(new newUser(post.getName(),post.getEmail(),postSnapshot.getKey(),post.getPermission()));
                    System.out.println("DONE");
                    loginBtn.setEnabled(true);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            final String email = emailInputText.getText().toString();
            String password = passwordInputText.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0 ; i < modelItems.size() ; i++) {
                            if(modelItems.get(i).getEmail().toLowerCase().equals(mAuth.getCurrentUser().getEmail()) && modelItems.get(i).getPermission().equals("User")){
                                Toast.makeText(MainActivity.this, "You are loged in successfully", Toast.LENGTH_SHORT).show();
                                Intent intentBundle = new Intent(MainActivity.this,MenuActivity.class);
                                Bundle bundle = new Bundle();
                                String[] cords = {modelItems.get(i).getName(),modelItems.get(i).getLocation()};
                                bundle.putStringArray("key", cords);
                                intentBundle.putExtras(bundle);
                                startActivity(intentBundle);
                            } else if(modelItems.get(i).getEmail().toLowerCase().equals(email.toLowerCase()) && modelItems.get(i).getPermission().equals("Tech")){
                                Toast.makeText(MainActivity.this,"Welcome Tech",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this,TechMenuActivity.class));
                            } else if(modelItems.get(i).getEmail().toLowerCase().equals(email.toLowerCase()) && modelItems.get(i).getPermission().equals("Admin")){
                                Toast.makeText(MainActivity.this,"Welcome Boss",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this,AdminActivity.class));
                            }
                        }
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
                        /*for (int i = 0 ; i < 50 ; i++){
                            newUser temp = new newUser();
                            temp.setName(usernameInputText.getText().toString()+i);
                            temp.setEmail(emailInputText.getText().toString()+i);
                            temp.setLocation("0,0");
                            temp.setPermission("User");
                            mDatabase.getRoot().child("Users").push().setValue(temp);
                        }*/
                        newUser temp = new newUser();
                        temp.setName(usernameInputText.getText().toString());
                        temp.setEmail(emailInputText.getText().toString());
                        temp.setLocation("0,0");
                        temp.setPermission("User");
                        mDatabase.getRoot().child("Users").push().setValue(temp);
                        startActivity(new Intent(MainActivity.this ,MainActivity.class));
                        Toast.makeText(MainActivity.this, "You have been regestered please logins",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
