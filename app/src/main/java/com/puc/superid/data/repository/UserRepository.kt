package com.puc.superid.data.repository

import com.puc.superid.data.datasource.UserDataSource
import com.puc.superid.data.model.User

/**
 * Classe gerenciadora de operações de repositório relacionadas a usuários
 *
 * @property userDataSource Instância de [UserDataSource] usada para interagir com o banco de dados
 */
class UserRepository(private val dataSource: UserDataSource) {

    /**
     * Cria um novo usuário chamando o método [createUser] no [UserDataSource].
     *
     * @param user Objeto do tipo [User] que será salvo no banco de dados
     */
    suspend fun createUser(user: User) {
        dataSource.createUser(user.uid, user)
    }
}