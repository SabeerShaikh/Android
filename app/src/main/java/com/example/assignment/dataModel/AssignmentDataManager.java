package com.example.assignment.dataModel;

import java.util.ArrayList;
import java.util.List;

public class AssignmentDataManager {
    private static AssignmentDataManager instance = null;
    private List<AssignmentModel> latestData;

    private AssignmentDataManager() {
        latestData = new ArrayList<AssignmentModel>();
    }

    public static AssignmentDataManager getInstance() {

        synchronized (AssignmentDataManager.class) {
            if (instance == null) {
                instance = new AssignmentDataManager();
            }
        }

        return instance;
    }

    public List<AssignmentModel> getLatestData() {
        return latestData;
    }

    public void setLatestData(List<AssignmentModel> latestData) {

        this.latestData = latestData;
    }
}
