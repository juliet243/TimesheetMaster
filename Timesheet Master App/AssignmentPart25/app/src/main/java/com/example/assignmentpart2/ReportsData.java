package com.example.assignmentpart2;

public class ReportsData {

    int totalHour, minGoal, maxGoal  ;

    String date, category;

    public ReportsData() {

    }

    public int getTotalHour() {
        return totalHour;
    }

    public int getMinGoal() {
        return minGoal;
    }

    public int getMaxGoal() {
        return maxGoal;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public ReportsData(int totalHour, int minGoal, int maxGoal, String date, String category) {

        this.totalHour = totalHour;
        this.minGoal = minGoal;
        this.maxGoal = maxGoal;
        this.date = date;
        this.category =category;
    }
}
