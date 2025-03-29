package com.example.microgoals.ui.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microgoals.data.local.UserDao
import com.example.microgoals.data.model.User
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserViewModel(
    private val userDao: UserDao
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    fun getUserById(userId: String) {
        viewModelScope.launch {
            userDao.getUserById(userId).collect { user ->
                _user.value = user
            }
        }
    }

    fun insertUser(user: User) {
        viewModelScope.launch {
            userDao.insertUser(user)
        }
    }

    fun getActiveUser(): User? {
        viewModelScope.launch {
            userDao.getActiveUser().collect { users ->
                if (users.isNotEmpty()) {
                    _user.value = users[0]
                } else {
                    _user.value = null
                }
            }
        }
        return _user.value
    }


    fun updateUser(user: User) {
        viewModelScope.launch {
            userDao.updateUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            userDao.deleteUser(user)
        }
    }

    // Factory for creating the ViewModel
    class Factory(private val userDao: UserDao) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                return UserViewModel(userDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
