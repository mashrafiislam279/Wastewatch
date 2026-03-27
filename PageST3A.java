package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
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

public class PageST3A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3A.html";

    @Override
    public void handle(Context context) throws Exception {
        JDBCConnection jdbc = new JDBCConnection();

        // Fetch the list of countries and regions
        ArrayList<String> names = jdbc.getCountryAndRegionNames();

        // Fetch the year range
        String yearRange = JDBCConnection.getYearRange();
        int startYear = Integer.parseInt(yearRange.split(" - ")[0]);
        int endYear = Integer.parseInt(yearRange.split(" - ")[1]);

        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Header information
        html = html + "<head>" + 
               "<title>Location Similarity</title>";

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
                <a href='mission.html'>Our Mission</a>
                <a href='page2A.html'>Comparison by Country</a>
                <a href='page2B.html'>Comparison by Food</a>
                <a href='/'page3A.html'' class='active'>Location Similarity</a>
                <a href='page3B.html'>Food Group Similarity</a>
            </div>
        """;

        // Add header content block
        html = html + """
            <div class='header'>
                <h2>Location Similarity</h2>
            </div>
        """;

        html = html + "<div class='content'>";

        // Form to select year, geographic location, and similarity criteria
        html = html + "<form action='/page3A.html' method='post'>";

        // Year selection
        html = html + "<label for='year'>Select Year:</label><br>";
        html = html + "<select id='year' name='year'>";
        for (int year = startYear; year <= endYear; year++) {
            html = html + "<option value='" + year + "'>" + year + "</option>";
        }
        html = html + "</select><br><br>";

        // Country/Region selection
        html = html + "<label for='location'>Select Country/Region:</label><br>";
        html = html + "<select id='location' name='location'>";
        for (String name : names) {
            html = html + "<option>" + name + "</option>";
        }
        html = html + "</select><br><br>";

        // Similarity criteria
        html = html + "<label for='similarity'>Select Similarity Criteria:</label><br>";
        html = html + "<select id='similarity' name='similarity'>";
        html = html + "<option value='foods'>Foods</option>";
        html = html + "<option value='percentage'>Percentage</option>";
        html = html + "<option value='both'>Both</option>";
        html = html + "</select><br><br>";

        // Similarity type
        html = html + "<label for='similarityType'>Select Similarity Type:</label><br>";
        html = html + "<select id='similarityType' name='similarityType'>";
        html = html + "<option value='absolute'>Absolute</option>";
        html = html + "<option value='overlap'>Overlap</option>";
        html = html + "</select><br><br>";

        // Number of results
        html = html + "<label for='numResults'>Number of Results:</label><br>";
        html = html + "<input type='number' id='numResults' name='numResults' min='1' max='100' value='5'><br><br>";

        html = html + "<button type='submit' class='btn btn-primary'>Submit</button>";
        html = html + "</form>";

        // Get user inputs
        String year = context.formParam("year");
        String location = context.formParam("location");
        String similarity = context.formParam("similarity");
        String similarityType = context.formParam("similarityType");
        String numResults = context.formParam("numResults");

        // Handle form submission if it is a POST request
        if (year != null && location != null && similarity != null && similarityType != null && numResults != null) {
            html = html + outputData(year, location, similarity, similarityType, Integer.parseInt(numResults));
        } else {
            html = html + "<h3><b>Please fill in all fields</b></h3>";
        }

        html = html + "</div>";

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

        html = html + "</body></html>";

        context.html(html);
    }

    public String outputData(String year, String location, String similarity, String similarityType, int numResults) {
        String html = "<h2> Similar Locations Analysis </h2>";

        ArrayList<String> similarLocations = getSimilarLocations(year, location, similarity, similarityType, numResults);
        html += "<table><tr>" +
                "<th>Country</th>" +
                "<th>Year</th>" +
                "<th>Similarity Score</th>";

        if (similarity.equals("foods") || similarity.equals("both")) {
            html += "<th>Common Foods</th>";
        }
        if (similarity.equals("percentage") || similarity.equals("both")) {
            html += "<th>Loss Percentage</th>";
        }
        html += "</tr>";
        for (String info : similarLocations) {
            html += info;
        }
        html += "</table>";

        return html;
    }

    public ArrayList<String> getSimilarLocations(String year, String location, String similarity, String similarityType, int numResults) {
        ArrayList<String> similarLocations = new ArrayList<>();

        Connection connection = null;
        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            
            java.sql.Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // Fetch the data
            String query = "SELECT c.name, AVG(cf.loss_percentage) as avgLossPercentage, " +
                    "GROUP_CONCAT(DISTINCT co.commodityName) as commonFoods, COUNT(DISTINCT co.commodityName) as commonFoodsCount, " +
                    "AVG(CASE WHEN c.name = '" + location + "' THEN cf.loss_percentage ELSE 0 END) as selectedAvgLossPercentage, " +
                    "GROUP_CONCAT(DISTINCT CASE WHEN c.name = '" + location + "' THEN co.commodityName ELSE NULL END) as selectedCommonFoods, " +
                    "COUNT(DISTINCT CASE WHEN c.name = '" + location + "' THEN co.commodityName ELSE NULL END) as selectedCommonFoodsCount " +
                    "FROM Country c " +
                    "JOIN countryFoodloss cf ON c.m49code = cf.m49code " +
                    "JOIN ReportedIn ri ON c.m49code = ri.m49code " +
                    "JOIN Commodity co ON ri.CPCcode = co.CPCcode " +
                    "WHERE cf.year = " + year + " " +
                    "GROUP BY c.name";
            System.out.println("Executing query: " + query);
            ResultSet results = statement.executeQuery(query);

            double locationAvgLossPercentage = 0;
            int locationCommonFoodsCount = 0;
            while (results.next()) {
                String countryName = results.getString("name");
                double avgLossPercentage = results.getDouble("avgLossPercentage");
                String commonFoods = results.getString("commonFoods");
                int commonFoodsCount = results.getInt("commonFoodsCount");

                if (countryName.equals(location)) {
                    locationAvgLossPercentage = avgLossPercentage;
                    locationCommonFoodsCount = commonFoodsCount;
                    continue;
                }

                double score = 0.0;
                if (similarity.equals("percentage") || similarity.equals("both")) {
                    score += Math.abs(locationAvgLossPercentage - avgLossPercentage);
                }
                if (similarity.equals("foods") || similarity.equals("both")) {
                    score += Math.abs(locationCommonFoodsCount - commonFoodsCount);
                }

                String details = "<tr>" +
                        "<td>" + countryName + "</td>" +
                        "<td>" + year + "</td>" +
                        "<td>" + String.format("%.2f", score) + "</td>";
                if (similarity.equals("foods") || similarity.equals("both")) {
                    details += "<td>" + commonFoods + "</td>";
                }
                if (similarity.equals("percentage") || similarity.equals("both")) {
                    details += "<td>" + String.format("%.2f", avgLossPercentage) + "%</td>";
                }
                details += "</tr>";

                similarLocations.add(details);
            }

            // Limit the results based on numResults
            if (numResults < similarLocations.size()) {
                similarLocations = new ArrayList<>(similarLocations.subList(0, numResults));
            }

            statement.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLDataException e) {
                System.err.println(e.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return similarLocations;
    }
}
