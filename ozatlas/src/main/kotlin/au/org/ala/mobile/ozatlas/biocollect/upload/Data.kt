package au.org.ala.mobile.ozatlas.biocollect.upload

import org.threeten.bp.Instant
import org.threeten.bp.LocalTime
import java.util.ArrayList

data class Data @JvmOverloads constructor (
//    var altitude: String? = null,
//    var course: String? = null,
//    var habitatDescription: String? = null,
//    var horizontalAccuracy: String? = null,
//    var locationLatitude: Double? = null,
//    var locationLongitude: Double? = null,
//    var notes: String? = null,
//    var numberOfYoung: String? = null,
//    var recordedBy: String? = null,
//    var sightingPhoto: List<SightingPhoto> = ArrayList(),
//    var species: Species? = null,
//    var speed: String? = null,
//    var surveyDate: String? = null,
//    var surveyStartTime: String? = null,
//    var treeHealth: String? = null,
//    var verticalAccuracy: String? = null
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
