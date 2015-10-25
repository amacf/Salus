## Salus
A crash safety mobile app that detects vehicle impact and signals for help.

#Inspiration
Vehicular death continues to be a leading cause of death in the US, claiming over 30,000 annually. Self-driving vehicles promise to bring this number close to 0, but in the meantime, plenty can still be done to help lower this number.

While services already exist that claim to offer the same service many are only available in luxury vehicles. We also found a purchasable service titled "splitsecnd", that can automatically call emergency services for you. If you can't dial 911 yourself, why would you be able to speak to them?

#What it does
Salus monitors your mobile devices internal accelerometer to detect a rapid change. If this acceleration is over a certain G-threshold, the program "activates", giving the user an 8-second grace period in which he/she can manually override the activation with the push of a single button. This feature is important to avoid the potential false-positive of your device falling off it's mount. When the 8 second override window expires, Salus sends an automated voice message to emergency services alerting the dispatcher of your location and situation.

This automation is important in the case a car crash knocks you unconscious, flings you from your seat, or pins you in a position such that you can't make the call yourself.

#How I built it
Salus was built in Android Studio, using Google Play services to pull an accurate location, and the Tropo API to send this information to emergency services in the form of a voice call.

#Challenges I ran into
There were a bunch of poorly documented/dysfunctional APIs we wasted time troubleshooting with before ditching altogether. Difficulty synchronizing multiple threads was also annoying.

#Accomplishments that I'm proud of
The app came out fully functional, with no compromises made. It has a clear use, and real potential to save lives.

#What I learned
We learned Android Studio from scratch, the Tropo API, Google APIs. Everything used were unfamiliar to us to begin with. Designing a simple and effective UI was also a fun challenge.

#What's next for Salus
Salus was never meant to be fully functional as a mobile app, the speed at which it has to ping the accelerometer would drain battery life, and poor cell service is always a possible issue. The Salus software would shine embedded in a dedicated accelerometer, attached to a car. It could also be used in luxury vehicles that already run Android-based software.
