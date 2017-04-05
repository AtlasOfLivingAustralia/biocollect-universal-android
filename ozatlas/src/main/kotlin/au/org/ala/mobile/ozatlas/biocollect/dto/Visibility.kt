package au.org.ala.mobile.ozatlas.biocollect.dto

data class Visibility @JvmOverloads constructor (
    var embargoForDays: Int? = null,
    var embargoOption: EmbargoOption? = null,
    var embargoUntil: String? = null
)