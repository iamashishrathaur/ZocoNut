package com.example.zoconut

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zoconut.Adepter.UserAdepter
import com.example.zoconut.Modal.AddData
import com.example.zoconut.databinding.ActivityAllContractsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllContracts : AppCompatActivity() {
    private lateinit var binding: ActivityAllContractsBinding
    lateinit var email:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAllContractsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences: SharedPreferences =getSharedPreferences("user", MODE_PRIVATE)
        email= sharedPreferences.getString("email","")!!
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val arrayList = ArrayList<AddData>()
        val myRef=FirebaseDatabase.getInstance().getReference("myData")
        myRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    arrayList.clear()
                    for (dataSnapshot in snapshot.children){
                        val data=dataSnapshot.getValue(AddData::class.java)
                        if (data!!.myEmail.equals(email)){
                            arrayList.add(data)
                        }
                    }
                    val adapter = UserAdepter(arrayList,this@AllContracts)
                    binding.recyclerView.adapter=adapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}