package com.weather.demo.viewmodel

import androidx.lifecycle.ViewModel
import com.weather.demo.webservice.ResultWrapper
import com.weather.demo.webservice.safeApiCall
import com.weather.demo.Controller
import com.weather.demo.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private val job = Job()

    protected fun <T> callApiAndShowDialog(
        call: suspend () -> Response<T>,
        handleSuccess: (T) -> Unit,
        handleGeneric: (String) -> Unit = {},
        handleNetwork: (String) -> Unit = {},
        handleNoContent: (String) -> Unit = {},
        handleSessionExpiredError: (String) -> Unit = {},
        showDialg: Boolean = true,
        handledGenerinc: Boolean = false
    ) {
        launch {
            if (Utils.hasInternet(Controller.instance.applicationContext)) {
                if (showDialg) Controller.instance.context?.showProgress()
                val result = safeApiCall(Dispatchers.IO) {
                    call.invoke()
                }

                if (showDialg) Controller.instance.context?.hideProgress()

                when (result) {
                    is ResultWrapper.NoContent -> {
                        handleNoContent(result.error ?: "Something went wrong")
                    }
                    is ResultWrapper.NetworkError -> {
                        handleNetwork(result.error ?: "Something went wrong")
                    }
                    is ResultWrapper.GenericError -> {
                        if (handledGenerinc) {
                            handleGeneric(result.error ?: "Something went wrong")
                        } else {
                            handleGeneric(result.error ?: "Something went wrong")
                        }
                    }

                    is ResultWrapper.Success -> {
                        result.value.body()?.let {
                            handleSuccess(it)
                        }
                    }
                    is ResultWrapper.ServerError -> {
                        Controller.instance.context?.let {
                        }
                    }
                    else -> {
                        handleGeneric("Something went wrong")
                    }
                }
            }
        }
    }
}
