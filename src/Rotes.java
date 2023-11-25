/*
 * @author pmpedrolima@gmail.com
 */

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.sql.Timestamp;  // Import Timestamp class
import java.util.concurrent.TimeUnit;  // Import TimeUnit class

public class Rotes {

    private static int getLastPage() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("last_page_rotas.json"));
            String line = reader.readLine();
            if (line != null) {
                return Integer.parseInt(line);
            }
        } catch (FileNotFoundException e) {
            // Handle file not found exception
        } catch (IOException e) {
            // Handle IO exception
        }
        return 0;
    }

    private static void storeLastPage(int page) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("last_page_rotas.json"));
            writer.write(Integer.toString(page));
            writer.close();
        } catch (IOException e) {
            // Handle IO exception
        }
    }

    private static Timestamp parseDateTime(String dateString) {
        if (dateString != null && !dateString.isEmpty()) {
            Timestamp timestamp = Timestamp.valueOf(dateString);
            timestamp.setTime(timestamp.getTime() - TimeUnit.HOURS.toMillis(3));  // Reduce 3 hours
            return timestamp;
        }
        return null;
    }

    private static void insertRoutesFromApi(String token) {
        String apiUrl = "https://api.vuupt.com/api/v1/routes";
        String server = "Your server";
        String database = "Your database";
        String user = "Your username";
        String password = "Your password";

        try {
            Connection dbConnection = DriverManager.getConnection("jdbc:sqlserver://" + server + ";databaseName=" + database + ";user=" + user + ";password=" + password);
            Statement statement = dbConnection.createStatement();

            int page = getLastPage() + 1;
            int lastProcessedPage = 0;

            while (true) {
                URL url = new URL(apiUrl + "?page=" + page);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = httpConnection.getResponseCode();

                if (responseCode == 200) {
                    // Your code for processing API response and inserting into the database goes here

                    // Update last processed page
                    lastProcessedPage = page;

                    page++;
                } else {
                    System.out.println("Error getting data from API. Status code: " + responseCode);
                    break;
                }
            }

            storeLastPage(lastProcessedPage);

            // Close the database connection
            statement.close();
            dbConnection.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String token = "";
        insertRoutesFromApi(token);
    }
}