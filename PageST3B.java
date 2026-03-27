package app;
 
import io.javalin.http.Context;
import io.javalin.http.Handler;
 
import java.util.ArrayList;
 
/**
* Example Index HTML class using Javalin
* <p>
* Generate a static HTML page using Javalin
* by writing the raw HTML into a Java String object
*
* @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
* @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
* @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
*/
 
public class PageST3B implements Handler {
 
    public static final String URL = "/page3B.html";
 
    @Override
    public void handle(Context context) throws Exception {
        String html = "<html>";
 
        html += "<head>" +
                "<title>Food Group Similarity</title>" +
                "<link rel='stylesheet' type='text/css' href='common.css' />" +
                "<style>" +
                ".flex-container { display: flex; flex-direction: row; justify-content: center; align-items: center; }" +
                ".flex-item { margin: 10px; }" +
                ".flex-item img { width: 400px; height: auto; }" +
                ".text-content { text-align: center; }" +
                ".text-content ul { list-style-position: inside; display: inline-block; text-align: left; }" +
                ".topnav { overflow: hidden; background-color: transparent; display: flex; align-items: center; }" +
                ".topnav img { height: 80px; margin-right: 10px; }" +
                ".topnav a { float: none; display: block; color: black; text-align: center; padding: 14px 16px; text-decoration: none; }" +
                ".topnav a:hover { background-color: #1e7e34; color: #ffffff; }" +
                ".topnav a.active { background-color: #1e7e34; color: #ffffff; font-weight: bold; }" +
                "</style>" +
                "</head>";
 
        html += "<body>";
 
        html += """
<div class='topnav'>
<a href='/'>
<img src='logo.png' alt='EcoBytes'>
</a>
<a href='/'>Homepage</a>
<a href='mission.html'>Our Mission</a>
<a href='/page2A.html'>Comparison by Country</a>
<a href='/page2B.html'>Comparison by Food</a>
<a href='/page3A.html'>Location Similarity</a>
<a href='/page3B.html' class='active'>Food Group Similarity</a>
</div>
        """;
 
        html += """
<div class='header'>
<h2>Food Group Similarity</h2>
</div>
        """;
 
        html += "<div class='content'>";
 
        // Handle form submission and display results
        if (context.method().equalsIgnoreCase("POST")) {
            String selectedCommodity = context.formParam("commodity");
            // Assuming JDBCConnection is properly implemented
            JDBCConnection jdbc = new JDBCConnection();
            ArrayList<String> commodities = jdbc.getFoodCommodities();
 
            html += "<h2>Selected Commodity: " + selectedCommodity + "</h2>";
 
            // Example of displaying all commodities
            html += "<h3>All Commodities:</h3>";
            for (String commodity : commodities) {
                html += "<p>" + commodity + "</p>";
            }
        } else {
            // Fetch food commodities and display form
            JDBCConnection jdbc = new JDBCConnection();
            ArrayList<String> commodities = jdbc.getFoodCommodities();
 
            html += "<form action='/page3B.html' method='post'>";
            html += "<label for='commodity'>Select a Commodity:</label>";
            html += "<select id='commodity' name='commodity'>";
            for (String commodity : commodities) {
                html += "<option value='" + commodity + "'>" + commodity + "</option>";
            }
            html += "</select>";
            html += "<input type='submit' value='Submit'>";
            html += "</form>";
        }
 
        html += "</div>";
 
        html += """
<div class='footer'>
<p>COSC2803 - Studio Project Starter Code (Apr24)</p>
<p>
<a href='/privacy'>Privacy Policy</a> |
<a href='/terms'>Terms of Service</a> |
<a href='/contact'>Contact Us</a>
</p>
</div>
        """;
 
        html += "</body></html>";
 
        context.html(html);
    }
}
