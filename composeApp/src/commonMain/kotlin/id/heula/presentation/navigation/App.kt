package id.heula.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.vector.ImageVector
import id.heula.presentation.screen.history.HistoryScreen
import id.heula.presentation.screen.home.HomeScreen
import id.heula.presentation.screen.onboarding.OnboardingScreen
import id.heula.presentation.screen.profile.ProfileScreen
import id.heula.presentation.screen.stats.StatsScreen
import id.heula.presentation.store.AppStore
import id.heula.presentation.theme.BrandPrimary
import id.heula.presentation.theme.HeulaTheme
import id.heula.presentation.util.Strings
import id.heula.presentation.util.getStrings

enum class ScreenTab {
    HOME, HISTORY, STATS, PROFILE
}

@Composable
fun App(
    store: AppStore,
) {
    val settings by store.settings.collectAsState()
    val s = getStrings(settings.language)
    var currentTab by remember { mutableStateOf(ScreenTab.HOME) }
    var isOnboardingMode by remember(settings.onboarded) { mutableStateOf(!settings.onboarded) }

    HeulaTheme(userTheme = settings.theme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            if (isOnboardingMode) {
                OnboardingScreen(
                    store = store,
                    onFinish = { isOnboardingMode = false },
                )
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isWide = maxWidth >= 768.dp

                    if (isWide) {
                        // Desktop / Web Layout: Side NavigationRail + Main Content Area
                        Row(modifier = Modifier.fillMaxSize()) {
                            NavigationRail(
                                modifier = Modifier.fillMaxHeight(),
                                containerColor = MaterialTheme.colorScheme.surface,
                                header = {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(vertical = 20.dp),
                                    ) {
                                        Text(
                                            text = "Heula",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = BrandPrimary,
                                        )
                                    }
                                }
                            ) {
                                NavigationRailItem(
                                    selected = currentTab == ScreenTab.HOME,
                                    onClick = { currentTab = ScreenTab.HOME },
                                    icon = { Icon(Icons.Default.Timer, contentDescription = s.navTimer) },
                                    label = { Text(s.navTimer) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = BrandPrimary,
                                        selectedTextColor = BrandPrimary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    )
                                )
                                NavigationRailItem(
                                    selected = currentTab == ScreenTab.HISTORY,
                                    onClick = { currentTab = ScreenTab.HISTORY },
                                    icon = { Icon(Icons.Default.History, contentDescription = s.navHistory) },
                                    label = { Text(s.navHistory) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = BrandPrimary,
                                        selectedTextColor = BrandPrimary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    )
                                )
                                NavigationRailItem(
                                    selected = currentTab == ScreenTab.STATS,
                                    onClick = { currentTab = ScreenTab.STATS },
                                    icon = { Icon(Icons.Default.BarChart, contentDescription = s.navStats) },
                                    label = { Text(s.navStats) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = BrandPrimary,
                                        selectedTextColor = BrandPrimary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    )
                                )
                                NavigationRailItem(
                                    selected = currentTab == ScreenTab.PROFILE,
                                    onClick = { currentTab = ScreenTab.PROFILE },
                                    icon = { Icon(Icons.Default.Person, contentDescription = s.navProfile) },
                                    label = { Text(s.navProfile) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = BrandPrimary,
                                        selectedTextColor = BrandPrimary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                when (currentTab) {
                                    ScreenTab.HOME -> HomeScreen(store = store)
                                    ScreenTab.HISTORY -> HistoryScreen(store = store)
                                    ScreenTab.STATS -> StatsScreen(store = store)
                                    ScreenTab.PROFILE -> ProfileScreen(
                                        store = store,
                                        onResetOnboarding = { isOnboardingMode = true }
                                    )
                                }
                            }
                        }
                    } else {
                        // Mobile Layout: Screen Content with generous bottom padding for wide pill bar
                        Box(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 86.dp)
                            ) {
                                when (currentTab) {
                                    ScreenTab.HOME -> HomeScreen(store = store)
                                    ScreenTab.HISTORY -> HistoryScreen(store = store)
                                    ScreenTab.STATS -> StatsScreen(store = store)
                                    ScreenTab.PROFILE -> ProfileScreen(
                                        store = store,
                                        onResetOnboarding = { isOnboardingMode = true }
                                    )
                                }
                            }

                            IosFloatingPillNavBar(
                                currentTab = currentTab,
                                onTabSelected = { currentTab = it },
                                s = s,
                                modifier = Modifier.align(Alignment.BottomCenter),
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class TabItem(
    val tab: ScreenTab,
    val label: String,
    val icon: ImageVector,
)

@Composable
private fun IosFloatingPillNavBar(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    s: Strings,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        TabItem(ScreenTab.HOME, s.navTimer, Icons.Default.Timer),
        TabItem(ScreenTab.HISTORY, s.navHistory, Icons.Default.History),
        TabItem(ScreenTab.STATS, s.navStats, Icons.Default.BarChart),
        TabItem(ScreenTab.PROFILE, s.navProfile, Icons.Default.Person),
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
            shadowElevation = 12.dp,
            tonalElevation = 2.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEach { item ->
                    val isSelected = item.tab == currentTab
                    Surface(
                        onClick = { onTabSelected(item.tab) },
                        shape = CircleShape,
                        color = if (isSelected) BrandPrimary else Color.Transparent,
                        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    ) {
                        Row(
                            modifier = Modifier
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                )
                                .padding(
                                    horizontal = if (isSelected) 16.dp else 12.dp,
                                    vertical = 10.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(20.dp),
                            )
                            AnimatedVisibility(
                                visible = isSelected,
                                enter = fadeIn() + expandHorizontally(),
                                exit = fadeOut() + shrinkHorizontally(),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = item.label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
