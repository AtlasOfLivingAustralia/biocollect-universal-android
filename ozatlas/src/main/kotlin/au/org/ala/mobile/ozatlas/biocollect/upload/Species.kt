package au.org.ala.mobile.ozatlas.biocollect.upload

data class Species @JvmOverloads constructor (
    var name: String? = null,
    var guid: String? = null,
    var outputSpeciesId: String? = null
)