package com.example.assignment;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Sabeer Shaikh on 12/2/19.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class AssignmentActivityTest {

    @Rule
    public ActivityTestRule<AssignmentActivity> mActivityTestRule = new ActivityTestRule<>(AssignmentActivity.class);

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @Test
    public void assignmentActivityTest() {
        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.action_bar_root),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler_view),
                        childAtPosition(
                                allOf(withId(R.id.swipe_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                0)),
                                0),
                        isDisplayed()));
        recyclerView.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.title_text),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));


        ViewInteraction textView2 = onView(
                allOf(withId(R.id.description_text),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.image_test),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.card_view),
                                        0),
                                1),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

    }
}
