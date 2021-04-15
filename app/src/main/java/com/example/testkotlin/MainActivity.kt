package com.example.testkotlin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var Site_Key: String = "6LfBVKoaAAAAAHINLfLKvM8qeHple4NwjTjWLBkt"
    private var Serect_Key: String = "6LfBVKoaAAAAAOIrSUVUM4QTR8wZFLKjimEVV8b3"

    //
    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient
    private var RC_CODE=1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            intent()
        }
        //
        var gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleClient= GoogleSignIn.getClient(this,gso)
        auth= FirebaseAuth.getInstance()
        googleButton.setOnClickListener{
            signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RC_CODE){
            val task: Task<GoogleSignInAccount> =GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }
    private fun handleResult(completeTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount?=completeTask.getResult(ApiException::class.java)
            if (account!=null){
                val credential=GoogleAuthProvider.getCredential(account.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener{task -> if (task.isSuccessful){
                    Toast.makeText(this,account.displayName.toString(),Toast.LENGTH_SHORT).show()
                }
                    else{
                    Toast.makeText(this,"fail",Toast.LENGTH_SHORT).show()
                }
                }
            }
        } catch (e:ApiException){

        }
    }

    private fun signIn(){
        val signInItent=googleClient.signInIntent
        startActivityForResult(signInItent,RC_CODE)
    }

    private fun recaptcha() {
        SafetyNet.getClient(this).verifyWithRecaptcha(Site_Key)
            .addOnSuccessListener { reponse ->
                val userResponseToken = reponse.tokenResult
                if (userResponseToken.isNotEmpty() == true) {
                    var url: String = "https://www.google.com/recaptcha/api/siteverify";
                }
            }
            .addOnFailureListener { e ->
                if (e is ApiException) {
                    Toast.makeText(this, e.statusCode.toString(), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Other", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun intent() {
        var name = editText.text.toString();
        var age = edtAge.text.toString();
        if (name.isEmpty() && age.isEmpty()) {
            name = "default value";
            age = "0";
        }
        var person: Person = Person(name = name, age = age.toInt());
        Toast.makeText(this, "name = ${person.name} age =${person.age}", Toast.LENGTH_SHORT).show();
        editText.setText("");
        edtAge.setText("");
        var intent: Intent = Intent(this, WebViewActivity::class.java)
        startActivity(intent)
    }

    private fun addNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "sub"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ID", "a", importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(this, "ID")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Notification example")
            .setContentText("Hello")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val manager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, builder.build())
    }
}



