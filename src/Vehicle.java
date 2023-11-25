/*
* @author pmpedrolima@gmail.com
 */

import java.sql.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Vehicle {

    private static Timestamp parseDateTime(String dateString) {
        if (dateString != null && !dateString.isEmpty()) {
            Timestamp timestamp = Timestamp.valueOf(dateString);
            timestamp.setTime(timestamp.getTime() - TimeUnit.HOURS.toMillis(3));  // Reduce 3 hours
            return timestamp;
        }
        return null;
    }

    private static void insertVehiclesFromApi(String token) {
        String apiUrl = "https://api.vuupt.com/api/v1/vehicles";  // Update with the correct API endpoint
        String server = "Your Server";
        String database = "Your Database";
        String username = "Your User ID";
        String password = "Your Password";

        try {
            // JDBC Connection
            String connectionString = "jdbc:sqlserver://" + server + ";databaseName=" + database +
                    ";user=" + username + ";password=" + password;
            Connection conn = DriverManager.getConnection(connectionString);

            // Statement
            Statement stmt = conn.createStatement();

            int page_number = 1;

            while (true) {
                // HTTP GET request to the API
                URL url = new URL(apiUrl + "?page=" + page_number);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);

                // Check API response
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the API response
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Parse JSON response and insert into the database
                    // TODO: Implement JSON parsing and database insertion

                    page_number++;

                } else {
                    // Handle API error
                    System.out.println("Error retrieving data from the API. Status code: " + responseCode);
                    break;
                }
            }

            // Close the database connection
            stmt.close();
            conn.close();

        } catch (SQLException | IOException e) {
            // Handle exceptions
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Replace the empty string with your actual access token
        insertVehiclesFromApi("");
    }
}
