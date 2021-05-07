/**
 * Main part of the application, combining all other elements together and creating a Caduceus event.
 * <p>
 * Last modified: 07.05.2021
 * Author: Karoline Elisabeth Wild
 */

import com.xaidat.caduceus.Caduceus;
import com.xaidat.caduceus.Tags;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import com.xaidat.caduceus.Properties;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("usage: <path_to_config_file>");
            System.exit(1);
        }

        ConfigParser cp = new ConfigParser(new FileInputStream(args[0]));
        // IFetcher fetcher = new URLFetcher(new URL("https://www.trackcorona.live/api/countries"));
        IFetcher fetcher = new URLFetcher(cp.getURL());

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                String content = fetcher.fetch();
                List<Country> countryList = convertIntoCountries(content);
                Map<String, Country> countryMap = filter(cp, countryList);
                removeDuplicates(cp, countryMap);
                sendToCaduceus(countryMap);
            }
        };
        t.scheduleAtFixedRate(task, 0, (long) (cp.getInterval() * 60 * 1000));
    }

    @SneakyThrows
    static List<Country> convertIntoCountries(String content) {
        StringConverter stringConverter = new StringConverter(content);
        return stringConverter.convertWithJackson();
    }

    static Map<String, Country> filter(ConfigParser cp, List<Country> countries) {
        CountryMapper countryMapper = new CountryMapper();
        return countryMapper.getCountries(cp, countries);
    }

    static void removeDuplicates(ConfigParser cp, Map<String, Country> countryMap) {
        try {
            // remove already sent countries
            Map<String, Country> countriesToRemove = new HashMap<>();
            DBManager dbManager = new DBManager(cp.getDBLocation());
            // iterate over db and check if data has already been sent
            ResultSet resultSet = dbManager.createTable();
            if (!resultSet.next()) {
                dbManager.insertMap(dbManager.getConnection(), countryMap);
            } else {
                countriesToRemove = dbManager.updateOrInsertMap(dbManager.getConnection(), countryMap);
            }
            resultSet.close();
            dbManager.closeConnection(dbManager.getConnection());

            countryMap.keySet().removeAll(countriesToRemove.keySet());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendToCaduceus(Map<String, Country> countryMap) {
        /**
         * Send notification to Caduceus Server.
         *
         * @param category   The category of the notification.
         * @param subject    The subject that describes the content of the event.
         * @param body       The event's body containing all details of the event.
         * @param tags       An arbitrary number of tags.
         * @param properties An arbitrary number of properties associated with the event.
         */
        if (!countryMap.isEmpty()) {
            countryMap.forEach((k, v) -> {
                Map<String, String> props = new HashMap<>();
                props.put("countryCode", v.getCountryCode());
                props.put("latitude", String.valueOf(v.getLatitude()));
                props.put("longitude", String.valueOf(v.getLongitude()));
                props.put("confirmed", String.valueOf(v.getConfirmed()));
                props.put("dead", String.valueOf(v.getDead()));
                props.put("location", v.getLocation());
                props.put("recovered", String.valueOf(v.getRecovered()));
                props.put("updated", v.getUpdated().toString());
                Properties p = Properties.of(props);

                Caduceus.requireAgent().notify("COVID", "covid data " + v.getCountryCode(), Tags.of(v.getCountryCode()), p);
            });
        }
    }
}