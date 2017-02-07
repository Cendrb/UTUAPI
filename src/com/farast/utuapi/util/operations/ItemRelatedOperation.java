package com.farast.utuapi.util.operations;

import com.farast.utuapi.data.common.UtuType;

/**
 * Created by cendr on 07/02/2017.
 */
public interface ItemRelatedOperation extends Operation {
    UtuType getItemType();

    String getItemName();

    ItemOperationType getOperationType();
}
