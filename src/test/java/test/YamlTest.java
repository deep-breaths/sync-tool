package test;

import cn.hutool.json.JSONUtil;
import com.example.script.command.api.Api;
import com.example.script.command.domain.DataSourceParam;
import com.example.script.command.domain.Param;
import com.example.script.common.rule.BackupRule;
import com.example.script.common.rule.RuleUtils;
import com.example.script.common.rule.SqlUpdateRule;
import com.example.script.common.rule.condition.DBParam;
import com.example.script.utils.CommentUtils;
import com.example.script.utils.CommentUtils1;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.example.script.constant.DBConstant.*;
import static com.example.script.constant.DBConstant.DRIVER_CLASS_NAME;

/**
 * @author albert lewis
 * @date 2024/2/26
 */
public class YamlTest {
    private static final Api api = new Api();

    public static void main(String[] args) {

//        toInitSQL();
        String sql = "INSERT INTO `nacos`.`config_info` (`id`, `data_id`, `group_id`, `content`, `md5`, `gmt_create`," +
                " `gmt_modified`, `src_user`, `src_ip`, `app_name`, `tenant_id`, `c_desc`, `c_use`, `effect`, `type`, `c_schema`, `encrypted_data_key`) VALUES (121, 'transmit-mqtt-prod.yaml', 'DEFAULT_GROUP', '#端口配置\\nserver:\\n  port: 5081   #固定端口\\n  # port: ${randomServerPort.value[5081,5085]}  #随机端口\\n\\n\\nspring:\\n  datasource:\\n    dynamic:\\n      enable: true\\n    druid:\\n      # JDBC 配置(驱动类自动从url的mysql识别,数据源类型自动识别)\\n      core:\\n        url: jdbc:mysql://${env.mysql.host}/device-center?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai\\n        username: ${env.mysql.username}\\n        password: ${env.mysql.password}\\n        driver-class-name:  com.mysql.cj.jdbc.Driver\\n      dict:\\n        url: jdbc:mysql://${env.mysql.host}/dict-center?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai\\n        username: ${env.mysql.username}\\n        password: ${env.mysql.password}\\n        driver-class-name:  com.mysql.cj.jdbc.Driver\\n      log:\\n        url: jdbc:mysql://${env.mysql.host}/log-center?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai\\n        username: ${env.mysql.username}\\n        password: ${env.mysql.password}\\n        driver-class-name:  com.mysql.cj.jdbc.Driver\\n    #连接池配置(通常来说，只需要修改initialSize、minIdle、maxActive\\n    initial-size: 1\\n    max-active: 20\\n    min-idle: 1\\n    # 配置获取连接等待超时的时间\\n    max-wait: 60000\\n    #打开PSCache，并且指定每个连接上PSCache的大小\\n    pool-prepared-statements: true\\n    max-pool-prepared-statement-per-connection-size: 20\\n    validation-query: SELECT \\'x\\'\\n    test-on-borrow: false\\n    test-on-return: false\\n    test-while-idle: true\\n    #配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒\\n    time-between-eviction-runs-millis: 60000\\n    #配置一个连接在池中最小生存的时间，单位是毫秒\\n    min-evictable-idle-time-millis: 300000\\n    filters: stat,wall\\n    # WebStatFilter配置，说明请参考Druid Wiki，配置_配置WebStatFilter\\n    #是否启用StatFilter默认值true\\n    web-stat-filter.enabled: true\\n    web-stat-filter.url-pattern:  /*\\n    web-stat-filter.exclusions: \\\"*.js , *.gif ,*.jpg ,*.png ,*.css ,*.ico , /druid/*\\\"\\n    web-stat-filter.session-stat-max-count: 1000\\n    web-stat-filter.profile-enable: true\\n    # StatViewServlet配置\\n    #展示Druid的统计信息,StatViewServlet的用途包括：1.提供监控信息展示的html页面2.提供监控信息的JSON API\\n    #是否启用StatViewServlet默认值true\\n    stat-view-servlet.enabled: true\\n    #根据配置中的url-pattern来访问内置监控页面，如果是上面的配置，内置监控页面的首页是/druid/index.html例如：\\n    #http://110.76.43.235:9000/druid/index.html\\n    #http://110.76.43.235:8080/mini-web/druid/index.html\\n    stat-view-servlet.url-pattern:  /druid/*\\n    #允许清空统计数据\\n    stat-view-servlet.reset-enable:  true\\n    stat-view-servlet.login-username: admin\\n    stat-view-servlet.login-password: admin\\n    #StatViewSerlvet展示出来的监控信息比较敏感，是系统运行的内部情况，如果你需要做访问控制，可以配置allow和deny这两个参数\\n    #deny优先于allow，如果在deny列表中，就算在allow列表中，也会被拒绝。如果allow没有配置或者为空，则允许所有访问\\n    #配置的格式\\n    #<IP>\\n    #或者<IP>/<SUB_NET_MASK_size>其中128.242.127.1/24\\n    #24表示，前面24位是子网掩码，比对的时候，前面24位相同就匹配,不支持IPV6。\\n    #stat-view-servlet.allow=\\n    #stat-view-servlet.deny=128.242.127.1/24,128.242.128.1\\n    # Spring监控配置，说明请参考Druid Github Wiki，配置_Druid和Spring关联监控配置\\n    #aop-patterns= # Spring监控AOP切入点，如x.y.z.service.*,配置多个英文逗号分隔\\n  ################### mysq end ##########################\\n  #  zipkin:\\n  #    base-url: http://127.0.0.1:11008\\n  redis:\\n    ################### redis 单机版 start ##########################\\n    host: ${env.redis.host}\\n    port: ${env.redis.port}\\n    timeout: 6000\\n    database: 8\\n    lettuce:\\n      pool:\\n        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）,如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)\\n        max-idle: 8   # 连接池中的最大空闲连接 ，默认值也是8\\n        max-wait: 100 # # 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException\\n        min-idle: 2    # 连接池中的最小空闲连接 ，默认值也是0\\n      shutdown-timeout: 100ms\\n################### redis 单机版 end ##########################\\n#    cluster:\\n#      nodes: 130.75.131.237:7000,130.75.131.238:7000,130.75.131.239:7000,130.75.131.237:7001,130.75.131.238:7001,130.75.131.239:7001\\n#        #130.75.131.237:7000,130.75.131.238:7000,130.75.131.239:7000,130.75.131.237:7001,130.75.131.238:7001,130.75.131.239:7001\\n#        #192.168.3.157:7000,192.168.3.158:7000,192.168.3.159:7000,192.168.3.157:7001,192.168.3.158:7001,192.168.3.159:7001\\n#    timeout: 1000 # 连接超时时间（毫秒）\\n#    lettuce:\\n#      pool:\\n#        max-active: 10 # 连接池最大连接数（使用负值表示没有限制）,如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)\\n#        max-idle: 8   # 连接池中的最大空闲连接 ，默认值也是8\\n#        max-wait: 100 # # 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException\\n#        min-idle: 2    # 连接池中的最小空闲连接 ，默认值也是0\\n#      shutdown-timeout: 100ms\\n  data:\\n    mongodb:\\n      auto-index-creation: true\\n      mode: cluster\\n      uri: mongodb://${env.mongo.rs}/device_app?replicaSet=rs0&connectTimeoutMS=300000\\nmybatis-plus:\\n  global-config:\\n    banner: false\\n    db-config:\\n      logic-delete-value: \\\"UNIX_TIMESTAMP()\\\"\\n      logic-not-delete-value: \\\"0\\\"\\n  configuration:\\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\\n  mapper-locations: classpath*:com/hushan/**/dao/*.xml\\n\\nsecurity:\\n  oauth2:\\n    ignored: /users-anon/**  , /doc.html   ,/document.html ,/users/save\\n    token:\\n      store:\\n        type: redis\\n\\n#设置最大超时时间\\nribbon:\\n  ServerListRefreshInterval: 10  #刷新服务列表源的间隔时间\\n  OkToRetryOnAllOperations: true\\n  MaxAutoRetries: 1\\n  MaxAutoRetriesNextServer: 1\\n  ReadTimeout: 16000\\n  ConnectTimeout: 16000\\n  eager-load:\\n    enabled: true\\n    \\n#设置最大容错超时时间\\nhystrix:\\n  command:\\n    default:\\n      execution:\\n        timeout:\\n          enabled: true\\n        isolation:\\n          thread:\\n            timeoutInMilliseconds: 16000\\n\\nfeign:\\n  okhttp:\\n    enabled: true\\n  httpclient:\\n    enabled: false\\n    max-connections: 1000\\n    max-connections-per-route: 100\\n  compression:\\n    response:\\n      enabled: true\\n    request:\\n      enabled: true\\n\\nxxl:\\n  job:\\n    executor:\\n      logpath: ../logs/xxl-job/executor\\n      logretentiondays: 7\\n      port: 9997\\n      appname: ${spring.application.name}\\n    admin:\\n      addresses: http://127.0.0.1:8081/ \\n\\nlogging:\\n  level:\\n    org.hibernate: INFO\\n    org.hibernate.type.descriptor.sql.BasicBinder: TRACE\\n    org.hibernate.type.descriptor.sql.BasicExtractor: TRACE\\n    com.alibaba.nacos.client.config.impl: WARN\\n#    com.netflix: DEBUG                    #用于心跳检测输出的日志\\n\\nseata:\\n  enanble: true\\n  application-id: ${spring.application.name}\\n  tx-service-group: fsp_tx_group\\n  config:\\n    type: nacos\\n    nacos:\\n      namespace: 379ab54b-f04b-4b76-87b1-34102b908fab\\n      server-addr: ${env.nacos.host}\\n      group: SEATA_GROUP\\n  registry:\\n    type: nacos\\n    nacos:\\n      application: seata-server\\n      server-addr: ${env.nacos.host}\\n      namespace: 379ab54b-f04b-4b76-87b1-34102b908fab\\n  service:\\n    grouplist: \\n      default: ${env.seata.host}\\n    vgroupMapping:\\n      fsp_tx_group: default\\niot:\\n  mqtt:\\n    username: admin\\n    password: admin\\n    urls: \\n      - ${env.emqx.host}\\n    defaultTopics: /sys/device/register\\n    subscriber-client-id: ${spring.application.name}_${random.uuid}_subscription\\n    producer-client-id: ${spring.application.name}_${random.uuid}_production', 'a85a46394b60b4b8cb28382e2a669652', '2023-02-11 17:26:38', '2023-09-05 10:36:56', 'nacos', '192.168.30.59', '', 'adb19121-cfe9-4aa6-bb9f-a1fa18fdbd3e', '', '', '', 'yaml', '', '');\n";
        String yamlString = SqlUpdateRule.nacosRule(sql);
//        fillYamlWithComments(yamlString);
        fillYamlWithComments1(yamlString);

//        modifiedYaml(yamlString);
    }

