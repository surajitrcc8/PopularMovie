# Project Overview
Most of us can relate to kicking back on the couch and enjoying a movie with friends and family. In this project, you’ll build an app to allow users to discover the most popular movies playing.
This app will:
*	Present the user with a grid arrangement of movie posters upon launch.
*	Allow user to change sort order via a setting:
*	The sort order can be by most popular or by highest-rated or by favorite.
* Allow the user to tap on a movie poster and transition to a details screen with additional information such as:
  * original title
  * movie poster image thumbnail
  *	A plot synopsis (called overview in the api)
  *	user rating (called vote average in the api)
  *	release date
* Allow users to perform certain operations such as::
  * view and play trailers (either in the YouTube app or a web browser).
  * read reviews of a selected movie
  * mark a movie as a favorite in the details view by tapping a button(love).
*	Create a database and content provider to store the names and ids of the user's favorite movies (and optionally, the rest of the information needed to display their favorites collection while offline).
# Why this Project?
To become an Android developer, you must know how to bring mobile experiences to life. Specifically, you need to know how to build clean and compelling user interfaces (UIs), fetch data from network services, and optimize the experience for various mobile devices. I will hone these fundamental skills in this project.
By building this app, I will demonstrate my understanding of the foundational elements of programming for Android. This app will communicate with the Internet and provide a responsive and delightful user experience.
# What Will You Learn from This Project?
*	You will fetch data from the Internet with theMovieDB API.
*	You will use adapters and custom list layouts to populate list views.
*	You will incorporate libraries to simplify the amount of code you need to write
*	You will store the data in SQLite DB using Content Provider to support offline access

# Pre-requisite
You need to get a free TMDB API key from here. After that add this line MyTMDBApiKey="YOUR_TMDB_API_KEY" to [USER_HOME]/.gradle/gradle.properties In my case in mac it was at /Users/surajitbiswas/.gradle/gradle.properties

# Note
If you do not have such file in above mention location then follow below process.

You need to create the gradle.properties file using android studio File > New > New file
and give gradle.properties as name and put it [USER_HOME]/.gradle/


