package com.farast.utuapi.util.functional_interfaces;

/**
 * Created by cendr_000 on 25.07.2016.
 */
public interface Action<T> {
    void accept(T parameter);
}
