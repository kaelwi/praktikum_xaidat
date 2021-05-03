import java.util.Comparator;

public class ConfirmedComparator implements Comparator<Country> {
    @Override
    public int compare(Country o1, Country o2) {
        if (o1.getConfirmed() > o2.getConfirmed()) {
            return 1;
        } else if (o1.getConfirmed() < o2.getConfirmed()) {
            return -1;
        } else {
            return 0;
        }
    }
}
