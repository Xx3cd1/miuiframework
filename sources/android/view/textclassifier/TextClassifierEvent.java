package android.view.textclassifier;

import android.icu.util.ULocale;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

public abstract class TextClassifierEvent implements Parcelable {
    public static final int CATEGORY_CONVERSATION_ACTIONS = 3;
    public static final int CATEGORY_LANGUAGE_DETECTION = 4;
    public static final int CATEGORY_LINKIFY = 2;
    public static final int CATEGORY_SELECTION = 1;
    public static final Creator<TextClassifierEvent> CREATOR = new Creator<TextClassifierEvent>() {
        public TextClassifierEvent createFromParcel(Parcel in) {
            int token = in.readInt();
            if (token == 1) {
                return new TextSelectionEvent(in, null);
            }
            if (token == 2) {
                return new TextLinkifyEvent(in, null);
            }
            if (token == 4) {
                return new LanguageDetectionEvent(in, null);
            }
            if (token == 3) {
                return new ConversationActionsEvent(in, null);
            }
            throw new IllegalStateException("Unexpected input event type token in parcel.");
        }

        public TextClassifierEvent[] newArray(int size) {
            return new TextClassifierEvent[size];
        }
    };
    private static final int PARCEL_TOKEN_CONVERSATION_ACTION_EVENT = 3;
    private static final int PARCEL_TOKEN_LANGUAGE_DETECTION_EVENT = 4;
    private static final int PARCEL_TOKEN_TEXT_LINKIFY_EVENT = 2;
    private static final int PARCEL_TOKEN_TEXT_SELECTION_EVENT = 1;
    public static final int TYPE_ACTIONS_GENERATED = 20;
    public static final int TYPE_ACTIONS_SHOWN = 6;
    public static final int TYPE_AUTO_SELECTION = 5;
    public static final int TYPE_COPY_ACTION = 9;
    public static final int TYPE_CUT_ACTION = 11;
    public static final int TYPE_LINK_CLICKED = 7;
    public static final int TYPE_MANUAL_REPLY = 19;
    public static final int TYPE_OTHER_ACTION = 16;
    public static final int TYPE_OVERTYPE = 8;
    public static final int TYPE_PASTE_ACTION = 10;
    public static final int TYPE_SELECTION_DESTROYED = 15;
    public static final int TYPE_SELECTION_DRAG = 14;
    public static final int TYPE_SELECTION_MODIFIED = 2;
    public static final int TYPE_SELECTION_RESET = 18;
    public static final int TYPE_SELECTION_STARTED = 1;
    public static final int TYPE_SELECT_ALL = 17;
    public static final int TYPE_SHARE_ACTION = 12;
    public static final int TYPE_SMART_ACTION = 13;
    public static final int TYPE_SMART_SELECTION_MULTI = 4;
    public static final int TYPE_SMART_SELECTION_SINGLE = 3;
    private final int[] mActionIndices;
    private final String[] mEntityTypes;
    private final int mEventCategory;
    private final TextClassificationContext mEventContext;
    private final int mEventIndex;
    private final int mEventType;
    private final Bundle mExtras;
    @VisibleForTesting(visibility = Visibility.PACKAGE)
    public TextClassificationSessionId mHiddenTempSessionId;
    private final ULocale mLocale;
    private final String mModelName;
    private final String mResultId;
    private final float[] mScores;

    public static abstract class Builder<T extends Builder<T>> {
        private int[] mActionIndices;
        private String[] mEntityTypes;
        private final int mEventCategory;
        private TextClassificationContext mEventContext;
        private int mEventIndex;
        private final int mEventType;
        private Bundle mExtras;
        private ULocale mLocale;
        private String mModelName;
        private String mResultId;
        private float[] mScores;

        public abstract T self();

        /* synthetic */ Builder(int x0, int x1, AnonymousClass1 x2) {
            this(x0, x1);
        }

        private Builder(int eventCategory, int eventType) {
            this.mEntityTypes = new String[0];
            this.mScores = new float[0];
            this.mActionIndices = new int[0];
            this.mEventCategory = eventCategory;
            this.mEventType = eventType;
        }

        public T setEntityTypes(String... entityTypes) {
            Preconditions.checkNotNull(entityTypes);
            this.mEntityTypes = new String[entityTypes.length];
            System.arraycopy(entityTypes, 0, this.mEntityTypes, 0, entityTypes.length);
            return self();
        }

        public T setEventContext(TextClassificationContext eventContext) {
            this.mEventContext = eventContext;
            return self();
        }

