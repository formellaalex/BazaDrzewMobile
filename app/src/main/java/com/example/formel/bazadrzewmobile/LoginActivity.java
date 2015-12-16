package com.example.formel.bazadrzewmobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.formel.bazadrzewmobile.helpers.AuthenticationHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private CheckTokenTask checkTokenTask;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton;
    private Button registerButton;
    private Button offlineBtn;
    HttpURLConnection conn;
    private static final int READ_CREDENTIALS = 333;
    private static final int RESOLUTION_CONNECTION = 444;
    private static final int UNKNOWN_CONNECTION_PROBLEM = 500;
    GoogleApiClient mCredentialsClient;
    ConnectionService connectionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        connectionService = new ConnectionService(getApplicationContext());
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        registerButton = (Button) findViewById(R.id.get_register_btn);
        offlineBtn = (Button)findViewById(R.id.offline_mode_btn);
        mCredentialsClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.CREDENTIALS_API).build();
        mCredentialsClient.connect();

/*
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
*/


        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        offlineBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //offlineMode();
                TestTokenTask ttt = new TestTokenTask();
                ttt.execute();

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public class TestTokenTask extends AsyncTask<Void, Void, Integer> {


        @Override
        protected Integer doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL("http://www.reichel.pl/bdp/webapi/web/check_user");
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("token","aj7aGH8ja");
                System.out.println("Response : " + conn.getResponseCode());


            } catch (IOException e) {
                Log.e("Error", "Error when connecting to url. " + e.toString());
//                Toast.makeText(getApplicationContext(), "Błąd podczas uwierzytelnienia.", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        // The good stuff goes here.
        Log.d("CONNECTION", "Success connection!");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
        Log.d("CONNECTION", "Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the 'Handle Connection Failures' section.
        Log.d("CONNECTION", "Failed");
    }

    private void attemptLogin() {
        if(connectionService.isOnline()) {
            if (mAuthTask != null) {
                return;
            }

            mEmailView.setError(null);
            mPasswordView.setError(null);

            // Store values at the time of the login attempt.
            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.
            if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgress(true);
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);

            }
        }
        else{
            //Toast.makeText(LoginActivity.this, "Brak połączenia z internetem.", Toast.LENGTH_LONG).show();
            offlineMode();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String responseString = null;
            try {
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mEmail, mPassword.toCharArray());
                    }
                });
                URL url = new URL("http://www.reichel.pl/bdp/webapi/web/app.php/login");
                conn = (HttpURLConnection) url.openConnection();
                return conn.getResponseCode();
            } catch (IOException e) {
                //return HttpsURLConnection.HTTP_UNAUTHORIZED;
                Log.d("Connection error", e.toString());

            }

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return -1;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    //return pieces[1].equals(mPassword);
                }
            }
            return UNKNOWN_CONNECTION_PROBLEM;
        }

        @Override
        protected void onPostExecute(final Integer result) {
            mAuthTask = null;
            showProgress(false);

            if (result == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                mEmailView.setError(getString(R.string.error_invalid_email));
            } else if(result == HttpsURLConnection.HTTP_OK) {
                Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                mainActivity.putExtra("LOGIN", mEmail);
                AuthenticationHelper.TOKEN = conn.getHeaderField("token");
                Random generator = new Random();
                saveCredentials(mEmail, mPassword, conn.getHeaderField("token")); //get token
                startActivity(mainActivity);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"Nieznany problem",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class CheckTokenTask extends AsyncTask<Void, Void, Integer> {

        private final Credential credential;
        private String token;

        CheckTokenTask(Credential mCredential){
            credential = mCredential;
            token = mCredential.getName();
        }


        @Override
        protected Integer doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL("http://www.reichel.pl/bdp/webapi/web/check_user");
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("token", Charset.forName("UTF-8").encode(token).toString());
                return conn.getResponseCode();

            } catch (IOException e) {
                Log.e("Error", "Error when connecting to url. " + e.toString());
//                Toast.makeText(getApplicationContext(), "Błąd podczas uwierzytelnienia.", Toast.LENGTH_SHORT).show();
            }


            return UNKNOWN_CONNECTION_PROBLEM;
        }

        @Override
        protected void onPostExecute(final Integer result){
            checkTokenTask = null;
            showProgress(false);
            if(HttpURLConnection.HTTP_OK == result){
                Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                mainActivity.putExtra("LOGIN", credential.getId());
                AuthenticationHelper.TOKEN = credential.getName();
                startActivity(mainActivity);
                finish();
            }
            else if(HttpURLConnection.HTTP_UNAUTHORIZED == result){
                mEmailView.setText(credential.getId());
                mPasswordView.setText(credential.getPassword());
                Log.e("READ CREDENTIAL", "GREAT! " + credential.getPassword() + "   token:" + credential.getName());
            }
            else{
                Toast.makeText(getApplicationContext(), "Wystąpił błąd.", Toast.LENGTH_LONG).show();
                Log.e("CHECK TOKEN RESULT", "Error when checking token. " + result);
            }
        }
    }

    private void saveCredentials(String login, String passw, String token){
        Credential credential = new Credential.Builder(login)
                .setPassword(passw)
                .setName(token)
                .build();
        Auth.CredentialsApi.save(mCredentialsClient, credential).setResultCallback(
                new ResultCallback() {
                    @Override
                    public void onResult(Result result) {
                        com.google.android.gms.common.api.Status status = result.getStatus();
                        if (status.isSuccess()) {
                            Log.d("CREDENTIALS", "SAVE: OK");
                            //Toast.makeText(this, "Credentials saved", Toast.LENGTH_SHORT).show();
                        } else {
                            if (status.hasResolution()) {
                                // Try to resolve the save request. This will prompt the user if
                                // the credential is new.
                                try {
                                    status.startResolutionForResult(LoginActivity.this, RESOLUTION_CONNECTION);
                                } catch (IntentSender.SendIntentException e) {
                                    // Could not resolve the request
                                    Log.e("CREDENTIALS_SAVE", "STATUS: Failed to send resolution.", e);
                                    //Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Request has no resolution
                                Log.d("CREDENTIALS_SAVE", "FAILED JAK PIERUN");
                            }
                        }
                    }
                });
    }



    @Override
    protected void onStart() {
        super.onStart();
        CredentialRequest mCredentialRequest = new CredentialRequest.Builder()
                .setSupportsPasswordLogin(true)
                .setAccountTypes(IdentityProviders.GOOGLE)
                .build();
        Auth.CredentialsApi.request(mCredentialsClient, mCredentialRequest).setResultCallback(
                new ResultCallback<CredentialRequestResult>() {
                    @Override
                    public void onResult(CredentialRequestResult credentialRequestResult) {
                        if (credentialRequestResult.getStatus().isSuccess()) {
                            // See "Handle successful credential requests"
                            //onCredentialRetrieved(credentialRequestResult.getCredential());
                            Log.d("API RETRIEVE", "SUCCESS RETRIEVE");

                        } else {
                            // See "Handle unsuccessful and incomplete credential requests"
                            //resolveResult(credentialRequestResult.getStatus());
                           Log.d("API RETRIEVE", "FAILED RETRIEVE" + "  " + credentialRequestResult.getStatus());

                            try {
                                if(!connectionService.isOnline()) {
                                    Toast.makeText(getApplicationContext(), "Nie udało się pobrać danych użytkownika - brak połączenia z internetem", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    credentialRequestResult.getStatus().startResolutionForResult(LoginActivity.this, READ_CREDENTIALS);
                                }
                            } catch (IntentSender.SendIntentException e) {
                                Toast.makeText(getApplicationContext(), "Nie udało się pobrać danych użytkownika - brak połączenia z internetem", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }
                });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 333) {
                if (resultCode == RESULT_OK) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    showProgress(true);
                    checkTokenTask = new CheckTokenTask(credential);
                    checkTokenTask.execute();

                }
                else{
                    Log.i("Info", "Other result code " + resultCode);
                }
            }
    }

    private void offlineMode(){
        new AlertDialog.Builder(LoginActivity.this).setTitle("Brak połączenia")
                .setMessage("Nie masz połączenia z internetem. Czy chcesz przejść do trybu offline?")
                .setPositiveButton("Przejdź", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                        mainActivity.putExtra("Mode", R.string.offline);
                        startActivity(mainActivity);
                        finish();
                    }

                })
            .show();
    }

}

