import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class DBManagerTest {
    @Test
    public void testConnection() throws ClassNotFoundException, SQLException {
        DBManager dbManager = new DBManager("jdbc:h2:mem:test");
        Connection con = dbManager.getConnection();
        Assertions.assertFalse(con.isClosed());
        dbManager.closeConnection(con);
        Assertions.assertTrue(con.isClosed());
    }

    @Test
    public void testCreateTable() throws SQLException, ClassNotFoundException {
        DBManager dbManager = new DBManager("jdbc:h2:mem:test1");
        ResultSet rs = dbManager.createTable();
        Assertions.assertFalse(rs.next());
    }

    @Test
    public void testInsertMap() throws SQLException, ClassNotFoundException {
        DBManager dbManager = new DBManager("jdbc:h2:mem:test2");
        ResultSet rs = dbManager.createTable();
        Map<String, Country> map1 = new HashMap<>();
        Country c1 = new Country("austria", "at", 111, 123, 1, 1, 1, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        Country c2 = new Country("czech republic", "cz", 111, 123, 1, 1, 1, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        map1.put(c1.getCountryCode(), c1);
        map1.put(c2.getCountryCode(), c2);
        dbManager.insertMap(dbManager.getConnection(), map1);
        ResultSet testRs = dbManager.selectAll();
        while (testRs.next()) {
            Assertions.assertTrue(testRs.getString("countryCode").equals(c1.getCountryCode()) ||
                    testRs.getString("countryCode").equals(c2.getCountryCode()));
        }
    }

    @Test
    public void testUpdateOrInsertMap() throws SQLException, ClassNotFoundException {
        DBManager dbManager = new DBManager("jdbc:h2:mem:test3");
        ResultSet rs = dbManager.createTable();
        Map<String, Country> map1 = new HashMap<>();
        Country c1 = new Country("austria", "at", 111, 123, 1, 1, 1, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        Country c2 = new Country("czech republic", "cz", 111, 123, 1, 1, 1, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        map1.put(c1.getCountryCode(), c1);
        map1.put(c2.getCountryCode(), c2);
        Assertions.assertTrue(dbManager.updateOrInsertMap(dbManager.getConnection(), map1).isEmpty());
    }
}
