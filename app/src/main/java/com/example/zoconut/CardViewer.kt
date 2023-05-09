package com.example.zoconut

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zoconut.databinding.ActivityCardViewerBinding
import com.squareup.picasso.Picasso

class CardViewer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding:ActivityCardViewerBinding= ActivityCardViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent:Bundle= intent.extras!!
        binding.name.text=intent.getString("name")
        binding.email.text=intent.getString("email")
        Picasso.get().load(intent.getString("image")).fit().into(binding.image)
    }
}