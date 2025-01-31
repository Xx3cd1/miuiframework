package android.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import com.android.internal.R;

@Deprecated
public class TabActivity extends ActivityGroup {
    private String mDefaultTab = null;
    private int mDefaultTabIndex = -1;
    private TabHost mTabHost;

    public void setDefaultTab(String tag) {
        this.mDefaultTab = tag;
        this.mDefaultTabIndex = -1;
    }

    public void setDefaultTab(int index) {
        this.mDefaultTab = null;
        this.mDefaultTabIndex = index;
    }

    /* Access modifiers changed, original: protected */
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        ensureTabHost();
        String cur = state.getString("currentTab");
        if (cur != null) {
            this.mTabHost.setCurrentTabByTag(cur);
        }
        if (this.mTabHost.getCurrentTab() < 0) {
            String str = this.mDefaultTab;
            if (str != null) {
                this.mTabHost.setCurrentTabByTag(str);
                return;
            }
            int i = this.mDefaultTabIndex;
            if (i >= 0) {
                this.mTabHost.setCurrentTab(i);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onPostCreate(Bundle icicle) {
        super.onPostCreate(icicle);
        ensureTabHost();
        if (this.mTabHost.getCurrentTab() == -1) {
            this.mTabHost.setCurrentTab(0);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String currentTabTag = this.mTabHost.getCurrentTabTag();
        if (currentTabTag != null) {
            outState.putString("currentTab", currentTabTag);
        }
    }

    public void onContentChanged() {
        super.onContentChanged();
        this.mTabHost = (TabHost) findViewById(16908306);
        TabHost tabHost = this.mTabHost;
        if (tabHost != null) {
            tabHost.setup(getLocalActivityManager());
            return;
        }
        throw new RuntimeException("Your content must have a TabHost whose id attribute is 'android.R.id.tabhost'");
    }

    private void ensureTabHost() {
        if (this.mTabHost == null) {
            setContentView((int) R.layout.tab_content);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onChildTitleChanged(Activity childActivity, CharSequence title) {
        if (getLocalActivityManager().getCurrentActivity() == childActivity) {
            View tabView = this.mTabHost.getCurrentTabView();
            if (tabView != null && (tabView instanceof TextView)) {
                ((TextView) tabView).setText(title);
            }
        }
    }

    public TabHost getTabHost() {
        ensureTabHost();
        return this.mTabHost;
    }

    public TabWidget getTabWidget() {
        return this.mTabHost.getTabWidget();
    }
}
