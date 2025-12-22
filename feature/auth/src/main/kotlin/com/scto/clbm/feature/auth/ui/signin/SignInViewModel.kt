/*
 * Copyright 2025 Atick Faisal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scto.clbm.feature.auth.ui.signin

import android.app.Activity
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.scto.clbm.core.extensions.isEmailValid
import com.scto.clbm.core.extensions.isPasswordValid
import com.scto.clbm.core.ui.utils.TextFiledData
import com.scto.clbm.core.ui.utils.UiState
import com.scto.clbm.core.ui.utils.updateState
import com.scto.clbm.core.ui.utils.updateWith
import com.scto.clbm.data.repository.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * [ViewModel] for [SignInScreen].
 *
 * @param authRepository [AuthRepository].
 */
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _signInUiState = MutableStateFlow(UiState(SignInScreenData()))
    val signInUiState = _signInUiState.asStateFlow()

    fun updateEmail(email: String) {
        _signInUiState.updateState {
            copy(
                email = TextFiledData(
                    value = email,
                    errorMessage = if (email.isEmailValid()) null else "Email Not Valid",
                ),
            )
        }
    }

    fun updatePassword(password: String) {
        _signInUiState.updateState {
            copy(
                password = TextFiledData(
                    value = password,
                    errorMessage = if (password.isPasswordValid()) null else "Password Not Valid",
                ),
            )
        }
    }

    fun signInWithSavedCredentials(activity: Activity) {
        _signInUiState.updateWith {
            authRepository.signInWithSavedCredentials(activity)
        }
    }

    fun signInWithGoogle(activity: Activity) {
        _signInUiState.updateWith { authRepository.signInWithGoogle(activity) }
    }

    fun loginWithEmailAndPassword() {
        _signInUiState.updateWith {
            authRepository.signInWithEmailAndPassword(
                email = email.value,
                password = password.value,
            )
        }
    }
}

/**
 * Data for [SignInScreen].
 *
 * @param email [TextFiledData].
 * @param password [TextFiledData].
 */
@Immutable
data class SignInScreenData(
    val email: TextFiledData = TextFiledData(String()),
    val password: TextFiledData = TextFiledData(String()),
)
