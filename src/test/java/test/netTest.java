package test;

import cn.hutool.core.date.StopWatch;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author albert lewis
 * @date 2024/1/8
 */
public class netTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        StopWatch stopWatch=new StopWatch();
        stopWatch.start("任务");
//        pingAsync2();

        test();

        //ping();

        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));

    }

    private static void ping() {
        String ipAddress = "192.168.30.";
        HttpClient client = HttpClient.newBuilder()
                                      .followRedirects(HttpClient.Redirect.ALWAYS)
                                      .build();

        for (int i = 116; i < 255; i++) {
            String host = ipAddress + i;
            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(URI.create("http://" + host))
                                             .timeout(Duration.ofSeconds(5))
                                             .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    System.out.println("---------------" + host);
                    System.out.println(response.body());
                }
            } catch (IOException | InterruptedException e) {
                System.err.println(host + "错误");
            }
        }

    }

    private static void pingAsync2(){
        String ipAddress = "192.168.30.";
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor(); // 创建自定义线程池
        for (int i = 3; i < 255; i++) {
            String host = ipAddress + i;

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    cn.hutool.http.HttpResponse response = cn.hutool.http.HttpRequest.get("http://" + host)
                                                                                     .setFollowRedirects(true) // 启用重定向
                                                                                     .timeout(1000 * 5).execute();
                    if (response.getStatus() == 200) {
                        System.out.println("************" + host);
                        System.out.println(response.body());
                    }
                } catch (Exception e) {
                    System.err.println(host + "错误");
                }
            },executor);

            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allOf.get(); // 等待所有的CompletableFuture完成
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown(); // 关闭线程池
    }

    private static void pingAsync() throws ExecutionException, InterruptedException {
        String ipAddress = "http://192.168.30.";

        ExecutorService executor = Executors.newFixedThreadPool(8);
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                                      .executor(executor)
                                      .build();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 144; i < 145; i++) {
            String host = ipAddress + i;
            HttpRequest request = HttpRequest.newBuilder(URI.create(host)).timeout(Duration.ofSeconds(5)).GET().build();

                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                      .whenComplete((stringHttpResponse, throwable) -> {
                          if (throwable != null) {
                              System.out.println(host+"错误");
                              //throwable.printStackTrace();
                          }
                          if (stringHttpResponse != null) {
                              System.out.println("******************"+host+"\n"+stringHttpResponse.body());
                          }
                          countDownLatch.countDown();
                      });

        }



        //阻塞直至所有请求完成
        countDownLatch.await();
        client.close();
        executor.shutdown();
    }
private static void test(){
    SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);

    repository.console("create table t_emp(emp_id bigint, name varchar(20));");
    repository.console("create table t_org(org_id bigint, name varchar(20));");

    String sql = "SELECT emp_id, a.name AS emp_name, org_id, b.name AS org_name\n" +
            "FROM t_emp a\n" +
            "\tINNER JOIN t_org b ON a.emp_id = b.org_id";

    List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
    System.out.println(stmtList.size());

    SQLSelectStatement stmt = (SQLSelectStatement) stmtList.get(0);
    SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();

// 大小写不敏感
    System.out.println(queryBlock.findTableSource("A"));

    System.out.println(queryBlock.findTableSourceWithColumn("emp_id"));

// 使用repository做column resolve
    repository.resolve(stmt);

    System.out.println(queryBlock.findTableSourceWithColumn("emp_id"));

    SQLExprTableSource tableSource = (SQLExprTableSource) queryBlock.findTableSourceWithColumn("emp_id");
    System.out.println(tableSource.getSchemaObject());

    SQLCreateTableStatement createTableStmt = (SQLCreateTableStatement) tableSource.getSchemaObject().getStatement();
    System.out.println(createTableStmt);

    SQLSelectItem selectItem = queryBlock.findSelectItem("org_name");
    System.out.println(selectItem);
    SQLPropertyExpr selectItemExpr = (SQLPropertyExpr) selectItem.getExpr();
    SQLColumnDefinition column = selectItemExpr.getResolvedColumn();
    System.out.println(column);
    System.out.println("name::"+column.getName().toString());
    System.out.println("t_org::"+(((SQLCreateTableStatement)column.getParent()).getName().toString()));

    System.out.println(queryBlock.findTableSource("B")+"\n ------\n"+ selectItemExpr.getResolvedTableSource());
}

}
