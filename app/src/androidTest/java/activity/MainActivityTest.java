package activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.activity.LoginActivity;
import au.csiro.ozatlas.manager.AtlasSharedPreferenceManager;

import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;

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
        Intents.init();
    }

    @Test
    public void testSettings() throws InterruptedException {

        aspm.writeUserId("test@example.org");
        aspm.writeUsername("test@example.org");
        aspm.writeUserDisplayName("test test");
        aspm.writeAuthKey("XXXXX");

        // Launch activity
        mActivityRule.launchActivity(new Intent());

        // open drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // navigate to settings
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_setting));

        // check logout setting is displayed
        onView(withId(R.id.logout_settings_button)).check(matches(isDisplayed()));
        // check logout small text has user's username
        onView(withId(R.id.logout_settings_small_text)).check(matches(withText("You're currently logged in as test@example.org")));
        // click logout
        onView(withId(R.id.logout_settings_button)).perform(doubleClick());

        // wait for dialog TODO work around having to do this somehow?!?

        final long SLEEP_TIME = 1000;

        TimeUnit.MILLISECONDS.sleep(SLEEP_TIME);

        // check dialog with logout text is displayed
        onView(withText(R.string.logout_message)).check(matches(isDisplayed()));

        // check click cancel doesn't do anything
        onView(withId(android.R.id.button2)).perform(click());
        TimeUnit.MILLISECONDS.sleep(SLEEP_TIME);
        onView(withText(R.string.logout_message)).check(doesNotExist());
        onView(withId(R.id.logout_settings_button)).check(matches(isDisplayed()));
    }
}
