package com.jocoos.flipflop.sample

import android.app.Application

class FlipFlopSampleApp : Application() {
    companion object {
        val ffcUrl = "https://api-sandbox.flipflop.cloud" // ""<FlipFlop_Cloud_Server_Address>"
        val ffcAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJudWxsIiwianRpIjoiMnR2Q3lXVm1veUs5TnFvVHZjZ0dkSHJZTEpxIiwiaXNzIjoiRmxpcEZsb3AiLCJjbGFpbXMiOnsiYXBwSWQiOjE0LCJ0eXBlIjoiTUVNQkVSIiwiYXBwVXNlcklkIjoiNjY5NTE5YTRlMDFhYzA4YjI5MTE2ODE3IiwibWVtYmVySWQiOjk0ODgsInVzZXJuYW1lIjoiYmx1ZXNreTU1NSJ9LCJpYXQiOjE3NDEyMjM0MTUsImV4cCI6MTc0MTgyODIxNX0.-NhJaNKN3Tih24mxfYbZJOQf59121gqU-Hz71FjTwCU"

        const val WEBRTC_SERVER_URL = "webRtcServerUrl"
        const val WEBRTC_TOKEN = "webRtcToken"
    }
}
