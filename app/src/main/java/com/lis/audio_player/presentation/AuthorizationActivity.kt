package com.lis.audio_player.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.lis.audio_player.R
import com.lis.audio_player.databinding.ActivityAuthorizationBinding
import com.lis.audio_player.domain.tools.ImageLoader
import com.lis.vkaudiotoken.TokenException
import com.lis.vkaudiotoken.TokenExceptionType
import com.lis.vkaudiotoken.VkAudioToken
import kotlinx.coroutines.launch

class AuthorizationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorizationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        binding.bindElement()
        setContentView(binding.root)
    }

    private fun ActivityAuthorizationBinding.bindElement() {
        getTokenButton.setOnClickListener {
            lifecycleScope.launch {
                confirmListenerClick()
            }
        }
    }

    private var captchaSid: String? = null
    private suspend fun ActivityAuthorizationBinding.confirmListenerClick() {
        val login = usernameEditText.text.toString().ifEmpty {
            showToast(resources.getString(R.string.login_hint))
            return
        }
        val password = passwordEditText.text.toString().ifEmpty {
            showToast(resources.getString(R.string.password_hint))
            return
        }
        val code = codeEditText.text.toString()
            .ifEmpty {
                if (codeEditText.visibility == View.GONE) null else {
                    showToast("code")
                    return
                }
            }
        val captchaKey = captchaEditText.text.toString()
            .ifEmpty {
                if (captchaLayout.visibility == View.GONE) null else {
                    showToast("captcha")
                    return
                }
            }
        val tokenReceiver = VkAudioToken(login, password)
        try {
            val token = tokenReceiver.getToken(code, captchaSid, captchaKey)
            saveAuthInfo(token.first, token.second, token.third)
            val intent = Intent(this@AuthorizationActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: TokenException) {
            when (e.code) {
                TokenExceptionType.REGISTRATION_ERROR -> {
                    errorMessage.text = e.message
                    errorMessage.visibility = View.VISIBLE
                }
                TokenExceptionType.TWO_FA_REQ -> {
                    errorMessage.text = resources.getString(R.string.sms_sent)
                    errorMessage.visibility = View.VISIBLE
                    codeEditText.visibility = View.VISIBLE
                }
                TokenExceptionType.TWO_FA_ERR -> {
                    errorMessage.text = resources.getString(R.string.two_fa_error)
                    errorMessage.visibility = View.VISIBLE
                    hideElement()
                }
                TokenExceptionType.REQUEST_ERR -> {
                    errorMessage.text = resources.getString(R.string.request_error)
                    errorMessage.visibility = View.VISIBLE
                    hideElement()
                }
                TokenExceptionType.NEED_CAPTCHA -> {
                    captchaSid = e.captchaSid
                    errorMessage.text = e.message
                    errorMessage.visibility = View.VISIBLE
                    ImageLoader().setImage(e.captchaImg, captchaImage)
                    captchaLayout.visibility = View.VISIBLE
                }
                TokenExceptionType.TOKEN_NOT_RECEIVED -> {
                    errorMessage.text = resources.getString(R.string.token_not_sent)
                    errorMessage.visibility = View.VISIBLE
                    hideElement()
                }
            }
        }

    }

    private fun showToast(field: String) {
        Toast.makeText(this, "Поле: $field не заполненно", Toast.LENGTH_LONG).show()
    }

    private fun ActivityAuthorizationBinding.hideElement() {
        codeEditText.visibility = View.GONE
        captchaLayout.visibility = View.GONE
    }

    private fun saveAuthInfo(token: String, userId: Long, userAgent: String) {
        val preferences =
            getSharedPreferences(getString(R.string.authorization_info), Context.MODE_PRIVATE)

        if (preferences != null) {
            with(preferences.edit()) {
                putString(getString(R.string.token_key), token)
                putLong(getString(R.string.user_id), userId)
                putString(getString(R.string.user_agent), userAgent)
                apply()
            }
        }
    }
}
