package com.diadsimulation.auth.core.common

object Messages {
    // Requisições HTTP
    const val INVALID_REQUEST_DATA = "Dados da requisição inválidos"
    const val REQUEST_CONFLICT = "Conflito ao processar a requisição"
    const val INTERNAL_SERVER_ERROR_MESSAGE = "Erro interno do servidor"

    // Validação de campos obrigatórios
    const val NAME_REQUIRED = "Nome é obrigatório"
    const val EMAIL_REQUIRED = "E-mail é obrigatório"
    const val PASSWORD_REQUIRED = "Senha é obrigatório"
    const val ROLE_REQUIRED = "Perfil é obrigatório"

    // Validação de formato e conteúdo
    const val EMAIL_INVALID = "E-mail é inválido"
    const val BIRTH_DATE_FUTURE = "Data de nascimento não pode ser futura"
    const val BIRTH_DATE_INVALID_FORMAT = "Data de nascimento inválida. Utilize o formato yyyy-MM-dd"

    // Validação de parâmetros
    const val INVALID_USER_UPDATE_PARAMS = "Informe pelo menos um campo para atualização"
    const val INVALID_USER_SEARCH_PARAMS =
        "Informe apenas um parâmetro: userId, email, cpf ou rg"

    // Validação de paginação
    const val PAGE_CANNOT_BE_NEGATIVE = "A página não pode ser negativa"
    const val PAGE_SIZE_MIN_INVALID = "O tamanho mínimo da página é 1"
    const val PAGE_SIZE_MAX_INVALID = "O tamanho máximo da página é 100"

    // Validação de ordenação e filtros
    const val INVALID_SORT_FIELD = "Campo de ordenação inválido"
    const val INVALID_FILTER_FIELD = "Campo de filtro inválido"
    const val FILTER_PARAMETERS_REQUIRED = "filterBy e filterValue devem ser informados juntos"
    const val DATE_FIELD_REQUIRED = "O campo dateField é obrigatório ao informar filtros de data."

    // Regras de negócio
    const val USER_ALREADY_EXISTS = "Já existe um usuário cadastrado com o "
    const val EMAIL_ALREADY_EXISTS = "E-mail já cadastrado"
    const val USER_NOT_FOUND = "Usuário não encontrado"

    // Erros de infraestrutura
    const val PASSWORD_ENCODING_FAILED = "Falha ao codificar a senha"
    const val PERSISTENCE_FAILED = "Persistence failed"
    const val SENSITIVE_DATABASE_INFORMATION = "Sensitive database information"

    // Identificadores de requisição
    const val REGISTER_USER_REQUEST = "registerUserRequest"
}
