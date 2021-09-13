package com.relevantAds.splashcall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRouter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nikhilpanju.recyclerviewenhanced.OnActivityTouchListener;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.relevantAds.splashcall.APIManager.ApiClient;
import com.relevantAds.splashcall.APIManager.ApiInterface;
import com.relevantAds.splashcall.Adapter.MobileNumbersListAdapter;
import com.relevantAds.splashcall.Database.DatabaseHelper;
import com.relevantAds.splashcall.Database.Model.PhoneNumber;
import com.relevantAds.splashcall.Others.DeviceData;
import com.relevantAds.splashcall.views.InfoDialogFragment;
import com.relevantAds.splashcall.views.LoadingProgressDialogue;
import com.relevantAds.splashcall.views.MyDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecyclerTouchListener.RecyclerTouchListenerHelper, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    public ArrayList<PhoneNumber> mobileNumbersList = new ArrayList<>();
    public MobileNumbersListAdapter mobileNumbersListAdapter;
    public Button noneOfTheAbove;
    public RecyclerView mobileNumbersRV;
    LinearLayoutManager linearLayoutManager;
    private RecyclerTouchListener onTouchListener;
    private OnActivityTouchListener touchListener;
    MyDividerItemDecoration myDividerItemDecoration;
    String enteredMobileNumber = "";
    private LinearLayout errorLayout;
    public TextView errorTopText;
    public TextView errorBottomText;
    private ApiInterface apiService;
    private Call<ResponseBody> call;
    public LoadingProgressDialogue loadingProgressDialogue;
    String generatedUUID;
    private DatabaseHelper db;
    private SharedPreferences deviceDataPrefs;
    public static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mCredentialsApiClient;
    private static final int RC_HINT = 1000;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

        mCredentialsApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
        requestPhoneNumber();


                Toolbar toolbar = findViewById(R.id.toolbar_launch_screen);
                setSupportActionBar(toolbar);
                getSupportActionBar()/* or getSupportActionBar() */.setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "Splash Call" + "</font>"));

                db = new DatabaseHelper(MainActivity.this);
                deviceDataPrefs = getSharedPreferences(DeviceData.DEVICE_DATA_PREFERENCES,Context.MODE_PRIVATE);
                generatedUUID = deviceDataPrefs.getString(DeviceData.DEVICE_UUID,"");
                Log.d("generate_UID",generatedUUID);

                noneOfTheAbove = findViewById(R.id.none_of_the_above_button);
                noneOfTheAbove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent addCustomNumber = new Intent(MainActivity.this,AddNewNumber.class);

                        startActivityForResult(addCustomNumber,1);
                    }
                });
                mobileNumbersRV = findViewById(R.id.mobile_numbers_rv);

                // setup recyclerView
                myDividerItemDecoration = new MyDividerItemDecoration(MainActivity.this,LinearLayoutManager.VERTICAL,20);
                mobileNumbersRV.addItemDecoration(myDividerItemDecoration);
                mobileNumbersRV.setHasFixedSize(true);
                mobileNumbersRV.setNestedScrollingEnabled(true);
                linearLayoutManager = new LinearLayoutManager(this);
                mobileNumbersRV.setLayoutManager(linearLayoutManager);
                onTouchListener = new RecyclerTouchListener(MainActivity.this, mobileNumbersRV);
                onTouchListener
                        .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                            /**
                             * @param position
                             * Method onRowClicked is responsible for the independent row clicked in recycler view
                             * while the position dictates the number of the row clicked
                             */
                            @Override
                            public void onRowClicked(int position) {
                                sendLoginRequest(mobileNumbersList.get(position).getAddedPhoneNumber(),generatedUUID);
                            }

                            /**
                             * @param independentViewID
                             * @param position
                             * Independent view represents any button in the list which we want to act as separate click event.
                             *
                             */
                            @Override
                            public void onIndependentViewClicked(int independentViewID, int position) {

                            }
                        });
                errorLayout = findViewById(R.id.layout_error);
                errorTopText = findViewById(R.id.error_top_text);
                errorBottomText = findViewById(R.id.error_lower_tax);
                loadingProgressDialogue = new LoadingProgressDialogue();

                 mobileNumbersListAdapter = new MobileNumbersListAdapter(MainActivity.this,mobileNumbersList);
                 mobileNumbersRV.setAdapter(mobileNumbersListAdapter);

                fetchMobileNumbers();
    }
    public void fetchMobileNumbers(){
        mobileNumbersList.clear();

        mobileNumbersList.addAll(db.getAllNumbers());

        if (mobileNumbersList.size() == 0){
            displayEmpty("","");
        }else{
            displayMobileNumbers();
        }


    }

    public void sendLoginRequest(String mobileNumber, String uuid) {
        loadingProgressDialogue.showDialog(MainActivity.this);
        apiService =
                ApiClient.getClient(35).create(ApiInterface.class);

        Long number = Long.parseLong(mobileNumber);
        call = apiService.deviceLoginEndPoint(uuid,number);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("response",response.code()+"");
                if (response.isSuccessful()){
                    Log.d("response","success");

                    try {
                        switch (response.code()){
                            case 202:
                                generateOTPSMSRequest(mobileNumber,uuid);
                                break;
                            case 200:
                                SharedPreferences.Editor e = deviceDataPrefs.edit();
                                e.putLong(DeviceData.LOCAL_PHONE_NUMBER,number);
                                e.apply();


                                // Send SIP Settings received to the Dial Pad Screen
                                JSONObject responseObj = new JSONObject(response.body().string());
                                Log.d("response",responseObj.toString());
                                JSONObject sipObject = responseObj.getJSONObject("sipSettings");
                                int iMemberID = sipObject.getInt("iMemberID");
                                String sipServerIP = sipObject.getString("sipServerIP");
                                String sipExtension = sipObject.getString("sipExtension");
                                String secret = sipObject.getString("secret");
                                Intent dialPad = new Intent(MainActivity.this,DialPad.class);
                                dialPad.putExtra("iMemberID",iMemberID);
                                dialPad.putExtra("sipServerIP",sipServerIP);
                                dialPad.putExtra("sipExtension",sipExtension);
                                dialPad.putExtra("secret",secret);
                                startActivity(dialPad);
                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    int statusCode = response.code();
                    switch (statusCode){
                        case 500:
                            // Call OTPSMSRequest
                            generateOTPSMSRequest(mobileNumber,uuid);
                            break;
                        case 202:
                            generateOTPSMSRequest(mobileNumber,uuid);
                        case 200:
                            //SMS Request Sent
                            break;
                        case 400:
                            // To many attempts. try later.
                            loadingProgressDialogue.dismissDialogue(MainActivity.this);
                            showInfo("Attempts Limit Reached.","Too many requests, please wait a few minutes before retrying.");
                            break;
                        case 402:
                            loadingProgressDialogue.dismissDialogue(MainActivity.this);
                            showInfo("Payment Required.","Your account has been suspended due to non payment.");
                            break;
                        case 404:
                            loadingProgressDialogue.dismissDialogue(MainActivity.this);
                            showInfo("Device Not Found.","This device does not exist and the phone number is invalid.");
                            break;

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingProgressDialogue.dismissDialogue(MainActivity.this);
                showInfo("Something Went Wrong","Please try again later.");

            }
        });

    }

    public void generateOTPSMSRequest(String mobileNumber, String uuid){
        apiService =
                ApiClient.getClient(35).create(ApiInterface.class);

        Long number = Long.parseLong(mobileNumber);
        call = apiService.otpSmsRequest(uuid,number);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadingProgressDialogue.dismissDialogue(MainActivity.this);
                if (response.isSuccessful()){
                    Log.d("response","success");
                    switch (response.code()){
                        case 200:
                            // Navigate to VerifyOTP Screen
                            Intent otpVerificationActivity = new Intent(MainActivity.this,OTPVerification.class);
                            otpVerificationActivity.putExtra("uuid",generatedUUID);
                            otpVerificationActivity.putExtra("mobile_number",number);
                            startActivity(otpVerificationActivity);
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
                loadingProgressDialogue.dismissDialogue(MainActivity.this);
                showInfo("Something Went Wrong","Please try again later.");
            }
        });
    }
    private void showHint() {
//        ui.clearKeyboard();
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent =
                Auth.CredentialsApi.getHintPickerIntent(mCredentialsApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RC_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Could not start hint picker Intent", e);
        }
    }

    public void displayEmpty(String title, String message){
        errorTopText.setText(title);
        errorBottomText.setText(message);
        errorLayout.setVisibility(View.VISIBLE);
        mobileNumbersRV.setVisibility(View.GONE);
    }
    public void displayMobileNumbers(){
        mobileNumbersListAdapter = new MobileNumbersListAdapter(MainActivity.this,mobileNumbersList);
        mobileNumbersRV.setAdapter(mobileNumbersListAdapter);
        errorLayout.setVisibility(View.GONE);
        mobileNumbersRV.setVisibility(View.VISIBLE);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
    @Override
    public void setOnActivityTouchListener(OnActivityTouchListener listener) {
        this.touchListener = listener;
    }
    /**
     * On Resume
     * when fragment reappeared from onPause state
     */
    @Override
    public void onResume() {
        super.onResume();
        mobileNumbersRV.addOnItemTouchListener(onTouchListener);
    }


    /**
     * Method OnPause()
     * Responsible for the action being done when fragment is in pause state
     * currently we are removing the recycler view touch listener in this method
     */
    @Override
    public void onPause() {
        super.onPause();
        mobileNumbersRV.removeOnItemTouchListener(onTouchListener);
    }
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if(resultCode == RESULT_OK) {
//                enteredMobileNumber = data.getStringExtra("added_number");
//                db.insertPhoneNumber(enteredMobileNumber);
//                fetchMobileNumbers();
//            }
//        }
//    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "GoogleApiClient is suspended with cause code: " + cause);
        displayEmpty("Couldn't Fetch Mobile Number","You can add mobile numbers here by clicking the button below.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient failed to connect: " + connectionResult);
        displayEmpty("Couldn't Fetch Mobile Number","You can add mobile numbers here by clicking the button below.");

    }
    public void requestPhoneNumber() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(mCredentialsApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), 10, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Could not start hint picker Intent", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (cred.getId().length() > 0){
                    sendLoginRequest(cred.getId(),generatedUUID);
                }
            }
            else{
                displayEmpty("Couldn't Fetch Mobile Number","You can add mobile numbers here by clicking the button below.");

            }

        }
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                enteredMobileNumber = data.getStringExtra("added_number");
                db.insertPhoneNumber(enteredMobileNumber);
                fetchMobileNumbers();
            }
        }
    }
}