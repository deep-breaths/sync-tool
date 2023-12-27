package com.example.script.constant;

import com.example.script.factory.DataBaseFactory;
import com.example.script.factory.DataSourceFactory;
import com.example.script.factory.SqlFileFactory;
import com.example.script.utils.SpringUtils;

import java.util.function.Function;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
public enum DataSourceTypeEnum {
    DATA_BASE(0, "数据库", (_) -> {
        return SpringUtils.getBean(DataBaseFactory.class);
    }),
    SQL_FILE(1, "sql文件", (_) -> {

        return SpringUtils.getBean(SqlFileFactory.class);
    });


    private Integer code;
    private String desc;
    private Function<String, DataSourceFactory> factory;

    DataSourceTypeEnum(Integer code, String desc, Function<String, DataSourceFactory> factory) {
        this.code = code;
        this.desc = desc;
        this.factory = factory;
    }

    public static DataSourceFactory toGetFactory(Integer code) {
        DataSourceTypeEnum actionType = findFactoryTypeByCode(code);
        if (actionType != null) {
            return actionType.factory.apply(null);
        } else {
            throw new RuntimeException("不支持该查询");
        }
    }

    private static DataSourceTypeEnum findFactoryTypeByCode(Integer code) {
        for (DataSourceTypeEnum actionType : values()) {
            if (actionType.code.equals(code)) {
                return actionType;
            }
        }
        return null;
    }


}
