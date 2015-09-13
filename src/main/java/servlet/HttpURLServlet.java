package servlet;

import main.java.launch.TimeCalculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Servlet implementation class HttpURLServlet
 */
@WebServlet("/HttpURLServlet")
public class HttpURLServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public HttpURLServlet() {
        
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpURLServlet http = new HttpURLServlet();
	      
	    String owner = request.getParameter("owner"); // Getting the owner name of repository
	    String repo = request.getParameter("repo"); // Getting the repository
	    

		String url = "https://api.github.com/repos/" + owner + "/"  + repo + "/issues"; // passing the GitHub public repository api url
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();  // open the connection
		
		String linkString = con.getHeaderField("Link");  //get the header link
		String[] links = new String[100];
		links = linkString.split(","); // split the links 

		
		//removing '<' and '>' characters from the link string
		for (int i=0 ; i < links.length; i++) {
			links[i] = links[i].substring(links[i].indexOf("<") + 1);
			links[i] = links[i].substring(0, links[i].indexOf(">"));
		}
		
		//To fin the total number of pages in the issues repository
		String page = links[1].substring(links[1].indexOf("=") + 1);
		int pageNumber = Integer.parseInt(page);
		
		PrintWriter out = response.getWriter();
		
		String docType ="<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
		out.println(docType +"<html>\n" +
			                "<head><title>Result</title></head>\n" +
			                "<body bgcolor=\"#f0f0f0\">\n" +
			                "<h3 align=\"center\">" + "Processing pages </h3>\n"
			                +"</body></html>");
	
		
		try {
			
			http.sendGet(url, pageNumber, response);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// HTTP GET request
		private void sendGet(String url, int totalPages, HttpServletResponse response) throws Exception {

			//Initializing the open counts
			int totalOpenCount = 0;
			int twentyFourHourCount = 0;
			int sevenDayCount = 0;
			int moreThanSevenDayCount = 0;
			
			// Set response content type
		    response.setContentType("text/html");
		    
		    int page = 0;
			
			//loop to the every page to get the required counts
			for (page = 1 ; page <= totalPages; page++) {
							
				String newUrl = url + "?page=" + page; // creating dynamic urls for each page
							
				URL obj = new URL(newUrl);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				// optional default is GET
				con.setRequestMethod("GET");
			
				con.setRequestProperty("Accept", "application/json");
				
				//read the page loaded and put it into the response string
				BufferedReader in = new BufferedReader(
						new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer webResponse = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					webResponse.append(inputLine);
				}
				in.close();
			
				//Parse the response string and convert it into the JSON Array
				JSONArray jsonObj = (JSONArray)new JSONParser().parse(webResponse.toString());
				//print result		
			
				Iterator itr = jsonObj.iterator(); //Iterate over the JSON Array to get the required felds
			
				while (itr.hasNext()) {
				
					JSONObject jsonObject = (JSONObject) itr.next();
					
					//neglecting the pull_requests to count as an open issue
					if (jsonObject.get("pull_request") == null) {
					
						String time = (String)jsonObject.get("created_at"); // getting the created time of an issue
						
						//Formating the date string
						DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		            
						Date finalResult = df1.parse(time);
						
						TimeCalculator timeDiff = new TimeCalculator(); //calling the time function
		            
						if( timeDiff.calculate24HourTime().getTime() < finalResult.getTime()) {
							twentyFourHourCount++;
						}
						else if(timeDiff.calculateSevenDayTime().getTime() < finalResult.getTime() && timeDiff.calculate24HourTime().getTime() > finalResult.getTime()) {
							sevenDayCount++;
						}
						else {
							moreThanSevenDayCount++;
						}
					}
					
					
				}
			}
			//Print the counts
			totalOpenCount = twentyFourHourCount+sevenDayCount+moreThanSevenDayCount;
			
			PrintWriter out = response.getWriter();
			
			page -= 1;
			
			String docType ="<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
			out.println(docType +"<html>\n" +
				                "<head><title>Result</title></head>\n" +
				                "<body bgcolor=\"#f0f0f0\">\n"+
				                "<h3 align=\"center\">" + "Processed page " + page + " out of " + totalPages + "</h3>\n"
				                + "<table align=\"center\"><tr><td>" +
				                "Total Open Issues :</td><td>" + totalOpenCount + "</td></tr>" +
				                "<tr><td>" +
				                "Number of open issues that were opened in the last 24 hours :</td><td>"  + twentyFourHourCount + "</td></tr>" +
				                "<tr><td>" +
				                "Number of open issues that were opened more than 24 hours ago but less than 7 days ago :</td><td>" + sevenDayCount + "</td></tr>" +
				                "<tr><td>" +
				                "Number of open issues that were opened more than 7 days ago :</td><td>" + moreThanSevenDayCount + "</td></tr></table>" 
				                +"</body></html>");
			

		}
		
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			doGet(request, response);
		}
		
}
