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

    /**
     * Simple method to convert List to Map
     *
     * @param countries (List<Country>)
     * @return countryMap (Map<String, Country>)
     */
    public Map<String, Country> convertArrayList(List<Country> countries) {
        Map<String, Country> countryMap = new HashMap<>();
        for (Country c : countries) {
            countryMap.put(c.getCountryCode(), c);
        }
        return countryMap;
    }

    /**
     * Method used originally to check if data has already been sent. Data was stored in an attribute of Main (and reseted with every new run)
     *
     * @param countrySent (Map<String, Country> of already sent data)
     * @param countryMap (Map<String, Country> of data to be send)
     * @return returnSet (Map<String, Country> actual countries that shall be sent)
     */
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

    /**
     * If there is a filter set in the config file, return filtered countries. Otherwise return all countries.
     *
     * @param cp (ConfigParser with properties)
     * @param countries (List<Country> of all fetched country)
     * @return countryMap (Map<String, Country> of countries to be sent)
     */
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
