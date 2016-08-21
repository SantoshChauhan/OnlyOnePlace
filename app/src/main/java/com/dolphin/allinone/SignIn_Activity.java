package com.dolphin.allinone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.R.attr.password;
import static com.dolphin.allinone.R.id.email;

public class SignIn_Activity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{

    GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser firebaseUser;
    EditText edEmail;
    EditText edPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_);

        edEmail =(EditText)findViewById(R.id.editTextEmail);
        edPassword = (EditText)findViewById(R.id.editTextPassword);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("ggogle sign in ", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("google sign in", "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                //updateUI(user);
                // [END_EXCLUDE]
            }
        };
        SignInButton signInButton = (SignInButton)findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(this);

        Button emailSignInButton = (Button)findViewById(R.id.email_signup);
        emailSignInButton.setOnClickListener(this);
        Button btnSignUp = (Button)findViewById(R.id.btn_signup);
        btnSignUp.setOnClickListener(this);
        Button btnSignIn = (Button)findViewById(R.id.btn_signin);
        btnSignIn.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.email_signup:
                emailSignUp();
                break;
            case R.id.btn_signin:
                signInEmail();
                break;
            case R.id.btn_signup:
                doSignUp();
                break;
        }
    }

    private void signIn() {
        Intent signinIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signinIntent,20);
    }

    private void signInEmail()
    {
       mFirebaseAuth.signInWithEmailAndPassword(edEmail.getText()+"",edPassword.getText()+"").addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {


               if(!task.isSuccessful())
               {
                   Log.i("Signin Activity","Authentication failed");
               }
               else
               {
                   startActivity(new Intent(SignIn_Activity.this, MainActivity.class));
                   finish();
               }
           }
       });
    }

    private  void emailSignUp()
    {
        RelativeLayout rlEmail = (RelativeLayout)findViewById(R.id.rl_email);
        rlEmail.setVisibility(View.VISIBLE);


    }

    private void doSignUp()
    {

        mFirebaseAuth.createUserWithEmailAndPassword(edEmail.getText()+"",edPassword.getText()+"").addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(!task.isSuccessful())
                {

                }
                else
                {
                    Log.i("sign in activity","user is signed up");
                    signInEmail();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==20)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acccount)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(acccount.getIdToken(),null);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(!task.isSuccessful())
                {
                    Log.w("All in One", "signInWithCredential", task.getException());
                    Toast.makeText(SignIn_Activity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(SignIn_Activity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull com.google.android.gms.common.ConnectionResult connectionResult) {

    }
}
