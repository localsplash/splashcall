package com.relevantAds.splashcall


import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.linphone.core.*

class PushNotificationsActivity: AppCompatActivity() {
    private lateinit var core: Core

    private val coreListener = object: CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(core: Core, account: Account, state: RegistrationState?, message: String) {
            findViewById<TextView>(R.id.registration_status).text = message

            if (state == RegistrationState.Failed) {
                findViewById<Button>(R.id.connect).isEnabled = true
            } else if (state == RegistrationState.Ok) {
                findViewById<LinearLayout>(R.id.register_layout).visibility =
                        View.GONE

                // This will display the push information stored in the contact URI parameters
                findViewById<TextView>(R.id.push_info).text = account.params.contactUriParameters
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_push_notification)

        // For push notifications to work, you have to copy your google-services.json in the app/ folder
        // And you must declare our FirebaseMessaging service in the Manifest
        // You also have to make some changes in your build.gradle files, see the ones in this project

        val factory = Factory.instance()
        factory.setDebugMode(true, "Hello Linphone")
        core = factory.createCore(null, null, this)

        // Make sure the core is configured to use push notification token from firebase
        core.isPushNotificationEnabled = true

        findViewById<Button>(R.id.connect).setOnClickListener {
            login()
            it.isEnabled = false
        }
    }

    private fun login() {
        val username = findViewById<EditText>(R.id.username).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()
        val domain = findViewById<EditText>(R.id.domain).text.toString()
        val transportType = when (findViewById<RadioGroup>(R.id.transport).checkedRadioButtonId) {
            R.id.udp -> TransportType.Udp
            R.id.tcp -> TransportType.Tcp
            else -> TransportType.Tls
        }
        val authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, domain, null)

        val params = core.createAccountParams()
        val identity = Factory.instance().createAddress("sip:$username@$domain")
        params.identityAddress = identity

        val address = Factory.instance().createAddress("sip:$domain")
        address?.transport = transportType
        params.serverAddress = address
        params.registerEnabled = true

        // Ensure push notification is enabled for this account
        params.pushNotificationAllowed = true

        core.addAuthInfo(authInfo)
        val account = core.createAccount(params)
        core.addAccount(account)

        core.defaultAccount = account
        core.addListener(coreListener)
        core.start()

        if (!core.isPushNotificationAvailable) {
            Toast.makeText(this, "Something is wrong with the push setup!", Toast.LENGTH_LONG).show()
        }

        // And that's it!
        // You can kill this app and send a message or initiate a call to the identity you registered and you'll see the toast.

        // When a push notification will be received by your app, either:
        // - the Core is alive and it will check it is properly registered & connected to the proxy
        // - the Core isn't available and a broadcast on org.linphone.core.action.PUSH_RECEIVED will be fired

        // Another way is to create your own Application object and create the Core in it
        // This way, when a push will be received, the Core will be created before the push being handled
        // so the first case above will always be true. See our linphone-android app for an example of that.
    }
}
