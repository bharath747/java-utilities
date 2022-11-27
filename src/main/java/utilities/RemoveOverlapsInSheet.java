package utilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class RemoveOverlapsInSheet {

    public static void main(String[] args) throws IOException {
        int[] indexes = {0, 1, 2, 3, 7, 13, 14, 15, 16, 18, 24};//[Date, DprId, RecordId, Boq, BoqQty, ActivityQty, ChainageSide, ChainageFrom, ChainageTo, StructureType, LaneCompleted]
        Map<String, List<DPR>> map = getDataFromExcel("/home/bharath/development/others/LCW-ANGUL-DPR.xlsx", indexes);

        Map<String, List<DPR>> correctedMap = new HashMap<String, List<DPR>>();
        for (Map.Entry<String, List<DPR>> entry : map.entrySet()) {
            correctedMap.put(entry.getKey(), getCorrectedList(entry.getValue()));
        }
        writeToSheet(correctedMap);
    }

    private static List<DPR> getCorrectedList(List<DPR> DPRList) {
        List<DPR> correctedList = new ArrayList<DPR>();
        if (DPRList != null && !DPRList.isEmpty()) {

            Collections.sort(DPRList, new Comparator<DPR>() {
                public int compare(DPR t0, DPR t1) {
                    int index = t0.getChainageFrom().compareTo(t1.getChainageFrom());
                    return index == 0 ? t0.getChainageTo().compareTo(t1.getChainageTo()) : index;
                }
            });

            for (DPR dpr : DPRList) {
                int index = correctedList.size();
                if (index == 0) {
                    correctedList.add(dpr);
                } else {
                    if (correctedList.get(index - 1).getChainageTo() <=  dpr.getChainageFrom()) {
                        correctedList.add(dpr);
                    } else if (correctedList.get(index - 1).getChainageTo() < dpr.getChainageTo()) {
                        dpr.setChainageFrom(correctedList.get(index - 1).getChainageTo());
                        correctedList.add(dpr);
                    }
                }
            }
        }
        return correctedList;
    }

    private static void printChainages(List<DPR> correctedList) {
        double length = 0;

        for (DPR dpr : correctedList) {
            System.out.println(dpr.getChainageFrom() + "-" + dpr.getChainageTo());
        }

        System.out.println(length);
    }

    private static void printLength(List<DPR> correctedList) {
        double length = 0;

        for (DPR dpr : correctedList) {
            length = length + (dpr.getChainageTo() - dpr.getChainageFrom());
        }

        System.out.println(length);
    }

    private static class DPR {
        private String date;
        private Integer dprId;
        private Integer recordId;
        private String boq;
        private Double boqQty;
        private Double activityQty;
        private String chainageSide;
        private Double chainageFrom;
        private Double chainageTo;
        private Double length;
        private String structureType;
        private String laneCompleted;

        private DPR() {
        }

        String getDate() {
            return date;
        }

        private void setDate(String date) {
            this.date = date;
        }

        private Integer getDprId() {
            return dprId;
        }

        private void setDprId(Integer dprId) {
            this.dprId = dprId;
        }

        private Integer getRecordId() {
            return recordId;
        }

        private void setRecordId(Integer recordId) {
            this.recordId = recordId;
        }

        private String getBoq() {
            return boq;
        }

        private void setBoq(String boq) {
            this.boq = boq;
        }

        private Double getBoqQty() {
            return boqQty;
        }

        private void setBoqQty(Double boqQty) {
            this.boqQty = boqQty;
        }

        private Double getActivityQty() {
            return activityQty;
        }

        private void setActivityQty(Double activityQty) {
            this.activityQty = activityQty;
        }

        private String getChainageSide() {
            return chainageSide;
        }

        private void setChainageSide(String chainageSide) {
            this.chainageSide = chainageSide;
        }

        private Double getChainageFrom() {
            return chainageFrom;
        }

        private void setChainageFrom(Double chainageFrom) {
            this.chainageFrom = chainageFrom;
        }

        private Double getChainageTo() {
            return chainageTo;
        }

        private void setChainageTo(Double chainageTo) {
            this.chainageTo = chainageTo;
        }

        public Double getLength() {
            return length;
        }

        private void setLength(Double length) {
            this.length = length;
        }

        private String getStructureType() {
            return structureType;
        }

        private void setStructureType(String structureType) {
            this.structureType = structureType;
        }

        private String getLaneCompleted() {
            return laneCompleted;
        }

        private void setLaneCompleted(String laneCompleted) {
            this.laneCompleted = laneCompleted;
        }
    }

    private static Map<String, List<DPR>> getDataFromExcel(String fileName, int[] indexes) throws IOException {
        Map<String, List<DPR>> map = new HashMap<String, List<DPR>>();
        try {
            FileInputStream file = new FileInputStream(new File(fileName));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int indexOfChainageSide = indexes[6];
            int indexOfBoqQty = indexes[4];
            int indexOfLaneCompleted = indexes[10];

            for (Row row : sheet) {
                String side = row.getCell(indexOfChainageSide).getStringCellValue();
                if (side.equals("") || (!side.equalsIgnoreCase("LCW") && !side.equalsIgnoreCase("RCW") && !side.equalsIgnoreCase("MCW-Both Sides"))) {
                    continue;
                }
                Double boqQty = row.getCell(indexOfBoqQty).getNumericCellValue();
                boolean isMainActivity = boqQty > 0;
                if (!isMainActivity) {
                    continue;
                }

                String laneCompleted = row.getCell(indexOfLaneCompleted).getStringCellValue();
                if (!laneCompleted.equals("Completed")) {
                    continue;
                }

                DPR dpr = new DPR();
                dpr.setDate(row.getCell(0).getStringCellValue());
                dpr.setDprId(Double.valueOf(row.getCell(1).getNumericCellValue()).intValue());
                dpr.setRecordId(Double.valueOf(row.getCell(2).getNumericCellValue()).intValue());
                dpr.setBoq(row.getCell(3).getStringCellValue());
                dpr.setBoqQty(row.getCell(7).getNumericCellValue());
                dpr.setActivityQty(row.getCell(13).getNumericCellValue());
                dpr.setChainageSide(row.getCell(14).getStringCellValue());
                dpr.setChainageFrom(row.getCell(15).getNumericCellValue());
                dpr.setChainageTo(row.getCell(16).getNumericCellValue());
                dpr.setStructureType(row.getCell(18).getStringCellValue());
                dpr.setLaneCompleted(row.getCell(24).getStringCellValue());

                if (side.equals("MCW-Both Sides")) {
                    if (map.containsKey("LCW")) {
                        map.get("LCW").add(dpr);
                    } else {
                        List<DPR> dprs = new ArrayList<DPR>();
                        dprs.add(dpr);
                        map.put("LCW", dprs);
                    }
                    if (map.containsKey("RCW")) {
                        map.get("RCW").add(dpr);
                    } else {
                        List<DPR> dprs = new ArrayList<DPR>();
                        dprs.add(dpr);
                        map.put("RCW", dprs);
                    }
                } else {
                    if (map.containsKey(side)) {
                        map.get(side).add(dpr);
                    } else {
                        List<DPR> dprs = new ArrayList<DPR>();
                        dprs.add(dpr);
                        map.put(side, dprs);
                    }
                }
            }
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private static void writeToSheet(Map<String, List<DPR>> correctedMap) {
        XSSFWorkbook workbook = new XSSFWorkbook();

        for (Map.Entry<String, List<DPR>> entry : correctedMap.entrySet()) {
            XSSFSheet sheet = workbook.createSheet(entry.getKey());

            int rownum = 0;
            for (DPR dpr : entry.getValue()) {
                Row row = sheet.createRow(rownum++);
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(dpr.getDate());

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(dpr.getDprId());

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(dpr.getRecordId());

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(dpr.getBoq());

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(dpr.getBoqQty());

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(dpr.getActivityQty());

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(dpr.getChainageSide());

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(dpr.getChainageFrom());

                Cell cell8 = row.createCell(8);
                cell8.setCellValue(dpr.getChainageTo());

                Cell cell9 = row.createCell(9);
                cell9.setCellValue(dpr.getChainageTo() - dpr.getChainageFrom());

                Cell cell10 = row.createCell(10);
                cell10.setCellValue(dpr.getStructureType());

                Cell cell11 = row.createCell(11);
                cell11.setCellValue(dpr.getLaneCompleted());
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(new File("/home/bharath/development/others/LCW-ANGUL-DPR-OUTPUT-1.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("written successfully on sheet.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}