package com.lims.api.audit.service.impl;

import com.lims.api.audit.domain.SqlColumn;
import com.lims.api.audit.domain.SqlParameter;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuditTrailRepository {

    DataSource dataSource;

    public AuditTrailRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Transactional
    public List<SqlColumn> findAllById(String sql, List<SqlParameter> parameters) throws SQLException {
        List<SqlColumn> result = new ArrayList<>();
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);

        for (int i=0; i < parameters.size(); i++) {
            statement.setString(i + 1, String.valueOf(parameters.get(i).getValue()));
        }

        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            SqlColumn column = new SqlColumn();
            for (int i=0; i < columnCount; i++) {
                metaData.getColumnName(i);
                column.put(metaData.getColumnName(i), resultSet.getString(i));
            }
            result.add(column);
        }
        return result;
    }

    public String makeSelectSql(String tableName, List<String> columnNames, List<SqlParameter> sqlParameters) {
        String conditionClause = sqlParameters.stream().map(k -> k.getName() + " = ?").collect(Collectors.joining(" AND "));
        String columns = String.join(", ", columnNames);
        return String.format("SELECT %s FROM %s WHERE %s", columns, tableName, conditionClause);
    }

}
