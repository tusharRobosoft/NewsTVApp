package com.example.common.network

import com.example.demo.views.utils.Constants
import java.io.IOException

class ApiException(code: Int? = Constants.NetworkConstants.DEFAULT_ERROR_CODE, message: String?) :
    IOException(message)