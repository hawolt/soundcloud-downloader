package com.hawolt.data.media.search.language;

import com.hawolt.data.media.search.language.impl.JapaneseValidator;
import com.hawolt.data.media.search.language.impl.RussianValidator;

public interface CharacterValidator {
    CharacterValidator JAPANESE = new JapaneseValidator();
    CharacterValidator RUSSIAN = new RussianValidator();

    boolean matching(String input);

    boolean contains(String input);
}
