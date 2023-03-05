# homevision-challenge

The project's requirements state that the first 10 pages of the houses API must be requested, but there's
         no specification as to how this needs to work. For standard pagination the client of our API would indicate
         a page number and page size and the backend would invoke the houses API accordingly passing the given params.
         So in order to get the first 10 pages with size 10 the client would make 10 calls to our API.
         
For the sake of the exercise and to make the code a bit more interesting I decided that our new API endpoint
         does not take any parameter and for a single call the backend always requests 10 pages with size 10 to the
         houses API and then processes the bulk altogether. Since I built a small REST application instead of a plain script
         this approach does not necessarily make sense in a real world scenario but should fit the project's requirements.