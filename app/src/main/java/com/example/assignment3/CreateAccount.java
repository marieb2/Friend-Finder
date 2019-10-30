package com.example.assignment3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class CreateAccount extends AppCompatActivity {

    private EditText name ;
    private EditText email;
    private EditText password;
    private Button createAccount;
    private static final String TAG = "EmailPassword";

    private FirebaseAuth mAuth;

    private String newUser;

    private DatabaseReference mDatabase;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        name = (EditText) findViewById(R.id.editText3);
        email = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);

        createAccount = (Button) findViewById(R.id.button);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateTheAccount(email.getText().toString(), password.getText().toString());

            }
        });



    }

    public void CreateTheAccount(String email2, String password) {

        mAuth.createUserWithEmailAndPassword(email2, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            newUser = "true";

                            Toast.makeText(CreateAccount.this, "Authentication Success",
                                    Toast.LENGTH_SHORT).show();

                            id = user.getUid();
                            User tmp_ = new User(name.getText().toString(),email.getText().toString() ,0,0);
                            mDatabase.child(id).setValue(tmp_) ;
                            //mDatabase.child(id).

                            Intent intent = new Intent(CreateAccount.this, MainActivity.class);

                            //intent.putExtra("name", name.getText().toString());
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccount.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
