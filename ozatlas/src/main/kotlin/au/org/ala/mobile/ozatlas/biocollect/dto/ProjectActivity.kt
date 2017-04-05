package au.org.ala.mobile.ozatlas.biocollect.dto

import java.util.ArrayList

data class ProjectActivity @JvmOverloads constructor (
    var commentsAllowed: Boolean? = null,
    var description: String? = null,
    var endDate: String? = null,
    var id: String? = null,
    var name: String? = null,
    var pActivityFormName: String? = null,
    var projectActivityId: String? = null,
    var projectId: String? = null,
    var publicAccess: Boolean? = null,
    var restrictRecordToSites: Boolean? = null,
    var sites: List<String> = ArrayList(),
    var startDate: String? = null,
    var status: String? = null,
    var visibility: Visibility? = null
)
