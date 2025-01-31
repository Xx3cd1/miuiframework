package com.miui.whetstone.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class WhetstoneProviderContract {
    public static final String AUTHORITY = "com.miui.whetstone";
    public static final Uri CONTENT_URI = Uri.parse("content://com.miui.whetstone");

    public static final class ServiceManager implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.miui.whetstone/servicemanager");
        public static final String IS_ACTIVE = "is_active";
        public static final String IS_PERSIST = "is_persist";
        public static final String SERVICE_NAME = "service_name";

        private ServiceManager() {
        }
    }

    private WhetstoneProviderContract() {
    }
}
