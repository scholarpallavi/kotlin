package o

fun foo() {
    val <!ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE!>x<!>: Int
    val <!UNUSED_VARIABLE!>s<!> = {
    <!INITIALIZATION_FROM_LOCAL_FUNCTION!>x<!> = 23
}
}

fun bar() {
    val <!ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE!>x<!>: Int
    fun local() {
        <!INITIALIZATION_FROM_LOCAL_FUNCTION!>x<!> = 42
    }
}

fun baz() {
    val <!ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE!>x<!>: Int = 1
    fun local() {
        <!VAL_REASSIGNMENT!>x<!> = 2
    }
}