import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StringConverterTest {

    @Test
    public void testConvert() {
        String content = "{\n" +
                "  \"code\": 200,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"location\": \"Iran\",\n" +
                "      \"country_code\": \"ir\",\n" +
                "      \"latitude\": 32.427908,\n" +
                "      \"longitude\": 53.688046,\n" +
                "      \"confirmed\": 2168872,\n" +
                "      \"dead\": 65680,\n" +
                "      \"recovered\": 1749041,\n" +
                "      \"updated\": \"2021-04-15 14:15:07.782944+00:00\"\n" +
                "    }]}";
        StringConverter sc = new StringConverter(content);
        List<Country> country = sc.convert();
        List<Country> expectedCountry = new ArrayList<>();
        expectedCountry.add(new Country("Iran", "ir", 32.427908, 53.688046, 2168872, 65680, 1749041, LocalDateTime.parse("2021-04-15 14:15:07", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        Assertions.assertEquals(country, expectedCountry);
    }



}
