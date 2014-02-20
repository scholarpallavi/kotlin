package h

class RightSquare() {
    var size : Double = 1.0
        set(value) {
            $area = size * size
        }

    var area: Double = 0.0
        private set
}

class Square() {
    var size : Double =
    <!UNRESOLVED_REFERENCE!>set<!>(<!UNRESOLVED_REFERENCE!>value<!>) {
        <!INITIALIZATION_BEFORE_DECLARATION!>$area<!> = size * size
    }

    <!MUST_BE_INITIALIZED_OR_BE_ABSTRACT!>var area : Double<!>
        private set
}

fun main(args : Array<String>) {
    val s = Square()

    s.size = 2.0
}