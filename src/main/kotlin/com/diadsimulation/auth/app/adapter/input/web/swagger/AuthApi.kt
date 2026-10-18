import com.diadsimulation.auth.app.adapter.input.web.dtos.requests.LoginRequest
import com.diadsimulation.auth.app.adapter.input.web.dtos.responses.LoginResponse
import com.diadsimulation.auth.app.adapter.input.web.exception.ApiErrorResponse
import com.diadsimulation.auth.core.common.Messages.INTERNAL_SERVER_ERROR_MESSAGE
import com.diadsimulation.auth.core.common.Messages.INVALID_CREDENTIALS
import com.diadsimulation.auth.core.common.Messages.INVALID_REQUEST_DATA
import com.diadsimulation.auth.core.common.Messages.USER_NOT_ACTIVE
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

@Tag(
    name = "Users",
    description = "Operações relacionadas ao gerenciamento de usuários",
)
interface AuthApi {
    @Operation(
        summary = "Autenticar usuário",
        description = "Valida e-mail ou login e senha e retorna um token JWT para autenticação.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Autenticação realizada com sucesso",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = LoginResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = INVALID_REQUEST_DATA,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "401",
                description = INVALID_CREDENTIALS,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = USER_NOT_ACTIVE,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "500",
                description = INTERNAL_SERVER_ERROR_MESSAGE,
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<LoginResponse>
}
