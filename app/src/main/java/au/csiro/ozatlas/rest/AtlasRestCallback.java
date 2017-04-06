package au.csiro.ozatlas.rest;

import java.io.IOException;

import au.csiro.ozatlas.manager.AtlasManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class AtlasRestCallback<T> implements Callback<T> {
    private static final int INVALID_ACCESS_TOKEN = 401;
    private String UNKNOWN_ERROR_MSG = "Unknown Error. Please try again later.";
    private String INVALID_ACCESS_TOKEN_ERROR_MSG = "Invalid Access Token";

    public abstract void onFailure(RetrofitException retrofitException);

    public abstract void onResponse(Response<T> response);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.code() == INVALID_ACCESS_TOKEN) {
            AtlasManager.eventBus.post(RetrofitException.accessTokenError(new Exception()));
            return;
        }
        if (response.isSuccessful()) {
            onResponse(response);
        } else {
            onFailure(RetrofitException.serverResponseUnsuccessful(response));
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (!call.isCanceled()) {
            if (t instanceof IOException) {
                onFailure(RetrofitException.networkError((IOException) t));
            } else {
                onFailure(RetrofitException.unexpectedError(t));
            }
        }
    }
}
