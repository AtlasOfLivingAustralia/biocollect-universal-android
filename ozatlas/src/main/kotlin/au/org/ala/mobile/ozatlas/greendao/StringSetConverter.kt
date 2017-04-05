package au.org.ala.mobile.ozatlas.greendao

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.greenrobot.greendao.converter.PropertyConverter


class StringSetConverter : PropertyConverter<Set<String>, String> {

    companion object {
        val moshi = Moshi.Builder().build()
        val adapterType = Types.newParameterizedType(Set::class.java, String::class.java)
    }

    override fun convertToEntityProperty(strings: String?): Set<String> = strings.let { ss -> moshi.adapter<Set<String>>(adapterType).fromJson(ss) } ?: emptySet()

    override fun convertToDatabaseValue(strings: Set<String>?): String = moshi.adapter<Set<String>>(adapterType).toJson(strings ?: emptySet<String>())
}