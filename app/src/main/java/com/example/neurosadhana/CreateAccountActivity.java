package com.example.neurosadhana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    EditText emaiEditText,pasEditText,conEditText;
    Button CreateAccountBtn;
    ProgressBar progressBar;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emaiEditText = findViewById(R.id.emaileditText);
        pasEditText = findViewById(R.id.passeditText);
        conEditText = findViewById(R.id.confpasseditText);
        CreateAccountBtn = findViewById(R.id.createaccountbtn);
        progressBar = findViewById(R.id.Progress_bar);
        login = findViewById(R.id.login);

        CreateAccountBtn.setOnClickListener(v-> createAccount());
        login.setOnClickListener(v-> finish());
    }

    void createAccount(){
        String email = emaiEditText.getText().toString();
        String password = pasEditText.getText().toString();
        String confirmPassword = conEditText.getText().toString();

        boolean validate = isValidate(email,password,confirmPassword);

        if(!validate){
            return;
        }

        createAccountInFirebase(email,password);

    }
    void createAccountInFirebase(String email, String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    Toast.makeText(CreateAccountActivity.this,"Succesfully create account,Check email to verify",Toast.LENGTH_SHORT).show();
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    finish();
                }else{
                    Toast.makeText(CreateAccountActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            CreateAccountBtn.setVisibility(View.GONE);
        }else{
            CreateAccountBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
    boolean isValidate(String email,String password,String confirmPassword){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emaiEditText.setError("Email is invalid");
            return false;
        }
        if(password.length()<7){
            pasEditText.setError("Password length is invalid");
            return false;
        }
        if(!password.equals(confirmPassword)){
            conEditText.setError("Password doesn't match");
            return false;
        }
        return true;
    }
}