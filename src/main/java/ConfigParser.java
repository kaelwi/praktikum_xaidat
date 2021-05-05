import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

@Slf4j
public class ConfigParser {
    private Properties prop;

    public ConfigParser(InputStream inputStream) throws IOException {
        prop = new Properties();
        try {
            prop.load(inputStream);
        } catch (NullPointerException e) {
            log.error("Could not retrieve inputstream ", e);
        }

    }

    public int getInterval() {
        return Integer.parseInt(prop.getProperty("update_interval"));
    }

    public ArrayList<String> getCountries() {
        String countries = prop.getProperty("country_codes");
        ArrayList<String> countryList = new ArrayList<>(Arrays.asList(countries.split("\\s*,\\s*")));
        return countryList;
    }
}
