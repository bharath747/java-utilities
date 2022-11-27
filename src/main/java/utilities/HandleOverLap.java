package utilities;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class HandleOverLap {
    public static void main(String[] args) throws IOException {
        List<Pair> pairList = getData();

        Collections.sort(pairList, new Comparator<Pair>() {
            public int compare(Pair t0, Pair t1) {
                int index = Double.valueOf(t0.getKey1()).compareTo(t1.getKey1());
                return index == 0 ? Double.valueOf(t0.getKey2()).compareTo(t1.getKey2()) : index;
            }
        });

        double[] arr = new double[pairList.size() * 2];

        int index = 0;
        for (Pair pair : pairList) {
            if (index == 0) {
                arr[index++] = pair.getKey1();
                arr[index++] = pair.getKey2();
            } else {
                if (arr[index - 1] <=  pair.getKey1()) {
                    arr[index] = pair.getKey1();
                    arr[index + 1] = pair.getKey2();

                    index = index + 2;
                } else if (arr[index - 1] < pair.getKey2()) {
                    arr[index] = arr[index - 1];
                    arr[index + 1] = pair.getKey2();

                    index = index + 2;
                }
            }
        }
        printArray(arr, pairList.size());
        //printLength(arr, pairList.size());
    }

    private static void printArray(double[] arr, int size) {
        int index = 0;
        for (int i = 0; i < size ; i++) {
            if (arr[index] == 0) {
                break;
            }
            System.out.println(arr[index++] + "-" + arr[index++]);
        }
    }

    private static void printLength(double[] arr, int size) {
        double length = 0;

        int index = 0;
        for (int i = 0; i < size ; i++) {
            if (arr[index] == 0) {
                break;
            }
            length = length + arr[index + 1] - arr[index];
            index = index + 2;
        }

        System.out.println(length);
    }

    private static class Pair {
        private double key1;
        private double key2;

        private Pair() {
        }

        private Pair(double key1, double key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        private double getKey1() {
            return key1;
        }

        private void setKey1(double key1) {
            this.key1 = key1;
        }

        private double getKey2() {
            return key2;
        }

        private void setKey2(double key2) {
            this.key2 = key2;
        }
    }

    private static List<Pair> getData() throws IOException {
        List<Pair> data = new ArrayList<Pair>();
        try {
            FileInputStream file = new FileInputStream(new File("/home/bharath/development/others/LCW-ANGUL-DPR.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Pair pair = new Pair(row.getCell(7).getNumericCellValue(), row.getCell(8).getNumericCellValue());
                data.add(pair);
            }
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}