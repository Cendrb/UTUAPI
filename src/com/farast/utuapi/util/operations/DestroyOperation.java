package com.farast.utuapi.util.operations;

import com.farast.utuapi.data.Updatable;

/**
 * Created by cendr_000 on 18.08.2016.
 */
public class DestroyOperation implements Operation {

    Updatable mItem;

    public DestroyOperation(Updatable item) {
        mItem = item;
    }

    @Override
    public String getName() {
        return "removing " + mItem.getTitle();
    }
}
