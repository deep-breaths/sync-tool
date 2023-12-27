package com.example.script.product.strategy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
public abstract class DataBaseStrategy {
    public abstract Map<String, Map<String, List<String>>> toGetInitData(Connection sourceConn) throws SQLException;

}
