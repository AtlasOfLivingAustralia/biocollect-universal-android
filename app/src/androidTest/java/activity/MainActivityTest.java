package activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class MainActivityTest {

    AtlasSharedPreferenceManager aspm;
    SharedPreferences.Editor preferencesEditor;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class, true, false);


    @Before
    public void setUp() {
        Context targetContext = getInstrumentation().getTargetContext();
        preferencesEditor = PreferenceManager.getDefaultSharedPreferences(targetContext).edit();
        aspm = new AtlasSharedPreferenceManager(targetContext);
    }

    @Test
    public void testSettings() {

        aspm.writeUserId("test@example.org");
        aspm.writeUsername("test@example.org");
        aspm.writeUserDisplayName("test test");
        aspm.writeAuthKey("XXXXX");

        // Launch activity
        mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_setting));


        onView(withId(R.id.logout_settings_button)).check(matches(isDisplayed()));
        onView(withId(R.id.logout_settings_small_text)).check(matches(withText("You're currently logged in as test@example.org")));

    }
}
