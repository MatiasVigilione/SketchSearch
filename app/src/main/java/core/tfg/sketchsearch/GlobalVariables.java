package core.tfg.sketchsearch;

import android.app.Application;

public class GlobalVariables extends Application {

    private String kind = "ORB";

    public int getORB_distance() {
        return ORB_distance;
    }

    public void setORB_distance(int ORB_distance) {
        this.ORB_distance = ORB_distance;
    }

    private int ORB_distance = 50;

    public int getNbins_r() {
        return nbins_r;
    }

    public void setNbins_r(int nbins_r) {
        this.nbins_r = nbins_r;
    }

    private int nbins_r = 5;
    private int nbins_theta = 12;

    public int getNbins_theta() {
        return nbins_theta;
    }

    public Double getR_innter() {
        return r_innter;
    }

    public Double getR_outter() {
        return r_outter;
    }

    public int getWindow_height() {
        return window_height;
    }

    public int getWindow_width() {
        return window_width;
    }

    public void setWindow_width(int window_width) {
        this.window_width = window_width;
    }

    public void setWindow_height(int window_height) {
        this.window_height = window_height;
    }

    public int getQlength() {
        return qlength;
    }

    public int getWindow_mov() {
        return window_mov;
    }

    public void setWindow_mov(int window_mov) {
        this.window_mov = window_mov;
    }

    public boolean isWindow_state() {
        return window_state;
    }

    public void setWindow_state(boolean window_state) {
        this.window_state = window_state;
    }

    public void setQlength(int qlength) {
        this.qlength = qlength;
    }

    public void setR_outter(Double r_outter) {
        this.r_outter = r_outter;
    }

    public void setR_innter(Double r_innter) {
        this.r_innter = r_innter;
    }

    public void setNbins_theta(int nbins_theta) {
        this.nbins_theta = nbins_theta;
    }

    public int getSimpleto_shapeContext() {
        return simpleto_shapeContext;
    }

    public void setSimpleto_shapeContext(int simpleto_shapeContext) {
        this.simpleto_shapeContext = simpleto_shapeContext;
    }

    private int simpleto_shapeContext = 100;
    private Double r_innter = 0.1250;
    private Double r_outter = 2.0;
    private int qlength = 0;

    private boolean window_state = false;

    private int window_mov = 50;
    private int window_height = 50;
    private int window_width = 50;

    public int getSimpleto_hausdorff() {
        return simpleto_hausdorff;
    }

    public void setSimpleto_hausdorff(int simpleto_hausdorff) {
        this.simpleto_hausdorff = simpleto_hausdorff;
    }

    private int simpleto_hausdorff = 100;


    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }





}
