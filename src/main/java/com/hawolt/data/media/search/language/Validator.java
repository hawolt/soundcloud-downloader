package com.hawolt.data.media.search.language;

import java.util.HashSet;
import java.util.Set;

public abstract class Validator implements CharacterValidator {

    private final Set<Character.UnicodeBlock> set = new HashSet<>();

    public Validator() {
        for (Character.UnicodeBlock block : get()) {
            set.add(block);
        }
    }

    protected abstract Character.UnicodeBlock[] get();

    @Override
    public boolean matching(String input) {
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(character);
            if (!set.contains(block)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(String input) {
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(character);
            if (set.contains(block)) {
                return true;
            }
        }
        return false;
    }
}
