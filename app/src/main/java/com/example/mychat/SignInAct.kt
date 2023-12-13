package com.example.mychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mychat.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInAct : AppCompatActivity() {

    lateinit var launcher: ActivityResultLauncher<Intent>

    lateinit var auth: FirebaseAuth

    lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth //иницилизируем аунтефикацию
        //выдает результат
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data) //вызов к этой переменной где отправляем выбр аккаунт
            try{

                val account = task.getResult(ApiException::class.java) //тут уже прям прям достаем аккаунт
                if(account!=null){
                    firebaseAuthWithGoogle(account.idToken.toString())
                }

            } catch (e: ApiException){//если выдаст ошибку
                Log.d("MyLog", "ApiExcept")
            }
        }
        binding.bSignIn.setOnClickListener {
            signInWithGoogle()
        }
        checkAuthState()//тут ну во первых запуск, а вто вторых он будет при открытие приложения проверять, зарегистрировались ли мы уже

    }

    private fun getClient(): GoogleSignInClient{//Авторизация по гугл аккаунту, именно это функция выдает списки аккаунтов
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle(){ //отправка в верхнюю функцию, после запускается лаунчер, то есть отправляем клиента
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String){// сюда приходит токин, когда выберем гугл аккаунт
        val cridential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(cridential).addOnCompleteListener{//addOnCompl это считывание прошло ли все успешно
            if(it.isSuccessful){
                Log.d("MyLog", "Google signIn done")
                checkAuthState()//если саксесфулл, то выполняется новая функция, по интенту в новое активити
            } else{
                Log.d("MyLog", "Google signIn falls")
            }
        }
    }

    private fun checkAuthState(){ //ну это понятно переход на новый активити, если регистрация прошла успешно
        if(auth.currentUser != null){
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }

}