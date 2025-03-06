package com.jocoos.flipflop.sample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.jocoos.flipflop.FFCLocalVideoOptions
import com.jocoos.flipflop.FFCVideoCaptureParameter
import com.jocoos.flipflop.FFCVideoEncoding
import com.jocoos.flipflop.FFCVideoPublishOptions
import com.jocoos.flipflop.FFCVideoRoom
import com.jocoos.flipflop.FFCVideoRoomEvent
import com.jocoos.flipflop.FFCVideoRoomOptions
import com.jocoos.flipflop.FFCloudSDK
import com.jocoos.flipflop.events.collect
import com.jocoos.flipflop.sample.FlipFlopSampleApp.Companion.WEBRTC_SERVER_URL
import com.jocoos.flipflop.sample.FlipFlopSampleApp.Companion.WEBRTC_TOKEN
import com.jocoos.flipflop.view.FFCSurfaceViewRenderer
import com.jocoos.flipflop.view.FFCTextureViewRenderer
import kotlinx.coroutines.launch

class VideoRoomActivity : AppCompatActivity() {
    private lateinit var videoRoom: FFCVideoRoom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_room)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val webRtcServerUrl = intent.getStringExtra(WEBRTC_SERVER_URL)
        val webRtcToken = intent.getStringExtra(WEBRTC_TOKEN)

        if (webRtcServerUrl == null || webRtcToken == null) {
            Toast.makeText(this, "invalid WebRTC values", Toast.LENGTH_LONG).show()
            finish()
        } else {
            connectToRoom(webRtcServerUrl, webRtcToken)
        }
    }

    private fun connectToRoom(webRtcServerUrl: String, webRtcToken: String) {
        lifecycleScope.launch {
            videoRoom = FFCloudSDK.connectWebRtcVideoRoom(
                applicationContext,
                webRtcServerUrl,
                webRtcToken,
                // FFCVideoCaptureParameter's default: width = 1280, height = 720, maxFps = 30
                localVideoOptions = FFCLocalVideoOptions(
                    captureParams = FFCVideoCaptureParameter(width = 1280, height = 720, maxFps = 30)
                ),
                // FFCVideoEncoding's default: maxBitrate = 1_700_000, maxFps = 30
                videoPublishOptions = FFCVideoPublishOptions(
                    videoEncoding = FFCVideoEncoding(maxBitrate = 1_700_000, maxFps = 30)
                ),
            )

            videoRoom.initVideoRenderer(findViewById<FFCSurfaceViewRenderer>(R.id.renderer))
            videoRoom.initVideoRenderer(findViewById<FFCTextureViewRenderer>(R.id.local_camera))

            launch {
                videoRoom.events.collect { event ->
                    when (event) {
                        is FFCVideoRoomEvent.Disconnected -> {
                            // video room was disconnected
                        }
                        is FFCVideoRoomEvent.TrackSubscribed -> onTrackSubscribed(event)
                        is FFCVideoRoomEvent.TrackUnsubscribed -> onTrackUnsubscribed(event)
                        else -> {}
                    }
                }
            }

            val renderer = findViewById<FFCTextureViewRenderer>(R.id.local_camera)
            videoRoom.attachLocalVideo(viewRenderer = renderer, enableMicrophone = false, enableCamera = true)
        }
    }

    private fun onTrackSubscribed(event: FFCVideoRoomEvent.TrackSubscribed) {
        val track = event.track
        if (track.isVideoTrack()) {
            findViewById<View>(R.id.progress).visibility = View.GONE

            val remoteRenderer = findViewById<FFCSurfaceViewRenderer>(R.id.renderer)
            videoRoom.attachRemoteVideo(track, remoteRenderer)

            remoteRenderer.visibility = View.VISIBLE
        } else {
            track.setMicrophoneEnabled(false)
        }
    }

    private fun onTrackUnsubscribed(event: FFCVideoRoomEvent.TrackUnsubscribed) {
        val track = event.track
        if (track.isVideoTrack()) {
            val remoteRenderer = findViewById<FFCSurfaceViewRenderer>(R.id.renderer)
            videoRoom.detachRemoteVideo(track, remoteRenderer)

            remoteRenderer.visibility = View.INVISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoRoom.disconnect()
    }
}
