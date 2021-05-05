import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javax.validation.constraints.Null;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ConfigParserTest {

    @Test
    public void testGetInterval() throws IOException {
        String input = "update_interval=100\ncountry_codes=zw, iq, no";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ConfigParser cp = new ConfigParser(inputStream);
        Assertions.assertEquals(100, cp.getInterval());
    }

    @Test
    public void testIntervalNull() throws IOException {
        String input = "country_codes=zw, iq, no";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ConfigParser cp = new ConfigParser(inputStream);
        Assertions.assertEquals(1, cp.getInterval());
    }

    @Test
    public void testEmptyInput() throws IOException {
        String input = "";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ConfigParser cp = new ConfigParser(inputStream);
        Assertions.assertEquals(1, cp.getInterval());
        Assertions.assertEquals(null, cp.getCountries());
    }
}
