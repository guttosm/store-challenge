package com.jumbo.store.domain.util;

import java.util.Locale;

public interface SourceMessage {

    String getMessage(String key);

    String getMessage(String key, Object... objects);

    String getMessage(String key, Locale locale);
}
