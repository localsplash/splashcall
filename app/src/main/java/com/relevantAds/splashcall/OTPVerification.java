package com.relevantAds.splashcall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.relevantAds.splashcall.APIManager.ApiClient;
import com.relevantAds.splashcall.APIManager.ApiInterface;
import com.relevantAds.splashcall.Others.DeviceData;
import com.relevantAds.splashcall.views.InfoDialogFragment;
import com.relevantAds.splashcall.views.LoadingProgressDialogue;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPVerification extends AppCompatActivity {

    public String uuid;
    public long mobile_number;
    private ApiInterface apiService;
    private Call<ResponseBody> call;
    public LoadingProgressDialogue loadingProgressDialogue;
    private SharedPreferences deviceDataPrefs;


    public EditText otpEditText;
    public TextView retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_verification);

        Toolbar toolbar = findViewById(R.id.toolbar_otp_verification);
        setSupportActionBar(toolbar);
        getSupportActionBar()/* or getSupportActionBar() */.setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "Enter OTP" + "</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deviceDataPrefs = getSharedPreferences(DeviceData.DEVICE_DATA_PREFERENCES, Context.MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            uuid = extras.getString("uuid");
            mobile_number = extras.getLong("mobile_number");
        }

        otpEditText = findViewById(R.id.otp_edit_text);
        otpEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 6){
                    verifyOTP(editable.toString());
                }
            }
        });

        retryButton = findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOTP();
            }
        });
        loadingProgressDialogue = new LoadingProgressDialogue();
    }

    public void resendOTP(){
        apiService =
                ApiClient.getClient(35).create(ApiInterface.class);
        loadingProgressDialogue.showDialog(OTPVerification.this);
        call = apiService.otpSmsRequest(uuid,mobile_number);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadingProgressDialogue.dismissDialogue(OTPVerification.this);
                if (response.isSuccessful()){
                    Log.d("response","success");
                    switch (response.code()){
                        case 200:
                            showInfo("Success","OTP Generated Successfully.");
                            break;
                    }
                    try {
                        JSONObject responseObj = new JSONObject(response.body().string());
                        Log.d("response",responseObj.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    switch (response.code()){
                        case 400:
                            showInfo("Attempts Limit Reached.","Too many requests, please wait a few minutes before retrying.");
                            break;
                        case 500:
                            showInfo("Error","Device Not Recognized.");
                            break;

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showInfo("Something Went Wrong","Please try again later.");
            }
        });

    }
    public void verifyOTP(String otpCode){
        apiService =
                ApiClient.getClient(35).create(ApiInterface.class);
        loadingProgressDialogue.showDialog(OTPVerification.this);
        long code = Long.parseLong(otpCode);
        call = apiService.otpSmsResponse(uuid,code);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    if (response.code() == 200){
                        // Device Approved OTP is valid
                        // call Login API Now
                        SharedPreferences.Editor e = deviceDataPrefs.edit();
                        e.putLong(DeviceData.LOCAL_PHONE_NUMBER,mobile_number);
                        e.apply();
                        sendLoginRequest();


                    }
                }
                else{
                    loadingProgressDialogue.dismissDialogue(OTPVerification.this);
                    switch (response.code()){

                        case 301:
                            showInfo("Verification Failed","You have entered an old OTP code, please enter the latest code you have received.");
                            otpEditText.setText("");
                            break;
                        case 501:
                            showInfo("Verification Failed","OTP you have entered is invalid, please enter a valid OTP code.");
                            otpEditText.setText("");
                            break;
                        case 502:
                            showInfo("Something Went Wrong","Could not validate your current device.");
                            otpEditText.setText("");
                            break;

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showInfo("Something Went Wrong","Please try again later.");

            }
        });
    }
    public void sendLoginRequest() {
        apiService =
                ApiClient.getClient(35).create(ApiInterface.class);


        call = apiService.deviceLoginEndPoint(uuid,mobile_number);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadingProgressDialogue.dismissDialogue(OTPVerification.this);
                if (response.isSuccessful()){
                    Log.d("response","success");
                    if (response.code() ==  200){
                        try {
                            JSONObject responseObj = new JSONObject(response.body().string());
                            Log.d("response",responseObj.toString());


                            JSONObject sipObject = responseObj.getJSONObject("sipSettings");
                            int iMemberID = sipObject.getInt("iMemberID");
                            String sipServerIP = sipObject.getString("sipServerIP");
                            String sipExtension = sipObject.getString("sipExtension");
                            String secret = sipObject.getString("secret");
                            Intent dialPad = new Intent(OTPVerification.this,DialPad.class);
                            dialPad.putExtra("iMemberID",iMemberID);
                            dialPad.putExtra("sipServerIP",sipServerIP);
                            dialPad.putExtra("sipExtension",sipExtension);
                            dialPad.putExtra("secret",secret);
                            startActivity(dialPad);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    int statusCode = response.code();
                    Log.d("statusCode",statusCode+"");
                    switch (statusCode){
                        case 500:
                            // Call OTPSMSRequest
                            break;
                        case 200:
                            //SMS Request Sent
                            break;
                        case 400:
                            // To many attempts. try later.
                            showInfo("Attempts Limit Reached.","Too many requests, please wait a few minutes before retrying.");
                            break;

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showInfo("Something Went Wrong","Please try again later.");

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home){
            onBackPressed();
            return true;
        }
        return false;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
    public void showInfo(String title,String message)
    {
        InfoDialogFragment infoDialogFragment = InfoDialogFragment.newInstance(title, message);
        infoDialogFragment.setOnInfoClickListener(new InfoDialogFragment.InfoDialogListener() {
            @Override
            public void onInfoDialogOkClick() {

            }
        });
        infoDialogFragment.show(getSupportFragmentManager(), "information");
    }
}