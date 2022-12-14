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


public class Smoke_RankingActivity extends AppCompatActivity implements View.OnClickListener {



    private final int FRAGMENT_DAY = 1;
    private final int FRAGMENT_PACK = 2;
    private ImageView backButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어
    private String getEmail;
    private String my_ranking_name;
    private String getName;
    private String getDay;
    private String getPack;
    private Button btn_stop_smoke_day, btn_stop_smoke_pack;
    boolean btn_day_active = true;
    boolean btn_pack_active = false;
    private Smoke_FragmentDay fragmentDay;
    private Smoke_FragmentPack fragmentPack;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smoke_ranking);


        mAuth = FirebaseAuth.getInstance();
        backButton = findViewById(R.id.btn_back);


        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        getEmail = intent.getStringExtra("email");
        getName = intent.getStringExtra("name");
        getDay = intent.getStringExtra("day");
        getPack = intent.getStringExtra("pack");


        Bundle bundle = new Bundle(); // 프라그먼트에 넘겨줄 정보들
        bundle.putString("email", getEmail);
        bundle.putString("name", getName);
        bundle.putString("day", getDay);
        bundle.putString("pack", getPack);

        btn_stop_smoke_day = (Button) findViewById(R.id.btn_stop_smoke_day);
        btn_stop_smoke_pack = (Button) findViewById(R.id.btn_stop_smoke_pack);

        btn_stop_smoke_day.setOnClickListener(this);
        btn_stop_smoke_pack.setOnClickListener(this);
        callFragment(FRAGMENT_DAY, bundle);
        backButton.bringToFront();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 해당 액티비티 종료 후 전 화면으로 이동.
                finish();
            }
        });

        ProgressDialog progressDialog = new ProgressDialog(Smoke_RankingActivity.this);
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
        bundle.putString("pack", getPack);

        switch (v.getId()) {
            case R.id.btn_stop_smoke_day:
                // '버튼DAY' 클릭 시 '프래그먼트DAY' 호출 및 버튼 색 변경


                callFragment(FRAGMENT_DAY, bundle);
                if (btn_pack_active) {
                    btn_stop_smoke_day.setBackgroundResource(R.drawable.remove_btn_padding_active);
                    btn_stop_smoke_pack.setBackgroundResource(R.drawable.remove_btn_padding);
                    btn_pack_active = false;
                    btn_day_active = true;
                }
                break;

            case R.id.btn_stop_smoke_pack:
                // '버튼PACK' 클릭 시 '프래그먼트PACK' 호출 및 버튼 색 변경
                callFragment(FRAGMENT_PACK, bundle);
                if (btn_day_active) {
                    btn_stop_smoke_day.setBackgroundResource(R.drawable.remove_btn_padding);
                    btn_stop_smoke_pack.setBackgroundResource(R.drawable.remove_btn_padding_active);
                    btn_day_active = false;
                    btn_pack_active = true;
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
                    fragmentDay = new Smoke_FragmentDay();
                    fragmentDay.setArguments(bundle);
                    manager.beginTransaction().add(R.id.smoke_fragment_container,fragmentDay).commit();
                }
                if(fragmentDay!=null) manager.beginTransaction().show(fragmentDay).commit();
                if(fragmentPack!=null) manager.beginTransaction().hide(fragmentPack).commit();
                break;

            case 2:
                // '프래그먼트BOTTLE' 호출

                if(fragmentPack == null){
                    ProgressDialog progressDialog = new ProgressDialog(Smoke_RankingActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("잠시 기다려 주세요.");
                    progressDialog.show();

                    new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 1000);

                    fragmentPack = new Smoke_FragmentPack();
                    fragmentPack.setArguments(bundle);
                    manager.beginTransaction().add(R.id.smoke_fragment_container,fragmentPack).commit();
                }
                if(fragmentPack!=null) manager.beginTransaction().show(fragmentPack).commit();
                if(fragmentDay!=null) manager.beginTransaction().hide(fragmentDay).commit();

                break;
        }
    }
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//
//        switch (fragment_no) {
//            case 1:
//                // '프래그먼트1' 호출
//
//                Smoke_FragmentDay fragmentDay = new Smoke_FragmentDay();
//                fragmentDay.setArguments(bundle);
//                fragmentTransaction.replace(R.id.smoke_fragment_container, fragmentDay);
//                fragmentTransaction.commit();
//                break;
//
//            case 2:
//                // '프래그먼트2' 호출
//                Smoke_FragmentPack fragmentPack = new Smoke_FragmentPack();
//                fragmentPack.setArguments(bundle);
//                fragmentTransaction.replace(R.id.smoke_fragment_container, fragmentPack);
//                fragmentTransaction.commit();
//                break;
//        }
//    }
}
