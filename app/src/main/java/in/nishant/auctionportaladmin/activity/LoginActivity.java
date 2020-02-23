package in.nishant.auctionportaladmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import in.nishant.auctionportaladmin.R;
import in.nishant.auctionportaladmin.utils.CustomToast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText edtEmail,edtPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private TextView txtRegister;
    private ProgressDialog pd;
    
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

        pd = new ProgressDialog(LoginActivity.this,R.style.DialogTheme);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait");

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
                CustomToast.show(LoginActivity.this,0,"Enter email and password!");
            } else {
                pd.show();
                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    finish();
                                } else {
                                    CustomToast.show(LoginActivity.this,0,"Invalid username or password!");
                                }
                                pd.dismiss();
                            }
                        });
            }
        } else if(view==txtRegister){
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        }
    }
}
