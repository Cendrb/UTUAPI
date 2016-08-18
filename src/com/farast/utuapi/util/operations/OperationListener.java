package com.farast.utuapi.util.operations;

/**
 * Created by cendr_000 on 28.07.2016.
 */
public interface OperationListener {
    void started(Operation operation, OperationManager sourceManager);
    void ended(Operation operation, OperationManager sourceManager);
}
