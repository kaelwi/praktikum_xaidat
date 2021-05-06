import javax.xml.transform.Result;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DBManager {
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection con = DriverManager.getConnection("jdbc:h2:~/test", "test", "");
        return con;
    }

    public static void closeConnetcion() throws SQLException, ClassNotFoundException {
        getConnection().close();
    }

    public static ResultSet createTable() throws SQLException, ClassNotFoundException {
        Statement stmt = getConnection().createStatement();
        // stmt.executeUpdate( "DROP TABLE countries" );
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS countries ( countryCode char(2) PRIMARY KEY," +
                "location varchar(255), latitude decimal(9, 6), longitude decimal(10, 6), confirmed mediumint," +
                "dead mediumint, recovered mediumint, updated timestamp )");
        ResultSet rs = stmt.executeQuery("SELECT * FROM countries");
        return rs;
    }

    public static void insertMap(Connection con, Map<String, Country> countryMap) {
        countryMap.forEach((k, v) -> {
            try {
                // parameterised statement
                // https://stackoverflow.com/questions/20781743/java-how-to-write-variable-into-h2-database
                insertSQL(con, v);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static Map<String, Country> updateOrInsertMap(Connection con, Map<String, Country> countryMap) {
        Map<String, Country> countriesToRemove = new HashMap<>();
        countryMap.forEach((k, v) -> {
            try {
                ResultSet compare = selectUpdatedSQL(con, v);

                if (!compare.next()) {
                    insertSQL(con, v);
                } else {
                    if (Timestamp.valueOf(v.getUpdated()).toInstant().isAfter(compare.getTimestamp("updated").toInstant())) {
                        updateSQL(con, v);
                    } else {
                        countriesToRemove.put(k, v);
                    }
                }

                compare.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        return countriesToRemove;
    }

    private static void insertSQL(Connection con, Country c) throws SQLException {
        PreparedStatement statement = con.prepareStatement("INSERT INTO countries (countryCode, location, latitude, longitude, " +
                "confirmed, dead, recovered, updated) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, c.getCountryCode());
        statement.setString(2, c.getLocation());
        statement.setDouble(3, c.getLatitude());
        statement.setDouble(4, c.getLongitude());
        statement.setInt(5, c.getConfirmed());
        statement.setInt(6, c.getDead());
        statement.setInt(7, c.getRecovered());
        statement.setTimestamp(8, Timestamp.valueOf(c.getUpdated()));

        statement.executeUpdate();
        statement.close();
    }

    private static void updateSQL(Connection con, Country c) throws SQLException {
        PreparedStatement update = con.prepareStatement("UPDATE countries SET updated = ? WHERE countryCode = ?");
        update.setTimestamp(1, Timestamp.valueOf(c.getUpdated()));
        update.setString(2, c.getCountryCode());
        update.executeUpdate();
        update.close();
    }

    private static ResultSet selectUpdatedSQL(Connection con, Country c) throws SQLException {
        PreparedStatement preparedStatement = con.prepareStatement("SELECT updated FROM countries WHERE countryCode = ?");
        preparedStatement.setString(1, c.getCountryCode());
        return preparedStatement.executeQuery();
    }

    public static ResultSet selectAll() throws SQLException, ClassNotFoundException {
        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM countries");
        return rs;
    }

}
