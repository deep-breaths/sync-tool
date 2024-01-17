package test.calcite;

import cn.hutool.core.date.StopWatch;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.calcite.adapter.jdbc.JdbcSchema;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.example.script.constant.DBConstant.*;

/**
 * @author albert lewis
 * @date 2024/1/17
 */
public class test {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("calcite 任务");

//        test1();
        test2();

        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }

    public static void test1() throws SQLException, ClassNotFoundException {
        // check driver exist

        // the properties for calcite connection
        Properties info = new Properties();
        info.setProperty("lex", "JAVA");
        info.setProperty("remarks", "true");
        // SqlParserImpl can analysis sql dialect for sql parse
        info.setProperty("parserFactory", "org.apache.calcite.sql.parser.impl.SqlParserImpl#FACTORY");

        // create calcite connection and schema
        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        System.out.println(calciteConnection.getProperties());
        SchemaPlus rootSchema = calciteConnection.getRootSchema();

        DataSource dataSource = createDataSource();
        // get database list
        try (ResultSet rs = dataSource.getConnection().getMetaData().getCatalogs()) {
            while (rs.next()) {
                String databaseName = rs.getString(1);
                System.out.println("Database: " + databaseName);

                // create schema for each database
                Schema schema = JdbcSchema.create(rootSchema, databaseName, dataSource, null, databaseName);
                rootSchema.add(databaseName, schema);

                // get table list for each database
                try (ResultSet tablesRs = dataSource
                        .getConnection()
                        .getMetaData()
                        .getTables(databaseName, null, null, null)) {
                    while (tablesRs.next()) {
                        String tableName = tablesRs.getString(3);
                        System.out.println("Table: " + tableName);
                    }
                }
            }
        }
        connection.close();
    }

    public static void test2() throws SQLException{
        // check driver exist

        // the properties for calcite connection
        Properties info = new Properties();
        info.setProperty("lex", "JAVA");
        info.setProperty("remarks", "true");
        // SqlParserImpl can analysis sql dialect for sql parse
        info.setProperty("parserFactory", "org.apache.calcite.sql.parser.impl.SqlParserImpl#FACTORY");

        // create calcite connection and sysSchema
        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();


        Map<String, Object> operand = new HashMap<>();
        operand.put("jdbcUrl", SOURCE_URL);
        operand.put("jdbcUser", SOURCE_USERNAME);
        operand.put("jdbcPassword", SOURCE_PASSWORD);

        // create sysSchema for database
        String rootSchemaName = "source";
        JdbcSchema sysSchema = JdbcSchema.create(rootSchema, rootSchemaName, operand);
        rootSchema.add(rootSchemaName,sysSchema);

        FrameworkConfig config = Frameworks.newConfigBuilder()
                                           .defaultSchema(rootSchema)
                                           .build();

        // create RelBuilder
        RelBuilder relBuilder = RelBuilder.create(config);
        // get database list
        DataSource dataSource = sysSchema.getDataSource();
        try (ResultSet rs = dataSource.getConnection().getMetaData().getCatalogs()) {
            while (rs.next()) {
                String databaseName = rs.getString(1);
                System.out.println("Database: " + databaseName);
                // create schema for each database
                Schema schema = JdbcSchema.create(rootSchema, databaseName, dataSource, databaseName, databaseName);
                rootSchema.add(databaseName, schema);

                for (String tableName : schema.getTableNames()) {
                    RelNode structureNode = relBuilder.scan(databaseName, tableName).limit(0,0).build();
                    RelDataType structureType = structureNode.getRowType();
                    System.out.println("  table: "+tableName);
                    System.out.println(structureType.toString());
                }
            }
        }

        connection.close();
    }

    private static DataSource createDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(SOURCE_URL);
        dataSource.setUser(SOURCE_USERNAME);
        dataSource.setPassword(SOURCE_PASSWORD);
        return dataSource;
    }

}
