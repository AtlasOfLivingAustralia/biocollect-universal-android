package au.csiro.ozatlas.model;

import java.util.List;

import au.csiro.ozatlas.model.SightingPhoto;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by sad038 on 24/4/17.
 */

public class AddSight extends RealmObject {
    public String mainTheme;

    public String activityId;

    public String siteId;

    public String projectId;

    public RealmList<Outputs> outputs;

    public String type;

    public String projectStage;
}




