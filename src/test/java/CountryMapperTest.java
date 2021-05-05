import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CountryMapperTest {
    @Test
    public void testCheckSent() {
        CountryMapper countryMapper = new CountryMapper();
        HashMap<String, Country> map1 = new HashMap<>();
        HashMap<String, Country> map2 = new HashMap<>();
        Country c1 = new Country("austria", "at", 111, 123, 1, 1, 1, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        Country c2 = new Country("austria", "at", 111, 123, 1, 1, 1, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        map1.put(c1.getCountryCode(), c1);
        map2.put(c2.getCountryCode(), c2);
        Assumptions.assumeTrue(countryMapper.checkSent(map1, map2).isEmpty());

        Country c3 = new Country("france", "fr", 111, 123, 1, 1, 1, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        map2.put(c3.getCountryCode(), c3);
        countryMapper.checkSent(map1, map2);

    }

    @Test
    public void testGetCountries() {
        String input = "";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ConfigParser cp = new ConfigParser(inputStream);
        Country c1 = new Country("austria", "at", 111, 123, 1, 1, 1, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        List<Country> list = new ArrayList<>();
        list.add(c1);
        CountryMapper countryMapper = new CountryMapper();

        Assertions.assertEquals(countryMapper.getCountries(cp, list), countryMapper.convertArrayList(list));
    }
}
