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

    @Test
    public void testConvertWithWrongInput() {
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
                "    },\n" +
                "    {\n" +
                "      \"location\": \"Germany\",\n" +
                "      \"country_code\": \"de\",\n" +
                "      \"latitude\": 51.165691,\n" +
                "      \"longitude\": 10.451526,\n" +
                "      \"confirmed\": 3064382,\n" +
                "      \"recovered\": 2736100,\n" +
                "      \"updated\": \"2021-04-15 14:15:07.782944+00:00\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"location\": \"Russia\",\n" +
                "      \"country_code\": \"ru\",\n" +
                "      \"latitude\": 61.52401,\n" +
                "      \"longitude\": 105.318756,\n" +
                "      \"confirmed\": 4675153,\n" +
                "      \"dead\": 104398,\n" +
                "      \"recovered\": 4301448,\n" +
                "      \"updated\": \"2021-04-15 14:15:07.782944+00:00\"\n" +
                "    }]}";

        StringConverter sc = new StringConverter(content);
        List<Country> country = sc.convert();
        List<Country> expectedCountry = new ArrayList<>();
        expectedCountry.add(new Country("Russia", "ru", 61.52401, 105.318756, 4675153, 104398, 4301448, LocalDateTime.parse("2021-04-15 14:15:07", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        Assertions.assertEquals(country.get(1), expectedCountry.get(0));
    }

    @Test
    public void testConvertNoInput() {
        // String content = null;
        StringConverter sc = new StringConverter(null);
        Assertions.assertThrows(NullPointerException.class, sc::convert);
    }

}
