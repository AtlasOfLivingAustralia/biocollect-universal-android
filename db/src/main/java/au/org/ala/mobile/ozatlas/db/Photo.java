package au.org.ala.mobile.ozatlas.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity
public class Photo {

    @Id
    private Long id;

    private boolean synced;

    private String path;

    private String licence;

    private String attribution;

    @NotNull
    private Long sightingId;

    @ToOne(joinProperty = "sightingId")
    private Sighting sighting;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 566311508)
    private transient PhotoDao myDao;

    @Generated(hash = 1662641913)
    public Photo(Long id, boolean synced, String path, String licence, String attribution,
            @NotNull Long sightingId) {
        this.id = id;
        this.synced = synced;
        this.path = path;
        this.licence = licence;
        this.attribution = attribution;
        this.sightingId = sightingId;
    }

    @Generated(hash = 1043664727)
    public Photo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getSynced() {
        return this.synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSightingId() {
        return this.sightingId;
    }

    public void setSightingId(Long sightingId) {
        this.sightingId = sightingId;
    }

    @Generated(hash = 1386675046)
    private transient Long sighting__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 67101214)
    public Sighting getSighting() {
        Long __key = this.sightingId;
        if (sighting__resolvedKey == null || !sighting__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SightingDao targetDao = daoSession.getSightingDao();
            Sighting sightingNew = targetDao.load(__key);
            synchronized (this) {
                sighting = sightingNew;
                sighting__resolvedKey = __key;
            }
        }
        return sighting;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2090491933)
    public void setSighting(@NotNull Sighting sighting) {
        if (sighting == null) {
            throw new DaoException(
                    "To-one property 'sightingId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.sighting = sighting;
            sightingId = sighting.getId();
            sighting__resolvedKey = sightingId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public String getLicence() {
        return this.licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getAttribution() {
        return this.attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 442052972)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPhotoDao() : null;
    }

}
