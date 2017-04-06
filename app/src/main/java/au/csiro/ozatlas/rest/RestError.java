package au.csiro.ozatlas.rest;


import com.google.gson.annotations.SerializedName;

public class RestError {
    @SerializedName("staOSSRestCallback.javatus")
    public String status;

    @SerializedName("error")
    public String error;
    @SerializedName("error_message")
    public String strMessage = "";
    @SerializedName("error_description")
    public String strDescription;
}
