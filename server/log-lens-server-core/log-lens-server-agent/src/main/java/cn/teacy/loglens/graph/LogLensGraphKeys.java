package cn.teacy.loglens.graph;

import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import lombok.AccessLevel;
import lombok.Getter;

public enum LogLensGraphKeys {

    INPUT("input"),

    CATEGORY_RESULT("category_result"),

    SUMMARY_RESULT("summary_result"),

    SUGGESTED_RESOLUTION("suggested_resolution"),

    ;

    @Getter
    private final String key;

    @Getter(AccessLevel.PRIVATE)
    private final KeyStrategy strategy;

    public String getWrappedKey() {
        return "{" + this.key + "}";
    }

    LogLensGraphKeys(String key) {
        this.key = key;
        this.strategy = new ReplaceStrategy();
    }

    LogLensGraphKeys(String key, KeyStrategy strategy) {
        this.key = key;
        this.strategy = strategy;
    }

    public static KeyStrategyFactory buildKeyStrategyFactory() {
        return () -> {
            var map = new java.util.HashMap<String, KeyStrategy>();
            for (LogLensGraphKeys value : LogLensGraphKeys.values()) {
                map.put(value.getKey(), value.getStrategy());
            }
            return map;
        };
    }

}
