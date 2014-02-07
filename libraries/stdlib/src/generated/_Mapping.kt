package kotlin

//
// NOTE THIS FILE IS AUTO-GENERATED by the GenerateStandardLib.kt
// See: https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib
//

import java.util.*

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single list
 */
public fun <T, R> Iterable<T>.flatMap(transform: (T)-> Iterable<R>) : List<R> {
    return flatMapTo(ArrayList<R>(), transform)
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single list
 */
public fun <T, R> Array<T>.flatMap(transform: (T)-> Iterable<R>) : List<R> {
    return flatMapTo(ArrayList<R>(), transform)
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single list
 */
public fun <R> ByteArray.flatMap(transform: (Byte)-> Iterable<R>) : List<R> {
    return flatMapTo(ArrayList<R>(), transform)
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single list
 */
public fun <R> CharArray.flatMap(transform: (Char)-> Iterable<R>) : List<R> {
    return flatMapTo(ArrayList<R>(), transform)
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single list
 */
public fun <R> DoubleArray.flatMap(transform: (Double)-> Iterable<R>) : List<R> {
    return flatMapTo(ArrayList<R>(), transform)
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single list
 */
public fun <R> LongArray.flatMap(transform: (Long)-> Iterable<R>) : List<R> {
    return flatMapTo(ArrayList<R>(), transform)
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single list
 */
public fun <R> IntArray.flatMap(transform: (Int)-> Iterable<R>) : List<R> {
    return flatMapTo(ArrayList<R>(), transform)
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single list
 */
public fun <R> ShortArray.flatMap(transform: (Short)-> Iterable<R>) : List<R> {
    return flatMapTo(ArrayList<R>(), transform)
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single list
 */
public fun <R> BooleanArray.flatMap(transform: (Boolean)-> Iterable<R>) : List<R> {
    return flatMapTo(ArrayList<R>(), transform)
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single list
 */
public fun <R> FloatArray.flatMap(transform: (Float)-> Iterable<R>) : List<R> {
    return flatMapTo(ArrayList<R>(), transform)
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single list
 */
public fun <T, R> Stream<T>.flatMap(transform: (T)-> Stream<R>) : Stream<R> {
    return flatMapTo(ArrayList<R>(), transform).stream()
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single collection
 */
public fun <T, R, C: MutableCollection<in R>> Iterable<T>.flatMapTo(result: C, transform: (T) -> Iterable<R>) : C {
    for (element in this) {
        val list = transform(element)
        result.addAll(list)
    }
    return result
    
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single collection
 */
public fun <T, R, C: MutableCollection<in R>> Array<T>.flatMapTo(result: C, transform: (T) -> Iterable<R>) : C {
    for (element in this) {
        val list = transform(element)
        result.addAll(list)
    }
    return result
    
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single collection
 */
public fun <R, C: MutableCollection<in R>> ByteArray.flatMapTo(result: C, transform: (Byte) -> Iterable<R>) : C {
    for (element in this) {
        val list = transform(element)
        result.addAll(list)
    }
    return result
    
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single collection
 */
public fun <R, C: MutableCollection<in R>> CharArray.flatMapTo(result: C, transform: (Char) -> Iterable<R>) : C {
    for (element in this) {
        val list = transform(element)
        result.addAll(list)
    }
    return result
    
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single collection
 */
public fun <R, C: MutableCollection<in R>> DoubleArray.flatMapTo(result: C, transform: (Double) -> Iterable<R>) : C {
    for (element in this) {
        val list = transform(element)
        result.addAll(list)
    }
    return result
    
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single collection
 */
public fun <R, C: MutableCollection<in R>> LongArray.flatMapTo(result: C, transform: (Long) -> Iterable<R>) : C {
    for (element in this) {
        val list = transform(element)
        result.addAll(list)
    }
    return result
    
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single collection
 */
public fun <R, C: MutableCollection<in R>> IntArray.flatMapTo(result: C, transform: (Int) -> Iterable<R>) : C {
    for (element in this) {
        val list = transform(element)
        result.addAll(list)
    }
    return result
    
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single collection
 */
public fun <R, C: MutableCollection<in R>> ShortArray.flatMapTo(result: C, transform: (Short) -> Iterable<R>) : C {
    for (element in this) {
        val list = transform(element)
        result.addAll(list)
    }
    return result
    
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single collection
 */
public fun <R, C: MutableCollection<in R>> BooleanArray.flatMapTo(result: C, transform: (Boolean) -> Iterable<R>) : C {
    for (element in this) {
        val list = transform(element)
        result.addAll(list)
    }
    return result
    
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single collection
 */
public fun <R, C: MutableCollection<in R>> FloatArray.flatMapTo(result: C, transform: (Float) -> Iterable<R>) : C {
    for (element in this) {
        val list = transform(element)
        result.addAll(list)
    }
    return result
    
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single stream
 */
public fun <T, R, C: MutableCollection<in R>> Stream<T>.flatMapTo(result: C, transform: (T) -> Iterable<R>) : C {
    for (element in this) {
        val list = transform(element)
        result.addAll(list)
    }
    return result
    
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single stream
 */
public fun <T, R, C: MutableCollection<in R>> Stream<T>.flatMapTo(result: C, transform: (T) -> Stream<R>) : C {
    for (element in this) {
        val list = transform(element)
        result.addAll(list)
    }
    return result
    
}

/**
 * Groups the elements in the collection into a new [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 */
public fun <T, K> Stream<T>.groupBy(toKey: (T) -> K) : Map<K, List<T>> {
    return groupByTo(HashMap<K, MutableList<T>>(), toKey)
}

/**
 * Groups the elements in the collection into a new [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 */
public fun <T, K> Iterable<T>.groupBy(toKey: (T) -> K) : Map<K, List<T>> {
    return groupByTo(HashMap<K, MutableList<T>>(), toKey)
}

/**
 * Groups the elements in the collection into a new [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 */
public fun <T, K> Array<T>.groupBy(toKey: (T) -> K) : Map<K, List<T>> {
    return groupByTo(HashMap<K, MutableList<T>>(), toKey)
}

/**
 * Groups the elements in the collection into a new [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 */
public fun <K> ByteArray.groupBy(toKey: (Byte) -> K) : Map<K, List<Byte>> {
    return groupByTo(HashMap<K, MutableList<Byte>>(), toKey)
}

/**
 * Groups the elements in the collection into a new [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 */
public fun <K> CharArray.groupBy(toKey: (Char) -> K) : Map<K, List<Char>> {
    return groupByTo(HashMap<K, MutableList<Char>>(), toKey)
}

/**
 * Groups the elements in the collection into a new [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 */
public fun <K> DoubleArray.groupBy(toKey: (Double) -> K) : Map<K, List<Double>> {
    return groupByTo(HashMap<K, MutableList<Double>>(), toKey)
}

/**
 * Groups the elements in the collection into a new [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 */
public fun <K> LongArray.groupBy(toKey: (Long) -> K) : Map<K, List<Long>> {
    return groupByTo(HashMap<K, MutableList<Long>>(), toKey)
}

/**
 * Groups the elements in the collection into a new [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 */
public fun <K> IntArray.groupBy(toKey: (Int) -> K) : Map<K, List<Int>> {
    return groupByTo(HashMap<K, MutableList<Int>>(), toKey)
}

/**
 * Groups the elements in the collection into a new [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 */
public fun <K> ShortArray.groupBy(toKey: (Short) -> K) : Map<K, List<Short>> {
    return groupByTo(HashMap<K, MutableList<Short>>(), toKey)
}

/**
 * Groups the elements in the collection into a new [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 */
public fun <K> BooleanArray.groupBy(toKey: (Boolean) -> K) : Map<K, List<Boolean>> {
    return groupByTo(HashMap<K, MutableList<Boolean>>(), toKey)
}

/**
 * Groups the elements in the collection into a new [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 */
public fun <K> FloatArray.groupBy(toKey: (Float) -> K) : Map<K, List<Float>> {
    return groupByTo(HashMap<K, MutableList<Float>>(), toKey)
}

public fun <T, K> Stream<T>.groupByTo(result: MutableMap<K, MutableList<T>>, toKey: (T) -> K) : Map<K, MutableList<T>> {
    for (element in this) {
        val key = toKey(element)
        val list = result.getOrPut(key) { ArrayList<T>() }
        list.add(element)
    }
    return result
    
}

public fun <T, K> Iterable<T>.groupByTo(result: MutableMap<K, MutableList<T>>, toKey: (T) -> K) : Map<K, MutableList<T>> {
    for (element in this) {
        val key = toKey(element)
        val list = result.getOrPut(key) { ArrayList<T>() }
        list.add(element)
    }
    return result
    
}

public fun <T, K> Array<T>.groupByTo(result: MutableMap<K, MutableList<T>>, toKey: (T) -> K) : Map<K, MutableList<T>> {
    for (element in this) {
        val key = toKey(element)
        val list = result.getOrPut(key) { ArrayList<T>() }
        list.add(element)
    }
    return result
    
}

public fun <K> ByteArray.groupByTo(result: MutableMap<K, MutableList<Byte>>, toKey: (Byte) -> K) : Map<K, MutableList<Byte>> {
    for (element in this) {
        val key = toKey(element)
        val list = result.getOrPut(key) { ArrayList<Byte>() }
        list.add(element)
    }
    return result
    
}

public fun <K> CharArray.groupByTo(result: MutableMap<K, MutableList<Char>>, toKey: (Char) -> K) : Map<K, MutableList<Char>> {
    for (element in this) {
        val key = toKey(element)
        val list = result.getOrPut(key) { ArrayList<Char>() }
        list.add(element)
    }
    return result
    
}

public fun <K> DoubleArray.groupByTo(result: MutableMap<K, MutableList<Double>>, toKey: (Double) -> K) : Map<K, MutableList<Double>> {
    for (element in this) {
        val key = toKey(element)
        val list = result.getOrPut(key) { ArrayList<Double>() }
        list.add(element)
    }
    return result
    
}

public fun <K> LongArray.groupByTo(result: MutableMap<K, MutableList<Long>>, toKey: (Long) -> K) : Map<K, MutableList<Long>> {
    for (element in this) {
        val key = toKey(element)
        val list = result.getOrPut(key) { ArrayList<Long>() }
        list.add(element)
    }
    return result
    
}

public fun <K> IntArray.groupByTo(result: MutableMap<K, MutableList<Int>>, toKey: (Int) -> K) : Map<K, MutableList<Int>> {
    for (element in this) {
        val key = toKey(element)
        val list = result.getOrPut(key) { ArrayList<Int>() }
        list.add(element)
    }
    return result
    
}

public fun <K> ShortArray.groupByTo(result: MutableMap<K, MutableList<Short>>, toKey: (Short) -> K) : Map<K, MutableList<Short>> {
    for (element in this) {
        val key = toKey(element)
        val list = result.getOrPut(key) { ArrayList<Short>() }
        list.add(element)
    }
    return result
    
}

public fun <K> BooleanArray.groupByTo(result: MutableMap<K, MutableList<Boolean>>, toKey: (Boolean) -> K) : Map<K, MutableList<Boolean>> {
    for (element in this) {
        val key = toKey(element)
        val list = result.getOrPut(key) { ArrayList<Boolean>() }
        list.add(element)
    }
    return result
    
}

public fun <K> FloatArray.groupByTo(result: MutableMap<K, MutableList<Float>>, toKey: (Float) -> K) : Map<K, MutableList<Float>> {
    for (element in this) {
        val key = toKey(element)
        val list = result.getOrPut(key) { ArrayList<Float>() }
        list.add(element)
    }
    return result
    
}

/**
 * Returns a new List containing the results of applying the given *transform* function to each element in this collection
 */
public fun <T, R> Stream<T>.map(transform : (T) -> R) : Stream<R> {
    return TransformingStream(this, transform) 
}

/**
 * Returns a new List containing the results of applying the given *transform* function to each element in this collection
 */
public fun <T, R> Iterable<T>.map(transform : (T) -> R) : List<R> {
    return mapTo(ArrayList<R>(), transform)
}

/**
 * Returns a new List containing the results of applying the given *transform* function to each element in this collection
 */
public fun <T, R> Array<T>.map(transform : (T) -> R) : List<R> {
    return mapTo(ArrayList<R>(), transform)
}

/**
 * Returns a new List containing the results of applying the given *transform* function to each element in this collection
 */
public fun <R> ByteArray.map(transform : (Byte) -> R) : List<R> {
    return mapTo(ArrayList<R>(), transform)
}

/**
 * Returns a new List containing the results of applying the given *transform* function to each element in this collection
 */
public fun <R> CharArray.map(transform : (Char) -> R) : List<R> {
    return mapTo(ArrayList<R>(), transform)
}

/**
 * Returns a new List containing the results of applying the given *transform* function to each element in this collection
 */
public fun <R> DoubleArray.map(transform : (Double) -> R) : List<R> {
    return mapTo(ArrayList<R>(), transform)
}

/**
 * Returns a new List containing the results of applying the given *transform* function to each element in this collection
 */
public fun <R> LongArray.map(transform : (Long) -> R) : List<R> {
    return mapTo(ArrayList<R>(), transform)
}

/**
 * Returns a new List containing the results of applying the given *transform* function to each element in this collection
 */
public fun <R> IntArray.map(transform : (Int) -> R) : List<R> {
    return mapTo(ArrayList<R>(), transform)
}

/**
 * Returns a new List containing the results of applying the given *transform* function to each element in this collection
 */
public fun <R> ShortArray.map(transform : (Short) -> R) : List<R> {
    return mapTo(ArrayList<R>(), transform)
}

/**
 * Returns a new List containing the results of applying the given *transform* function to each element in this collection
 */
public fun <R> BooleanArray.map(transform : (Boolean) -> R) : List<R> {
    return mapTo(ArrayList<R>(), transform)
}

/**
 * Returns a new List containing the results of applying the given *transform* function to each element in this collection
 */
public fun <R> FloatArray.map(transform : (Float) -> R) : List<R> {
    return mapTo(ArrayList<R>(), transform)
}

/**
 * Transforms each element of this collection with the given *transform* function and
 * adds each return value to the given *results* collection
 */
public fun <T, R, C: MutableCollection<in R>> Stream<T>.mapTo(result: C, transform : (T) -> R) : C {
    for (item in this)
        result.add(transform(item))
    return result
    
}

/**
 * Transforms each element of this collection with the given *transform* function and
 * adds each return value to the given *results* collection
 */
public fun <T, R, C: MutableCollection<in R>> Iterable<T>.mapTo(result: C, transform : (T) -> R) : C {
    for (item in this)
        result.add(transform(item))
    return result
    
}

/**
 * Transforms each element of this collection with the given *transform* function and
 * adds each return value to the given *results* collection
 */
public fun <T, R, C: MutableCollection<in R>> Array<T>.mapTo(result: C, transform : (T) -> R) : C {
    for (item in this)
        result.add(transform(item))
    return result
    
}

/**
 * Transforms each element of this collection with the given *transform* function and
 * adds each return value to the given *results* collection
 */
public fun <R, C: MutableCollection<in R>> ByteArray.mapTo(result: C, transform : (Byte) -> R) : C {
    for (item in this)
        result.add(transform(item))
    return result
    
}

/**
 * Transforms each element of this collection with the given *transform* function and
 * adds each return value to the given *results* collection
 */
public fun <R, C: MutableCollection<in R>> CharArray.mapTo(result: C, transform : (Char) -> R) : C {
    for (item in this)
        result.add(transform(item))
    return result
    
}

/**
 * Transforms each element of this collection with the given *transform* function and
 * adds each return value to the given *results* collection
 */
public fun <R, C: MutableCollection<in R>> DoubleArray.mapTo(result: C, transform : (Double) -> R) : C {
    for (item in this)
        result.add(transform(item))
    return result
    
}

/**
 * Transforms each element of this collection with the given *transform* function and
 * adds each return value to the given *results* collection
 */
public fun <R, C: MutableCollection<in R>> LongArray.mapTo(result: C, transform : (Long) -> R) : C {
    for (item in this)
        result.add(transform(item))
    return result
    
}

/**
 * Transforms each element of this collection with the given *transform* function and
 * adds each return value to the given *results* collection
 */
public fun <R, C: MutableCollection<in R>> IntArray.mapTo(result: C, transform : (Int) -> R) : C {
    for (item in this)
        result.add(transform(item))
    return result
    
}

/**
 * Transforms each element of this collection with the given *transform* function and
 * adds each return value to the given *results* collection
 */
public fun <R, C: MutableCollection<in R>> ShortArray.mapTo(result: C, transform : (Short) -> R) : C {
    for (item in this)
        result.add(transform(item))
    return result
    
}

/**
 * Transforms each element of this collection with the given *transform* function and
 * adds each return value to the given *results* collection
 */
public fun <R, C: MutableCollection<in R>> BooleanArray.mapTo(result: C, transform : (Boolean) -> R) : C {
    for (item in this)
        result.add(transform(item))
    return result
    
}

/**
 * Transforms each element of this collection with the given *transform* function and
 * adds each return value to the given *results* collection
 */
public fun <R, C: MutableCollection<in R>> FloatArray.mapTo(result: C, transform : (Float) -> R) : C {
    for (item in this)
        result.add(transform(item))
    return result
    
}

