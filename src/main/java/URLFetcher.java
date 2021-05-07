/**
 * The URL Fetcher class fetches the data from an URL and returns it as a String.
 *
 * Last modified: 07.05.2021
 * Author: Karoline Elisabeth Wild
 */

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class URLFetcher implements  IFetcher {

    private final URL url;

    public URLFetcher(URL url) {
        // log.debug(String.valueOf(url));
        this.url = url;
    }

    /**
     * Get data from URL as String
     *
     * @return getString() of fetched data
     */
    public String fetch() {
        return getString();
    }

    private String getString() {

        BufferedReader in = null;
        HttpURLConnection con = null;
        String result = "";
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();

            if (status > 299) {
                in = new BufferedReader(
                        new InputStreamReader(con.getErrorStream()));
            } else {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                // log.debug("Status {}", status);
            }

            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            result = content.toString();
            //throws excpetion if code > 299
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(in);
            if (con != null) {
                con.disconnect();
            }
        }

        return result;
    }

    private static void closeStream(BufferedReader in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
