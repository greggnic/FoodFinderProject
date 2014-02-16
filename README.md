Food Finder
=================

This is an android studio project.

The goal of this application is to find restaurants within a radius of a selected position.

##Current features:
* Uses google's android maps api to view current position
* Allows a user to select a position on the map by long-pressing
* Keywords can be entered to include in the search
* The search radius can be modified
* Uses google's places api to search for restaurants with the user's search parameters
* User can choose a destination, or choose random to have one selected randomly
* The selected destination is marked on the map
* The user can touch the marker to bring up google's navigation which will provide directions to the destination

##Future features:
* Allow the user to press back to get back to the search results
* Allow the user to save search results
* Allow the user to remove destinations from search results
* Fetch the next 20 results on scroll

##Limitations:
* Google's places api only allows 20 results to show up at a time, with a maximum of 60 results

##Installation instructions:
1. Setup [android studio](http://developer.android.com/sdk/installing/studio.html)
2. Import the project from git
    * VCS > Checkout from Version Control > GitHub
3. You will need to enter your own API keys for google services
    * [Get an Android certificate and the Google Maps API key](https://developers.google.com/maps/documentation/android/start#get_an_android_certificate_and_the_google_maps_api_key)
    * Make sure you add com.zeroday.foodfinder to the allowed Android applications or refactor the package and add your own package
    * Use ANDROID_API_KEY in AndroidManifest.xml
    * Use BROWSER_API_KEY in com.zeroday.Constants
