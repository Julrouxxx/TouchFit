package uqac.bigbrainstudio.touchfit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import uqac.bigbrainstudio.touchfit.MainActivity;
import uqac.bigbrainstudio.touchfit.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<AuthResult> {
    private FirebaseAuth mFirebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
        final Button loginButton = findViewById(R.id.login);
        final Button loginAnoButton = findViewById(R.id.login_anony);

        loginButton.setOnClickListener(this);
        loginAnoButton.setOnClickListener(this);

        if(getIntent().getBooleanExtra("googleOnly", false))
            loginAnoButton.setVisibility(View.INVISIBLE);

        loadingProgressBar = findViewById(R.id.loading);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 9001);
    }
    private void launchMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TouchFit", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 9001){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TouchFit", "Google sign in failed", e);
                Snackbar.make(loadingProgressBar, R.string.sign_in_failed_connection, Snackbar.LENGTH_LONG).show();
                // [START_EXCLUDE]
               loadingProgressBar.setVisibility(View.INVISIBLE);
                // [END_EXCLUDE]
            }
        }
    }

    @Override
    public void onClick(View v) {
        loadingProgressBar.setVisibility(View.VISIBLE);


        if(v.getId() == R.id.login){
            //Login via Google
            signIn();

        }else{

            signInAnonymously();
        }
    }

    private void signInAnonymously() {
        mFirebaseAuth.signInAnonymously().addOnCompleteListener(this);

    }

    @Override
    public void onComplete(@NonNull Task task) {
        loadingProgressBar.setVisibility(View.INVISIBLE);



        if (task.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information
            Log.d("TouchFit", "signInWithCredential:success");
            launchMainActivity();
            finish();
        } else {
            // If sign in fails, display a message to the user.
            Log.w("TouchFit", "signInWithCredential:failure", task.getException());
            Snackbar.make(findViewById(R.id.login), R.string.auth_failed, Snackbar.LENGTH_SHORT).show();
        }
    }
}
