package com.jocoos.flipflop.samgle

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.jocoos.flipflop.ffcloud.FFCApi
import com.jocoos.flipflop.ffcloud.FFCVideoRoom
import com.jocoos.flipflop.ffcloud.FFCVideoRoomEvent
import com.jocoos.flipflop.ffcloud.FFCloudSDK
import com.jocoos.flipflop.ffcloud.events.collect
import com.jocoos.flipflop.ffcloud.view.FFCSurfaceViewRenderer
import com.jocoos.flipflop.ffcloud.view.FFCTextureViewRenderer
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var api: FFCApi
    private lateinit var videoRoom: FFCVideoRoom

    private val ffcUrl = "<FlipFlop_Cloud_Server_Address>"
    private val ffcAccessToken = "<ACCESS_TOKEN>"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        api = FFCloudSDK.api(ffcUrl, ffcAccessToken)

        requestNeededPermissions { connectToRoom() }
    }

    private fun connectToRoom() {
        lifecycleScope.launch {
            api.createVideoRoom("new video room", "android sdk test")
                .onSuccess {
                    api.issueWebRtcVideoRoomToken(it.id)
                        .onSuccess {
                            videoRoom = FFCloudSDK.connectWebRtcVideoRoom(applicationContext, it.webRtcServerUrl, it.webRtcToken)

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
                        .onFailure {
                            throw IllegalStateException("failed to create webrtc access token")
                        }
                }
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

    private fun requestNeededPermissions(onHasPermissions: () -> Unit) {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
                var hasDenied = false
                // Check if any permissions weren't granted.
                for (grant in grants.entries) {
                    if (!grant.value) {
                        Toast.makeText(this, "Missing permission: ${grant.key}", Toast.LENGTH_SHORT).show()

                        hasDenied = true
                    }
                }

                if (!hasDenied) {
                    onHasPermissions()
                }
            }

        // Assemble the needed permissions to request
        val neededPermissions = listOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
            .filter { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED }
            .toTypedArray()

        if (neededPermissions.isNotEmpty()) {
            requestPermissionLauncher.launch(neededPermissions)
        } else {
            onHasPermissions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoRoom.disconnect()
    }
}