        public T setResultId(String resultId) {
            this.mResultId = resultId;
            return self();
        }

        public T setEventIndex(int eventIndex) {
            this.mEventIndex = eventIndex;
            return self();
        }

        public T setScores(float... scores) {
            Preconditions.checkNotNull(scores);
            this.mScores = new float[scores.length];
            System.arraycopy(scores, 0, this.mScores, 0, scores.length);
            return self();
        }

        public T setModelName(String modelVersion) {
            this.mModelName = modelVersion;
            return self();
        }

        public T setActionIndices(int... actionIndices) {
            this.mActionIndices = new int[actionIndices.length];
            System.arraycopy(actionIndices, 0, this.mActionIndices, 0, actionIndices.length);
            return self();
        }

        public T setLocale(ULocale locale) {
            this.mLocale = locale;
            return self();
        }

        public T setExtras(Bundle extras) {
            this.mExtras = (Bundle) Preconditions.checkNotNull(extras);
            return self();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Category {
    }

    public static final class ConversationActionsEvent extends TextClassifierEvent implements Parcelable {
        public static final Creator<ConversationActionsEvent> CREATOR = new Creator<ConversationActionsEvent>() {
            public ConversationActionsEvent createFromParcel(Parcel in) {
                in.readInt();
                return new ConversationActionsEvent(in, null);
            }

            public ConversationActionsEvent[] newArray(int size) {
                return new ConversationActionsEvent[size];
            }
        };

        public static final class Builder extends Builder<Builder> {
            public Builder(int eventType) {
                super(3, eventType, null);
            }

            /* Access modifiers changed, original: 0000 */
            public Builder self() {
                return this;
            }

            public ConversationActionsEvent build() {
                return new ConversationActionsEvent(this, null);
            }
        }

        private ConversationActionsEvent(Parcel in) {
            super(in, null);
        }

        private ConversationActionsEvent(Builder builder) {
            super((Builder) builder, null);
        }
    }

    public static final class LanguageDetectionEvent extends TextClassifierEvent implements Parcelable {
        public static final Creator<LanguageDetectionEvent> CREATOR = new Creator<LanguageDetectionEvent>() {
            public LanguageDetectionEvent createFromParcel(Parcel in) {
                in.readInt();
                return new LanguageDetectionEvent(in, null);
            }

            public LanguageDetectionEvent[] newArray(int size) {
                return new LanguageDetectionEvent[size];
            }
        };

        public static final class Builder extends Builder<Builder> {
            public Builder(int eventType) {
                super(4, eventType, null);
            }

            /* Access modifiers changed, original: 0000 */
            public Builder self() {
                return this;
            }

            public LanguageDetectionEvent build() {
                return new LanguageDetectionEvent(this, null);
            }
        }

        private LanguageDetectionEvent(Parcel in) {
            super(in, null);
        }

        private LanguageDetectionEvent(Builder builder) {
            super((Builder) builder, null);
        }
    }

    public static final class TextLinkifyEvent extends TextClassifierEvent implements Parcelable {
        public static final Creator<TextLinkifyEvent> CREATOR = new Creator<TextLinkifyEvent>() {
            public TextLinkifyEvent createFromParcel(Parcel in) {
                in.readInt();
                return new TextLinkifyEvent(in, null);
            }

            public TextLinkifyEvent[] newArray(int size) {
                return new TextLinkifyEvent[size];
            }
        };

        public static final class Builder extends Builder<Builder> {
            public Builder(int eventType) {
                super(2, eventType, null);
            }

            /* Access modifiers changed, original: 0000 */
            public Builder self() {
                return this;
            }

            public TextLinkifyEvent build() {
                return new TextLinkifyEvent(this, null);
            }
        }

        private TextLinkifyEvent(Parcel in) {
            super(in, null);
        }

        private TextLinkifyEvent(Builder builder) {
            super((Builder) builder, null);
        }
    }

    public static final class TextSelectionEvent extends TextClassifierEvent implements Parcelable {
        public static final Creator<TextSelectionEvent> CREATOR = new Creator<TextSelectionEvent>() {
            public TextSelectionEvent createFromParcel(Parcel in) {
                in.readInt();
                return new TextSelectionEvent(in, null);
            }

            public TextSelectionEvent[] newArray(int size) {
                return new TextSelectionEvent[size];
            }
        };
        final int mRelativeSuggestedWordEndIndex;
        final int mRelativeSuggestedWordStartIndex;
        final int mRelativeWordEndIndex;
        final int mRelativeWordStartIndex;

        public static final class Builder extends Builder<Builder> {
            int mRelativeSuggestedWordEndIndex;
            int mRelativeSuggestedWordStartIndex;
            int mRelativeWordEndIndex;
            int mRelativeWordStartIndex;

            public Builder(int eventType) {
                super(1, eventType, null);
            }

            public Builder setRelativeWordStartIndex(int relativeWordStartIndex) {
                this.mRelativeWordStartIndex = relativeWordStartIndex;
                return this;
            }

            public Builder setRelativeWordEndIndex(int relativeWordEndIndex) {
                this.mRelativeWordEndIndex = relativeWordEndIndex;
                return this;
            }

            public Builder setRelativeSuggestedWordStartIndex(int relativeSuggestedWordStartIndex) {
                this.mRelativeSuggestedWordStartIndex = relativeSuggestedWordStartIndex;
                return this;
            }

            public Builder setRelativeSuggestedWordEndIndex(int relativeSuggestedWordEndIndex) {
                this.mRelativeSuggestedWordEndIndex = relativeSuggestedWordEndIndex;
                return this;
            }

            /* Access modifiers changed, original: 0000 */
            public Builder self() {
                return this;
            }

            public TextSelectionEvent build() {
                return new TextSelectionEvent(this, null);
            }
        }

        private TextSelectionEvent(Builder builder) {
            super((Builder) builder, null);
            this.mRelativeWordStartIndex = builder.mRelativeWordStartIndex;
            this.mRelativeWordEndIndex = builder.mRelativeWordEndIndex;
            this.mRelativeSuggestedWordStartIndex = builder.mRelativeSuggestedWordStartIndex;
            this.mRelativeSuggestedWordEndIndex = builder.mRelativeSuggestedWordEndIndex;
        }

        private TextSelectionEvent(Parcel in) {
            super(in, null);
            this.mRelativeWordStartIndex = in.readInt();
            this.mRelativeWordEndIndex = in.readInt();
            this.mRelativeSuggestedWordStartIndex = in.readInt();
            this.mRelativeSuggestedWordEndIndex = in.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.mRelativeWordStartIndex);
            dest.writeInt(this.mRelativeWordEndIndex);
            dest.writeInt(this.mRelativeSuggestedWordStartIndex);
            dest.writeInt(this.mRelativeSuggestedWordEndIndex);
        }

        public int getRelativeWordStartIndex() {
            return this.mRelativeWordStartIndex;
        }

        public int getRelativeWordEndIndex() {
            return this.mRelativeWordEndIndex;
        }

        public int getRelativeSuggestedWordStartIndex() {
            return this.mRelativeSuggestedWordStartIndex;
        }

        public int getRelativeSuggestedWordEndIndex() {
            return this.mRelativeSuggestedWordEndIndex;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    private TextClassifierEvent(Builder builder) {
        this.mEventCategory = builder.mEventCategory;
        this.mEventType = builder.mEventType;
        this.mEntityTypes = builder.mEntityTypes;
        this.mEventContext = builder.mEventContext;
        this.mResultId = builder.mResultId;
        this.mEventIndex = builder.mEventIndex;
        this.mScores = builder.mScores;
        this.mModelName = builder.mModelName;
        this.mActionIndices = builder.mActionIndices;
        this.mLocale = builder.mLocale;
        this.mExtras = builder.mExtras == null ? Bundle.EMPTY : builder.mExtras;
    }

    private TextClassifierEvent(Parcel in) {
        this.mEventCategory = in.readInt();
        this.mEventType = in.readInt();
        this.mEntityTypes = in.readStringArray();
        ULocale uLocale = null;
        this.mEventContext = (TextClassificationContext) in.readParcelable(null);
        this.mResultId = in.readString();
        this.mEventIndex = in.readInt();
        this.mScores = new float[in.readInt()];
        in.readFloatArray(this.mScores);
        this.mModelName = in.readString();
        this.mActionIndices = in.createIntArray();
        String languageTag = in.readString();
        if (languageTag != null) {
            uLocale = ULocale.forLanguageTag(languageTag);
        }
        this.mLocale = uLocale;
        this.mExtras = in.readBundle();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getParcelToken());
        dest.writeInt(this.mEventCategory);
        dest.writeInt(this.mEventType);
        dest.writeStringArray(this.mEntityTypes);
        dest.writeParcelable(this.mEventContext, flags);
        dest.writeString(this.mResultId);
        dest.writeInt(this.mEventIndex);
        dest.writeInt(this.mScores.length);
        dest.writeFloatArray(this.mScores);
        dest.writeString(this.mModelName);
        dest.writeIntArray(this.mActionIndices);
        ULocale uLocale = this.mLocale;
        dest.writeString(uLocale == null ? null : uLocale.toLanguageTag());
        dest.writeBundle(this.mExtras);
    }

    private int getParcelToken() {
        if (this instanceof TextSelectionEvent) {
            return 1;
        }
        if (this instanceof TextLinkifyEvent) {
            return 2;
        }
        if (this instanceof LanguageDetectionEvent) {
            return 4;
        }
        if (this instanceof ConversationActionsEvent) {
            return 3;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unexpected type: ");
        stringBuilder.append(getClass().getSimpleName());
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public int getEventCategory() {
        return this.mEventCategory;
    }

    public int getEventType() {
        return this.mEventType;
    }

    public String[] getEntityTypes() {
        return this.mEntityTypes;
    }

    public TextClassificationContext getEventContext() {
        return this.mEventContext;
    }

    public String getResultId() {
        return this.mResultId;
    }

    public int getEventIndex() {
        return this.mEventIndex;
    }

    public float[] getScores() {
        return this.mScores;
    }

    public String getModelName() {
        return this.mModelName;
    }

    public int[] getActionIndices() {
        return this.mActionIndices;
    }

    public ULocale getLocale() {
        return this.mLocale;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public String toString() {
        StringBuilder out = new StringBuilder(128);
        out.append(getClass().getSimpleName());
        out.append("{");
        out.append("mEventCategory=");
        out.append(this.mEventCategory);
        out.append(", mEventTypes=");
        out.append(Arrays.toString(this.mEntityTypes));
        out.append(", mEventContext=");
        out.append(this.mEventContext);
        out.append(", mResultId=");
        out.append(this.mResultId);
        out.append(", mEventIndex=");
        out.append(this.mEventIndex);
        out.append(", mExtras=");
        out.append(this.mExtras);
        out.append(", mScores=");
        out.append(Arrays.toString(this.mScores));
        out.append(", mModelName=");
        out.append(this.mModelName);
        out.append(", mActionIndices=");
        out.append(Arrays.toString(this.mActionIndices));
        out.append("}");
        return out.toString();
    }

    @VisibleForTesting(visibility = Visibility.PACKAGE)
    public final SelectionEvent toSelectionEvent() {
        int eventType;
        int eventCategory = getEventCategory();
        if (eventCategory == 1) {
            eventCategory = 1;
        } else if (eventCategory != 2) {
            return null;
        } else {
            eventCategory = 2;
        }
        String str = "";
        SelectionEvent selectionEvent = new SelectionEvent(0, 0, 0, getEntityTypes().length > 0 ? getEntityTypes()[0] : str, 0, "");
        selectionEvent.setInvocationMethod(eventCategory);
        if (getEventContext() != null) {
            selectionEvent.setTextClassificationSessionContext(getEventContext());
        }
        selectionEvent.setSessionId(this.mHiddenTempSessionId);
        String resultId = getResultId();
        if (resultId != null) {
            str = resultId;
        }
        selectionEvent.setResultId(str);
        selectionEvent.setEventIndex(getEventIndex());
        switch (getEventType()) {
            case 1:
                eventType = 1;
                break;
            case 2:
                eventType = 2;
                break;
            case 3:
                eventType = 3;
                break;
            case 4:
                eventType = 4;
                break;
            case 5:
                eventType = 5;
                break;
            case 8:
                eventType = 100;
                break;
            case 9:
                eventType = 101;
                break;
            case 10:
                eventType = 102;
                break;
            case 11:
                eventType = 103;
                break;
            case 12:
                eventType = 104;
                break;
            case 13:
                eventType = 105;
                break;
            case 14:
                eventType = 106;
                break;
            case 15:
                eventType = 107;
                break;
            case 16:
                eventType = 108;
                break;
            case 17:
                eventType = 200;
                break;
            case 18:
                eventType = 201;
                break;
            default:
                eventType = 0;
                break;
        }
        selectionEvent.setEventType(eventType);
        if (this instanceof TextSelectionEvent) {
            TextSelectionEvent selEvent = (TextSelectionEvent) this;
            selectionEvent.setStart(selEvent.getRelativeWordStartIndex());
            selectionEvent.setEnd(selEvent.getRelativeWordEndIndex());
            selectionEvent.setSmartStart(selEvent.getRelativeSuggestedWordStartIndex());
            selectionEvent.setSmartEnd(selEvent.getRelativeSuggestedWordEndIndex());
        }
        return selectionEvent;
    }
}
