package au.csiro.ozatlas.manager;

import android.content.SharedPreferences;

import org.mockito.Mock;

import static org.junit.Assert.*;

/**
 * Created by sad038 on 15/8/17.
 */
public class AtlasSharedPreferenceManagerTest {
    @Mock
    SharedPreferences mMockSharedPreferences;

    @Mock
    SharedPreferences mMockBrokenSharedPreferences;

    @Mock
    SharedPreferences.Editor mMockEditor;

    @Mock
    SharedPreferences.Editor mMockBrokenEditor;

}