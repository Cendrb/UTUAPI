package com.farast.utuapi.util.operations;

import com.farast.utuapi.data.common.UtuType;
import com.farast.utuapi.data.interfaces.Updatable;

/**
 * Created by cendr_000 on 18.08.2016.
 */
public class DestroyOperation implements ItemRelatedOperation {

    private Updatable item;

    public DestroyOperation(Updatable item) {
        this.item = item;
    }

    @Override
    public UtuType getItemType() {
        return item.getUtuType();
    }

    @Override
    public String getItemName() {
        return item.getOnelineRepresentation();
    }

    @Override
    public ItemOperationType getOperationType() {
        return ItemOperationType.DELETE;
    }
}
