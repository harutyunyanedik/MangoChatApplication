package com.example.chatapplication.data

import com.example.chatapplication.common.utils.net.model.ApiWrapper
import com.example.chatapplication.common.utils.net.parseHttpResponse
import com.example.chatapplication.data.apiservice.ChatApiService
import com.example.chatapplication.data.model.checkcode.CheckAuthCodeDto
import com.example.chatapplication.data.model.checkcode.CheckAuthCodeRequest
import com.example.chatapplication.data.model.me.MeDto
import com.example.chatapplication.data.model.register.RegistrationDto
import com.example.chatapplication.data.model.register.RegistrationRequest
import com.example.chatapplication.data.model.sendcode.SendAuthCodeDto
import com.example.chatapplication.data.model.sendcode.SendAuthCodeRequest

class ChatNetworkAdapter(
    private val chatApiService: ChatApiService
) : ChatNetworkPort {

    override suspend fun sendAuthCode(phone: String): ApiWrapper<SendAuthCodeDto?> {
        return parseHttpResponse {
            chatApiService.sendAuthCode(SendAuthCodeRequest(phone))
        }
    }

    override suspend fun checkAuthCode(phone: String, code: String): ApiWrapper<CheckAuthCodeDto?> {
        return parseHttpResponse {
            chatApiService.checkAuthCode(CheckAuthCodeRequest(phone, code))
        }
    }

    override suspend fun register(name: String, phone: String, username: String): ApiWrapper<RegistrationDto?> {
        return parseHttpResponse {
            chatApiService.register(RegistrationRequest(phone, name, username))
        }
    }

    override suspend fun me(): ApiWrapper<MeDto?> {
        return parseHttpResponse {
            chatApiService.me()
        }
    }
}