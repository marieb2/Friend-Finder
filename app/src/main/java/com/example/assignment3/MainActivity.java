package com.example.assignment3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button emailSignInButton ;
    private String emailString;
    //private DatabaseReference mDat;
    private String newUser = "false";
    private DatabaseReference mDatabase;

    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    String userId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //mDat = FirebaseDatabase.getInstance().getReference();


        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.createAccount).setOnClickListener(this);

        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        emailString = email;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userId = user.getUid();


                            //updateUI(user);

                            //User newUser = new User("Marie", emailString);
                            //mDat.child("users").child("fuck").setValue(user);

                            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                            intent.putExtra("email", emailString);
                            intent.putExtra("userID", userId) ;
                            intent.putExtra("isNewUser", newUser);
                            intent.putExtra("newUser", newUser) ;

                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        if (!task.isSuccessful()) {

                        }
                    }
                });
    }



    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.createAccount) {

            Intent intent = new Intent(MainActivity.this, CreateAccount.class);

            startActivity(intent);


        } else if (i == R.id.emailSignInButton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }

    }
}
