package core.tfg.sketchsearch;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Comparison {
    private int nbins_r;
    private int nbins_theta;
    private Double r_inner;
    private Double r_outer;
    private int simpleto;

    public Comparison(int nbins_r, int nbins_theta, Double r_outer, Double r_inner, int simpleto) {
        this.nbins_r = nbins_r;
        this.nbins_theta = nbins_theta;
        this.r_inner = r_inner;
        this.r_outer = r_outer;
        this.simpleto = simpleto;
    }

    private static double distance(Point star, Point dest) {
        return Math.sqrt((star.y - dest.y) * (star.y - dest.y) + (star.x - dest.x) * (star.x - dest.x));
    }

    private static double angle(Point star, Point dest) {
        Double angle = 0.0;
        if(!(star.x == dest.x || star.y == dest.y))
        {
            angle = Math.atan2(Math.abs(dest.y - star.y), Math.abs(dest.x - star.x));
        }
        return angle;
    }

    private static ArrayList logarithmicSpace(double start, double end, int bins)
    {
        int count = bins - 1;

        ArrayList ArrList= new ArrayList();

        for(int i = 0; i <= count; i++)
        {
            double f = start * Math.pow( ((double)end)/start, ((double)i)/count);
            ArrList.add(f);
        }

        return ArrList;
    }

    private static int[][] reshape(int[][] A, int m, int n) {
        int origM = A.length;
        int origN = A[0].length;
        if(origM*origN != m*n){
            throw new IllegalArgumentException("New matrix must be of same area as matix A");
        }
        int[][] B = new int[m][n];
        int[] A1D = new int[A.length * A[0].length];

        int index = 0;
        for(int i = 0;i<A.length;i++){
            for(int j = 0;j<A[0].length;j++){
                A1D[index++] = A[i][j];
            }
        }

        index = 0;
        for(int i = 0;i<n;i++){
            for(int j = 0;j<m;j++){
                B[j][i] = A1D[index++];
            }

        }
        return B;
    }

    private static Point[] findContours(Mat image, int simpleto) {
        Mat dest = new Mat();

        Core.inRange(image, new Scalar(0,0,0), new Scalar(105,105,105), dest);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(dest, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
        Imgproc.drawContours(dest, contours, -1, new Scalar(255,255,0));

        int length = 0;
        if (contours.toArray().length > 1) {
            length = contours.get(0).toArray().length + contours.get(1).toArray().length;
        } else {
            length = contours.get(0).toArray().length;
        }

        Point[] result_full = new Point[length];
        int pos = 0;
        for (int i = 0; i < 2; i++) {
            System.arraycopy(contours.get(i).toArray(), 0, result_full, pos, contours.get(i).toArray().length);
            pos = pos + contours.get(i).toArray().length;
        }

        double step = (double)length / (double)simpleto;

        Point[] result = new Point[simpleto];
        pos = 0;
        for (double i = 0; pos < simpleto; i = i + step) {
            result[pos] = result_full[(int)Math.floor(i)];
            pos = pos + 1;
        }

        return result;
    }

    public static int[][] ShapeContext(Mat image, int nbins_r, int nbins_theta, int simpleto, Double r_inner, Double r_outer) {

        Point[] result = findContours(image, simpleto);

        Double[][] r_array = new Double[result.length][result.length];
        Double sum = 0.0;
        int count = 0;
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result.length; j++) {
                r_array[i][j] = distance(result[i], result[j]);
                sum = sum + distance(result[i], result[j]);
                count = count + 1;
            }
        }
        Double mean = sum / count;

        Double max = 0.0;
        int arg_max[] = new int[2];

        Double[][] r_array_n = new Double[result.length][result.length];
        for (int i = 0; i < r_array.length; i++) {
            for (int j = 0; j < r_array[i].length; j++) {
                if (r_array[i][j] > max) {
                    max = r_array[i][j];
                    arg_max[0] = i;
                    arg_max[1] = j;
                }
                r_array_n[i][j] = r_array[i][j] / mean;
            }
        }

        ArrayList logSpace;

        logSpace = logarithmicSpace(r_inner, r_outer, nbins_r);
        Double[] r_bin_edges = new Double[logSpace.size()];
        logSpace.toArray(r_bin_edges);

        int[][] r_array_q = new int[r_array_n.length][r_array_n[0].length];
        for (int i = 0; i < r_array_q.length; i++) {
            for (int j = 0; j < r_array_q.length; j++) {
                r_array_q[i][j] = 0;
            }
        }

        for (int i = 0; i < r_bin_edges.length; i++) {
            for (int j = 0; j < r_array_q.length; j++) {
                for (int r = 0; r < r_array_q[0].length; r++) {
                    if (r_array_n[j][r] < r_bin_edges[i]) {
                        r_array_q[j][r] = r_array_q[j][r] + 1;
                    }
                }
            }
        }

        Double[][] r_array_ang = new Double[result.length][result.length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result.length; j++) {
                r_array_ang[i][j] = angle(result[i], result[j]);
            }
        }
        Double norm_ang = r_array_ang[arg_max[0]][arg_max[1]];

        Double[][] theta_array = new Double[result.length][result.length];
        Double[][] theta_array_2 = new Double[result.length][result.length];
        int[][] theta_array_q = new int[result.length][result.length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result.length; j++) {
                if (j == i) {
                    theta_array[i][j] = 0.0;
                } else {
                    theta_array[i][j] = r_array_ang[i][j] - norm_ang;
                }
                if (Math.abs(theta_array[i][j]) < 1e-7) {
                    theta_array[i][j] = 0.0;
                }
                if (theta_array[i][j] < 0) {
                    theta_array_2[i][j] = theta_array[i][j] + (2*Math.PI);
                } else {
                    theta_array_2[i][j] = theta_array[i][j];
                }
                theta_array_q[i][j] = (int)(1 + Math.floor(theta_array_2[i][j] / (2*Math.PI /nbins_theta)));
            }
        }

        int nbins = nbins_theta * nbins_r;
        int[][] descriptor = new int[result.length][nbins];
        for (int i = 0; i < result.length; i++) {
            int[][] sn = new int[nbins_r][nbins_theta];
            for (int j = 0; j < result.length; j ++) {
                if (r_array_q[i][j] > 0) {
                    sn[r_array_q[i][j] - 1][theta_array_q[i][j] - 1] += 1;
                }
            }
            int[][] reshape = new int[1][nbins];
            reshape = reshape(sn, 1, nbins);
            for (int j = 0; j < nbins; j++) {
                descriptor[i][j] = reshape[0][j];
            }
            if (i == 1) {
            }
        }

        return descriptor;
    }

    public static Mat Canny (Mat image) {
        Mat gray = new Mat();
        Mat draw = new Mat();
        Mat wide = new Mat();

        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(gray, wide, 50, 150, 3, false);
        wide.convertTo(draw, CvType.CV_8U);

        Core.bitwise_not(draw, draw);


        return draw;
    }

    private static double[][] cost_by_paper(int[][] P, int[][]Q, int qlength, int nbins_r, int nbins_theta) {
        int p, p2, d;
        p = P.length;
        p2 = Q.length;
        d = p2;
        if (qlength > 0) {
            d = qlength;
        }
        double[][] C = new double[p][p2];
        for(int i = 0; i < p; i++) {
            for (int j = 0; j < p2; j++) {
                Double[] q = new Double[Q[j].length];
                Double[] w = new Double[P[i].length];
                for (int k = 0; k < Q[j].length; k++) {
                    q[k] = (double)Q[j][k] / (double)d;
                    w[k] = (double)P[i][k] / (double)p;
                }
                C[i][j] = cost(q, w, nbins_r, nbins_theta);
            }
        }
        return C;
    }

    private static Double cost(Double[] hi, Double[] hj, int nbins_r, int nbins_theta) {
        Double cost = 0.0;
        int nbins = nbins_r * nbins_theta;
        for (int i = 0; i < nbins; i++) {
            if (hi[i] + hj[i] != 0) {
                cost += (Math.pow((hi[i] - hj[i]),2) / (hi[i] + hj[i]));
            }
        }
        cost = cost * 0.5;
        return cost;
    }



    public static Double diff(int[][] P, int[][] Q, int qlength, int nbins_r, int nbins_theta) {
        Double result;
        double[][] C = cost_by_paper(P, Q, qlength, nbins_r, nbins_theta);

        int[] assigmentL = new HungarianAlgorithm(C).execute();

        Double total = 0.0;

        for (int i = 0; i < C.length; i++){
            total = total + C[i][assigmentL[i]];
        }

        return total;
    }

    public static int ORB(Mat sketch, Mat draw, int distance) {
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        //First photo

        Mat descriptors1 = new Mat();
        MatOfKeyPoint keyPoint1 = new MatOfKeyPoint();

        detector.detect(sketch, keyPoint1);
        descriptor.compute(sketch, keyPoint1, descriptors1);

        //Second photo

        Mat descriptors2 = new Mat();
        MatOfKeyPoint keyPoint2 = new MatOfKeyPoint();

        detector.detect(draw, keyPoint2);
        descriptor.compute(draw, keyPoint2, descriptors2);

        //Matching
        MatOfDMatch matches = new MatOfDMatch();
        MatOfDMatch filteredMatches = new MatOfDMatch();
        matcher.match(descriptors1, descriptors2, matches);

        //Linking
        Scalar RED = new Scalar(255,0,0);
        Scalar GREEN = new Scalar(0,255,0);

        List<DMatch> matchesList = matches.toList();
        Double max_dist = 0.0;
        Double min_dist = 100.0;

        for (int i = 0; i < matchesList.size(); i++) {
            Double dist = (double) matchesList.get(i).distance;
            if (dist < min_dist)
                min_dist = dist;
            if (dist > max_dist)
                max_dist = dist;
        }


        LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
        for (int i = 0; i < matchesList.size(); i++) {
            if (matchesList.get(i).distance <= distance)
                good_matches.addLast(matchesList.get(i));
        }
        System.out.println(good_matches.size());


        //Printing
        MatOfDMatch goodMatches = new MatOfDMatch();
        goodMatches.fromList(good_matches);

        Mat outputImg = new Mat();
        MatOfByte drawnMatches = new MatOfByte();
        Features2d.drawMatches(sketch, keyPoint1, draw, keyPoint2, goodMatches, outputImg, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);

        return goodMatches.toArray().length;
    }

    public static int window_ORB(Mat image, Mat sketch, int window_mov, int row, int col, int distance) {
        double rows = (double)image.rows()*((double)row / 100);
        System.out.println((int)rows);
        System.out.println(image.rows());
        double cols = (double)image.cols()*((double)col / 100);
        Mat window;
        int good_matches = 0;
        boolean row_exit = false;
        boolean col_exit = false;

        int count = 1;

        for (int j = 0; !row_exit; j = j + window_mov){
            if ((j + (int)rows) > image.rows()) {
                j = image.rows() - (int)rows;
                row_exit = true;
            }
            for (int i = 0; !col_exit; i = i + window_mov) {
                if ((i + (int)cols) > image.cols()) {
                    i = image.cols() - (int)cols;
                    col_exit = true;
                }
                window = image.submat(j, j + (int)rows, i, i + (int)cols);

                int res_ORB = ORB(sketch, window, distance);
                if (good_matches < res_ORB) {
                    good_matches = res_ORB;
                }
                count++;
            }
            col_exit = false;
        }
        return good_matches;
    }

    public static Double window_ShapeContext(Mat image, int window_mov, int row, int col, int nbins_r, int nbins_theta, int simpleto, Double r_inner, Double r_outer, int qlength, int [][] des2) {
        double rows = (double)image.rows()*((double)row / 100);
        System.out.println((int)rows);
        System.out.println(image.rows());
        double cols = (double)image.cols()*((double)col / 100);
        Mat window;
        Double best_distance = 1000.0;
        boolean row_exit = false;
        boolean col_exit = false;

        int count = 1;

        for (int j = 0; !row_exit; j = j + window_mov){
            if ((j + (int)rows) > image.rows()) {
                j = image.rows() - (int)rows;
                row_exit = true;
            }
            for (int i = 0; !col_exit; i = i + window_mov) {
                if ((i + (int)cols) > image.cols()) {
                    i = image.cols() - (int)cols;
                    col_exit = true;
                }
                window = image.submat(j, j + (int)rows, i, i + (int)cols);

                int [][] des1 = ShapeContext(window, nbins_r, nbins_theta, simpleto, r_inner, r_outer);
                Double res_ShapeContext = diff(des1, des2, qlength, nbins_r, nbins_theta);
                if (best_distance > res_ShapeContext) {
                    best_distance = res_ShapeContext;
                }
                count++;
            }
            col_exit = false;
        }
        return best_distance;
    }

    public static Double window_Hausdorff(Mat image, Mat sketch, int window_mov, int row, int col, int simpleto) {
        double rows = (double)image.rows()*((double)row / 100);
        System.out.println((int)rows);
        System.out.println(image.rows());
        double cols = (double)image.cols()*((double)col / 100);
        Mat window;
        Double best_distance = 10000000.0;
        boolean row_exit = false;
        boolean col_exit = false;

        int count = 1;

        for (int j = 0; !row_exit; j = j + window_mov){
            if ((j + (int)rows) > image.rows()) {
                j = image.rows() - (int)rows;
                row_exit = true;
            }
            for (int i = 0; !col_exit; i = i + window_mov) {
                if ((i + (int)cols) > image.cols()) {
                    i = image.cols() - (int)cols;
                    col_exit = true;
                }
                window = image.submat(j, j + (int)rows, i, i + (int)cols);

                double result = distanceHausdorff(window, sketch, simpleto);
                if (best_distance > result) {
                    best_distance = result;
                }
                count++;
            }
            col_exit = false;
        }
        return best_distance;
    }

    public static double distanceHausdorff(Mat image, Mat sketch, int simpleto) {

        Point[] routeOne = findContours(image, simpleto);
        Point[] routeTwo = findContours(sketch, simpleto);

        int maxDistAB = distanceCalc(routeOne, routeTwo);
        int maxDistBA = distanceCalc(routeTwo, routeOne);
        int maxDist = Math.max(maxDistAB, maxDistBA);

        return Math.sqrt((double)maxDist);
    }

    private static int distanceCalc(Point[] a, Point[] b)
    {
        int maxDistAB = 0;

        for (Point posicionOne : a){

            double minB = 1000000.0;

            for (Point posicionTwo : b){

                double dx = (posicionOne.x - posicionTwo.x);
                double dy = (posicionOne.y - posicionTwo.y);
                double tmpDist = dx*dx + dy*dy;

                if (tmpDist < minB){
                    minB = tmpDist;
                }

                if ( tmpDist == 0 ){
                    break; // can't be better than 0
                }
            }
            maxDistAB += minB;
        }
        return maxDistAB;
    }

}
