/**
 * Estado da UI do login contendo email, senha e flags de erro.
 *
 * @property email Email do usuário
 * @property password Senha do usuário
 * @property emailError Indica se há erro no email
 * @property passwordError Indica se há erro na senha
 * @property errorMessage Mensagem de erro genérica (não usada no momento)
 */

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val errorMessage: String? = null
)