package au.org.ala.mobile.ozatlas.db;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import org.greenrobot.greendao.annotation.Generated;

@Entity(
        indexes = {
                @Index(value = "uuid", unique = true),
                @Index(value = "uuid, accountName", unique = true)
        }
)
public class Sighting {

    @Id private Long id;

    @NotNull private String accountName;

    @NotNull private String uuid;

    @Convert(converter = LocalDateConverter.class, columnType = String.class)
    @NotNull private LocalDate date;

    @Convert(converter = LocalTimeConverter.class, columnType = String.class)
    private LocalTime time;

    private String notes;

    private String recordedBy;

    private String speciesName;

    private Boolean confident;

    @Convert(converter = StringSetConverter.class, columnType =  String.class)
    private Set<String> tags;

    private String comments;

    private String individualCount;

    @NotNull private Double latitude;

    @NotNull private Double longitude;

    private Integer accuracy;

    private String source;

    private String localityMatch;

    private String locationNotes;

    private boolean updatedLocally;

    @Convert(converter = InstantConverter.class, columnType = Date.class)
    private Instant serverLastUpdated;

    public boolean fieldsEqual(Sighting that) {
        if (TextUtils.equals(this.uuid, that.uuid)) {
            return false;
        }
        if (!Objects.equals(this.date, that.date)) {
            return false;
        }
        if (!Objects.equals(this.time, that.time)) {
            return false;
        }
        if (!TextUtils.equals(this.notes, that.notes)) {
            return false;
        }
        if (!TextUtils.equals(this.recordedBy, that.recordedBy)) {
            return false;
        }
        if (!TextUtils.equals(this.speciesName, that.speciesName)) {
            return false;
        }
        if (!Objects.equals(this.confident, that.confident)) {
            return false;
        }
        if (!Objects.equals(this.tags, that.tags)) {
            return false;
        }
        if (!TextUtils.equals(this.comments, that.comments)) {
            return false;
        }
        if (!TextUtils.equals(this.individualCount, that.individualCount)) {
            return false;
        }
        if (!Objects.equals(this.latitude, that.latitude)) {
            return false;
        }
        if (!Objects.equals(this.longitude, that.longitude)) {
            return false;
        }
        if (!Objects.equals(this.accuracy, that.accuracy)) {
            return false;
        }
        if (!TextUtils.equals(this.source, that.source)) {
            return false;
        }
        if (!TextUtils.equals(this.localityMatch, that.localityMatch)) {
            return false;
        }
        if (!TextUtils.equals(this.locationNotes, that.locationNotes)) {
            return false;
        }
        return true;
    }

    public Sighting updateFrom(Sighting other) {
        this.uuid = other.uuid;
        this.accuracy = other.accuracy;
        this.comments = other.comments;
        this.confident = other.confident;
        this.date = other.date;
        this.individualCount = other.individualCount;
        this.latitude = other.latitude;
        this.localityMatch = other.localityMatch;
        this.locationNotes = other.locationNotes;
        this.longitude = other.longitude;
        this.notes = other.notes;
        this.recordedBy = other.recordedBy;
        this.serverLastUpdated = other.serverLastUpdated;

        return this;
    }

@Generated(hash = 1454564233)
public Sighting(Long id, @NotNull String accountName, @NotNull String uuid,
        @NotNull LocalDate date, LocalTime time, String notes,
        String recordedBy, String speciesName, Boolean confident,
        Set<String> tags, String comments, String individualCount,
        @NotNull Double latitude, @NotNull Double longitude, Integer accuracy,
        String source, String localityMatch, String locationNotes,
        boolean updatedLocally, Instant serverLastUpdated) {
    this.id = id;
    this.accountName = accountName;
    this.uuid = uuid;
    this.date = date;
    this.time = time;
    this.notes = notes;
    this.recordedBy = recordedBy;
    this.speciesName = speciesName;
    this.confident = confident;
    this.tags = tags;
    this.comments = comments;
    this.individualCount = individualCount;
    this.latitude = latitude;
    this.longitude = longitude;
    this.accuracy = accuracy;
    this.source = source;
    this.localityMatch = localityMatch;
    this.locationNotes = locationNotes;
    this.updatedLocally = updatedLocally;
    this.serverLastUpdated = serverLastUpdated;
}

@Generated(hash = 1556412311)
public Sighting() {
}

public Long getId() {
    return this.id;
}

public void setId(Long id) {
    this.id = id;
}

public String getUuid() {
    return this.uuid;
}

public void setUuid(String uuid) {
    this.uuid = uuid;
}

public LocalDate getDate() {
    return this.date;
}

public void setDate(LocalDate date) {
    this.date = date;
}

public LocalTime getTime() {
    return this.time;
}

public void setTime(LocalTime time) {
    this.time = time;
}

public String getNotes() {
    return this.notes;
}

public void setNotes(String notes) {
    this.notes = notes;
}

public String getRecordedBy() {
    return this.recordedBy;
}

public void setRecordedBy(String recordedBy) {
    this.recordedBy = recordedBy;
}

public String getSpeciesName() {
    return this.speciesName;
}

public void setSpeciesName(String speciesName) {
    this.speciesName = speciesName;
}

public Boolean getConfident() {
    return this.confident;
}

public void setConfident(Boolean confident) {
    this.confident = confident;
}

public Set<String> getTags() {
    return this.tags;
}

public void setTags(Set<String> tags) {
    this.tags = tags;
}

public String getComments() {
    return this.comments;
}

public void setComments(String comments) {
    this.comments = comments;
}

public String getIndividualCount() {
    return this.individualCount;
}

public void setIndividualCount(String individualCount) {
    this.individualCount = individualCount;
}

public Double getLatitude() {
    return this.latitude;
}

public void setLatitude(Double latitude) {
    this.latitude = latitude;
}

public Double getLongitude() {
    return this.longitude;
}

public void setLongitude(Double longitude) {
    this.longitude = longitude;
}

public Integer getAccuracy() {
    return this.accuracy;
}

public void setAccuracy(Integer accuracy) {
    this.accuracy = accuracy;
}

public String getSource() {
    return this.source;
}

public void setSource(String source) {
    this.source = source;
}

public String getLocalityMatch() {
    return this.localityMatch;
}

public void setLocalityMatch(String localityMatch) {
    this.localityMatch = localityMatch;
}

public String getLocationNotes() {
    return this.locationNotes;
}

public void setLocationNotes(String locationNotes) {
    this.locationNotes = locationNotes;
}

public boolean getUpdatedLocally() {
    return this.updatedLocally;
}

public void setUpdatedLocally(boolean updatedLocally) {
    this.updatedLocally = updatedLocally;
}

public Instant getServerLastUpdated() {
    return this.serverLastUpdated;
}

public void setServerLastUpdated(Instant serverLastUpdated) {
    this.serverLastUpdated = serverLastUpdated;
}

public String getAccountName() {
    return this.accountName;
}

public void setAccountName(String accountName) {
    this.accountName = accountName;
}

}
