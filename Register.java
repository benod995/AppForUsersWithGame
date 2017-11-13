package com.example.benodonnell.a3rdyearproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

/**
 * Created by benodonnell on 12/10/2017.
 */



public class Register extends Activity {

    private Button btn;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private DatabaseReference mDataBase;
    private ProgressDialog mProgress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
        btn = (Button) findViewById(R.id.bWelcome);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(Register.this, MapsPage
                            .class));
                }
            }
        };
        mProgress = new ProgressDialog(this);

    }

    public void registerUser(){
        EditText emailInput = (EditText) findViewById(R.id.emailIn);
        EditText passwordInput = (EditText) findViewById(R.id.passwordIn);
        EditText nameInput = (EditText) findViewById(R.id.nameIn);
        EditText patientInput = (EditText) findViewById(R.id.patientID);

        final String name = nameInput.getText().toString().trim();
        final String email = emailInput.getText().toString().trim();
        final String password = passwordInput.getText().toString();
        final String patient = patientInput.getText().toString().trim(); // TODO

        if (name.length() == 0){
            Toast.makeText(getApplicationContext(), "Please Enter a Name", Toast.LENGTH_SHORT).show();
        }else{
            if (email.length() == 0){
                Toast.makeText(getApplicationContext(), "Please Enter an Email", Toast.LENGTH_SHORT).show();
            }else{

                if (email.contains("@") == false){
                    Toast.makeText(getApplicationContext(), "Please Enter a Valid Email", Toast.LENGTH_SHORT).show();
                }else{
                    if (password.length() <6){
                        Toast.makeText(getApplicationContext(), "Please Enter a Password Password at Least 6 Characters", Toast.LENGTH_SHORT).show();
                    }else{
                        mProgress.setMessage("Signing Up......");
                        mProgress.show();
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                   if (task.isSuccessful()){
                                       String userId = firebaseAuth.getCurrentUser().getUid();
                                       mDataBase.child(userId);
                                       DatabaseReference current_user_db = mDataBase.child(userId);
                                       current_user_db.child("name").setValue(name);
                                       current_user_db.child(("image")).setValue("default");
                                       mProgress.dismiss();
                                   }
                               }
                           });
                    }
                }
            }
        }


    }
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListner);
    }

    public void onStop() {
        super.onStop();
        if (firebaseAuthListner != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListner);
            firebaseAuth.signOut();        }
    }

}
