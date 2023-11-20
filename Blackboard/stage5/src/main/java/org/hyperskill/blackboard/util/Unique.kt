package org.hyperskill.blackboard.util

class Unique<T>(val value:T) {

    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return value.toString()
    }
}