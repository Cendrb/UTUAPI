package com.farast.utuapi.util.operations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cendr_000 on 28.07.2016.
 */
public class OperationManager {
    private List<Operation> runningOperations = new ArrayList<>();

    private List<OperationListener> operationListeners = new ArrayList<>();

    public void addOperationListener(OperationListener listener) {
        operationListeners.add(listener);
    }

    public void clearOperationListeners() {
        operationListeners.clear();
    }

    public void setOperationListener(OperationListener listener) {
        operationListeners.clear();
        operationListeners.add(listener);
    }

    public void startOperation(Operation operation) {
        if (runningOperations.contains(operation))
            throw new OperationAlreadyRunning(operation);
        runningOperations.add(operation);
        for (OperationListener listener : operationListeners)
            listener.started(operation, this);
    }

    public void endOperation(Operation operation) {
        if (!runningOperations.contains(operation))
            throw new OperationNotRunningException(operation);
        runningOperations.remove(operation);
        for (OperationListener listener : operationListeners)
            listener.ended(operation, this);
    }

    public List<Operation> getRunningOperations() {
        return new ArrayList<>(runningOperations);
    }

    public boolean isRunning()
    {
        return runningOperations.size() > 0;
    }

    public static class OperationAlreadyRunning extends RuntimeException {
        Operation mOperation;

        public OperationAlreadyRunning(Operation operation) {
            mOperation = operation;
        }

        @Override
        public String getMessage() {
            return "Instance of operation \"" + mOperation.getClass().getSimpleName() + "\" is already running";
        }
    }

    public static class OperationNotRunningException extends RuntimeException {
        Operation mOperation;

        public OperationNotRunningException(Operation operation) {
            mOperation = operation;
        }

        @Override
        public String getMessage() {
            return "Instance of operation \"" + mOperation.getClass().getSimpleName() + "\" is not running, so it cannot be stopped";
        }
    }
}
