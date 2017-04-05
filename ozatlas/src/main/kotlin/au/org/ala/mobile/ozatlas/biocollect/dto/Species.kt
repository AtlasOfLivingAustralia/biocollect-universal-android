package au.org.ala.mobile.ozatlas.biocollect.dto

data class Species @JvmOverloads constructor (
    var commonName: String? = null,
    var guid: String? = null,
    var name: String? = null,
    var outputSpeciesId: String? = null,
    var scientificName: String? = null
)
