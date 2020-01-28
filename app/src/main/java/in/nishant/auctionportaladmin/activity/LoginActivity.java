package in.nishant.auctionportaladmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import in.nishant.auctionportaladmin.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmail,edtPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private TextView txtRegister;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initComponents();
        
        btnLogin.setOnClickListener(LoginActivity.this);
        txtRegister.setOnClickListener(LoginActivity.this);
    }

    private void initComponents() {
        edtEmail = findViewById(R.id.login_edtEmail);
        edtPassword= findViewById(R.id.login_edtPassword);
        btnLogin = findViewById(R.id.login_btnLogin);
        txtRegister = findViewById(R.id.login_txtRegister);
        
        // Firebase
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onClick(View v) {
        setListeners(v);
    }

    private void setListeners(View view) {
        if(view==btnLogin){
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            if(TextUtils.isEmpty(email)|TextUtils.isEmpty(password)){
                Toast.makeText(this, "Enter email and password!", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid username & Password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } else if(view==txtRegister){
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            finish();
        }
    }
}
