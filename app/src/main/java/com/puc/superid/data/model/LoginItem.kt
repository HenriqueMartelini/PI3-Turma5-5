/**
 * Modelo de dados para representar um login salvo no Firestore.
 *
 * @property id Identificador do documento do login.
 * @property site Nome do site ou serviço.
 * @property email Email usado no login.
 * @property category Categoria à qual o login pertence.
 * @property createdAt Timestamp de criação do login.
 * @property apiKey Chave API gerada para esse login.
 */

data class LoginItem(
    val id: String,
    val site: String,
    val email: String,
    val category: String,
    val createdAt: Long = 0,
    val apiKey: String = ""
)