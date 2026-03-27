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
 * @autor Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @autor Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @autor Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */
 
public class PageMission implements Handler {
 
    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/mission.html";
 
    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";
 
        // Add some Head information
        html = html + "<head>" + 
               "<title>Our Mission</title>";
 
        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
        html = html + "<style>" +
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
                      "</style>";
        html = html + "</head>";
 
        // Add the body
        html = html + "<body>";
 
        // Add the topnav
        html = html + """
<div class='topnav'>
<a href='/'>
<img src='logo.png' alt='EcoBytes'>
</a>
<a href='/'>Homepage</a>
<a href='mission.html' class='active'>Our Mission</a>
<a href='/page2A.html'>Comparison by Country</a>
<a href='/page2B.html'>Comparison by Food</a>
<a href='/page3A.html'>Location Similarity</a>
<a href='/page3B.html'>Food Group Similarity</a>
</div>
        """;
 
        // Add header content block
        html = html + """
<div class='header'>
<h2>Our Mission</h2>
</div>
        """;
 
        html = html + "<div class='content'>";
 
        // Add HTML for the page content
        html = html + """
<p>Our website is dedicated to addressing the global challenge of food loss by providing
            data-driven insights and solutions. Users can explore various metrics and information 
            related to food loss across different countries and commodities.</p>
 
            <p>This platform can be used by researchers, policymakers, and individuals who are interested
            in understanding and reducing food loss.</p>
        """;
 
        // Retrieve personas and students from the database
        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<String[]> personas = jdbc.getPersonas();
        ArrayList<String[]> students = jdbc.getStudents();
 
        // Add personas section
        html = html + "<h2>Target Personas</h2><ul>";
        for (String[] persona : personas) {
            html = html + "<li><b>" + persona[0] + ":</b> <img src='" + persona[1] + "' alt='" + persona[0] + "'></li>";
        }
        html = html + "</ul>";
 
        // Add students section
        html = html + "<h2>Team Members</h2><ul>";
        for (String[] student : students) {
            html = html + "<li>" + student[0] + " - " + student[1] + "</li>";
        }
        html = html + "</ul>";
 
        // Close Content div
        html = html + "</div>";
 
        // Footer
        html = html + """
<div class='footer'>
<p>COSC2803 - Studio Project Starter Code (Apr24)</p>
<p>
<a href='/privacy'>Privacy Policy</a> |
<a href='/terms'>Terms of Service</a> |
<a href='/contact'>Contact Us</a>
</p>
</div>
        """;
 
        // Finish the HTML webpage
        html = html + "</body>" + "</html>";
 
        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }
}