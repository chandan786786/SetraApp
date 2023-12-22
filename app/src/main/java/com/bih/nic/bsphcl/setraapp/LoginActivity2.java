package com.bih.nic.bsphcl.setraapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bih.nic.bsphcl.fragments.LoginFragment;
import com.bih.nic.bsphcl.model.UserInformation;
import com.bih.nic.bsphcl.smsReceiver.SmsListener;
import com.bih.nic.bsphcl.smsReceiver.SmsReceiver;
import com.bih.nic.bsphcl.utilitties.CommonPref;
import com.bih.nic.bsphcl.utilitties.GlobalVariables;
import com.bih.nic.bsphcl.utilitties.Urls_this_pro;
import com.bih.nic.bsphcl.utilitties.Utiilties;
import com.bih.nic.bsphcl.utilitties.WebHandler;
import com.bih.nic.bsphcl.webHelper.WebServiceHelper;


public class LoginActivity2 extends AppCompatActivity implements View.OnClickListener {
    EditText edit_user_name, edit_pass;
    Button button_login, signup;
    TextView text_ver,text_imei;
    String version;
    TextView text_forgot_password;
    TelephonyManager tm;
    private ProgressDialog dialog = null;
    private static String imei = "";
    SmsReceiver smsReceiver;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        init();
    }
    @Override
    public void onResume() {
        super.onResume();
        readPhoneState();
    }
    private void init() {
        text_imei= findViewById(R.id.txtimei);
        text_ver = (TextView) findViewById(R.id.txtVersion);
        edit_user_name = (EditText) findViewById(R.id.edit_username);
        edit_pass = (EditText) findViewById(R.id.edit_pass);
        button_login = (Button) findViewById(R.id.signin);
        signup = (Button) findViewById(R.id.signup);
        text_forgot_password = (TextView) findViewById(R.id.text_forget_pass);
        text_forgot_password.setOnClickListener(this);
        button_login.setOnClickListener(this);
        signup.setOnClickListener(this);
        if (CommonPref.getUserDetails(LoginActivity2.this) != null) {
            if (!CommonPref.getUserDetails(LoginActivity2.this).getUserID().equals("")) {
             //   edit_user_name.setText("" + CommonPref.getUserDetails(LoginActivity2.this).getUserID());
                edit_user_name.setText("AITM_SB");
            }
        }
        dialog = new ProgressDialog(
                LoginActivity2.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Authenticating...");
    }
    public void readPhoneState() {
        try {
            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) ;
            if (ActivityCompat.checkSelfPermission(LoginActivity2.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                imei = tm.getDeviceId();
            } else {
                imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
               //   imei = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            version = LoginActivity2.this.getPackageManager().getPackageInfo(LoginActivity2.this.getPackageName(), 0).versionName;
            Log.e("App Version : ", "" + version + " ( " + imei + " )");
            text_ver.setText("App Version : " + version );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signin) {
            if (edit_user_name.getText().toString().trim().equals("")) {
                Toast.makeText(LoginActivity2.this, "Enter User Name", Toast.LENGTH_SHORT).show();
            } else if (edit_pass.getText().toString().trim().equals("")) {
                Toast.makeText(LoginActivity2.this, "Enter Password", Toast.LENGTH_SHORT).show();
            } else if (!Utiilties.isOnline(LoginActivity2.this)) {
                Toast.makeText(LoginActivity2.this, "Please go online !", Toast.LENGTH_SHORT).show();
            } else {
                filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
                smsReceiver=new SmsReceiver();
                registerReceiver(smsReceiver, filter);
                SmsReceiver.bindListener(new SmsListener() {
                    @Override
                    public void messageReceived(String messageText) {
                        // text_resend.setVisibility(View.GONE);
                        Log.d("activity",""+messageText);
                        // dialog1.dismiss();
                        //if (smsVerificationService!=null && smsVerificationService.getStatus()!=SmsVerificationService.Status.RUNNING) {
                        new SmsVerificationService(LoginActivity2.this,imei,imei).execute(messageText.split(" ")[0].trim());
                        //}
                    }
                });
                new LoginLoader().execute(edit_user_name.getText().toString().trim() + "|" + edit_pass.getText().toString().trim() + "|" + imei.trim());
            }
        } else if (v.getId() == R.id.signup) {
            Intent intent = new Intent(LoginActivity2.this, RegisterActivity.class);
            startActivity(intent);
        }else if (v.getId() == R.id.text_forget_pass) {
            if (edit_user_name.getText().toString().trim().equals("")) {
                Toast.makeText(LoginActivity2.this, "Enter User Name", Toast.LENGTH_SHORT).show();
            } else {
              if(Utiilties.isOnline(LoginActivity2.this)){
                  final AlertDialog alertDialog1 = new AlertDialog.Builder(LoginActivity2.this).create();
                   alertDialog1.setTitle("Forgot Password");
                   alertDialog1.setMessage("Are you sure to reset Password");
                  alertDialog1.setButton(Dialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          new Requestforgetpassword().execute(reqString(edit_user_name.getText().toString() + "|" + imei ));
                      }
                  });
                  alertDialog1.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          if(alertDialog1.isShowing()){
                              alertDialog1.dismiss();
                          }
                      }
                  });
                   alertDialog1.show();
              }else{
                  Toast.makeText(this, "Please be online for this service", Toast.LENGTH_SHORT).show();
              }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    class LoginLoader extends AsyncTask<String, Void, UserInformation> {

        private final ProgressDialog dialog1 = new ProgressDialog(LoginActivity2.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity2.this).create();

        @Override
        protected void onPreExecute() {

            this.dialog1.setCanceledOnTouchOutside(false);
            this.dialog1.setMessage("Logging...");
            this.dialog1.show();
        }
        @Override
        protected UserInformation doInBackground(String... strings) {
            UserInformation userInfo2 = null;
            if (Utiilties.isOnline(LoginActivity2.this)) {
                String result = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    try {
                        result = WebHandler.callByPostwithoutparameter(Urls_this_pro.LOG_IN_URL + reqString(strings[0]));
                        System.out.println(result);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    alertDialog.setMessage("Android version must kitkat(19) or above !");
                    alertDialog.show();
                }
                if (result != null) {
                    userInfo2 = WebServiceHelper.loginParser(result);
                }
                return userInfo2;
            } else {
                userInfo2 = CommonPref.getUserDetails(LoginActivity2.this);
                if (userInfo2.getUserID().length() > 4) {
                    userInfo2.setAuthenticated(true);
                    return userInfo2;
                } else {
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(UserInformation result) {
            super.onPostExecute(result);
            if (this.dialog1.isShowing()) {
                this.dialog1.cancel();
            }
            if (result == null) {
                alertDialog.setTitle("Failed");
                alertDialog.setMessage("Something Went Wrong ! May be Server Problem !");
                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        edit_user_name.setFocusable(true);
                    }
                });
                alertDialog.show();
            } else if (!result.getAuthenticated()) {
                if((result.getMessageString().toString().contains("OTP SENT"))){
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setMessage("Waiting For Otp...");
                    dialog.show();
                }else{
                    String msgs="IMEI MISMATCH";
                    if(result.getMessageString().equalsIgnoreCase(msgs)){
                        text_imei.setVisibility(View.VISIBLE);
                        text_imei.setText("IMEI NO : "+imei);
                    }
                    alertDialog.setTitle("Failed");
                    alertDialog.setMessage("Authentication Failed" + result.getMessageString());
                    alertDialog.show();
                }
            } else {
                Intent cPannel = new Intent(LoginActivity2.this, Main2Activity.class);
                if (Utiilties.isOnline(LoginActivity2.this)) {
                    if (result != null) {
                        if (imei.equalsIgnoreCase(result.getImeiNo())) {
                            try {
                                result.setPassword(edit_pass.getText().toString());
                                GlobalVariables.LoggedUser = result;
                                CommonPref.setUserDetails(LoginActivity2.this, result);
                                startActivity(cPannel);
                                LoginActivity2.this.finish();

                            } catch (Exception ex) {
                                Toast.makeText(LoginActivity2.this, "Login failed due to Some Error !", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            alertDialog.setTitle("Device Not Registered");
                            alertDialog.setMessage("Sorry, your device is not registered!.\r\nPlease contact your Admin.");
                            alertDialog.setButton(Dialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    edit_user_name.setFocusable(true);
                                }
                            });
                            alertDialog.show();
                        }
                    }
                } else {
                    if (CommonPref.getUserDetails(LoginActivity2.this) != null) {
                        GlobalVariables.LoggedUser = result;
                        if (GlobalVariables.LoggedUser.getUserID().equalsIgnoreCase(edit_user_name.getText().toString().trim()) && GlobalVariables.LoggedUser.getPassword().equalsIgnoreCase(edit_pass.getText().toString().trim())) {
                            startActivity(cPannel);
                            LoginActivity2.this.finish();
                        } else {
                            Toast.makeText(LoginActivity2.this, "User name and password not matched !", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity2.this, "Please enable internet connection for first time login.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(), LoginActivity2.this);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, Base64.NO_WRAP);//.getEncoder().encodeToString(chipperdata);
        encString = encString.replaceAll("\\/", "SSLASH").replaceAll("\\=", "EEQUAL").replaceAll("\\+", "PPLUS");
        return encString;
    }
    class Requestforgetpassword extends AsyncTask<String,Void,String>{
        private final ProgressDialog dialog =new ProgressDialog(LoginActivity2.this);
        private final AlertDialog alertdialog=new AlertDialog.Builder(LoginActivity2.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCancelable(false);
            this.dialog.setMessage("Processing....");
           // this.dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result=null;
                result = WebHandler.callByGet( Urls_this_pro.FORGET_PASSWORD_URL+strings[0]);
            return result;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(this.dialog.isShowing()){
                this.dialog.cancel();
            }
            if(s!=null){
                alertdialog.setMessage(s.toString());
                alertdialog.show();
            }
        }
    }
    public class SmsVerificationService extends AsyncTask<String, Void, String> {
        private Activity activity;
        private android.app.AlertDialog alertDialog;
        String imei,serial_id;
        public SmsVerificationService(Activity activity, String imei, String serial_id) {
            this.activity = activity;
            this.imei=imei;
            this.serial_id=serial_id;
            alertDialog = new android.app.AlertDialog.Builder( this.activity).create();
        }
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Verifying...");
            dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            if (Utiilties.isOnline(activity)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Log.d("params",strings[0]);
                    //return WebServiceHelper.validateotp(userid, imei, strings[0]);
             return       WebHandler.callByGet(Urls_this_pro.GET_VALIDATE_OTP + reqString(edit_user_name.getText().toString()+"|"+edit_pass.getText().toString()+"|"+imei+"|"+String.valueOf(strings[0])));
                } else {
                    Log.e("error","Your device must have atleast Kitkat or Above Version");
                }
            } else {
                Log.e("error","No Internet Connection !");
            }
            return result;

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Log.e("res","res varification :"+result);
                if (result.contains("SUCCESS")) {
                    if (dialog.isShowing()) dialog.cancel();
                    new LoginLoader().execute(edit_user_name.getText().toString().trim() + "|" + edit_pass.getText().toString().trim() + "|" + imei.trim());
                } else {
                    if (dialog.isShowing()) dialog.cancel();
                    Toast.makeText(activity, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (dialog.isShowing()) dialog.cancel();
                Toast.makeText(activity, "Server Problem", Toast.LENGTH_SHORT).show();
            }
        }
    }
}