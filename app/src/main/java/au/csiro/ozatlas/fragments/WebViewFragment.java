package au.csiro.ozatlas.fragments;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.base.BaseFragment;

/**
 * Created by sad038 on 18/4/17.
 */

/**
 * This class layout has a webview.
 */
public class WebViewFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        WebView webView = (WebView) view.findViewById(R.id.webView);
        //getting the URL
        Bundle bundle = getArguments();
        if (bundle != null) {
            String url = bundle.getString(getString(R.string.url_parameter));
            if (url != null) {
                //showing the content from the given URL
                webView.setWebViewClient(new WebViewClient() {
                    public void onPageFinished(WebView view, String url) {
                        hideProgressDialog();
                    }

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        hideProgressDialog();
                        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage(description);
                        alertDialog.setButton(0, "OK", new DialogInterface.OnClickListener() {
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
                showProgressDialog();
                webView.loadUrl(url);
            }
        }
        return view;
    }
}
