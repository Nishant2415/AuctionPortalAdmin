package in.nishant.auctionportaladmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import in.nishant.auctionportaladmin.R;
import in.nishant.auctionportaladmin.utils.CustomToast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText edtName, edtEmail, edtMobileNo, edtPassword, edtConfirmPassword;
    private Button btnRegister;
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
        edtMobileNo = findViewById(R.id.reg_edtMobileNo);
        edtPassword = findViewById(R.id.reg_edtPassword);
        edtConfirmPassword = findViewById(R.id.reg_edtConfirmPassword);
        btnRegister = findViewById(R.id.reg_btnRegister);

        pd = new ProgressDialog(RegisterActivity.this,R.style.DialogTheme);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait");

        // Fire base variables
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    private void setListeners() {
        mName = edtName.getText().toString().trim();
        mEmail = edtEmail.getText().toString().trim();
        mMobileNo = edtMobileNo.getText().toString().trim();
        mPassword = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mName) | TextUtils.isEmpty(mEmail) | TextUtils.isEmpty(mMobileNo) | TextUtils.isEmpty(mPassword) | TextUtils.isEmpty(confirmPassword)) {
            CustomToast.show(RegisterActivity.this,0,"Please fill the form!");
        } else {
            if (mPassword.equals(confirmPassword)) {
                pd.show();
                createAccount(mEmail, mPassword);
            } else {
                CustomToast.show(RegisterActivity.this,0,"Password didn't match!");
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
                        if (task.isSuccessful()) {
                            Map<String, String> userInfo = new HashMap<>();
                            userInfo.put("username", mName);
                            userInfo.put("mobileNo", mMobileNo);
                            rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(userInfo)
                                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                CustomToast.show(RegisterActivity.this, 1, "Registration successful!");
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                                finish();
                                            } else {
                                                CustomToast.show(RegisterActivity.this, 0, "Something wrong!");
                                                Log.e("AuctionError", task.getException().toString());
                                            }
                                            pd.hide();
                                        }
                                    });
                        } else {
                            pd.hide();
                            CustomToast.show(RegisterActivity.this, 0, "Something wrong!");
                            Log.e("AuctionError", task.getException().toString());
                        }
                    }
                });
    }
}
