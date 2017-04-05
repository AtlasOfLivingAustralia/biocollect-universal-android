package au.org.ala.mobile.ozatlas.biocollect.dto

import org.threeten.bp.Instant
import org.threeten.bp.LocalTime
import java.util.ArrayList

data class Data @JvmOverloads constructor (
    var comments: String? = null,
    var identificationConfidence: String? = null,
    var individualCount: String? = null,
    var locationAccuracy: Int? = null,
    var locationLatitude: Double? = null,
    var locationLocality: String? = null,
    var locationLongitude: Double? = null,
    var locationNotes: String? = null,
    var locationSource: String? = null,
    var notes: String? = null,
    var recordedBy: String? = null,
    var sightingPhoto: List<SightingPhoto> = ArrayList(),
    var species: Species? = null,
    var surveyDate: Instant? = null,
    var surveyStartTime: LocalTime? = null,
    var tags: List<String> = ArrayList()
)
