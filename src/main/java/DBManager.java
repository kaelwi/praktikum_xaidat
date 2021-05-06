import javax.xml.transform.Result;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DBManager {
    private String dbLocation;

    public DBManager(String dbLocation) {
        this.dbLocation = dbLocation;
    }

    /**
     * Establish connecton to database. DB location is set as an attribut (retrieved from config file)
     *
     * @return con (Connection)
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        // Connection con = DriverManager.getConnection("jdbc:h2:~/test", "test", "");
        Connection con = DriverManager.getConnection(dbLocation, "test", "");
        return con;
    }

    public void closeConnection(Connection con) throws SQLException, ClassNotFoundException {
        con.close();
    }

    /**
     * Create table if not exists. Return result from select statement.
     *
     * @return rs (ResultSet from select statement)
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public ResultSet createTable() throws SQLException, ClassNotFoundException {
        Statement stmt = getConnection().createStatement();
        // stmt.executeUpdate( "DROP TABLE countries" );
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS countries ( countryCode char(2) PRIMARY KEY," +
                "location varchar(255), latitude decimal(9, 6), longitude decimal(10, 6), confirmed mediumint," +
                "dead mediumint, recovered mediumint, updated timestamp )");
        ResultSet rs = stmt.executeQuery("SELECT * FROM countries");
        return rs;
    }

    /**
     * Method to insert all countries given through the parameter into the table.
     *
     * @param con (Connection)
     * @param countryMap (Map<String, Country> of countries to be inserted)
     */
    public void insertMap(Connection con, Map<String, Country> countryMap) {
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

    /**
     * Given a countryMap, update a table or insert values into a table. If there is an entry in the map which does
     * not lead to an update or insert statement, put this entry in a separate map (countriesToRemove).
     *
     * @param con (Connection)
     * @param countryMap (Map<String, Country> of countries to be inserted or updated if necessary)
     * @return countriesToRemove (Map<String, Country> of countries that were not updated or inserted)
     */
    public Map<String, Country> updateOrInsertMap(Connection con, Map<String, Country> countryMap) {
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

    private void insertSQL(Connection con, Country c) throws SQLException {
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

    private void updateSQL(Connection con, Country c) throws SQLException {
        PreparedStatement update = con.prepareStatement("UPDATE countries SET updated = ? WHERE countryCode = ?");
        update.setTimestamp(1, Timestamp.valueOf(c.getUpdated()));
        update.setString(2, c.getCountryCode());
        update.executeUpdate();
        update.close();
    }

    private ResultSet selectUpdatedSQL(Connection con, Country c) throws SQLException {
        PreparedStatement preparedStatement = con.prepareStatement("SELECT updated FROM countries WHERE countryCode = ?");
        preparedStatement.setString(1, c.getCountryCode());
        return preparedStatement.executeQuery();
    }

    public ResultSet selectAll() throws SQLException, ClassNotFoundException {
        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM countries");
        return rs;
    }

}