    /**
     * 基于文件
     * @param yamlString
     */
    private static void fillYamlWithComments1(String yamlString) {
        writeToFile("./test.yaml", yamlString);
        File f = new File("./test.yaml");
        try {
            String s = FileUtils.readFileToString(f, StandardCharsets.UTF_8);

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);  // 设置 YAML 块风格
            Yaml yaml = new Yaml(options);

            // 记录 yaml 文件的注释信息
            CommentUtils1.CommentHolder holder = CommentUtils1.buildCommentHolder(f);

            LinkedHashMap map = yaml.loadAs(s, LinkedHashMap.class);
            map.put("key", "value");
            Map<String, Object> re = new LinkedHashMap<>();
            re.put("port", 5081);
            map.remove("server", re);

            s = yaml.dump(map);
            FileUtils.writeStringToFile(f, s, StandardCharsets.UTF_8);

            // 因为删掉了 name 行, 这里也同步移除一下, 防止错位
            holder.removeLine("^\\s*server:\\s.*$");
            holder.removeLine("^\\s*port:\\s.*$");

            // 填充注释信息
            holder.fillComments(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 基于字符串，失败，增加或删除时注释位置可能会跑偏
     * @param yamlString
     */
    private static void fillYamlWithComments(String yamlString) {

        writeToFile("./test0.yaml", yamlString);
        List<String> stringList = stringList(yamlString);
        // 记录 yaml 文件的注释信息
        CommentUtils.CommentHolder holder = CommentUtils.buildCommentHolder(stringList);
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);  // 设置 YAML 块风格
        Yaml yaml = new Yaml(options);

        LinkedHashMap map = yaml.loadAs(yamlString, LinkedHashMap.class);
        map.put("key", "value");
        Map<String, Object> re = new LinkedHashMap<>();
        re.put("port", 5081);
        map.remove("server", re);

        yamlString = yaml.dump(map);
        writeToFile("./test1.yaml", yamlString);

        // 因为删掉了 name 行, 这里也同步移除一下, 防止错位
        holder.removeLine("^\\s*server:\\s.*$");

        // 填充注释信息
        String s = holder.fillComments(stringList);

        writeToFile("./test2.yaml", s);
    }

    /**
     * 修改字符串并保存测试
     * @param yamlString
     */
    private static void modifiedYaml(String yamlString) {
        writeToFile("./test1.yaml", yamlString);
        // 创建 Yaml 对象
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(yamlString);

        // Modify the value
        data.put("key1", "new_value1");

        // Convert the map back to YAML string
//        String modifiedYamlString = dumpWithComments(data, yamlString);
//        System.out.println(modifiedYamlString);
//        writeToFile("./test22.yaml",modifiedYamlString);
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);  // 设置 YAML 块风格

        // 生成YAML格式的字符串
        yaml = new Yaml(options);
        String modifiedYamlString = yaml.dump(data);
        writeToFile("./test2.yaml", modifiedYamlString);

        Map<String, Object> yaml1 = readYaml("./test1.yaml");
        Map<String, Object> yaml2 = readYaml("./test2.yaml");
        boolean equals = yaml1.equals(yaml2);
        System.out.println(equals);
    }

