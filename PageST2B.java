package app;

 

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.ResultSet;

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

 

public class PageST2B implements Handler {

 

    // URL of this page relative to http://localhost:7001/

    public static final String URL = "/page2B.html";

 

    @Override

    public void handle(Context context) throws Exception {

        JDBCConnection jdbc = new JDBCConnection();

 

        // Fetch the list of food groups

        ArrayList<String> foodGroups = jdbc.getFoodGroupNames();

 

        // Fetch the year range

        String yearRange = JDBCConnection.getYearRange();

        int startYear = Integer.parseInt(yearRange.split(" - ")[0]);

        int endYear = Integer.parseInt(yearRange.split(" - ")[1]);

 

        String html = "<html>";

 

        html = html + "<head>"

                + "<title>Comparison by Food Group</title>";

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

 

        html = html + "<body>";

 

        html = html + """

            <div class='topnav'>

                <a href='/'>

                    <img src='logo.png' alt='EcoBytes'>

                </a>

                <a href='/'>Homepage</a>

                <a href='mission.html'>Our Mission</a>

                <a href='/page2A.html'>Comparison by Country</a>

                <a href='/page2B.html' class='active'>Comparison by Food</a>

                <a href='/page3A.html'>Location Similarity</a>

                <a href='/page3B.html'>Food Group Similarity</a>

            </div>

        """;

 

        html = html + """

            <div class='header'>

                <h2>Comparison by Food Group</h2>

            </div>

        """;

 

        html = html + "<div class='content'>";

 

        // Form to select food groups, start year, end year, and filters

        html = html + "<form action='/page2B.html' method='post'>";

 

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

 

        // Food group selection

        html = html + "<div class='form-group'>";

        html = html + "<label for='foodGroup_drop'>Select Food Group:</label>";

        html = html + "<select id='foodGroup_drop' name='foodGroup_drop' multiple>";

        for (String foodGroup : foodGroups) {

            html = html + "<option>" + foodGroup + "</option>";

        }

        html = html + "</select>";

        html = html + "</div>";

 

        // Filters

        html = html + "<div class='form-group'>";

        html = html + "<label for='column_drop'>Select which column(s) to show:</label>";

        html = html + "<select id='column_drop' name='column_drop' multiple>";

        html = html + "<option>Activity</option>";

        html = html + "<option>Food Supply Stage</option>";

        html = html + "<option>Cause of Loss</option>";

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

        List<String> foodGroup_drop = context.formParams("foodGroup_drop");

        String startYear_drop = context.formParam("startYear");

        String endYear_drop = context.formParam("endYear");

        List<String> preference = context.formParams("column_drop");

        String order_drop = context.formParam("order_drop");

 

        // Handle form submission if it is a POST request

        if (startYear_drop != null && endYear_drop != null && !foodGroup_drop.isEmpty() && !preference.isEmpty()) {

            if (Integer.parseInt(endYear_drop) > Integer.parseInt(startYear_drop)) {

                html = html + outputData(startYear_drop, endYear_drop, foodGroup_drop, preference, order_drop);

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

 

    public String outputData(String start, String end, List<String> foodGroups, List<String> preference, String order) {

        String html = "<h2> Loss Percentage Analysis </h2>";

 

        ArrayList<String> lossPercentageDiff = getDiff(start, end, foodGroups, preference, order);

        html += "<table><tr>" +

                "<th>Food Group</th>" +

                "<th>Start Year</th>" +

                "<th>Start Loss Percentage</th>" +

                "<th>End Year</th>" +

                "<th>End Loss Percentage</th>" +

                "<th>Loss Change</th>";

 

        if (preference.contains("Activity")) {

            html += "<th>Activity</th>";

        }

        if (preference.contains("Food Supply Stage")) {

            html += "<th>Food Supply Stage</th>";

        }

        if (preference.contains("Cause of Loss")) {

            html += "<th>Cause of Loss</th>";

        }

        html += "</tr>";

        for (String info : lossPercentageDiff) {

            html += info;

        }

        html += "</table>";

 

        return html;

    }

 

    public ArrayList<String> getDiff(String start, String end, List<String> foodGroups, List<String> preference, String order) {

        ArrayList<String> diff = new ArrayList<>();

        ArrayList<String> naResults = new ArrayList<>();

   

        Connection connection = null;

        try {

            // Connect to JDBC data base

            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

   

            // Prepare a new SQL Query & Set a timeout

            java.sql.Statement statement = connection.createStatement();

            statement.setQueryTimeout(30);

   

            // Create the quotedFoodGroups string for the SQL IN clause

            StringBuilder quotedFoodGroupsBuilder = new StringBuilder();

            for (String foodGroup : foodGroups) {

                if (quotedFoodGroupsBuilder.length() > 0) {

                    quotedFoodGroupsBuilder.append(", ");

                }

                quotedFoodGroupsBuilder.append("'").append(foodGroup.replace("'", "''")).append("'");

            }

            String quotedFoodGroups = quotedFoodGroupsBuilder.toString();

   

            String selectColumns = "f.name, " +

                    "AVG(COALESCE(foodGroupLoss_start.loss_percentage, 0)) AS avgStartLoss, " +

                    "AVG(COALESCE(foodGroupLoss_end.loss_percentage, 0)) AS avgEndLoss, " +

                    "CASE WHEN AVG(COALESCE(foodGroupLoss_start.loss_percentage, 0)) = 0 THEN 0 " +

                    "ELSE (AVG(COALESCE(foodGroupLoss_end.loss_percentage, 0)) - AVG(COALESCE(foodGroupLoss_start.loss_percentage, 0)))/" +

                    "NULLIF(AVG(COALESCE(foodGroupLoss_start.loss_percentage, 0)),0) * 100 END AS lossChange";

            String joinConditions = " FROM FoodGroup f " +

                    "JOIN ReportedIn AS r ON f.id = r.foodGroupId " +

                    "LEFT JOIN foodGroupLoss foodGroupLoss_start ON f.id = foodGroupLoss_start.foodGroupId AND foodGroupLoss_start.year = " + start + " " +

                    "LEFT JOIN foodGroupLoss foodGroupLoss_end ON f.id = foodGroupLoss_end.foodGroupId AND foodGroupLoss_end.year = " + end;

            String whereConditions = " WHERE f.name IN (" + quotedFoodGroups + ")";

            String groupBy = " GROUP BY f.name, foodGroupLoss_start.activity, foodGroupLoss_start.stage, foodGroupLoss_start.causeOfLoss";

   

            if (preference.contains("Activity")) {

                selectColumns += ", foodGroupLoss_start.activity";

                joinConditions += " LEFT JOIN foodGroupLoss foodGroupLoss_start ON f.id = foodGroupLoss_start.foodGroupId AND foodGroupLoss_start.year = " + start + " AND foodGroupLoss_start.activity IS NOT NULL";

                groupBy += ", foodGroupLoss_start.activity";

            }

            if (preference.contains("Food Supply Stage")) {

                selectColumns += ", foodGroupLoss_start.stage";

                joinConditions += " LEFT JOIN foodGroupLoss foodGroupLoss_start ON f.id = foodGroupLoss_start.foodGroupId AND foodGroupLoss_start.year = " + start + " AND foodGroupLoss_start.stage IS NOT NULL";

                groupBy += ", foodGroupLoss_start.stage";

            }

            if (preference.contains("Cause of Loss")) {

                selectColumns += ", foodGroupLoss_start.causeOfLoss";

                joinConditions += " LEFT JOIN foodGroupLoss foodGroupLoss_start ON f.id = foodGroupLoss_start.foodGroupId AND foodGroupLoss_start.year = " + start + " AND foodGroupLoss_start.causeOfLoss IS NOT NULL";

                groupBy += ", foodGroupLoss_start.causeOfLoss";

            }

   

            String sqlQuery = "SELECT " + selectColumns + joinConditions + whereConditions + groupBy;

   

            // Order the results if specified

            if (order.equals("Ascending")) {

                sqlQuery += " ORDER BY lossChange ASC";

            } else if (order.equals("Descending")) {

                sqlQuery += " ORDER BY lossChange DESC";

            }

   

            // Fetch results

            ResultSet resultSet = statement.executeQuery(sqlQuery);

   

            while (resultSet.next()) {

                String foodGroupName = resultSet.getString("name");

                double startLoss = resultSet.getDouble("avgStartLoss");

                double endLoss = resultSet.getDouble("avgEndLoss");

                double lossChange = resultSet.getDouble("lossChange");

   

                StringBuilder rowBuilder = new StringBuilder();

                rowBuilder.append("<tr><td>").append(foodGroupName).append("</td>")

                        .append("<td>").append(start).append("</td>")

                        .append("<td>").append(startLoss).append("</td>")

                        .append("<td>").append(end).append("</td>")

                        .append("<td>").append(endLoss).append("</td>")

                        .append("<td>").append(lossChange).append("</td>");

   

                if (preference.contains("Activity")) {

                    String activity = resultSet.getString("activity");

                    rowBuilder.append("<td>").append(activity).append("</td>");

                }

                if (preference.contains("Food Supply Stage")) {

                    String stage = resultSet.getString("stage");

                    rowBuilder.append("<td>").append(stage).append("</td>");

                }

                if (preference.contains("Cause of Loss")) {

                    String cause = resultSet.getString("causeOfLoss");

                    rowBuilder.append("<td>").append(cause).append("</td>");

                }

   

                rowBuilder.append("</tr>");

                diff.add(rowBuilder.toString());

            }

   

        } catch (SQLException e) {

            System.err.println(e.getMessage());

        } finally {

            try {

                if (connection != null) {

                    connection.close();

                }

            } catch (SQLException e) {

                System.err.println(e.getMessage());

            }

        }

   

        // Handle cases where no results were found

        if (diff.isEmpty()) {

            StringBuilder noResultBuilder = new StringBuilder();

            noResultBuilder.append("<tr><td colspan='6'>No results found for the selected criteria.</td></tr>");

            diff.add(noResultBuilder.toString());

        }

   

        return diff;

    }

}
