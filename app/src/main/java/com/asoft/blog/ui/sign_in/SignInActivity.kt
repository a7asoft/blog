package com.asoft.blog.ui.sign_in

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.airbnb.lottie.LottieAnimationView
import com.asoft.blog.R
import com.asoft.blog.databinding.ActivitySignInBinding
import com.asoft.blog.ui.main.activities.MainActivity
import com.asoft.blog.utils.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.*


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var googleClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configViews()

        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleClient = GoogleSignIn.getClient(this, googleConf)

        binding.buttonFirst.setOnClickListener {
            startActivityForResult(googleClient.signInIntent, 100)
        }
    }

    private fun configViews() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(250L)
            withContext(Dispatchers.Main) {
                binding.tvHeader1.scaleViewWithAnimation()
                binding.tvHeader.translateViewWithAnimation()
            }
            delay(250L)
            withContext(Dispatchers.Main) {
                binding.tvDesc.translateViewWithAnimation()
                binding.buttonFirst.translateFromDownViewWithAnimation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account != null) {
            Constants.idUser = account.id ?: ""
            Constants.name = account.displayName ?: ""
            Constants.photoUrl = account.photoUrl
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    Constants.photoUrl = account.photoUrl
                    Constants.name = account.displayName ?: ""
                    Constants.idUser = account.id ?: ""
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                                finish()
                            } else {
                                showAlertDialog(
                                    mTitle = "Error al iniciar sesión",
                                    positiveButtonText = "Entendido",
                                    anim = R.raw.error,
                                    onActionPositive = {},
                                    onActionNegative = null,
                                    mSubtitle = "Asegúrese de tener una conexión activa y estable a Internet",
                                    negativeButtonText = null
                                )
                            }
                        }
                } else {
                    showAlertDialog(
                        mTitle = "Error al iniciar sesión",
                        positiveButtonText = "Entendido",
                        anim = R.raw.error,
                        onActionPositive = {},
                        onActionNegative = null,
                        mSubtitle = "Asegúrese de tener una conexión activa y estable a Internet",
                        negativeButtonText = null
                    )
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }


}