import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

@Slf4j
public class ConfigParser {
    private final Properties prop = new Properties();;

    public ConfigParser(InputStream inputStream) {
        // prop = new Properties();
        try {
            prop.load(inputStream);
        } catch (IOException e) {
            log.error("Could not retrieve properties file", e);
        } catch (IllegalArgumentException e) {
            log.error("Malformed Unicode escape in the input", e);
        }

    }

    public double getInterval() {
        double returnValue = 1;
        if (prop.getProperty("update_interval") != null) {
            if (isNumeric(prop.getProperty("update_interval"))) {
                returnValue = Double.parseDouble(prop.getProperty("update_interval"));
            }
        } else {
            log.debug("Interval can't be null! ");
            log.debug("Interval set to smallest possible positive number (integer).");
        }
        return returnValue;
    }

    private static boolean isNumeric(String str) {
        return str != null && str.matches("[-+]?\\d*\\.?\\d+");
    }

    public ArrayList<String> getCountries() {
        if (prop.getProperty("country_codes") != null) {
            String countries = prop.getProperty("country_codes");
            ArrayList<String> countryList = new ArrayList<>(Arrays.asList(countries.split("\\s*,\\s*")));
            return removeEmpty(countryList);
        } else {
            log.debug("No filter found.");
            return null;
        }

    }

    private ArrayList<String> removeEmpty(ArrayList<String> list) {
        Iterator it = list.iterator();
        while(it.hasNext()) {
            String s = (String) it.next();
            if(s.isEmpty()) {
                it.remove();
            }
        }
        return list;
    }
}
