package com.asoft.blog.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.asoft.blog.R
import com.asoft.blog.data.remote.Post
import com.asoft.blog.databinding.ItemPostBinding
import com.asoft.blog.utils.ColorGenerator
import com.asoft.blog.utils.TextDrawable
import com.bumptech.glide.Glide

class PostsAdapter(private val posts: MutableList<Post>, private val context: Context) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {
    interface OnItemTap {
        fun onTap(post: Post)
    }

    fun setItemTapListener(l: OnItemTap) {
        onTapListener = l
    }

    private var onTapListener: OnItemTap? = null

    fun updateList(mPosts: List<Post>) {
        posts.clear()
        posts.addAll(mPosts)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemBinding: ItemPostBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(post: Post) {
            itemBinding.postTitle.text = post.title
            itemBinding.tvPostText.text = post.description
            itemBinding.tvName.text = post.author
            itemBinding.tvDate.text = post.date

            val generator = ColorGenerator.MATERIAL
            val color = generator!!.randomColor

            val drawable: TextDrawable = TextDrawable.builder()
                .buildRound(post.author?.first()?.toString(), color)

            itemBinding.profilePicture.setImageDrawable(drawable)
            /*Glide.with(context)
                .load(drawable)
                .circleCrop()
                .into(itemBinding.profilePicture)*/
            //itemBinding.profilePicture.setImageDrawable(drawable)
            /*val person = getRandomPerson(context)
            itemBinding.tvName.text = person.name.trim()
            Glide.with(context)
                .load(person.imageDrw)
                .circleCrop()
                .into(itemBinding.profilePicture)*/

            itemBinding.cardComment.setOnClickListener {
                onTapListener?.onTap(post)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rv_item_anim)
        return holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

}