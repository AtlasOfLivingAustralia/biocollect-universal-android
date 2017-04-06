package au.csiro.ozatlas.rest;

import android.util.Log;

import com.google.gson.Gson;
import java.io.IOException;
import retrofit2.Response;
import retrofit2.Retrofit;

// This is RetrofitError converted to Retrofit 2
public class RetrofitException extends RuntimeException {
    private static final String TAG = "RetrofitException";

    private final String url;
    private final Response response;
    private final Kind kind;
    private final Retrofit retrofit;
    private final String errorBody;

    private RetrofitException(String message, String url, Response response, Kind kind, Throwable exception, Retrofit retrofit, String errorBody) {
        super(message, exception);
        this.url = url;
        this.response = response;
        this.kind = kind;
        this.retrofit = retrofit;
        this.errorBody = errorBody;
    }

    public static RetrofitException accessTokenError(Exception exception) {
        Log.e(TAG, "Access Token is expired");
        return new RetrofitException(exception.getMessage(), null, null, Kind.HTTP, exception, null, null);
    }

    public static RetrofitException networkError(IOException exception) {
        Log.e(TAG, exception.getMessage());
        return new RetrofitException("Network Offline Message", null, null, Kind.NETWORK, exception, null, null);
    }

    public static RetrofitException unexpectedError(Throwable exception) {
        Log.e(TAG, exception.getMessage());
        return new RetrofitException(exception.getMessage(), null, null, Kind.UNEXPECTED, exception, null, null);
    }

    public static RetrofitException serverResponseUnsuccessful(Response response) {
       try {
            return new RetrofitException(response.message(), null, response, Kind.UNSUCCESSFUL_SERVER_RESPONSE, new Exception(), null, response.errorBody().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RetrofitException(response.message(), null, response, Kind.UNSUCCESSFUL_SERVER_RESPONSE, new Exception(), null, null);
    }

    /**
     * The request URL which produced the error.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Response object containing status code, headers, body, etc.
     */
    public Response getResponse() {
        return response;
    }

    /**
     * The event kind which triggered this error.
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * The Retrofit this request was executed on
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }

    @Override
    public String getMessage() {
        if (response != null) {
            try {
                RestError restError = getErrorBodyAs(RestError.class);
                if (restError != null && !restError.strDescription.equals("")) {
                    return restError.strDescription;
                } else {
                    return super.getMessage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.getMessage();
    }

    /**
     * HTTP response body converted to specified {@code type}. {@code null} if there is no
     * response.
     *
     * @throws IOException if unable to convert the body to the specified {@code type}.
     */
    /*public <T> T getErrorBodyAs(Class<T> type) throws IOException {
        if (response == null || response.errorBody() == null) {
            return null;
        }
        Converter<ResponseBody, T> converter = new OSSRestClient().getRetrofit().responseBodyConverter(type, new Annotation[0]);//(type, new Annotation[0]);
        return converter.convert(response.errorBody());
    }*/
    public <T> T getErrorBodyAs(Class<T> type) throws IOException {
        if (errorBody != null) {
            return new Gson().fromJson(errorBody, type);
        } else {
            return null;
        }
    }

    /**
     * Identifies the event kind which triggered a {@link RetrofitException}.
     */
    public enum Kind {
        /**
         * An {@link IOException} occurred while communicating to the server.
         */
        NETWORK,
        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED,

        UNSUCCESSFUL_SERVER_RESPONSE
    }
}