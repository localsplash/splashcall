package com.relevantAds.splashcall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.linphone.core.Account;
import org.linphone.core.AccountParams;
import org.linphone.core.Address;
import org.linphone.core.AudioDevice;
import org.linphone.core.AuthInfo;
import org.linphone.core.AuthMethod;
import org.linphone.core.Call;
import org.linphone.core.CallLog;
import org.linphone.core.CallStats;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatRoom;
import org.linphone.core.Conference;
import org.linphone.core.ConfiguringState;
import org.linphone.core.Content;
import org.linphone.core.Core;
import org.linphone.core.CoreListener;
import org.linphone.core.EcCalibratorStatus;
import org.linphone.core.Event;
import org.linphone.core.Factory;
import org.linphone.core.Friend;
import org.linphone.core.FriendList;
import org.linphone.core.GlobalState;
import org.linphone.core.InfoMessage;
import org.linphone.core.PresenceModel;
import org.linphone.core.ProxyConfig;
import org.linphone.core.PublishState;
import org.linphone.core.RegistrationState;
import org.linphone.core.SubscriptionState;
import org.linphone.core.TransportType;
import org.linphone.core.Transports;
import org.linphone.core.VersionUpdateCheckResult;

public class DialPad extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    public int iMemberID;
    public String sipServerIP;
    public String sipExtension;
    public String secret;
    public Core core;
    public TransportType transportType;
    public AppCompatButton buttonStar;
    public AppCompatButton buttonHash;
    public AppCompatButton buttonZero;
    public AppCompatButton buttonOne;
    public AppCompatButton buttonTwo;
    public AppCompatButton buttonThree;
    public AppCompatButton buttonFour;
    public AppCompatButton buttonFive;
    public AppCompatButton buttonSix;
    public AppCompatButton buttonSeven;
    public AppCompatButton buttonEight;
    public AppCompatButton buttonNine;

    public EditText typedPhoneNumber;
    public AppCompatImageButton backSpaceButton;

    private Vibrator mVibrator;
    private static final int DURATION = 50; // Vibrate duration


    public AppCompatImageButton makeAPhoneCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial_pad);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //find all related views
        buttonStar = findViewById(R.id.dial_pad_button_star);
        buttonHash = findViewById(R.id.dial_pad_button_hash);
        buttonZero = findViewById(R.id.dial_pad_button_zero);
        buttonOne = findViewById(R.id.dial_pad_button_one);
        buttonTwo = findViewById(R.id.dial_pad_button_two);
        buttonThree = findViewById(R.id.dial_pad_button_three);
        buttonFour = findViewById(R.id.dial_pad_button_four);
        buttonFive = findViewById(R.id.dial_pad_button_five);
        buttonSix = findViewById(R.id.dial_pad_button_six);
        buttonSeven = findViewById(R.id.dial_pad_button_seven);
        buttonEight = findViewById(R.id.dial_pad_button_eight);
        buttonNine = findViewById(R.id.dial_pad_button_nine);
        typedPhoneNumber = findViewById(R.id.typed_mobile_number_editText);
        backSpaceButton = findViewById(R.id.back_space_button);
        makeAPhoneCall = findViewById(R.id.dial_pad_button_make_a_call);


