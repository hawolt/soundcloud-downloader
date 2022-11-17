package com.hawolt.data.media.search.endpoint.impl;

import com.hawolt.data.media.search.endpoint.AbstractInstruction;

import java.util.Random;

public class TimestampInstruction extends AbstractInstruction {

    @Override
    public String modify(String base, String[] args) {
        return String.valueOf(System.currentTimeMillis());
    }

    @Override
    protected int getArguments() {
        return 0;
    }

    @Override
    public String getName() {
        return "timestamp";
    }
}
