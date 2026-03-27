package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Class for Managing the JDBC Connection to a SQLLite Database.
 * Allows SQL queries to be used with the SQLLite Database in Java.
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class JDBCConnection {

    // Name of database file (contained in database folder)
    public static final String DATABASE = "jdbc:sqlite:database/foodloss.db";
    private static double maxLossPercentage;

    /**
     * This creates a JDBC Object so we can keep talking to the database
     */
    public JDBCConnection() {
        System.out.println("Created JDBC Connection Object");
    }

    /**
     * Get all of the Countries in the database.
     * @return
     *    Returns an ArrayList of Country objects
     */
    public ArrayList<String> getCountryName() {
        // Create the ArrayList of Country objects to return
        ArrayList<String> countryName = new ArrayList<String>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT name FROM Country";
            System.err.println(query);
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need
                
                String country  = results.getString("name");

                // Add the Country object to the array
                countryName.add(country);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the countries
        return countryName;
    }

    public static String getYearRange() {
        String yearRange = "";
        try (Connection connection = DriverManager.getConnection(DATABASE);
             Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30);
            String query = "SELECT MIN(year) AS startYear, MAX(year) AS endYear FROM groupFoodLoss";
            ResultSet results = statement.executeQuery(query);
            if (results.next()) {
                int startYear = results.getInt("startYear");
                int endYear = results.getInt("endYear");
                yearRange = startYear + " - " + endYear;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return yearRange;
    }

    public static double getMaxSingleYearLossPercentage() {
        try (Connection connection = DriverManager.getConnection(DATABASE);
             Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30);
            String query = "SELECT MAX(loss_percentage) as maxLoss FROM countryFoodloss;";
            ResultSet results = statement.executeQuery(query);
            if (results.next()) {
                maxLossPercentage = results.getDouble("maxLoss");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxLossPercentage;
    }

    public ArrayList<String> getCommoditiesWithMaxLoss() {
        ArrayList<String> commodities = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DATABASE);
             Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30);

            String commodityQuery = "SELECT DISTINCT Commodity.commodityName FROM Commodity INNER JOIN groupFoodloss ON Commodity.CPCcode=groupFoodloss.CPCcode WHERE groupFoodloss.loss_percentage = (SELECT MAX(loss_percentage) FROM countryFoodloss)";
            ResultSet results = statement.executeQuery(commodityQuery);

            while (results.next()) {
                String commodity = results.getString("commodityName");
                System.out.println("Commodity with Max Loss: " + commodity);
                commodities.add(commodity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commodities;
    }

    public ArrayList<String[]> getPersonas() {
        ArrayList<String[]> personas = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DATABASE);
             Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30);
            String query = "SELECT name, image FROM Persona";
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String name = results.getString("name");
                String image = results.getString("image");
                personas.add(new String[]{name, image});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return personas;
    }

    public ArrayList<String[]> getStudents() {
        ArrayList<String[]> students = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DATABASE);
             Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30);
            String query = "SELECT studentId, name FROM Student";
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String studentId = results.getString("studentId");
                String name = results.getString("name");
                students.add(new String[]{studentId, name});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public ArrayList<String[]> getFoodLossData(int startYear, int endYear, String[] fields, String order) {
        ArrayList<String[]> data = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DATABASE);
             Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30);

            // Build the query based on input parameters
            String query = "SELECT Commodity.commodityName, " +
                           "AVG(groupFoodloss.loss_percentage) as avgLoss " +
                           "FROM groupFoodloss " +
                           "JOIN Commodity ON groupFoodloss.CPCcode = Commodity.CPCcode " +
                           "WHERE groupFoodloss.year BETWEEN " + startYear + " AND " + endYear + " ";

            if (fields.length > 0) {
                query += "GROUP BY " + String.join(", ", fields) + " ";
            }

            query += "ORDER BY avgLoss " + order;

            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String commodityName = results.getString("commodityName");
                String avgLoss = results.getString("avgLoss");
                data.add(new String[]{commodityName, avgLoss});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public ArrayList<String> getCountryAndRegionNames() {
        ArrayList<String> names = new ArrayList<>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Query to get all country names
            String queryCountry = "SELECT name FROM Country";
            ResultSet resultsCountry = connection.createStatement().executeQuery(queryCountry);
            while (resultsCountry.next()) {
                names.add(resultsCountry.getString("name"));
            }

            // Query to get all region names
            String queryRegion = "SELECT regionName FROM Region";
            ResultSet resultsRegion = connection.createStatement().executeQuery(queryRegion);
            while (resultsRegion.next()) {
                names.add(resultsRegion.getString("regionName"));
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

        return names;
    }

    // New methods for Sub-Task B

    public ArrayList<String> getFoodCommodities() {
        ArrayList<String> commodities = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String query = "SELECT DISTINCT commodityName FROM Commodity";
            ResultSet results = statement.executeQuery(query);
            while (results.next()) {
                commodities.add(results.getString("commodityName"));
            }
            statement.close();
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
        return commodities;
    }

    public String getFoodGroup(String commodityName) {
        String foodGroup = "";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String query = "SELECT foodGroup FROM Commodity WHERE commodityName = '" + commodityName + "'";
            ResultSet results = statement.executeQuery(query);
            if (results.next()) {
                foodGroup = results.getString("foodGroup");
            }
            statement.close();
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
        return foodGroup;
    }

    public ArrayList<String> getFoodGroupNames() {
        ArrayList<String> foodGroups = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String query = "SELECT DISTINCT foodGroup FROM Commodity";
            ResultSet results = statement.executeQuery(query);
            while (results.next()) {
                foodGroups.add(results.getString("foodGroup"));
            }
            statement.close();
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
        return foodGroups;
    }
}

