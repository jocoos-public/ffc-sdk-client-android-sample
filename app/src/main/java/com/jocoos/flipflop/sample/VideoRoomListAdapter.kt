package com.jocoos.flipflop.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.api.VideoRoomInfo

class VideoRoomListAdapter : RecyclerView.Adapter<VideoRoomListAdapter.ViewHolder>() {
    private var items: List<VideoRoomInfo> = emptyList()
    private var listener: ClickListener? = null
    interface ClickListener {
        fun onClicked(videoRoomInfo: VideoRoomInfo)
    }

    fun setItems(items: List<VideoRoomInfo>) {
        this.items = items
    }

    fun setClickListener(listener: ClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_room_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            listener?.onClicked(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val idView = view.findViewById<TextView>(R.id.id)
        private val roomTypeView = view.findViewById<TextView>(R.id.room_type)
        private val stateView = view.findViewById<TextView>(R.id.state)
        private val titleView = view.findViewById<TextView>(R.id.title)
        private val ownerView = view.findViewById<TextView>(R.id.owner)
        private val createdAtView = view.findViewById<TextView>(R.id.created_at)

        fun bind(videoRoomInfo: VideoRoomInfo) {
            idView.text = videoRoomInfo.id.toString()
            videoRoomInfo.type?.let {
                roomTypeView.text = it.toString()
            }
            videoRoomInfo.videoRoomState?.let {
                stateView.text = it.toString()
            }
            titleView.text = videoRoomInfo.title
            ownerView.text = videoRoomInfo.member.appUserName
            createdAtView.text = videoRoomInfo.createdAt
        }
    }
}
