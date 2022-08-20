package data;

import models.Passenger;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PassengerDao implements Dao<Passenger> {
    public static final HashMap<Integer,Passenger> passengersMap = new HashMap<>();

    public void updatePassengersMap(int id) {
        read(id);
    }

    @Override
    public int create(Passenger passenger) {
        Connection conn = DataSource.getConnection();
        String statement = "INSERT INTO passengers (firstname, lastname, birthDate, gender, country) VALUES (?,?,?,?,?);";
        try {
            PreparedStatement query = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            query.setString(1, passenger.getFirstname());
            query.setString(2, passenger.getLastname());
            query.setString(3, passenger.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            query.setString(4, passenger.getGender());
            query.setString(5, passenger.getCountry());

            query.executeUpdate();
            ResultSet id = query.getGeneratedKeys();
            if (id.next()) {
                passenger.setId(id.getInt(1));
                passengersMap.put(id.getInt(1), passenger);
            }

            query.close();
            return id.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Passenger read(int id) {

        Passenger passenger = passengersMap.get(id);
        if (passenger != null) {
            return passenger;
        }

        Connection conn = DataSource.getConnection();
        String statement = "SELECT * FROM passengers WHERE id = ?;";
        try {
            PreparedStatement query = conn.prepareStatement(statement);
            query.setInt(1, id);

            ResultSet res = query.executeQuery();

            if (res.next()) {
                passenger = new Passenger();
                passenger.setId(res.getInt("id"));
                passenger.setFirstname(res.getString("firstname"));
                passenger.setLastname(res.getString("lastname"));
                passenger.setBirthDate(LocalDate.parse(res.getString("birthDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                passenger.setGender(res.getString("gender"));
                passenger.setCountry(res.getString("country"));

                passengersMap.put(res.getInt("id"), passenger);
            }

            query.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return passenger;
    }


    @Override
    public List<Passenger> readAll() {
        Connection conn = DataSource.getConnection();
        List<Passenger> list = new ArrayList<>();

        try {
            PreparedStatement query = conn.prepareStatement("SELECT * FROM passengers;");
            ResultSet res = query.executeQuery();
            while (res.next()) {
                Passenger passenger = new Passenger();
                passenger.setId(res.getInt("id"));
                passenger.setFirstname(res.getString("firstname"));
                passenger.setLastname(res.getString("lastname"));
                passenger.setBirthDate(LocalDate.parse(res.getString("birthDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                passenger.setGender(res.getString("gender"));
                passenger.setCountry(res.getString("country"));
                list.add(passenger);
                passengersMap.put(res.getInt("id"), passenger);
            }
            return list;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void update(int id, Passenger passenger) {
        Connection conn = DataSource.getConnection();
        Passenger original =  this.read(id);

        String statement = "UPDATE passengers SET firstname = ?, lastname = ?, birthDate = ?, gender = ?, country = ? WHERE id = ? ;";

        try {
            PreparedStatement query = conn.prepareStatement(statement);
            query.setInt(6, id);

            if (passenger.getFirstname() != null) {
                query.setString(1, passenger.getFirstname());
                original.setFirstname(passenger.getFirstname());
            }
            else {
                query.setString(1, original.getFirstname());
            }

            if (passenger.getLastname() != null) {
                query.setString(2, passenger.getLastname());
                original.setLastname(passenger.getLastname());
            }
            else {
                query.setString(2, original.getLastname());
            }

            if (passenger.getBirthDate() != null) {
                query.setString(3, passenger.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                original.setBirthDate(passenger.getBirthDate());
            }
            else {
                query.setString(3, original.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }

            if (passenger.getGender() != null) {
                query.setString(4, passenger.getGender());
                original.setGender(passenger.getGender());
            }
            else {
                query.setString(4, original.getGender());
            }

            if (passenger.getCountry() != null) {
                query.setString(5, passenger.getCountry());
                original.setCountry(passenger.getCountry());
            }
            else {
                query.setString(5, original.getCountry());
            }

            query.executeUpdate();
            query.close();
            read(id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        Connection conn = DataSource.getConnection();
        try {
            PreparedStatement query = conn.prepareStatement("DELETE FROM passengers WHERE id = ? ;");
            query.setInt(1, id);
            query.executeUpdate();
            query.close();

            passengersMap.remove(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
