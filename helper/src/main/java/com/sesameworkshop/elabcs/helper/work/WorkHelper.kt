package com.sesameworkshop.elabcs.helper.work

import com.sesameworkshop.elabcs.helper.work.model.MainValues

interface WorkHelper {
    suspend fun startMainWork(
        mainValues: MainValues,
        infoTitle: String,
        callBack: (url: String, isSavedCache : Boolean) -> Unit
    )
}