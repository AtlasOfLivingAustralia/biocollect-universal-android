package au.org.ala.mobile.ozatlas.biocollect.upload

data class Output @JvmOverloads constructor (
    var name: String? = null,
    var outputId: String? = null,
    var data: Data? = null,
    var outputNotCompleted: Boolean? = null
)
