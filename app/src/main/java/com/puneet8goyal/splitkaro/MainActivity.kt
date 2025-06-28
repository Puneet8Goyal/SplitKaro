package com.puneet8goyal.splitkaro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.puneet8goyal.splitkaro.navigation.AppNavigationGraph
import com.puneet8goyal.splitkaro.ui.theme.AppTheme
import com.puneet8goyal.splitkaro.ui.theme.SplitKaroTheme
import com.puneet8goyal.splitkaro.utils.UserOnboardingDialog
import com.puneet8goyal.splitkaro.utils.UserPreferences
import com.puneet8goyal.splitkaro.repository.MemberRepository
import com.puneet8goyal.splitkaro.data.Member
import androidx.compose.runtime.rememberCoroutineScope
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
                App(userPreferences = userPreferences, memberRepository = memberRepository)
            }
        }
    }
}

@Composable
fun App(
    userPreferences: UserPreferences,
    memberRepository: MemberRepository
) {
    val navController = rememberNavController()
    var showOnboarding by remember { mutableStateOf(false) }
    var isCheckingUser by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Check if user needs onboarding immediately when app starts
    LaunchedEffect(Unit) {
        showOnboarding = userPreferences.isFirstTimeUser()
        isCheckingUser = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        // Show main app only after user is set up
        if (!isCheckingUser && !showOnboarding) {
            AppNavigationGraph(navController)
        }

        // Show onboarding dialog immediately if first-time user
        if (showOnboarding) {
            UserOnboardingDialog(
                onUserInfoSubmitted = { userName ->
                    scope.launch {
                        // Create Member record for current user
                        val member = Member(name = userName)
                        memberRepository.insertMember(member).fold(
                            onSuccess = { generatedMemberId ->
                                // Store user info
                                userPreferences.setCurrentUserName(userName)
                                userPreferences.setCurrentUserMemberId(generatedMemberId)
                                showOnboarding = false
                                println("DEBUG: Created user member with ID: $generatedMemberId")
                            },
                            onFailure = { exception ->
                                println("DEBUG: Failed to create user member: ${exception.message}")
                                // Fallback - still save the name
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
