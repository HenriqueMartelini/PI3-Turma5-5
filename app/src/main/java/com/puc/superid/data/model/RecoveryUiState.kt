/**
 * Estado da UI para recuperação de senha, contendo email e flag de erro.
 *
 * @property email Email informado pelo usuário
 * @property emailError Indica se o email é inválido ou contém erro
 */
data class RecoveryUiState(
    val email: String = "",
    val emailError: Boolean = false
)