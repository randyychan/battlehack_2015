package demo.la.battlehack.com.image;

import android.os.Handler;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import demo.la.battlehack.com.helpers.Constants;
import demo.la.battlehack.com.helpers.ImageSaver;

public class ColorBlobDetector {

    private static final double UPPER_BOUNDS = 0.8;
    private static final double LOWER_BOUND_CLIP = Constants.LOWER_THRESHOLD;

    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(25,50,50,0);
    private MatOfPoint2f  matOfPoint2f;
            // Cache
    private Mat mPyrDownMat = new Mat();
    private Mat mHierarchy = new Mat();
    private List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    private List<MatOfPoint> contourz = new ArrayList<MatOfPoint>();
    private Handler handler = new Handler();
    private double rawX = 0;
    private double rawY = 0;
    private float rawRadius = 0;
    private int rawWidth = 0;
    private int rawHeight = 0;
    private boolean isTooClose = false;
    private double normalizedX = 0;
    private double normalizedY = 0;
    private boolean process = false;
    private UpdateCallback updateCallback;

    private static Scalar lowerGreen;
    private static Scalar upperGreen;

    static {
        lowerGreen = new Scalar(0);
        lowerGreen.val[0] = 102;
        lowerGreen.val[1] = 147;
        lowerGreen.val[2] = 178;
        lowerGreen.val[3] = 0;

        upperGreen = new Scalar(0);
        upperGreen.val[0] = 150;
        upperGreen.val[1] = 247;
        upperGreen.val[2] = 278;
        upperGreen.val[3] = 255;
    }

    public ColorBlobDetector(UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }


    public void setHsvColor(Scalar hsvColor) {
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;
        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH-minH; j++) {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                process = true;
                handler.postDelayed(this, 3000);
            }
        }, 3000);

    }

    public void processFilterColor(Mat rgbaImage) {
        if (process) {
            process = false;
            Mat copy = rgbaImage.clone();
            ImageSaver.INSTANCE.addImage(copy);
        }
        Imgproc.cvtColor(rgbaImage, rgbaImage, Imgproc.COLOR_RGB2HSV_FULL);
//        Core.inRange(rgbaImage, lowerGreen, upperGreen, rgbaImage);
        Core.inRange(rgbaImage, mLowerBound, mUpperBound, rgbaImage);
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);
        contours.clear();
        Imgproc.findContours(mPyrDownMat, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        Iterator<MatOfPoint> each = contours.iterator();

        MatOfPoint drawThisContour = null;
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (drawThisContour == null) {
                drawThisContour = contour;
            }
            else if (Imgproc.contourArea(contour) > Imgproc.contourArea(drawThisContour)) {
                drawThisContour = contour;
            }
        }

        if (drawThisContour == null)
            return;


        Core.multiply(drawThisContour, new Scalar(8, 8), drawThisContour);

        Point point = new Point();
        float[] radius = new float[1];
        matOfPoint2f = new MatOfPoint2f(drawThisContour.toArray());
        Imgproc.minEnclosingCircle(matOfPoint2f, point, radius);

        rawWidth = rgbaImage.width();
        rawHeight = rgbaImage.height();
        rawX = point.x;
        rawY = point.y;
        rawRadius = radius[0];

        normalize();

        Scalar CONTOUR_COLOR = new Scalar(255,0,0,255);
        contourz.clear();
        contourz.add(drawThisContour);
        Imgproc.drawContours(rgbaImage, contourz, -1, CONTOUR_COLOR);


        Point pt = new Point();
        pt.x = 50;
        pt.y = 200;

        if (normalizedX > 0) {
            Core.putText(rgbaImage, "right: " + String.format( "%.2f", normalizedX ), pt, Core.FONT_HERSHEY_DUPLEX, 4f, CONTOUR_COLOR);
            pt.y = 300;
            Core.putText(rgbaImage, "radius: " + String.format( "%.2f", radius[0]), pt, Core.FONT_HERSHEY_DUPLEX, 4f, CONTOUR_COLOR);
        } else if (normalizedX < 0){
            Core.putText(rgbaImage, "left: " + String.format( "%.2f", normalizedX ), pt, Core.FONT_HERSHEY_DUPLEX, 4f, CONTOUR_COLOR);
            pt.y = 300;
            Core.putText(rgbaImage, "radius: " + String.format( "%.2f", radius[0]), pt, Core.FONT_HERSHEY_DUPLEX, 4f, CONTOUR_COLOR);
        } else {
            Core.putText(rgbaImage, "centered", pt, Core.FONT_HERSHEY_DUPLEX, 4f, CONTOUR_COLOR);
            pt.y = 300;
            Core.putText(rgbaImage, "radius: " + String.format( "%.2f", radius[0]), pt, Core.FONT_HERSHEY_DUPLEX, 4f, CONTOUR_COLOR);
        }
        mPyrDownMat.release();
    }


    //normalized to -1 and 1
    private void normalizeValues() {
        double centerX = rawWidth/2;
        double centerY = rawHeight/2;

        double relativeToCenterX = rawX - centerX;
        double relativeToCenterY = rawY - centerY;

        //normalize between -1 and 1
        normalizedX = relativeToCenterX/centerX;
        normalizedY = relativeToCenterY/centerY;

    }

    // lowers to 0.8
    private void lowerBoundThreshold() {
        normalizedX /= UPPER_BOUNDS;
        normalizedY /= UPPER_BOUNDS;
    }

    //
    private void clipLowerBounds() {
        if (Math.abs(normalizedX) < LOWER_BOUND_CLIP) {
            normalizedX = 0;
        }

        if (Math.abs(normalizedY) < LOWER_BOUND_CLIP) {
            normalizedY = 0;
        }
    }

    private void normalize() {
        normalizeValues();
        lowerBoundThreshold();
        clipLowerBounds();

        if (updateCallback != null) {

            if (rawRadius > 300 && !isTooClose) {
                isTooClose = true;
                updateCallback.onRangeUpdate(0, 0);

                updateCallback.distanceClose(true);
            } else if (rawRadius <= 300 && isTooClose) {


                isTooClose = false;
                updateCallback.distanceClose(false);

                updateCallback.onRangeUpdate(normalizedX, normalizedY);

            } else {
                updateCallback.onRangeUpdate(normalizedX, normalizedY);
            }
        }
    }

    public double getNormalizedY() {
        return normalizedY;
    }

    public double getNormalizedX() {
        return normalizedX;
    }

    public interface UpdateCallback {
        void onRangeUpdate(double rangeX, double rangeY);
        void distanceClose(boolean isTooClose);
    }



}
