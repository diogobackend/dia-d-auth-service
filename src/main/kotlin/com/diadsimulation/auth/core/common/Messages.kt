package com.diadsimulation.auth.core.common

object Messages {

    // Requisições e erros HTTP
    const val REQUEST_CONFLICT = "Conflito ao processar a requisição"
    const val INVALID_REQUEST_DATA = "Dados da requisição inválidos"
    const val INTERNAL_SERVER_ERROR_MESSAGE = "Erro interno do servidor"

    // Validação de campos
    const val EMAIL_REQUIRED = "E-mail é obrigatório"
    const val EMAIL_INVALID = "E-mail é inválido"
    const val PASSWORD_REQUIRED = "Senha é obrigatório"

    // Regras de negócio
    const val USER_ALREADY_EXISTS = "Já existe um usuário cadastrado com o "

    // Erros de infraestrutura
    const val PASSWORD_ENCODING_FAILED = "Falha ao codificar a senha"
    const val PERSISTENCE_FAILED = "Persistence failed"
    const val SENSITIVE_DATABASE_INFORMATION = "Sensitive database information"

    // Identificadores de requisição
    const val REGISTER_USER_REQUEST = "registerUserRequest"
}