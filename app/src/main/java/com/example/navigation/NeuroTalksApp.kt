package com.example.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.NeuroTalksBrandMark
import com.example.ui.screens.EmergencyScreen
import com.example.ui.screens.ForumScreen
import com.example.ui.screens.GuidelinesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PodcastScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SimulationScreen
import com.example.ui.screens.WelcomeScreen
import com.example.viewmodel.NeuroTalksUiState
import com.example.viewmodel.NeuroTalksViewModel

object NeuroTalksRoutes {
    const val WELCOME = "welcome"
    const val GUIDELINES = "guidelines"
    const val HOME = "home"
    const val PODCAST = "podcast"
    const val SIMULATION = "simulation"
    const val FORUM = "forum"
    const val EMERGENCY = "emergency"
    const val PROFILE = "profile"
}

private data class MainNavTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val mainTabs = listOf(
    MainNavTab(
        route = NeuroTalksRoutes.HOME,
        label = "Home",
        selectedIcon = Icons.Rounded.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_tab_home"
    ),
    MainNavTab(
        route = NeuroTalksRoutes.PODCAST,
        label = "Podcast",
        selectedIcon = Icons.Rounded.Headphones,
        unselectedIcon = Icons.Outlined.Headphones,
        testTag = "nav_tab_podcast"
    ),
    MainNavTab(
        route = NeuroTalksRoutes.SIMULATION,
        label = "Simulate",
        selectedIcon = Icons.Rounded.SelfImprovement,
        unselectedIcon = Icons.Outlined.SelfImprovement,
        testTag = "nav_tab_simulation"
    ),
    MainNavTab(
        route = NeuroTalksRoutes.FORUM,
        label = "Forum",
        selectedIcon = Icons.Rounded.Forum,
        unselectedIcon = Icons.Outlined.Forum,
        testTag = "nav_tab_forum"
    ),
    MainNavTab(
        route = NeuroTalksRoutes.PROFILE,
        label = "Profile",
        selectedIcon = Icons.Rounded.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "nav_tab_profile"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeuroTalksApp(
    uiState: NeuroTalksUiState,
    viewModel: NeuroTalksViewModel
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: NeuroTalksRoutes.WELCOME

    val showMainChrome = currentRoute in setOf(
        NeuroTalksRoutes.HOME,
        NeuroTalksRoutes.PODCAST,
        NeuroTalksRoutes.SIMULATION,
        NeuroTalksRoutes.FORUM,
        NeuroTalksRoutes.PROFILE
    )

    val topBarTitle = when (currentRoute) {
        NeuroTalksRoutes.HOME -> "NeuroTalks"
        NeuroTalksRoutes.PODCAST -> "Podcast"
        NeuroTalksRoutes.SIMULATION -> "Ideal Life"
        NeuroTalksRoutes.FORUM -> "Forum"
        NeuroTalksRoutes.PROFILE -> "Profile"
        else -> "NeuroTalks"
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = showMainChrome && maxWidth >= 640.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (useNavRail) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    header = {
                        NeuroTalksBrandMark(
                            size = 40.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                ) {
                    mainTabs.forEach { tab ->
                        val selected = currentRoute == tab.route
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigateToMainTab(navController, tab.route) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = if (showMainChrome) WindowInsets.safeDrawing else WindowInsets(0, 0, 0, 0),
                topBar = {
                    if (showMainChrome) {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                titleContentColor = MaterialTheme.colorScheme.onBackground
                            ),
                            navigationIcon = {
                                if (!useNavRail) {
                                    NeuroTalksBrandMark(
                                        size = 34.dp,
                                        modifier = Modifier
                                            .padding(start = 16.dp)
                                            .clickable { navigateToMainTab(navController, NeuroTalksRoutes.HOME) }
                                    )
                                }
                            },
                            title = {
                                Text(
                                    text = topBarTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            actions = {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f)),
                                    modifier = Modifier
                                        .padding(end = 14.dp)
                                        .clip(RoundedCornerShape(50))
                                        .clickable { navController.navigate(NeuroTalksRoutes.EMERGENCY) }
                                        .testTag("topbar_emergency_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.HealthAndSafety,
                                            contentDescription = "Emergency Support",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "SOS",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        )
                    }
                },
                bottomBar = {
                    if (showMainChrome && !useNavRail) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 4.dp
                        ) {
                            mainTabs.forEach { tab ->
                                val selected = currentRoute == tab.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToMainTab(navController, tab.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = NeuroTalksRoutes.WELCOME,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable(NeuroTalksRoutes.WELCOME) {
                        WelcomeScreen(
                            onExploreSpace = {
                                navController.navigate(NeuroTalksRoutes.GUIDELINES)
                            },
                            onOpenEmergency = {
                                navController.navigate(NeuroTalksRoutes.EMERGENCY)
                            },
                            onSkipToHome = {
                                navController.navigate(NeuroTalksRoutes.HOME) {
                                    popUpTo(NeuroTalksRoutes.WELCOME) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(NeuroTalksRoutes.GUIDELINES) {
                        GuidelinesScreen(
                            guidelinesAccepted = uiState.guidelinesAccepted,
                            onAcceptedChange = viewModel::setGuidelinesAccepted,
                            onContinueToHome = {
                                navController.navigate(NeuroTalksRoutes.HOME) {
                                    popUpTo(NeuroTalksRoutes.WELCOME) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onOpenEmergency = {
                                navController.navigate(NeuroTalksRoutes.EMERGENCY)
                            },
                            onBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(NeuroTalksRoutes.HOME)
                                }
                            }
                        )
                    }

                    composable(NeuroTalksRoutes.HOME) {
                        HomeScreen(
                            uiState = uiState,
                            onSelectMood = viewModel::selectDailyMood,
                            onNavigatePodcast = { navigateToMainTab(navController, NeuroTalksRoutes.PODCAST) },
                            onNavigateSimulation = { navigateToMainTab(navController, NeuroTalksRoutes.SIMULATION) },
                            onNavigateForum = { navigateToMainTab(navController, NeuroTalksRoutes.FORUM) },
                            onNavigateEmergency = { navController.navigate(NeuroTalksRoutes.EMERGENCY) },
                            onNavigateGuidelines = { navController.navigate(NeuroTalksRoutes.GUIDELINES) },
                            onNavigateProfile = { navigateToMainTab(navController, NeuroTalksRoutes.PROFILE) }
                        )
                    }

                    composable(NeuroTalksRoutes.PODCAST) {
                        PodcastScreen(
                            uiState = uiState,
                            onRefreshFeeds = viewModel::refreshPublicPodcastFeeds,
                            onSelectCategory = viewModel::selectPodcastCategory,
                            onToggleStreamableOnly = viewModel::toggleStreamableOnlyFilter,
                            onSelectEpisode = viewModel::selectPodcastEpisode,
                            onPlayStreamableEpisode = viewModel::playStreamableEpisode,
                            onToggleMedia3PlayPause = viewModel::toggleMedia3PlayPause,
                            onSeekMedia3Ms = viewModel::seekMedia3To,
                            onSkipMedia3Ms = viewModel::skipMedia3By,
                            onCycleMedia3Speed = viewModel::cycleMedia3Speed,
                            onRecordExternalOpen = viewModel::recordExternalPodcastOpen,
                            onToggleBookmark = viewModel::toggleRealPodcastBookmark
                        )
                    }

                    composable(NeuroTalksRoutes.SIMULATION) {
                        SimulationScreen(
                            simState = uiState.lifeSim,
                            onSetPhase = viewModel::setLifeSimPhase,
                            onContinueSavedWorld = viewModel::continueSavedLifeSim,
                            onSelectLifestyle = viewModel::selectLifeSimLifestyle,
                            onSelectPresetCharacter = viewModel::selectPresetCharacter,
                            onUpdateAppearance = viewModel::updateLifeSimAppearance,
                            onEnterWorld = viewModel::enterLifeSimWorld,
                            onSetWalkTarget = viewModel::setLifeSimWalkTarget,
                            onMoveDirectionStep = viewModel::moveLifeSimByDirection,
                            onTickMovement = viewModel::tickLifeSimMovement,
                            onPerformActivity = viewModel::performLifeSimActivity,
                            onTalkToNpc = viewModel::talkToLifeSimNpc,
                            onAddCustomReflection = viewModel::addLifeSimJournalReflection,
                            onDismissChatBubble = viewModel::dismissLifeSimChatBubble,
                            onDismissNewDaySummary = viewModel::dismissNewDaySummary,
                            onOpenScenarioReflection = viewModel::openScenarioReflection,
                            onDismissScenarioReflection = viewModel::dismissScenarioReflection
                        )
                    }

                    composable(NeuroTalksRoutes.FORUM) {
                        ForumScreen(
                            uiState = uiState,
                            onSelectCategory = viewModel::selectForumCategory,
                            onToggleExpanded = viewModel::togglePostExpanded,
                            onToggleWarmth = viewModel::toggleWarmth,
                            onToggleRelate = viewModel::toggleRelate,
                            onAddReply = viewModel::addForumReply,
                            onCreatePost = viewModel::createDemoPost,
                            onReportPost = viewModel::reportPost,
                            onOpenGuidelines = { navController.navigate(NeuroTalksRoutes.GUIDELINES) }
                        )
                    }

                    composable(NeuroTalksRoutes.EMERGENCY) {
                        EmergencyScreen(
                            onBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(NeuroTalksRoutes.HOME)
                                }
                            }
                        )
                    }

                    composable(NeuroTalksRoutes.PROFILE) {
                        ProfileScreen(
                            uiState = uiState,
                            onCycleAlias = viewModel::cycleAnonymousAlias,
                            onUpdateSettings = viewModel::updateSettings,
                            onResetDemoData = viewModel::resetDemoData,
                            onOpenGuidelines = { navController.navigate(NeuroTalksRoutes.GUIDELINES) },
                            onOpenEmergency = { navController.navigate(NeuroTalksRoutes.EMERGENCY) },
                            onReplayWelcome = {
                                navController.navigate(NeuroTalksRoutes.WELCOME) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun navigateToMainTab(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
