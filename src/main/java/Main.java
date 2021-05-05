import lombok.SneakyThrows;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

    private static HashMap<String, Country> countrySent = new HashMap<>();

    public static void main(String[] args) throws IOException {

        ConfigParser cp = new ConfigParser(Main.class.getClassLoader().getResourceAsStream("config.properties"));
        URLFetcher urlFetcher = new URLFetcher(new URL("https://www.trackcorona.live/api/countries"));

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                String content = urlFetcher.fetch();

                //convertieren in java objekte
                StringConverter stringConverter = new StringConverter(content);
                List<Country> countriesWithJackson = stringConverter.convertWithJackson();

                // get filtered countries
                CountryMapper countryMapper = new CountryMapper();
                HashMap<String, Country> countryMap = countryMapper.getCountries(cp, countriesWithJackson);

                // remove already sent countries
                HashMap<String, Country> countryToSend = countryMapper.checkSent(countrySent, countryMap);

                // send to caduceus

                // update list/map of sent countries
                countrySent.putAll(countryToSend);
            }
        };
        t.scheduleAtFixedRate(task, 0, (long) (cp.getInterval() *60*1000));
    }
}
