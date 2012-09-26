package net.svamp.wifitracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import net.svamp.wifitracker.core.Point3D;

import java.util.ArrayList;

public class CompassView extends View{
    private int smallestRad =0;
    private Point3D center;
    private double angle;

    private ArrayList<Point3D> points = new ArrayList<Point3D>();
    private ArrayList<String> pointNames = new ArrayList<String>();
    public CompassView(Context context) {
        super( context);
    }

    public CompassView(Context context, AttributeSet attrs) {

        super( context, attrs );
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {

        super( context, attrs, defStyle );
    }


    public void addPoint(String name,double dist, double bearing) {
        //Let's calculate distance in pixels: smallestRad is 60m.
        int r = (int)(dist*smallestRad/60);
        Point3D p = Point3D.getCylindrical(r, bearing, 0);
        p.x+=center.x;
        p.y+=center.y;
        points.add(p);
        pointNames.add(name);
    }


    @Override
    protected void onDraw(Canvas canvas){
        /*
           * The following is run only one time. At view start
           */
        if(center==null) {
            center=new Point3D(getLeft()+getWidth()/2,
                    getTop()+getHeight()/2);
            smallestRad = Math.min(getWidth()/2-10, getHeight()/2-10);

            Handler cvHandle = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    /* get values from message. */

                    //New Data about an AP's position!
                    if(msg.getData().get("newAPPointData")!=null) {
                        String apName = msg.getData().getString("apName");
                        double apDist = msg.getData().getDouble("apDistance");
                        double apBearing = msg.getData().getDouble("apBearing");
                        //We have this AP on map already. Delete and re-add.
                        if(pointNames.contains(apName)) {
                            int i = pointNames.indexOf(apName);
                            pointNames.remove(i);
                            points.remove(i);
                        }
                        addPoint(apName,apDist,apBearing);
                    }
                }
            };
            CardListener.getInstance().addHandler(cvHandle);
        }

        try {
            Thread.sleep(50);
            if(CardListener.getInstance()!=null)
                angle=CardListener.getInstance().getCompassOrientation();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Paint myPaint = new Paint();
        myPaint.setStrokeWidth(2);
        myPaint.setAntiAlias(true);
        myPaint.setStyle(Style.STROKE);
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
        canvas.drawText("20m",
                translateX(center.x-smallestRad/3, center.y,angle),
                translateY(center.x-smallestRad/3, center.y,angle),
                myPaint);
        canvas.drawText("20m",
                translateX(center.x+smallestRad/3, center.y,angle),
                translateY(center.x+smallestRad/3, center.y,angle),
                myPaint);
        canvas.drawText("40m",
                translateX(center.x-2*smallestRad/3, center.y,angle),
                translateY(center.x-2*smallestRad/3, center.y,angle),
                myPaint);
        canvas.drawText("40m",
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
        for(Point3D p : points) {
            canvas.drawCircle(translateX((int)p.x,(int)p.y,angle),
                    translateY((int)p.x,(int)p.y,angle),5,myPaint);
            canvas.drawText(pointNames.get(i),
                    translateX((int)p.x,(int)p.y,angle)+6,
                    translateY((int)p.x,(int)p.y,angle),
                    myPaint);
            i++;
        }
        invalidate();
    }



    /*
      * @return translated coordinates for a rotated cartesian system.
      */
    private int translateX(double x,double y, double angle) {
        return (int)((x-center.x)*Math.cos(angle)-(y-center.y)*Math.sin(angle)+center.x);
    }
    private int translateY(double x,double y, double angle) {
        return (int)((x-center.x)*Math.sin(angle)+(y-center.y)*Math.cos(angle)+center.y);
    }
} 
