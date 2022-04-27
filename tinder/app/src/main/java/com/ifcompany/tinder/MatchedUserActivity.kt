package com.ifcompany.tinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ifcompany.tinder.DBKey.Companion.LIKED_BY
import com.ifcompany.tinder.DBKey.Companion.MATCH
import com.ifcompany.tinder.DBKey.Companion.NAME
import com.ifcompany.tinder.DBKey.Companion.USERS

class MatchedUserActivity : AppCompatActivity() {

    private lateinit var usersDb: DatabaseReference
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val adapter = MatchedUserAdapter()
    private val cardItems = mutableListOf<CardItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matched_user)

        usersDb = Firebase.database.reference.child(USERS)

        initMatchedUserRecyclerView()
        getMatchUsers()
    }

    private fun getMatchUsers() {
        val matchedDB = usersDb.child(getCurrentUserID()).child(LIKED_BY).child(MATCH)

        matchedDB.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.key?.isNotEmpty() == true){
                    getUserByKey(snapshot.key.orEmpty())
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun getUserByKey(userId: String) {
        usersDb.child(userId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                cardItems.add(CardItem(userId,snapshot.child(NAME).value.toString()))
                adapter.submitList(cardItems)
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun initMatchedUserRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.matchedUserRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun getCurrentUserID(): String {
        if(auth.currentUser == null){
            messageShow("로그인이 되어있지 않습니다.")
            finish()
        }

        return auth.currentUser?.uid.orEmpty()
    }

    private fun messageShow(text:String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}