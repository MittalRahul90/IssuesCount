#GitHub OpenIssuesCount

Used JSP page to take user input
This application will take the two parameters from the user to get the input url for the repository issues, 
owner name and the repository name.
After taking the input from user it will call the Java Servlet to open the connection and passes the user input.
Once the connection is open, it will read the page one by one and store in a response string till the last page.
In order to fetch the values, I will convert this response string into JSON Array using external library "json-simple".
Then, program will iterate over this array and fetch the "created_time" and "pull_request" fields from the JSON Array.
Using this fields I will calculate the time of all the Open issues accordingly neglecting all the pull requests.
After getting all the counts of open issue. I will display the results on the web page.

Live Application link: https://infinite-springs-5121.herokuapp.com/
