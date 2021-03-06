import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used in identifying the contours and cropping them into rectangles.
 */
public class RectangleDetection {

    static ImageUtils imageUtils;
    static int CONTOUR_THRESHOLD=10;

    public RectangleDetection(){
        imageUtils = new ImageUtils();
    }

    public MatOfPoint detectRectangle(Mat mRgba,Mat graphImage) {

        //convert the image to black and white does (8 bit)


        //imageUtils.displayImage(graphImage);

        //find the contours
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(graphImage, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);


        //get Square Contours
        List<MatOfPoint> squareContours = getSquareContours(contours);

        // Filter contours by area and resize to fit the original image size
        List<MatOfPoint> mContours = new ArrayList<>();

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea) {
                maxArea = area;
            }
        }

        MatOfPoint borderContour = null;
       // Find the Border contour and draw it on the image
        if(squareContours!=null) {
            each = squareContours.iterator();
            int idx = 0;
            double secondMax = 0;

            while (each.hasNext()) {
                MatOfPoint contour = each.next();
                double area = Imgproc.contourArea(contour);
                if (area > secondMax && Imgproc.contourArea(contour) < maxArea) {
                    mContours.add(contour);
                    secondMax = area;
                    borderContour = contour;
                    idx++;
                }
            }
            if(secondMax<10000){
                borderContour=null;
            }
        }

        return borderContour;
    }


    /**
     * Checks whether the contour formed is a square or not
     * @param thisContour
     * @return
     */
    public boolean isContourSquare(MatOfPoint thisContour) {

        Rect ret = null;

        MatOfPoint2f thisContour2f = new MatOfPoint2f();
        MatOfPoint approxContour = new MatOfPoint();
        MatOfPoint2f approxContour2f = new MatOfPoint2f();

        thisContour.convertTo(thisContour2f, CvType.CV_32FC2);

        Imgproc.approxPolyDP(thisContour2f, approxContour2f, 25, true);

        approxContour2f.convertTo(approxContour, CvType.CV_32S);

        if (approxContour.size().height == 4) {
            ret = Imgproc.boundingRect(approxContour);
        }

        return (ret != null);
    }

    /**
     * Returns a list of all the square contours
     * @param contours
     * @return
     */
    public List<MatOfPoint> getSquareContours(List<MatOfPoint> contours) {
        List<MatOfPoint> squares = null;

        for (MatOfPoint c : contours) {

            if ((isContourSquare(c)) && Imgproc.contourArea(c)>10000) {

                if (squares == null)
                    squares = new ArrayList<MatOfPoint>();
                squares.add(c);
            }
        }

        return squares;
    }

//    public List<MatOfPoint> getSmallCountours(List<MatOfPoint> contours) {
//        List<MatOf>
//    }
}
