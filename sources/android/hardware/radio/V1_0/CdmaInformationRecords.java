package android.hardware.radio.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

public final class CdmaInformationRecords {
    public ArrayList<CdmaInformationRecord> infoRec = new ArrayList();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != CdmaInformationRecords.class) {
            return false;
        }
        if (HidlSupport.deepEquals(this.infoRec, ((CdmaInformationRecords) otherObject).infoRec)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(HidlSupport.deepHashCode(this.infoRec))});
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(".infoRec = ");
        builder.append(this.infoRec);
        builder.append("}");
        return builder.toString();
    }

    public final void readFromParcel(HwParcel parcel) {
        readEmbeddedFromParcel(parcel, parcel.readBuffer(16), 0);
    }

    public static final ArrayList<CdmaInformationRecords> readVectorFromParcel(HwParcel parcel) {
        ArrayList<CdmaInformationRecords> _hidl_vec = new ArrayList();
        HwBlob _hidl_blob = parcel.readBuffer(16);
        int _hidl_vec_size = _hidl_blob.getInt32(8);
        HwBlob childBlob = parcel.readEmbeddedBuffer((long) (_hidl_vec_size * 16), _hidl_blob.handle(), 0, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CdmaInformationRecords _hidl_vec_element = new CdmaInformationRecords();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, (long) (_hidl_index_0 * 16));
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        HwParcel hwParcel;
        int _hidl_vec_size = _hidl_blob.getInt32((_hidl_offset + 0) + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer((long) (_hidl_vec_size * 120), _hidl_blob.handle(), (_hidl_offset + 0) + 0, true);
        this.infoRec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CdmaInformationRecord _hidl_vec_element = new CdmaInformationRecord();
            hwParcel = parcel;
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, (long) (_hidl_index_0 * 120));
            this.infoRec.add(_hidl_vec_element);
        }
        hwParcel = parcel;
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(16);
        writeEmbeddedToBlob(_hidl_blob, 0);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<CdmaInformationRecords> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8, _hidl_vec_size);
        _hidl_blob.putBool(12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 16);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            ((CdmaInformationRecords) _hidl_vec.get(_hidl_index_0)).writeEmbeddedToBlob(childBlob, (long) (_hidl_index_0 * 16));
        }
        _hidl_blob.putBlob(0, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        int _hidl_vec_size = this.infoRec.size();
        _hidl_blob.putInt32((_hidl_offset + 0) + 8, _hidl_vec_size);
        _hidl_blob.putBool((_hidl_offset + 0) + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 120);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            ((CdmaInformationRecord) this.infoRec.get(_hidl_index_0)).writeEmbeddedToBlob(childBlob, (long) (_hidl_index_0 * 120));
        }
        _hidl_blob.putBlob((_hidl_offset + 0) + 0, childBlob);
    }
}
