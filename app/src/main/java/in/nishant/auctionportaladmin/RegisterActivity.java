package in.nishant.auctionportaladmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout edtName, edtEmail, edtMobileno, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeComponents();

        btnRegister.setOnClickListener(RegisterActivity.this);
    }

    private void initializeComponents() {
        edtName = findViewById(R.id.reg_edtName);
        edtEmail = findViewById(R.id.reg_edtEmail);
        edtMobileno = findViewById(R.id.reg_edtMobileno);
        edtPassword = findViewById(R.id.reg_edtpassword);
        edtConfirmPassword = findViewById(R.id.reg_edtConfirmPassword);
        btnRegister = findViewById(R.id.reg_btnRegister);
        // Firebase variables
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(RegisterActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait");
    }

    private void setListeners() {
        String name = edtName.getEditText().getText().toString().trim();
        String email = edtEmail.getEditText().getText().toString().trim();
        String mobileno = edtMobileno.getEditText().getText().toString().trim();
        String password = edtPassword.getEditText().getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(name) | TextUtils.isEmpty(email) | TextUtils.isEmpty(mobileno) | TextUtils.isEmpty(password) | TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill the form!", Toast.LENGTH_SHORT).show();
        } else {
            if (password.equals(confirmPassword)) {
                pd.show();
                createAccount(email, password);
            } else {
                Toast.makeText(this, "Password didn't match!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        setListeners();
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Register Successfull", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
