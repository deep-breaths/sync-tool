package com.example.script.test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @author albert lewis
 * @date 2023/12/20
 */
public class ResultSetUtils {
    public static List<String> resultSetToList(ResultSet resultSet) {
        List<String> resultList = new ArrayList<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    row.append(resultSet.getString(i)).append("\t");
                }
                resultList.add(row.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}