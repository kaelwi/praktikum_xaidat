import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        // daten fetchen von https://www.trackcorona.live/api/countries
        URLFetcher urlFetcher = new URLFetcher(new URL("https://www.trackcorona.live/api/countries"));
        String content = urlFetcher.fetch();

        //convertieren in java objekte
        StringConverter stringConverter = new StringConverter(content);
        List<Country> countryList = stringConverter.convert();

        //senden an caduceus
    }
}
