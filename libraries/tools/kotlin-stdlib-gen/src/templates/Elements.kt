package templates

import templates.Family.*

fun elements(): List<GenericFunction> {
    val templates = arrayListOf<GenericFunction>()

    templates add f("indexOf(item: T)") {
        doc { "Returns first index of item, or -1 if the array does not contain item" }
        returns("Int")

        body {
            """
            var index = 0
            for (element in this) {
                if (element == item)
                    return index
                index++
            }
            return -1
            """
        }

        body(ArraysOfObjects) {
            """
            if (item == null) {
                for (i in indices) {
                    if (this[i] == null) {
                        return i
                    }
                }
            } else {
                for (i in indices) {
                    if (item == this[i]) {
                        return i
                    }
                }
            }
            return -1
           """
        }
        body(ArraysOfPrimitives) {
            """
            for (i in indices) {
                if (item == this[i]) {
                    return i
                }
            }
            return -1
           """
        }
    }

    templates add f("elementAt(index : Int)") {
        doc { "Returns element at index" }
        returns("T")
        body {
            """
            if (this is List<*>)
                return get(index) as T
            val iterator = iterator()
            var count = 0
            while (iterator.hasNext()) {
                val element = iterator.next()
                if (index == count++)
                    return element
            }
            throw IndexOutOfBoundsException("Collection doesn't contain element at index ")

            """
        }
        body(Streams) {
            """
            val iterator = iterator()
            var count = 0
            while (iterator.hasNext()) {
                val element = iterator.next()
                if (index == count++)
                    return element
            }
            throw IndexOutOfBoundsException("Collection doesn't contain element at index ")

            """
        }
        body(Lists, ArraysOfObjects, ArraysOfPrimitives) {
            """
            return get(index)
            """
        }
    }

    templates add f("first()") {
        doc { "Returns first element" }
        returns("T")
        body {
            """
            val iterator = iterator()
            if (!iterator.hasNext())
                throw IllegalArgumentException("Collection is empty")
            return iterator.next()
            """
        }
        body(Lists, ArraysOfObjects, ArraysOfPrimitives) {
            """
            return this[0]
            """
        }
    }

    templates add f("first(predicate: (T) -> Boolean)") {
        doc { "Returns first element matching the given *predicate*" }
        returns("T")
        body {
            """
            for (element in this) if (predicate(element)) return element
            throw IllegalArgumentException("No element matching predicate was found")
            """
        }
    }

    templates add f("firstOrNull(predicate: (T) -> Boolean)") {
        doc { "Returns first element matching the given *predicate*, or *null* if element was not found" }
        returns("T?")
        body {
            """
            for (element in this) if (predicate(element)) return element
            return null
            """
        }
    }

    templates add f("last()") {
        doc { "Returns last element" }
        returns("T")
        body {
            """
            when (this) {
                is List<*> -> return this[size - 1] as T
                else -> {
                    val iterator = iterator()
                    if (!iterator.hasNext())
                        throw IllegalArgumentException("Collection is empty")
                    var last = iterator.next()
                    while (iterator.hasNext())
                        last = iterator.next()
                    return last
                }
            }
            """
        }
        body(Lists, ArraysOfObjects, ArraysOfPrimitives) {
            """
            if (size == 0)
                throw IllegalArgumentException("Collection is empty")
            return this[size - 1]
            """
        }
    }

    templates add f("last(predicate: (T) -> Boolean)") {
        doc { "Returns last element matching the given *predicate*" }
        returns("T")
        body {
            """
            fun Iterator<T>.first() : T {
                for (element in this) if (predicate(element)) return element
                throw IllegalArgumentException("Collection doesn't have matching element")
            }
            val iterator = iterator()
            var last = iterator.first()
            while (iterator.hasNext()) {
                val element = iterator.next()
                if (predicate(element))
                    last = element
            }
            return last
            """
        }
    }

    templates add f("lastOrNull(predicate: (T) -> Boolean)") {
        doc { "Returns last element matching the given *predicate*, or null if element was not found" }
        returns("T?")
        body {
            """
            fun Iterator<T>.first() : T? {
                for (element in this) if (predicate(element)) return element
                return null
            }
            val iterator = iterator()
            var last = iterator.first()
            if (last == null)
                return null
            while (iterator.hasNext()) {
                val element = iterator.next()
                if (predicate(element))
                    last = element
            }
            return last
            """
        }
    }

    val bucks = '$'
    templates add f("single()") {
        doc { "Returns single element" }
        returns("T")
        body {
            """
            when (this) {
                is List<*> -> return if (size == 1) this[0] as T else throw IllegalArgumentException("Collection has ${bucks}size elements")
                else -> {
                    val iterator = iterator()
                    if (!iterator.hasNext())
                        throw IllegalArgumentException("Collection is empty")
                    var single = iterator.next()
                    if (iterator.hasNext())
                        throw IllegalArgumentException("Collection has more than one element")
                    return single
                }
            }
            """
        }
        body(ArraysOfObjects, ArraysOfPrimitives) {
            """
            if (size != 1)
                throw IllegalArgumentException("Collection has ${bucks}size elements")
            return this[0]
            """
        }
    }

    templates add f("single(predicate: (T) -> Boolean)") {
        doc { "Returns single element matching the given *predicate*" }
        returns("T")
        body {
            """
            fun Iterator<T>.first() : T {
                for (element in this) if (predicate(element)) return element
                throw IllegalArgumentException("Collection doesn't have matching element")
            }
            val iterator = iterator()
            var single = iterator.first()
            while (iterator.hasNext()) {
                val element = iterator.next()
                if (predicate(element))
                    throw IllegalArgumentException("Collection has more than one matching element")
            }
            return single
            """
        }
    }

    templates add f("singleOrNull(predicate: (T) -> Boolean)") {
        doc { "Returns single element matching the given *predicate*, or null if element was not found" }
        returns("T?")
        body {
            """
            fun Iterator<T>.first() : T? {
                for (element in this) if (predicate(element)) return element
                return null
            }
            val iterator = iterator()
            var single = iterator.first()
            if (single == null)
                return null
            while (iterator.hasNext()) {
                val element = iterator.next()
                if (predicate(element))
                    throw IllegalArgumentException("Collection has more than one matching element")
            }
            return single
            """
        }
    }

    return templates
}