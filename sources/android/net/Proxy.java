package android.net;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.text.TextUtils;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Proxy {
    private static final Pattern EXCLLIST_PATTERN = Pattern.compile(EXCLLIST_REGEXP);
    private static final String EXCLLIST_REGEXP = "^$|^[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*(,[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*)*$";
    private static final String EXCL_REGEX = "[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*";
    @Deprecated
    public static final String EXTRA_PROXY_INFO = "android.intent.extra.PROXY_INFO";
    private static final Pattern HOSTNAME_PATTERN = Pattern.compile(HOSTNAME_REGEXP);
    private static final String HOSTNAME_REGEXP = "^$|^[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*$";
    private static final String NAME_IP_REGEX = "[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*";
    public static final String PROXY_CHANGE_ACTION = "android.intent.action.PROXY_CHANGE";
    public static final int PROXY_EXCLLIST_INVALID = 5;
    public static final int PROXY_HOSTNAME_EMPTY = 1;
    public static final int PROXY_HOSTNAME_INVALID = 2;
    public static final int PROXY_PORT_EMPTY = 3;
    public static final int PROXY_PORT_INVALID = 4;
    public static final int PROXY_VALID = 0;
    private static final String TAG = "Proxy";
    private static ConnectivityManager sConnectivityManager = null;
    private static final ProxySelector sDefaultProxySelector = ProxySelector.getDefault();

    @UnsupportedAppUsage
    public static final java.net.Proxy getProxy(Context ctx, String url) {
        String host = "";
        if (!(url == null || isLocalHost(host))) {
            List<java.net.Proxy> proxyList = ProxySelector.getDefault().select(URI.create(url));
            if (proxyList.size() > 0) {
                return (java.net.Proxy) proxyList.get(0);
            }
        }
        return java.net.Proxy.NO_PROXY;
    }

    @Deprecated
    public static final String getHost(Context ctx) {
        String str = null;
        java.net.Proxy proxy = getProxy(ctx, null);
        if (proxy == java.net.Proxy.NO_PROXY) {
            return null;
        }
        try {
            str = ((InetSocketAddress) proxy.address()).getHostName();
            return str;
        } catch (Exception e) {
            return str;
        }
    }

    @Deprecated
    public static final int getPort(Context ctx) {
        java.net.Proxy proxy = getProxy(ctx, null);
        if (proxy == java.net.Proxy.NO_PROXY) {
            return -1;
        }
        try {
            return ((InetSocketAddress) proxy.address()).getPort();
        } catch (Exception e) {
            return -1;
        }
    }

    @Deprecated
    public static final String getDefaultHost() {
        String host = System.getProperty("http.proxyHost");
        if (TextUtils.isEmpty(host)) {
            return null;
        }
        return host;
    }

    @Deprecated
    public static final int getDefaultPort() {
        if (getDefaultHost() == null) {
            return -1;
        }
        try {
            return Integer.parseInt(System.getProperty("http.proxyPort"));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static final boolean isLocalHost(String host) {
        if (host == null) {
            return false;
        }
        try {
            if (host.equalsIgnoreCase(ProxyInfo.LOCAL_HOST) || NetworkUtils.numericToInetAddress(host).isLoopbackAddress()) {
                return true;
            }
            return false;
        } catch (IllegalArgumentException e) {
        }
    }

    public static int validate(String hostname, String port, String exclList) {
        Matcher match = HOSTNAME_PATTERN.matcher(hostname);
        Matcher listMatch = EXCLLIST_PATTERN.matcher(exclList);
        if (!match.matches()) {
            return 2;
        }
        if (!listMatch.matches()) {
            return 5;
        }
        if (hostname.length() > 0 && port.length() == 0) {
            return 3;
        }
        if (port.length() > 0) {
            if (hostname.length() == 0) {
                return 1;
            }
            try {
                int portVal = Integer.parseInt(port);
                if (portVal <= 0 || portVal > 65535) {
                    return 4;
                }
            } catch (NumberFormatException e) {
                return 4;
            }
        }
        return 0;
    }

    @UnsupportedAppUsage
    public static final void setHttpProxySystemProperty(ProxyInfo p) {
        String host = null;
        String port = null;
        String exclList = null;
        Uri pacFileUrl = Uri.EMPTY;
        if (p != null) {
            host = p.getHost();
            port = Integer.toString(p.getPort());
            exclList = p.getExclusionListAsString();
            pacFileUrl = p.getPacFileUrl();
        }
        setHttpProxySystemProperty(host, port, exclList, pacFileUrl);
    }

    public static final void setHttpProxySystemProperty(String host, String port, String exclList, Uri pacFileUrl) {
        if (exclList != null) {
            exclList = exclList.replace(",", "|");
        }
        String str = "https.proxyHost";
        String str2 = "http.proxyHost";
        if (host != null) {
            System.setProperty(str2, host);
            System.setProperty(str, host);
        } else {
            System.clearProperty(str2);
            System.clearProperty(str);
        }
        str = "https.proxyPort";
        str2 = "http.proxyPort";
        if (port != null) {
            System.setProperty(str2, port);
            System.setProperty(str, port);
        } else {
            System.clearProperty(str2);
            System.clearProperty(str);
        }
        str = "https.nonProxyHosts";
        str2 = "http.nonProxyHosts";
        if (exclList != null) {
            System.setProperty(str2, exclList);
            System.setProperty(str, exclList);
        } else {
            System.clearProperty(str2);
            System.clearProperty(str);
        }
        if (Uri.EMPTY.equals(pacFileUrl)) {
            ProxySelector.setDefault(sDefaultProxySelector);
        } else {
            ProxySelector.setDefault(new PacProxySelector());
        }
    }
}
