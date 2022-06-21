package com.lims.api.audit.service.impl;

import com.lims.api.audit.domain.SqlParameter;
import com.lims.api.audit.domain.SqlRow;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class AuditRepository {

    private final DataSource dataSource;

    public AuditRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Transactional
    public List<SqlRow> findAllById(AuditSqlProvider sqlProvider, Object[] parameters) throws SQLException {
        List<SqlParameter> sqlParameters = sqlProvider.getSqlParameter(parameters);
        String sql = sqlProvider.generateSelectSql(sqlParameters);
        PreparedStatement statement = getConnection().prepareStatement(sql);

        for (int i=0; i < sqlParameters.size(); i++) {
            statement.setString(i + 1, String.valueOf(sqlParameters.get(i).getValue()));
        }

        List<SqlRow> result = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            SqlRow column = new SqlRow();
            for (int i=1; i <= columnCount; i++) {
                column.put(metaData.getColumnName(i), resultSet.getString(i));
            }
            result.add(column);
        }
        return result;
    }

}