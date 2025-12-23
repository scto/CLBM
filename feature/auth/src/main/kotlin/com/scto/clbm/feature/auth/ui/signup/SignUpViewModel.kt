/*
 * Copyright 2025 Thomas Schmidl
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

package com.scto.clbm.feature.auth.ui.signup

import android.app.Activity
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.scto.clbm.core.extensions.isEmailValid
import com.scto.clbm.core.extensions.isPasswordValid
import com.scto.clbm.core.extensions.isValidFullName
import com.scto.clbm.core.ui.utils.TextFiledData
import com.scto.clbm.core.ui.utils.UiState
import com.scto.clbm.core.ui.utils.updateState
import com.scto.clbm.core.ui.utils.updateWith
import com.scto.clbm.data.repository.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * [ViewModel] for [SignUpScreen].
 *
 * @param authRepository [AuthRepository].
 */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _signUpUiState = MutableStateFlow(UiState(SignUpScreenData()))
    val signUpUiState = _signUpUiState.asStateFlow()

    fun updateName(name: String) {
        _signUpUiState.updateState {
            copy(
                name = TextFiledData(
                    value = name,
                    errorMessage = if (name.isValidFullName()) null else "Name Not Valid",
                ),
            )
        }
    }

    fun updateEmail(email: String) {
        _signUpUiState.updateState {
            copy(
                email = TextFiledData(
                    value = email,
                    errorMessage = if (email.isEmailValid()) null else "Email Not Valid",
                ),
            )
        }
    }

    fun updatePassword(password: String) {
        _signUpUiState.updateState {
            copy(
                password = TextFiledData(
                    value = password,
                    errorMessage = if (password.isPasswordValid()) null else "Password Not Valid",
                ),
            )
        }
    }

    fun registerWithGoogle(activity: Activity) {
        _signUpUiState.updateWith { authRepository.registerWithGoogle(activity) }
    }

    fun registerWithEmailAndPassword(activity: Activity) {
        _signUpUiState.updateWith {
            authRepository.registerWithEmailAndPassword(
                name = name.value,
                email = email.value,
                password = password.value,
                activity = activity,
            )
        }
    }
}

/**
 * Data for [SignUpScreen].
 *
 * @param name [TextFiledData].
 * @param email [TextFiledData].
 * @param password [TextFiledData].
 */
@Immutable
data class SignUpScreenData(
    val name: TextFiledData = TextFiledData(String()),
    val email: TextFiledData = TextFiledData(String()),
    val password: TextFiledData = TextFiledData(String()),
)
