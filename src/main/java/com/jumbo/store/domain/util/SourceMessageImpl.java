package com.jumbo.store.domain.util;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SourceMessageImpl implements SourceMessage {

    private final MessageSource messageSource;

    @Override
    public String getMessage(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, null, locale);
    }

    @Override
    public String getMessage(String key, Object... objects) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, objects, locale);
    }

    @Override
    public String getMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
}
