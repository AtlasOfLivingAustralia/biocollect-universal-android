package di;

import javax.inject.Singleton;

import au.csiro.ozatlas.base.BaseActivity;
import au.csiro.ozatlas.base.BaseAuthWorker;
import au.csiro.ozatlas.base.BaseFragment;
import au.csiro.ozatlas.base.BaseIntentService;
import au.csiro.ozatlas.di.AnalyticsModule;
import au.csiro.ozatlas.di.PreferenceModule;
import au.csiro.ozatlas.di.RestModule;
import au.csiro.ozatlas.rest.CustomRequestInterceptor;
import dagger.Component;

/**
 * Created by sad038 on 5/4/17.
 */

@Singleton
@Component(modules = {PreferenceModule.class, RestModule.class, AnalyticsModule.class})
public interface AppComponent {
    void inject(BaseActivity activity);

    void inject(BaseFragment fragment);

    void inject(CustomRequestInterceptor customRequestInterceptor);

    void inject(BaseAuthWorker baseAuthWorker);

    void inject(BaseIntentService intentService);
}