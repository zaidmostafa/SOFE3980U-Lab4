package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import com.opencsv.CSVReader;

public class App {
    public static void main(String[] args) {
        evaluateModel("model.csv");
    }

    public static void evaluateModel(String fileName) {
        double epsilon = 1e-15;
        double ce = 0.0;
        int n = 0;

        // confusion matrix [predicted][actual]
        int[][] confusionMatrix = new int[5][5];

        try {
            FileReader filereader = new FileReader(fileName);
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;

            csvReader.readNext(); 

            while ((nextRecord = csvReader.readNext()) != null) {
                int actualClass = Integer.parseInt(nextRecord[0]);

                double[] probs = new double[5];
                for (int i = 0; i < 5; i++) {
                    probs[i] = Double.parseDouble(nextRecord[i + 1]);
                }

                double trueClassProb = Math.max(epsilon, probs[actualClass - 1]);
                ce += -Math.log(trueClassProb);

                int predictedClass = 0;
                double maxProb = probs[0];
                for (int i = 1; i < 5; i++) {
                    if (probs[i] > maxProb) {
                        maxProb = probs[i];
                        predictedClass = i;
                    }
                }

                confusionMatrix[predictedClass][actualClass - 1]++;
                n++;
            }

            csvReader.close();
        } catch (Exception e) {
            System.out.println("Error reading file: " + fileName);
            e.printStackTrace();
            return;
        }

        ce /= n;

        System.out.println("CE =" + ce);
        System.out.println("Confusion matrix");
        System.out.println("\t\t\ty=1\t\ty=2\t\ty=3\t\ty=4\t\ty=5");

        for (int i = 0; i < 5; i++) {
            System.out.print("\t\ty^=" + (i + 1));
            for (int j = 0; j < 5; j++) {
                System.out.print("\t" + confusionMatrix[i][j]);
            }
            System.out.println();
        }
    }
}