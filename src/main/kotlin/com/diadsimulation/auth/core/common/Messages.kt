package com.diadsimulation.auth.core.common

object Messages {
    // Requisições HTTP
    const val INVALID_REQUEST_DATA = "Dados da requisição inválidos"
    const val REQUEST_CONFLICT = "Conflito ao processar a requisição"
    const val INTERNAL_SERVER_ERROR_MESSAGE = "Erro interno do servidor"

    // Campos obrigatórios
    const val NAME_REQUIRED = "Nome é obrigatório"
    const val EMAIL_REQUIRED = "E-mail é obrigatório"
    const val LOGIN_REQUIRED = "Login é obrigatório"
    const val PASSWORD_REQUIRED = "Senha é obrigatória"
    const val ROLE_REQUIRED = "Perfil é obrigatório"
    const val PHONE_REQUIRED = "Telefone não pode ser vazio"
    const val CPF_REQUIRED = "CPF não pode ser vazio"
    const val RG_REQUIRED = "RG não pode ser vazio"

    // Formatos e validações
    const val EMAIL_INVALID = "E-mail é inválido"
    const val PHONE_INVALID = "Telefone inválido"
    const val CPF_INVALID = "CPF inválido"
    const val RG_INVALID = "RG inválido"
    const val LOGIN_LOWERCASE_REQUIRED = "O login deve conter apenas letras minúsculas"
    const val LOGIN_MUST_START_WITH_LETTER = "O login deve começar com uma letra minúscula"
    const val LOGIN_WHITESPACE_NOT_ALLOWED = "O login não pode conter espaços em branco"
    const val BIRTH_DATE_FUTURE = "Data de nascimento não pode ser futura"
    const val BIRTH_DATE_INVALID_FORMAT = "Data de nascimento inválida. Utilize o formato yyyy-MM-dd"

    // Validação de tamanho dos campos
    const val NAME_MAX_LENGTH = "Nome deve possuir no máximo 150 caracteres"
    const val EMAIL_MAX_LENGTH = "E-mail deve possuir no máximo 255 caracteres"
    const val LOGIN_LENGTH = "O login deve conter entre 3 e 35 caracteres"
    const val PASSWORD_LENGTH = "Senha deve possuir entre 8 e 100 caracteres"
    const val PHONE_MAX_LENGTH = "Telefone deve possuir no máximo 30 caracteres"
    const val ADDRESS_MAX_LENGTH = "Endereço deve possuir no máximo 500 caracteres"
    const val CPF_MAX_LENGTH = "CPF deve possuir no máximo 14 caracteres"
    const val RG_MAX_LENGTH = "RG deve possuir no máximo 20 caracteres"

    // Regras de negócio - Usuário / E-mail / Login / CPF / RG /
    const val USER_ALREADY_EXISTS = "Já existe um usuário cadastrado com o "
    const val USER_NOT_FOUND = "Usuário não encontrado"
    const val EMAIL_ALREADY_EXISTS = "E-mail já cadastrado"
    const val LOGIN_ALREADY_EXISTS = "Login já cadastrado"
    const val CPF_ALREADY_EXISTS = "CPF já existe"
    const val CPF_ALREADY_REGISTERED = "Você não pode alterar o CPF"
    const val RG_ALREADY_EXISTS = "RG já existe"
    const val RG_ALREADY_REGISTERED = "Você não pode alterar o RG"

    // Parâmetros de requisição
    const val INVALID_USER_UPDATE_PARAMS = "Informe pelo menos um campo para atualização"
    const val INVALID_USER_SEARCH_PARAMS = "Informe apenas um parâmetro: userId, email, login, cpf ou rg"
    const val BIRTH_DATE_ALREADY_REGISTERED = "A data de nascimento já está cadastrada"
    const val BIRTH_DATE_CANNOT_BE_FUTURE = "A data de nascimento não pode ser futura"

    // Paginação
    const val PAGE_CANNOT_BE_NEGATIVE = "A página não pode ser negativa"
    const val PAGE_SIZE_MIN_INVALID = "O tamanho mínimo da página é 1"
    const val PAGE_SIZE_MAX_INVALID = "O tamanho máximo da página é 100"

    // Ordenação e filtros
    const val INVALID_SORT_FIELD = "Campo de ordenação inválido"
    const val INVALID_FILTER_FIELD = "Campo de filtro inválido"
    const val FILTER_PARAMETERS_REQUIRED = "filterBy e filterValue devem ser informados juntos"
    const val DATE_FIELD_REQUIRED = "O campo dateField é obrigatório ao informar filtros de data."

    // Autenticação e autorização
    const val INVALID_CREDENTIALS = "Credenciais inválidas"
    const val USER_NOT_ACTIVE = "Usuário não está ativo"
    const val ACCESS_DENIED = "Acesso negado"
    const val UNAUTHORIZED_ACCESS = "Autenticação necessária ou inválida"
    const val AUTHORIZATION = "Authorization"
    const val INVALID_LOGIN_PARAMS = "Informe apenas e-mail ou login"

    // Tokens e dados auxiliares de testes
    const val VALID_TOKEN = "valid-token"
    const val INVALID_TOKEN = "invalid-token"
    const val BASIC_CREDENTIALS = "Basic credentials"
    const val LOWERCASE_BEARER_TOKEN = "bearer valid-token"
    const val INVALID_JWT = "Invalid JWT"
    const val INVALID_TOKEN_MESSAGE = "Invalid token"
    const val INVALID_CLAIMS_TYPE = "Invalid claims type"

    // Infraestrutura
    const val PASSWORD_ENCODING_FAILED = "Falha ao codificar a senha"
    const val PERSISTENCE_FAILED = "Persistence failed"
    const val SENSITIVE_DATABASE_INFORMATION = "Sensitive database information"
    const val REGISTER_USER_REQUEST = "registerUserRequest"
}
