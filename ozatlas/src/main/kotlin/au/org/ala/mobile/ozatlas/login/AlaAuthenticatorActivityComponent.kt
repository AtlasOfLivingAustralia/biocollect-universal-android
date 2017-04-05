package au.org.ala.mobile.ozatlas.login

import au.org.ala.mobile.ozatlas.OzAtlasComponent
import au.org.ala.mobile.ozatlas.dagger.AlaAuthenticatorActivityScope
import dagger.Component
@AlaAuthenticatorActivityScope
@Component(
        dependencies = arrayOf(OzAtlasComponent::class),
        modules = arrayOf(AlaAuthenticatorActivityModule::class)
)
interface AlaAuthenticatorActivityComponent {
    fun inject(activity: AlaAuthenticatorActivity)
}