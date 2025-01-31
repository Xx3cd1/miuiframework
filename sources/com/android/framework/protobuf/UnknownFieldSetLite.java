package com.android.framework.protobuf;

import java.io.IOException;
import java.util.Arrays;

public final class UnknownFieldSetLite {
    private static final UnknownFieldSetLite DEFAULT_INSTANCE = new UnknownFieldSetLite(0, new int[0], new Object[0], false);
    private static final int MIN_CAPACITY = 8;
    private int count;
    private boolean isMutable;
    private int memoizedSerializedSize;
    private Object[] objects;
    private int[] tags;

    public static UnknownFieldSetLite getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    static UnknownFieldSetLite newInstance() {
        return new UnknownFieldSetLite();
    }

    static UnknownFieldSetLite mutableCopyOf(UnknownFieldSetLite first, UnknownFieldSetLite second) {
        int count = first.count + second.count;
        int[] tags = Arrays.copyOf(first.tags, count);
        System.arraycopy(second.tags, 0, tags, first.count, second.count);
        Object[] objects = Arrays.copyOf(first.objects, count);
        System.arraycopy(second.objects, 0, objects, first.count, second.count);
        return new UnknownFieldSetLite(count, tags, objects, true);
    }

    private UnknownFieldSetLite() {
        this(0, new int[8], new Object[8], true);
    }

    private UnknownFieldSetLite(int count, int[] tags, Object[] objects, boolean isMutable) {
        this.memoizedSerializedSize = -1;
        this.count = count;
        this.tags = tags;
        this.objects = objects;
        this.isMutable = isMutable;
    }

    public void makeImmutable() {
        this.isMutable = false;
    }

    /* Access modifiers changed, original: 0000 */
    public void checkMutable() {
        if (!this.isMutable) {
            throw new UnsupportedOperationException();
        }
    }

    public void writeTo(CodedOutputStream output) throws IOException {
        for (int i = 0; i < this.count; i++) {
            int tag = this.tags[i];
            int fieldNumber = WireFormat.getTagFieldNumber(tag);
            int tagWireType = WireFormat.getTagWireType(tag);
            if (tagWireType == 0) {
                output.writeUInt64(fieldNumber, ((Long) this.objects[i]).longValue());
            } else if (tagWireType == 1) {
                output.writeFixed64(fieldNumber, ((Long) this.objects[i]).longValue());
            } else if (tagWireType == 2) {
                output.writeBytes(fieldNumber, (ByteString) this.objects[i]);
            } else if (tagWireType == 3) {
                output.writeTag(fieldNumber, 3);
                ((UnknownFieldSetLite) this.objects[i]).writeTo(output);
                output.writeTag(fieldNumber, 4);
            } else if (tagWireType == 5) {
                output.writeFixed32(fieldNumber, ((Integer) this.objects[i]).intValue());
            } else {
                throw InvalidProtocolBufferException.invalidWireType();
            }
        }
    }

    public int getSerializedSize() {
        int size = this.memoizedSerializedSize;
        if (size != -1) {
            return size;
        }
        size = 0;
        for (int i = 0; i < this.count; i++) {
            int tag = this.tags[i];
            int fieldNumber = WireFormat.getTagFieldNumber(tag);
            int tagWireType = WireFormat.getTagWireType(tag);
            if (tagWireType == 0) {
                tagWireType = CodedOutputStream.computeUInt64Size(fieldNumber, ((Long) this.objects[i]).longValue());
            } else if (tagWireType == 1) {
                tagWireType = CodedOutputStream.computeFixed64Size(fieldNumber, ((Long) this.objects[i]).longValue());
            } else if (tagWireType == 2) {
                tagWireType = CodedOutputStream.computeBytesSize(fieldNumber, (ByteString) this.objects[i]);
            } else if (tagWireType == 3) {
                tagWireType = (CodedOutputStream.computeTagSize(fieldNumber) * 2) + ((UnknownFieldSetLite) this.objects[i]).getSerializedSize();
            } else if (tagWireType == 5) {
                tagWireType = CodedOutputStream.computeFixed32Size(fieldNumber, ((Integer) this.objects[i]).intValue());
            } else {
                throw new IllegalStateException(InvalidProtocolBufferException.invalidWireType());
            }
            size += tagWireType;
        }
        this.memoizedSerializedSize = size;
        return size;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof UnknownFieldSetLite)) {
            return false;
        }
        UnknownFieldSetLite other = (UnknownFieldSetLite) obj;
        if (this.count == other.count && Arrays.equals(this.tags, other.tags) && Arrays.deepEquals(this.objects, other.objects)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((((17 * 31) + this.count) * 31) + Arrays.hashCode(this.tags)) * 31) + Arrays.deepHashCode(this.objects);
    }

    /* Access modifiers changed, original: final */
    public final void printWithIndent(StringBuilder buffer, int indent) {
        for (int i = 0; i < this.count; i++) {
            MessageLiteToString.printField(buffer, indent, String.valueOf(WireFormat.getTagFieldNumber(this.tags[i])), this.objects[i]);
        }
    }

    private void storeField(int tag, Object value) {
        ensureCapacity();
        int[] iArr = this.tags;
        int i = this.count;
        iArr[i] = tag;
        this.objects[i] = value;
        this.count = i + 1;
    }

    private void ensureCapacity() {
        int increment = this.count;
        if (increment == this.tags.length) {
            int newLength = this.count + (increment < 4 ? 8 : increment >> 1);
            this.tags = Arrays.copyOf(this.tags, newLength);
            this.objects = Arrays.copyOf(this.objects, newLength);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public boolean mergeFieldFrom(int tag, CodedInputStream input) throws IOException {
        checkMutable();
        int fieldNumber = WireFormat.getTagFieldNumber(tag);
        int tagWireType = WireFormat.getTagWireType(tag);
        if (tagWireType == 0) {
            storeField(tag, Long.valueOf(input.readInt64()));
            return true;
        } else if (tagWireType == 1) {
            storeField(tag, Long.valueOf(input.readFixed64()));
            return true;
        } else if (tagWireType == 2) {
            storeField(tag, input.readBytes());
            return true;
        } else if (tagWireType == 3) {
            UnknownFieldSetLite subFieldSet = new UnknownFieldSetLite();
            subFieldSet.mergeFrom(input);
            input.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            storeField(tag, subFieldSet);
            return true;
        } else if (tagWireType == 4) {
            return false;
        } else {
            if (tagWireType == 5) {
                storeField(tag, Integer.valueOf(input.readFixed32()));
                return true;
            }
            throw InvalidProtocolBufferException.invalidWireType();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public UnknownFieldSetLite mergeVarintField(int fieldNumber, int value) {
        checkMutable();
        if (fieldNumber != 0) {
            storeField(WireFormat.makeTag(fieldNumber, 0), Long.valueOf((long) value));
            return this;
        }
        throw new IllegalArgumentException("Zero is not a valid field number.");
    }

    /* Access modifiers changed, original: 0000 */
    public UnknownFieldSetLite mergeLengthDelimitedField(int fieldNumber, ByteString value) {
        checkMutable();
        if (fieldNumber != 0) {
            storeField(WireFormat.makeTag(fieldNumber, 2), value);
            return this;
        }
        throw new IllegalArgumentException("Zero is not a valid field number.");
    }

    private UnknownFieldSetLite mergeFrom(CodedInputStream input) throws IOException {
        while (true) {
            int tag = input.readTag();
            if (tag == 0 || !mergeFieldFrom(tag, input)) {
                return this;
            }
        }
        return this;
    }
}
