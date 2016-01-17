package com.example.formel.bazadrzewmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.formel.bazadrzewmobile.tasks.RegisterTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    Button registerBtn;
    EditText loginTxt;
    EditText emailTxt;
    EditText passwTxt;
    EditText repPasswTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        registerBtn = (Button)findViewById(R.id.register_btn);
        loginTxt = (EditText)findViewById(R.id.reg_login);
        emailTxt = (EditText)findViewById(R.id.reg_email);
        passwTxt = (EditText) findViewById(R.id.reg_password);
        repPasswTxt = (EditText) findViewById(R.id.reg_rep_passw);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!(TextUtils.equals(passwTxt.getText(),repPasswTxt.getText()))){

                    repPasswTxt.setError("Hasła nie są takie same!");
                }
                else if(passwTxt.getText().toString().length() < 6){
                    passwTxt.setError("Hasło jest za krótkie!");
                }
                else if(!passwTxt.getText().toString().matches(".*\\d+.*")){
                    passwTxt.setError("Hasło musi zawierać przynajmniej jedną liczbę!");
                }
                else if(passwTxt.getText().toString().equals(passwTxt.getText().toString().toLowerCase())
                    || passwTxt.getText().toString().equals(passwTxt.getText().toString().toUpperCase())){
                    passwTxt.setError("Hasło musi zawierać duże i małe litery!");
                }
                else if(loginTxt.getText().length() == 0){
                    loginTxt.setError("Wypełnij to pole!");
                } else if(emailTxt.getText().length() == 0){
                    emailTxt.setError("Wypełnij to pole!");
                }
                else{
                    JSONObject jsonParams = new JSONObject();
                    try {
                        jsonParams.put("login", loginTxt.getText().toString());
                        jsonParams.put("upassword", passwTxt.getText().toString());
                        jsonParams.put("email", emailTxt.getText().toString());
                        jsonParams.put("adddate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        RegisterTask registerTask = new RegisterTask(jsonParams, RegisterActivity.this);
                        registerTask.setRegisterUser(new RegisterTask.RegisterUser() {
                            @Override
                            public void onFinishedRegistering(Integer responseCode) {
                                if (HttpURLConnection.HTTP_OK == responseCode){
                                    Toast.makeText(getApplicationContext(), "Zarejestrowano", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Wystąpił błąd: " + responseCode, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        registerTask.execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

}

