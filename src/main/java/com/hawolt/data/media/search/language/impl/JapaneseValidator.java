package com.hawolt.data.media.search.language.impl;

import com.hawolt.data.media.search.language.Validator;

public class JapaneseValidator extends Validator {
    @Override
    protected Character.UnicodeBlock[] get() {
        return new Character.UnicodeBlock[]{
                Character.UnicodeBlock.KATAKANA,
                Character.UnicodeBlock.HIRAGANA,
                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS,
                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
        };
    }
}
