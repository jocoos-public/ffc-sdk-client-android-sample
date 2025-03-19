# ffc-sdk-client-android-sample

## How to test a sample app

You need 'ffcUrl' and 'ffcAccessToken' to start a sample app(contact Jocoos to get the value).

At first, You need to update 'ffcUrl' and 'ffcAccessToken in **FlipFlopSampleApp.kt**. You can get it from FlipFlop Cloud server SDK(refer to server SDK documentation).
* url: FlipFlop Cloud Server Address
* accessToken: access token for accessing FlipFlop Cloud

A sample app has following functions.
* creating a video rom and then join it
* showing video room list and join a video room which you selected

#### How to change video resolution

You can set video options when creating video room.

```
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
```

## How to use ffc-sdk-client-android

### Installation
```
dependencies {
    implementation "com.github.jocoos-public:ffc-sdk-client-android:1.1.3"
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
  // webRtcServerUrl, webRtcToken: values from issueWebRtcVideoRoomToken
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
