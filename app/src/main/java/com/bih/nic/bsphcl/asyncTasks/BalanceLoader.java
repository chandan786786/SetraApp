package com.bih.nic.bsphcl.asyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bih.nic.bsphcl.utilitties.Urls_this_pro;
import com.bih.nic.bsphcl.utilitties.Utiilties;
import com.bih.nic.bsphcl.utilitties.WebHandler;

import org.json.JSONObject;

public class BalanceLoader extends AsyncTask<String, Void, String> {

    private AppCompatActivity activity;
    String key = "_USER_DETAILS";
    public BalanceLoader(AppCompatActivity activity){
        this.activity=activity;
    }
    @Override
    protected void onPreExecute() {

    }


    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        if (Utiilties.isOnline(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                result = WebHandler.callByPostwithoutparameter( Urls_this_pro.New_IN_URL + reqString(String.valueOf(strings[0])));
            }else{
                Toast.makeText(activity, "Your device must have atleast Kitkat or Above Version", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(activity, "No Internet Connection !", Toast.LENGTH_SHORT).show();
        }
        return result;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //if (this.dialog1.isShowing()) this.dialog1.cancel();
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                SharedPreferences prefs = activity.getSharedPreferences(key,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("WalletAmount", jsonObject.getString("accountBalance"));
                editor.commit();
                //balanceListner.balanceReceived(Double.parseDouble(jsonObject.getString("accountBalance")));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(activity, "Server Problem", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        //balanceListner.balanceReceived(0.00);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(),activity);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, android.util.Base64.NO_WRAP );//.getEncoder().encodeToString(chipperdata);
        encString=encString.replaceAll("\\/","SSLASH").replaceAll("\\=","EEQUAL").replaceAll("\\+","PPLUS");
        return encString;
    }
//    public static void bindmListener(BalanceListner listener) {
//        balanceListner = listener;
//    }
}
