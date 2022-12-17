package com.asoft.blog.ui.sign_in

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.asoft.blog.ui.main.activities.MainActivity
import com.asoft.blog.R
import com.asoft.blog.databinding.ActivitySignInBinding
import com.asoft.blog.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var googleClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleClient = GoogleSignIn.getClient(this, googleConf)

        binding.buttonFirst.setOnClickListener {
            startActivityForResult(googleClient.signInIntent, 100)
            //logout()
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account != null) {
            Constants.idUser = account.id ?: ""
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        } else {
            Toast.makeText(this, "No account found", Toast.LENGTH_LONG).show()
        }
    }


    private fun logout() {
        googleClient.signOut().addOnCompleteListener(this) {
            Toast.makeText(this, "Signed Out", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Constants.idUser = account.id ?: ""
                                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                            } else {
                                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }
}