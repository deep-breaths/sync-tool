package com.example.script.utils;

import com.example.script.constant.FolderType;
import com.example.script.functions.TriConsumer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author albert lewis
 * @date 2023/12/21
 */
public class FileUtils {
    private static final String DEFAULT_PATH="./";
    public static void saveToFile(String filePath, String fileName, List<String> lines){
        if(lines==null||lines.isEmpty()){
            return;
        }
        Path file = Path.of(filePath, fileName);
        try {
            if (Files.notExists(file)) {
                Files.createDirectories(file.getParent());
                Files.createFile(file);
            }
            Files.write(file, lines, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void saveToFile(String fileName, List<String> lines) {
        saveToFile(DEFAULT_PATH,fileName,lines);
    }

    /**
     *
     * @param folderPath
     */
    public static void getFile(String... folderPath){
        if (folderPath!=null){
            for (String s : folderPath) {
                getFile(s);
            }
        }
    }
    public static Map<String, Map<String, Map<String, List<String>>>> getFileByDefault(String... folder){
        Map<String,Map<String, Map<String, List<String>>>> result=new HashMap<>();
        if (folder!=null){
            for (String s : folder) {
                Map<String, Map<String, List<String>>> map = getFile(getPath(DEFAULT_PATH, s));
                result.put(s,map);
            }
        }
        return result;
    }
    public static Map<String, Map<String, List<String>>> getInitSQLByDefault(){
        return getFileByDefault(FolderType.INIT);
    }
    /**
     *
     * @param folder
     * @return 《SQL文件类型，《数据库名，sql语句》》
     */
    public static Map<String, Map<String, List<String>>> getFileByDefault(String folder){
        if (folder!=null){
                return getFile(getPath(DEFAULT_PATH, folder));
        }
        return null;
    }
    public static void getFileByPath(String folderPath,String... folder){
        if (folder!=null){
            for (String s : folder) {
                getFile(getPath(folderPath, s));
            }
        }
    }
    public static String getPath(String... paths) {
        Path result = Paths.get("");

        for (String path : paths) {
            result = result.resolve(path);
        }

        return result.toString();
    }

    /**
     *
     * @param folderPath
     * @return 《SQL文件类型，《数据库名，sql语句》》
     */
    public static Map<String, Map<String, List<String>>> getFile(String folderPath) {
        // 创建用于存储数据库名称和SQL列表的Map
        Map<String, Map<String, List<String>>> allSQLMap = new HashMap<>();
        try {
            // 获取文件夹路径的Path对象
            Path folder = Paths.get(folderPath);

            // 遍历文件夹及其子文件
            try (var pathStream = Files.walk(folder)) {
                pathStream.filter(Files::isRegularFile)
                          .forEach(path -> {
                              // 获取数据库名称
                              String databaseName = path.getParent().getFileName().toString();

                              // 获取不包含后缀的文件名
                              String sqlSaveType = path.getFileName().toString().replaceFirst("[.][^.]+$", "");

                              // 读取文件的所有行，并将每一行作为一个完整的SQL添加到列表中
                              try {
                                  List<String> sqlList = Files.readAllLines(path);

                                  List<String> list = new ArrayList<>();
                                  StringBuilder stringBuilder = new StringBuilder();

                                  for (String sql : sqlList) {
                                      stringBuilder.append(sql);

                                      if (sql.endsWith(";")) {
                                          list.add(stringBuilder.toString());
                                          stringBuilder.setLength(0);
                                      }
                                  }
                                  Map<String, List<String>> databaseSQLMap = allSQLMap.computeIfAbsent(sqlSaveType, k -> new HashMap<>());
                                  List<String> stringList = databaseSQLMap.computeIfAbsent(databaseName, k -> new ArrayList<>());
                                  stringList.addAll(list);


                              } catch (IOException e) {
                                  e.printStackTrace();
                              }
                          });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return allSQLMap;

    }

    /**
     *
     * @param allData 《SQL文件类型，《数据库名，sql语句》》
     * @param fileProcessor
     * @param filePath
     */
    public static void process(Map<String, Map<String, List<String>>> allData, TriConsumer<String, String,
            List<String>> fileProcessor, String filePath) {
        allData.forEach((sqlType, sqlMap) -> sqlMap.forEach((dbName, sqlList) -> {
            String fullPath = filePath + dbName;
            fileProcessor.accept(fullPath, STR."\{sqlType}.sql", sqlList);
        }));
    }

//    public static void processInitSQL(Map<String, List<String>> initSQL, TriConsumer<String, String, List<String>> fileProcessor, String filePath) {
//        initSQL.keySet().forEach(x -> {
//            if (x.startsWith("InitSQL_create_")) {
//                fileProcessor.accept(filePath,"create.sql", initSQL.get(x));
//            } else if (x.startsWith("InitSQL_insert_")) {
//                fileProcessor.accept(filePath,"insert.sql", initSQL.get(x));
//            }
//        });
//    }

    public static void processDiffDDL(Map<String, List<String>> diffDDL, TriConsumer<String, String, List<String>> fileProcessor, String filePath) {
        diffDDL.keySet().forEach(x -> {
            if (x.startsWith("DiffDDL_")) {
                fileProcessor.accept(filePath,"diff_create.sql", diffDDL.get(x));
            }
        });
    }

    public static void processDiffDML(Map<String, List<String>> diffDML, TriConsumer<String, String, List<String>> fileProcessor, String filePath) {
        diffDML.keySet().forEach(x -> {
            if (x.startsWith("DiffDML_insert_")) {
                fileProcessor.accept(filePath,"diff_insert.sql", diffDML.get(x));
            } else if (x.startsWith("DiffDML_update_")) {
                fileProcessor.accept(filePath,"diff_update.sql", diffDML.get(x));
            } else if (x.startsWith("DiffDML_delete_")) {
                fileProcessor.accept(filePath,"diff_delete.sql", diffDML.get(x));
            }
        });
    }

}
