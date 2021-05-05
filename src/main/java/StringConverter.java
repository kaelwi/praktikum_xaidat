import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StringConverter {

    // private static final Logger logger = LoggerFactory.getLogger("stringconverter");

    private String content;

    public StringConverter(String content) {
        this.content = content;
    }

    public void setContent(String content) {
        // log.debug("setting content: {}",content);
        this.content = content;
    }

    public List<Country> convert() {
        // log.info("starting to convert elements");

        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
            JsonArray countries = jsonObject.getAsJsonArray("data");

            List<Country> countryList = new ArrayList<>();

            for (int i = 0; i < countries.size(); i++) {
                try {
                    JsonObject jobj = countries.get(i).getAsJsonObject();
                    String country = jobj.get("location").getAsString();
                    String countryCode = jobj.get("country_code").getAsString();
                    double latitude = jobj.get("latitude").getAsDouble();
                    double longitude = jobj.get("longitude").getAsDouble();
                    int confirmed = jobj.get("confirmed").getAsInt();
                    int dead = jobj.get("dead").getAsInt();
                    int recovered = jobj.get("recovered").getAsInt();
                    LocalDateTime updated = LocalDateTime.parse(jobj.get("updated").getAsString().substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    Country el = new Country(country, countryCode, latitude, longitude, confirmed, dead, recovered, updated);
                    // log.info("adding element: "+el);
                    // System.out.println("adding element: "+el);
                    countryList.add(el);
                }
                catch (NullPointerException e) {
                    // log.error("could not convert element at position " + i,e);
                    // System.out.println("NullPointerException at position " + i);
                }
        }
            return countryList;

    }

    public List<Country> convertWithJackson() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonArray countries = jsonObject.getAsJsonArray("data");
        List<Country> countryList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        for (int i = 0; i < countries.size(); i++) {
            String country = countries.get(i).toString();
            countryList.add(mapper.readValue(country, Country.class));
        }
        return countryList;


        /*JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        // JsonObject jsonObject1 = jsonObject.get("data").getAsJsonObject();
        JsonArray countries = jsonObject.getAsJsonArray("data");

        // String jsonArray = countries.getAsString();
        ObjectMapper mapper = new ObjectMapper();
        // return new ArrayList<Country>(1);
        // Country[] countries = mapper.readValue(content, Country[].class);
        CollectionType countryList = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Country.class);
        // List<MyClass> myObjects = mapper.readValue(jsonInput, mapper.getTypeFactory().constructCollectionType(List.class, MyClass.class));
        return mapper.readValue(countries.toString(), mapper.getTypeFactory().constructCollectionType(List.class, Country.class));
        // return mapper.readValue(String.valueOf(countries), countryList);
        // return mapper.readValue(jsonArray, new TypeReference<List<Country>>(){});*/
    }
}
