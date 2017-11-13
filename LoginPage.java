package com.example.benodonnell.a3rdyearproject;

        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;

        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;


public class LoginPage extends AppCompatActivity {
    private ProgressDialog mProgress;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        fireBaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    mProgress.dismiss();
                    startActivity(new Intent(LoginPage.this, MapsPage.class));
                }else{
                    mProgress.dismiss();
                }
            }
        };
        login();
    }

    public void login() {
        Button btn;
        btn = (Button) findViewById(R.id.bLogin);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mProgress.setMessage("Signing in......");
                mProgress.show();
                EditText email = (EditText) findViewById(R.id.editText);
                EditText password = (EditText) findViewById(R.id.editText2);

                final String emailText = email.getText().toString().trim();
                final String passwordsText = password.getText().toString().trim();


                if (emailText.length() >1  || passwordsText.length() >1) {
                    //Logging in the user
                    firebaseAuth.signInWithEmailAndPassword(emailText, passwordsText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                                if (task.isSuccessful() == false) {
                                    AlertDialog dialog = new AlertDialog.Builder(LoginPage.this).create();
                                    dialog.setMessage("Email or password does not exist\n Please Try Again");
                                    dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Try Again\n",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    mProgress.dismiss();
                                                }

                                            });
                                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Register\n",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                        Intent i = new Intent(LoginPage.this, Register.class);
                                                        startActivity(i);

                                                }

                                            });

                                    dialog.show();
                                }
                        }
                    });
                } else {
                    mProgress.dismiss();
                }
            }
        });
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.bRegister) {
            Intent i = new Intent(LoginPage.this, Register.class);
            startActivity(i);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(fireBaseAuthListner);
    }

    public void onStop() {
        super.onStop();
        if (fireBaseAuthListner != null) {
            firebaseAuth.removeAuthStateListener(fireBaseAuthListner);
            firebaseAuth.signOut();

        }
    }

}

