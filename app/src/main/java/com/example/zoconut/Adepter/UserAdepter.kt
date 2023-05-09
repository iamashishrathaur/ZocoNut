package com.example.zoconut.Adepter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zoconut.CardViewer
import com.example.zoconut.Modal.AddData
import com.example.zoconut.R
import com.squareup.picasso.Picasso

class UserAdepter(private val mList: List<AddData>,private val context: Context) : RecyclerView.Adapter<UserAdepter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = mList[position]
        holder.email.text = modal.email
        holder.name.text=modal.name
        try {
           Picasso.get().load(modal.image).fit().into(holder.image)
        }catch (e:NullPointerException) {
            Picasso.get().load(R.drawable.user).fit().into(holder.image)
        }
        holder.itemView.setOnClickListener{
            val intent=Intent(context,CardViewer::class.java)
            intent.putExtra("name",modal.name)
            intent.putExtra("email",modal.email)
            intent.putExtra("image",modal.image)
           context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val email: TextView = itemView.findViewById(R.id.email_item_view)
        val image: ImageView = itemView.findViewById(R.id.image_item_view)
        val name: TextView = itemView.findViewById(R.id.name_item_view)

    }


}