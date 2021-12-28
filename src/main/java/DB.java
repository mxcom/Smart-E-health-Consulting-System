import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

//Database
public class DB {
    static Connection connection = null;

    /**
     * To be called on application start.
     * Establishes connection to the database.
     * Creates a database if it doesn't exist yet.
     * Before creating a database, it asks for an admin password.
     */
    public static void init()
    {
        boolean isExistingDb = new File("ehealth.sqlite3").isFile(); //there isn't a database file

        String initialAdminPassword = null;
        if (!isExistingDb)
            initialAdminPassword = initialAdminPassword(); //ask before creating file!

        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:ehealth.sqlite3"); //creates file if it doesn't exist
            if (!isExistingDb)
                createDB(initialAdminPassword);
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

    /**
     * Ask for admin password and create database
     */
    private static void createDB(String initialAdminPassword) throws SQLException {
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);  // set timeout to 30 sec.

        //Create tables
        statement.execute(ResourceReader.getResourceString("createTableUsers.sql"));
        statement.execute(ResourceReader.getResourceString("createTableDoctors.sql"));
        statement.execute(ResourceReader.getResourceString("createTableAppointments.sql"));

        statement.execute(ResourceReader.getResourceString("createTableProblems.sql"));
        statement.execute(ResourceReader.getResourceString("createTableSpecializations.sql"));
        statement.execute(ResourceReader.getResourceString("createTableSuitableSpecializations.sql"));
        statement.execute(ResourceReader.getResourceString("insertIntoProblems.sql"));
        statement.execute(ResourceReader.getResourceString("insertIntoSpecializations.sql"));
        statement.execute(ResourceReader.getResourceString("insertIntoSuitableSpecializations.sql"));

        //Insert admin user
        String query = "INSERT INTO users (username, password) VALUES ('admin', ?)";
        PreparedStatement adminInsert = connection.prepareStatement(query);
        adminInsert.setString(1, hashPassword(initialAdminPassword));
        adminInsert.execute();
    }

    /**
     * Helper method to generate a password's hash
     * @param password the password to hash
     * @return hash
     */
    public static String hashPassword(String password)
    {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Ask the user for an initial admin password when creating the database
     * @return desired admin password entered by the user
     */
    private static String initialAdminPassword()
    {
        System.out.println("Please enter your desired admin password: ");

        Scanner in = new Scanner(System.in);
        String password = in.nextLine();
        in.close();

        return password;
    }

    /**
     * Properly closes the connection to the database
     * Probably only needed if a big query would still be running
     */
    public static void close()
    {
        try
        {
            if(connection != null)
                connection.close();
        }
        catch(SQLException e)
        {
            // connection close failed.
            System.err.println(e.getMessage());
        }
    }

    /**
     * Get a list of all usernames for login
     * @return usernames
     * @throws SQLException
     */
    public static ArrayList<String> getUsernames() throws SQLException {
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);  // set timeout to 30 sec.
        ResultSet rs = statement.executeQuery("SELECT username FROM users");

        ArrayList<String> usernames = new ArrayList<>();
        while (rs.next())
            usernames.add(rs.getString("username"));

        return usernames;
    }

    /**
     * Check if user entered their correct password
     * Retrieves stored password and verifies that the entered one matches the stored hash
     * This method is not part of the User class because there isn't a user object during login yet
     * @param username
     * @param password in plain text
     * @return whether password is correct
     */
    public static boolean checkPassword(String username, String password) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT password FROM users WHERE username = ?");
        statement.setString(1, username);
        ResultSet rs = statement.executeQuery();

        rs.next();
        String storedPassword = rs.getString("password");

