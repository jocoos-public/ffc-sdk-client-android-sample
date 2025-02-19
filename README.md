# ffc-sdk-client-android

### Installation
```
dependencies {
    implementation "com.jocoos.flipflop:ffc-sdk-client-android:1.0.1"
}
````

In your "settings.gradle" file:
```
dependencyResolutionManagement {
    repositories {
        ...
        maven { url = uri("https://jitpack.io") } // <= add this
    }
}
```
### Usage

FlipFlop relies on the RECORD_AUDIO and CAMERA permissions to use the microphone and camera. These permission must be requested at runtime. Reference the sample app for an example

1. Managing video room
  * Call following method to get instance for video room
```
// server_url: FlipFlop Cloud server address
// access_token: access token
val api = FFCloudSDK.api(serverUrl, accessToken)
```
  * Providing following functions
    - Creating video room
    ```
    // title: title
    // description: description
    // password: VideoRoom password for joining
    // customType: custom type
    // customData: custom data Key-Value Pair
    api.createVideoRoom(title, description, password, customType, customData)
    ```

    * Getting video room info

    ```
    // videoRoomId: video room's id
    api.getVideoRoom(videoRoomId)
    ```

    * Getting video room list

    ```
    // videoRomState: state
    // type: type
    // sortBy: sort
    // page: page number
    // pageSize: page size
    api.listVideoRooms(videoRoomState, type, sortBy, page, pageSize)
    ```

    * Getting webrtc information for joining in video room

    ```
    // videoRoomId: 
    // password: video room password
    // customData: custom data Key-Value Pair
    api.issueWebRtcVideoRoomToken(videoRoomId, password, customData)
    ```

2. Joining in a video room
  * 1. Creating video room instance
  ```
  // webRtcServerUrl, webRtcToken: issueWebRtcVideoRoomToken에서 얻어온 값
  val videoRoom = FFCloudSDK.connectWebRtcVideoRoom(applicationContext, webRtcServerUrl, webRtcToken)
  // R.id.renderer: a view for participant's screen
  // R.id.local_camera: a view for my camera screen
  videoRoom.initVideoRenderer(findViewById<FFCSurfaceViewRenderer>(R.id.renderer))
  videoRoom.initVideoRenderer(findViewById<FFCTextureViewRenderer>(R.id.local_camera))
  ```
  * 2. Registering events
  ```
  videoRoom.events.collect { event ->
      when (event) {
          is FFCVideoRoomEvent.Disconnected -> {
              // video room was disconnected
          }
          is FFCVideoRoomEvent.TrackSubscribed -> {
              // the participant has subscribed to a track
          }
          is FFCVideoRoomEvent.TrackUnsubscribed -> {
              // a previously subscribed track has been unsubscribed
          }
          else -> {}
      }
  }
  ```

  * 3. Displaying my camera
  ```
  val renderer = findViewById<FFCTextureViewRenderer>(R.id.local_camera)
  videoRoom.attachLocalVideo(viewRenderer = renderer, enableMicrophone = false, enableCamera = true)
  ```

  * 4. Displaying participant's screen
    * connecting between track and view when 'TrackSubscribed' event happened
  ```
  val remoteRenderer = findViewById<FFCSurfaceViewRenderer>(R.id.renderer)
  videoRoom.attachRemoteVideo(track, remoteRenderer)
  ```
