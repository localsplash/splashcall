package com.relevantAds.splashcall


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import org.linphone.core.*

class OutgoingCall: AppCompatActivity() {
    private lateinit var core: Core
    private var dialedNumber: String = ""
    var iMemberID = 0
    var sipServerIP: String = ""
    var sipExtension: String = ""
    var secret: String = ""

    private val coreListener = object: CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(core: Core, account: Account, state: RegistrationState?, message: String) {

        }

        override fun onCallStateChanged(
                core: Core,
                call: Call,
                state: Call.State?,
                message: String
        ) {
            // This function will be called each time a call state changes,
            // which includes new incoming/outgoing calls

            when (state) {

                Call.State.OutgoingInit -> {
                    // First state an outgoing call will go through
                }
                Call.State.OutgoingProgress -> {
                    // Right after outgoing init
                    findViewById<AppCompatTextView>(R.id.call_status_text_view).text = "Connecting..."

                }
                Call.State.OutgoingRinging -> {
                    // This state will be reached upon reception of the 180 RINGING
                    findViewById<AppCompatTextView>(R.id.call_status_text_view).text = "Ringing..."
                }
                Call.State.Connected -> {
                    // When the 200 OK has been received
                    findViewById<AppCompatTextView>(R.id.call_status_text_view).text = "Connected"
                    Toast.makeText(applicationContext, "this is toast message", Toast.LENGTH_SHORT).show()

                }
                Call.State.StreamsRunning -> {
//
                    findViewById<AppCompatTextView>(R.id.call_status_text_view).text = "Connected"
                }
                Call.State.Paused -> {

                }
                Call.State.PausedByRemote -> {
                    // When the remote end of the call pauses it, it will be PausedByRemote
                }
                Call.State.Updating -> {
                    // When we request a call update, for example when toggling video
                }
                Call.State.UpdatedByRemote -> {
                    // When the remote requests a call update
                }
                Call.State.Released -> {
                    // Call state will be released shortly after the End state
                    finish()
                }
                Call.State.Error -> {

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.outgoing_call_layout)

        dialedNumber = intent.getStringExtra("number_to_make_a_call").toString()
        iMemberID = intent.getIntExtra("iMemberID",0)
        sipServerIP = intent.getStringExtra("sipServerIP").toString()
        sipExtension = intent.getStringExtra("sipExtension").toString()
        secret = intent.getStringExtra("secret").toString()


        val factory = Factory.instance()
        factory.setDebugMode(true, "Hello Linphone")
        core = factory.createCore(null, null, this)

        // But it allows to use it later
        core.enableVideoCapture(true)
        core.enableVideoDisplay(true)

        core.videoActivationPolicy.automaticallyAccept = true

//        }

        findViewById<AppCompatImageButton>(R.id.hang_up_call_button).setOnClickListener {
            hangUp()
        }
        findViewById<AppCompatTextView>(R.id.dialed_number_text_view).text = dialedNumber
        findViewById<AppCompatTextView>(R.id.call_status_text_view).text = "Connecting"

        login()
    }

    private fun login() {
        val username = sipExtension
        val password = secret
        val domain = sipServerIP+":5060"
        val transportType = TransportType.Tcp
        val authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, domain, null)

        val params = core.createAccountParams()
        val identity = Factory.instance().createAddress("sip:$username@$domain")
        params.identityAddress = identity

        val address = Factory.instance().createAddress("sip:$domain")
        address?.transport = transportType
        params.serverAddress = address
        params.registerEnabled = true
        val account = core.createAccount(params)

        core.addAuthInfo(authInfo)
        core.addAccount(account)

        // Asks the CaptureTextureView to resize to match the captured video's size ratio
        core.config.setBool("video", "auto_resize_preview_to_keep_ratio", true)

        core.defaultAccount = account
        core.addListener(coreListener)
        core.start()

        // We will need the RECORD_AUDIO permission for video call
        if (packageManager.checkPermission(Manifest.permission.RECORD_AUDIO, packageName) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)
            return
        }
        outgoingCall(dialedNumber)


    }

    private fun outgoingCall(number: String) {
        // As for everything we need to get the SIP URI of the remote and convert it to an Address
//        val remoteSipUri = findViewById<EditText>(R.id.remote_address).text.toString()
        val remoteSipUri = "sip:"+number+"@172.83.90.120:5060";
        val remoteAddress = Factory.instance().createAddress(remoteSipUri)
        remoteAddress ?: return // If address parsing fails, we can't continue with outgoing call process

        val params = core.createCallParams(null)
        params ?: return // Same for params

        // We can now configure it
        // Here we ask for no encryption but we could ask for ZRTP/SRTP/DTLS
        params.mediaEncryption = MediaEncryption.None
        // If we wanted to start the call with video directly
        //params.enableVideo(true)

        // Finally we start the call
        core.inviteAddressWithParams(remoteAddress, params)
        // Call process can be followed in onCallStateChanged callback from core listener
    }

    private fun hangUp() {
        if (core.callsNb == 0) finish()

        // If the call state isn't paused, we can get it using core.currentCall
        val call = if (core.currentCall != null) core.currentCall else core.calls[0]
        call ?: return

        // Terminating a call is quite simple
        call.terminate()
        finish()
    }

    private fun toggleVideo() {
        if (core.callsNb == 0) return
        val call = if (core.currentCall != null) core.currentCall else core.calls[0]
        call ?: return

        // We will need the CAMERA permission for video call
        if (packageManager.checkPermission(Manifest.permission.CAMERA, packageName) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
            return
        }

        // To update the call, we need to create a new call params, from the call object this time
        val params = core.createCallParams(call)
        // Here we toggle the video state (disable it if enabled, enable it if disabled)
        // Note that we are using currentParams and not params or remoteParams
        // params is the object you configured when the call was started
        // remote params is the same but for the remote
        // current params is the real params of the call, resulting of the mix of local & remote params
        params?.enableVideo(!call.currentParams.videoEnabled())
        // Finally we request the call update
        call.update(params)

        // Note that when toggling off the video, TextureViews will keep showing the latest frame displayed
    }




}
