package android.text.util;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.util.EventLog;
import android.util.Log;
import android.util.Patterns;
import android.webkit.WebView;
import android.widget.TextView;
import com.android.i18n.phonenumbers.PhoneNumberMatch;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.PhoneNumberUtil.Leniency;
import com.miui.internal.variable.api.v29.Android_Text_Util_Linkify.Extension;
import com.miui.internal.variable.api.v29.Android_Text_Util_Linkify.Interface;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libcore.util.EmptyArray;

public class Linkify {
    public static final int ALL = 15;
    private static final Function<String, URLSpan> DEFAULT_SPAN_FACTORY = -$$Lambda$Linkify$7J_-cMhIF2bcttjkxA2jDFP8sKw.INSTANCE;
    public static final int EMAIL_ADDRESSES = 2;
    private static final String LOG_TAG = "Linkify";
    @Deprecated
    public static final int MAP_ADDRESSES = 8;
    public static final int PHONE_NUMBERS = 4;
    private static final int PHONE_NUMBER_MINIMUM_DIGITS = 5;
    public static final int WEB_URLS = 1;
    public static final MatchFilter sPhoneNumberMatchFilter = new MatchFilter() {
        public final boolean acceptMatch(CharSequence s, int start, int end) {
            int digitCount = 0;
            for (int i = start; i < end; i++) {
                if (Character.isDigit(s.charAt(i))) {
                    digitCount++;
                    if (digitCount >= 5) {
                        return true;
                    }
                }
            }
            return false;
        }
    };
    public static final TransformFilter sPhoneNumberTransformFilter = new TransformFilter() {
        public final String transformUrl(Matcher match, String url) {
            return Patterns.digitsAndPlusOnly(match);
        }
    };
    public static final MatchFilter sUrlMatchFilter = new MatchFilter() {
        public final boolean acceptMatch(CharSequence s, int start, int end) {
            if (start != 0 && s.charAt(start - 1) == '@') {
                return false;
            }
            return true;
        }
    };

    public interface MatchFilter {
        boolean acceptMatch(CharSequence charSequence, int i, int i2);
    }

    public interface TransformFilter {
        String transformUrl(Matcher matcher, String str);
    }

    private static class Impl implements Interface {
        private Impl() {
        }

        /* synthetic */ Impl(AnonymousClass1 x0) {
            this();
        }

        public void gatherLinks(Linkify linkify, ArrayList arrayList, Spannable spannable, Pattern pattern, String[] strings, MatchFilter matchFilter, TransformFilter transformFilter) {
            Linkify.originalGatherLinks(arrayList, spannable, pattern, strings, matchFilter, transformFilter);
        }

