package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class App {

    static class Metrics {
        String fileName;
        double bce;
        int tp, tn, fp, fn;
        double accuracy, precision, recall, f1, auc;

        Metrics(String fileName, double bce, int tp, int tn, int fp, int fn,
                double accuracy, double precision, double recall, double f1, double auc) {
            this.fileName = fileName;
            this.bce = bce;
            this.tp = tp;
            this.tn = tn;
            this.fp = fp;
            this.fn = fn;
            this.accuracy = accuracy;
            this.precision = precision;
            this.recall = recall;
            this.f1 = f1;
            this.auc = auc;
        }
    }

    public static void main(String[] args) {
        Metrics m1 = evaluateModel("model_1.csv");
        Metrics m2 = evaluateModel("model_2.csv");
        Metrics m3 = evaluateModel("model_3.csv");

        printBestBCE(m1, m2, m3);
        printBestAccuracy(m1, m2, m3);
        printBestPrecision(m1, m2, m3);
        printBestRecall(m1, m2, m3);
        printBestF1(m1, m2, m3);
        printBestAUC(m1, m2, m3);
    }

    public static Metrics evaluateModel(String fileName) {
        List<Integer> trueValues = new ArrayList<>();
        List<Double> predictedValues = new ArrayList<>();
        double epsilon = 1e-15;

        try {
            FileReader filereader = new FileReader(fileName);
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;

            csvReader.readNext(); // skip header

            while ((nextRecord = csvReader.readNext()) != null) {
                int trueValue = Integer.parseInt(nextRecord[0]);
                double predictedValue = Double.parseDouble(nextRecord[1]);

                trueValues.add(trueValue);
                predictedValues.add(predictedValue);
            }

            csvReader.close();
        } catch (Exception e) {
            System.out.println("Error reading file: " + fileName);
            e.printStackTrace();
            return null;
        }

        int n = trueValues.size();
        double bce = 0.0;
        int tp = 0, tn = 0, fp = 0, fn = 0;
        int nPositive = 0, nNegative = 0;

        for (int i = 0; i < n; i++) {
            int y = trueValues.get(i);
            double yHat = predictedValues.get(i);

            yHat = Math.max(epsilon, Math.min(1.0 - epsilon, yHat));
            bce += -(y * Math.log(yHat) + (1 - y) * Math.log(1 - yHat));

            int predBinary = (yHat >= 0.5) ? 1 : 0;

            if (y == 1) nPositive++;
            else nNegative++;

            if (predBinary == 1 && y == 1) tp++;
            else if (predBinary == 1 && y == 0) fp++;
            else if (predBinary == 0 && y == 0) tn++;
            else if (predBinary == 0 && y == 1) fn++;
        }

        bce /= n;

        double accuracy = (double) (tp + tn) / (tp + tn + fp + fn);
        double precision = (tp + fp == 0) ? 0 : (double) tp / (tp + fp);
        double recall = (tp + fn == 0) ? 0 : (double) tp / (tp + fn);
        double f1 = (precision + recall == 0) ? 0 : 2 * precision * recall / (precision + recall);

        double[] x = new double[101];
        double[] y = new double[101];

        for (int i = 0; i <= 100; i++) {
            double th = i / 100.0;
            int tpRoc = 0;
            int fpRoc = 0;

            for (int j = 0; j < n; j++) {
                int actual = trueValues.get(j);
                double predicted = predictedValues.get(j);

                if (actual == 1 && predicted >= th) tpRoc++;
                if (actual == 0 && predicted >= th) fpRoc++;
            }

            y[i] = (double) tpRoc / nPositive; // TPR
            x[i] = (double) fpRoc / nNegative; // FPR
        }

        double auc = 0.0;
        for (int i = 1; i <= 100; i++) {
            auc += (y[i - 1] + y[i]) * Math.abs(x[i - 1] - x[i]) / 2.0;
        }

        System.out.println("for " + fileName);
        System.out.println("\tBCE =" + bce);
        System.out.println("\tConfusion matrix");
        System.out.println("\t\t\ty=1\t\ty=0");
        System.out.println("\t\ty^=1\t" + tp + "\t" + fp);
        System.out.println("\t\ty^=0\t" + fn + "\t" + tn);
        System.out.println("\tAccuracy =" + accuracy);
        System.out.println("\tPrecision =" + precision);
        System.out.println("\tRecall =" + recall);
        System.out.println("\tf1 score =" + f1);
        System.out.println("\tauc roc =" + auc);

        return new Metrics(fileName, bce, tp, tn, fp, fn, accuracy, precision, recall, f1, auc);
    }

    public static void printBestBCE(Metrics m1, Metrics m2, Metrics m3) {
        Metrics best = m1;
        if (m2.bce < best.bce) best = m2;
        if (m3.bce < best.bce) best = m3;
        System.out.println("According to BCE, The best model is " + best.fileName);
    }

    public static void printBestAccuracy(Metrics m1, Metrics m2, Metrics m3) {
        Metrics best = m1;
        if (m2.accuracy > best.accuracy) best = m2;
        if (m3.accuracy > best.accuracy) best = m3;
        System.out.println("According to Accuracy, The best model is " + best.fileName);
    }

    public static void printBestPrecision(Metrics m1, Metrics m2, Metrics m3) {
        Metrics best = m1;
        if (m2.precision > best.precision) best = m2;
        if (m3.precision > best.precision) best = m3;
        System.out.println("According to Precision, The best model is " + best.fileName);
    }

    public static void printBestRecall(Metrics m1, Metrics m2, Metrics m3) {
        Metrics best = m1;
        if (m2.recall > best.recall) best = m2;
        if (m3.recall > best.recall) best = m3;
        System.out.println("According to Recall, The best model is " + best.fileName);
    }

    public static void printBestF1(Metrics m1, Metrics m2, Metrics m3) {
        Metrics best = m1;
        if (m2.f1 > best.f1) best = m2;
        if (m3.f1 > best.f1) best = m3;
        System.out.println("According to F1 score, The best model is " + best.fileName);
    }

    public static void printBestAUC(Metrics m1, Metrics m2, Metrics m3) {
        Metrics best = m1;
        if (m2.auc > best.auc) best = m2;
        if (m3.auc > best.auc) best = m3;
        System.out.println("According to AUC ROC, The best model is " + best.fileName);
    }
}