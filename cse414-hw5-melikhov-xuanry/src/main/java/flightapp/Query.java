package flightapp;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Arrays;

public class Query {
  // DB Connection
  private Connection conn;

  // Password hashing parameter constants
  private static final int HASH_STRENGTH = 65536;
  private static final int KEY_LENGTH = 128;

  // Canned queries
  private static final String CHECK_FLIGHT_CAPACITY = "SELECT capacity FROM Flights WHERE fid = ?";
  private PreparedStatement checkFlightCapacityStatement;

  // For check dangling
  private static final String TRANCOUNT_SQL = "SELECT @@TRANCOUNT AS tran_count";
  private PreparedStatement tranCountStatement;

  // TODO: YOUR CODE HERE
  //Code added 02.18.20 (Daniel)
  private static final String CLEAR_USERS = "DELETE FROM Users";
  private PreparedStatement clearUsersStatement;
  private static final String CLEAR_RESERVATIONS = "DELETE FROM Reservations";
  private PreparedStatement clearReservationsStatement;
  private static final String clearRid = "TRUNCATE TABLE Reservations";
  private PreparedStatement clearRidStatement;
  private static final String CREATE_USER = "INSERT INTO Users (username, password, salt, balance) VALUES (?,?,?,?)";
  private PreparedStatement createUserStatement;
  private boolean loggedIn = false;
  private static final String LOG_IN = "SELECT username, password, salt FROM Users WHERE username = ?";
  private PreparedStatement logInStatement;
  private static final String SEARCH_DIRECT = "SELECT TOP (?) fid, carrier_id, flight_num, origin_city, " +
          "dest_city, actual_time,capacity, price FROM Flights WHERE origin_city = (?) AND dest_city = (?) AND " +
          "canceled = 0 AND day_of_month = (?) ORDER BY actual_time ASC, fid";
  private PreparedStatement searchDirectStatement;
  private static final String SEARCH_OneStop = "SELECT TOP (?) F1.fid AS F1_fid, F2.fid AS F2_fid, " +
          "F1.carrier_id AS F1_carrier_id, F2.carrier_id AS F2_carrier_id, " +
          "F1.flight_num AS F1_flight_num, F1.origin_city AS F1_origin_city, F1.dest_city AS F1_dest_city, " +
          "F1.actual_time AS F1_actual_time, F1.price AS F1_price, F1.capacity AS F1_capacity, " +
          "F2.flight_num AS F2_flight_num, F2.origin_city AS F2_origin_city, F2.dest_city AS F2_dest_city, " +
          "F2.actual_time AS F2_actual_time, F2.price AS F2_price, F2.capacity AS F2_capacity, " +
          "(F1.actual_time + F2.actual_time) AS total_time " +
          "FROM Flights AS F1, Flights AS F2 " +
          "WHERE F1.canceled = 0 AND F2.canceled = 0 AND F1.origin_city = (?) AND F2.dest_city = (?) " +
          "AND F1.day_of_month = (?) AND F1.dest_city != (?) " +
          "AND F1.day_of_month = F2.day_of_month AND F1.dest_city = F2.origin_city " +
          "ORDER BY total_time, F1.fid, F2.fid";
  private PreparedStatement searchOneStopStatement;
  private ArrayList<Itinerary> lastSearch = null;
  private static final String BOOK = "INSERT INTO Reservations (rid, username, it_date, itinerary, paid, canceled) VALUES (?,?,?,?,?,?)";
  private PreparedStatement bookStatement;
  private static final String NONCANCEL_RESERVATION = "SELECT * FROM Reservations WHERE username = ? AND canceled = 0";
  private PreparedStatement noncancelReservationStatement;
  private static final String ALL_RESERVATION = "SELECT COUNT(*) as cnt FROM Reservations";
  private PreparedStatement allReservationStatement;
  private static final String CHECK_DATE = "SELECT * FROM Reservations WHERE username = ? AND it_date = ?";
  private PreparedStatement checkDate;
  
  private String loggedUser;
  private static final String RESERVATION_CNT = "SELECT COUNT(R.itinerary) AS reservationCnt FROM " +
  "Reservations R WHERE R.itinerary LIKE ? OR R.itinerary LIKE ? OR R.itinerary LIKE ? AND R.canceled = 0";
  private PreparedStatement reservationCapacityStatement;
  private static final String FLIGHT = "SELECT * FROM Flights WHERE fid = ?";
  private PreparedStatement flightStatement;
  
