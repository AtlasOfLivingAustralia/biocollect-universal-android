package au.csiro.ozatlas.manager;

/**
 * Created by sad038 on 8/11/17.
 */

public interface AtlasCallback<T> {
    void onSuccess(T t);

    void onFailure(T t);
}
