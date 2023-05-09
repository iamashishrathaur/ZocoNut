package com.example.zoconut

import android.Manifest.permission
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.zoconut.Modal.AddData
import com.example.zoconut.Modal.User
import com.example.zoconut.databinding.ActivityQrCodeScannerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import eu.livotov.labs.android.camview.ScannerLiveView
import eu.livotov.labs.android.camview.ScannerLiveView.ScannerViewEventListener
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder

class QrCodeScanner : AppCompatActivity() {
     private var PERMISSION_CODE = 200
     lateinit var binding: ActivityQrCodeScannerBinding
     lateinit var name:String
     lateinit var email:String
     lateinit var databaseData:String
     lateinit var databaseEmail: String
     private var databaseName:String?=null
     private var databaseImage: String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityQrCodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences:SharedPreferences=getSharedPreferences("user", MODE_PRIVATE)
        name= sharedPreferences.getString("name","")!!
        email= sharedPreferences.getString("email","")!!
        if (!checkPermission()){
            requestPermission()
        }
        binding.qrScanner.scannerViewEventListener = object : ScannerViewEventListener {
            override fun onScannerStarted(scanner: ScannerLiveView) {}
            override fun onScannerStopped(scanner: ScannerLiveView) {}
            override fun onScannerError(err: Throwable) {}
            override fun onCodeScanned(data: String) {
                if (data.isNotEmpty()){
                    val myRef=FirebaseDatabase.getInstance().getReference("user")
                    myRef.addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                binding.qrScanner.stopScanner()
                                for (dataSnapshot in snapshot.children) {
                                    val user = dataSnapshot.getValue(User::class.java)
                                    if (user!!.email == data) {
                                        databaseName=user.name
                                        databaseImage=user.image
                                        addData(data)
                                    }
                                }
                            }
                            else{
                                binding.qrScanner.stopScanner()
                                onBackPressedDispatcher.onBackPressed()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            binding.qrScanner.stopScanner()
                            Toast.makeText(this@QrCodeScanner,error.message, Toast.LENGTH_SHORT).show()
                            onBackPressedDispatcher.onBackPressed()
                        }

                    })
                }
            }
        }
    }

    private fun addData(data: String) {
        val ref= FirebaseDatabase.getInstance().getReference("myData")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (dataSnapshot in snapshot.children){
                        val userdata=dataSnapshot.getValue(AddData::class.java)
                        databaseData= userdata!!.email!!
                        databaseEmail= userdata.myEmail!!
                    }
                    if (databaseData!=data || databaseEmail!=email){
                       saveData(ref,data)
                    }
                    else{
                        Toast.makeText(this@QrCodeScanner,"data already exist", Toast.LENGTH_SHORT).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
                else{
                    saveData(ref,data)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@QrCodeScanner,error.message, Toast.LENGTH_SHORT).show()
                onBackPressedDispatcher.onBackPressed()
            }

        })


    }

    private fun saveData(ref: DatabaseReference,data: String) {

        val myData= AddData(email,data,databaseName,databaseImage)
        ref.child(System.currentTimeMillis().toString()).setValue(myData).addOnSuccessListener {
            Toast.makeText(this@QrCodeScanner,"user successfully saved", Toast.LENGTH_SHORT).show()
            onBackPressedDispatcher.onBackPressed()

      }
   }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(permission.CAMERA,permission.VIBRATE), PERMISSION_CODE)
    }

    private fun checkPermission(): Boolean {
        val camera = ContextCompat.checkSelfPermission(applicationContext, permission.CAMERA)
        val vibrator = ContextCompat.checkSelfPermission(applicationContext, permission.VIBRATE)
        return camera == PackageManager.PERMISSION_GRANTED && vibrator==PackageManager.PERMISSION_GRANTED
    }
    override fun onResume() {
        super.onResume()
        val zxDecoder = ZXDecoder()
        zxDecoder.scanAreaPercent = 0.8
        binding.qrScanner.decoder = zxDecoder
        binding.qrScanner.startScanner()
    }

    override fun onPause() {
        super.onPause()
        binding.qrScanner.stopScanner()
    }

}