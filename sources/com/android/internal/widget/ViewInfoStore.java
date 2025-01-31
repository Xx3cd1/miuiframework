package com.android.internal.widget;

import android.util.ArrayMap;
import android.util.LongSparseArray;
import android.util.Pools.Pool;
import android.util.Pools.SimplePool;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.RecyclerView.ItemAnimator.ItemHolderInfo;
import com.android.internal.widget.RecyclerView.ViewHolder;

class ViewInfoStore {
    private static final boolean DEBUG = false;
    @VisibleForTesting
    final ArrayMap<ViewHolder, InfoRecord> mLayoutHolderMap = new ArrayMap();
    @VisibleForTesting
    final LongSparseArray<ViewHolder> mOldChangedHolders = new LongSparseArray();

    interface ProcessCallback {
        void processAppeared(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        void processDisappeared(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        void processPersistent(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        void unused(ViewHolder viewHolder);
    }

    static class InfoRecord {
        static final int FLAG_APPEAR = 2;
        static final int FLAG_APPEAR_AND_DISAPPEAR = 3;
        static final int FLAG_APPEAR_PRE_AND_POST = 14;
        static final int FLAG_DISAPPEARED = 1;
        static final int FLAG_POST = 8;
        static final int FLAG_PRE = 4;
        static final int FLAG_PRE_AND_POST = 12;
        static Pool<InfoRecord> sPool = new SimplePool(20);
        int flags;
        ItemHolderInfo postInfo;
        ItemHolderInfo preInfo;

        private InfoRecord() {
        }

        static InfoRecord obtain() {
            InfoRecord record = (InfoRecord) sPool.acquire();
            return record == null ? new InfoRecord() : record;
        }

        static void recycle(InfoRecord record) {
            record.flags = 0;
            record.preInfo = null;
            record.postInfo = null;
            sPool.release(record);
        }

        static void drainCache() {
            while (sPool.acquire() != null) {
            }
        }
    }

    ViewInfoStore() {
    }

    /* Access modifiers changed, original: 0000 */
    public void clear() {
        this.mLayoutHolderMap.clear();
        this.mOldChangedHolders.clear();
    }

    /* Access modifiers changed, original: 0000 */
    public void addToPreLayout(ViewHolder holder, ItemHolderInfo info) {
        InfoRecord record = (InfoRecord) this.mLayoutHolderMap.get(holder);
        if (record == null) {
            record = InfoRecord.obtain();
            this.mLayoutHolderMap.put(holder, record);
        }
        record.preInfo = info;
        record.flags |= 4;
    }

    /* Access modifiers changed, original: 0000 */
    public boolean isDisappearing(ViewHolder holder) {
        InfoRecord record = (InfoRecord) this.mLayoutHolderMap.get(holder);
        return (record == null || (record.flags & 1) == 0) ? false : true;
    }

    /* Access modifiers changed, original: 0000 */
    public ItemHolderInfo popFromPreLayout(ViewHolder vh) {
        return popFromLayoutStep(vh, 4);
    }

    /* Access modifiers changed, original: 0000 */
    public ItemHolderInfo popFromPostLayout(ViewHolder vh) {
        return popFromLayoutStep(vh, 8);
    }

    private ItemHolderInfo popFromLayoutStep(ViewHolder vh, int flag) {
        int index = this.mLayoutHolderMap.indexOfKey(vh);
        if (index < 0) {
            return null;
        }
        InfoRecord record = (InfoRecord) this.mLayoutHolderMap.valueAt(index);
        if (record == null || (record.flags & flag) == 0) {
            return null;
        }
        ItemHolderInfo info;
        record.flags &= ~flag;
        if (flag == 4) {
            info = record.preInfo;
        } else if (flag == 8) {
            info = record.postInfo;
        } else {
            throw new IllegalArgumentException("Must provide flag PRE or POST");
        }
        if ((record.flags & 12) == 0) {
            this.mLayoutHolderMap.removeAt(index);
            InfoRecord.recycle(record);
        }
        return info;
    }

    /* Access modifiers changed, original: 0000 */
    public void addToOldChangeHolders(long key, ViewHolder holder) {
        this.mOldChangedHolders.put(key, holder);
    }

    /* Access modifiers changed, original: 0000 */
    public void addToAppearedInPreLayoutHolders(ViewHolder holder, ItemHolderInfo info) {
        InfoRecord record = (InfoRecord) this.mLayoutHolderMap.get(holder);
        if (record == null) {
            record = InfoRecord.obtain();
            this.mLayoutHolderMap.put(holder, record);
        }
        record.flags |= 2;
        record.preInfo = info;
    }

    /* Access modifiers changed, original: 0000 */
    public boolean isInPreLayout(ViewHolder viewHolder) {
        InfoRecord record = (InfoRecord) this.mLayoutHolderMap.get(viewHolder);
        return (record == null || (record.flags & 4) == 0) ? false : true;
    }

    /* Access modifiers changed, original: 0000 */
    public ViewHolder getFromOldChangeHolders(long key) {
        return (ViewHolder) this.mOldChangedHolders.get(key);
    }

    /* Access modifiers changed, original: 0000 */
    public void addToPostLayout(ViewHolder holder, ItemHolderInfo info) {
        InfoRecord record = (InfoRecord) this.mLayoutHolderMap.get(holder);
        if (record == null) {
            record = InfoRecord.obtain();
            this.mLayoutHolderMap.put(holder, record);
        }
        record.postInfo = info;
        record.flags |= 8;
    }

    /* Access modifiers changed, original: 0000 */
    public void addToDisappearedInLayout(ViewHolder holder) {
        InfoRecord record = (InfoRecord) this.mLayoutHolderMap.get(holder);
        if (record == null) {
            record = InfoRecord.obtain();
            this.mLayoutHolderMap.put(holder, record);
        }
        record.flags |= 1;
    }

    /* Access modifiers changed, original: 0000 */
    public void removeFromDisappearedInLayout(ViewHolder holder) {
        InfoRecord record = (InfoRecord) this.mLayoutHolderMap.get(holder);
        if (record != null) {
            record.flags &= -2;
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void process(ProcessCallback callback) {
        for (int index = this.mLayoutHolderMap.size() - 1; index >= 0; index--) {
            ViewHolder viewHolder = (ViewHolder) this.mLayoutHolderMap.keyAt(index);
            InfoRecord record = (InfoRecord) this.mLayoutHolderMap.removeAt(index);
            if ((record.flags & 3) == 3) {
                callback.unused(viewHolder);
            } else if ((record.flags & 1) != 0) {
                if (record.preInfo == null) {
                    callback.unused(viewHolder);
                } else {
                    callback.processDisappeared(viewHolder, record.preInfo, record.postInfo);
                }
            } else if ((record.flags & 14) == 14) {
                callback.processAppeared(viewHolder, record.preInfo, record.postInfo);
            } else if ((record.flags & 12) == 12) {
                callback.processPersistent(viewHolder, record.preInfo, record.postInfo);
            } else if ((record.flags & 4) != 0) {
                callback.processDisappeared(viewHolder, record.preInfo, null);
            } else if ((record.flags & 8) != 0) {
                callback.processAppeared(viewHolder, record.preInfo, record.postInfo);
            } else {
                int i = record.flags;
            }
            InfoRecord.recycle(record);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void removeViewHolder(ViewHolder holder) {
        for (int i = this.mOldChangedHolders.size() - 1; i >= 0; i--) {
            if (holder == this.mOldChangedHolders.valueAt(i)) {
                this.mOldChangedHolders.removeAt(i);
                break;
            }
        }
        InfoRecord info = (InfoRecord) this.mLayoutHolderMap.remove(holder);
        if (info != null) {
            InfoRecord.recycle(info);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void onDetach() {
        InfoRecord.drainCache();
    }

    public void onViewDetached(ViewHolder viewHolder) {
        removeFromDisappearedInLayout(viewHolder);
    }
}
