import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLFetcher {
    protected URL url;

    public URLFetcher(URL url) {
        this.url = url;
    }

    public String fetch() throws IOException {
        return getString();
    }

    private String getString() throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        int status = con.getResponseCode();

        BufferedReader in = null;

        if (status > 299) {
            in = new BufferedReader(
                    new InputStreamReader(con.getErrorStream()));
        } else {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        }

        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        con.disconnect();

        return content.toString();
    }

}