    /**
     * 按换行符分割
     * @param str
     * @return
     */
    public static List<String> stringList(String str) {
        String[] lines = str.split("\\r?\\n");
        return new ArrayList<>(List.of(lines));
    }

    /**
     * 写入指定内容到指定文件
     * @param filePath
     * @param content
     */
    private static void writeToFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            System.out.println("Modified YAML written to file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取指定yaml文件的内容
     * @param filePath
     * @return
     */
    public static Map<String, Object> readYaml(String filePath) {
        Map<String, Object> data = new HashMap<>();

        try {
            // 读取 YAML 文件
            FileInputStream inputStream = new FileInputStream(filePath);

            // 创建 Yaml 对象
            Yaml yaml = new Yaml();

            // 解析 YAML 文件并将其转换为 Map 对象
            data = yaml.load(inputStream);

            // 使用解析后的数据
            // ...
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 写入注释测试，失败
     * @param data
     * @param originalYaml
     * @return
     */
    private static String dumpWithComments(Map<String, Object> data, String originalYaml) {
        StringBuilder modifiedYaml = new StringBuilder();
        String[] lines = originalYaml.split("\\r?\\n");
        for (String line : lines) {
            if (line.trim().startsWith("#")) {
                // If it's a comment, just append it
                modifiedYaml.append(line).append("\n");
            } else {
                // If it's not a comment, find the corresponding key in the data map
                String[] parts = line.split(":", 2);
                String key = parts[0].trim();
                if (data.containsKey(key)) {
                    // If the key exists in the data map, append the modified value
                    modifiedYaml.append(key).append(": ").append(data.get(key));
                    // If there's a comment for this key, append it
                    if (parts.length > 1) {
                        modifiedYaml.append("  ").append(parts[1].trim());
                    }
                    modifiedYaml.append("\n");
                }
            }
        }
        return modifiedYaml.toString();
    }

    private static void toInitSQL() {
        BackupRule build = new BackupRule()
                .buildDB("nacos", DBParam::isAllData)
                .build();

        RuleUtils.setRuleMap(build.getRuleMap());
        Param param = new Param();
        initSQl(param);
        api.execute(JSONUtil.toJsonStr(param));
    }

    private static void initSQl(Param param) {
        param.setType("init");
        param.setSourceType("ds");
        DataSourceParam dataSourceParam = new DataSourceParam();
        dataSourceParam.setType(param.getSourceType());
        dataSourceParam.setUrl(SOURCE_URL);
        dataSourceParam.setUserName(SOURCE_USERNAME);
        dataSourceParam.setPassword(SOURCE_PASSWORD);
        dataSourceParam.setDriverName(DRIVER_CLASS_NAME);
        param.setSourceDataParam(JSONUtil.toJsonStr(dataSourceParam));
    }
}