  private static final String BALANCE_CHECK = "SELECT balance FROM Users WHERE username = ?";
  private PreparedStatement balanceCheckStatement;
  private static final String UPDATE_PAY = "UPDATE Reservations SET paid = ? WHERE username = ?";
  private PreparedStatement updatePayStatement;

  private static final String RESERVATION_CHECK = "SELECT * FROM Reservations WHERE rid = ? AND username = ?";
  private PreparedStatement reservationCheckStatement;

  private static final String SEARCH_UNPAID_RESERVATION = "SELECT * FROM Reservations WHERE rid = ? AND username = ? AND paid = 0";
  private PreparedStatement searchUnpaidReservation;

  private static final String UPDATE_CANCEL = "UPDATE Reservations SET canceled = 1, paid = 0 WHERE rid = ?";
  private PreparedStatement updateCancelStatement;
  private static final String UPDATE_BALANCE = "UPDATE Users SET balance = ? WHERE username = ?";
  private PreparedStatement updateBalanceStatement;




  public Query() throws SQLException, IOException {
    this(null, null, null, null);
  }

  protected Query(String serverURL, String dbName, String adminName, String password)
          throws SQLException, IOException {
    conn = serverURL == null ? openConnectionFromDbConn() :
            openConnectionFromCredential(serverURL, dbName, adminName, password);

    prepareStatements();
  }

  /**
   * Return a connecion by using dbconn.properties file
   *
   * @throws SQLException
   * @throws IOException
   */
  public static Connection openConnectionFromDbConn() throws SQLException, IOException {
    // Connect to the database with the provided connection configuration
    Properties configProps = new Properties();
    configProps.load(new FileInputStream("dbconn.properties"));
    String serverURL = configProps.getProperty("hw5.server_url");
    String dbName = configProps.getProperty("hw5.database_name");
    String adminName = configProps.getProperty("hw5.username");
    String password = configProps.getProperty("hw5.password");
    return openConnectionFromCredential(serverURL, dbName, adminName, password);
  }

  /**
   * Return a connecion by using the provided parameter.
   *
   * @param serverURL example: example.database.widows.net
   * @param dbName    database name
   * @param adminName username to login server
   * @param password  password to login server
   * @throws SQLException
   */
  protected static Connection openConnectionFromCredential(String serverURL, String dbName,
                                                           String adminName, String password) throws SQLException {
    String connectionUrl =
            String.format("jdbc:sqlserver://%s:1433;databaseName=%s;user=%s;password=%s", serverURL,
                    dbName, adminName, password);
    Connection conn = DriverManager.getConnection(connectionUrl);

    // By default, automatically commit after each statement
    conn.setAutoCommit(true);

    // By default, set the transaction isolation level to serializable
    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

    return conn;
  }

  /**
   * Get underlying connection
   */
  public Connection getConnection() {
    return conn;
  }

  /**
   * Closes the application-to-database connection
   */
  public void closeConnection() throws SQLException {
    conn.close();
  }

