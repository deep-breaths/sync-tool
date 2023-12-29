package com.example.script.product.pgsql.strategy;


import com.example.script.common.strategy.SqlFileStrategy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@Component
@Scope("prototype")
public class PGSqlFileStrategy extends SqlFileStrategy {
    @Override
    public String getName() {
        return "pgsql";
    }

    @Override
    public void createDataSource(String url, String username, String password) {

    }

    @Override
    public Map<String, Map<String, List<String>>> toGetInitData() throws SQLException {
        return null;
    }


}
