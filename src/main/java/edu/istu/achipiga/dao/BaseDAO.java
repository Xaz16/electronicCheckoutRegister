package edu.istu.achipiga.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class BaseDAO<T> {
    @Getter
    @Setter
    public DAOContext context = new DAOContext();

    @Getter
    @Setter
    private T currentInstance = null;
    private static final String DB_URL = "jdbc:sqlite:checkout_register.db";

    protected static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    abstract T getById(int id);
    abstract List<T> getAll();
    abstract T save(T instance);
    abstract void delete(T instance);
    abstract T getCurrent();
}