  /**
   * Clear the data in any custom tables created.
   * <p>
   * WARNING! Do not drop any tables and do not clear the flights table.
   */
  public void clearTables() {
    try {
      // TODO: YOUR CODE HERE
      clearRidStatement.execute();
      clearReservationsStatement.execute();
      clearUsersStatement.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
   * prepare all the SQL statements in this method.
   */
  private void prepareStatements() throws SQLException {
    checkFlightCapacityStatement = conn.prepareStatement(CHECK_FLIGHT_CAPACITY);
    tranCountStatement = conn.prepareStatement(TRANCOUNT_SQL);
    // TODO: YOUR CODE HERE
    //Code added 02.18.20 (Daniel)
    clearReservationsStatement = conn.prepareStatement(CLEAR_RESERVATIONS);
    clearUsersStatement = conn.prepareStatement(CLEAR_USERS);
    createUserStatement = conn.prepareStatement(CREATE_USER);
    logInStatement = conn.prepareStatement(LOG_IN);
    searchDirectStatement = conn.prepareStatement(SEARCH_DIRECT);
    searchOneStopStatement = conn.prepareStatement(SEARCH_OneStop);
    bookStatement = conn.prepareStatement(BOOK);
    noncancelReservationStatement = conn.prepareStatement(NONCANCEL_RESERVATION);
    allReservationStatement = conn.prepareStatement(ALL_RESERVATION);
    checkDate = conn.prepareStatement(CHECK_DATE);
    reservationCapacityStatement = conn.prepareStatement(RESERVATION_CNT);
    flightStatement = conn.prepareStatement(FLIGHT);
    clearRidStatement = conn.prepareStatement(clearRid);
    balanceCheckStatement = conn.prepareStatement(BALANCE_CHECK);
    updatePayStatement = conn.prepareStatement(UPDATE_PAY);
    updateCancelStatement = conn.prepareStatement(UPDATE_CANCEL);
    reservationCheckStatement = conn.prepareStatement(RESERVATION_CHECK);
    updateBalanceStatement = conn.prepareStatement(UPDATE_BALANCE);
    searchUnpaidReservation = conn.prepareStatement(SEARCH_UNPAID_RESERVATION);
  }

  /**
   * Takes a user's username and password and attempts to log the user in.
   *
   * @param username user's username
   * @param password user's password
   * @return If someone has already logged in, then return "User already logged in\n" For all other
   * errors, return "Login failed\n". Otherwise, return "Logged in as [username]\n".
   */
  public String transaction_login(String username, String password) {
    try {
      conn.setAutoCommit(false);
      logInStatement.setString(1, username);
      ResultSet rs = logInStatement.executeQuery();
      int count = 0;
      byte[] salt = null;
      byte[] old_hash = null;

      while (rs.next()) {
        count++;
        salt = rs.getBytes("salt");
        old_hash = rs.getBytes("password");
      }
      if (count != 0) {
        byte[] new_hash = hashPassword(password, salt);
        if (!Arrays.equals(new_hash, old_hash)) {
          throw new Exception("password incorrect");
        } else {
          if (loggedIn) {
            throw new Exception("Already logged in");
          }
          loggedIn = true;
          loggedUser = username;
          conn.commit();
          conn.setAutoCommit(true);
          return "Logged in as " + username + "\n";

        }
      } else {
        throw new Exception("user not exist");
      }
    } catch (Exception e) {
      try {
        conn.rollback();
        conn.setAutoCommit(true);
      } catch (SQLException sql_e){
        return("Database error: " + sql_e.getMessage());
      }
      if (e.getMessage() == "Already logged in") {
        return "User already logged in\n";
      } else {
        return "Login failed\n";
      }
    } finally {
      checkDanglingTransaction();
    }
  }

  /**
   * Implement the create user function.
   *
   * @param username   new user's username. User names are unique the system.
   * @param password   new user's password.
   * @param initAmount initial amount to deposit into the user's account, should be >= 0 (failure
   *                   otherwise).
   * @return either "Created user {@code username}\n" or "Failed to create user\n" if failed.
   */
  public String transaction_createCustomer(String username, String password, int initAmount) {
    try {
      conn.setAutoCommit(false);
      if (initAmount < 0) {
        throw new Exception("Negative balance");
      }
      createUserStatement.setString(1, username);
      ArrayList<byte[]> result = hashPassword(password);
      byte[] hash = result.get(0);
      byte[] salt = result.get(1);
      createUserStatement.setBytes(2, hash);
      createUserStatement.setBytes(3, salt);
      createUserStatement.setInt(4, initAmount);

      createUserStatement.execute();
      conn.commit();
      conn.setAutoCommit(true);
      return "Created user " + username + "\n";
    } catch (Exception e) {
      try {
        conn.rollback();
        conn.setAutoCommit(true);
      } catch (SQLException sql_e){
        return("Database error: " + sql_e.getMessage());
      }
      return "Failed to create user\n";
    } finally {
      checkDanglingTransaction();
    }
  }

  //Code added 02.18.20 (Daniel)

  /**
   * Generates a salted hash byte-array, using a specified password
   *
   * @param password password to be hashed
   */
  public ArrayList<byte[]> hashPassword(String password) {
    // Generate a random cryptographic salt
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);

    // Specify the hash parameters
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_STRENGTH, KEY_LENGTH);

    // Generate the hash
    SecretKeyFactory factory = null;
    byte[] hash = null;
    try {
      factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      hash = factory.generateSecret(spec).getEncoded();
      ArrayList<byte[]> arr = new ArrayList<byte[]>();
      arr.add(hash);
      arr.add(salt);
      return arr;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
      throw new IllegalStateException();
    }

  }

