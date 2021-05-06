import java.util.*;

public class CountryMapper {

    private Map<String, Country> getFilteredCountries(List<String> filteredCountries, List<Country> allCountries) {
        Set<String> countriesToCheck = new HashSet<>(filteredCountries);

        Map<String, Country> countryMap = new HashMap<>();
        for (Country c : allCountries) {
            countryMap.put(c.getCountryCode(), c);
        }

        countryMap.entrySet().removeIf(entry -> !countriesToCheck.contains(entry.getKey()));

        return countryMap;
    }

    public Map<String, Country> convertArrayList(List<Country> countries) {
        Map<String, Country> countryMap = new HashMap<>();
        for (Country c : countries) {
            countryMap.put(c.getCountryCode(), c);
        }
        return countryMap;
    }

    public Map<String, Country> checkSent(Map<String, Country> countrySent, Map<String, Country> countryMap) {
        Map<String, Country> returnSet = new HashMap<>();

        if (countrySent.isEmpty()) {
            returnSet = countryMap;
        } else {
            for (String country : countryMap.keySet()) {
                if (!countrySent.containsKey(country)) {
                    returnSet.put(country, countryMap.get(country));
                }
                if (countryMap.get(country).getUpdated().isAfter(countrySent.get(country).getUpdated())) {
                    returnSet.put(country, countryMap.get(country));
                }
            }
        }
        return returnSet;
    }

    public Map<String, Country> getCountries(ConfigParser cp, List<Country> countries) {
        Map<String, Country> countryMap;
        if (cp.getCountries() != null) {
            countryMap = getFilteredCountries(cp.getCountries(), countries);
        } else {
            countryMap = convertArrayList(countries);
        }
        return countryMap;
    }
}
