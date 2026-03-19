package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class App {

    static class Metrics {
        String fileName;
        double mse;
        double mae;
        double mare;

        Metrics(String fileName, double mse, double mae, double mare) {
            this.fileName = fileName;
            this.mse = mse;
            this.mae = mae;
            this.mare = mare;
        }
    }

    public static void main(String[] args) {
        Metrics m1 = evaluateModel("model_1.csv");
        Metrics m2 = evaluateModel("model_2.csv");
        Metrics m3 = evaluateModel("model_3.csv");

        printBestModelByMSE(m1, m2, m3);
        printBestModelByMAE(m1, m2, m3);
        printBestModelByMARE(m1, m2, m3);
    }

    public static Metrics evaluateModel(String fileName) {
        List<Double> trueValues = new ArrayList<>();
        List<Double> predictedValues = new ArrayList<>();
        double epsilon = 1e-10;

        try {
            FileReader filereader = new FileReader(fileName);
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;

            csvReader.readNext(); // skip header

            while ((nextRecord = csvReader.readNext()) != null) {
                double trueValue = Double.parseDouble(nextRecord[0]);
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
        double mse = 0.0;
        double mae = 0.0;
        double mare = 0.0;

        for (int i = 0; i < n; i++) {
            double y = trueValues.get(i);
            double yHat = predictedValues.get(i);
            double error = y - yHat;

            mse += error * error;
            mae += Math.abs(error);
            mare += Math.abs(error) / (Math.abs(y) + epsilon);
        }

        mse /= n;
        mae /= n;
        mare /= n;

        System.out.println("for " + fileName);
        System.out.println("\tMSE =" + mse);
        System.out.println("\tMAE =" + mae);
        System.out.println("\tMARE =" + mare);

        return new Metrics(fileName, mse, mae, mare);
    }

    public static void printBestModelByMSE(Metrics m1, Metrics m2, Metrics m3) {
        Metrics best = m1;
        if (m2.mse < best.mse) best = m2;
        if (m3.mse < best.mse) best = m3;
        System.out.println("According to MSE, The best model is " + best.fileName);
    }

    public static void printBestModelByMAE(Metrics m1, Metrics m2, Metrics m3) {
        Metrics best = m1;
        if (m2.mae < best.mae) best = m2;
        if (m3.mae < best.mae) best = m3;
        System.out.println("According to MAE, The best model is " + best.fileName);
    }

    public static void printBestModelByMARE(Metrics m1, Metrics m2, Metrics m3) {
        Metrics best = m1;
        if (m2.mare < best.mare) best = m2;
        if (m3.mare < best.mare) best = m3;
        System.out.println("According to MARE, The best model is " + best.fileName);
    }
}
