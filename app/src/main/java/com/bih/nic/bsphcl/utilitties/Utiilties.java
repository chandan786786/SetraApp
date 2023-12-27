package com.bih.nic.bsphcl.utilitties;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bih.nic.bsphcl.setraapp.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Chandan Singh on 11-06-2018.
 */
public class Utiilties {
	//public static String APP_DIR = SplashActivity.SDCARD+"/Logfile";
	//public static String LOG_FILE_PATH = APP_DIR+"/ myapp_log.txt";

	KeyPairGenerator kpg;
	KeyPair kp;
	PublicKey publicKey;
	PrivateKey privateKey;
	byte[] encryptedBytes, decryptedBytes;
	Cipher cipher, cipher1;
	String encrypted, decrypted;

	public Utiilties() {
		// TODO Auto-generated constructor stub
	}

	public static void ShowMessage(Context context, String Title, String Message) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(Title);
		alertDialog.setMessage(Message);
		alertDialog.show();
	}



    //check online
	public static boolean isOnline(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected() == true);
	}


	//generate photo of specific height and width
	public static Bitmap GenerateThumbnail(Bitmap imageBitmap,
                                           int THUMBNAIL_HEIGHT, int THUMBNAIL_WIDTH) {

		Float width = new Float(imageBitmap.getWidth());
		Float height = new Float(imageBitmap.getHeight());
		Float ratio = width / height;
		Bitmap CompressedBitmap = Bitmap.createScaledBitmap(imageBitmap,
				(int) (THUMBNAIL_HEIGHT * ratio), THUMBNAIL_HEIGHT, false);

		return CompressedBitmap;
	}


    //set text on image
	public static Bitmap DrawText(AppCompatActivity activity, Bitmap mBitmap, String displaytext1,
                                  String displaytext2, String displaytext3, String displaytext4) {
		Bitmap bmOverlay = Bitmap.createBitmap(mBitmap.getWidth(),
				mBitmap.getHeight(), Bitmap.Config.ARGB_4444);
		// create a canvas on which to draw
		Canvas canvas = new Canvas(bmOverlay);

		Paint paint = new Paint();
		paint.setColor(activity.getResources().getColor(R.color.colorAccent));
		paint.setTextSize(40);
		 paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		 paint.setStyle(Paint.Style.STROKE);
		 paint.setStrokeWidth(3);
		 paint.setFakeBoldText(false);
		 paint.setShadowLayer(1, 0, 0, Color.BLACK);

		// if the background image is defined in main.xml, omit this line
		canvas.drawBitmap(mBitmap, 0, 0, paint);

		canvas.drawText(displaytext1, 10, mBitmap.getHeight() - 100, paint);
		canvas.drawText(displaytext2, 10, mBitmap.getHeight() - 50, paint);

		canvas.drawText(displaytext3, 10, mBitmap.getHeight() - 150, paint);

		canvas.drawText(displaytext4, 10, mBitmap.getHeight() - 200, paint);
		// set the bitmap into the ImageView
		return bmOverlay;
	}

	//deserilese byte array data
	public static Object deserialize(byte[] data) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			ObjectInputStream is = new ObjectInputStream(in);
			return is.readObject();
		} catch (Exception ex) {
			return null;
		}
	}

	public static String getDateString() {
		SimpleDateFormat postFormater = new SimpleDateFormat(
				"MMMM dd, yyyy hh:mm a");

		String newDateStr = postFormater.format(Calendar.getInstance()
				.getTime());
		return newDateStr;




	}

	public static String getDateString(String formats) {
		SimpleDateFormat postFormater = new SimpleDateFormat(formats);

		String newDateStr = postFormater.format(Calendar.getInstance().getTime());
		return newDateStr;
	}






	public static void setStatusBarColor(AppCompatActivity activity)
	{
		if (Build.VERSION.SDK_INT >= 21) {

			Window window = activity.getWindow();


			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

			// finally change the color
			window.setStatusBarColor(Color.parseColor("#1565a9"));
		}
	}




	public static String getCurrentDate()
	{
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
		return formattedDate.trim();

	}




	public static String getCurrentDateWithTime() throws ParseException {

		SimpleDateFormat f = new SimpleDateFormat("MMM d,yyyy HH:mm");
		Date date=null;
		date=f.parse(getDateString());
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM d,yyyy HH:mm a");
		String dateString = formatter.format(date);
		return dateString;
	}



	public static String getDateTime() {

		String date=getDateString();
		String a="";
		StringTokenizer st=new StringTokenizer(date," ");
		while (st.hasMoreTokens()){
			a=st.nextToken();
		}

		if(a.equals("a.m."))
		{

			date=date.replace(a,"AM");
		}
		if(a.equals("p.m."))
		{
			date=date.replace(a,"PM");


		}


		return date;
	}


    //get show current date
	public static String getshowCurrentDate()
	{
		Calendar cal= Calendar.getInstance();
		int day=cal.get(Calendar.DAY_OF_MONTH);
		int month=cal.get(Calendar.MONTH);
		int year=cal.get(Calendar.YEAR);
		month =month+1;

		int h=cal.get(Calendar.HOUR);
		int m=cal.get(Calendar.MINUTE);
		int s=cal.get(Calendar.SECOND);

		String date=day+"/"+month+"/"+year;
		return date;

	}

	public static boolean isEmulator() {
		return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
				|| Build.FINGERPRINT.startsWith("generic")
				|| Build.FINGERPRINT.startsWith("unknown")
				|| Build.HARDWARE.contains("goldfish")
				|| Build.HARDWARE.contains("ranchu")
				|| Build.MODEL.contains("google_sdk")
				|| Build.MODEL.contains("Emulator")
				|| Build.MODEL.contains("Android SDK built for x86")
				|| Build.MODEL.contains("sdk_gphone_x86_64")
				|| Build.MANUFACTURER.contains("Genymotion")
				|| Build.MANUFACTURER.contains("Google")
				|| Build.MANUFACTURER.contains("google")
				|| Build.PRODUCT.contains("sdk_google")
				|| Build.PRODUCT.contains("google_sdk")
				|| Build.PRODUCT.contains("sdk")
				|| Build.PRODUCT.contains("sdk_x86")
				|| Build.PRODUCT.contains("sdk_gphone64_arm64")
				|| Build.PRODUCT.contains("vbox86p")
				|| Build.PRODUCT.contains("emulator")
				|| Build.PRODUCT.contains("simulator");
	}


	//parse date to day/month/year
	public static String parseDate(String date)
	{
		StringTokenizer st=new StringTokenizer(date,"/");
		String month="",day="",year="";
		try {
			month = st.nextToken();
			day = st.nextToken();
			year = st.nextToken();
		}catch (Exception e){e.printStackTrace();}

		return day+"/"+month+"/"+year;
	}

	//check gps is avalable or not if not then get dialog
	public static void displayPromptForEnablingGPS(final AppCompatActivity activity)
	{

		final AlertDialog.Builder builder =  new AlertDialog.Builder(activity);
		final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
		final String message = "Do you want open GPS setting?";

		builder.setMessage(message)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface d, int id) {
								activity.startActivity(new Intent(action));
								d.dismiss();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface d, int id) {
								d.cancel();
								activity.finish();
							}
						});
		builder.create().show();
	}
	public static boolean isGPSEnabled (Context mContext){
		LocationManager locationManager = (LocationManager)
				mContext.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	public static boolean isfrontCameraAvalable(){
		int numCameras= Camera.getNumberOfCameras();
		for(int i=0;i<numCameras;i++){
			Camera.CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(i, info);
			if(Camera.CameraInfo.CAMERA_FACING_FRONT == info.facing){
				return true;
			}
		}
		return false;
	}

	//permission check
	public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static boolean checkPermission(final Context context)
	{
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if(currentAPIVersion>= Build.VERSION_CODES.M)
		{
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder.setMessage("External storage permission is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
						}
					});
					AlertDialog alert = alertBuilder.create();
					alert.show();
				} else {
					ActivityCompat.requestPermissions((AppCompatActivity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	//base64 to bitmap
	public static Bitmap StringToBitMap(String encodedString) {
		try {
			byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
			return bitmap;
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}

	//bitmap to byte array
	public static byte[] bitmaptoByte(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}

	//byte array to base64
	public static String BitArrayToString(byte[] b1) {
		byte[] b = b1;
		String temp = Base64.encodeToString(b, Base64.NO_WRAP);
		return temp;
	}

	public static byte[] stringToBytes(String s) {
		byte[] b2 = new BigInteger(s, 36).toByteArray();
		return Arrays.copyOfRange(b2, 1, b2.length);
	}

	//bitmap to base64
	public static String BitMapToString(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] b = baos.toByteArray();
		String temp = Base64.encodeToString(b, Base64.DEFAULT);
		return temp;
	}

	//get bitmap from url
	public static Bitmap getBitmapfromUrl(String imageUrl)
	{
		try
		{
			URL url = new URL(imageUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(input);
			return bitmap;

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}
	}

	//inflate small image icon on image
	public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
		Bitmap bmOverlay=null;
		try {
			bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
			Canvas canvas = new Canvas(bmOverlay);
			canvas.drawBitmap(bmp1, new Matrix(), null);
			Bitmap bitmap = Utiilties.GenerateThumbnail(bmp2, 60, 60);
			canvas.drawBitmap(bitmap, 0, 0, null);
		}catch (Exception ex){
			ex.printStackTrace();
			//Utiilties.writeIntoLog(Log.getStackTraceString(ex));
		}
		return bmOverlay;
	}

	/*//write log when exception
	public static void writeIntoLog(String data)
	{
		 final String SDCARD = String.valueOf(Environment
				.getExternalStorageDirectory());
		FileWriter fw = null;
		try {

			fw = new FileWriter(LOG_FILE_PATH , true);
			BufferedWriter buffer = new BufferedWriter(fw);
			buffer.append(data+"\n");
			buffer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}*/
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	private static PublicKey readKeyFromFile(AppCompatActivity activity) throws IOException {
		ObjectInputStream oin = null;
		try (InputStream in = getKeyPath(activity);) {
			oin = new ObjectInputStream(new BufferedInputStream(in));
			PublicKey pubKey = (PublicKey) oin.readObject();
			return pubKey;
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("Spurious serialisation error", e);
		} finally {
			oin.close();
		}
	}



	@TargetApi(Build.VERSION_CODES.KITKAT)
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public static byte[] rsaEncrypt(byte[] data, AppCompatActivity activity) {

		try {
			//getKeyPath();
			PublicKey pubKey = readKeyFromFile(activity);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] cipherData = cipher.doFinal(data);
			return cipherData;
		} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
			System.err.println(ex.getMessage());
		}
		return null;
	}

	private static InputStream getKeyPath(AppCompatActivity activity) {
		InputStream app;
		try {
			app = activity.getApplicationContext().getAssets().open("public.key");

		} catch (Exception ex) {
			System.out.println(ex);
			app = null;
		}
		return app;
	}

	public static int monthsBetweenDates(Date startDate, Date endDate){

		Calendar start = Calendar.getInstance();
		start.setTime(startDate);

		Calendar end = Calendar.getInstance();
		end.setTime(endDate);

		int monthsBetween = 0;
		int dateDiff = end.get(Calendar.DAY_OF_MONTH)-start.get(Calendar.DAY_OF_MONTH);

		if(dateDiff<0) {
			int borrrow = end.getActualMaximum(Calendar.DAY_OF_MONTH);
			dateDiff = (end.get(Calendar.DAY_OF_MONTH)+borrrow)-start.get(Calendar.DAY_OF_MONTH);
			monthsBetween--;

			if(dateDiff>0) {
				monthsBetween++;
			}
		}
		else {
			monthsBetween++;
		}
		monthsBetween += end.get(Calendar.MONTH)-start.get(Calendar.MONTH);
		monthsBetween  += (end.get(Calendar.YEAR)-start.get(Calendar.YEAR))*12;
		return monthsBetween;
	}
}
