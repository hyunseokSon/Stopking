package com.sample.stopking_project;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class Drink_RankingActivity extends AppCompatActivity implements View.OnClickListener {



    private final int FRAGMENT_DAY = 1;
    private final int FRAGMENT_BOTTLE = 2;
    private ImageView backButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어
    private String getEmail;
    private String my_ranking_name;
    private String getName;
    private String getDay;
    private String getBottle;
    private Button btn_stop_drink_day, btn_stop_drink_bottle;
    boolean btn_day_active = true;
    boolean btn_bottle_active = false;
    private Drink_FragmentDay fragmentDay;
    private Drink_FragmentBottle fragmentBottle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_ranking);

        mAuth = FirebaseAuth.getInstance();
        backButton = findViewById(R.id.btn_back);

        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        getEmail = intent.getStringExtra("email");
        getName = intent.getStringExtra("name");
        getDay = intent.getStringExtra("day");
        getBottle = intent.getStringExtra("bottle");


        Bundle bundle = new Bundle(); // 프라그먼트에 넘겨줄 정보들
        bundle.putString("email", getEmail);
        bundle.putString("name", getName);
        bundle.putString("day", getDay);
        bundle.putString("bottle", getBottle);

        btn_stop_drink_day = (Button) findViewById(R.id.btn_stop_drink_day);
        btn_stop_drink_bottle = (Button) findViewById(R.id.btn_stop_drink_bottle);

        btn_stop_drink_day.setOnClickListener(this);
        btn_stop_drink_bottle.setOnClickListener(this);
        callFragment(FRAGMENT_DAY, bundle);


        backButton.bringToFront(); // z-index를 최상위로
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 해당 액티비티 종료 후 전 화면으로 이동.
                finish();
            }
        });


        ProgressDialog progressDialog = new ProgressDialog(Drink_RankingActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("잠시 기다려 주세요.");
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 1000);



    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("email", getEmail);
        bundle.putString("name", getName);
        bundle.putString("day", getDay);
        bundle.putString("bottle", getBottle);

        switch (v.getId()) {
            case R.id.btn_stop_drink_day:
                // '버튼DAY' 클릭 시 '프래그먼트DAY' 호출 및 버튼 색 변경


                callFragment(FRAGMENT_DAY, bundle);
                if (btn_bottle_active) {
                    btn_stop_drink_day.setBackgroundResource(R.drawable.remove_btn_padding_active);
                    btn_stop_drink_bottle.setBackgroundResource(R.drawable.remove_btn_padding);
                    btn_bottle_active = false;
                    btn_day_active = true;
                }
                break;

            case R.id.btn_stop_drink_bottle:
                // '버튼BOTTLE' 클릭 시 '프래그먼트BOTTLE' 호출 및 버튼 색 변경
                callFragment(FRAGMENT_BOTTLE, bundle);
                if (btn_day_active) {
                    btn_stop_drink_day.setBackgroundResource(R.drawable.remove_btn_padding);
                    btn_stop_drink_bottle.setBackgroundResource(R.drawable.remove_btn_padding_active);
                    btn_day_active = false;
                    btn_bottle_active = true;
                }
                break;
        }
    }


    private void callFragment(int fragment_no, Bundle bundle) {

        // 프래그먼트 사용을 위해
        FragmentManager manager = getSupportFragmentManager();

        switch (fragment_no) {
            case 1:
                // '프래그먼트DAY' 호출
                if(fragmentDay == null){
                    fragmentDay = new Drink_FragmentDay();
                    fragmentDay.setArguments(bundle);
                    manager.beginTransaction().add(R.id.drink_fragment_container,fragmentDay).commit();
                }
                if(fragmentDay!=null) manager.beginTransaction().show(fragmentDay).commit();
                if(fragmentBottle!=null) manager.beginTransaction().hide(fragmentBottle).commit();
                break;

            case 2:
                // '프래그먼트BOTTLE' 호출

                if(fragmentBottle == null){
                    ProgressDialog progressDialog = new ProgressDialog(Drink_RankingActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("잠시 기다려 주세요.");
                    progressDialog.show();

                    new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 1000);

                    fragmentBottle = new Drink_FragmentBottle();
                    fragmentBottle.setArguments(bundle);
                    manager.beginTransaction().add(R.id.drink_fragment_container,fragmentBottle).commit();
                }
                if(fragmentBottle!=null) manager.beginTransaction().show(fragmentBottle).commit();
                if(fragmentDay!=null) manager.beginTransaction().hide(fragmentDay).commit();

                break;
        }
    }
}
