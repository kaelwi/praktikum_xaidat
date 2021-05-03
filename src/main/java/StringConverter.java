import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StringConverter {
    protected String content;

    public StringConverter(String content) {
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Country> convert() {
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
            LocalDateTime updated = LocalDateTime.parse(jobj.get("updated").getAsString().substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            countryList.add(new Country(country, countryCode, latitude, longitude, confirmed, dead, recovered, updated));
        }

        return countryList;
    }
}
