package au.csiro.ozatlas;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;


import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleInstrumentedTest {
    @Test
    @Ignore // not all au.csiro.ozatlas
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("au.csiro.ozatlas", appContext.getPackageName());
    }
}
