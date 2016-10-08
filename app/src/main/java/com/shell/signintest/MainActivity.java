package com.shell.signintest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    
    private EditText mEditTextViewEmail;
    private EditText mEditTextViewPassword;

    private TextView mTextViewLoggedEmail;
    private TextView mTextViewLoggedUId;

    private Button m;

    //Firebase Variables
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextViewEmail = (EditText) findViewById(R.id.email_text);
        mEditTextViewPassword = (EditText) findViewById(R.id.password_text);

        mTextViewLoggedEmail = (TextView) findViewById(R.id.logged_email_id);
        mTextViewLoggedUId = (TextView) findViewById(R.id.logged_uid);

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);

        //Intialize Firebasse Variables
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User is signed in
                    Toast.makeText(MainActivity.this, "Loged In", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(MainActivity.this, homeActivity.class));
                } else {
                    //User is signed out
                }

                updateUI(user);
            }
        };
    }

    private void createAccount(String email, String password){
        if(!validateForm()){
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Unable to creat Account", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email, String password){
        if(!validateForm()){
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Unable to login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private  void  signOut(){
        mAuth.signOut();
        updateUI(null);
        Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
    }

    private void updateUI(FirebaseUser user){
        if(user != null){
            mTextViewLoggedEmail.setText(user.getEmail().toString());
            mTextViewLoggedUId.setText(user.getUid());
        }else{
            mTextViewLoggedEmail.setText(null);
            mTextViewLoggedUId.setText(null);
        }
    }

    private boolean validateForm(){
        boolean valid = true;

        String email = mEditTextViewEmail.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(MainActivity.this, "Email field required", Toast.LENGTH_SHORT).show();
            valid = false;
        }else{

        }

        String password = mEditTextViewPassword.getText().toString();
        if(TextUtils.isEmpty(password)){
            Toast.makeText(MainActivity.this, "Password field required", Toast.LENGTH_SHORT).show();
            valid = false;
        }else{

        }

        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.email_create_account_button){
            createAccount(mEditTextViewEmail.getText().toString(), mEditTextViewPassword.getText().toString());
        }else if(id == R.id.email_sign_in_button){
            signIn(mEditTextViewEmail.getText().toString(), mEditTextViewPassword.getText().toString());
        }else if(id == R.id.sign_out_button){
            signOut();
        }
    }
}
