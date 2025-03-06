package com.jocoos.flipflop.sample

import android.app.Application

class FlipFlopSampleApp : Application() {
    companion object {
        val ffcUrl = "https://api-sandbox.flipflop.cloud" // ""<FlipFlop_Cloud_Server_Address>"
        val ffcAccessToken = "<ACCESS_TOKEN>"

        const val WEBRTC_SERVER_URL = "webRtcServerUrl"
        const val WEBRTC_TOKEN = "webRtcToken"
    }
}
