import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

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
        } catch (IOException e) {
            log.error("Could not retrieve properties file", e);
        } catch (IllegalArgumentException e) {
            log.error("Malformed Unicode escape in the input", e);
        }

    }

    public int getInterval() {
        int returnValue = 1;
        if (prop.getProperty("update_interval") != null) {
            returnValue = Integer.parseInt(prop.getProperty("update_interval"));
        } else {
            log.debug("Interval can't be null! ");
            log.debug("Interval set to smallest possible positive number (integer).");
        }
        return returnValue;
    }

    public ArrayList<String> getCountries() {
        if (prop.getProperty("country_codes") != null) {
            String countries = prop.getProperty("country_codes");
            ArrayList<String> countryList = new ArrayList<>(Arrays.asList(countries.split("\\s*,\\s*")));
            return countryList;
        } else {
            log.debug("No filter found.");
            return null;
        }

    }
}
