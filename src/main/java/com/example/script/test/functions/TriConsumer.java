package com.example.script.test.functions;

/**
 * @author albert lewis
 * @date 2023/12/21
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}
