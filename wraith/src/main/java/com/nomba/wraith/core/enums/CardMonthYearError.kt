package com.nomba.wraith.core.enums

enum class CardMonthYearError {
    EMPTY,
    MONTH_REQUIRED,
    MONTH_INVALID,
    YEAR_REQUIRED,
    YEAR_OVER_20_YEARS_LATER,
    YEAR_INVALID,
    EXPIRED,
    NONE,
}