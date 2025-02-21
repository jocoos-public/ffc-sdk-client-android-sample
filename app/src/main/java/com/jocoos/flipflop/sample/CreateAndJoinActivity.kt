package com.jocoos.flipflop.sample

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.jocoos.flipflop.FFCApi
import com.jocoos.flipflop.FFCloudSDK
import com.jocoos.flipflop.api.WebRtcVideoTokenInfo
import kotlinx.coroutines.launch

class CreateAndJoinActivity : AppCompatActivity() {
    private lateinit var api: FFCApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_and_join)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        api = FFCloudSDK.api(FlipFlopSampleApp.ffcUrl, FlipFlopSampleApp.ffcAccessToken)

        findViewById<TextView>(R.id.start).setOnClickListener {
            val videoRoomTitle = findViewById<EditText>(R.id.title).text.toString().trim()
            if (videoRoomTitle.isNotEmpty()) {
                createAndJoinVideoRoom(videoRoomTitle)
            }
        }
    }

    private fun createAndJoinVideoRoom(title: String) {
        lifecycleScope.launch {
            api.createVideoRoom(title, "sample sample")
                .onSuccess {
                    api.issueWebRtcVideoRoomToken(it.id)
                        .onSuccess {
                            showVideoRoom(it)
                        }
                        .onFailure {
                            Toast.makeText(this@CreateAndJoinActivity, "failed to issue webrtc token", Toast.LENGTH_LONG).show()
                        }
                }
                .onFailure {
                    Toast.makeText(this@CreateAndJoinActivity, "failed to create video room", Toast.LENGTH_LONG).show()
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
