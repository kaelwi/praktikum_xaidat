import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ConfigParserTest {

    @Test
    public void givenValidIntervalConfig_whenConfigIsParsed_intervalIsCorrect() {
        String input = "update_interval=100\ncountry_codes=zw, iq, no";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ConfigParser cp = new ConfigParser(inputStream);
        Assertions.assertEquals(100, cp.getInterval());
    }

    @Test
    public void testIntervalNull() {
        String input = "country_codes=zw, iq, no";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ConfigParser cp = new ConfigParser(inputStream);
        Assertions.assertEquals(1, cp.getInterval());
    }

    @Test
    public void testEmptyInput() {
        String input = "";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ConfigParser cp = new ConfigParser(inputStream);
        Assertions.assertEquals(1, cp.getInterval());
        Assertions.assertNull(cp.getCountries());
    }

    @Test
    public void testGetCountriesSplitter() {
        String input = "country_codes=,,,zw, iq, , , ,,,no";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ConfigParser cp = new ConfigParser(inputStream);
        Assertions.assertEquals(3, cp.getCountries().size());
    }

    @Test
    public void checkValidInterval() {
        String input = "update_interval=100.01";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ConfigParser cp = new ConfigParser(inputStream);
        Assertions.assertEquals(100.01, cp.getInterval());
    }

    @Test
    public void checkInvalidInterval() {
        String input = "update_interval=100,01";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ConfigParser cp = new ConfigParser(inputStream);
        Assertions.assertNotEquals(100.01, cp.getInterval());
        Assertions.assertEquals(1, cp.getInterval());
    }

    @Test
    public void checkNegativeInterval() {
        String input = "update_interval=-1";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ConfigParser cp = new ConfigParser(inputStream);
        Assertions.assertNotEquals(100.01, cp.getInterval());
        Assertions.assertEquals(1, cp.getInterval());
    }
}
