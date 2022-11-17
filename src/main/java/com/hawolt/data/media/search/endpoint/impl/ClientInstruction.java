package com.hawolt.data.media.search.endpoint.impl;

import com.hawolt.data.VirtualClient;
import com.hawolt.data.media.search.endpoint.AbstractInstruction;

public class ClientInstruction extends AbstractInstruction {

    @Override
    public String modify(String base, String[] args) throws Exception {
        return VirtualClient.getID();
    }

    @Override
    protected int getArguments() {
        return 0;
    }

    @Override
    public String getName() {
        return "client";
    }
}
