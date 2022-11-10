package com.sample.stopking_project;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {



    public static Date convertStringtoDate(String Date){ // 데이터베이스에서 가져온 날짜 변환
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try{
            date = format.parse(Date);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String caculateBank(int average_drink,int week_drink,int days){ // 절약 금액 계산
        int drink_price = 4500;
        int week = days / 7;
        int result = week * average_drink * week_drink * drink_price;
        DecimalFormat formatter = new DecimalFormat("###,###");  // 수에 콤마 넣기
        String result_str = formatter.format(result);
        return result_str;
    }

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int user_stop_days; // 금주 일수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        String getEmail = intent.getStringExtra("email");

        // 금주 날짜 가져오기
        DocumentReference docRef = db.collection("users").document(getEmail);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                TextView day_info = findViewById(R.id.day_info);
                String day_info_text = documentSnapshot.getString(("stop_drink"));
                Date date = convertStringtoDate(day_info_text);
                Date startDateValue = date;
                Date now = new Date();
                long diff = now.getTime() - startDateValue.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = (hours / 24) + 1;
                String d = String.valueOf(days);
                user_stop_days = Integer.parseInt(d);
                day_info.setText(d + "일째"); // 금주 날짜 계산 및 표시
            }
        });

        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() { // 절약 금액 계산을 위한 데이터 fetch
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                TextView bank_info = findViewById(R.id.bank_info);

                String average_drink_str = documentSnapshot.getString("average_drink");
                int average_drink_int = Integer.parseInt(average_drink_str);

                String week_drink_str = documentSnapshot.getString("week_drink");
                int week_drink_int = Integer.parseInt(week_drink_str);
                String bank_info_text = caculateBank(average_drink_int,week_drink_int,user_stop_days);
                bank_info.setText(bank_info_text + "원");
            }
        });






        Button btn_register = findViewById(R.id.btn_statistics);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 통계 화면으로 이동
                Intent intent = new Intent(MainActivity.this, Statistics.class);
                startActivity(intent);
            }
        });



    }


}