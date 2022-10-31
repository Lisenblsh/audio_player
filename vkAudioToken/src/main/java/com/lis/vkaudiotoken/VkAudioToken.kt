package com.lis.vkaudiotoken

class VkAudioToken(private val login: String, private val password: String) {
    suspend fun getToken(
        code: String? = null,
        captchaSid: String? = null,
        captchaKey: String? = null
    ): Pair<String,String> {
        val receiverOfficial = TokenReceiverOfficial(login, password)
        return receiverOfficial.getToken(code, captchaSid, captchaKey)
    }
}