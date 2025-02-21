package com.jocoos.flipflop.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jocoos.flipflop.FFCApi
import com.jocoos.flipflop.FFCloudSDK
import com.jocoos.flipflop.api.VideoRoomInfo
import com.jocoos.flipflop.api.WebRtcVideoTokenInfo
import kotlinx.coroutines.launch

class VideoRoomListActivity : AppCompatActivity() {
    private lateinit var api: FFCApi
    private lateinit var roomList: VideoRoomListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_room_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        roomList = VideoRoomListAdapter().apply {
            setClickListener(object : VideoRoomListAdapter.ClickListener {
                override fun onClicked(videoRoomInfo: VideoRoomInfo) {
                    joinVideoRoom(videoRoomInfo)
                }
            })
        }
        val roomListView = findViewById<RecyclerView>(R.id.video_room_list)
        roomListView.layoutManager = LinearLayoutManager(this)
        roomListView.adapter = roomList

        api = FFCloudSDK.api(FlipFlopSampleApp.ffcUrl, FlipFlopSampleApp.ffcAccessToken)
        lifecycleScope.launch {
            api.listVideoRooms()
                .onSuccess {
                    roomList.setItems(it.content)
                    roomList.notifyDataSetChanged()
                }
                .onFailure {
                    Toast.makeText(this@VideoRoomListActivity, "failed to get video room list", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun joinVideoRoom(videoRoomInfo: VideoRoomInfo) {
        lifecycleScope.launch {
            api.issueWebRtcVideoRoomToken(videoRoomInfo.id)
                .onSuccess {
                    showVideoRoom(it)
                }
                .onFailure {
                    Toast.makeText(this@VideoRoomListActivity, "failed to issue webrtc token", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun showVideoRoom(webRtcVideoTokenInfo: WebRtcVideoTokenInfo) {
        val intent = Intent(this, VideoRoomActivity::class.java)
        intent.putExtra(FlipFlopSampleApp.WEBRTC_SERVER_URL, webRtcVideoTokenInfo.webRtcServerUrl)
        intent.putExtra(FlipFlopSampleApp.WEBRTC_TOKEN, webRtcVideoTokenInfo.webRtcToken)
        startActivity(intent)
    }
}
