/*
 * @author pmpedrolima@gmail.com
 */

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

public class CheckList {

    private static int getLastPage() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("last_page_check.json"));
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
            BufferedWriter writer = new BufferedWriter(new FileWriter("last_page_check.json"));
            writer.write(Integer.toString(page));
            writer.close();
        } catch (IOException e) {
            // Handle IO exception
        }
    }

    private static void insertRoutesFromApi(String token) {
        String apiUrl = "https://api.vuupt.com/api/v1/checklists";
        String server = "Your server";
        String database = "Your database";
        String user = "Your username";
        String password = "Your password";

        try {
            Connection dbConnection = DriverManager.getConnection("jdbc:sqlserver://" + server + ";databaseName=" + database + ";user=" + user + ";password=" + password);
            Statement statement = dbConnection.createStatement();

            int page = getLastPage() + 1;

            while (true) {
                URL url = new URL(apiUrl + "?page=" + page);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = httpConnection.getResponseCode();

                if (responseCode == 200) {
                    // Your code for processing API response and inserting into the database goes here

                    // Update last processed page
                    storeLastPage(page);

                    page++;
                } else {
                    System.out.println("Error getting data from API.");
                    break;
                }
            }

            statement.close();
            dbConnection.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String token = "YOUR_ACCESS_TOKEN";
        insertRoutesFromApi(token);
    }
}