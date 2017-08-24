package au.csiro.ozatlas.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasDialogManager;
import au.csiro.ozatlas.manager.FileUtils;
import base.BaseMainActivityFragment;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sad038 on 18/4/17.
 */

/**
 * This class layout has a webview.
 */
public class WebViewFragment extends BaseMainActivityFragment {
    private final static int FCR = 1;
    private final static int PERMISSION_REQUEST = 2;
    private WebView webView;
    private String mCM;
    private String url;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private LocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        webView = (WebView) view.findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setGeolocationEnabled(true);

        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(0);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        //getting the URL
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString(getString(R.string.url_parameter));
            if (url != null) {
                boolean isChromeClientNeeded = bundle.getBoolean(getString(R.string.chrome_client_need_parameter), false);
                if (isChromeClientNeeded) {
                    if (checkPermissionForChromeClient()) {
                        setupWebViewClient();
                        if (setupChromeCleint())
                            webView.loadUrl(url, sharedPreferences.getHeaderMap());
                    }
                } else {
                    setupWebViewClient();
                    webView.loadUrl(url, sharedPreferences.getHeaderMap());
                }
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sendAnalyticsScreenName("WebView Fragment", TAG);
    }

    private boolean checkPermissionForChromeClient() {
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
            return false;
        }
        return true;
    }

    /**
     * look for hardware GPS location
     */
    private boolean lookForGPSLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                return true;
            else
                AtlasDialogManager.alertBoxForSetting(getActivity(), "Your Device's GPS or Network is Disable", "Location Provider Status", "Setting", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                        dialog.cancel();
                        getActivity().onBackPressed();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().onBackPressed();
                    }
                });
        }
        return false;
    }

    private boolean setupChromeCleint() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (lookForGPSLocation()) {
            webView.setWebChromeClient(new WebChromeClient() {
                public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                    callback.invoke(origin, true, false);
                }

                //For Android 3.0+
                public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                    mUM = uploadMsg;
                    /*Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image*//*");*/
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
                }

                // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
                public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                    mUM = uploadMsg;
                    /*Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image*//*");*/
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(
                            Intent.createChooser(i, "File Browser"),
                            FCR);
                }

                //For Android 4.1+
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                    mUM = uploadMsg;
                    /*Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image*//*");*/
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
                }

                //For Android 5.0+
                public boolean onShowFileChooser(
                        WebView webView, ValueCallback<Uri[]> filePathCallback,
                        WebChromeClient.FileChooserParams fileChooserParams) {
                    if (mUMA != null) {
                        mUMA.onReceiveValue(null);
                    }
                    mUMA = filePathCallback;
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = FileUtils.createImageFile(getActivity());
                            takePictureIntent.putExtra("PhotoPath", mCM);
                        } catch (IOException ex) {
                            Log.e("Error", "Image file creation failed", ex);
                        }
                        if (photoFile != null) {
                            mCM = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        } else {
                            takePictureIntent = null;
                        }
                    }

                    Intent contentSelectionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    /*Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("image*//*");*/
                    Intent[] intentArray;
                    if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else {
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, FCR);
                    return true;
                }
            });
        } else {
            return false;
        }
        return true;
    }

    private void setupWebViewClient() {
        webView.setWebViewClient(new WebViewClient() {
            private int running = 0;

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String urlNewString) {
                running++;
                webView.loadUrl(urlNewString);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showProgressDialog(true);
                running = Math.max(running, 1); // First request move it to 1.
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (--running == 0) {
                    hideProgressDialog();
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                hideProgressDialog();
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(description);
                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //Check if response is positive
            if (resultCode == RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    /**
     * Marshmellow permission
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupWebViewClient();
                    if (setupChromeCleint())
                        webView.loadUrl(url, sharedPreferences.getHeaderMap());
                } else {
                    showSnackBarMessage(getString(R.string.permission_denied));
                }
                break;
        }
    }
}
