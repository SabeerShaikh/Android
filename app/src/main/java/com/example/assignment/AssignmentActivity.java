package com.example.assignment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by Sabeer Shaikh on 11/28/19.
 */
public class AssignmentActivity extends AppCompatActivity {
    private String TAG_ASSIGNMENT_FRAGMENT = "AssignmentFragment";
    private AssignmentFragment mAssignmentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        mAssignmentFragment = new AssignmentFragment();
        transaction.replace(R.id.title_fragment, mAssignmentFragment).commit();

    }
}
