// !DIAGNOSTICS: -UNUSED_VARIABLE
annotation class Ann(vararg val i: String)

val topLevel = "topLevel"

fun foo() {
    val a1 = "a"
    val a2 = "b"
    val a3 = a1 + a2

    val a4 = 1
    val a5 = 1.0

    [Ann(
            <!ANNOTATION_PARAMETER_MUST_BE_CONST!>a1<!>,
            <!ANNOTATION_PARAMETER_MUST_BE_CONST!>a2<!>,
            <!ANNOTATION_PARAMETER_MUST_BE_CONST!>a3<!>,
            "$topLevel",
            <!ANNOTATION_PARAMETER_MUST_BE_CONST!>"$a1"<!>,
            <!ANNOTATION_PARAMETER_MUST_BE_CONST!>"$a1 $topLevel"<!>,
            <!ANNOTATION_PARAMETER_MUST_BE_CONST!>"$a4"<!>,
            <!ANNOTATION_PARAMETER_MUST_BE_CONST!>"$a5"<!>
    )] val b = 1
}