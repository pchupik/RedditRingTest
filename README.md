# Reddit Sample App
A simple Reddit client that shows top entries from www.reddit.com/top

Features
* App shows list of enties which consist of
  * Title (full length)
  * Author
  * Entry date, in format "x hours ago"
  * A thumbnail for those who have a picture
  * Number of comments
* User can tap on thumbnail image to see the full size picture (opens in browser)
* Tap on list item opens reddit post in browser
* Do not reload list on orientation change
* Pagination (10 items per page)

Libraries used
* Android support
  * AppCompat  
  * RecyclerView
  * CardView
* Android Architecture Components
  * ViewModel and LiveData
  * Paging
* Square
  * okHttp
  * Picasso
