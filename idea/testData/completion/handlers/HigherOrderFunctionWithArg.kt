fun <T> kotlin.Array<T>.filter(predicate : (T) -> kotlin.Boolean) : java.util.List<T> = throw UnsupportedOperationException()

fun <T> kotlin.Array<T>.filterNot(predicate : (T) -> kotlin.Boolean) : java.util.List<T> = throw UnsupportedOperationException()

fun main(args: Array<String>) {
    args.filter<caret> {it != ""}
}
