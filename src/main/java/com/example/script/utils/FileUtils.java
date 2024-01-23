package com.example.script.utils;

import com.example.script.constant.FolderType;
import com.example.script.functions.TriConsumer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author albert lewis
 * @date 2023/12/21
 */
public class FileUtils {
    public static final String DEFAULT_PATH="./";
    public static void saveToFile(String filePath, String fileName, List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return;
        }

        Path file = Path.of(filePath, fileName);

        try {
            if (Files.notExists(file)) {
                Files.createDirectories(file.getParent());
                Files.createFile(file);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.APPEND)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
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
    public static void getSqlFile(String... folderPath){
        if (folderPath!=null){
            for (String s : folderPath) {
                getSqlFile(s);
            }
        }
    }
    public static Map<String, Map<String, Map<String, List<String>>>> getFileByDefault(String... folder){
        Map<String,Map<String, Map<String, List<String>>>> result=new HashMap<>();
        if (folder!=null){
            for (String s : folder) {
                Map<String, Map<String, List<String>>> map = getSqlFile(getPath(DEFAULT_PATH, s));
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
                return getSqlFile(getPath(DEFAULT_PATH, folder));
        }
        return null;
    }

    public static Map<String, Map<String, List<String>>> getInit (String path){
        if (path==null||path.isBlank()){
            path=DEFAULT_PATH;
        }
        return FileUtils.getSqlFile(FileUtils.getPath(path, FolderType.INIT));
    }
    public static Map<String, Map<String, List<String>>> getFileByPath(String folderPath, String folder){
        if (folder!=null){
            return getSqlFile(getPath(folderPath, folder));
        }
        return null;
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
    public static Map<String, Map<String, List<String>>> getSqlFile(String folderPath) {
        // 创建用于存储数据库名称和SQL列表的Map
        Map<String, Map<String, List<String>>> allSQLMap = new HashMap<>();
        try {
            // 获取文件夹路径的Path对象
            Path folder = Paths.get(folderPath);
            if (!Files.exists(folder) || !Files.isDirectory(folder)) {
                return allSQLMap;
            }

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
     * @param allData        《SQL文件类型，《数据库名，sql语句》》
     * @param fileProcessor
     * @param filePath
     * @param suffixFilePath
     */
    public static void process(Map<String, Map<String, List<String>>> allData, TriConsumer<String, String,
            List<String>> fileProcessor, String filePath, String suffixFilePath) {
        allData.forEach((sqlType, sqlMap) -> sqlMap.forEach((dbName, sqlList) -> {
            String fullPath = getPath(filePath, suffixFilePath,dbName);
            fileProcessor.accept(fullPath, sqlType+".sql", sqlList);
        }));
    }


    public static void deleteFile(String folderPath){
        File folder = new File(folderPath);
        if (folder.exists()) {
            try (Stream<Path> walk = Files.walk(Paths.get(folderPath))) {
                walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("文件夹删除成功");
        } else {
            System.out.println("文件夹不存在");
        }
    }
public static String getJsonFile(String filePath){
    String content=null;
    try {
        content = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);

    } catch (IOException e) {
        e.printStackTrace();
    }
    return content;
}
}
