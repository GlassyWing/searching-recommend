package org.manlier.srapp.history;

import org.junit.Test;
import org.manlier.srapp.common.PhoenixPool$;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class HistoryDAOTest {

    @Test
    public void testGenerateStat() {
        String sql = HistoryDBUtil.generateUpsertSQL("0", "A1", "B2", 3);
        System.out.println(sql);
    }

    @Test
    public void addHistoryRecord() throws SQLException {
        String sql = HistoryDBUtil.generateUpsertSQL("0", "A1", "B2", 3);
        Connection connection = PhoenixPool$.MODULE$.getConnection();
        Statement stat = connection.createStatement();
        connection.setAutoCommit(false);
        stat.addBatch(sql);
        stat.executeBatch();
        connection.commit();
    }
}
