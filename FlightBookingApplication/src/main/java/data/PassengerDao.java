package data;

import models.Passenger;

import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class PassengerDao implements Dao<Passenger> {

    @Override
    public void create(Passenger passenger) {
        Connection conn = DataSource.getConnection();
        String statement = "INSERT INTO passengers (firstname, lastname, birthDate) VALUES (?,?,?);";
        try {
            PreparedStatement query = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            query.setString(1, passenger.getFirstname());
            query.setString(2, passenger.getLastname());
            query.setLong(3, passenger.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

            query.executeUpdate();
            ResultSet id = query.getGeneratedKeys();
            if (id.next()) {
                passenger.setId(id.getInt(1));
            }

            query.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Passenger read(int id) {
        Connection conn = DataSource.getConnection();
        Passenger passenger = null;
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
                passenger.setBirthDate(res.getTimestamp("birthDate").toLocalDateTime().toLocalDate());
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
        List<Passenger> list = new ArrayList<Passenger>();

        try {
            PreparedStatement query = conn.prepareStatement("SELECT * FROM passengers;");
            ResultSet res = query.executeQuery();
            while (res.next()) {
                Passenger passenger = new Passenger();
                passenger.setId(res.getInt("id"));
                passenger.setFirstname(res.getString("firstname"));
                passenger.setLastname(res.getString("lastname"));
                passenger.setBirthDate(res.getTimestamp("birthDate").toLocalDateTime().toLocalDate());

                list.add(passenger);
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

        String statement = "UPDATE passenger SET firstname = ?, lastname = ?, birthDate = ? WHERE id = ? ;";

        try {
            PreparedStatement query = conn.prepareStatement(statement);
            query.setInt(4, id);

            if (passenger.getFirstname() != null) {
                query.setString(1, passenger.getFirstname());
            }
            else {
                query.setString(1, original.getFirstname());
            }

            if (passenger.getLastname() != null) {
                query.setString(2, passenger.getLastname());
            }
            else {
                query.setString(2, original.getLastname());
            }

            if (passenger.getBirthDate() != null) {
                query.setLong(3, passenger.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
            else {
                query.setLong(3, original.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }

            query.executeUpdate();
            query.close();

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}