package android.database;

import android.annotation.UnsupportedAppUsage;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteProgram;
import android.database.sqlite.SQLiteStatement;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.OperationCanceledException;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.Collator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

public class DatabaseUtils {
    private static final boolean DEBUG = false;
    private static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.AM_PM, 'b', 'c', DateFormat.DATE, 'e', 'f'};
    public static final int STATEMENT_ABORT = 6;
    public static final int STATEMENT_ATTACH = 3;
    public static final int STATEMENT_BEGIN = 4;
    public static final int STATEMENT_COMMIT = 5;
    public static final int STATEMENT_DDL = 8;
    public static final int STATEMENT_OTHER = 99;
    public static final int STATEMENT_PRAGMA = 7;
    public static final int STATEMENT_SELECT = 1;
    public static final int STATEMENT_UNPREPARED = 9;
    public static final int STATEMENT_UPDATE = 2;
    private static final String TAG = "DatabaseUtils";
    private static Collator mColl = null;

    @Deprecated
    public static class InsertHelper {
        public static final int TABLE_INFO_PRAGMA_COLUMNNAME_INDEX = 1;
        public static final int TABLE_INFO_PRAGMA_DEFAULT_INDEX = 4;
        private HashMap<String, Integer> mColumns;
        private final SQLiteDatabase mDb;
        private String mInsertSQL = null;
        private SQLiteStatement mInsertStatement = null;
        private SQLiteStatement mPreparedStatement = null;
        private SQLiteStatement mReplaceStatement = null;
        private final String mTableName;

        public InsertHelper(SQLiteDatabase db, String tableName) {
            this.mDb = db;
            this.mTableName = tableName;
        }

        private void buildSQL() throws SQLException {
            String str = "'";
            String str2 = ")";
            StringBuilder sb = new StringBuilder(128);
            sb.append("INSERT INTO ");
            sb.append(this.mTableName);
            sb.append(" (");
            StringBuilder sbv = new StringBuilder(128);
            sbv.append("VALUES (");
            int i = 1;
            Cursor cur = null;
            try {
                SQLiteDatabase sQLiteDatabase = this.mDb;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("PRAGMA table_info(");
                stringBuilder.append(this.mTableName);
                stringBuilder.append(str2);
                cur = sQLiteDatabase.rawQuery(stringBuilder.toString(), null);
                this.mColumns = new HashMap(cur.getCount());
                while (cur.moveToNext()) {
                    String columnName = cur.getString(1);
                    String defaultValue = cur.getString(4);
                    this.mColumns.put(columnName, Integer.valueOf(i));
                    sb.append(str);
                    sb.append(columnName);
                    sb.append(str);
                    if (defaultValue == null) {
                        sbv.append("?");
                    } else {
                        sbv.append("COALESCE(?, ");
                        sbv.append(defaultValue);
                        sbv.append(str2);
                    }
                    String str3 = ", ";
                    sb.append(i == cur.getCount() ? ") " : str3);
                    if (i == cur.getCount()) {
                        str3 = ");";
                    }
                    sbv.append(str3);
                    i++;
                }
                cur.close();
                sb.append(sbv);
                this.mInsertSQL = sb.toString();
            } catch (Throwable th) {
                if (cur != null) {
                    cur.close();
                }
            }
        }

        private SQLiteStatement getStatement(boolean allowReplace) throws SQLException {
            if (allowReplace) {
                if (this.mReplaceStatement == null) {
                    if (this.mInsertSQL == null) {
                        buildSQL();
                    }
                    String replaceSQL = new StringBuilder();
                    replaceSQL.append("INSERT OR REPLACE");
                    replaceSQL.append(this.mInsertSQL.substring(6));
                    this.mReplaceStatement = this.mDb.compileStatement(replaceSQL.toString());
                }
                return this.mReplaceStatement;
            }
            if (this.mInsertStatement == null) {
                if (this.mInsertSQL == null) {
                    buildSQL();
                }
                this.mInsertStatement = this.mDb.compileStatement(this.mInsertSQL);
            }
            return this.mInsertStatement;
        }

        private long insertInternal(ContentValues values, boolean allowReplace) {
            this.mDb.beginTransactionNonExclusive();
            try {
                SQLiteStatement stmt = getStatement(allowReplace);
                stmt.clearBindings();
                for (Entry<String, Object> e : values.valueSet()) {
                    DatabaseUtils.bindObjectToProgram(stmt, getColumnIndex((String) e.getKey()), e.getValue());
                }
                long result = stmt.executeInsert();
                this.mDb.setTransactionSuccessful();
                this.mDb.endTransaction();
                return result;
            } catch (SQLException e2) {
                String str = DatabaseUtils.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Error inserting ");
                stringBuilder.append(values);
                stringBuilder.append(" into table  ");
                stringBuilder.append(this.mTableName);
                Log.e(str, stringBuilder.toString(), e2);
                this.mDb.endTransaction();
                return -1;
            } catch (Throwable th) {
                this.mDb.endTransaction();
                throw th;
            }
        }

        public int getColumnIndex(String key) {
            getStatement(false);
            Integer index = (Integer) this.mColumns.get(key);
            if (index != null) {
                return index.intValue();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("column '");
            stringBuilder.append(key);
            stringBuilder.append("' is invalid");
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        public void bind(int index, double value) {
            this.mPreparedStatement.bindDouble(index, value);
        }

        public void bind(int index, float value) {
            this.mPreparedStatement.bindDouble(index, (double) value);
        }

        public void bind(int index, long value) {
            this.mPreparedStatement.bindLong(index, value);
        }

        public void bind(int index, int value) {
            this.mPreparedStatement.bindLong(index, (long) value);
        }

        public void bind(int index, boolean value) {
            this.mPreparedStatement.bindLong(index, value ? 1 : 0);
        }

        public void bindNull(int index) {
            this.mPreparedStatement.bindNull(index);
        }

        public void bind(int index, byte[] value) {
            if (value == null) {
                this.mPreparedStatement.bindNull(index);
            } else {
                this.mPreparedStatement.bindBlob(index, value);
            }
        }

        public void bind(int index, String value) {
            if (value == null) {
                this.mPreparedStatement.bindNull(index);
            } else {
                this.mPreparedStatement.bindString(index, value);
            }
        }

        public long insert(ContentValues values) {
            return insertInternal(values, false);
        }

        public long execute() {
            SQLiteStatement sQLiteStatement = this.mPreparedStatement;
            if (sQLiteStatement != null) {
                try {
                    long executeInsert = sQLiteStatement.executeInsert();
                    this.mPreparedStatement = null;
                    return executeInsert;
                } catch (SQLException e) {
                    String str = DatabaseUtils.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Error executing InsertHelper with table ");
                    stringBuilder.append(this.mTableName);
                    Log.e(str, stringBuilder.toString(), e);
                    this.mPreparedStatement = null;
                    return -1;
                } catch (Throwable th) {
                    this.mPreparedStatement = null;
                    throw th;
                }
            }
            throw new IllegalStateException("you must prepare this inserter before calling execute");
        }

        public void prepareForInsert() {
            this.mPreparedStatement = getStatement(false);
            this.mPreparedStatement.clearBindings();
        }

        public void prepareForReplace() {
            this.mPreparedStatement = getStatement(true);
            this.mPreparedStatement.clearBindings();
        }

        public long replace(ContentValues values) {
            return insertInternal(values, true);
        }

        public void close() {
            SQLiteStatement sQLiteStatement = this.mInsertStatement;
            if (sQLiteStatement != null) {
                sQLiteStatement.close();
                this.mInsertStatement = null;
            }
            sQLiteStatement = this.mReplaceStatement;
            if (sQLiteStatement != null) {
                sQLiteStatement.close();
                this.mReplaceStatement = null;
            }
            this.mInsertSQL = null;
            this.mColumns = null;
        }
    }

    public static final void writeExceptionToParcel(Parcel reply, Exception e) {
        int code;
        boolean logException = true;
        boolean z = e instanceof FileNotFoundException;
        String str = "Writing exception to parcel";
        String str2 = TAG;
        if (z) {
            code = 1;
            logException = false;
        } else if (e instanceof IllegalArgumentException) {
            code = 2;
        } else if (e instanceof UnsupportedOperationException) {
            code = 3;
        } else if (e instanceof SQLiteAbortException) {
            code = 4;
        } else if (e instanceof SQLiteConstraintException) {
            code = 5;
        } else if (e instanceof SQLiteDatabaseCorruptException) {
            code = 6;
        } else if (e instanceof SQLiteFullException) {
            code = 7;
        } else if (e instanceof SQLiteDiskIOException) {
            code = 8;
        } else if (e instanceof SQLiteException) {
            code = 9;
        } else if (e instanceof OperationApplicationException) {
            code = 10;
        } else if (e instanceof OperationCanceledException) {
            code = 11;
            logException = false;
        } else {
            reply.writeException(e);
            Log.e(str2, str, e);
            return;
        }
        reply.writeInt(code);
        reply.writeString(e.getMessage());
        if (logException) {
            Log.e(str2, str, e);
        }
    }

    public static final void readExceptionFromParcel(Parcel reply) {
        int code = reply.readExceptionCode();
        if (code != 0) {
            readExceptionFromParcel(reply, reply.readString(), code);
        }
    }

    public static void readExceptionWithFileNotFoundExceptionFromParcel(Parcel reply) throws FileNotFoundException {
        int code = reply.readExceptionCode();
        if (code != 0) {
            String msg = reply.readString();
            if (code != 1) {
                readExceptionFromParcel(reply, msg, code);
                return;
            }
            throw new FileNotFoundException(msg);
        }
    }

    public static void readExceptionWithOperationApplicationExceptionFromParcel(Parcel reply) throws OperationApplicationException {
        int code = reply.readExceptionCode();
        if (code != 0) {
            String msg = reply.readString();
            if (code != 10) {
                readExceptionFromParcel(reply, msg, code);
                return;
            }
            throw new OperationApplicationException(msg);
        }
    }

    private static final void readExceptionFromParcel(Parcel reply, String msg, int code) {
        switch (code) {
            case 2:
                throw new IllegalArgumentException(msg);
            case 3:
                throw new UnsupportedOperationException(msg);
            case 4:
                throw new SQLiteAbortException(msg);
            case 5:
                throw new SQLiteConstraintException(msg);
            case 6:
                throw new SQLiteDatabaseCorruptException(msg);
            case 7:
                throw new SQLiteFullException(msg);
            case 8:
                throw new SQLiteDiskIOException(msg);
            case 9:
                throw new SQLiteException(msg);
            case 11:
                throw new OperationCanceledException(msg);
            default:
                reply.readException(code, msg);
                return;
        }
    }

    public static void bindObjectToProgram(SQLiteProgram prog, int index, Object value) {
        if (value == null) {
            prog.bindNull(index);
        } else if ((value instanceof Double) || (value instanceof Float)) {
            prog.bindDouble(index, ((Number) value).doubleValue());
        } else if (value instanceof Number) {
            prog.bindLong(index, ((Number) value).longValue());
        } else if (value instanceof Boolean) {
            if (((Boolean) value).booleanValue()) {
                prog.bindLong(index, 1);
            } else {
                prog.bindLong(index, 0);
            }
        } else if (value instanceof byte[]) {
            prog.bindBlob(index, (byte[]) value);
        } else {
            prog.bindString(index, value.toString());
        }
    }

    public static String bindSelection(String selection, Object... selectionArgs) {
        if (selection == null) {
            return null;
        }
        if (ArrayUtils.isEmpty(selectionArgs) || selection.indexOf(63) == -1) {
            return selection;
        }
        char before = ' ';
        int arg = 0;
        char len = selection.length();
        StringBuilder res = new StringBuilder(len);
        char c = 0;
        while (c < len) {
            char i = c + 1;
            c = selection.charAt(c);
            if (c == '?') {
                Object arg2;
                char after = ' ';
                char start = i;
                while (i < len) {
                    c = selection.charAt(i);
                    if (c < '0' || c > '9') {
                        after = c;
                        break;
                    }
                    i++;
                }
                if (start != i) {
                    arg2 = Integer.parseInt(selection.substring(start, i)) - 1;
                }
                int argIndex = arg2 + 1;
                arg2 = selectionArgs[arg2];
                if (!(before == ' ' || before == '=')) {
                    res.append(' ');
                }
                int typeOfObject = getTypeOfObject(arg2);
                if (typeOfObject == 0) {
                    res.append(WifiEnterpriseConfig.EMPTY_VALUE);
                } else if (typeOfObject == 1) {
                    res.append(((Number) arg2).longValue());
                } else if (typeOfObject == 2) {
                    res.append(((Number) arg2).doubleValue());
                } else if (typeOfObject == 4) {
                    throw new IllegalArgumentException("Blobs not supported");
                } else if (arg2 instanceof Boolean) {
                    res.append(((Boolean) arg2).booleanValue());
                } else {
                    res.append(DateFormat.QUOTE);
                    res.append(arg2.toString());
                    res.append(DateFormat.QUOTE);
                }
                if (after != ' ') {
                    res.append(' ');
                }
                c = i;
                arg = argIndex;
            } else {
                res.append(c);
                before = c;
                c = i;
            }
        }
        return res.toString();
    }

    @UnsupportedAppUsage(maxTargetSdk = 28, trackingBug = 115609023)
    public static int getTypeOfObject(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof byte[]) {
            return 4;
        }
        if ((obj instanceof Float) || (obj instanceof Double)) {
            return 2;
        }
        if ((obj instanceof Long) || (obj instanceof Integer) || (obj instanceof Short) || (obj instanceof Byte)) {
            return 1;
        }
        return 3;
    }

    public static void cursorFillWindow(Cursor cursor, int position, CursorWindow window) {
        if (position >= 0 && position < cursor.getCount()) {
            int oldPos = cursor.getPosition();
            int numColumns = cursor.getColumnCount();
            window.clear();
            window.setStartPosition(position);
            window.setNumColumns(numColumns);
            if (cursor.moveToPosition(position)) {
                loop0:
                while (window.allocRow()) {
                    for (int i = 0; i < numColumns; i++) {
                        boolean success;
                        int type = cursor.getType(i);
                        if (type == 0) {
                            success = window.putNull(position, i);
                        } else if (type == 1) {
                            success = window.putLong(cursor.getLong(i), position, i);
                        } else if (type == 2) {
                            success = window.putDouble(cursor.getDouble(i), position, i);
                        } else if (type != 4) {
                            String value = cursor.getString(i);
                            if (value != null) {
                                success = window.putString(value, position, i);
                            } else {
                                success = window.putNull(position, i);
                            }
                        } else {
                            byte[] value2 = cursor.getBlob(i);
                            if (value2 != null) {
                                success = window.putBlob(value2, position, i);
                            } else {
                                success = window.putNull(position, i);
                            }
                        }
                        if (!success) {
                            window.freeLastRow();
                            break loop0;
                        }
                    }
                    position++;
                    if (!cursor.moveToNext()) {
                        break;
                    }
                }
            }
            cursor.moveToPosition(oldPos);
        }
    }

    public static void appendEscapedSQLString(StringBuilder sb, String sqlString) {
        sb.append(DateFormat.QUOTE);
        if (sqlString.indexOf(39) != -1) {
            int length = sqlString.length();
            for (int i = 0; i < length; i++) {
                char c = sqlString.charAt(i);
                if (c == DateFormat.QUOTE) {
                    sb.append(DateFormat.QUOTE);
                }
                sb.append(c);
            }
        } else {
            sb.append(sqlString);
        }
        sb.append(DateFormat.QUOTE);
    }

    public static String sqlEscapeString(String value) {
        StringBuilder escaper = new StringBuilder();
        appendEscapedSQLString(escaper, value);
        return escaper.toString();
    }

    public static final void appendValueToSql(StringBuilder sql, Object value) {
        if (value == null) {
            sql.append(WifiEnterpriseConfig.EMPTY_VALUE);
        } else if (!(value instanceof Boolean)) {
            appendEscapedSQLString(sql, value.toString());
        } else if (((Boolean) value).booleanValue()) {
            sql.append('1');
        } else {
            sql.append('0');
        }
    }

    public static String concatenateWhere(String a, String b) {
        if (TextUtils.isEmpty(a)) {
            return b;
        }
        if (TextUtils.isEmpty(b)) {
            return a;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        stringBuilder.append(a);
        stringBuilder.append(") AND (");
        stringBuilder.append(b);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public static String getCollationKey(String name) {
        byte[] arr = getCollationKeyInBytes(name);
        try {
            return new String(arr, 0, getKeyLen(arr), "ISO8859_1");
        } catch (Exception e) {
            return "";
        }
    }

    public static String getHexCollationKey(String name) {
        byte[] arr = getCollationKeyInBytes(name);
        return new String(encodeHex(arr), 0, getKeyLen(arr) * 2);
    }

    private static char[] encodeHex(byte[] input) {
        int l = input.length;
        char[] out = new char[(l << 1)];
        int j = 0;
        for (int i = 0; i < l; i++) {
            int i2 = j + 1;
            char[] cArr = DIGITS;
            out[j] = cArr[(input[i] & 240) >>> 4];
            j = i2 + 1;
            out[i2] = cArr[input[i] & 15];
        }
        return out;
    }

    private static int getKeyLen(byte[] arr) {
        if (arr[arr.length - 1] != (byte) 0) {
            return arr.length;
        }
        return arr.length - 1;
    }

    private static byte[] getCollationKeyInBytes(String name) {
        if (mColl == null) {
            mColl = Collator.getInstance();
            mColl.setStrength(0);
        }
        return mColl.getCollationKey(name).toByteArray();
    }

    public static void dumpCursor(Cursor cursor) {
        dumpCursor(cursor, System.out);
    }

    public static void dumpCursor(Cursor cursor, PrintStream stream) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(">>>>> Dumping cursor ");
        stringBuilder.append(cursor);
        stream.println(stringBuilder.toString());
        if (cursor != null) {
            int startPos = cursor.getPosition();
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                dumpCurrentRow(cursor, stream);
            }
            cursor.moveToPosition(startPos);
        }
        stream.println("<<<<<");
    }

    public static void dumpCursor(Cursor cursor, StringBuilder sb) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(">>>>> Dumping cursor ");
        stringBuilder.append(cursor);
        stringBuilder.append("\n");
        sb.append(stringBuilder.toString());
        if (cursor != null) {
            int startPos = cursor.getPosition();
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                dumpCurrentRow(cursor, sb);
            }
            cursor.moveToPosition(startPos);
        }
        sb.append("<<<<<\n");
    }

    public static String dumpCursorToString(Cursor cursor) {
        StringBuilder sb = new StringBuilder();
        dumpCursor(cursor, sb);
        return sb.toString();
    }

    public static void dumpCurrentRow(Cursor cursor) {
        dumpCurrentRow(cursor, System.out);
    }

    public static void dumpCurrentRow(Cursor cursor, PrintStream stream) {
        String[] cols = cursor.getColumnNames();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(cursor.getPosition());
        stringBuilder.append(" {");
        stream.println(stringBuilder.toString());
        int length = cols.length;
        for (int i = 0; i < length; i++) {
            String value;
            try {
                value = cursor.getString(i);
            } catch (SQLiteException e) {
                value = "<unprintable>";
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("   ");
            stringBuilder2.append(cols[i]);
            stringBuilder2.append('=');
            stringBuilder2.append(value);
            stream.println(stringBuilder2.toString());
        }
        stream.println("}");
    }

    public static void dumpCurrentRow(Cursor cursor, StringBuilder sb) {
        String[] cols = cursor.getColumnNames();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(cursor.getPosition());
        stringBuilder.append(" {\n");
        sb.append(stringBuilder.toString());
        int length = cols.length;
        for (int i = 0; i < length; i++) {
            String value;
            try {
                value = cursor.getString(i);
            } catch (SQLiteException e) {
                value = "<unprintable>";
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("   ");
            stringBuilder2.append(cols[i]);
            stringBuilder2.append('=');
            stringBuilder2.append(value);
            stringBuilder2.append("\n");
            sb.append(stringBuilder2.toString());
        }
        sb.append("}\n");
    }

    public static String dumpCurrentRowToString(Cursor cursor) {
        StringBuilder sb = new StringBuilder();
        dumpCurrentRow(cursor, sb);
        return sb.toString();
    }

    public static void cursorStringToContentValues(Cursor cursor, String field, ContentValues values) {
        cursorStringToContentValues(cursor, field, values, field);
    }

    public static void cursorStringToInsertHelper(Cursor cursor, String field, InsertHelper inserter, int index) {
        inserter.bind(index, cursor.getString(cursor.getColumnIndexOrThrow(field)));
    }

    public static void cursorStringToContentValues(Cursor cursor, String field, ContentValues values, String key) {
        values.put(key, cursor.getString(cursor.getColumnIndexOrThrow(field)));
    }

    public static void cursorIntToContentValues(Cursor cursor, String field, ContentValues values) {
        cursorIntToContentValues(cursor, field, values, field);
    }

    public static void cursorIntToContentValues(Cursor cursor, String field, ContentValues values, String key) {
        int colIndex = cursor.getColumnIndex(field);
        if (cursor.isNull(colIndex)) {
            values.put(key, (Integer) null);
        } else {
            values.put(key, Integer.valueOf(cursor.getInt(colIndex)));
        }
    }

    public static void cursorLongToContentValues(Cursor cursor, String field, ContentValues values) {
        cursorLongToContentValues(cursor, field, values, field);
    }

    public static void cursorLongToContentValues(Cursor cursor, String field, ContentValues values, String key) {
        int colIndex = cursor.getColumnIndex(field);
        if (cursor.isNull(colIndex)) {
            values.put(key, (Long) null);
        } else {
            values.put(key, Long.valueOf(cursor.getLong(colIndex)));
        }
    }

    public static void cursorDoubleToCursorValues(Cursor cursor, String field, ContentValues values) {
        cursorDoubleToContentValues(cursor, field, values, field);
    }

    public static void cursorDoubleToContentValues(Cursor cursor, String field, ContentValues values, String key) {
        int colIndex = cursor.getColumnIndex(field);
        if (cursor.isNull(colIndex)) {
            values.put(key, (Double) null);
        } else {
            values.put(key, Double.valueOf(cursor.getDouble(colIndex)));
        }
    }

    public static void cursorRowToContentValues(Cursor cursor, ContentValues values) {
        String[] columns = cursor.getColumnNames();
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            if (cursor.getType(i) == 4) {
                values.put(columns[i], cursor.getBlob(i));
            } else {
                values.put(columns[i], cursor.getString(i));
            }
        }
    }

    @UnsupportedAppUsage
    public static int cursorPickFillWindowStartPosition(int cursorPosition, int cursorWindowCapacity) {
        return Math.max(cursorPosition - (cursorWindowCapacity / 3), 0);
    }

    public static long queryNumEntries(SQLiteDatabase db, String table) {
        return queryNumEntries(db, table, null, null);
    }

    public static long queryNumEntries(SQLiteDatabase db, String table, String selection) {
        return queryNumEntries(db, table, selection, null);
    }

    public static long queryNumEntries(SQLiteDatabase db, String table, String selection, String[] selectionArgs) {
        String s;
        if (TextUtils.isEmpty(selection)) {
            s = "";
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" where ");
            stringBuilder.append(selection);
            s = stringBuilder.toString();
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("select count(*) from ");
        stringBuilder2.append(table);
        stringBuilder2.append(s);
        return longForQuery(db, stringBuilder2.toString(), selectionArgs);
    }

    public static boolean queryIsEmpty(SQLiteDatabase db, String table) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select exists(select 1 from ");
        stringBuilder.append(table);
        stringBuilder.append(")");
        return longForQuery(db, stringBuilder.toString(), null) == 0;
    }

    public static long longForQuery(SQLiteDatabase db, String query, String[] selectionArgs) {
        SQLiteStatement prog = db.compileStatement(query);
        try {
            long longForQuery = longForQuery(prog, selectionArgs);
            return longForQuery;
        } finally {
            prog.close();
        }
    }

    public static long longForQuery(SQLiteStatement prog, String[] selectionArgs) {
        prog.bindAllArgsAsStrings(selectionArgs);
        return prog.simpleQueryForLong();
    }

    public static String stringForQuery(SQLiteDatabase db, String query, String[] selectionArgs) {
        SQLiteStatement prog = db.compileStatement(query);
        try {
            String stringForQuery = stringForQuery(prog, selectionArgs);
            return stringForQuery;
        } finally {
            prog.close();
        }
    }

    public static String stringForQuery(SQLiteStatement prog, String[] selectionArgs) {
        prog.bindAllArgsAsStrings(selectionArgs);
        return prog.simpleQueryForString();
    }

    public static ParcelFileDescriptor blobFileDescriptorForQuery(SQLiteDatabase db, String query, String[] selectionArgs) {
        SQLiteStatement prog = db.compileStatement(query);
        try {
            ParcelFileDescriptor blobFileDescriptorForQuery = blobFileDescriptorForQuery(prog, selectionArgs);
            return blobFileDescriptorForQuery;
        } finally {
            prog.close();
        }
    }

    public static ParcelFileDescriptor blobFileDescriptorForQuery(SQLiteStatement prog, String[] selectionArgs) {
        prog.bindAllArgsAsStrings(selectionArgs);
        return prog.simpleQueryForBlobFileDescriptor();
    }

    public static void cursorStringToContentValuesIfPresent(Cursor cursor, ContentValues values, String column) {
        int index = cursor.getColumnIndex(column);
        if (index != -1 && !cursor.isNull(index)) {
            values.put(column, cursor.getString(index));
        }
    }

    public static void cursorLongToContentValuesIfPresent(Cursor cursor, ContentValues values, String column) {
        int index = cursor.getColumnIndex(column);
        if (index != -1 && !cursor.isNull(index)) {
            values.put(column, Long.valueOf(cursor.getLong(index)));
        }
    }

    public static void cursorShortToContentValuesIfPresent(Cursor cursor, ContentValues values, String column) {
        int index = cursor.getColumnIndex(column);
        if (index != -1 && !cursor.isNull(index)) {
            values.put(column, Short.valueOf(cursor.getShort(index)));
        }
    }

    public static void cursorIntToContentValuesIfPresent(Cursor cursor, ContentValues values, String column) {
        int index = cursor.getColumnIndex(column);
        if (index != -1 && !cursor.isNull(index)) {
            values.put(column, Integer.valueOf(cursor.getInt(index)));
        }
    }

    public static void cursorFloatToContentValuesIfPresent(Cursor cursor, ContentValues values, String column) {
        int index = cursor.getColumnIndex(column);
        if (index != -1 && !cursor.isNull(index)) {
            values.put(column, Float.valueOf(cursor.getFloat(index)));
        }
    }

    public static void cursorDoubleToContentValuesIfPresent(Cursor cursor, ContentValues values, String column) {
        int index = cursor.getColumnIndex(column);
        if (index != -1 && !cursor.isNull(index)) {
            values.put(column, Double.valueOf(cursor.getDouble(index)));
        }
    }

    public static void createDbFromSqlStatements(Context context, String dbName, int dbVersion, String sqlStatements) {
        int i = 0;
        SQLiteDatabase db = context.openOrCreateDatabase(dbName, 0, null);
        String[] statements = TextUtils.split(sqlStatements, ";\n");
        int length = statements.length;
        while (i < length) {
            String statement = statements[i];
            if (!TextUtils.isEmpty(statement)) {
                db.execSQL(statement);
            }
            i++;
        }
        db.setVersion(dbVersion);
        db.close();
    }

    public static int getSqlStatementType(String sql) {
        sql = sql.trim();
        if (sql.length() < 3) {
            return 99;
        }
        String prefixSql = sql.substring(0, 3).toUpperCase(Locale.ROOT);
        if (prefixSql.equals("SEL")) {
            return 1;
        }
        if (prefixSql.equals("INS") || prefixSql.equals("UPD") || prefixSql.equals("REP") || prefixSql.equals("DEL")) {
            return 2;
        }
        if (prefixSql.equals("ATT")) {
            return 3;
        }
        if (prefixSql.equals("COM") || prefixSql.equals("END")) {
            return 5;
        }
        if (prefixSql.equals("ROL")) {
            if (!sql.toUpperCase(Locale.ROOT).contains(" TO ")) {
                return 6;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Statement '");
            stringBuilder.append(sql);
            stringBuilder.append("' may not work on API levels 16-27, use ';");
            stringBuilder.append(sql);
            stringBuilder.append("' instead");
            Log.w(TAG, stringBuilder.toString());
            return 99;
        } else if (prefixSql.equals("BEG")) {
            return 4;
        } else {
            if (prefixSql.equals("PRA")) {
                return 7;
            }
            if (prefixSql.equals("CRE") || prefixSql.equals("DRO") || prefixSql.equals("ALT")) {
                return 8;
            }
            if (prefixSql.equals("ANA") || prefixSql.equals("DET")) {
                return 9;
            }
            return 99;
        }
    }

    public static String[] appendSelectionArgs(String[] originalValues, String[] newValues) {
        if (originalValues == null || originalValues.length == 0) {
            return newValues;
        }
        String[] result = new String[(originalValues.length + newValues.length)];
        System.arraycopy(originalValues, 0, result, 0, originalValues.length);
        System.arraycopy(newValues, 0, result, originalValues.length, newValues.length);
        return result;
    }

    public static int findRowIdColumnIndex(String[] columnNames) {
        int length = columnNames.length;
        for (int i = 0; i < length; i++) {
            if (columnNames[i].equals("_id")) {
                return i;
            }
        }
        return -1;
    }
}
