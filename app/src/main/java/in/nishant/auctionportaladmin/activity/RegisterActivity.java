package in.nishant.auctionportaladmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import in.nishant.auctionportaladmin.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtName, edtEmail, edtMobileno, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private FirebaseUser currentUser;
    private ProgressDialog pd;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String mName,mEmail,mMobileNo,mPassword;

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
        edtPassword = findViewById(R.id.reg_edtPasssword);
        edtConfirmPassword = findViewById(R.id.reg_edtConfirmPassword);
        btnRegister = findViewById(R.id.reg_btnRegister);

        pd = new ProgressDialog(RegisterActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait");

        // Firebase variables
        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    private void setListeners() {
        mName = edtName.getText().toString().trim();
        mEmail = edtEmail.getText().toString().trim();
        mMobileNo = edtMobileno.getText().toString().trim();
        mPassword = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mName) | TextUtils.isEmpty(mEmail) | TextUtils.isEmpty(mMobileNo) | TextUtils.isEmpty(mPassword) | TextUtils.isEmpty(confirmPassword)) {
            View view = getLayoutInflater().inflate(R.layout.layout_toast,null,false);
            TextView txtName = view.findViewById(R.id.toast_txtName);
            txtName.setText("Successful!");
            Toast toast = new Toast(RegisterActivity.this);
            toast.setView(view);
            toast.show();
        } else {
            if (mPassword.equals(confirmPassword)) {
                pd.show();
                createAccount(mEmail, mPassword);
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
                            Map<String,String> userInfo = new HashMap<>();
                            userInfo.put("name",mName);
                            userInfo.put("mobileNo",mMobileNo);
                            rootRef.child("Admin").child(currentUser.getUid()).setValue(userInfo)
                                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                            pd.dismiss();
                                        }
                                    });

                        } else {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
