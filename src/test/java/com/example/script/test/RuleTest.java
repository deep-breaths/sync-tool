package com.example.script.test;

import com.example.script.common.rule.DBRule;
import com.example.script.common.rule.RuleUtils;

import java.util.Map;

/**
 * @author albert lewis
 * @date 2024/1/12
 */
public class RuleTest {

    public static void main(String[] args) {
        Map<String, DBRule> ruleMap = RuleUtils.getRuleMap();
        System.out.println(ruleMap);
    }
}
