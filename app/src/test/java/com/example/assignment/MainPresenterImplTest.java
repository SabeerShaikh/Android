package com.example.assignment;

import com.example.assignment.Interfaces.MainView;
import com.example.assignment.Presenter.MainPresenterImpl;
import com.example.assignment.dataModel.AssignmentModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;



import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Sabeer Shaikh on 12/3/19.
 */
@RunWith(MockitoJUnitRunner.class)

public class MainPresenterImplTest {
    @Mock
    MainView mainView;

    private MainPresenterImpl mainPresenter;

    @Before
    public void setUp() {
        mainPresenter = new MainPresenterImpl(mainView);
    }

    @Test
    public void checkIfEQArePassedToView() {
        AssignmentModel test1 = mock(AssignmentModel.class);
        AssignmentModel test2 = mock(AssignmentModel.class);

        List<AssignmentModel> list = new ArrayList<>(2);
        list.add(test1);
        list.add(test2);

        mainPresenter.onSuccess("sucess",list);
        verify(mainView, times(1)).onGetDataSuccess(list);
        verify(mainView, times(1)).hideProgress();

    }

    @Test
    public void checkIfViewIsReleasedOnStop() {
        mainPresenter.onDestroy();
        assertNull(mainPresenter.getMainView());
    }



}
