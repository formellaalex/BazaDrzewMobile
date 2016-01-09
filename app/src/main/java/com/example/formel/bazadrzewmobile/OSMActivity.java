package com.example.formel.bazadrzewmobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.Arrays;

/**
 *
 * @author Nicolas Gramlich
 *
 */
public class OSMActivity extends Activity {

    private MapView mapView;

    private IMapController mapController;

    Location location;

    Projection proj;
    IGeoPoint tappedLocation;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final RelativeLayout rl = new RelativeLayout(this);

        this.mapView = new MapView(this);
        proj = mapView.getProjection();
        location = new Location("");
        rl.addView(this.mapView, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));

		/* ZoomControls */
        {
			/* Create a ImageView with a zoomIn-Icon. */
            final ImageView ivZoomIn = new ImageView(this);
            ivZoomIn.setImageResource(R.drawable.zoom_in);
			/* Create RelativeLayoutParams, that position in in the top right corner. */
            final RelativeLayout.LayoutParams zoominParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            zoominParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            zoominParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rl.addView(ivZoomIn, zoominParams);

            ivZoomIn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    OSMActivity.this.mapView.getController().zoomIn();
                }
            });

			/* Create a ImageView with a zoomOut-Icon. */
            final ImageView ivZoomOut = new ImageView(this);
            ivZoomOut.setImageResource(R.drawable.zoom_out);

			/* Create RelativeLayoutParams, that position in in the top left corner. */
            final RelativeLayout.LayoutParams zoomoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            zoomoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            zoomoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rl.addView(ivZoomOut, zoomoutParams);

            ivZoomOut.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    OSMActivity.this.mapView.getController().zoomOut();
                }
            });
        }

		/* MiniMap */
        {
            MinimapOverlay miniMapOverlay = new MinimapOverlay(this,
                    mapView.getTileRequestCompleteHandler());
            this.mapView.getOverlays().add(miniMapOverlay);
        }

        this.setContentView(rl);

        mapController = mapView.getController();
        mapController.setZoom(8);
        GeoPoint startPoint = new GeoPoint(getIntent().getDoubleExtra("LAT", 53.0),
                getIntent().getDoubleExtra("LON", 19.0));
        mapController.setCenter(startPoint);
        addMarker(true, getIntent().getDoubleExtra("LAT", 53.0), getIntent().getDoubleExtra("LON", 19.0));
        location.setLatitude(getIntent().getDoubleExtra("LAT", 53.0));
        location.setLongitude(getIntent().getDoubleExtra("LON", 19.0));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                int x = (int)event.getX();
                int y = (int)event.getY();
                addMarker(false,x,y);
                break;
        }
        return super.onTouchEvent(event);
    }

    private IGeoPoint geoPointFromScreenCoords(boolean ifStart, double x, double y, MapView vw){
        if (x < 0 || y < 0 ){
            return null; // coord out of bounds
        }
        // Get the top left GeoPoint
        Projection projection = vw.getProjection();
        GeoPoint geoPointTopLeft = (GeoPoint) projection.fromPixels(0, 0);
        Point topLeftPoint = new Point();
        // Get the top left Point (includes osmdroid offsets)
        projection.toPixels(geoPointTopLeft, topLeftPoint);
        // get the GeoPoint of any point on screen
        if(ifStart){
            return new GeoPoint(x,y);
        }
        GeoPoint rtnGeoPoint = (GeoPoint) projection.fromPixels((int)x, (int)y);
        return rtnGeoPoint;
    }

    private void addMarker(boolean ifStart, double lat, double lon){
        IGeoPoint loc = geoPointFromScreenCoords(ifStart,lat, lon, mapView);

        OverlayItem overlayItem = new OverlayItem("Poland", "Poland", loc);
        ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay
                = new ItemizedIconOverlay<OverlayItem>(
                this, Arrays.asList(overlayItem), null);
        mapView.getOverlays().clear();
        mapView.getOverlays().add(anotherItemizedIconOverlay);
        mapView.invalidate();
    }

    @Override
    public void finish() {
        mapView.getTileProvider().clearTileCache();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("LAT", location.getLatitude());
        returnIntent.putExtra("LON", location.getLongitude());
        setResult(Activity.RESULT_OK, returnIntent);
        super.finish();
    }





}