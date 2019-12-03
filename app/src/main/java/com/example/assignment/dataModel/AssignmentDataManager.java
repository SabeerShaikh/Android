package com.example.assignment.dataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabeer Shaikh on 11/28/19.
 */
public class AssignmentDataManager {
    private static AssignmentDataManager instance = null;
    private List<AssignmentModel> latestData;

    private AssignmentDataManager() {
        latestData = new ArrayList<>();
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
