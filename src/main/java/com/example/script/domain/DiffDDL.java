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
   private Map<String, Map<String, Map<String, Set<String>>>> sourceKeys;

    public Map<String, Map<String, List<String>>> getDiffSchemas() {
        return diffSchemas;
    }

    public void setDiffSchemas(Map<String, Map<String, List<String>>> diffSchemas) {
        this.diffSchemas = diffSchemas;
    }

    public Map<String, Map<String, Map<String, Set<String>>>> getSourceKeys() {
        return sourceKeys;
    }

    public void setSourceKeys(Map<String, Map<String, Map<String, Set<String>>>> sourceKeys) {
        this.sourceKeys = sourceKeys;
    }
}
