package com.sample.stopking_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "REGISTER";

    private FirebaseAuth mAuth;         // 파이어베이스 인증
    private EditText mEtEmail, mEtPwd, mEtName;  //회원가입 입력필드
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어
    private Button mbtnRegister;        //회원가입 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mbtnRegister = findViewById(R.id.btn_register);

        mEtName = findViewById(R.id.et_name);

        mbtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 처리 시작(기입한 회원 정보를 가져온다.)
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();


                //파이어베이스 인증 진행 및 신규 계정 등록하기.
                mAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // task는 회원가입의 결과를 return
                        // 인증 처리가 완료되었을 때. 즉 가입 성공 시
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String email = firebaseUser.getEmail();
                            String uid = firebaseUser.getUid();
                            String strName = mEtName.getText().toString();

                            //추가내용
                            //CollectionReference userList = db.collection("users");

                            HashMap<Object, String> user = new HashMap<>();
                            user.put("uid", uid);
                            user.put("email", email);
                            user.put("name", strName);
                            //userList.document(email).set(user); // 이메일을 제목으로 하여 DB에 값을 저장

                            //문서 추가
                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "Register successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });
                            Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } // 회원가입 성공.

                        else { // 회원가입 실패한 경우우
                           Toast.makeText(RegisterActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}