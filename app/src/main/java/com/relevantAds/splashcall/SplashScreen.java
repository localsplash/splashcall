package com.relevantAds.splashcall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.relevantAds.splashcall.APIManager.ApiClient;
import com.relevantAds.splashcall.APIManager.ApiInterface;
import com.relevantAds.splashcall.Others.DeviceData;
import com.relevantAds.splashcall.views.LoadingProgressDialogue;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.Core;
import org.linphone.core.Factory;

import java.io.IOException;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    public String generatedUUID;
    private SharedPreferences deviceDataPrefs;
    public long localPhoneNumber;
    private ApiInterface apiService;
    private Call<ResponseBody> call;
    public LoadingProgressDialogue loadingProgressDialogue;
    public Core core;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        Factory factory = Factory.instance();
        factory.setDebugMode(true, "Hello Linphone");
        core = factory.createCore(null,null,SplashScreen.this);
        core.start();

        deviceDataPrefs = getSharedPreferences(DeviceData.DEVICE_DATA_PREFERENCES, Context.MODE_PRIVATE);
        generatedUUID = generateUUID(SplashScreen.this);


        loadingProgressDialogue = new LoadingProgressDialogue();

        /**
         * Check if a phone number is saved in Shared Preferences.
         */
        localPhoneNumber = deviceDataPrefs.getLong(DeviceData.LOCAL_PHONE_NUMBER,0);
        if (localPhoneNumber == 0){
            // localPhoneNumber is not saved. Navigate to Initial setup screen.
            goToInitialSetupScreen();
        }
        else{
            // localPhoneNumber does have a value. Execute Device Login API.
            sendLoginRequest(generatedUUID,localPhoneNumber);
        }

    }

    public void goToInitialSetupScreen(){
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                // after branding of the app check the login session and display screens accordingly

                // User logged out go to Welcome Screen
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public void sendLoginRequest(String generatedUUID,long localPhoneNumber){
        apiService =
                ApiClient.getClient(35).create(ApiInterface.class);

        call = apiService.deviceLoginEndPoint(generatedUUID,localPhoneNumber);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()){
                    Log.d("response","success");
                    try {

                        if (response.code() ==  200){
                            JSONObject responseObj = new JSONObject(response.body().string());
                            JSONObject sipObject = responseObj.getJSONObject("sipSettings");
                            int iMemberID = sipObject.getInt("iMemberID");
                            String sipServerIP = sipObject.getString("sipServerIP");
                            String sipExtension = sipObject.getString("sipExtension");
                            String secret = sipObject.getString("secret");
                            Intent dialPad = new Intent(SplashScreen.this,DialPad.class);
                            dialPad.putExtra("iMemberID",iMemberID);
                            dialPad.putExtra("sipServerIP",sipServerIP);
                            dialPad.putExtra("sipExtension",sipExtension);
                            dialPad.putExtra("secret",secret);
                            startActivity(dialPad);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Intent mainActivityIntent = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(mainActivityIntent);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Intent mainActivityIntent = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(mainActivityIntent);
            }
        });
    }

    /**
     * Check if the UUID is already generated.
     * If not generate a UUID and return back
     * @param context
     * @return
     */
    public  String generateUUID(Context context) {
        String fetchedUUID = deviceDataPrefs.getString(DeviceData.DEVICE_UUID,"");
        if (fetchedUUID.length() == 0){
            fetchedUUID = UUID.randomUUID().toString();
            SharedPreferences.Editor e = deviceDataPrefs.edit();
            e.putString(DeviceData.DEVICE_UUID,fetchedUUID);
            e.apply();
        }

        return fetchedUUID;
    }
}