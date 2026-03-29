package com.knotworking.schengen.feature.schengen.domain

import com.knotworking.schengen.core.domain.Error

sealed interface SchengenError : Error {
    data object InvalidDateRange : SchengenError  // exit date before entry date
    data object Unknown : SchengenError
}
