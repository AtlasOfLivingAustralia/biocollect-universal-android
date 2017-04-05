package au.org.ala.mobile.ozatlas.biocollect.upload

import java.util.ArrayList

data class Upload @JvmOverloads constructor (
    var activityId: String? = null,
    var projectStage: String? = null,
    var mainTheme: String? = null,
    var type: String? = null,
    var projectId: String? = null,
    var siteId: String? = null,
    var outputs: List<Output> = ArrayList()
)