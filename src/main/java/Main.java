import lombok.SneakyThrows;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class Main {


        //config file(.properties file)
        // interval
        // country_filter
    public static void main(String[] args) throws IOException {

        ConfigParser cp = new ConfigParser(Main.class.getClassLoader().getResourceAsStream("config.properties"));
        URLFetcher urlFetcher = new URLFetcher(new URL("https://www.trackcorona.live/api/countries"));
        HashMap<LocalDateTime, Country> countryWithTimeStamp = new HashMap<>();

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                String content = urlFetcher.fetch();

                //convertieren in java objekte
                StringConverter stringConverter = new StringConverter(content);
                List<Country> countriesWithJackson = stringConverter.convertWithJackson();

                Set<String> countriesToCheck = new HashSet<>();
                for (String code : cp.getCountries()) {
                    countriesToCheck.add(code);
                }

                HashMap<String, Country> countryMap = new HashMap<>();
                for (Country c : countriesWithJackson) {
                    countryMap.put(c.getCountryCode(), c);
                }

                Iterator<Map.Entry<String, Country>> it = countryMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Country> entry = it.next();
                    if(!countriesToCheck.contains(entry.getKey())) {
                        it.remove();
                    }
                }

                if (countryWithTimeStamp.isEmpty()) {
                    for (Country c : countryMap.values()) {
                        countryWithTimeStamp.put(c.getUpdated(), c);
                    }
                }

                Iterator<Map.Entry<String, Country>> it2 = countryMap.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry<String, Country> entry = it2.next();
                    if(!countryWithTimeStamp.containsKey(entry.getValue().getUpdated())) {
                        it.remove();
                    }

                }


                // send to caduceus
                // save last sent to hashMap with TimeStamp -> beim nächsten durchlauf: if not empty, check

                //config - welche länder

                //duplikate ignorieren

                //senden an caduceus
            }
        };
        t.scheduleAtFixedRate(task, 0,cp.getInterval()*60*1000);





        // timer?




/*        // daten fetchen von https://www.trackcorona.live/api/countries
        //daten fetchen alle x sekunden
        // URLFetcher urlFetcher = new URLFetcher(new URL("https://www.trackcorona.live/api/countries"));
        String content = urlFetcher.fetch();

        //convertieren in java objekte
        StringConverter stringConverter = new StringConverter(content[0]);
        List<Country> countryList = stringConverter.convert();
        List<Country> countriesWithJackson = stringConverter.convertWithJackson();

        //config - welche länder

        //duplikate ignorieren

        //senden an caduceus*/

    }
}
