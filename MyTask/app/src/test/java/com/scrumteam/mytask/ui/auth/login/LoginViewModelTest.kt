package com.scrumteam.mytask.ui.auth.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.scrumteam.mytask.data.repository.auth.AuthRepository
import com.scrumteam.mytask.utils.getOrAwaitValue
import com.scrumteam.mytask.utils.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()


    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(authRepository)
    }

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun `When Login With Email Password Should Return Success`() = runTest {
        val currentUser = mock(FirebaseUser::class.java)
        val expected = LoginUiState(isError = false, isLoading = false, currentUser = currentUser)

        val data = Result.success(currentUser)

        whenever(
            authRepository.loginWithEmailPassword(
                EMAIL_DUMMY,
                PASSWORD_DUMMY
            )
        ).thenReturn(data)


        loginViewModel.loginWithEmailPassword(EMAIL_DUMMY, PASSWORD_DUMMY)

        val actual = loginViewModel.loginState.getOrAwaitValue()

        Mockito.verify(authRepository).loginWithEmailPassword(EMAIL_DUMMY, PASSWORD_DUMMY)
        Assert.assertTrue(actual.currentUser != null)
        Assert.assertEquals(expected.currentUser, actual.currentUser)
        Assert.assertFalse(actual.isLoading)
        Assert.assertFalse(actual.isError)
    }

    @Test
    fun `When Login Email Password Should Return Error`() = runTest {
        val expected = LoginUiState(isError = true, isLoading = false, currentUser = null)

        val data = Result.failure<FirebaseUser>(Exception())

        whenever(
            authRepository.loginWithEmailPassword(
                EMAIL_DUMMY,
                PASSWORD_DUMMY
            )
        ).thenReturn(data)


        loginViewModel.loginWithEmailPassword(EMAIL_DUMMY, PASSWORD_DUMMY)

        val actual = loginViewModel.loginState.getOrAwaitValue()

        Mockito.verify(authRepository).loginWithEmailPassword(EMAIL_DUMMY, PASSWORD_DUMMY)
        Assert.assertTrue(actual.isError)
        Assert.assertTrue(actual.currentUser == null)
        Assert.assertEquals(expected.currentUser, actual.currentUser)
        Assert.assertFalse(actual.isLoading)
    }

    @Test
    fun `When Login With Credential Should Return Success`() = runTest {
        val currentUser = mock(FirebaseUser::class.java)
        val authCredential = mock(AuthCredential::class.java)
        val expected = LoginUiState(isError = false, isLoading = false, currentUser = currentUser)

        val data = Result.success(currentUser)

        whenever(authRepository.loginWithCredential(authCredential)).thenReturn(data)

        loginViewModel.loginWithCredential(authCredential)

        val actual = loginViewModel.loginState.getOrAwaitValue()

        Mockito.verify(authRepository).loginWithCredential(authCredential)
        Assert.assertTrue(actual.currentUser != null)
        Assert.assertEquals(expected.currentUser, actual.currentUser)
        Assert.assertFalse(actual.isLoading)
        Assert.assertFalse(actual.isError)
    }

    @Test
    fun `When Login With Credential Should Return Error`() = runTest {
        val authCredential = mock(AuthCredential::class.java)
        val expected = LoginUiState(isError = true, isLoading = false, currentUser = null)

        val data = Result.failure<FirebaseUser>(Exception())

        whenever(authRepository.loginWithCredential(authCredential)).thenReturn(data)

        loginViewModel.loginWithCredential(authCredential)

        val actual = loginViewModel.loginState.getOrAwaitValue()

        Mockito.verify(authRepository).loginWithCredential(authCredential)
        Assert.assertTrue(actual.isError)
        Assert.assertTrue(actual.currentUser == null)
        Assert.assertEquals(expected.currentUser, actual.currentUser)
        Assert.assertFalse(actual.isLoading)
    }

    companion object {

        const val EMAIL_DUMMY = "didi@gmail.com"
        const val PASSWORD_DUMMY = "didi"

    }
}

