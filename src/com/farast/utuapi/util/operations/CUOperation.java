package com.farast.utuapi.util.operations;

import com.farast.utuapi.data.common.UtuType;
import com.farast.utuapi.data.interfaces.Updatable;

/**
 * Created by cendr_000 on 18.08.2016.
 */
public class CUOperation implements ItemRelatedOperation {

    private Updatable item;
    private ItemOperationType operationType;
    private UtuType utuType;

    public CUOperation(UtuType utuType, Updatable item) {
        this.utuType = utuType;
        this.item = item;
        if (item == null)
            operationType = ItemOperationType.CREATE;
        else
            operationType = ItemOperationType.UPDATE;
    }

    @Override
    public UtuType getItemType() {
        return utuType;
    }

    @Override
    public String getItemName() {
        return item.getOnelineRepresentation();
    }

    @Override
    public ItemOperationType getOperationType() {
        return operationType;
    }
}
