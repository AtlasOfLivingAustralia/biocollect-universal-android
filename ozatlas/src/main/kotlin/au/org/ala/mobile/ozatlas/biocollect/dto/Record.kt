package au.org.ala.mobile.ozatlas.biocollect.dto

import au.org.ala.mobile.ozatlas.db.Sighting
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

data class Record @JvmOverloads constructor (
    var activityId: String? = null,
    var coordinateUncertaintyInMeters: Int? = null,
    var dateCreated: Instant? = null,
    var decimalLatitude: Double? = null,
    var decimalLongitude: Double? = null,
    var eventDate: String? = null,
    var generalizedDecimalLatitude: Double? = null,
    var generalizedDecimalLongitude: Double? = null,
    var id: String? = null,
    var individualCount: Int? = null,
    var lastUpdated: Instant? = null,
    var numberOfOrganisms: Int? = null,
    var occurrenceID: String? = null,
    var outputId: String? = null,
    var outputItemId: Int? = null,
    var outputSpeciesId: String? = null,
    var projectActivityId: String? = null,
    var projectId: String? = null,
    var status: String? = null,
    var userId: String? = null
) {
    fun asDbRecord(output: Output): Sighting =
        Sighting().apply {
            this.uuid = outputId
            this.accuracy = coordinateUncertaintyInMeters
            this.latitude = decimalLatitude
            this.longitude = decimalLongitude
            this.comments = output.data?.comments
            this.confident = output.data?.identificationConfidence != "Uncertain"
            this.date = output.data?.surveyDate?.atZone(ZoneId.systemDefault())?.toLocalDate() // TODO CHECK THIS
            this.time = output.data?.surveyStartTime
            this.individualCount = output.data?.individualCount
            this.localityMatch = output.data?.locationLocality
            this.locationNotes = output.data?.locationNotes
            this.notes = output.data?.notes
            this.recordedBy = output.data?.recordedBy
            this.source = output.data?.locationSource
            this.speciesName = output.data?.species?.name
            this.tags = output.data?.tags?.toSet()
            this.serverLastUpdated = lastUpdated
            this.updatedLocally = false
        }
}