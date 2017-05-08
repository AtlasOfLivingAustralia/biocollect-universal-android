package au.csiro.ozatlas.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sad038 on 8/5/17.
 */

public class LoginResponse {
    @Expose
    @SerializedName("authKey")
    public String authKey;
    @Expose
    @SerializedName("userId")
    public String userId;
    @Expose
    @SerializedName("firstName")
    public String firstName;
    @Expose
    @SerializedName("lastName")
    public String lastName;
}
