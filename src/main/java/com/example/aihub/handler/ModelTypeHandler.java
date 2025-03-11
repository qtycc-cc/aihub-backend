package com.example.aihub.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import com.example.aihub.pojo.ModelType;

@MappedTypes(ModelType.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ModelTypeHandler extends BaseTypeHandler<ModelType> {

    @Override
    public ModelType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String model = rs.getString(columnName);
        return ModelType.fromString(model);
    }

    @Override
    public ModelType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String model = rs.getString(columnIndex);
        return ModelType.fromString(model);
    }

    @Override
    public ModelType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String model = cs.getString(columnIndex);
        return ModelType.fromString(model);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ModelType parameter, JdbcType jdbcType)
            throws SQLException {
                ps.setString(i, parameter.getValue());
    }
}
