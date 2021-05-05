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
    public void testInputParsing() throws IOException {
        String input = "update_interval=1\ncountry_codes=zw, iq, no";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ConfigParser cp = new ConfigParser(inputStream);
        System.out.println(cp.getInterval());
        System.out.println(cp.getCountries().toString());
    }
}
