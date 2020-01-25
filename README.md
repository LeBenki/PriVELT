# PriVELT

Android application to centralise known concatenatedData from different services.
The project is funded by PriVELT (https://privelt.ac.uk/).

To test the app you will need a github.properties file with :

'''
gpr.usr=GITHUB USERNAME
gpr.key=GITHUB TOKEN OR PASSWORD
'''

It is required to use the LoginService and the DataExtractor

# Current Services

  - Google.com Account automated connection
  - Hotels.com automated connection
  - Strava.com automated connection
  
# Description

The application is composed of a menu to choose the application to login in. Each fragment of the menu has a test button to test the auto-login feature of each service. The login activity has a debug mode to display the WebView during the automated task.

Contributors
----
- [Jordan Bonaldi (Back End API Solution)](http://github.com/jordanbonaldi/)
- [Lucas Benkemoun (Front End Solution)](https://github.com/LeBenki)

License
----

MIT
