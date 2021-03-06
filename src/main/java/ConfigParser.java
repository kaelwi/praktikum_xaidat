/**
 * A ConfigParser to parse the config and retrieve all necessary information. The config file includes
 * information about the interval (in which the fetching of data should be repeated), a country filter (in case
 * the user wants to get data for only some countries), the location of the database and the URL (where to
 * fetch the data).
 *
 * Last modified: 07.05.2021
 * Author: Karoline Elisabeth Wild
 */

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.*;

@Slf4j
public class ConfigParser {
    private final Properties prop = new Properties();

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

    /**
     * Get interval given in config file (in minutes)
     *
     * @return double interval in minutes
     */
    public double getInterval() {
        double returnValue = 1;
        if (prop.getProperty("update_interval") != null) {
            if (isNumeric(prop.getProperty("update_interval"))) {
                returnValue = Double.parseDouble(prop.getProperty("update_interval"));
            }
        } else {
            log.debug("Interval can't be null or negative! ");
            log.debug("Interval set to 1 minute.");
        }
        return returnValue;
    }

    private static boolean isNumeric(String str) {
        return str != null && str.matches("[+]?\\d*\\.?\\d+");
    }

    /**
     * Get URL (where data shall be fetched) from config file
     *
     * @return URL to data
     * @throws MalformedURLException
     */
    public URL getURL() throws MalformedURLException {
        if (prop.getProperty("covid_url") != null) {
            String url = prop.getProperty("covid_url");
            return new URL(url);
        } else {
            log.debug("No URL found!");
            return null;
        }
    }

    /**
     * Get location of the database from config file
     *
     * @return String location to DB
     */
    public String getDBLocation() {
        if (prop.getProperty("db_jdbc_url") != null) {
            return prop.getProperty("db_jdbc_url");
        } else {
            log.debug("Invalid DB location.");
            return null;
        }
    }

    /**
     * Get list of countries which data shall be fetched (filter) from config file
     *
     * @return List<String> of country codes.
     */
    public List<String> getCountries() {

        if (prop.getProperty("country_codes") != null) {
            String countries = prop.getProperty("country_codes");
            ArrayList<String> countryList = new ArrayList<>(Arrays.asList(countries.split("\\s*,\\s*")));
            return removeEmpty(countryList);
        } else {
            log.debug("No filter found.");
            return null;
        }
    }

    private List<String> removeEmpty(ArrayList<String> list) {
        return  list.stream()
                .filter(this::filterNonEmpty)
                .collect(Collectors.toList());
    }

    private boolean filterNonEmpty(String e){
            return !e.isEmpty();
    }


}
