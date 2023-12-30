package com.hawolt.data.media.search.language.impl;

import com.hawolt.data.media.search.language.Validator;

public class RussianValidator extends Validator {
    @Override
    protected Character.UnicodeBlock[] get() {
        return new Character.UnicodeBlock[]{
                Character.UnicodeBlock.CYRILLIC
        };
    }
}
