import com.xaidat.caduceus.Caduceus;
import com.xaidat.caduceus.Tags;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.*;

import com.xaidat.caduceus.Properties;

public class Main {

    // private static Map<String, Country> countrySent = new HashMap<>();

    public static void main(String[] args) throws IOException {

        ConfigParser cp = new ConfigParser(Main.class.getClassLoader().getResourceAsStream("config.properties"));
        IFetcher fetcher = fetcher(args);

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                String content = fetcher.fetch();

                //convertieren in java objekte
                StringConverter stringConverter = new StringConverter(content);
                List<Country> countriesWithJackson = stringConverter.convertWithJackson();

                // get filtered countries
                CountryMapper countryMapper = new CountryMapper();
                Map<String, Country> countryMap = countryMapper.getCountries(cp, countriesWithJackson);

                // remove already sent countries
                // Map<String, Country> countryToSend = countryMapper.checkSent(countrySent, countryMap);
                Map<String, Country> countriesToRemove = new HashMap<>();       // concurrentModificationException in iteration

                ResultSet rs = DBManager.createTable();

                if (!rs.next()) {
                    DBManager.insertMap(DBManager.getConnection(), countryMap);
                } else {
                    countriesToRemove = DBManager.updateOrInsertMap(DBManager.getConnection(), countryMap);
                }

                rs.close();
                DBManager.closeConnetcion();

/*                try {
                    Class.forName("org.h2.Driver");
                    Connection con = DriverManager.getConnection("jdbc:h2:~/test", "test", "");
                    Statement stmt = con.createStatement();
                    // stmt.executeUpdate( "DROP TABLE countries" );

                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS countries ( countryCode char(2) PRIMARY KEY," +
                            "location varchar(255), latitude decimal(9, 6), longitude decimal(10, 6), confirmed mediumint," +
                            "dead mediumint, recovered mediumint, updated timestamp )");

                    ResultSet rs = stmt.executeQuery("SELECT * FROM countries");

                    if (!rs.next()) {
                        countryMap.forEach((k, v) -> {
                            try {
                                // parameterised statement
                                // https://stackoverflow.com/questions/20781743/java-how-to-write-variable-into-h2-database
                                insertSQL(con, v);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        });
                    } else {
                        countryMap.forEach((k, v) -> {
                            try {
                                ResultSet compare = selectUpdatedSQL(con, v);

                                if (!compare.next()) {
                                    insertSQL(con, v);
                                } else {
                                    if (Timestamp.valueOf(v.getUpdated()).toInstant().isAfter(compare.getTimestamp("updated").toInstant())) {
                                        updateSQL(con, v);
                                    } else {
                                        countriesToRemove.put(k, v);
                                    }
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        });
                    }
                    stmt.close();
                    con.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }*/

                countryMap.keySet().removeAll(countriesToRemove.keySet());

                // send to caduceus COUNTRYMAP

                // sendToCaduceus(countryMap);

                //
                /**
                 * Send notification to Caduceus Server.
                 *
                 * @param category   The category of the notification.
                 * @param subject    The subject that describes the content of the event.
                 * @param body       The event's body containing all details of the event.
                 * @param tags       An arbitrary number of tags.
                 * @param properties An arbitrary number of properties associated with the event.
                 */
               /* countryToSend.forEach((k,v)->{
                        Map<String,String> props = new HashMap<>();
                    props.put("countryCode",v.getCountryCode());
                    props.put("latitude", String.valueOf(v.getLatitude()));
                    props.put("longitude", String.valueOf(v.getLongitude()));
                    props.put("confirmed", String.valueOf(v.getConfirmed()));
                    props.put("dead", String.valueOf(v.getDead()));
                    props.put("location",v.getLocation());
                    props.put("recovered", String.valueOf(v.getRecovered()));
                    props.put("updated",v.getUpdated().toString());
                    Properties p = Properties.of(props);

                    Caduceus.requireAgent().notify("COVID","covid data "+v.getCountryCode(), Tags.of(v.getCountryCode()),p);
                });*/
                // update list/map of sent countries
                // countrySent.putAll(countryMap);
            }
        };
        t.scheduleAtFixedRate(task, 0, (long) (cp.getInterval() * 60 * 1000));
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

    private static IFetcher fetcher(String[] args) {
        if (args.length > 0 && args[0].equals("file")) {
            return new FileFetcher();
        }
        try {
            return new URLFetcher(new URL("https://www.trackcorona.live/api/countries"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertSQL(Connection con, Country c) throws SQLException {
        PreparedStatement statement = con.prepareStatement("INSERT INTO countries (countryCode, location, latitude, longitude, " +
                "confirmed, dead, recovered, updated) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, c.getCountryCode());
        statement.setString(2, c.getLocation());
        statement.setDouble(3, c.getLatitude());
        statement.setDouble(4, c.getLongitude());
        statement.setInt(5, c.getConfirmed());
        statement.setInt(6, c.getDead());
        statement.setInt(7, c.getRecovered());
        statement.setTimestamp(8, Timestamp.valueOf(c.getUpdated()));

        statement.executeUpdate();
    }

    private static void updateSQL(Connection con, Country c) throws SQLException {
        PreparedStatement update = con.prepareStatement("UPDATE countries SET updated = ? WHERE countryCode = ?");
        update.setTimestamp(1, Timestamp.valueOf(c.getUpdated()));
        update.setString(2, c.getCountryCode());
        update.executeUpdate();
    }

    private static ResultSet selectUpdatedSQL(Connection con, Country c) throws SQLException {
        PreparedStatement preparedStatement = con.prepareStatement("SELECT updated FROM countries WHERE countryCode = ?");
        preparedStatement.setString(1, c.getCountryCode());
        return preparedStatement.executeQuery();
    }
}
