Food Finder
=================

This is an android studio project.

The goal of this application is to find restaurants within a radius of a selected position.

Current features:
1. Uses google's android maps api to view current position
2. Allows a user to select a position on the map by long-pressing
3. Keywords can be entered to include in the search
4. The search radius can be modified
5. Uses google's places api to search for restaurants with the user's search parameters
6. User can choose a destination, or choose random to have one selected randomly
7. The selected destination is marked on the map
8. The user can touch the marker to bring up google's navigation which will provide directions to the destination

Suggested features:
1. Allow the user to press back to get back to the search results
2. Allow the user to save search results
3. Allow the user to remove destinations from search results
4. Fetch the next 20 results on scroll

Limitations:
1. Google's places api only allows 20 results to show up at a time, with a maximum of 60 results

Installation instructions:
1. Setup android studio (http://developer.android.com/sdk/installing/studio.html)
2. Import the project from git
  VCS > Checkout from version control > Git/SVN/Mercurial
3. You will need to enter your own API keys for google services
  https://developers.google.com/maps/documentation/android/start#get_an_android_certificate_and_the_google_maps_api_key
