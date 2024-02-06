package com.nara.mia.mobile.infrastructure

class Signal {
    private val slots = mutableSetOf<() -> Unit>()

    operator fun plusAssign(slot: () -> Unit) {
        slots.add(slot)
    }

    operator fun minusAssign(slot: () -> Unit) {
        slots.remove(slot)
    }

    operator fun invoke() {
        for (slot in slots) {
            slot()
        }
    }
}