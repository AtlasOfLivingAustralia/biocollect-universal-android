package au.csiro.ozatlas.di;

import javax.inject.Singleton;

import au.csiro.ozatlas.base.BaseActivity;
import au.csiro.ozatlas.base.BaseFragment;
import dagger.Component;

/**
 * Created by sad038 on 5/4/17.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(BaseActivity activity);
    void inject(BaseFragment fragment);
}