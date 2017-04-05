package au.org.ala.mobile.ozatlas.biocollect.dto

import org.threeten.bp.Instant

data class Output @JvmOverloads constructor (
        var activityId: String? = null,
        var data: Data? = null,
        var dateCreated: Instant? = null,
        var id: String? = null,
        var lastUpdated: Instant? = null,
        var name: String? = null,
        var outputId: String? = null,
        var outputNotCompleted: Boolean? = null,
        var status: String? = null
)
