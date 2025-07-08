package com.puneet8goyal.splitkaro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.puneet8goyal.splitkaro.data.Member
import com.puneet8goyal.splitkaro.navigation.AppNavigationGraph
import com.puneet8goyal.splitkaro.repository.MemberRepository
import com.puneet8goyal.splitkaro.ui.theme.AppTheme
import com.puneet8goyal.splitkaro.ui.theme.SplitKaroTheme
import com.puneet8goyal.splitkaro.utils.UserOnboardingDialog
import com.puneet8goyal.splitkaro.utils.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var memberRepository: MemberRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SplitKaroTheme {
                SplitKaroApp(
                    userPreferences = userPreferences,
                    memberRepository = memberRepository
                )
            }
        }
    }
}

@Composable
fun SplitKaroApp(
    userPreferences: UserPreferences,
    memberRepository: MemberRepository
) {
    val navController = rememberNavController()
    var showOnboarding by remember { mutableStateOf(false) }
    var isCheckingUser by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Check if user needs onboarding
    LaunchedEffect(Unit) {
        showOnboarding = userPreferences.isFirstTimeUser()
        isCheckingUser = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .systemBarsPadding()
            .navigationBarsPadding()
    ) {
        // Show main app if user is set up
        if (!isCheckingUser && !showOnboarding) {
            AppNavigationGraph(navController)
        }

        // Show onboarding for new users
        if (showOnboarding) {
            UserOnboardingDialog(
                onUserInfoSubmitted = { userName ->
                    scope.launch {
                        val member = Member(name = userName)
                        memberRepository.insertMember(member).fold(
                            onSuccess = { generatedMemberId ->
                                userPreferences.setCurrentUserName(userName)
                                userPreferences.setCurrentUserMemberId(generatedMemberId)
                                showOnboarding = false
                            },
                            onFailure = {
                                // Still proceed even if member creation fails
                                userPreferences.setCurrentUserName(userName)
                                showOnboarding = false
                            }
                        )
                    }
                }
            )
        }
    }
}
