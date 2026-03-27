package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;

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

public class PageIndex implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/";
    @Override
    public void handle(Context context) throws Exception {
        // Instantiate JDBCConnection
        JDBCConnection jdbc = new JDBCConnection();
        // Get the year range from the database
        String yearRange = JDBCConnection.getYearRange();
        double maxLossPercentage = JDBCConnection.getMaxSingleYearLossPercentage();
        ArrayList<String> commoditiesWithMaxLoss = jdbc.getCommoditiesWithMaxLoss();

        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Header information
        html = html + "<head>" + 
               "<title>Homepage</title>";

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
                <a href='/' class='active'>Homepage</a>
                <a href='mission.html'>Our Mission</a>
                <a href='page2A.html'>Comparison by Country</a>
                <a href='page2B.html'>Comparison by Food</a>
                <a href='page3A.html'>Location Similarity</a>
                <a href='page3B.html'>Food Group Similarity</a>
            </div>
        """;

        // Add header content block
        html = html + """
            <div class='header'>
                <h2>Homepage</h2>
            </div>
        """;

        // Add Div for page Content
        html = html + "<div class='content'>";

        // Add HTML for the page content
        html = html + """
            <div class='flex-container'>
                <div class='flex-item'>
                    <img src='foodloss.jpeg' alt='Food Loss'>
                </div>
                <div class='flex-item'>
                    <img src='pic.png' alt='Pic'>
                </div>
            </div>
            <div class='text-content'>
                <h1>Welcome to EcoBytes</h1>
                <h2>Championing the Fight Against Global Food Waste</h2>
                <p>Discover vital insights and innovative solutions to combat food loss and waste worldwide. EcoBytes is your go-to portal for data-driven analyses, high-level summaries, and actionable strategies to reduce food waste and promote sustainability. Join us in making a difference for our planet and future generations.</p>
                <p><strong>Discover data on food loss percentages, trends over the years, and detailed analysis by commodities and countries-</strong></p>
        """;
        
        // Insert the year range from the database
        html = html + "<p><strong>Year Range:</strong> " + yearRange + "</p>";
        html = html + "<p><strong>Maximum Single Year Loss Percentage:</strong> " + maxLossPercentage + "%</p>";
        html = html + "<p><strong>Commodities with Maximum Loss:</strong></p>";
        html = html + "<ul>";
        for (String commodity : commoditiesWithMaxLoss) {
            html = html + "<li>" + commodity + "</li>";
        }
        html = html + "</ul></div>"; // Close the text-content div

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
