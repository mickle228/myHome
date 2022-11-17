package com.coursework.homeapp3_0.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends Configs {
    private Connection dbConnection;
    private static DatabaseHandler instance;

    private DatabaseHandler() {}

    public static DatabaseHandler getInstance() {
        if (instance == null) {
            return new DatabaseHandler();
        }
        return instance;
    }

    public Connection getDbConnection() throws SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":"
                + dbPort + "/" + dbName;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dbConnection = DriverManager.getConnection(connectionString, dbUser, "1234");
        return dbConnection;
    }

    public void AddApplianceInDb(Appliance appliance) throws SQLException {
        Connection connection = null;
        try {
            connection = getDbConnection();
            connection.setAutoCommit(false);
            int typeID = insertCategory(connection, appliance.getType(), Const.TYPE_TABLE);
            int companyID = insertCategory(connection, appliance.getCompany(), Const.COMPANY_TABLE);
            String insert = "INSERT INTO " + Const.APPLIANCE_TABLE + " VALUES (default, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, appliance.getModel());
            preparedStatement.setInt(2, typeID);
            preparedStatement.setInt(3, companyID);
            preparedStatement.setInt(4, appliance.getPower());
            preparedStatement.setString(5, appliance.getStatus());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            connection.close();
        }
    }

    private int checkUniq(Connection connection, String value, String table) throws SQLException {
        String select = "SELECT * FROM " + table + " WHERE " + table + " = ?";
        PreparedStatement statement = connection.prepareStatement(select);
        statement.setString(1, value);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next() ? resultSet.getInt(1) : -1;
    }

    private int insertCategory(Connection connection, String value, String table) throws SQLException {
        int key = checkUniq(connection, value, table);
        if (key != -1) {
            return key;
        }
        String insert = "INSERT INTO " + table + " VALUES (default, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, value);
        preparedStatement.executeUpdate();
        ResultSet rsKey = preparedStatement.getGeneratedKeys();
        return rsKey.next() ? rsKey.getInt(1) : 0;
    }


    public void changeStatusInDb(Appliance appliance) {
        try (Connection connection = getDbConnection()) {
            String update = "UPDATE " + Const.APPLIANCE_TABLE + " SET " + Const.APPLIANCE_STATUS
                    + " = ?" + " WHERE " + Const.APPLIANCE_MODEL + "=?";
            PreparedStatement preparedStatement = connection.prepareStatement(update);
            preparedStatement.setString(1, appliance.getStatus());
            preparedStatement.setString(2, appliance.getModel());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Appliance> getAllInRange(int fRange, int sRange) {
        List<Appliance> appliances = new ArrayList<>();
        try (Connection connection = getDbConnection()) {
            String select = "SELECT * FROM " + Const.APPLIANCE_TABLE +
                    " INNER  JOIN " + Const.TYPE_TABLE + " ON " +
                    Const.TYPE_TABLE + "." + Const.TYPE_ID + " = " + Const.APPLIANCE_TABLE + "." + Const.TYPE_ID +
                    " INNER JOIN " + Const.COMPANY_TABLE + " ON " +
                    Const.COMPANY_TABLE + "." + Const.COMPANY_ID + " = " + Const.APPLIANCE_TABLE + "." + Const.COMPANY_ID +
                    " WHERE " + Const.APPLIANCE_POWER + " >= ? AND " + Const.APPLIANCE_POWER + " <= ? ORDER BY " + Const.APPLIANCE_POWER;
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            preparedStatement.setInt(1, fRange);
            preparedStatement.setInt(2, sRange);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                appliances.add(new Appliance(resultSet.getString(Const.TYPE), resultSet.getString(Const.APPLIANCE_MODEL),
                        resultSet.getString(Const.COMPANY), resultSet.getInt(Const.APPLIANCE_POWER), resultSet.getString(Const.APPLIANCE_STATUS)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appliances;
    }

    public List<Appliance> getAllPluggedFromDb() {
        List<Appliance> appliances = new ArrayList<>();
        try (Connection connection = getDbConnection()) {
            String select = "SELECT * FROM " + Const.APPLIANCE_TABLE +
                    " INNER  JOIN " + Const.TYPE_TABLE + " ON " +
                    Const.TYPE_TABLE + "." + Const.TYPE_ID + " = " + Const.APPLIANCE_TABLE + "." + Const.TYPE_ID +
                    " INNER JOIN " + Const.COMPANY_TABLE + " ON " +
                    Const.COMPANY_TABLE + "." + Const.COMPANY_ID + " = " + Const.APPLIANCE_TABLE + "." + Const.COMPANY_ID +
                    " WHERE " + Const.APPLIANCE_STATUS + " = ? ORDER BY " + Const.APPLIANCE_POWER;
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            preparedStatement.setString(1, "on");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                appliances.add(new Appliance(resultSet.getString(Const.TYPE), resultSet.getString(Const.APPLIANCE_MODEL),
                        resultSet.getString(Const.COMPANY), resultSet.getInt(Const.APPLIANCE_POWER), resultSet.getString(Const.APPLIANCE_STATUS)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appliances;
    }

    public List<Appliance> getAllFromDb() {
        List<Appliance> appliance = new ArrayList<>();
        try (Connection connection = getDbConnection()) {
            String select = "SELECT " + Const.TYPE + ", " + Const.APPLIANCE_MODEL +
                    ", " + Const.COMPANY + ", " + Const.APPLIANCE_POWER + ", " + Const.APPLIANCE_STATUS +
                    " FROM " + Const.APPLIANCE_TABLE +
                    " INNER  JOIN " + Const.TYPE_TABLE + " ON " +
                    Const.TYPE_TABLE + "." + Const.TYPE_ID + " = " + Const.APPLIANCE_TABLE + "." + Const.TYPE_ID +
                    " INNER JOIN " + Const.COMPANY_TABLE + " ON " +
                    Const.COMPANY_TABLE + "." + Const.COMPANY_ID + " = " + Const.APPLIANCE_TABLE + "." + Const.COMPANY_ID +
                    " ORDER BY " + Const.APPLIANCE_POWER;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            while (resultSet.next()) {
                appliance.add(new Appliance(resultSet.getString(Const.TYPE), resultSet.getString(Const.APPLIANCE_MODEL),
                        resultSet.getString(Const.COMPANY), resultSet.getInt(Const.APPLIANCE_POWER),
                        resultSet.getString(Const.APPLIANCE_STATUS)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appliance;
    }

    public void removeFromDb(Appliance appliance) {
        try (Connection connection = getDbConnection()) {
            String delete = "DELETE FROM " + Const.APPLIANCE_TABLE + " WHERE " + Const.APPLIANCE_MODEL +
                    " = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.setString(1, appliance.getModel());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
