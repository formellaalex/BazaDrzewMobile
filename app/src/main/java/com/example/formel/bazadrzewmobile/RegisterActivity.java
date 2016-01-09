package com.example.formel.bazadrzewmobile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

                if(!passwTxt.getText().equals(repPasswTxt.getText())){
                    repPasswTxt.setError("Hasła nie są takie same!");
                }
                else if(loginTxt.getText().length() == 0){
                    loginTxt.setError("Wypełnij to pole!");
                }
                else if(emailTxt.getText().length() == 0){
                    emailTxt.setError("Wypełnij to pole!");
                }
                else{

                }
            }
        });


    }

}

