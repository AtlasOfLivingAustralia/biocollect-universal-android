package au.org.ala.mobile.ozatlas

import com.squareup.picasso.Picasso

interface OzAtlasGraph {
    fun inject(app: OzAtlasApp)
    fun picasso(): Picasso

}