        public boolean addLinks(Linkify linkify, Spannable spannable, int i) {
            return Linkify.originalAddLinks(spannable, i);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface LinkifyMask {
    }

    static {
        Extension.get().bindOriginal(new Impl());
    }

    public static final boolean addLinks(Spannable text, int mask) {
        if (Extension.get().getExtension() != null) {
            return ((Interface) Extension.get().getExtension().asInterface()).addLinks(null, text, mask);
        }
        return originalAddLinks(text, mask);
    }

    static final boolean originalAddLinks(Spannable text, int mask) {
        return addLinks(text, mask, null, null);
    }

    public static final boolean addLinks(Spannable text, int mask, Function<String, URLSpan> urlSpanFactory) {
        return addLinks(text, mask, null, urlSpanFactory);
    }

    private static boolean addLinks(Spannable text, int mask, Context context, Function<String, URLSpan> urlSpanFactory) {
        if (text != null && containsUnsupportedCharacters(text.toString())) {
            EventLog.writeEvent(1397638484, "116321860", Integer.valueOf(-1), "");
            return false;
        } else if (mask == 0) {
            return false;
        } else {
            URLSpan[] old = (URLSpan[]) text.getSpans(0, text.length(), URLSpan.class);
            for (int i = old.length - 1; i >= 0; i--) {
                text.removeSpan(old[i]);
            }
            ArrayList<LinkSpec> links = new ArrayList();
            if ((mask & 1) != 0) {
                gatherLinks(links, text, Patterns.AUTOLINK_WEB_URL, new String[]{"http://", "https://", "rtsp://"}, sUrlMatchFilter, null);
            }
            if ((mask & 2) != 0) {
                ArrayList<LinkSpec> arrayList = links;
                Spannable spannable = text;
                gatherLinks(arrayList, spannable, Patterns.AUTOLINK_EMAIL_ADDRESS, new String[]{"mailto:"}, null, null);
            }
            if ((mask & 4) != 0) {
                gatherTelLinks(links, text, context);
            }
            if ((mask & 8) != 0) {
                gatherMapLinks(links, text);
            }
            pruneOverlaps(links);
            if (links.size() == 0) {
                return false;
            }
            Iterator it = links.iterator();
            while (it.hasNext()) {
                LinkSpec link = (LinkSpec) it.next();
                applyLink(link.url, link.start, link.end, text, urlSpanFactory);
            }
            return true;
        }
    }

    public static boolean containsUnsupportedCharacters(String text) {
        boolean contains = text.contains("‬");
        String str = LOG_TAG;
        if (contains) {
            Log.e(str, "Unsupported character for applying links: u202C");
            return true;
        } else if (text.contains("‭")) {
            Log.e(str, "Unsupported character for applying links: u202D");
            return true;
        } else if (!text.contains("‮")) {
            return false;
        } else {
            Log.e(str, "Unsupported character for applying links: u202E");
            return true;
        }
    }

    public static final boolean addLinks(TextView text, int mask) {
        if (mask == 0) {
            return false;
        }
        Context context = text.getContext();
        CharSequence t = text.getText();
        if (!(t instanceof Spannable)) {
            CharSequence s = SpannableString.valueOf(t);
            if (!addLinks(s, mask, context, null)) {
                return false;
            }
            addLinkMovementMethod(text);
            text.setText(s);
            return true;
        } else if (!addLinks((Spannable) t, mask, context, null)) {
            return false;
        } else {
            addLinkMovementMethod(text);
            return true;
        }
    }

    private static final void addLinkMovementMethod(TextView t) {
        MovementMethod m = t.getMovementMethod();
        if ((m == null || !(m instanceof LinkMovementMethod)) && t.getLinksClickable()) {
            t.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static final void addLinks(TextView text, Pattern pattern, String scheme) {
        addLinks(text, pattern, scheme, null, null, null);
    }

    public static final void addLinks(TextView text, Pattern pattern, String scheme, MatchFilter matchFilter, TransformFilter transformFilter) {
        addLinks(text, pattern, scheme, null, matchFilter, transformFilter);
    }

    public static final void addLinks(TextView text, Pattern pattern, String defaultScheme, String[] schemes, MatchFilter matchFilter, TransformFilter transformFilter) {
        CharSequence spannable = SpannableString.valueOf(text.getText());
        if (addLinks((Spannable) spannable, pattern, defaultScheme, schemes, matchFilter, transformFilter)) {
            text.setText(spannable);
            addLinkMovementMethod(text);
        }
    }

    public static final boolean addLinks(Spannable text, Pattern pattern, String scheme) {
        return addLinks(text, pattern, scheme, null, null, null);
    }

    public static final boolean addLinks(Spannable spannable, Pattern pattern, String scheme, MatchFilter matchFilter, TransformFilter transformFilter) {
        return addLinks(spannable, pattern, scheme, null, matchFilter, transformFilter);
    }

    public static final boolean addLinks(Spannable spannable, Pattern pattern, String defaultScheme, String[] schemes, MatchFilter matchFilter, TransformFilter transformFilter) {
        return addLinks(spannable, pattern, defaultScheme, schemes, matchFilter, transformFilter, null);
    }

    public static final boolean addLinks(Spannable spannable, Pattern pattern, String defaultScheme, String[] schemes, MatchFilter matchFilter, TransformFilter transformFilter, Function<String, URLSpan> urlSpanFactory) {
        String str = "";
        if (spannable == null || !containsUnsupportedCharacters(spannable.toString())) {
            if (defaultScheme == null) {
                defaultScheme = "";
            }
            if (schemes == null || schemes.length < 1) {
                schemes = EmptyArray.STRING;
            }
            String[] schemesCopy = new String[(schemes.length + 1)];
            schemesCopy[0] = defaultScheme.toLowerCase(Locale.ROOT);
            for (int index = 0; index < schemes.length; index++) {
                String scheme = schemes[index];
                schemesCopy[index + 1] = scheme == null ? str : scheme.toLowerCase(Locale.ROOT);
            }
            boolean hasMatches = false;
            Matcher m = pattern.matcher(spannable);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                boolean allowed = true;
                if (matchFilter != null) {
                    allowed = matchFilter.acceptMatch(spannable, start, end);
                }
                if (allowed) {
                    applyLink(makeUrl(m.group(0), schemesCopy, m, transformFilter), start, end, spannable, urlSpanFactory);
                    hasMatches = true;
                }
            }
            return hasMatches;
        }
        EventLog.writeEvent(1397638484, "116321860", Integer.valueOf(-1), str);
        return false;
    }

    private static void applyLink(String url, int start, int end, Spannable text, Function<String, URLSpan> urlSpanFactory) {
        if (urlSpanFactory == null) {
            urlSpanFactory = DEFAULT_SPAN_FACTORY;
        }
        text.setSpan((URLSpan) urlSpanFactory.apply(url), start, end, 33);
    }

    private static final String makeUrl(String url, String[] prefixes, Matcher matcher, TransformFilter filter) {
        if (filter != null) {
            url = filter.transformUrl(matcher, url);
        }
        boolean hasPrefix = false;
        for (int i = 0; i < prefixes.length; i++) {
            if (url.regionMatches(true, 0, prefixes[i], 0, prefixes[i].length())) {
                StringBuilder stringBuilder;
                hasPrefix = true;
                if (!url.regionMatches(false, 0, prefixes[i], 0, prefixes[i].length())) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(prefixes[i]);
                    stringBuilder.append(url.substring(prefixes[i].length()));
                    url = stringBuilder.toString();
                }
                if (hasPrefix && prefixes.length > 0) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(prefixes[0]);
                    stringBuilder.append(url);
                    return stringBuilder.toString();
                }
            }
        }
        return hasPrefix ? url : url;
    }

