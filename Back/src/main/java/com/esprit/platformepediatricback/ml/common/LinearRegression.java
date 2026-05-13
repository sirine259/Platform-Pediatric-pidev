package com.esprit.platformepediatricback.ml.common;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class LinearRegression {

    private final SimpleRegression regression;
    private boolean trained = false;

    public LinearRegression() {
        this.regression = new SimpleRegression(true);
    }

    public void fit(double[][] x, double[] y) {
        regression.clear();
        for (int i = 0; i < x.length; i++) {
            regression.addData(x[i][0], y[i]);
        }
        trained = true;
    }

    public double predict(double[] x) {
        if (!trained) return 0;
        return regression.predict(x[0]);
    }

    public double getRSquared() {
        return regression.getRSquare();
    }

    public double getSlope() {
        return regression.getSlope();
    }

    public double getIntercept() {
        return regression.getIntercept();
    }

    public double getSignificance() {
        return regression.getSignificance();
    }

    public boolean isTrained() {
        return trained;
    }

    public double[][] prepareUnivariate(double[] values) {
        double[][] x = new double[values.length][1];
        for (int i = 0; i < values.length; i++) {
            x[i][0] = values[i];
        }
        return x;
    }
}
