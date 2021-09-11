package com.relevantAds.splashcall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

public class DialPad extends AppCompatActivity {

    public int iMemberID;
    public String sipServerIP;
    public String sipExtension;
    public String secret;
    public Core core;
    public TransportType transportType;

    public TextView mobileNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial_pad);

        Factory factory = Factory.instance();
        factory.setDebugMode(true, "Hello Linphone");
        core = factory.createCore(null,null,DialPad.this);


        mobileNumberTextView = findViewById(R.id.mobile_number_text_view);

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
}