package com.lims.api.audit.service.impl;

import com.lims.api.audit.domain.SqlColumn;
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

    private final String commentSuffix = AbstractSqlGenerator.COMMENT_SUFFIX;

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
        String columnLabel = null;

        while (resultSet.next()) {
            SqlRow row = new SqlRow();
            for (int i=1; i <= columnCount; i++) {
                columnLabel = metaData.getColumnLabel(i);

                if (columnLabel.contains(commentSuffix)) {
                    continue;
                }

                SqlColumn column = new SqlColumn();
                column.setValue(resultSet.getString(columnLabel));
                System.out.println(columnLabel);
                System.out.println(columnLabel + commentSuffix);
                column.setComment(resultSet.getString(columnLabel + commentSuffix));

                row.put(metaData.getColumnName(i), column);
            }
            result.add(row);
        }
        return result;
    }

}