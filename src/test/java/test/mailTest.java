package test;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author albert lewis
 * @date 2024/3/21
 */
public class mailTest {

    public static void main(String[] args) {
        MailAccount account = new MailAccount();
        account.setHost("smtp.qq.com");
        account.setPort(465);
        account.setAuth(true);
        account.setFrom("111");
        account.setPass("lrxyevyjtcegfihd");
        account.setSslEnable(true);
        account.setStarttlsEnable(false);
        MailUtil.send(account, "483119068@qq.com", "subject",
                     "content", true);

        }
    }
