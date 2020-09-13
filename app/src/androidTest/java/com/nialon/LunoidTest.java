package com.nialon;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class LunoidTest {
    @Rule
    public ActivityTestRule<Lunoid> mActivityRule = new ActivityTestRule<>(Lunoid.class);

    public void appLaunchesSuccessfully() {
        Espresso.onView(withId(R.id.imgNote)).check(matches(isDisplayed()));
    }


    public void test01012020() {
        Espresso.onView(withId(R.id.linearLayoutHaut)).perform(customSwipeDown());
        Espresso.onView(withId(R.id.textLever)).check(matches(withText("11:25")));
        Espresso.onView(withId(R.id.textCoucher)).check(matches(withText("22:40")));
        Espresso.onView(withId(R.id.textPct)).check(matches(withText("33 %")));
        Espresso.onView(withId(R.id.textHCD)).check(matches(withText("00:00")));
        Espresso.onView(withId(R.id.textHMD)).check(matches(withText("00:00")));
        Espresso.onView(withId(R.id.textPerigeeHour)).check(matches(withText("--:--")));
        Espresso.onView(withId(R.id.textApogeeHour)).check(matches(withText("--:--")));
        Espresso.onView(withId(R.id.textNoeudHour)).check(matches(withText("--:--")));
        Espresso.onView(withId(R.id.textCroissant)).check(matches(withTextColor(Color.YELLOW)));
        Espresso.onView(withId(R.id.textDecroissant)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textMontant)).check(matches(withTextColor(Color.YELLOW)));
        Espresso.onView(withId(R.id.textDescendant)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textFleurs)).check(matches(withTextColor(Color.YELLOW)));
        Espresso.onView(withId(R.id.textFruits)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textRacines)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textFeuilles)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textApogee)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textPerigee)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textNoeud)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textApogeeHour)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textPerigeeHour)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textNoeudHour)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
    }


    public void test02012020() {
        Espresso.onView(withId(R.id.linearLayoutHaut)).perform(customSwipeDown());
        Espresso.onView(withId(R.id.linearLayoutHaut)).perform(customSwipeLeft());
        Espresso.onView(withId(R.id.textLever)).check(matches(withText("11:45")));
        Espresso.onView(withId(R.id.textCoucher)).check(matches(withText("--:--")));
        Espresso.onView(withId(R.id.textPct)).check(matches(withText("42 %")));
        Espresso.onView(withId(R.id.textHCD)).check(matches(withText("00:00")));
        Espresso.onView(withId(R.id.textHMD)).check(matches(withText("00:00")));
        Espresso.onView(withId(R.id.textPerigeeHour)).check(matches(withText("--:--")));
        Espresso.onView(withId(R.id.textApogeeHour)).check(matches(withText("00:30")));
        Espresso.onView(withId(R.id.textNoeudHour)).check(matches(withText("--:--")));
        Espresso.onView(withId(R.id.textCroissant)).check(matches(withTextColor(Color.YELLOW)));
        Espresso.onView(withId(R.id.textDecroissant)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textMontant)).check(matches(withTextColor(Color.YELLOW)));
        Espresso.onView(withId(R.id.textDescendant)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textFleurs)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textFruits)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textRacines)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textFeuilles)).check(matches(withTextColor(Color.YELLOW)));
        Espresso.onView(withId(R.id.textApogee)).check(matches(withTextColor(Color.RED)));
        Espresso.onView(withId(R.id.textPerigee)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textNoeud)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textApogeeHour)).check(matches(withTextColor(Color.RED)));
        Espresso.onView(withId(R.id.textPerigeeHour)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.textNoeudHour)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
    }

    public void editNoteSave() {
        Espresso.onView(withId(R.id.imgNote)).perform(click());
        Espresso.onView(withId(R.id.editText)).perform(typeText("Planter les ails"), closeSoftKeyboard());
        Espresso.onView(withText(R.string.sauvegarder)).perform(click());
    }


    public void editNoteCancel() {
        Espresso.onView(withId(R.id.imgNote)).perform(click());
        Espresso.onView(withId(R.id.editText)).perform(typeText("Planter les oignons"), closeSoftKeyboard());
        Espresso.onView(withText(R.string.annuler)).perform(click());
    }

    public void findNext() {
        Espresso.onView(withId(R.id.linearLayoutHaut)).perform(customSwipeDown());
        Espresso.onView(withId(R.id.textDecroissant)).perform(click());
        Espresso.onView(withId(R.id.textDecroissant)).check(matches(withTextColor(Color.YELLOW)));
        Espresso.onView(withId(R.id.textCroissant)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.TextJour)).check(matches(withText("Saturday 11 January 2020")));
        Espresso.onView(withId(R.id.textMontant)).perform(click());
        Espresso.onView(withId(R.id.textMontant)).check(matches(withTextColor(Color.YELLOW)));
        Espresso.onView(withId(R.id.textDescendant)).check(matches(withTextColor(Color.rgb(120, 120, 120))));
        Espresso.onView(withId(R.id.TextJour)).check(matches(withText("Thursday 23 January 2020")));
    }


    public void displayInfos() {
        Espresso.onView(withId(R.id.linearLayoutHaut)).perform(customSwipeUp());
        Espresso.onView(withId(R.id.infosmois)).check(matches(isDisplayed()));
        Espresso.pressBack();
        Espresso.onView(withId(R.id.imgNote)).check(matches(isDisplayed()));
    }

    @Test
    public void Annee2020() {
        Espresso.onView(withId(R.id.linearLayoutHaut)).perform(customSwipeDown());
        for (int i = 0; i < 10; i++) {

            Espresso.onView(withId(R.id.linearLayoutHaut)).perform(customSwipeUp());
            Espresso.onView(withId(R.id.infosmois)).check(matches(isDisplayed()));
            Espresso.pressBack();
            Espresso.onView(withId(R.id.imgNote)).check(matches(isDisplayed()));
            Espresso.onView(withId(R.id.linearLayoutHaut)).perform(customSwipeLeft());
        }
    }

    public static ViewAction customSwipeDown() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.TOP_RIGHT, GeneralLocation.CENTER_RIGHT, Press.FINGER);
    }

    public static ViewAction customSwipeUp() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_RIGHT, GeneralLocation.TOP_RIGHT, Press.FINGER);
    }

    public static ViewAction customSwipeLeft() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_RIGHT, GeneralLocation.CENTER_LEFT, Press.FINGER);
    }

    public static Matcher<View> withTextColor(final int expectedId) {
        return new BoundedMatcher<View, TextView>(TextView.class) {

            @Override
            protected boolean matchesSafely(TextView textView) {
                //int colorId = ContextCompat.getColor(textView.getContext(), expectedId);
                return textView.getCurrentTextColor() == expectedId;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
                description.appendValue(expectedId);
            }
        };
    }
}