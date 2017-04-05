package au.org.ala.mobile.ozatlas.ui

import android.database.Cursor
import android.database.DataSetObserver
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.SparseIntArray
import timber.log.Timber

/**
 * Provides ability to bind a recycler view to a Android database cursor
 * Created by skyfishjy on 10/31/14.
 * Modified by emuneee on 1/5/16.
 */
abstract class CursorRecyclerViewAdapter<VH : RecyclerView.ViewHolder>
@JvmOverloads constructor(cursor: Cursor?, private val mComparisonColumn: String? = null) : RecyclerView.Adapter<VH>() {
    private val mDataSetObserver: DataSetObserver
    private var mRowIdColumn: Int = 0
    var cursor: Cursor?
        private set
    private var mDataValid: Boolean = false

    init {
        this.cursor = cursor
        mDataValid = cursor != null
        mRowIdColumn = if (mDataValid && cursor != null) cursor.getColumnIndex("_id") else -1
        mDataSetObserver = NotifyingDataSetObserver()

        this.cursor?.let { c -> c.registerDataSetObserver(mDataSetObserver) }
    }

    override fun getItemCount(): Int {
        Timber.v("getItemCount")
        return if (mDataValid) {
            cursor?.let { c -> c.count } ?: 0
        } else {
            0
        }
    }


    override fun getItemId(position: Int): Long {
        Timber.v("getItemId $position")
        return if (mDataValid) {
            cursor?.let { c ->
                if (c.moveToPosition(position)) {
                    c.getLong(mRowIdColumn)
                } else {
                    0
                }
            } ?: 0
        } else {
            0
        }
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        Timber.v("setHasStableIds")
        super.setHasStableIds(true)
    }

    abstract fun onBindViewHolder(viewHolder: VH, cursor: Cursor, position: Int)

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        Timber.v("onBindViewHolder")
        val c = cursor
        if (!mDataValid || c == null) {
            throw IllegalStateException("this should only be called when the cursor is valid")
        }


        if (!c.moveToPosition(position)) {
            throw IllegalStateException("couldn't move cursor to position $position")
        }
        onBindViewHolder(viewHolder, c, position)
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    fun changeCursor(cursor: Cursor?) {
        Timber.v("changeCursor")

        val current = this.cursor

        if (current == null) {
            swapCursor(cursor, null)
        } else {
            var changes: SparseIntArray? = null

            if (cursor != null && cursor !== current && !TextUtils.isEmpty(mComparisonColumn)) {
                changes = diffCursors(current, cursor)
            }
            val old = swapCursor(cursor, changes)

            old?.close()
        }
    }

    /**
     * Processes two cursors, old/existing cursor and a new cursor, returning a list of indexes who's
     * records were inserted, deleted, or changed
     * @param oldCursor
     * @param newCursor
     * @return
     */
    private fun diffCursors(oldCursor: Cursor, newCursor: Cursor): SparseIntArray {
        Timber.v("diffCursors")
        val changedOrInserted = getChangeOrInsertRecords(oldCursor, newCursor)

        // all records were inserted in new cursor
        if (changedOrInserted.get(ALL) == INSERTED) {
            return changedOrInserted
        }

        val deleted = getDeletedRecords(oldCursor, newCursor)

        if (deleted.get(ALL) == INSERTED) {
            return deleted
        }
        val changes = SparseIntArray(changedOrInserted.size() + deleted.size())

        for (i in 0..changedOrInserted.size() - 1) {
            changes.put(changedOrInserted.keyAt(i), changedOrInserted.valueAt(i))
        }

        for (i in 0..deleted.size() - 1) {
            changes.put(deleted.keyAt(i), deleted.valueAt(i))
        }
        return changes
    }

    /**
     * Returns a list of indexes of records that were deleted
     * May also return whether or not ALL records were inserted
     * @param oldCursor
     * @param newCursor
     * @return
     */
    private fun getDeletedRecords(oldCursor: Cursor, newCursor: Cursor): SparseIntArray {
        Timber.v("getDeletedRecords")
        val changes = SparseIntArray()
        val newCursorPosition = newCursor.position

        if (oldCursor.moveToFirst()) {
            var cursorIndex = 0

            // loop old cursor
            do {

                if (newCursor.moveToFirst()) {
                    var oldRecordFound = false

                    // loop new cursor
                    do {

                        // we found a record match
                        if (oldCursor.getInt(mRowIdColumn) == newCursor.getInt(mRowIdColumn)) {
                            oldRecordFound = true
                            break
                        }
                    } while (newCursor.moveToNext())

                    if (!oldRecordFound) {
                        changes.put(cursorIndex, REMOVED)
                    }
                    cursorIndex++
                }

            } while (oldCursor.moveToNext())
        } else {
            changes.put(ALL, INSERTED)
        }// unable to move the old cursor to the first record, all records in new were adde
        newCursor.moveToPosition(newCursorPosition)
        return changes
    }

    /**
     * Returns an array of indexes who's records were newly inserted or changed
     * Will also return whether or not all the records were inserted or removed
     * @param oldCursor
     * *
     * @param newCursor
     * *
     * @return
     */
    private fun getChangeOrInsertRecords(oldCursor: Cursor, newCursor: Cursor): SparseIntArray {
        Timber.v("getChangeOrInsertRecords")
        val changes = SparseIntArray()
        val newCursorPosition = newCursor.position

        if (newCursor.moveToFirst()) {
            val columnIndex = oldCursor.getColumnIndex(mComparisonColumn)
            var cursorIndex = 0

            // loop
            do {

                if (oldCursor.moveToFirst()) {
                    var newRecordFound = false

                    // loop
                    do {

                        // we found a record match
                        if (oldCursor.getInt(mRowIdColumn) == newCursor.getInt(mRowIdColumn)) {
                            newRecordFound = true

                            // values are different, this record has changed
                            if (!oldCursor.getString(columnIndex).contentEquals(newCursor.getString(columnIndex))) {
                                changes.put(cursorIndex, CHANGED)
                            }
                            break
                        }
                    } while (oldCursor.moveToNext())

                    // new record not found in old cursor, it was newly inserted
                    if (!newRecordFound) {
                        changes.put(cursorIndex, INSERTED)
                    }
                    cursorIndex++
                } else {
                    changes.put(ALL, INSERTED)
                    break
                }// unable to move the new cursor, all records in new are inserted
            } while (newCursor.moveToNext())
        } else {
            changes.put(ALL, REMOVED)
        }// unable to move new cursor to first
        newCursor.moveToPosition(newCursorPosition)
        return changes
    }

    /**
     * @param newCursor
     * @param changes
     * @return
     */
    private fun swapCursor(newCursor: Cursor?, changes: SparseIntArray?): Cursor? {
        Timber.v("swapCursor")
        if (newCursor === cursor) {
            return null
        }
        val oldCursor = cursor

        oldCursor?.unregisterDataSetObserver(mDataSetObserver)
        cursor = newCursor

        if (newCursor != null) {
            newCursor.registerDataSetObserver(mDataSetObserver)
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id")
            mDataValid = true
        } else {
            mRowIdColumn = -1
            mDataValid = false
        }

        if (newCursor != null && changes != null) {
            // process changes
            if (changes.get(ALL) == INSERTED) {
                notifyItemRangeInserted(0, newCursor.count)
            } else if (changes.get(ALL) == REMOVED) {
                notifyItemRangeRemoved(0, newCursor.count)
            } else {

                for (i in 0..changes.size() - 1) {
                    when (changes.valueAt(i)) {
                        CHANGED -> notifyItemChanged(changes.keyAt(i))
                        INSERTED -> notifyItemInserted(changes.keyAt(i))
                        REMOVED -> notifyItemRemoved(changes.keyAt(i))
                    }
                }
            }
        } else if (newCursor != null) {
            notifyItemRangeInserted(0, newCursor.count)
        }
        return oldCursor
    }

    private inner class NotifyingDataSetObserver : DataSetObserver() {

        override fun onChanged() {
            Timber.v("onChanged")
            super.onChanged()
            mDataValid = true
        }

        override fun onInvalidated() {
            Timber.v("onInvalidated")
            super.onInvalidated()
            mDataValid = false
        }
    }

    companion object {
        private val INSERTED = 1
        private val REMOVED = 2
        private val CHANGED = 3
        private val ALL = -1
    }
}