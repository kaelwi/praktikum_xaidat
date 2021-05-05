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

    private static HashMap<String, Country> countrySent = new HashMap<>();

    public static void main(String[] args) throws IOException {

        ConfigParser cp = new ConfigParser(Main.class.getClassLoader().getResourceAsStream("config.properties"));
        IFetcher fetcher =  fetcher(args);

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
                HashMap<String, Country> countryMap = countryMapper.getCountries(cp, countriesWithJackson);

                // remove already sent countries
                HashMap<String, Country> countryToSend = countryMapper.checkSent(countrySent, countryMap);

                try
                {
                    Class.forName("org.h2.Driver");
                    Connection con = DriverManager.getConnection("jdbc:h2:~/test", "test", "" );
                    // Connection con = DriverManager.getConnection("jdbc:h2:file:C:/Users/karol/Documents/xaidat/test", "test", "");
                    System.out.println("connection");
                    Statement stmt = con.createStatement();
                    //stmt.executeUpdate( "DROP TABLE table1" );

                    stmt.executeUpdate( "CREATE TABLE IF NOT EXISTS countries ( countryCode char(2) PRIMARY KEY," +
                            "location varchar(255), latitude decimal(9, 6), longitude decimal(10, 6), confirmed mediumint," +
                            "dead mediumint, recovered mediumint, updated varchar(50))");

                    ResultSet rs = stmt.executeQuery("SELECT * FROM countries");

                    System.out.println("first select");

                    if (!rs.next()) {
                        System.out.println("rs empty");
                        countryToSend.forEach((k, v)-> {
                            try {
                                stmt.executeUpdate("INSERT INTO countries (countryCode, location, latitude, longitude, " +
                                        "confirmed, dead, recovered, updated) VALUES (v.getCountryCode, v.getLocation, v.getLatitude, " +
                                        "v.getLongitude, v.getConfirmed, v.getDead, v.getRecovered, v.getUpdated.toString()) ");
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        });
                    } else {
                        System.out.println("rs not empty");
                        while(rs.next()) {
                            String cCode = rs.getString("countryCode");
                            if (countryToSend.containsKey(cCode) && countryToSend.get(cCode).getUpdated().toString().equals(rs.getString("updated"))) {
                                System.out.println("already sent");
                                countryToSend.remove(cCode);
                            } else {
                                if (!countryToSend.containsKey(cCode)) {
                                    System.out.println("never sent");
                                    stmt.executeUpdate("INSERT INTO countries (countryCode, location, latitude, longitude, " +
                                            "confirmed, dead, recovered, updated) VALUES (countryToSend.get(cCode).getCountryCode, " +
                                            "countryToSend.get(cCode).getLocation, countryToSend.get(cCode).getLatitude, " +
                                            "countryToSend.get(cCode).getLongitude, countryToSend.get(cCode).getConfirmed, " +
                                            "countryToSend.get(cCode).getDead, countryToSend.get(cCode).getRecovered, " +
                                            "countryToSend.get(cCode).getUpdated.toString()) ");
                                } else {
                                    System.out.println("updated");
                                    String newUpdated = countryToSend.get(cCode).getUpdated().toString();
                                    stmt.executeUpdate("UPDATE countries SET updated = newUpdated WHERE countryCode = cCode");
                                }
                            }
                        }
                    }

                    ResultSet resultSet = stmt.executeQuery("SELECT * FROM countries");
                    while(resultSet.next()) {
                        System.out.println(resultSet.getString("countryCode"));
                    }

                    stmt.executeUpdate( "CREATE TABLE table1 ( user varchar(50) )" );
                    stmt.executeUpdate( "INSERT INTO table1 ( user ) VALUES ( 'Claudio' )" );
                    stmt.executeUpdate( "INSERT INTO table1 ( user ) VALUES ( 'Bernasconi' )" );
                    ResultSet rs2 = stmt.executeQuery("SELECT * FROM table1");
                    while( rs2.next() )
                    {
                        String name = rs2.getString("user");
                        System.out.println( name );
                    }

                    stmt.close();
                    con.close();
                }
                catch( Exception e )
                {
                    System.out.println( e.getMessage() );
                }
                System.out.println("after db");

                // send to caduceus

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
                countrySent.putAll(countryToSend);
            }
        };
        t.scheduleAtFixedRate(task, 0, (long) (cp.getInterval() * 60 * 1000));
    }

    private static IFetcher fetcher(String[] args) {
        if (args.length > 0 && args[0].equals("file")) {
            return new FileFetcher();
        }
        try {
            return new URLFetcher(new URL("https://www.trackcorona.live/api/countries"));
        } catch (MalformedURLException e) {
            throw  new RuntimeException(e);
        }
    }
}