  public byte[] hashPassword(String password, byte[] salt) {
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_STRENGTH, KEY_LENGTH);
    SecretKeyFactory factory = null;
    byte[] hash = null;
    try {
      factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      hash = factory.generateSecret(spec).getEncoded();
      return hash;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
      throw new IllegalStateException();
    }
  }

  /**
   * Implement the search function.
   * <p>
   * Searches for flights from the given origin city to the given destination city, on the given day
   * of the month. If {@code directFlight} is true, it only searches for direct flights, otherwise
   * is searches for direct flights and flights with two "hops." Only searches for up to the number
   * of itineraries given by {@code numberOfItineraries}.
   * <p>
   * The results are sorted based on total flight time.
   *
   * @param originCity
   * @param destinationCity
   * @param directFlight        if true, then only search for direct flights, otherwise include
   *                            indirect flights as well
   * @param dayOfMonth
   * @param numberOfItineraries number of itineraries to return
   * @return If no itineraries were found, return "No flights match your selection\n". If an error
   * occurs, then return "Failed to search\n".
   * <p>
   * Otherwise, the sorted itineraries printed in the following format:
   * <p>
   * itinerary [itinerary number]: [number of flights] flight(s), [total flight time]
   * minutes\n [first flight in itinerary]\n ... [last flight in itinerary]\n
   * <p>
   * Each flight should be printed using the same format as in the {@code Flight} class.
   * Itinerary numbers in each search should always start from 0 and increase by 1.
   * @see Flight#toString()
   */
  public String transaction_search(String originCity, String destinationCity, boolean directFlight,
                                   int dayOfMonth, int numberOfItineraries) {
    try {
      conn.setAutoCommit(false);
      ArrayList<Itinerary> ItineraryArr = new ArrayList<Itinerary>();
      // call the direct flight query and generate a list of Itineraries
      // if not enough (# of itineries < given) and directFlight = 0
      // call one stop query and generate result
      // print and save result

      String result = "";
      searchDirectStatement.setInt(1, numberOfItineraries);
      searchDirectStatement.setString(2, originCity);
      searchDirectStatement.setString(3, destinationCity);
      searchDirectStatement.setInt(4, dayOfMonth);

      ResultSet oneHopResults = searchDirectStatement.executeQuery();
      int count = 0;

      while (oneHopResults.next()) {
        Flight oneStop = new Flight();
        oneStop.fid = oneHopResults.getInt("fid");
        oneStop.dayOfMonth = dayOfMonth;
        oneStop.carrierId = oneHopResults.getString("carrier_id");
        oneStop.flightNum = oneHopResults.getString("flight_num");
        oneStop.originCity = oneHopResults.getString("origin_city");
        oneStop.destCity = oneHopResults.getString("dest_city");
        oneStop.time = oneHopResults.getInt("actual_time");
        oneStop.capacity = oneHopResults.getInt("capacity");
        oneStop.price = oneHopResults.getInt("price");
        Itinerary it = new Itinerary(oneStop);
        count++;
        ItineraryArr.add(it);

      }
      oneHopResults.close();

      if (!directFlight) {
        // search for non-direct flights
        int remain = numberOfItineraries - count;
        searchOneStopStatement.setInt(1, remain);
        searchOneStopStatement.setString(2, originCity);
        searchOneStopStatement.setString(3, destinationCity);
        searchOneStopStatement.setInt(4, dayOfMonth);
        searchOneStopStatement.setString(5, originCity);

        ResultSet indirectResults = searchOneStopStatement.executeQuery();
        while (indirectResults.next()) {
          Flight F1 = new Flight();
          F1.fid = indirectResults.getInt("F1_fid");
          F1.dayOfMonth = dayOfMonth;
          F1.carrierId = indirectResults.getString("F1_carrier_id");
          F1.flightNum = indirectResults.getString("F1_flight_num");
          F1.originCity = indirectResults.getString("F1_origin_city");
          F1.destCity = indirectResults.getString("F1_dest_city");
          F1.time = indirectResults.getInt("F1_actual_time");
          F1.capacity = indirectResults.getInt("F1_capacity");
          F1.price = indirectResults.getInt("F1_price");

          Flight F2 = new Flight();
          F2.fid = indirectResults.getInt("F2_fid");
          F2.dayOfMonth = dayOfMonth;
          F2.carrierId = indirectResults.getString("F2_carrier_id");
          F2.flightNum = indirectResults.getString("F2_flight_num");
          F2.originCity = indirectResults.getString("F2_origin_city");
          F2.destCity = indirectResults.getString("F2_dest_city");
          F2.time = indirectResults.getInt("F2_actual_time");
          F2.capacity = indirectResults.getInt("F2_capacity");
          F2.price = indirectResults.getInt("F2_price");

          Itinerary it = new Itinerary(F1, F2);
          ItineraryArr.add(it);

        }
        indirectResults.close();
      }
      if (ItineraryArr.size() == 0) {
        throw new Exception("No such itineries found");
      }

      Collections.sort(ItineraryArr);
      lastSearch =  ItineraryArr;
      for (int i = 0; i < ItineraryArr.size(); i++) {
        Itinerary it = ItineraryArr.get(i);

        if (it.flights.length == 2) {
          result += "Itinerary " + i + ": " + "2 flight(s)" + ", " + it.totalTime + " minutes\n" +
                  it.flights[0].toString() + it.flights[1].toString();
        } else {
          result += "Itinerary " + i + ": " + "1 flight(s)" + ", " + it.totalTime + " minutes\n" + it.flights[0].toString();
        }
      }
      conn.commit();
      conn.setAutoCommit(true);
      return result;
    } catch (Exception e) {
      try {
        conn.rollback();
        conn.setAutoCommit(true);
      } catch (SQLException sql_e){
        return("Database error: " + sql_e.getMessage());
      }
      if (e.getMessage() == "No such itineries found") {
        return "No flights match your selection \n";
      }
      return "Failed to search\n";
    } finally {
      checkDanglingTransaction();
    }
  }

  /**
   * Implements the book itinerary function.
   *
   * @param itineraryId ID of the itinerary to book. This must be one that is returned by search in
   *                    the current session.
   * @return If the user is not logged in, then return "Cannot book reservations, not logged in\n".
   * If the user is trying to book an itinerary with an invalid ID or without having done a
   * search, then return "No such itinerary {@code itineraryId}\n". If the user already has
   * a reservation on the same day as the one that they are trying to book now, then return
   * "You cannot book two flights in the same day\n". For all other errors, return "Booking
   * failed\n".
   * <p>
   * And if booking succeeded, return "Booked flight(s), reservation ID: [reservationId]\n"
   * where reservationId is a unique number in the reservation system that starts from 1 and
   * increments by 1 each time a successful reservation is made by any user in the system.
   */
  public String transaction_book(int itineraryId) {
    for (int i = 0; i < 5; i++){
       try {
        if (!loggedIn) {
          throw new Exception("Not logged in");
        }
        if (itineraryId < 0 || itineraryId > lastSearch.size() - 1) {
          throw new NullPointerException();
        }

        conn.setAutoCommit(false);

        Itinerary itinerary = lastSearch.get(itineraryId);
        int request_date  =  itinerary.flights[0].dayOfMonth;
        String itineraryS = "";
        if (itinerary.flights.length == 2) {
          int flight1 = itinerary.flights[0].fid;
          int res_cap1 = capacity(flight1);
          int flight2 = itinerary.flights[1].fid;
          int res_cap2 = capacity(flight2);
          itineraryS += flight1 + "," + flight2;
        } else {
          int flight1 = itinerary.flights[0].fid;
          int res_cap = capacity(flight1);
          itineraryS += flight1;
        }

        checkDate.setString(1, loggedUser);
        checkDate.setInt(2, request_date);

        ResultSet rs = checkDate.executeQuery();

        while (rs.next()) {
          throw new Exception("Two flights in the same day");
        }

        rs = allReservationStatement.executeQuery();
        int count = -1;
        while(rs.next()){
          int cnt = rs.getInt("cnt");
          count = cnt + 1;
        }
        bookStatement.setInt(1, count);
        bookStatement.setString(2, loggedUser);
        bookStatement.setInt(3, request_date);                                     // bookStatement.setString(2, date);
        bookStatement.setString(4, itineraryS);
        bookStatement.setInt(5, 0);                                                // bookStatement.setInt(4, 0);
        bookStatement.setInt(6, 0);                                                // bookStatement.setInt(5, 0);
        bookStatement.execute();

        // checkDate.setString(1, loggedUser);
        // checkDate.setInt(2, request_date);
        rs = checkDate.executeQuery();

        int rid = -1;
        while (rs.next()) {
          rid = rs.getInt("rid");
        }
        conn.commit();
        conn.setAutoCommit(true);


        return "Booked flight(s), reservation ID: " + rid + "\n";                 // Booked flight(s), reservation ID: reservation_id
      } catch (NullPointerException e) {
        try {
          conn.rollback();
          conn.setAutoCommit(true);
        } catch (SQLException sql_e){
          // return("Database error: " + sql_e.getMessage());
        }
        return "No such itinerary " + itineraryId + "\n";
      } catch (Exception e) {
        try {
          conn.rollback();
          conn.setAutoCommit(true);
        } catch (SQLException sql_e){
          // return("Database error: " + sql_e.getMessage());
        }
        if (e.getMessage() == "Not logged in") {
          return "Cannot book reservations, not logged in\n";
        } else if (e.getMessage() == "Two flights in the same day") {
          return "You cannot book two flights in the same day\n";
        } else if(e.getMessage() == "not enough capacity") {
          return "Booking failed\n";
        } else {

        }
      } finally{
        checkDanglingTransaction();
      }
    }
    return "Booking failed\n";
  }

  public int capacity(int fid) throws Exception{
      reservationCapacityStatement.setString(1, "%," + Integer.toString(fid));
      reservationCapacityStatement.setString(2, Integer.toString(fid) + ",%");
      reservationCapacityStatement.setString(3, Integer.toString(fid));
      ResultSet rs = reservationCapacityStatement.executeQuery();
      int res_cap = 0;
      while(rs.next()){
        res_cap = rs.getInt("reservationCnt");
      }
      int flight_cap = checkFlightCapacity(fid);
      int cap = flight_cap - res_cap;
      if(cap<=0){
        throw new Exception("not enough capacity");
      }
      return cap;
    }

  /**
   * Implements the pay function.
   *
   * @param reservationId the reservation to pay for.
   * @return If no user has logged in, then return "Cannot pay, not logged in\n" If the reservation
   * is not found / not under the logged in user's name, then return "Cannot find unpaid
   * reservation [reservationId] under user: [username]\n" If the user does not have enough
   * money in their account, then return "User has only [balance] in account but itinerary
   * costs [cost]\n" For all other errors, return "Failed to pay for reservation
   * [reservationId]\n"
   * <p>
   * If successful, return "Paid reservation: [reservationId] remaining balance:
   * [balance]\n" where [balance] is the remaining balance in the user's account.
   */
  public String transaction_pay(int reservationId) {
    try {
      conn.setAutoCommit(false);
      // if not logged in
      if (!loggedIn) {
        throw new Exception("Not logged in");
      }
      // if reservation not found
      searchUnpaidReservation.setInt(1, reservationId);
      searchUnpaidReservation.setString(2, loggedUser);
      ResultSet rs =  searchUnpaidReservation.executeQuery();
      if(!rs.next()){
        conn.rollback();
        conn.setAutoCommit(true);
        return "Cannot find unpaid reservation " + reservationId + " under user: " + loggedUser +"\n";
      } else { // reservation found, now check balance
        int price = 0;
        Itinerary itinerary = new Itinerary(rs.getString("itinerary"));
        for (Flight flight : itinerary.flights) {
          price += flight.price;
        }

        balanceCheckStatement.setString(1, loggedUser);
        rs = balanceCheckStatement.executeQuery();
        rs.next();

        int balance = rs.getInt("balance");
        int remaining = balance - price;
        if (remaining < 0) {
          conn.rollback();
          conn.setAutoCommit(true);
          return "User has only " + balance + " in account but itinerary costs " + price + "\n";
        } else {
          updateBalanceStatement.setInt(1, remaining);
          updateBalanceStatement.setString(2, loggedUser);
          updateBalanceStatement.execute();

          updatePayStatement.setInt(1, 1);
          updatePayStatement.setString(2, loggedUser);
          updatePayStatement.execute();

          conn.commit();
          conn.setAutoCommit(true);
          return "Paid reservation: " + reservationId + " remaining balance: " + remaining + "\n";
        }
      }
    } catch (Exception e) {
      try {
        conn.rollback();
        conn.setAutoCommit(true);
      } catch (SQLException sql_e){
        return("Database error: " + sql_e.getMessage());
      }
      if (e.getMessage() == "Not logged in") {
        return "Cannot pay, not logged in\n";
      } else {
        return "Failed to pay for reservation " + reservationId + "\n";
      }
    } finally {
    checkDanglingTransaction();
    }
  }


  /**
   * Implements the reservations function.
   *
   * @return If no user has logged in, then return "Cannot view reservations, not logged in\n" If
   * the user has no reservations, then return "No reservations found\n" For all other
   * errors, return "Failed to retrieve reservations\n"
   * <p>
   * Otherwise return the reservations in the following format:
   * <p>
   * Reservation [reservation ID] paid: [true or false]:\n [flight 1 under the
   * reservation]\n [flight 2 under the reservation]\n Reservation [reservation ID] paid:
   * [true or false]:\n [flight 1 under the reservation]\n [flight 2 under the
   * reservation]\n ...
   * <p>
   * Each flight should be printed using the same format as in the {@code Flight} class.
   * @see Flight#toString()
   */
  public String transaction_reservations() {
    try {
      conn.setAutoCommit(false);
      if (!loggedIn) {
         throw new Exception("Not logged in");
       }
      noncancelReservationStatement.setString(1,loggedUser);
      ResultSet rs = noncancelReservationStatement.executeQuery();
      ArrayList<String> reservations = new ArrayList<String>();
      while(rs.next()){
          String reservation = "Reservation " + Integer.toString(rs.getInt("rid"));
          if(rs.getInt("paid") == 1)
              reservation += " paid: true:\n";
          else
              reservation += " paid: false:\n";
          Itinerary itinerary = new Itinerary(rs.getString("itinerary"));
          for(Flight flight : itinerary.flights)
              reservation += flight.toString();
          reservations.add(reservation);
      }
      // System.out.print(reservations);

      String reservationString = "";
      for (String reservation : reservations) {
        reservationString += reservation;
      }

      System.out.print("reservation String is 1 " + reservationString);

      if(reservationString == "") {
        throw new Exception("No reservations");
      }
      
      conn.commit();
      conn.setAutoCommit(true);
      System.out.print("reservation String is 2 " + reservationString);
      return reservationString;
    } catch(Exception e){
      try {
        conn.rollback();
        conn.setAutoCommit(true);
      } catch (SQLException sql_e){
        return("Database error: " + sql_e.getMessage());
      }
      if(e.getMessage() == "Not logged in"){
        return "Cannot view reservations, not logged in\n";
      } else if(e.getMessage() == "No reservations"){
        return "No reservations found\n";
      } else {
        return "Failed to retrieve reservations\n";
      }
    }
    finally {
      checkDanglingTransaction();
    }
  }

  /**
   * Implements the cancel operation.
   *
   * @param reservationId the reservation ID to cancel
   * @return If no user has logged in, then return "Cannot cancel reservations, not logged in\n" For
   * all other errors, return "Failed to cancel reservation [reservationId]\n"
   * <p>
   * If successful, return "Canceled reservation [reservationId]\n"
   * <p>
   * Even though a reservation has been canceled, its ID should not be reused by the system.
   */
  public String transaction_cancel(int reservationId) {
    try {
      conn.setAutoCommit(false);
      if(!loggedIn){
          throw new Exception("Not logged in");
      }
      reservationCheckStatement.setInt(1, reservationId);
      reservationCheckStatement.setString(2, loggedUser);
      ResultSet rs = reservationCheckStatement.executeQuery();
      while(rs.next()){
        if(rs.getInt("canceled") == 1) {
          throw new Exception("Already cancelled");
        } else if (rs.getInt("paid") == 1){
          Itinerary reservation = new Itinerary(rs.getString("itinerary"));
          int newBalance = 0;
          for(Flight flight : reservation.flights)
            newBalance += flight.price;
            balanceCheckStatement.setString(1, loggedUser);
            ResultSet rs2 = balanceCheckStatement.executeQuery();
            while(rs2.next()) {
              newBalance += rs2.getInt("balance");
              updateBalanceStatement.setInt(1, newBalance);
              updateBalanceStatement.setString(2, loggedUser);
              updateBalanceStatement.execute();
            }
        }
        updateCancelStatement.setInt(1, reservationId);
        updateCancelStatement.execute();
        conn.commit();
        conn.setAutoCommit(true);
        return "Canceled reservation " + reservationId + "\n";
      }
      throw new Exception();
    } catch(SQLException e) {
        try {
        conn.rollback();
        conn.setAutoCommit(true);
      } catch (SQLException sql_e){
        return("Database error: " + sql_e.getMessage());
      }
        return "database error: " + e.getMessage();
    } catch (Exception e) {
        try {
        conn.rollback();
        conn.setAutoCommit(true);
      } catch (SQLException sql_e){
        return("Database error: " + sql_e.getMessage());
      }
      if (e.getMessage() == "Not logged in"){
        return "Cannot cancel reservations, not logged in\n";
      } else {
        return "Failed to cancel reservation " + reservationId + "\n";
      }
    } finally {
      checkDanglingTransaction();
    }
  }


  /**
   * Example utility function that uses prepared statements
   */
  private int checkFlightCapacity(int fid) throws SQLException {
    checkFlightCapacityStatement.clearParameters();
    checkFlightCapacityStatement.setInt(1, fid);
    ResultSet results = checkFlightCapacityStatement.executeQuery();
    results.next();
    int capacity = results.getInt("capacity");
    results.close();

    return capacity;
  }

  /**
   * Throw IllegalStateException if transaction not completely complete, rollback.
   */
  private void checkDanglingTransaction() {
    try {
      try (ResultSet rs = tranCountStatement.executeQuery()) {
        rs.next();
        int count = rs.getInt("tran_count");
        if (count > 0) {
          throw new IllegalStateException(
                  "Transaction not fully commit/rollback. Number of transaction in process: " + count);
        }
      } finally {
        conn.setAutoCommit(true);
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Database error", e);
    }
  }

  private static boolean isDeadLock(SQLException ex) {
    return ex.getErrorCode() == 1205;
  }

  /**
   * A class to store flight information.
   */
  class Flight {
    public int fid;
    public int dayOfMonth;
    public String carrierId;
    public String flightNum;
    public String originCity;
    public String destCity;
    public int time;
    public int capacity;
    public int price;

    @Override
    public String toString() {
      return "ID: " + fid + " Day: " + dayOfMonth + " Carrier: " + carrierId + " Number: " +
              flightNum + " Origin: " + originCity + " Dest: " + destCity + " Duration: " + time +
              " Capacity: " + capacity + " Price: " + price + "\n";
    }
    public Flight (int fid) throws SQLException{
        this.fid = fid;
        flightStatement.setInt(1, fid);
        ResultSet rs = flightStatement.executeQuery();
        while (rs.next()){
            this.dayOfMonth = rs.getInt("day_of_month");
            this.carrierId = rs.getString("carrier_id");
            this.flightNum = rs.getString("flight_num");
            this.originCity = rs.getString("origin_city");
            this.destCity = rs.getString("dest_city");
            this.time = rs.getInt("actual_time");
            this.price = rs.getInt("price");
            this.capacity = rs.getInt("capacity");
        }
    }
    public Flight(){
    }
  }

  /**
   * A class to store itinerary information
   */
  class Itinerary implements Comparable<Itinerary> {
    public Flight[] flights;
    int totalTime;

    public Itinerary(Flight flight) {
      flights = new Flight[]{flight};
      totalTime = flight.time;
    }

    public Itinerary(Flight flight1, Flight flight2) {
      flights = new Flight[]{flight1, flight2};
      totalTime = flight1.time + flight2.time;
    }

    public Itinerary(String fid)throws SQLException {
        String[] fids = fid.split(",");
        if(fids.length == 2){
            flights = new Flight[]{new Flight(Integer.parseInt(fids[0])), new Flight(Integer.parseInt(fids[1]))};
        }
        else{
            flights = new Flight[]{new Flight(Integer.parseInt(fids[0]))};
        }
    }

    public int compareTo(Itinerary it) {
      if (it.totalTime == this.totalTime)
        return 0;
      if (it.totalTime < this.totalTime)
        return 1;
      else
        return -1;
    }
  }
}
