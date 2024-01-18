package com.example.script.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.util.List;

/**
 * @author albert lewis
 * @date 2024/1/18
 */
public class StringListTypeHandler implements TypeHandler<List<String>> {

private static final ObjectMapper objectMapper = new ObjectMapper();

@Override
public void setParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
    if (parameter != null) {
        ps.setString(i, toJson(parameter));
    } else {
        ps.setNull(i, Types.VARCHAR);
    }
}

@Override
public List<String> getResult(ResultSet rs, String columnName) throws SQLException {
    String json = rs.getString(columnName);
    return fromJson(json);
}

@Override
public List<String> getResult(ResultSet rs, int columnIndex) throws SQLException {
    String json = rs.getString(columnIndex);
    return fromJson(json);
}

@Override
public List<String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
    String json = cs.getString(columnIndex);
    return fromJson(json);
}

private String toJson(List<String> list) {
    try {
        return objectMapper.writeValueAsString(list);
    } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to convert List<String> to JSON", e);
    }
}

    private List<String> fromJson(String json) {
        if (json != null && !json.isEmpty()) {
            try {
                JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, String.class);
                return objectMapper.readValue(json, javaType);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to convert JSON to List<String>", e);
            }
        }
        return null;
    }
}
