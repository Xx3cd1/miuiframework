package android.database;

import android.annotation.UnsupportedAppUsage;

public abstract class AbstractWindowedCursor extends AbstractCursor {
    protected CursorWindow mWindow;

    public byte[] getBlob(int columnIndex) {
        checkPosition();
        return this.mWindow.getBlob(this.mPos, columnIndex);
    }

    public String getString(int columnIndex) {
        checkPosition();
        return this.mWindow.getString(this.mPos, columnIndex);
    }

    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
        checkPosition();
        this.mWindow.copyStringToBuffer(this.mPos, columnIndex, buffer);
    }

    public short getShort(int columnIndex) {
        checkPosition();
        return this.mWindow.getShort(this.mPos, columnIndex);
    }

    public int getInt(int columnIndex) {
        checkPosition();
        return this.mWindow.getInt(this.mPos, columnIndex);
    }

    public long getLong(int columnIndex) {
        checkPosition();
        return this.mWindow.getLong(this.mPos, columnIndex);
    }

    public float getFloat(int columnIndex) {
        checkPosition();
        return this.mWindow.getFloat(this.mPos, columnIndex);
    }

    public double getDouble(int columnIndex) {
        checkPosition();
        return this.mWindow.getDouble(this.mPos, columnIndex);
    }

    public boolean isNull(int columnIndex) {
        checkPosition();
        return this.mWindow.getType(this.mPos, columnIndex) == 0;
    }

    @Deprecated
    public boolean isBlob(int columnIndex) {
        return getType(columnIndex) == 4;
    }

    @Deprecated
    public boolean isString(int columnIndex) {
        return getType(columnIndex) == 3;
    }

    @Deprecated
    public boolean isLong(int columnIndex) {
        return getType(columnIndex) == 1;
    }

    @Deprecated
    public boolean isFloat(int columnIndex) {
        return getType(columnIndex) == 2;
    }

    public int getType(int columnIndex) {
        checkPosition();
        return this.mWindow.getType(this.mPos, columnIndex);
    }

    /* Access modifiers changed, original: protected */
    public void checkPosition() {
        super.checkPosition();
        if (this.mWindow == null) {
            throw new StaleDataException("Attempting to access a closed CursorWindow.Most probable cause: cursor is deactivated prior to calling this method.");
        }
    }

    public CursorWindow getWindow() {
        return this.mWindow;
    }

    public void setWindow(CursorWindow window) {
        if (window != this.mWindow) {
            closeWindow();
            this.mWindow = window;
        }
    }

    public boolean hasWindow() {
        return this.mWindow != null;
    }

    /* Access modifiers changed, original: protected */
    @UnsupportedAppUsage
    public void closeWindow() {
        CursorWindow cursorWindow = this.mWindow;
        if (cursorWindow != null) {
            cursorWindow.close();
            this.mWindow = null;
        }
    }

    /* Access modifiers changed, original: protected */
    @UnsupportedAppUsage
    public void clearOrCreateWindow(String name) {
        CursorWindow cursorWindow = this.mWindow;
        if (cursorWindow == null) {
            this.mWindow = new CursorWindow(name);
        } else {
            cursorWindow.clear();
        }
    }

    /* Access modifiers changed, original: protected */
    @UnsupportedAppUsage
    public void onDeactivateOrClose() {
        super.onDeactivateOrClose();
        closeWindow();
    }
}
