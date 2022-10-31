package com.lis.vkaudiotoken

import com.lis.vkaudiotoken.network.RetrofitService
import com.lis.vkaudiotoken.network.VK_OFFICIAL

internal class TwoFAHelper {
    suspend fun validatePhone(validationSid: String){
        val retrofitService = RetrofitService.create(VK_OFFICIAL.userAgent, "https://api.vk.com/method/")

        val response = retrofitService.validatePhone(validationSid)

        if(!response.isSuccessful){
            throw TokenException(TokenExceptionType.TWO_FA_ERR, message = "")
        }
    }
}