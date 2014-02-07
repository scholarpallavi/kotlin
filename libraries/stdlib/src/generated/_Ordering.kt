package kotlin

//
// NOTE THIS FILE IS AUTO-GENERATED by the GenerateStandardLib.kt
// See: https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib
//

import java.util.*

/**
 * Returns a list with elements in reversed order
 */
public fun <T> Array<T>.reverse() : List<T> {
    val list = toArrayList()
    Collections.reverse(list)
    return list
    
}

/**
 * Returns a list with elements in reversed order
 */
public fun BooleanArray.reverse() : List<Boolean> {
    val list = toArrayList()
    Collections.reverse(list)
    return list
    
}

/**
 * Returns a list with elements in reversed order
 */
public fun ByteArray.reverse() : List<Byte> {
    val list = toArrayList()
    Collections.reverse(list)
    return list
    
}

/**
 * Returns a list with elements in reversed order
 */
public fun CharArray.reverse() : List<Char> {
    val list = toArrayList()
    Collections.reverse(list)
    return list
    
}

/**
 * Returns a list with elements in reversed order
 */
public fun DoubleArray.reverse() : List<Double> {
    val list = toArrayList()
    Collections.reverse(list)
    return list
    
}

/**
 * Returns a list with elements in reversed order
 */
public fun FloatArray.reverse() : List<Float> {
    val list = toArrayList()
    Collections.reverse(list)
    return list
    
}

/**
 * Returns a list with elements in reversed order
 */
public fun IntArray.reverse() : List<Int> {
    val list = toArrayList()
    Collections.reverse(list)
    return list
    
}

/**
 * Returns a list with elements in reversed order
 */
public fun LongArray.reverse() : List<Long> {
    val list = toArrayList()
    Collections.reverse(list)
    return list
    
}

/**
 * Returns a list with elements in reversed order
 */
public fun ShortArray.reverse() : List<Short> {
    val list = toArrayList()
    Collections.reverse(list)
    return list
    
}

/**
 * Returns a list with elements in reversed order
 */
public fun <T> Iterable<T>.reverse() : List<T> {
    val list = toArrayList()
    Collections.reverse(list)
    return list
    
}

/**
 * Copies all elements into a [[List]] and sorts it
 */
public fun <T: Comparable<T>> Iterable<T>.sort() : List<T> {
    val sortedList = toArrayList()
    val sortBy: Comparator<T> = comparator<T> {(x: T, y: T) -> x.compareTo(y)}
    java.util.Collections.sort(sortedList, sortBy)
    return sortedList
    
}

/**
 * Copies all elements into a [[List]] and sorts it using provided comparator
 */
public fun <T> Array<T>.sortBy(comparator : Comparator<T>) : List<T> {
    val sortedList = toArrayList()
    java.util.Collections.sort(sortedList, comparator)
    return sortedList
    
}

/**
 * Copies all elements into a [[List]] and sorts it using provided comparator
 */
public fun <T> Iterable<T>.sortBy(comparator : Comparator<T>) : List<T> {
    val sortedList = toArrayList()
    java.util.Collections.sort(sortedList, comparator)
    return sortedList
    
}

/**
 * Copies all elements into a [[List]] and sorts it by value of f(element)
 */
public fun <T, R: Comparable<R>> Array<T>.sortBy(f: (T) -> R) : List<T> {
    val sortedList = toArrayList()
    val sortBy: Comparator<T> = comparator<T> {(x: T, y: T) -> f(x).compareTo(f(y))}
    java.util.Collections.sort(sortedList, sortBy)
    return sortedList
    
}

/**
 * Copies all elements into a [[List]] and sorts it by value of f(element)
 */
public fun <T, R: Comparable<R>> Iterable<T>.sortBy(f: (T) -> R) : List<T> {
    val sortedList = toArrayList()
    val sortBy: Comparator<T> = comparator<T> {(x: T, y: T) -> f(x).compareTo(f(y))}
    java.util.Collections.sort(sortedList, sortBy)
    return sortedList
    
}
