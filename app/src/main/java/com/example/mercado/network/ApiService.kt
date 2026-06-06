package com.example.mercado.network

import com.example.mercado.model.CadastroRequest
import com.example.mercado.model.LoginRequest
import com.example.mercado.model.Produto
import com.example.mercado.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {

    @POST("usuarios/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Usuario

    @POST("usuarios/cadastro")
    suspend fun cadastro(
        @Body request: CadastroRequest
    ): Response<Unit>

    @GET("produtos")
    suspend fun produtos(): List<Produto>
}