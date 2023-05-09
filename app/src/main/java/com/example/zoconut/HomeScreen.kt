package com.example.zoconut

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.zoconut.Modal.User
import com.example.zoconut.databinding.ActivityHomeScreenBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.squareup.picasso.Picasso


@Suppress("DEPRECATION")
class HomeScreen : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage
    private lateinit var reference: StorageReference
    private lateinit var dpReference:DatabaseReference
    private lateinit var  bit:Bitmap
    private var mTakeImage: ActivityResultLauncher<String>? = null
    lateinit var binding: ActivityHomeScreenBinding
    lateinit var email:String
    lateinit var name:String
    private var image:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences: SharedPreferences =getSharedPreferences("user", MODE_PRIVATE)
        email= sharedPreferences.getString("email","")!!
        name= sharedPreferences.getString("name","")!!
        binding.name.text=name
        binding.email.text=email
        storage = FirebaseStorage.getInstance()
        reference = storage.reference
        dpReference= FirebaseDatabase.getInstance().getReference("user")

        // qr code
        val writer= QRCodeWriter()
        try {
            val bitMatrix=writer.encode(email, BarcodeFormat.QR_CODE,512,512)
            val width=bitMatrix.width
            val height=bitMatrix.height
            val bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565)
            for (x in 0 until width){
                for (y in 0 until height){
                    bitmap.setPixel(x,y, if (bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                }
            }
            bit=bitmap
            binding.qrCode.setImageBitmap(bitmap)


        }catch (e: WriterException){
            throw e
        }

        // share
        binding.shareQrCode.setOnClickListener{
            val bitmapPath: String = MediaStore.Images.Media.insertImage(contentResolver, bit, "ZocoNut", null)
            val uri = Uri.parse(bitmapPath)
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/png"
            startActivity(intent)
        }

        binding.allContract.setOnClickListener{
            startActivity(Intent(this@HomeScreen,AllContracts::class.java))
        }
        binding.startScanner.setOnClickListener{
            startActivity(Intent(this@HomeScreen,QrCodeScanner::class.java))
        }

        // image
        mTakeImage = registerForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            if (result != null) {
                uploadImage(result)
                binding.progressBar.progress=100
                binding.progressBar.visibility=View.VISIBLE
            }
        }
        binding.userImage.setOnClickListener{
            mTakeImage!!.launch("image/*")
        }
        readImage()
    }

    private fun readImage() {
        dpReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    image=null
                    for (dataSnapshot in snapshot.children){
                        val user=dataSnapshot.getValue(User::class.java)
                        if (user!!.email.equals(email)){
                            image=user.image
                        }
                    }
                    try {
                        Picasso.get().load(image).fit().into(binding.userImage)
                    }
                    catch (e: NullPointerException){
                        throw e
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeScreen,error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadImage(result: Uri) {
        val storageReference: StorageReference = reference.child("Image/$email")
        storageReference.putFile(result).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri: Uri ->
                binding.progressBar.progress=0
                binding.progressBar.visibility=View.GONE
                val map: MutableMap<String, Any> = HashMap()
                map["image"] = uri.toString()
                dpReference.child(name).updateChildren(map)
                Picasso.get().load(uri).fit().into(binding.userImage)
            }
        }

    }

}