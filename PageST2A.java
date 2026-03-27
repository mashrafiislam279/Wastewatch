package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

public class PageST2A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page2A.html";

    @Override
    public void handle(Context context) throws Exception {
        JDBCConnection jdbc = new JDBCConnection();

        // Fetch the list of countries
        ArrayList<String> country = jdbc.getCountryName();

        // Fetch the year range
        String yearRange = JDBCConnection.getYearRange();
        int startYear = Integer.parseInt(yearRange.split(" - ")[0]);
        int endYear = Integer.parseInt(yearRange.split(" - ")[1]);

        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Header information
        html = html + "<head>" + 
               "<title>Comparison by Country</title>";

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
                <a href='/page2A.html' class='active'>Comparison by Country</a>
                <a href='/page2B.html'>Comparison by Food</a>
                <a href='/page3A.html'>Location Similarity</a>
                <a href='/page3B.html'>Food Group Similarity</a>
            </div>
        """;

        // Add header content block
        html = html + """
            <div class='header'>
                <h2>Comparison by Country</h2>
            </div>
        """;

        html = html + "<div class='content'>";

        // Form to select countries, start year, end year, and filters
        html = html + "<form action='/page2A.html' method='post'>";

        // Year selection
        html = html + "<label for='startYear'>Select Start Year:</label><br>";
        html = html + "<select id='startYear' name='startYear'>";
        for (int year = startYear; year <= endYear; year++) {
            html = html + "<option value='" + year + "'>" + year + "</option>";
        }
        html = html + "</select><br><br>";

        html = html + "<label for='endYear'>Select End Year:</label><br>";
        html = html + "<select id='endYear' name='endYear'>";
        for (int year = startYear; year <= endYear; year++) {
            html = html + "<option value='" + year + "'>" + year + "</option>";
        }
        html = html + "</select><br><br>";

        // Country selection
        html = html + "<div class='form-group'>";
        html = html + "<label for='countryname_drop'>Select Country:</label>";
        html = html + "<select id='countryname_drop' name='countryname_drop' multiple>";
        for (String countryName : country) {
            html = html + "<option>" + countryName + "</option>";
        }
        html = html + "</select>";
        html = html + "</div>";

        // Filters
        html = html + "<div class='form-group'>";
        html = html + "<label for='column_drop'>Select which column(s) to show:</label>";
        html = html + "<select id='column_drop' name='column_drop' multiple>";
        html = html + "<option>Commodity</option>";
        html = html + "<option>Activity</option>";
        html = html + "<option>Supply Stage</option>";
        html = html + "<option>Cause</option>";
        html = html + "</select>";
        html = html + "</div>";

        // Order
        html = html + "<div class='form-group'>";
        html = html + "<label for='order_drop'>Select the order:</label>";
        html = html + "<select id='order_drop' name='order_drop'>";
        html = html + "<option>Ascending</option>";
        html = html + "<option>Descending</option>";
        html = html + "</select>";
        html = html + "</div>";

        html = html + "<button type='submit' class='btn btn-primary'>Submit</button>";
        html = html + "</form>";

        // Get user inputs
        List<String> countryname_drop = context.formParams("countryname_drop");
        String startYear_drop = context.formParam("startYear");
        String endYear_drop = context.formParam("endYear");
        List<String> preference = context.formParams("column_drop");
        String order_drop = context.formParam("order_drop");

        // Handle form submission if it is a POST request
        if (startYear_drop != null && endYear_drop != null && !countryname_drop.isEmpty() && !preference.isEmpty()) {
            if (Integer.parseInt(endYear_drop) > Integer.parseInt(startYear_drop)) {
                html = html + outputData(startYear_drop, endYear_drop, countryname_drop, preference, order_drop);
            } else {
                html = html + "<h3><b>Select valid years (make sure end year is bigger than start year)</b></h3>";
            }
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

    public String outputData(String start, String end, List<String> countries, List<String> preference, String order) {
        String html = "<h2> Loss Percentage Analysis </h2>";

        ArrayList<String> lossPercentageDiff = getDiff(start, end, countries, preference, order);
        html += "<table><tr>" +
                "<th>Country</th>" +
                "<th>Start Year</th>" +
                "<th>Start Loss Percentage</th>" +
                "<th>End Year</th>" +
                "<th>End Loss Percentage</th>" +
                "<th>Loss Change</th>";

        if (preference.contains("Commodity")) {
            html += "<th>Commodity</th>";
        }
        if (preference.contains("Activity")) {
            html += "<th>Activity</th>";
        }
        if (preference.contains("Supply Stage")) {
            html += "<th>Supply Stage</th>";
        }
        if (preference.contains("Cause")) {
            html += "<th>Cause</th>";
        }
        html += "</tr>";
        for (String info : lossPercentageDiff) {
            html += info;
        }
        html += "</table>";

        return html;
    }

    public ArrayList<String> getDiff(String start, String end, List<String> countries, List<String> preference, String order) {
        ArrayList<String> diff = new ArrayList<>();
        ArrayList<String> naResults = new ArrayList<>();
    
        Connection connection = null;
        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);
    
            // Prepare a new SQL Query & Set a timeout
            java.sql.Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
    
            // Create the quotedCountries string for the SQL IN clause
            StringBuilder quotedCountriesBuilder = new StringBuilder();
            for (String country : countries) {
                if (quotedCountriesBuilder.length() > 0) {
                    quotedCountriesBuilder.append(", ");
                }
                quotedCountriesBuilder.append("'").append(country.replace("'", "''")).append("'");
            }
            String quotedCountries = quotedCountriesBuilder.toString();
    
            String selectColumns = "c.name, " +
                    "AVG(COALESCE(countryFoodloss_start.loss_percentage, 0)) AS avgStartLoss, " +
                    "AVG(COALESCE(countryFoodloss_end.loss_percentage, 0)) AS avgEndLoss, " +
                    "CASE WHEN AVG(COALESCE(countryFoodloss_start.loss_percentage, 0)) = 0 THEN 0 " +
                    "ELSE (AVG(COALESCE(countryFoodloss_end.loss_percentage, 0)) - AVG(COALESCE(countryFoodloss_start.loss_percentage, 0)))/" +
                    "NULLIF(AVG(COALESCE(countryFoodloss_start.loss_percentage, 0)),0) * 100 END AS lossChange";
            String joinConditions = " FROM Country c " +
                    "JOIN ReportedIn AS r ON c.m49code = r.m49code " +
                    "JOIN Commodity AS co ON r.CPCcode = co.CPCcode " +
                    "LEFT JOIN countryFoodloss countryFoodloss_start ON c.m49code = countryFoodloss_start.m49code AND countryFoodloss_start.year = " + start + " " +
                    "LEFT JOIN countryFoodloss countryFoodloss_end ON c.m49code = countryFoodloss_end.m49code AND countryFoodloss_end.year = " + end;
            String whereConditions = " WHERE c.name IN (" + quotedCountries + ")";
            String groupBy = " GROUP BY c.name, co.commodityName, countryFoodloss_start.activity, countryFoodloss_start.supplyStage, countryFoodloss_start.cause";
    
            if (preference.contains("Commodity")) {
                selectColumns += ", co.commodityName";
            }
            if (preference.contains("Activity")) {
                selectColumns += ", countryFoodloss_start.activity";
            }
            if (preference.contains("Supply Stage")) {
                selectColumns += ", countryFoodloss_start.supplyStage";
            }
            if (preference.contains("Cause")) {
                selectColumns += ", countryFoodloss_start.cause";
            }
    
            String query = "SELECT " + selectColumns + joinConditions + whereConditions + groupBy + " ORDER BY lossChange " + (order.equalsIgnoreCase("Ascending") ? "ASC" : "DESC");
    
            System.out.println("Executing query: " + query);
    
            // Get result
            ResultSet results = statement.executeQuery(query);
            // Process all of the results
            while (results.next()) {
                String details = "<tr>" +
                        "<td>" + results.getString("name") + "</td>" +
                        "<td>" + start + "</td>" +
                        "<td>" + (results.getString("avgStartLoss") == null || results.getString("avgStartLoss").equals("0.0") ? "No data for " + start : results.getString("avgStartLoss")) + "</td>" +
                        "<td>" + end + "</td>" +
                        "<td>" + (results.getString("avgEndLoss") == null || results.getString("avgEndLoss").equals("0.0") ? "No data for " + end : results.getString("avgEndLoss")) + "</td>" +
                        "<td>" + (results.getString("avgStartLoss").equals("0.0") || results.getString("avgEndLoss").equals("0.0") ? "N/A" : results.getDouble("lossChange")) + "</td>";
    
                if (preference.contains("Commodity")) {
                    details += "<td>" + (results.getString("commodityName") == null || results.getString("commodityName").equals("") ? "No Commodity" : results.getString("commodityName")) + "</td>";
                }
                if (preference.contains("Activity")) {
                    details += "<td>" + (results.getString("activity") == null || results.getString("activity").equals("") ? "No activity" : results.getString("activity")) + "</td>";
                }
                if (preference.contains("Supply Stage")) {
                    details += "<td>" + (results.getString("supplyStage") == null || results.getString("supplyStage").equals("") ? "No stage" : results.getString("supplyStage")) + "</td>";
                }
                if (preference.contains("Cause")) {
                    details += "<td>" + (results.getString("cause") == null || results.getString("cause").equals("") ? "No cause" : results.getString("cause")) + "</td>";
                }
    
                details += "</tr>";
                if (details.contains("N/A")) {
                    naResults.add(details);
                } else {
                    diff.add(details);
                }
            }
            // Add N/A results at the end
            diff.addAll(naResults);
    
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
        return diff;
    }
}
