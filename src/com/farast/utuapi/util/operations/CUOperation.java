package com.farast.utuapi.util.operations;

import com.farast.utuapi.data.OnelineRepresentable;
import com.farast.utuapi.data.Updatable;

/**
 * Created by cendr_000 on 18.08.2016.
 */
public class CUOperation implements Operation {

    Updatable item;
    String itemName;

    public CUOperation(String itemName, Updatable item)
    {
        this.itemName = itemName;
        this.item = item;
    }

    @Override
    public String getName() {
        StringBuilder builder = new StringBuilder();
        if(item == null)
            builder.append("creating");
        else
            builder.append("updating");
        builder.append(" ");
        builder.append(itemName);
        if(item != null)
            builder.append(" (").append(item.getOnelineRepresentation()).append(")");
        return builder.toString();
    }
}
