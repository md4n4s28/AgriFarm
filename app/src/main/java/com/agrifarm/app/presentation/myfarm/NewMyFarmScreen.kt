package com.agrifarm.app.presentation.myfarm

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NewMyFarmScreen(
    viewModel: MyFarmViewModel2 = hiltViewModel()
) {
    CompleteFarmScreen(viewModel)
}
