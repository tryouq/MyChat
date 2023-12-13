package com.example.mychat

import android.graphics.Bitmap.CompressFormat.*
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mychat.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding //для обычного слушания нажатия

    lateinit var auth: FirebaseAuth

    lateinit var adapter: UserAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth



        binding.ButtonOut.setOnClickListener {
            auth.signOut()
            finish()
        }


        val database = Firebase.database
        val myRef = database.getReference("message") //это выбераем как бы ветку(путь, ключ)
        binding.ButtonSend.setOnClickListener {
            myRef.child(myRef.push().key ?: "none").setValue(User(auth.currentUser?.displayName,binding.EditText.text.toString())) //Здесь заносим в ключ(но вроде как просто заменяем, не добовляем)
        }
        onChangeListener(myRef) //вызов функции для вызова сообщения
        initRcView()
    }

    private fun initRcView() = with(binding){
        adapter = UserAdapter()
        rcView.layoutManager = LinearLayoutManager(this@MainActivity).apply{
            stackFromEnd = true
            reverseLayout = false
        }

        rcView.adapter= adapter
    }


    private fun onChangeListener(dRef: DatabaseReference){ //слушатель действия пользователя, для этого функция
        dRef.addValueEventListener(object : ValueEventListener{//считывание постоянно
            override fun onDataChange(snapshot: DataSnapshot) {
            val list = ArrayList<User>()
            for(s in snapshot.children){
                    val user = s.getValue(User::class.java)
                    if(user != null)list.add(user)

                }
                adapter.submitList(list)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }





}