fun test(n: Int): String {
    val <caret>res: kotlin.String = if (n == 1) "one" else "two"

    return res
}