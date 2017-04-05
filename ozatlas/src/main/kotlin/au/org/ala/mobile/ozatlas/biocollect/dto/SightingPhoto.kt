package au.org.ala.mobile.ozatlas.biocollect.dto

import org.threeten.bp.Instant

data class SightingPhoto @JvmOverloads constructor (
    var activityId: String? = null,
    var attribution: String? = null,
    var contentType: String? = null,
    var dateTaken: Instant? = null,
    var documentId: String? = null,
    var filename: String? = null,
    var filepath: String? = null,
    var filesize: Int? = null,
    var formattedSize: String? = null,
    var identifier: String? = null,
    var licence: String? = null,
    var name: String? = null,
    var notes: String? = null,
    var outputId: String? = null,
    var role: String? = null,
    var status: String? = null,
    var type: String? = null
)