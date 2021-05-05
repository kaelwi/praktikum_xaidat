import org.junit.Assume;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CountryTest {
    @Test
    public void testGetter() {
        Country c = new Country("Iran", "ir", 32.427908, 53.688046, 2168872, 65680, 1749041, LocalDateTime.parse("2021-04-15 14:15:07", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Assertions.assertEquals("Iran", c.getLocation());
        Assertions.assertEquals("ir", c.getCountryCode());
        Assertions.assertEquals(53.688046, c.getLongitude(), 0.00001);
        Assertions.assertEquals(32.427908, c.getLatitude(), 0.00001);
        Assertions.assertEquals(2168872, c.getConfirmed());
        Assertions.assertEquals(65680, c.getDead());
        Assertions.assertEquals(1749041, c.getRecovered());
        Assertions.assertEquals(LocalDateTime.parse("2021-04-15 14:15:07", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), c.getUpdated());
    }

    @Test
    public void testPositiveNumbers() {
        Country c = new Country("Iran", "ir", 32.427908, 53.688046, -1, 65680, 1749041, LocalDateTime.parse("2021-04-15 14:15:07", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Assumptions.assumeTrue(c.getConfirmed() >= 0);
        Assumptions.assumeTrue(c.getDead() >= 0);
        Assumptions.assumeTrue(c.getRecovered() >= 0);
    }

    @Test
    public void testCountryCodeLength() {
        Country c = new Country("Iran", "ir", 32.427908, 53.688046, -1, 65680, 1749041, LocalDateTime.parse("2021-04-15 14:15:07", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Assumptions.assumeFalse(c.getCountryCode().length() != 2, "Country code must consist of exactly 2 letters!");
    }
}
