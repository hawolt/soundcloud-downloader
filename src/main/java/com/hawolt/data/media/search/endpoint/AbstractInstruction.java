package com.hawolt.data.media.search.endpoint;

public abstract class AbstractInstruction implements Instruction {

    @Override
    public String manipulate(String base, String[] args) throws Exception {
        if (args.length < getArguments()) return "[BAD USAGE]";
        return modify(base, args);
    }

    protected abstract String modify(String base, String[] args) throws Exception;

    protected abstract int getArguments();
}