    private static final void gatherLinks(ArrayList<LinkSpec> links, Spannable s, Pattern pattern, String[] schemes, MatchFilter matchFilter, TransformFilter transformFilter) {
        if (Extension.get().getExtension() != null) {
            ((Interface) Extension.get().getExtension().asInterface()).gatherLinks(null, links, s, pattern, schemes, matchFilter, transformFilter);
        } else {
            originalGatherLinks(links, s, pattern, schemes, matchFilter, transformFilter);
        }
    }

    static void originalGatherLinks(ArrayList<LinkSpec> links, Spannable s, Pattern pattern, String[] schemes, MatchFilter matchFilter, TransformFilter transformFilter) {
        Matcher m = pattern.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            if (matchFilter == null || matchFilter.acceptMatch(s, start, end)) {
                LinkSpec spec = new LinkSpec();
                spec.url = makeUrl(m.group(0), schemes, m, transformFilter);
                spec.start = start;
                spec.end = end;
                links.add(spec);
            }
        }
    }

    @UnsupportedAppUsage
    private static void gatherTelLinks(ArrayList<LinkSpec> links, Spannable s, Context context) {
        TelephonyManager telephonyManager;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        if (context == null) {
            telephonyManager = TelephonyManager.getDefault();
        } else {
            telephonyManager = TelephonyManager.from(context);
        }
        TelephonyManager tm = telephonyManager;
        for (PhoneNumberMatch match : phoneUtil.findNumbers(s.toString(), tm.getSimCountryIso().toUpperCase(Locale.US), Leniency.POSSIBLE, Long.MAX_VALUE)) {
            LinkSpec spec = new LinkSpec();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(WebView.SCHEME_TEL);
            stringBuilder.append(PhoneNumberUtils.normalizeNumber(match.rawString()));
            spec.url = stringBuilder.toString();
            spec.start = match.start();
            spec.end = match.end();
            links.add(spec);
        }
    }

    private static final void gatherMapLinks(ArrayList<LinkSpec> links, Spannable s) {
        String string = s.toString();
        int base = 0;
        while (true) {
            try {
                String findAddress = WebView.findAddress(string);
                String address = findAddress;
                if (findAddress == null) {
                    break;
                }
                int start = string.indexOf(address);
                if (start < 0) {
                    break;
                }
                LinkSpec spec = new LinkSpec();
                int end = start + address.length();
                spec.start = base + start;
                spec.end = base + end;
                string = string.substring(end);
                base += end;
                try {
                    String encodedAddress = URLEncoder.encode(address, "UTF-8");
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(WebView.SCHEME_GEO);
                    stringBuilder.append(encodedAddress);
                    spec.url = stringBuilder.toString();
                    links.add(spec);
                } catch (UnsupportedEncodingException e) {
                }
            } catch (UnsupportedOperationException e2) {
                return;
            }
        }
    }

    private static final void pruneOverlaps(ArrayList<LinkSpec> links) {
        Collections.sort(links, new Comparator<LinkSpec>() {
            public final int compare(LinkSpec a, LinkSpec b) {
                if (a.start < b.start) {
                    return -1;
                }
                if (a.start > b.start || a.end < b.end) {
                    return 1;
                }
                if (a.end > b.end) {
                    return -1;
                }
                return 0;
            }
        });
        int len = links.size();
        int i = 0;
        while (i < len - 1) {
            LinkSpec a = (LinkSpec) links.get(i);
            LinkSpec b = (LinkSpec) links.get(i + 1);
            int remove = -1;
            if (a.start <= b.start && a.end > b.start) {
                if (b.end <= a.end) {
                    remove = i + 1;
                } else if (a.end - a.start > b.end - b.start) {
                    remove = i + 1;
                } else if (a.end - a.start < b.end - b.start) {
                    remove = i;
                }
                if (remove != -1) {
                    links.remove(remove);
                    len--;
                }
            }
            i++;
        }
    }
}
