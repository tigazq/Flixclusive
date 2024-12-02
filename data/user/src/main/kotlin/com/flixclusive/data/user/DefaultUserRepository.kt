package com.flixclusive.data.user

import com.flixclusive.core.database.dao.UserDao
import com.flixclusive.core.util.coroutines.AppDispatchers.Companion.withIOContext
import com.flixclusive.model.database.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class DefaultUserRepository @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override fun observeUsers(): Flow<List<User>> = userDao.getAllItemsInFlow()

    override suspend fun getUser(id: Int): User? {
        return withIOContext {
            userDao.getUserById(id)
        }
    }

    override fun observeUser(id: Int): Flow<User?> = userDao.getUserByIdInFlow(id)

    override suspend fun addUser(user: User) {
        withIOContext {
            userDao.insert(user)
        }
    }

    override suspend fun deleteUser(id: Int) {
        withIOContext {
            userDao.deleteById(id)
        }
    }
}