//        typedPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());


        // set click listeners.
        buttonStar.setOnClickListener(this);
        buttonHash.setOnClickListener(this);
        buttonZero.setOnClickListener(this);
        buttonZero.setOnLongClickListener(this);
        buttonOne.setOnClickListener(this);
        buttonTwo.setOnClickListener(this);
        buttonThree.setOnClickListener(this);
        buttonFour.setOnClickListener(this);
        buttonFive.setOnClickListener(this);
        buttonSix.setOnClickListener(this);
        buttonSeven.setOnClickListener(this);
        buttonEight.setOnClickListener(this);
        buttonNine.setOnClickListener(this);
        makeAPhoneCall.setOnClickListener(this);

        backSpaceButton.setOnClickListener(this);
        backSpaceButton.setOnLongClickListener(this);

        Factory factory = Factory.instance();
        factory.setDebugMode(true, "Hello Linphone");
        core = factory.createCore(null,null,DialPad.this);



        makeAPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typedPhoneNumber.getText().toString().length() == 0){
                    return;
                }
                String numberToMakeACall = typedPhoneNumber.getText().toString();
                Intent outGoingCallActivity = new Intent(DialPad.this,OutgoingCall.class);
                outGoingCallActivity.putExtra("number_to_make_a_call",numberToMakeACall);
                outGoingCallActivity.putExtra("iMemberID",iMemberID);
                outGoingCallActivity.putExtra("sipServerIP",sipServerIP);
                outGoingCallActivity.putExtra("sipExtension",sipExtension);
                outGoingCallActivity.putExtra("secret",secret);

                startActivity(outGoingCallActivity);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            iMemberID = extras.getInt("iMemberID");
            sipServerIP = extras.getString("sipServerIP");
            sipExtension = extras.getString("sipExtension");
            secret = extras.getString("secret");


            Log.d("iMemberID",iMemberID+"");
            Log.d("sipServerIP",sipServerIP+"");
            Log.d("sipExtension",sipExtension+"");
            Log.d("secret",secret+"");


            loginToSIP();

        }

    }
    public void loginToSIP(){

        AuthInfo authInfo = Factory.instance().createAuthInfo(sipExtension,null,secret,null,null,sipServerIP);
        AccountParams accountParams = core.createAccountParams();
        Address identity = Factory.instance().createAddress("sip:"+sipExtension+"@"+sipServerIP);
        accountParams.setIdentityAddress(identity);

        Address address = Factory.instance().createAddress("sip:"+sipServerIP);
        address.setPort(5060);
        address.setTransport(TransportType.Tcp);
        accountParams.setRegisterEnabled(true);
        accountParams.setServerAddress(address);

        Account account = core.createAccount(accountParams);
        core.addAuthInfo(authInfo);
        core.addAccount(account);

        core.setDefaultAccount(account);

        core.addListener(new CoreListener() {
            @Override
            public void onNetworkReachable(@NonNull Core core, boolean reachable) {

            }

            @Override
            public void onTransferStateChanged(@NonNull Core core, @NonNull Call transfered, Call.State callState) {

            }

            @Override
            public void onAudioDevicesListUpdated(@NonNull Core core) {

            }

            @Override
            public void onConferenceStateChanged(@NonNull Core core, @NonNull Conference conference, Conference.State state) {

            }

            @Override
            public void onInfoReceived(@NonNull Core core, @NonNull Call call, @NonNull InfoMessage message) {

            }

            @Override
            public void onCallStateChanged(@NonNull Core core, @NonNull Call call, Call.State state, @NonNull String message) {

            }

            @Override
            public void onImeeUserRegistration(@NonNull Core core, boolean status, @NonNull String userId, @NonNull String info) {
                Log.d("message",info);

            }

            @Override
            public void onNotifyReceived(@NonNull Core core, @NonNull Event linphoneEvent, @NonNull String notifiedEvent, @NonNull Content body) {

            }

            @Override
            public void onNewSubscriptionRequested(@NonNull Core core, @NonNull Friend linphoneFriend, @NonNull String url) {

            }

            @Override
            public void onCallLogUpdated(@NonNull Core core, @NonNull CallLog callLog) {

            }

            @Override
            public void onFirstCallStarted(@NonNull Core core) {

            }

            @Override
            public void onChatRoomEphemeralMessageDeleted(@NonNull Core core, @NonNull ChatRoom chatRoom) {

            }

            @Override
            public void onLogCollectionUploadProgressIndication(@NonNull Core core, int offset, int total) {

            }

            @Override
            public void onNotifyPresenceReceivedForUriOrTel(@NonNull Core core, @NonNull Friend linphoneFriend, @NonNull String uriOrTel, @NonNull PresenceModel presenceModel) {

            }

            @Override
            public void onIsComposingReceived(@NonNull Core core, @NonNull ChatRoom chatRoom) {

            }

            @Override
            public void onFriendListCreated(@NonNull Core core, @NonNull FriendList friendList) {

            }

            @Override
            public void onQrcodeFound(@NonNull Core core, @Nullable String result) {

            }

            @Override
            public void onSubscribeReceived(@NonNull Core core, @NonNull Event linphoneEvent, @NonNull String subscribeEvent, @NonNull Content body) {

            }

            @Override
            public void onDtmfReceived(@NonNull Core core, @NonNull Call call, int dtmf) {

            }

            @Override
            public void onReferReceived(@NonNull Core core, @NonNull String referTo) {

            }

            @Override
            public void onAudioDeviceChanged(@NonNull Core core, @NonNull AudioDevice audioDevice) {

            }

            @Override
            public void onAuthenticationRequested(@NonNull Core core, @NonNull AuthInfo authInfo, @NonNull AuthMethod method) {

            }

            @Override
            public void onPublishStateChanged(@NonNull Core core, @NonNull Event linphoneEvent, PublishState state) {

            }

            @Override
            public void onGlobalStateChanged(@NonNull Core core, GlobalState state, @NonNull String message) {

            }

            @Override
            public void onCallStatsUpdated(@NonNull Core core, @NonNull Call call, @NonNull CallStats callStats) {

            }

            @Override
            public void onMessageReceivedUnableDecrypt(@NonNull Core core, @NonNull ChatRoom chatRoom, @NonNull ChatMessage message) {

            }

            @Override
            public void onMessageReceived(@NonNull Core core, @NonNull ChatRoom chatRoom, @NonNull ChatMessage message) {

            }

            @Override
            public void onCallCreated(@NonNull Core core, @NonNull Call call) {

            }

            @Override
            public void onBuddyInfoUpdated(@NonNull Core core, @NonNull Friend linphoneFriend) {

            }

            @Override
            public void onSubscriptionStateChanged(@NonNull Core core, @NonNull Event linphoneEvent, SubscriptionState state) {

            }

            @Override
            public void onChatRoomSubjectChanged(@NonNull Core core, @NonNull ChatRoom chatRoom) {

            }

            @Override
            public void onCallEncryptionChanged(@NonNull Core core, @NonNull Call call, boolean mediaEncryptionEnabled, @Nullable String authenticationToken) {

            }

            @Override
            public void onEcCalibrationAudioUninit(@NonNull Core core) {

            }

            @Override
            public void onLastCallEnded(@NonNull Core core) {

            }

            @Override
            public void onConfiguringStatus(@NonNull Core core, ConfiguringState status, @Nullable String message) {

            }

            @Override
            public void onRegistrationStateChanged(@NonNull Core core, @NonNull ProxyConfig proxyConfig, RegistrationState state, @NonNull String message) {
                Log.d("state_changed","[Account] Registration state changed:"+state+message);

            }

            @Override
            public void onLogCollectionUploadStateChanged(@NonNull Core core, Core.LogCollectionUploadState state, @NonNull String info) {

            }

            @Override
            public void onEcCalibrationAudioInit(@NonNull Core core) {

            }

            @Override
            public void onNotifyPresenceReceived(@NonNull Core core, @NonNull Friend linphoneFriend) {

            }

            @Override
            public void onVersionUpdateCheckResultReceived(@NonNull Core core, @NonNull VersionUpdateCheckResult result, String version, @Nullable String url) {

            }

            @Override
            public void onEcCalibrationResult(@NonNull Core core, EcCalibratorStatus status, int delayMs) {

            }

            @Override
            public void onChatRoomStateChanged(@NonNull Core core, @NonNull ChatRoom chatRoom, ChatRoom.State state) {

            }

            @Override
            public void onCallIdUpdated(@NonNull Core core, @NonNull String previousCallId, @NonNull String currentCallId) {

            }

            @Override
            public void onChatRoomRead(@NonNull Core core, @NonNull ChatRoom chatRoom) {

            }

            @Override
            public void onAccountRegistrationStateChanged(@NonNull Core core, @NonNull Account account, RegistrationState state, @NonNull String message) {
                Log.d("message",message);
            }

            @Override
            public void onMessageSent(@NonNull Core core, @NonNull ChatRoom chatRoom, @NonNull ChatMessage message) {

            }

            @Override
            public void onFriendListRemoved(@NonNull Core core, @NonNull FriendList friendList) {

            }
        });
        core.start();

    }
    private void keyPressed(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume_level= am.getStreamVolume(AudioManager.STREAM_RING); // Highest Ring volume level is 7, lowest is 0
        final ToneGenerator mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume_level * 14); // Raising volume to 100% (For eg. 7 * 14 ~ 100)
        mToneGenerator.stopTone();
        mToneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 50);
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        typedPhoneNumber.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.dial_pad_button_zero:
                keyPressed(KeyEvent.KEYCODE_0);
                return;
            case R.id.dial_pad_button_star:
                keyPressed(KeyEvent.KEYCODE_STAR);
                return;
            case R.id.dial_pad_button_hash:
                keyPressed(KeyEvent.KEYCODE_POUND);
                return;
            case R.id.dial_pad_button_one:
                keyPressed(KeyEvent.KEYCODE_1);
                return;
            case R.id.dial_pad_button_two:
                keyPressed(KeyEvent.KEYCODE_2);
                return;
            case R.id.dial_pad_button_three:
                keyPressed(KeyEvent.KEYCODE_3);
                return;
            case R.id.dial_pad_button_four:
                keyPressed(KeyEvent.KEYCODE_4);
                return;
            case R.id.dial_pad_button_five:
                keyPressed(KeyEvent.KEYCODE_5);
                return;
            case R.id.dial_pad_button_six:
                keyPressed(KeyEvent.KEYCODE_6);
                return;
            case R.id.dial_pad_button_seven:
                keyPressed(KeyEvent.KEYCODE_7);
                return;
            case R.id.dial_pad_button_eight:
                keyPressed(KeyEvent.KEYCODE_8);
                return;
            case R.id.dial_pad_button_nine:
                keyPressed(KeyEvent.KEYCODE_9);
                return;
            case R.id.back_space_button:
                keyPressed(KeyEvent.KEYCODE_DEL);
                return;


        }

    }

    @Override
    public boolean onLongClick(View view) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume_level= am.getStreamVolume(AudioManager.STREAM_RING); // Highest Ring volume level is 7, lowest is 0
        final ToneGenerator mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume_level * 14); // Raising volume to 100% (For eg. 7 * 14 ~ 100)
        mToneGenerator.stopTone();
        mToneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 100);
        switch (view.getId()){
            case R.id.back_space_button:
                Editable digits = typedPhoneNumber.getText();
                digits.clear();
                return true;
            case R.id.dial_pad_button_zero: {
                keyPressed(KeyEvent.KEYCODE_PLUS);
                return true;
            }
        }

        return false;
    }
}