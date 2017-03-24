package ee.ounapuu.herman.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ee.ounapuu.herman.messenger.CustomObjects.User;

import static ee.ounapuu.herman.messenger.R.id.register_email;

/**
 * Created by toks on 3/19/17.
 */

public class RegisterActivity extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase database;
    private DatabaseReference dbRef;

    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupFireBaseAuth();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
    }

    public void register(View view) {
        email = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.register_password);

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Authentication not successful",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            //todo: add user info to db
                            UserInfo userinfo = mAuth.getCurrentUser();
                            Log.d("userdata", mAuth.getCurrentUser().getEmail());
                            Log.d("userdata", mAuth.getCurrentUser().getUid());
                            addNewUserInfoToDB(userinfo.getUid(), userinfo.getEmail(), userinfo.getDisplayName());

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });


    }

    private void addNewUserInfoToDB(String uid, String userEmail, String username) {
        User dbUserData = new User(uid, username, userEmail);
        dbRef.child("users").child(uid).setValue(dbUserData);
    }

    private void setupFireBaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
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
}
