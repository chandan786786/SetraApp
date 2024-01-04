package com.bih.nic.bsphcl.setraapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.RequiresApi;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bih.nic.bsphcl.model.UserInformation;
import com.bih.nic.bsphcl.retrofit.APIClient;
import com.bih.nic.bsphcl.retrofit.APIInterface;
import com.bih.nic.bsphcl.retrofit.Urls_this_pro;
import com.chaos.view.PinView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImeiUpdationActivity extends AppCompatActivity {

    Toolbar toolbar_imei;
    TextView textUserId, textImei, textOldImei;
    Button button_req;
    UserInformation userInfo;
    String imei;

    PinView pinView;
    private APIInterface apiInterface;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imei_updation);
        toolbar_imei = findViewById(R.id.toolbar_imei);
        toolbar_imei.setTitle("Register");
        setSupportActionBar(toolbar_imei);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        imei = getIntent().getStringExtra("imei");
        userInfo = (UserInformation) getIntent().getSerializableExtra("user");
        init();
    }

    void init() {
        textUserId = findViewById(R.id.text_uid);
        textImei = findViewById(R.id.text_imei);
        //textOldImei=findViewById(R.id.text_old_imei);
        pinView = findViewById(R.id.pinview);
        pinView.setVisibility(View.INVISIBLE);
        button_req = findViewById(R.id.button_req);
        button_req.setOnClickListener((view) -> {
            if (button_req.getText().equals("Request For Update")) {
                //request for otp
                apiInterface = APIClient.getClient(Urls_this_pro.RETROFIT_BASE_URL).create(APIInterface.class);
                Call<UserInformation> call1 = apiInterface.doLogin(reqString);
                progressDialog = new ProgressDialog(ImeiUpdationActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                call1.enqueue(new Callback<UserInformation>() {
                    @Override
                    public void onResponse(Call<UserInformation> call, Response<UserInformation> response) {
                        UserInformation userInformation = response.body();
                        if (!userInformation.isImeiUpdated()) {
                            pinView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserInformation> call, Throwable t) {
                        Log.e("error", t.getMessage());
                        Toast.makeText(ImeiUpdationActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        call.cancel();
                    }
                });
            } else {
                //provide otp
                String otp = pinView.getText().toString();

            }
        });
    }
}