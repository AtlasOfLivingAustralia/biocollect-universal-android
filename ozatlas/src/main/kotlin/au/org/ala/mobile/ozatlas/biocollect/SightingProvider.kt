package au.org.ala.mobile.ozatlas.biocollect

import android.annotation.TargetApi
import android.app.Application
import android.content.*
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import au.org.ala.mobile.ozatlas.OzAtlasApp
import au.org.ala.mobile.ozatlas.db.DaoMaster
import au.org.ala.mobile.ozatlas.db.DaoSession
import au.org.ala.mobile.ozatlas.db.PhotoDao
import au.org.ala.mobile.ozatlas.db.SightingDao
import timber.log.Timber
import java.lang.reflect.Field


/* Copy this code snippet into your AndroidManifest.xml inside the <application> element:

    <provider
        android:name="au.org.ala.mobile.ozatlas.biocollect.SightingProvider"
        android:authorities="au.org.ala.mobile.ozatlas" />
*/
class SightingProvider : ContentProvider() {

//    public static ${schema.prefix}DaoSession daoSession;

    companion object {

        const val SIGHTING_DIR = 1
        const val SIGHTING_ID = 2
        const val PHOTO_DIR = 101
        const val PHOTO_ID = 102

        // The "Content authority" is a name for the entire content provider, similar to the
        // relationship between a domain name and its website.  A convenient string to use for the
        // content authority is the package name for the app, which is guaranteed to be unique on the
        // device.
        const val CONTENT_AUTHORITY = "au.org.ala.mobile.ozatlas"
        const val PATH_SIGHTING = "sighting"
        const val PATH_PHOTO = "photo"

        val CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY/$PATH_SIGHTING")
        val CONTENT_TYPE = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/$CONTENT_AUTHORITY/$PATH_SIGHTING"
        val CONTENT_ITEM_TYPE = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/$CONTENT_AUTHORITY/$PATH_SIGHTING"

        val URI_MATCHER = buildUriMatcher()

        internal fun buildUriMatcher(): UriMatcher {
            // I know what you're thinking.  Why create a UriMatcher when you can use regular
            // expressions instead?  Because you're not crazy, that's why.

            // All paths added to the UriMatcher have a corresponding code to return when a match is
            // found.  The code passed into the constructor represents the code to return for the root
            // URI.  It's common to use NO_MATCH as the code for this case.
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = CONTENT_AUTHORITY

            // For each type of URI you want to add, create a corresponding code.
            matcher.addURI(authority, PATH_SIGHTING, SIGHTING_DIR)
            matcher.addURI(authority, "$PATH_SIGHTING/#", SIGHTING_ID)
            matcher.addURI(authority, "$PATH_SIGHTING/#/$PATH_PHOTO", PHOTO_DIR)
            matcher.addURI(authority, "$PATH_SIGHTING/#/$PATH_PHOTO/#", PHOTO_ID)

            return matcher
        }
    }

    lateinit var daoSession : DaoSession
    lateinit private var openHelper : DaoMaster.OpenHelper

    override fun onCreate(): Boolean {
        openHelper = OzAtlasApp[context].component.openHelper()
        return true
    }

    private val database by lazy {
        openHelper.writableDatabase
//        return daoSession.database.rawDatabase as SQLiteDatabase
    }

    override fun getType(uri: Uri): String? =
        when(URI_MATCHER.match(uri)) {
            SIGHTING_DIR -> CONTENT_TYPE
            SIGHTING_ID -> CONTENT_ITEM_TYPE
            else -> throw UnsupportedOperationException("Unknown URI: $uri")
        }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = URI_MATCHER.match(uri)
        val path = when (uriType) {
            SIGHTING_DIR -> {
                insertTo(SightingDao.TABLENAME, uri, values)
            }
            PHOTO_DIR -> {
                insertTo(PhotoDao.TABLENAME, uri, values)
            }
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        context.contentResolver.notifyChange(uri, null)
        return path
    }

    private fun insertTo(tableName: String, uri: Uri, values: ContentValues?) : Uri {
        val id = database.insert(tableName, null, values)
        if (id == -1L) {
            throw SQLException("Failed to insert into $uri")
        }
        return uri.buildUpon().appendPath(id.toString()).build()
    }


    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        val queryBuilder = SQLiteQueryBuilder()
        val uriType = URI_MATCHER.match(uri)
        when (uriType) {
            SIGHTING_DIR -> queryBuilder.tables = SightingDao.TABLENAME;
            SIGHTING_ID -> {
                queryBuilder.tables = SightingDao.TABLENAME
                queryBuilder.appendWhere(SightingDao.Properties.Id.columnName + "=" + uri.lastPathSegment)
            }
            PHOTO_DIR -> {
                queryBuilder.tables = PhotoDao.TABLENAME
                queryBuilder.appendWhere(PhotoDao.Properties.SightingId.columnName + "=" + uri.pathSegments[1])
            }
            PHOTO_ID -> {
                queryBuilder.tables = PhotoDao.TABLENAME
                queryBuilder.appendWhere(PhotoDao.Properties.SightingId.columnName + "=" + uri.pathSegments[1] + " AND " + PhotoDao.Properties.Id.columnName + "=" + uri.lastPathSegment)
            }
            else -> throw IllegalArgumentException("Unknown URI: " + uri);
        }

        val db = database
        val cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        cursor.setNotificationUri(context.contentResolver, uri)

        return cursor
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val uriType = URI_MATCHER.match(uri)
        val db = database
        val rowsUpdated : Int
        when (uriType) {
            SIGHTING_DIR ->  rowsUpdated = db.update(SightingDao.TABLENAME, values, selection, selectionArgs)
            SIGHTING_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(SightingDao.TABLENAME, values, SightingDao.Properties.Id.columnName + "=" + id, null)
                } else {
                    rowsUpdated = db.update(SightingDao.TABLENAME, values, SightingDao.Properties.Id.columnName + "=" + id
                            + " and " + selection, selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: " + uri);
        }
        if (rowsUpdated > 0) {
            context.contentResolver.notifyChange(uri, null)
        }
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val uriType = URI_MATCHER.match(uri)
        val db = database
        val rowsDeleted : Int
        when (uriType) {
            SIGHTING_DIR -> rowsDeleted = db.delete(SightingDao.TABLENAME, selection ?: "1", selectionArgs)
            SIGHTING_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {
                    // probably safe since the id must match a number
                    rowsDeleted = db.delete(SightingDao.TABLENAME, SightingDao.Properties.Id.columnName + "=" + id, null)
                } else {
                    rowsDeleted = db.delete(SightingDao.TABLENAME, SightingDao.Properties.Id.columnName + "=" + id + " and "
                            + selection, selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: " + uri)
        }
        if (rowsDeleted > 1) {
            context.contentResolver.notifyChange(uri, null)
        }
        return rowsDeleted
    }

    override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        val db = database
        val match = URI_MATCHER.match(uri)
        when (match) {
            SIGHTING_DIR -> {
                db.beginTransaction()
                var returnCount = 0
                try {
                    for (value in values) {
                        val _id = db.insert(SightingDao.TABLENAME, null, value)
                        if (_id != -1L) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                context.contentResolver.notifyChange(uri, null)
                return returnCount
            }
            else -> return super.bulkInsert(uri, values)
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @TargetApi(11)
    override fun shutdown() {
        openHelper.close()
        super.shutdown()
    }


}
