package android.app.backup;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import java.io.File;

public class AbsoluteFileBackupHelper extends FileBackupHelperBase implements BackupHelper {
    private static final boolean DEBUG = false;
    private static final String TAG = "AbsoluteFileBackupHelper";
    Context mContext;
    String[] mFiles;

    @UnsupportedAppUsage
    public /* bridge */ /* synthetic */ void writeNewStateDescription(ParcelFileDescriptor parcelFileDescriptor) {
        super.writeNewStateDescription(parcelFileDescriptor);
    }

    public AbsoluteFileBackupHelper(Context context, String... files) {
        super(context);
        this.mContext = context;
        this.mFiles = files;
    }

    public void performBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) {
        String[] strArr = this.mFiles;
        FileBackupHelperBase.performBackup_checked(oldState, data, newState, strArr, strArr);
    }

    public void restoreEntity(BackupDataInputStream data) {
        String key = data.getKey();
        if (isKeyInList(key, this.mFiles)) {
            writeFile(new File(key), data);
        }
    }
}
