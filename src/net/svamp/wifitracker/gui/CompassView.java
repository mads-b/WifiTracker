package net.svamp.wifitracker.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import net.svamp.wifitracker.CardListener;
import net.svamp.wifitracker.core.LatLon;
import net.svamp.wifitracker.core.Point3D;
import net.svamp.wifitracker.core.WifiItem;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CompassView extends View{
    private int smallestRad =0;

    private int compassRad;
    private int fpsSetting;

    private Point3D center;
    private double angle;
    private LatLon lastLocation = new LatLon(0,0);
    private final Paint myPaint = new Paint();

    //True position of the wiFi APs found.
    private final Map<String,WifiItem> points = new HashMap<String, WifiItem>();
    //Points generated when calling recalculateRelativeApPositions().
    private final Map<String,Point3D> compassPoints = new HashMap<String, Point3D>();

    public CompassView(Context context) {
        super( context);
        init();
    }

    public CompassView(Context context, AttributeSet attrs) {
        super( context, attrs );
        init();
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super( context, attrs, defStyle );
        init();
    }

    private void init () {
        //Set scale of compass
        compassRad = PreferenceManager.getDefaultSharedPreferences(this.getContext()).getInt("compassRad",60);
        //Fetch FPS preference.
        fpsSetting = PreferenceManager.getDefaultSharedPreferences(this.getContext()).getInt("compassFPS",5);
        //Set some paint settings
        myPaint.setStrokeWidth(2);
        myPaint.setAntiAlias(true);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setTypeface(Typeface.MONOSPACE);
        myPaint.setTextSize(16);


        /*
         * Registers a handler for listening for new calculated AP positions.
         */

        Handler cvHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                try {
                    /* get values from message. */
                    //New Data about an AP's position!
                    if(msg.getData().getBoolean("newAPPointData")) {
                        //Fetch and deserialize json.
                        WifiItem ap = new WifiItem(new JSONObject(msg.getData().getString("wifiItemJson")));
                        //We have this AP on map already. Delete and re-add.
                        if(points.containsKey(ap.bssid)) {
                            points.remove(ap.bssid);
                        }
                        points.put(ap.bssid,ap);
                        recalculateRelativeApPositions();
                    }
                    else if(msg.getData().getBoolean("gpsAccurate")) {
                        lastLocation = new LatLon(
                                msg.getData().getDouble("curLatitude"),
                                msg.getData().getDouble("curLongitude"));
                        recalculateRelativeApPositions();
                    }
                } catch(JSONException e) { e.printStackTrace(); }
            }
        };
        CardListener.getInstance().addHandler(cvHandle);
    }

    /**
     * Uses the "points" field and the lastLocation field to calculate the relative locations of the
     * WiFi AP's in the vicinity.
     */
    private void recalculateRelativeApPositions() {
        for(WifiItem ap : points.values()) {
            LatLon apPos = ap.location;
            double distanceTo = LatLon.distanceBetween(lastLocation,apPos);
            //Subtract 90*, as the bearing is due north, and we need a due east one to translate to
            double bearingTo = Math.PI/2-LatLon.bearingBetween(lastLocation,apPos);
            //Translate from meters to pixels and from angle and distance to cartesian:
            Point3D apPoint = Point3D.getCylindrical(distanceTo*smallestRad/compassRad,bearingTo,0);
            //Move it from 0x0 to center.
            Point3D apPointCenter = new Point3D(apPoint.x+center.x,apPoint.y+center.y);
            compassPoints.put(ap.ssid,apPointCenter);
        }
    }


    @Override
    protected void onDraw(Canvas canvas){
        //View has evaluated its size. Set pixel values for center of screen and if height or width of screen dominates.
        if(center == null) {
            center=new Point3D(getLeft()+getWidth()/2,
                    getTop()+getHeight()/2);
            //Number of pixels from center to outer ring.
            smallestRad = Math.min(getWidth()/2-10, getHeight()/2-10);
        }

        try {
            //Sleep to synch FPS.
            Thread.sleep(1000/fpsSetting);
            if(CardListener.getInstance()!=null)
                angle=CardListener.getInstance().getCompassOrientation();

        } catch (InterruptedException e) { //Should never happen.
            e.printStackTrace();
        }

        myPaint.setColor(Color.GREEN);
        myPaint.setTextAlign(Align.CENTER);

        canvas.drawCircle((int)center.x, (int)center.y, smallestRad, myPaint);
        canvas.drawCircle((int)center.x, (int)center.y, smallestRad*2/3, myPaint);
        canvas.drawCircle((int)center.x, (int)center.y, smallestRad/3, myPaint);
        canvas.drawLine(translateX(center.x-smallestRad,center.y,angle),
                translateY(center.x-smallestRad,center.y,angle),
                translateX(center.x+smallestRad,center.y,angle),
                translateY(center.x+smallestRad,center.y,angle),
                myPaint);
        canvas.drawLine(translateX(center.x, center.y-smallestRad,angle),
                translateY(center.x,center.y-smallestRad,angle),
                translateX(center.x,center.y+smallestRad,angle),
                translateY(center.x,center.y+smallestRad,angle),
                myPaint);
        canvas.drawText((compassRad/3)+"m",
                translateX(center.x-smallestRad/3, center.y,angle),
                translateY(center.x-smallestRad/3, center.y,angle),
                myPaint);
        canvas.drawText((compassRad/3)+"m",
                translateX(center.x+smallestRad/3, center.y,angle),
                translateY(center.x+smallestRad/3, center.y,angle),
                myPaint);
        canvas.drawText((2*compassRad/3)+"m",
                translateX(center.x-2*smallestRad/3, center.y,angle),
                translateY(center.x-2*smallestRad/3, center.y,angle),
                myPaint);
        canvas.drawText((2*compassRad/3)+"m",
                translateX(center.x+2*smallestRad/3, center.y,angle),
                translateY(center.x+2*smallestRad/3, center.y,angle),
                myPaint);
        myPaint.setColor(Color.RED);
        canvas.drawText("N",
                translateX(center.x, center.y-smallestRad,angle),
                translateY(center.x, center.y-smallestRad,angle),
                myPaint);
        myPaint.setColor(Color.BLUE);
        canvas.drawText("S",
                translateX(center.x, center.y+smallestRad,angle),
                translateY(center.x, center.y+smallestRad,angle),
                myPaint);
        //Draw wifi AP's on compass!
        myPaint.setColor(Color.RED);
        myPaint.setTextAlign(Align.LEFT);
        int i=0;
        for(Map.Entry<String,Point3D> ap : compassPoints.entrySet()) {
            Point3D p = ap.getValue();
            String name = ap.getKey();
            canvas.drawCircle(translateX((int)p.x,(int)p.y,angle),
                    translateY((int)p.x,(int)p.y,angle),5,myPaint);
            canvas.drawText(name,
                    translateX((int)p.x,(int)p.y,angle)+6,
                    translateY((int)p.x,(int)p.y,angle),
                    myPaint);
            i++;
        }
        invalidate();
    }



    /*
      * @return translated coordinates for a rotated cartesian system. Origin in center.
      */
    private int translateX(double x,double y, double angle) {
        return (int)((x-center.x)*Math.cos(angle)-(y-center.y)*Math.sin(angle)+center.x);
    }
    private int translateY(double x,double y, double angle) {
        return (int)((x-center.x)*Math.sin(angle)+(y-center.y)*Math.cos(angle)+center.y);
    }
} 
