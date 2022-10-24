package activity;

import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

import au.csiro.ozatlas.R;
import au.csiro.ozatlas.activity.LoginActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

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
    public void loginActivityTest() {
        ViewInteraction appCompatEditTextUserName = onView(
                allOf(withId(R.id.editUsername), isDisplayed()));
        appCompatEditTextUserName.perform(replaceText(""), closeSoftKeyboard());

        ViewInteraction appCompatEditTextPassword = onView(
                allOf(withId(R.id.editPassword), isDisplayed()));
        appCompatEditTextPassword.perform(replaceText(""), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.loginButton), withText("Login"),
                        withParent(withId(R.id.filedLayout)),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.inputLayoutUsername),
                                        1),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("UserName Missing")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textinput_error),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.inputLayoutPassword),
                                        1),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("Password Missing")));
    }
}
