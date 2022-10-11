package com.world_quant.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.val;

public class TableCopier {
    
    private final CopyingArgs ownArgs;
    private final HashSet<String> processed;
    private final Connection source;
    private final Connection destination;

    public TableCopier(CopyingArgs ca) throws SQLException, ClassNotFoundException {
        val a = new org.postgresql.Driver();
        processed = new HashSet<String>();
        ownArgs = ca;
        source = DriverManager.getConnection(ca.getFromDb());
        destination = DriverManager.getConnection(ca.getToDb());
    }

    private String buildInsertBy(String table, ResultSetMetaData rsmd) throws SQLException {
        val sql = new StringBuilder();
        sql.append("INSERT INTO " + ownArgs.getToSchema() + "." + table + " (");
        val colNames = new ArrayList<String>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            colNames.add(rsmd.getColumnName(i));
        }

        val c = Collectors.joining(", ");
        sql.append(colNames.stream().collect(c));
        sql.append(") VALUES (");
        sql.append(colNames.stream().map(s -> "?").collect(c));
        sql.append(")");
        return sql.toString();
    }

    private void copy(String table) throws RuntimeException {
        if (processed.contains(table)) {
            System.out.println("Warning, " + table + " already copied");
            return;
        }

        try {
            val rs = source.createStatement().executeQuery("SELECT * FROM " + table);
            val rsmd = rs.getMetaData();
            val insertSql = buildInsertBy(table, rsmd);
            PreparedStatement ps = destination.prepareStatement(insertSql);

            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    ps.setObject(i, rs.getObject(i));
                }
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void copyAll() {
        Stream.of(ownArgs.getFromTables()).forEach(table -> copy(table));
    }

}
