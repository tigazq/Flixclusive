package com.flixclusive.feature.mobile.provider.test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.flixclusive.data.provider.ProviderManager
import com.flixclusive.domain.provider.test.TestProviderUseCase
import com.flixclusive.gradle.entities.ProviderData
import com.flixclusive.model.provider.id
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProviderTestScreenViewModel @Inject constructor(
    providerManager: ProviderManager,
    savedStateHandle: SavedStateHandle,
    val testProviderUseCase: TestProviderUseCase
) : ViewModel() {
//    val providerData = savedStateHandle.navArgs<ProviderTestScreenNavArgs>().providers
//    val providerInstance = providerManager.providers[providerData.name]

    var showRepetitiveTestWarning by mutableStateOf(false)
        private set

    fun stopTests() {
        testProviderUseCase.stop()
    }

    fun pauseTests() {
        testProviderUseCase.pause()
    }

    fun resumeTests() {
        testProviderUseCase.resume()
    }

    fun startTests(
        providers: ArrayList<ProviderData>,
        skipTestedProviders: Boolean = false
    ) {
        if (
            !showRepetitiveTestWarning
            && providers.fastAny { it.hasAlreadyBeenTested() }
        ) {
            showRepetitiveTestWarning = true
            return
        }

        val providersToTest = providers.let {
            if (skipTestedProviders) {
                return@let it.fastFilter { provider ->
                    !provider.hasAlreadyBeenTested()
                }.toCollection(ArrayList())
            } else it
        }

        showRepetitiveTestWarning = false
        testProviderUseCase(providers = providersToTest)
    }

    private fun ProviderData.hasAlreadyBeenTested(): Boolean {
        return testProviderUseCase.results.fastAny {
            it.provider.id == id
        }
    }
}