        return BCrypt.checkpw(password, storedPassword);
    }

    /**
     * General method for inserting a single row into a database table
     * @param tableName the name of the table to which the row shall be added
     * @param parameters array consisting of pairs of column name and value
     * @throws SQLException
     */
    public static void insert(String tableName, Object[][] parameters) throws SQLException {
        String query = "INSERT INTO <tableName> (<names>) VALUES (<values>)".replace("<tableName>", tableName);

        //add parameter names
        String names = "";
        String separator = ", ";
        for (int i = 0; i < parameters.length; i++)
        {
            String name = (String)parameters[i][0];
            names += name + separator;
        }
        names = names.substring(0, names.length() - separator.length()); //remove last separator
        query = query.replace("<names>", names);

        //add question marks
        String questionMarks = "?" + separator;
        questionMarks = questionMarks.repeat(parameters.length);
        questionMarks = questionMarks.substring(0, questionMarks.length() - separator.length()); //remove last separator
        query = query.replace("<values>", questionMarks);

        PreparedStatement statement = DB.connection.prepareStatement(query);
        //insert parameter values
        for (int i = 0; i < parameters.length; i++)
        {
            Object value = parameters[i][1];
            insertValueIntoStatement(i, value, statement);
        }

        statement.execute();
    }

    /**
     * General method for updating rows in a database table
     * @param tableName the name of the table where the row is to be found
     * @param newValues array consisting of pairs of column name and value, these values will be set on the matching rows
     * @param conditions array consisting of pairs of column name and value, rows have to match these criteria
     * @throws SQLException
     */
    public static void update(String tableName, Object[][] newValues, Object[][] conditions) throws SQLException {
        String query = "UPDATE <tableName> SET <newValues> WHERE <conditions>".replace("<tableName>", tableName);

        //add placeholders for newValues
        String separator = ", ";
        String placeHolder = "";
        for (Object[] newValue : newValues)
        {
            placeHolder += newValue[0] + " = ?" + separator;
        }
        placeHolder = placeHolder.substring(0, placeHolder.length() - separator.length()); //remove last separator
        query = query.replace("<newValues>", placeHolder);

        //add placeholders for conditions
        String conjunction = " AND ";
        String conditionsPlaceholders = "";
        for (Object[] condition : conditions)
        {
            conditionsPlaceholders += condition[0] + " = ?" + conjunction;
        }
        conditionsPlaceholders = conditionsPlaceholders.substring(0, conditionsPlaceholders.length() - conjunction.length()); //remove last conjunction
        query = query.replace("<conditions>", conditionsPlaceholders);

        PreparedStatement statement = DB.connection.prepareStatement(query);

        int i = 0; //questionMarkIndex

        //insert newValues
        for (Object[] newValue : newValues)
        {
            Object value = newValue[1];
            insertValueIntoStatement(i++, value, statement);
        }

        //insert conditions
        for (Object[] condition : conditions)
        {
            Object value = condition[1];
            insertValueIntoStatement(i++, value, statement);
        }

        statement.execute();
    }

    private static void insertValueIntoStatement(int i, Object value, PreparedStatement statement) throws SQLException {
        if (value instanceof String)
            statement.setString(i+1, (String)value);
        else if (value instanceof Boolean)
            statement.setBoolean(i+1, (Boolean)value);
        else
            statement.setInt(i+1, (Integer)value);
    }

    /**
     * Get all rows of users table from the database
     * @return all users
     * @throws SQLException
     */
    public static ArrayList<User> getAllUsers() throws SQLException {
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);  // set timeout to 30 sec.
        ResultSet rs = statement.executeQuery("SELECT * FROM users");
        return loadUsersFromResultSet(rs);
    }

    /**
     * Get a user object with all user's details loaded from the database
     * @param username
     * @return
     */
    public static User getUser(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet rs = statement.executeQuery();
        return loadUsersFromResultSet(rs).get(0);
    }

    /**
     * Helper method to turn result set into array of user objects
     * @param rs resultSet after the query was executed
     * @return
     * @throws SQLException
     */
    private static ArrayList<User> loadUsersFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        while (rs.next())
        {
            String username = rs.getString("username");
            LocalDate birthDate = (username.equals("admin") ? null : LocalDate.of(rs.getInt("birthYear"), rs.getInt("birthMonth"), rs.getInt("birthDay"))); //admin doesn't have a stored birthdate

            User user = new User(
                    username,
                    false,
                    null,
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("mail"),
                    rs.getString("street"),
                    rs.getString("houseNo"),
                    rs.getInt("zipCode"),
                    birthDate,
                    rs.getString("preExistingConditions"),
                    rs.getString("allergies"),
                    rs.getString("pastTreatments"),
                    rs.getString("currentTreatments"),
                    rs.getString("medications"),
                    rs.getString("insurance"),
                    rs.getBoolean("privateInsurance")
            );
            users.add(user);
        }
        return users;
    }
}
