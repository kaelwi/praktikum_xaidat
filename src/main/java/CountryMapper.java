import java.util.*;

public class CountryMapper {

    private HashMap<String, Country> getFilteredCountries(List<String> filteredCountries, List<Country> allCountries) {
        Set<String> countriesToCheck = new HashSet<>(filteredCountries);

        HashMap<String, Country> countryMap = new HashMap<>();
        for (Country c : allCountries) {
            countryMap.put(c.getCountryCode(), c);
        }

        Iterator<Map.Entry<String, Country>> it = countryMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Country> entry = it.next();
            if (!countriesToCheck.contains(entry.getKey())) {
                it.remove();
            }
        }

        return countryMap;
    }

    public HashMap<String, Country> convertArrayList(List<Country> countries) {
        HashMap<String, Country> countryMap = new HashMap<>();
        for (Country c : countries) {
            countryMap.put(c.getCountryCode(), c);
        }
        return countryMap;
    }

    public HashMap<String, Country> checkSent(HashMap<String, Country> countrySent, HashMap<String, Country> countryMap) {
        HashMap<String, Country> returnSet = new HashMap<>();

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

    public HashMap<String, Country> getCountries(ConfigParser cp, List<Country> countries) {
        HashMap<String, Country> countryMap;
        if (cp.getCountries() != null) {
            countryMap = getFilteredCountries(cp.getCountries(), countries);
        } else {
            countryMap = convertArrayList(countries);
        }
        return countryMap;
    }
}
