Sprockets
=========

Sprockets is a Java library that provides a Java interface for the [Google Places][1], [Google Street View Image][2] and [Google Distance Matrix][9] APIs.

* Features
    * [Google Places API](#google-places-api)
    * [Google Street View Image API](#google-street-view-image-api)
    * [Google Distance Matrix API](#google-distance-matrix-api)
* [Download and Configure](#download-and-configure)
* [Javadoc][3]

Google Places API
-----------------

Full support for Place Search, Details, Photos, Autocomplete, and Query Autocomplete requests, including all parameters and returned fields.  Getting a list of places can be as simple as:

```java
Places.textSearch(new Params().query("pizza near willis tower")).getResult();
```

More detailed searches can include lat/long with radius, specific types of places, keywords, price range, places that are open now, etc.  For each returned place, you can also retrieve its full details, reviews, photos, and events.

The Google Places API can return a lot of information about each place and most of the time you probably won't need every detail.  For maximum performance and minimum memory usage, you can specify which fields you want and limit the number of results.

```java
Places.nearbySearch(new Params().location(47.60567, -122.3315).radius(5000)
        .keyword("swimming").openNow().maxResults(5),
        NAME, VICINITY, RATING, PHOTOS).getResult();
```

[Places Javadoc][7]


Google Street View Image API
----------------------------

Download a Google Street View Image by supplying a lat/long or location name.

```java
StreetView.image(new Params().location("18 Rue Cujas, Paris, France")).getResult();
```

For fine control of the camera, you can also specify the heading, pitch, and field of view.

```java
StreetView.image(new Params().location(40.748769, -73.985332)
        .heading(210).pitch(33).fov(110)).getResult();
```

[StreetView Javadoc][8]


Google Distance Matrix API
-----------------

Query the Google Distance Matrix API to get durations and distances between multiple locations

```java
DistanceMatrix.distances(new Params().origin(48.2116039, 16.37701)
   .destinations("Staatsoper in Wien, Austria", "Rathaus in Wien, Austria")
   .mode("waling")).getResults();
```



[1]: https://developers.google.com/places/
[2]: https://developers.google.com/maps/documentation/streetview/
[3]: http://pushbit.github.io/sprockets/java/apidocs/
[4]: http://search.maven.org/#artifactdetails|net.sf.sprockets|sprockets|1.1.0|jar
[5]: https://code.google.com/apis/console/
[6]: http://pushbit.github.io/sprockets/java/apidocs/index.html?net/sf/sprockets/Sprockets.html
[7]: http://pushbit.github.io/sprockets/java/apidocs/index.html?net/sf/sprockets/google/Places.html
[8]: http://pushbit.github.io/sprockets/java/apidocs/index.html?net/sf/sprockets/google/StreetView.html
[9]: https://developers.google.com/maps/documentation/distancematrix/
