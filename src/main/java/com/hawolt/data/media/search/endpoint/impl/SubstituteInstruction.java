package com.hawolt.data.media.search.endpoint.impl;

import com.hawolt.data.media.search.endpoint.AbstractInstruction;

public class SubstituteInstruction extends AbstractInstruction {

    @Override
    public String modify(String base, String[] args) throws Exception {
        return args[1];
    }

    @Override
    protected int getArguments() {
        return 1;
    }

    @Override
    public String getName() {
        return "substitute";
    }
}
