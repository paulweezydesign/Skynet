package data;

import models.Account;
import models.BankCard;

import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class BankCardDao implements Dao<BankCard> {

    AccountDao accountDao = new AccountDao();

    @Override
    public void create(BankCard bankCard) {
        Connection conn = DataSource.getConnection();
        String statement = "INSERT INTO bankCards (cardNumber, expirationDate, CVV, cardHolder) VALUES (?,?,?,?);";
        try {
            PreparedStatement query = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            query.setString(1, bankCard.getCardNumber());
            query.setLong(2, bankCard.getExpirationDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            query.setString(3, bankCard.getCVV());
            query.setInt(4, bankCard.getCardHolder().getId());

            query.executeUpdate();
            ResultSet id = query.getGeneratedKeys();
            if (id.next()) {
                bankCard.setId(id.getInt(1));
            }
            query.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BankCard read(int id) {
        Connection conn = DataSource.getConnection();
        BankCard bankCard = null;
        String statement = "SELECT * FROM bankCards WHERE id = ?;";
        try {
            PreparedStatement query = conn.prepareStatement(statement);
            query.setInt(1, id);

            ResultSet res = query.executeQuery();

            if (res.next()) {
                bankCard = new BankCard();
                bankCard.setId(res.getInt("id"));
                bankCard.setCardNumber(res.getString("cardNumber"));
                bankCard.setExpirationDate(res.getTimestamp("expirationDate").toLocalDateTime().toLocalDate());
                bankCard.setCVV(res.getString("CVV"));
                bankCard.setCardHolder(res.getInt("cardHolder"));

            }

            query.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bankCard;
    }

    public List<BankCard> read(Account account) {
        Connection conn = DataSource.getConnection();
        List<BankCard> list = new ArrayList<>();

        try {
            PreparedStatement query = conn.prepareStatement("SELECT * FROM bankCards Where cardHolder = ?;");
            query.setInt(1, account.getId());
            ResultSet res = query.executeQuery();

            while (res.next()) {
                BankCard bankCard = new BankCard();
                bankCard.setId(res.getInt("id"));
                bankCard.setCardNumber(res.getString("cardNumber"));
                bankCard.setExpirationDate(res.getTimestamp("expirationDate").toLocalDateTime().toLocalDate());
                bankCard.setCVV(res.getString("CVV"));
                bankCard.setCardHolder(res.getInt("cardHolder"));

                list.add(bankCard);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public List<BankCard> readAll() {
        Connection conn = DataSource.getConnection();
        List<BankCard> list = new ArrayList<>();

        try {
            PreparedStatement query = conn.prepareStatement("SELECT * FROM bankCards;");
            ResultSet res = query.executeQuery();
            while (res.next()) {
                BankCard bankCard = new BankCard();
                bankCard.setId(res.getInt("id"));
                bankCard.setCardNumber(res.getString("cardNumber"));
                bankCard.setExpirationDate(res.getTimestamp("expirationDate").toLocalDateTime().toLocalDate());
                bankCard.setCVV(res.getString("CVV"));
                bankCard.setCardHolder(res.getInt("cardHolder"));

                list.add(bankCard);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void update(int id, BankCard bankCard) {
        Connection conn = DataSource.getConnection();
        BankCard original =  this.read(id);

        String statement = "UPDATE bankCards SET cardNumber = ?, expirationDate = ?, CVV = ? WHERE id = ? ;";

        try {
            PreparedStatement query = conn.prepareStatement(statement);
            query.setInt(4, id);

            if (bankCard.getCardNumber() != null) {
                query.setString(1, bankCard.getCardNumber());
            }
            else {
                query.setString(1, original.getCardNumber());
            }

            if (bankCard.getExpirationDate() != null) {
                query.setLong(2,  bankCard.getExpirationDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
            else {
                query.setLong(2,  bankCard.getExpirationDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }

            if (bankCard.getCVV() != null) {
                query.setString(3, bankCard.getCVV());
            }
            else {
                query.setString(3, original.getCVV());
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
            PreparedStatement query = conn.prepareStatement("DELETE FROM bankCards WHERE id = ? ;");
            query.setInt(1, id);
            query.executeUpdate();
            query.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
