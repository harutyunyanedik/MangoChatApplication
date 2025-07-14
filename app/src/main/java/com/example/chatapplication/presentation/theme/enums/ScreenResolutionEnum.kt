package com.example.chatapplication.presentation.theme.enums

enum class ScreenResolutionEnum(private val range: IntRange) {
    XHdpi(0..240),
    XXHdpi(241..400),
    XXXHdpi(401..600);

    companion object {
        private val map = entries.associateBy(ScreenResolutionEnum::range)
        fun from(value: IntRange) = map[value] ?: XXHdpi

        operator fun get(value: Int) = entries.find {
            value in it.range
        } ?: XXHdpi
    }
}