package com.lims.api.audit.sql;

import com.lims.api.audit.domain.SqlColumn;
import com.lims.api.audit.domain.SqlEntity;
import com.lims.api.audit.domain.SqlParameter;
import com.lims.api.audit.domain.SqlRow;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AuditSqlRepository {

    private final DataSource dataSource;
    private final AuditSqlManager sqlManager;
    private final AuditSqlProvider sqlProvider;

    public AuditSqlRepository(DataSource dataSource, AuditSqlManager sqlManager, AuditSqlProvider sqlProvider) {
        this.dataSource = dataSource;
        this.sqlManager = sqlManager;
        this.sqlProvider = sqlProvider;
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    public List<SqlParameter> getSqlParameters(Class<?> entityClazz, Map<String, Object> parameter) {
        SqlEntity sqlEntity = sqlManager.get(entityClazz.getName());
        return sqlProvider.getSqlParameters(sqlEntity, parameter);
    }

    public List<SqlRow> findAllById(Class<?> entityClazz, List<SqlParameter> sqlParameters) {
        String commentSuffix = AbstractSqlGenerator.COMMENT_SUFFIX;
        SqlEntity sqlEntity = sqlManager.get(entityClazz.getName());
        String conditionClause = sqlProvider.makeConditionClause(sqlParameters);

        String sql = sqlEntity.getSelectClause() + conditionClause;

        List<SqlRow> result = new ArrayList<>();
        try {
            PreparedStatement statement = getConnection().prepareStatement(sql);

            for (int i = 0; i < sqlParameters.size(); i++) {
                statement.setString(i + 1, String.valueOf(sqlParameters.get(i).getData()));
            }

            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            String columnLabel = null;

            while (resultSet.next()) {
                SqlRow row = new SqlRow();
                for (int i = 1; i <= columnCount; i++) {
                    columnLabel = metaData.getColumnLabel(i);

                    if (columnLabel.contains(commentSuffix)) {
                        continue;
                    }

                    SqlColumn column = new SqlColumn();
                    column.setData(resultSet.getString(columnLabel));
                    column.setComment(resultSet.getString(columnLabel + commentSuffix));

                    row.put(metaData.getColumnName(i), column);
                }
                result.add(row);
            }
        } catch(SQLException e) {
            throw new InvalidDataAccessResourceUsageException(e.getMessage(), e.getCause());
        }
        return result;
    }

}