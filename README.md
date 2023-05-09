#Android SDK
Place your Android SDK somewhere in your home directory or some other application-independent location. Some distributions of IDEs include the SDK when installed, and may place it under the same directory as the IDE. This can be bad when you need to upgrade (or reinstall) the IDE, as you may lose your SDK installation, forcing a long and tedious redownload.

Also avoid putting the SDK in a system-level directory that might need root permissions, to avoid permissions issues.

#Build system
Your default option should be Gradle using the Android Gradle plugin.

It is important that your application's build process is defined by your Gradle files, rather than being reliant on IDE specific configurations. This allows for consistent builds between tools and better support for continuous integration systems.

#Project structure
Although Gradle offers a large degree of flexibility in your project structure, unless you have a compelling reason to do otherwise, you should accept its default structure as this simplify your build scripts.

#Gradle configuration
General structure. Follow Google's guide on Gradle for Android.

#minSdkVersion: 24 We recommend to have a look at the Android version usage chart before defining the minimum API required. Remember that the statistics given are global statistics and may differ when targeting a specific regional/demographic market. It is worth mentioning that some material design features are only available on Android 5.0 (API level 21) and above. And also, from API 21, the multidex support library is not needed anymore.

Small tasks. Instead of (shell, Python, Perl, etc) scripts, you can make tasks in Gradle. Just follow Gradle's documentation for more details. Google also provides some helpful Gradle recipes, specific to Android.

Passwords. In your app's build.gradle you will need to define the signingConfigs for the release build. Here is what you should avoid:

Don't do this. This would appear in the version control system.
