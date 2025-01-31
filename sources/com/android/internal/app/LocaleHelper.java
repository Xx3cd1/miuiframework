package com.android.internal.app;

import android.annotation.UnsupportedAppUsage;
import android.icu.text.ListFormatter;
import android.icu.util.ULocale;
import android.os.LocaleList;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import com.android.internal.app.LocaleStore.LocaleInfo;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import libcore.icu.ICU;

public class LocaleHelper {

    public static final class LocaleInfoComparator implements Comparator<LocaleInfo> {
        private static final String PREFIX_ARABIC = "ال";
        private final Collator mCollator;
        private final boolean mCountryMode;

        @UnsupportedAppUsage
        public LocaleInfoComparator(Locale sortLocale, boolean countryMode) {
            this.mCollator = Collator.getInstance(sortLocale);
            this.mCountryMode = countryMode;
        }

        private String removePrefixForCompare(Locale locale, String str) {
            if ("ar".equals(locale.getLanguage())) {
                String str2 = PREFIX_ARABIC;
                if (str.startsWith(str2)) {
                    return str.substring(str2.length());
                }
            }
            return str;
        }

        @UnsupportedAppUsage
        public int compare(LocaleInfo lhs, LocaleInfo rhs) {
            if (lhs.isSuggested() == rhs.isSuggested()) {
                return this.mCollator.compare(removePrefixForCompare(lhs.getLocale(), lhs.getLabel(this.mCountryMode)), removePrefixForCompare(rhs.getLocale(), rhs.getLabel(this.mCountryMode)));
            }
            return lhs.isSuggested() ? -1 : 1;
        }
    }

    public static String toSentenceCase(String str, Locale locale) {
        if (str.isEmpty()) {
            return str;
        }
        int firstCodePointLen = str.offsetByCodePoints(0, 1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str.substring(0, firstCodePointLen).toUpperCase(locale));
        stringBuilder.append(str.substring(firstCodePointLen));
        return stringBuilder.toString();
    }

    @UnsupportedAppUsage
    public static String normalizeForSearch(String str, Locale locale) {
        return str.toUpperCase();
    }

    private static boolean shouldUseDialectName(Locale locale) {
        String lang = locale.getLanguage();
        return "fa".equals(lang) || "ro".equals(lang) || "zh".equals(lang);
    }

    @UnsupportedAppUsage
    public static String getDisplayName(Locale locale, Locale displayLocale, boolean sentenceCase) {
        String result;
        ULocale displayULocale = ULocale.forLocale(displayLocale);
        if (shouldUseDialectName(locale)) {
            result = ULocale.getDisplayNameWithDialect(locale.toLanguageTag(), displayULocale);
        } else {
            result = ULocale.getDisplayName(locale.toLanguageTag(), displayULocale);
        }
        return sentenceCase ? toSentenceCase(result, displayLocale) : result;
    }

    public static String getDisplayName(Locale locale, boolean sentenceCase) {
        return getDisplayName(locale, Locale.getDefault(), sentenceCase);
    }

    @UnsupportedAppUsage
    public static String getDisplayCountry(Locale locale, Locale displayLocale) {
        String country = ULocale.getDisplayCountry(locale.toLanguageTag(), ULocale.forLocale(displayLocale));
        if (locale.getUnicodeLocaleType("nu") == null) {
            return country;
        }
        return String.format("%s (%s)", new Object[]{country, ULocale.getDisplayKeywordValue(languageTag, "numbers", uDisplayLocale)});
    }

    public static String getDisplayCountry(Locale locale) {
        return ULocale.getDisplayCountry(locale.toLanguageTag(), ULocale.getDefault());
    }

    public static String getDisplayLocaleList(LocaleList locales, Locale displayLocale, int maxLocales) {
        int localeCount;
        int listCount;
        Locale dispLocale = displayLocale == null ? Locale.getDefault() : displayLocale;
        boolean ellipsisNeeded = locales.size() > maxLocales;
        if (ellipsisNeeded) {
            localeCount = maxLocales;
            listCount = maxLocales + 1;
        } else {
            listCount = locales.size();
            localeCount = listCount;
        }
        String[] localeNames = new String[listCount];
        for (int i = 0; i < localeCount; i++) {
            localeNames[i] = getDisplayName(locales.get(i), dispLocale, false);
        }
        if (ellipsisNeeded) {
            localeNames[maxLocales] = TextUtils.getEllipsisString(TruncateAt.END);
        }
        return ListFormatter.getInstance(dispLocale).format((Object[]) localeNames);
    }

    public static Locale addLikelySubtags(Locale locale) {
        return ICU.addLikelySubtags(locale);
    }
}
