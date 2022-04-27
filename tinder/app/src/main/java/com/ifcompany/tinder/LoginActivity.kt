package com.ifcompany.tinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        callbackManager = CallbackManager.Factory.create()

        initLoginButton()
        initSignUpButton()
        initEmailAndPasswordEditText()
        initFacebookLoginButton()
    }

    private fun initFacebookLoginButton() {
        val facebookLoginButton = findViewById<LoginButton>(R.id.facebookLoginButton)

        facebookLoginButton.setPermissions("email","public_profile")
        facebookLoginButton.registerCallback(callbackManager, object :FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult) {
                // 로그인이 성공적
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if(task.isSuccessful){
                            handleSuccessLogin()
                        }else{
                            Toast.makeText(this@LoginActivity, "페이스북 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@LoginActivity, "페이스북 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initEmailAndPasswordEditText() {
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        emailEditText.addTextChangedListener {
            val enable = getInputEmail().isNotEmpty() && getInputPassword().isNotEmpty()
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }

        passwordEditText.addTextChangedListener {
            val enable = getInputEmail().isNotEmpty() && getInputPassword().isNotEmpty()
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }
    }

    private fun initSignUpButton() {
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            if(password.length < 6){
                messageShow("비밀번호는 6자리 이상이어야 합니다.")
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            messageShow("회원가입을 성공했습니다. 로그인 버튼을 눌러 로그인해주세요.")
                        } else {
                            Log.e("error",task.exception.toString())
                            messageShow("이미 가입한 이메일이거나, 회원가입에 실패했습니다.")
                        }
                    }

        }
    }

    private fun initLoginButton() {
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            handleSuccessLogin()
                        } else {
                            messageShow("로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.")
                        }
                    }
        }
    }

    private fun getInputEmail(): String{
        return findViewById<EditText>(R.id.emailEditText).text.toString()
    }

    private fun getInputPassword(): String{
        return findViewById<EditText>(R.id.passwordEditText).text.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode,resultCode,data)
    }

    private fun messageShow(text:String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun handleSuccessLogin(){
        if(auth.currentUser == null){
            messageShow("로그인에 실패했습니다.")
            return
        }

        val userId = auth.currentUser?.uid.orEmpty()
        val currentUserDB = Firebase.database.reference.child("Users").child(userId)
        val user = mutableMapOf<String,Any>()
        user["userId"] = userId
        currentUserDB.updateChildren(user)

        finish()
    }
}