import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Country {
    protected String location;
    @JsonProperty("country_code")
    protected String countryCode;
    protected double latitude;
    protected double longitude;
    protected int confirmed;
    protected int dead;
    protected int recovered;
    protected LocalDateTime updated;


    public Country() {
    }

    public Country(String location, String countryCode, double latitude, double longitude, int confirmed, int dead, int recovered, LocalDateTime updated) {
        this.location = location;
        this.countryCode = countryCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.confirmed = Math.max(confirmed, 0);
        this.dead = Math.max(dead, 0);
        this.recovered = Math.max(recovered, 0);
        this.updated = updated;
    }

    public String getLocation() {
        return location;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public int getDead() {
        return dead;
    }

    public int getRecovered() {
        return recovered;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }


    public void setUpdated(String updated) {
        this.updated = LocalDateTime.parse(updated.substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return "Country{" +
                "location='" + location + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", confirmed=" + confirmed +
                ", dead=" + dead +
                ", recovered=" + recovered +
                ", updated='" + updated + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Double.compare(country.latitude, latitude) == 0 && Double.compare(country.longitude, longitude) == 0 && confirmed == country.confirmed && dead == country.dead && recovered == country.recovered && Objects.equals(location, country.location) && Objects.equals(countryCode, country.countryCode) && Objects.equals(updated, country.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, countryCode, latitude, longitude, confirmed, dead, recovered, updated);
    }
}
