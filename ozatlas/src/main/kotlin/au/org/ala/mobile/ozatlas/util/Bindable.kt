package au.org.ala.mobile.ozatlas.util

import android.databinding.BaseObservable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> BaseObservable.bindable(initialValue: T, fieldId: Int):
        ReadWriteProperty<Any?, T> = object : ReadWriteProperty<Any?, T> {
    private var value = initialValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
        notifyPropertyChanged(fieldId)
    }
}