package com.farast.utuapi.util;

/**
 * Created by cendr_000 on 27.07.2016.
 */
public class SclassDoesNotExistException extends Exception {

    private CollectionUtil.RecordNotFoundException recordNotFoundException;
    public SclassDoesNotExistException(CollectionUtil.RecordNotFoundException e)
    {
        super(e);
        recordNotFoundException = e;
    }

    @Override
    public String getMessage() {
        return String.format("Supplied sclass id(%s) doesn't correspond to any sclass object in database", recordNotFoundException.getRecordId());
    }
}
