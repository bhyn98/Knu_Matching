package com.example.knu_matching.membermanage;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.knu_matching.main.MainActivity;
import com.example.knu_matching.R;
import com.example.knu_matching.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseref;
    private EditText edt_StudentID, edt_Major, edt_Email, edt_password, edt_repassword, edt_Nickname;
    private Button btn_finish, btn_check_nick, btn_knuID;
    private String strEmail, strPassword, strNick, strMaojr, strStudentId;
    private boolean nickname_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseref = FirebaseDatabase.getInstance().getReference("Knu_Matching");
        DatabaseReference mProfieDatabaseReference = mDatabaseref.child("UserAccount");

        edt_StudentID = findViewById(R.id.edt_StudentID);
        edt_Major = findViewById(R.id.edt_Major);
        edt_Email = findViewById(R.id.edt_Email);
        edt_password = findViewById(R.id.edt_Password);
        edt_repassword = findViewById(R.id.edt_RePassword);
        edt_Nickname = findViewById(R.id.edt_nickname);


        btn_knuID = findViewById(R.id.btn_knuID);
        btn_check_nick = findViewById(R.id.btn_check_nick);
        btn_finish = findViewById(R.id.btn_registerButton);

        btn_check_nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strNick = edt_Nickname.getText().toString();
                System.out.println("test2222   " + strNick + " " + strEmail + " " + strStudentId);

                mProfieDatabaseReference.orderByChild("nickName").equalTo(strNick).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            nickname_state = false;
                            System.out.println("test_state" + nickname_state);
                            Toast.makeText(RegisterActivity.this, "실패패패패패", Toast.LENGTH_SHORT).show();
                        } else {
                            nickname_state = true;
                            System.out.println("test_state" + nickname_state);
                            Toast.makeText(RegisterActivity.this, "성공공공공공", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btn_knuID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveSubActivity();
            }
        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strStudentId = edt_StudentID.getText().toString();
                strEmail = edt_Email.getText().toString();
                strPassword = edt_password.getText().toString();
                strNick = edt_Nickname.getText().toString();
                System.out.println("test" + strNick + " " + strEmail + " " + strStudentId);

                if (strEmail.trim().equals("") || strPassword.trim().equals("") || strNick.trim().equals("") || strStudentId.trim().equals("") || strMaojr.trim().equals("")) {
                    Toast.makeText(RegisterActivity.this, "빈칸을 채워주세요:(", Toast.LENGTH_SHORT).show();
                } else {
                    if (nickname_state == false) {
                        Toast.makeText(RegisterActivity.this, "닉네임 중복여부를 확인해주세요:(", Toast.LENGTH_SHORT).show();
                    }
                    if (nickname_state == true) {
                        mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                    UserAccount account = new UserAccount();
                                    account.setIdToken(firebaseUser.getUid());
                                    account.setEmailId(firebaseUser.getEmail());
                                    account.setStudentId(strStudentId);
                                    account.setMajor(strMaojr);
                                    account.setNickName(strNick);
                                    account.setPassword(strPassword);
                                    mProfieDatabaseReference.child(firebaseUser.getEmail().replace(".", ">")).setValue(account);
                                    Toast.makeText(RegisterActivity.this, "성공", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void moveSubActivity() {
        Intent intent = new Intent(RegisterActivity.this, Student_Certificate.class);
        startActivityResult.launch(intent);
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "RegisterActivity로 돌아왔다. ");
                        strStudentId = result.getData().getStringExtra("StudentId");
                        System.out.println("회원가입 디버깅:" + strStudentId);
                        strMaojr = result.getData().getStringExtra("Major");
                        System.out.println("회원가입 디버깅:" + strMaojr);
                        strEmail = result.getData().getStringExtra("Email");
                        edt_StudentID.setText(strStudentId);
                        edt_Major.setText(strMaojr);
                        edt_Email.setText(strEmail);
                    }
                }
            });
}