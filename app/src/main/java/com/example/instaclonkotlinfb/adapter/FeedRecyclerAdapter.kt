package com.example.instaclonkotlinfb.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.instaclonkotlinfb.databinding.RecyclerRowBinding
import com.example.instaclonkotlinfb.model.Post
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(val postArrayList : ArrayList<Post>):RecyclerView.Adapter<FeedRecyclerAdapter.FeedHolder>() {
    class FeedHolder(val binding : RecyclerRowBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context) , parent ,false)
        return FeedHolder(binding)
    }

    override fun getItemCount(): Int {
       return postArrayList.size
    }

    override fun onBindViewHolder(holder: FeedHolder, position: Int) {
        holder.binding.recyclerEmailText.text = postArrayList.get(position).email
        holder.binding.recyclerCommentText.text = postArrayList.get(position).comment
        Picasso.get().load(postArrayList.get(position).downloadUrl).into(holder.binding.recyclerImageView)
    }
}