# Popmasterr (Mobile)

### A geography game about population density written in Kotlin and Jetpack Compose.

## Table of Contents
- [How to build](#how-to-build)
- [Features](#features)
- [More in depth about gamemodes](#more-in-depth-about-gamemodes)
- [Roadmap](#roadmap)
- [Showcase](#showcase) and [Screenshots](#screenshots)
- [Reflection](#reflection)

## How to build
- Send me a nice message on whatever social media you can find me for the backend link :)
- Put it in networkrequests or env.kt as baseUrl
- (Theoretically you don't have to get a personal maps api key, but I recommend getting one and replacing it in androidmanifest.xml. Either way it's restricted to work only on this app)
- Open the project in Android Studio
- Build and run the app on an emulator or a physical device (min sdk is 26)
  (Sorry, I will not be providing a build for this)

## Features
- **Two gamemodes**:
- - Classic: Guess the population of a given rectangle and get score based on how well you did!
- - Streak: Guess which of two given rectangles has a higher population and try to collect as high of a streak as you can!
- [(Will go more in-depth about the gamemodes further down)](#more-in-depth-about-gamemodes)
- **Profile page**:
- - Google auth integration to show your beautiful profile picture and name ;) (However online data storage and any "functionality" with your profile isn't yet implemented)
- - Local statistics tracking for both gamemodes(tracks classic games played, total score, average score, perfect games, current streak and best streak)
- A simple settings page to change the map type (will expand in the future)
</br>
[Screenshots](#screenshots)

## More in depth about gamemodes
The population is calculated in a backend that's written in python and uses rasterio from a [.tif file with population density of each pixel](https://human-settlement.emergency.copernicus.eu/download.php?ds=pop) </br>
and hosted on a google cloud run instance. A lot of the code for this was written in the past for a webapp group project (of a similar game) </br>

Both of the gamemodes have a retractable panel on the bottom used for inputting guesses and showing the score and other info </br>
The maps are implemented using [Maps Compose](https://developers.google.com/maps/documentation/android-sdk/maps-compose) </br>
Worth noting that the game works better on larger screens (i.e. tablets) as it displays more of the map </br>

## Roadmap 
(or what I may implement next (if I continue this project) in no order)
- [ ] Online data storage with a database for statistics and other things :)
- [ ] Leaderboards
- [ ] Achievements
- [ ] Other login methods (email + password; anonymous, other social medias?, etc.)
- [ ] More gamemodes (Multiplayer!)
- [ ] More settings (not sure what yet)
- [ ] Unit and UI tests (dissapointed that I didn't have time to implement these at the start)

# Showcase
[![Showcase as a video](https://img.youtube.com/vi/YOUTUBE_VIDEO_ID_HERE/0.jpg)](https://www.youtube.com/watch?v=YOUTUBE_VIDEO_ID_HERE)


## Screenshots

<img width="408" height="883" alt="image" src="https://github.com/user-attachments/assets/430aa0c1-9b20-4165-9628-394bb06bfa8d" />
<img width="408" height="883" alt="image" src="https://github.com/user-attachments/assets/95c8eb91-de7b-4e2a-82fc-70c9f811b231" />
<img width="448" height="883" alt="image" src="https://github.com/user-attachments/assets/917c7dad-8022-4ad6-a65f-6c1abbdf7120" />
<img width="408" height="883" alt="image" src="https://github.com/user-attachments/assets/b2648285-1de4-4bfd-b3d2-d09348df1729" />
<img width="408" height="883" alt="image" src="https://github.com/user-attachments/assets/c88ead5d-3636-48a1-9d2c-e19bc3735b28" />
<img width="408" height="883" alt="image" src="https://github.com/user-attachments/assets/a54458c7-fcd3-48a8-87cc-035248575de7" />
<img width="408" height="883" alt="image" src="https://github.com/user-attachments/assets/48c43d25-2c28-4adc-ac18-97b3b35b5025" />
<img width="408" height="883" alt="image" src="https://github.com/user-attachments/assets/6f998dcd-1bf4-4116-a55f-d21cc0f63ec4" />
On a larger device (tablet) and with a different map setting:
<img width="1413" height="883" alt="image" src="https://github.com/user-attachments/assets/12ef4adb-dcb4-4657-924b-708c0d23c939" />
<img width="1413" height="883" alt="image" src="https://github.com/user-attachments/assets/d14586b2-f964-4f44-b179-d70854cbf105" />



## Reflection
Since this is a learning project I thought I may include a small reflection (rather some unordered thoughts) :) </br>

I did the project in the snap of about 4 weeks, half of which I spent on vacation, so progress was not that fast :D </br>

This was a fun learning project and I learned a lot about Kotlin and Jetpack Compose, although there are definitely
a lot of things I could've done better such as implementing tests, writing a better backend and using a database for storing data. Also there are certain bugs with projections and google maps that I couldn't figure out a fix for. </br>

I tried to use as little AI as I could, although I did resort to it for certain UI improvements, cleanups and certain other things </br>

Overall I enjoyed working on this project and am somewhat proud of it. I did my best to make the code readable, although
parts of it are still somewhat messy :(


