package au.org.ala.mobile.ozatlas.util

inline fun <T,R> List<T>.toMap(mapFunc: (T) -> Pair<R, T>): Map<R,T> = this.map(mapFunc).toMap()