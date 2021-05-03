import java.io.IOException;
import java.net.URL;
import java.util.List;

/*

URL url = new URL("https://www.trackcorona.live/api/countries");
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


        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonArray countries = jsonObject.getAsJsonArray("data");

        List<Country> countryList = new ArrayList<>();

        for (int i = 0; i < countries.size(); i++) {
            JsonObject jobj = countries.get(i).getAsJsonObject();
            String country = jobj.get("location").getAsString();
            String countryCode = jobj.get("country_code").getAsString();
            double latitude = jobj.get("latitude").getAsDouble();
            double longitude = jobj.get("longitude").getAsDouble();
            int confirmed = jobj.get("confirmed").getAsInt();
            int dead = jobj.get("dead").getAsInt();
            int recovered = jobj.get("recovered").getAsInt();
            // String updated = jobj.get("updated").getAsString();
            LocalDateTime updated = LocalDateTime.parse(jobj.get("updated").getAsString().substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            countryList.add(new Country(country, countryCode, latitude, longitude, confirmed, dead, recovered, updated));
        }


*/
public class Main {
    public static void main(String[] args) throws IOException {

        // daten fetchen von https://www.trackcorona.live/api/countries
        URLFetcher urlFetcher = new URLFetcher(new URL("https://www.trackcorona.live/api/countries"));
        String content = urlFetcher.fetch();

        //convertieren in java objekte
        StringConverter stringConverter = new StringConverter(content);
        List<Country> countryList = stringConverter.convert();

        //senden an caduceus
    }

/*    private static String fetch() throws  IOException{
        URL url = new URL("https://www.trackcorona.live/api/countries");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        return getString(con);
    }*/

    /*private static List<Country> convert(String content){
        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonArray countries = jsonObject.getAsJsonArray("data");

        List<Country> countryList = new ArrayList<>();

        for (int i = 0; i < countries.size(); i++) {
            JsonObject jobj = countries.get(i).getAsJsonObject();
            String country = jobj.get("location").getAsString();
            String countryCode = jobj.get("country_code").getAsString();
            double latitude = jobj.get("latitude").getAsDouble();
            double longitude = jobj.get("longitude").getAsDouble();
            int confirmed = jobj.get("confirmed").getAsInt();
            int dead = jobj.get("dead").getAsInt();
            int recovered = jobj.get("recovered").getAsInt();
            // String updated = jobj.get("updated").getAsString();
            LocalDateTime updated = LocalDateTime.parse(jobj.get("updated").getAsString().substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            countryList.add(new Country(country, countryCode, latitude, longitude, confirmed, dead, recovered, updated));
        }
        return countryList;
    }*/
}
