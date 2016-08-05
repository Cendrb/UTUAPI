package com.farast.utuapi.util.operations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cendr_000 on 28.07.2016.
 */
public class OperationManager {
    private Operation currentOperation = null;

    private List<OperationListener> operationListeners = new ArrayList<>();

    public void addOperationListener(OperationListener listener) {
        operationListeners.add(listener);
    }

    public void clearOperationListeners() {
        operationListeners.clear();
    }

    public void setOperationListener(OperationListener listener)
    {
        operationListeners.clear();
        operationListeners.add(listener);
    }

    public void startOperation(Operation operation) {
        if(currentOperation != null)
            throw new InvalidOperationException(InvalidOperationException.OperationType.start);
        currentOperation = operation;
        for (OperationListener listener : operationListeners)
            listener.started(currentOperation);
    }

    public void endOperation() {
        if(currentOperation == null)
            throw new InvalidOperationException(InvalidOperationException.OperationType.end);
        for (OperationListener listener : operationListeners)
            listener.ended(currentOperation);
        currentOperation = null;
    }

    public Operation getCurrentOperation() {
        return currentOperation;
    }

    public static class InvalidOperationException extends RuntimeException
    {
        private OperationType type;

        private enum OperationType { start, end }

        private InvalidOperationException(OperationType type)
        {
            this.type = type;
        }

        @Override
        public String getMessage() {
            if(type == OperationType.start)
                return "New operation cannot be started before the old one ends";
            else
                return "No operation to end";
        }
    }
}
