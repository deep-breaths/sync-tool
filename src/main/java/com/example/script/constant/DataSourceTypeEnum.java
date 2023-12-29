package com.example.script.constant;

import com.example.script.common.factory.DataBaseFactory;
import com.example.script.common.factory.DataSourceFactory;
import com.example.script.common.factory.SqlFileFactory;
import com.example.script.common.utils.SpringUtils;
import lombok.Getter;

import java.util.function.Supplier;

/**
 * @author albert lewis
 * @date 2023/12/27
 */
@Getter
public enum DataSourceTypeEnum {
    DATA_BASE(0, "数据库", () -> {
        return SpringUtils.getBean(DataBaseFactory.class);
    }),
    SQL_FILE(1, "sql文件", () -> {

        return SpringUtils.getBean(SqlFileFactory.class);
    });


    private final Integer code;
    private final String desc;
    private final Supplier<DataSourceFactory> factory;

    DataSourceTypeEnum(Integer code, String desc, Supplier<DataSourceFactory> factory) {
        this.code = code;
        this.desc = desc;
        this.factory = factory;
    }

    public static DataSourceFactory toGetFactory(Integer code) {
        DataSourceTypeEnum actionType = findFactoryTypeByCode(code);
        if (actionType != null) {
            return actionType.factory.get();
        } else {
            throw new RuntimeException("不支持该类型");
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
