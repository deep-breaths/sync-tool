package com.example.script.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author albert lewis
 * @date 2023/12/25
 */
public class DiffDDL {
   private Map<String, Map<String, List<String>>> diffSchemas;
   private Map<String, Map<String, Set<String>>> keys;

    public Map<String, Map<String, List<String>>> getDiffSchemas() {
        return diffSchemas;
    }

    public void setDiffSchemas(Map<String, Map<String, List<String>>> diffSchemas) {
        this.diffSchemas = diffSchemas;
    }

    public Map<String, Map<String, Set<String>>> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, Map<String, Set<String>>> keys) {
        this.keys = keys;
    }
}
