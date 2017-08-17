package au.csiro.ozatlas.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by sad038 on 15/8/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class AtlasSharedPreferenceManagerTest {

    private AtlasSharedPreferenceManager mMockSharedPreferencesHelper;

    @Mock
    Context context;

    @Mock
    SharedPreferences sharedPreferences;

    @Mock
    SharedPreferences.Editor mMockEditor;

    @Before
    public void initMocks() {
        //when(PreferenceManager.getDefaultSharedPreferences(context)).thenReturn(sharedPreferences);

        // Create a mocked SharedPreferences.
        mMockSharedPreferencesHelper = new AtlasSharedPreferenceManager(sharedPreferences);

        // Return the MockEditor when requesting it.
        when(mMockSharedPreferencesHelper.getSharedPreferences().edit()).thenReturn(mMockEditor);
        when(mMockSharedPreferencesHelper.getAuthKey())
                .thenReturn("TEST");

    }

    @Test
    public void authKeyTest() throws Exception{
        mMockSharedPreferencesHelper.writeAuthKey("TEST");
        assertEquals("TEST", mMockSharedPreferencesHelper.getAuthKey());
    }
}

