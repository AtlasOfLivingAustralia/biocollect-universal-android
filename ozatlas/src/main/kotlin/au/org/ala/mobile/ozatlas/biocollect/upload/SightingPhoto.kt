package au.org.ala.mobile.ozatlas.biocollect.upload

import org.threeten.bp.Instant

data class SightingPhoto @JvmOverloads constructor (
    var attribution: String? = null,
    var contentType: String? = null,
    var dateTaken: Instant? = null,
    var documentId: String? = null,
    var filename: String? = null,
    var filesize: Int? = null,
    var formattedSize: String? = null,
    var licence: String? = null,
    var name: String? = null,
    var notes: String? = null,
    var staged: Boolean? = null,
    var status: String? = null,
    var thumbnailUrl: String? = null,
    var url: String? = null
)