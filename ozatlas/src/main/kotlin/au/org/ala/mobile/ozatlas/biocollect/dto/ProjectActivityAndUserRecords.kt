package au.org.ala.mobile.ozatlas.biocollect.dto

import java.util.ArrayList

data class ProjectActivityAndUserRecords @JvmOverloads constructor (
    var lastUpdate: Long? = null,
    var projectActivity: ProjectActivity? = null,
    var records: List<Record> = ArrayList(),
    var outputs: List<Output> = ArrayList()
)
