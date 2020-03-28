package ca.sfu.cmpt276projectaluminium.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.lang.ref.WeakReference;

import ca.sfu.cmpt276projectaluminium.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final static String TAG = "CustomInfoWindowAdapter";
    private final View mWindow;
    private WeakReference<Context> mContext;

    public CustomInfoWindowAdapter(Context context) {
        this.mContext = new WeakReference<>(context);
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void rendoWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);

        if(!title.equals("")) {
            tvTitle.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.snippet);

        if(!title.equals("")) {
            tvSnippet.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendoWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendoWindowText(marker, mWindow);
        return mWindow;
    }
}
