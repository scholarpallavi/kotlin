class Point(val x: Int, val y: Int)

fun box(): String {
    val answer = apply(Point(3, 5), { Point.(scalar: Int): Point ->
        Point(x * scalar, y * scalar)
    })

    return if (answer.x == 6 && answer.y == 10) "OK" else "FAIL"
}

fun apply(arg: Point, f: Point.(scalar: Int) -> Point): Point {
    return arg.f(2)
}
