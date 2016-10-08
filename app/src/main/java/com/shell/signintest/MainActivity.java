package com.shell.signintest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout mTextInputLayoutEmail;
    private TextInputLayout mTextInputLayoutPassword;
    
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

        mTextInputLayoutEmail = (TextInputLayout) findViewById(R.id.text_input_layout_email);
        mTextInputLayoutPassword = (TextInputLayout) findViewById(R.id.text_input_layout_password);

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

    //Create new Account(Sign Up)
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

    //sign In
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

    //Sign out
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

    //Validate Email and Password Field
    private boolean validateForm(){

        String email = mEditTextViewEmail.getText().toString();
        if(TextUtils.isEmpty(email)){
            mTextInputLayoutEmail.setError("Emaill Id Required");
            Toast.makeText(MainActivity.this, "Email field required", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!isEmailValid(email)){
            mTextInputLayoutEmail.setError("A valid Email id is required");
            return false;
        }else{
            mTextInputLayoutEmail.setError(null);
        }

        String password = mEditTextViewPassword.getText().toString();
        if(TextUtils.isEmpty(password)){
            mTextInputLayoutPassword.setError("Password required");
            Toast.makeText(MainActivity.this, "Password field required", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!isPasswordValid(password)) {
            mTextInputLayoutPassword.setError("Password must be at least 6 characters");
            return false;
        }else{
            mTextInputLayoutPassword.setError(null);
        }

        return true;
    }

    private boolean isEmailValid(String email) {

        boolean isValid = false;

        CharSequence emailString = email;

        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(emailString);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
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
