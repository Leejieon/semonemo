package com.semonemo.presentation

import BottomNavigationBar
import android.util.Log
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.semonemo.presentation.navigation.CustomFAB
import com.semonemo.presentation.navigation.ScreenDestinations
import com.semonemo.presentation.screen.aiAsset.AiAssetScreen
import com.semonemo.presentation.screen.aiAsset.AssetDoneScreen
import com.semonemo.presentation.screen.aiAsset.DrawAssetScreen
import com.semonemo.presentation.screen.aiAsset.PromptAssetScreen
import com.semonemo.presentation.screen.auction.AuctionProcessScreen
import com.semonemo.presentation.screen.auction.AuctionScreen
import com.semonemo.presentation.screen.frame.FrameScreen
import com.semonemo.presentation.screen.imgAsset.ImageAssetScreen
import com.semonemo.presentation.screen.imgAsset.ImageSelectRoute
import com.semonemo.presentation.screen.login.LoginRoute
import com.semonemo.presentation.screen.moment.MomentScreen
import com.semonemo.presentation.screen.mypage.DetailScreen
import com.semonemo.presentation.screen.mypage.MyPageRoute
import com.semonemo.presentation.screen.picture.PictureMainScreen
import com.semonemo.presentation.screen.signup.SignUpRoute
import com.semonemo.presentation.screen.store.StoreFullViewScreen
import com.semonemo.presentation.screen.store.StoreScreen
import com.semonemo.presentation.screen.wallet.WalletScreen
import com.semonemo.presentation.theme.Gray01
import com.semonemo.presentation.theme.GunMetal
import com.semonemo.presentation.theme.Typography
import com.semonemo.presentation.util.toUriOrDefault
import kotlinx.coroutines.launch

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val (visible, setVisible) =
        remember {
            mutableStateOf(false)
        }
    when (currentRoute) {
        "mypage", "shop", "moment", "wallet", "auction", "storeFullView/{isFrame}" ->
            setVisible(
                true,
            )

        else -> setVisible(false)
    }

    val snackBarHostState =
        remember {
            SnackbarHostState()
        }

    val onShowErrorSnackBar: (errorMessage: String) -> Unit = { errorMessage ->
        coroutineScope.launch {
            snackBarHostState.showSnackbar(errorMessage)
        }
    }

    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            if (visible) {
                BottomNavigationBar(navController = navController, currentRoute = currentRoute)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (visible) {
                val isSelected = currentRoute == ScreenDestinations.Moment.route
                CustomFAB(
                    onClick = {
                        navController.navigate(ScreenDestinations.Moment.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    icon =
                        if (isSelected) {
                            R.drawable.ic_bot_nav_fill_frame
                        } else {
                            R.drawable.ic_bot_nav_outline_frame
                        },
                    iconColor = Color.White,
                    labelColor =
                        if (isSelected) GunMetal else Gray01,
                    labelStyle =
                        if (isSelected) Typography.bodySmall.copy(fontSize = 12.sp) else Typography.labelSmall,
                )
            }
        },
    ) { _ ->
        MainNavHost(
            navController = navController,
            startDestination = ScreenDestinations.Moment.route,
            onShowErrorSnackBar = onShowErrorSnackBar,
        )
    }
}

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
    onShowErrorSnackBar: (String) -> Unit,
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(
            route = ScreenDestinations.Login.route,
        ) {
            LoginRoute(
                modifier = modifier,
                popUpBackStack = navController::popBackStack,
                navigateToRegister = { walletAddress ->
                    navController.navigate(ScreenDestinations.Register.createRoute(walletAddress))
                },
                onShowErrorSnackBar = onShowErrorSnackBar,
                navigateToMoment = {
                    navController.navigate(ScreenDestinations.Moment.route) {
                        popUpTo(startDestination) {
                            inclusive = true
                        }
                    }
                },
            )
        }

        composable(
            route = ScreenDestinations.Register.route,
            arguments = ScreenDestinations.Register.arguments,
        ) {
            SignUpRoute(
                modifier = modifier,
                popUpBackStack = navController::popBackStack,
                onShowErrorSnackBar = onShowErrorSnackBar,
            )
        }

        composable(
            route = ScreenDestinations.Shop.route,
        ) {
            StoreScreen(
                modifier = modifier,
                navigateToFullView = { isFrame ->
                    navController.navigate(
                        ScreenDestinations.StoreFullView.createRoute(
                            isFrame,
                        ),
                    )
                },
            )
        }

        composable(
            route = ScreenDestinations.Auction.route,
        ) {
            AuctionScreen(
                modifier = modifier,
                navigateToAuctionProcess = { auctionId ->
                    navController.navigate(
                        ScreenDestinations.AuctionProcess.createRoute(
                            auctionId,
                        ),
                    )
                },
            )
        }

        composable(
            route = ScreenDestinations.Moment.route,
        ) {
            MomentScreen(
                modifier = modifier,
                navigateToAiAsset = { navController.navigate(ScreenDestinations.AiAsset.route) },
                navigateToImageAsset = { navController.navigate(ScreenDestinations.ImageAsset.route) },
                navigateToFrame = { navController.navigate(ScreenDestinations.Frame.route) },
                navigateToPicture = { navController.navigate(ScreenDestinations.PictureMain.route) },
            )
        }

        composable(
            route = ScreenDestinations.Wallet.route,
        ) {
            WalletScreen(modifier = modifier)
        }

        composable(
            route = ScreenDestinations.MyPage.route,
        ) {
            MyPageRoute(
                modifier = modifier,
                navigateToDetail = { imgUrl ->
                    navController.navigate(
                        ScreenDestinations.Detail.createRoute(
                            imgUrl,
                        ),
                    )
                },
                onErrorSnackBar = onShowErrorSnackBar,
            )
        }

        composable(
            route = ScreenDestinations.ImageAsset.route,
        ) {
            ImageAssetScreen(
                modifier = modifier,
                popUpBackStack = navController::popBackStack,
                navigateToSelect = { selectedImg ->
                    navController.navigate(ScreenDestinations.Select.createRoute(selectedImg))
                },
            )
        }

        composable(
            route = ScreenDestinations.Select.route,
            arguments = ScreenDestinations.Select.arguments,
        ) { navBackStackEntry ->
            val imageUriString = navBackStackEntry.arguments?.getString("selectedImg")
            val imageUri = imageUriString?.toUriOrDefault()
            ImageSelectRoute(
                modifier = modifier,
                popUpBackStack = navController::popBackStack,
                navigateToDone = { assetUrl ->
                    navController.navigate(ScreenDestinations.AssetDone.createRoute(assetUrl))
                },
            )
        }

        composable(
            route = ScreenDestinations.AssetDone.route,
            arguments = ScreenDestinations.AssetDone.arguments,
        ) { navBackStackEntry ->
            val assetUrl = navBackStackEntry.arguments?.getString("assetUrl")
            AssetDoneScreen(
                modifier = modifier,
                assetUrl = assetUrl,
                popUpBackStack = navController::popBackStack,
                navigateToMy = {
                    navController.navigate(ScreenDestinations.MyPage.route)
                },
            )
        }

        composable(
            route = ScreenDestinations.AiAsset.route,
        ) {
            AiAssetScreen(
                modifier = modifier,
                navigateToDraw = {
                    navController.navigate(ScreenDestinations.DrawAsset.route)
                },
                navigateToPrompt = {
                    navController.navigate(ScreenDestinations.PromptAsset.route)
                },
            )
        }

        composable(
            route = ScreenDestinations.PictureMain.route,
        ) {
            PictureMainScreen()
        }

        composable(
            route = ScreenDestinations.DrawAsset.route,
        ) {
            DrawAssetScreen(
                modifier = modifier,
                navigateToDone = { assetUrl ->
                    navController.navigate(ScreenDestinations.AssetDone.createRoute(assetUrl))
                },
            )
        }

        composable(
            route = ScreenDestinations.PromptAsset.route,
        ) {
            PromptAssetScreen(
                modifier = modifier,
                navigateToDone = { assetUrl ->
                    navController.navigate(ScreenDestinations.AssetDone.createRoute(assetUrl))
                },
            )
        }

        composable(
            route = ScreenDestinations.Detail.route,
        ) {
            DetailScreen(
                modifier = modifier,
                imgUrl = it.arguments?.getString("imgUrl"),
            )
        }

        composable(
            route = ScreenDestinations.Frame.route,
        ) {
            FrameScreen(
                modifier = modifier,
//                navigateToFrameDone = { frame ->
//                    val base64String = Base64.encodeToString(frame, Base64.DEFAULT)
//                    Log.d("test", "${frame}\n  $base64String")
//                    navController.navigate(ScreenDestinations.FrameDone.createRoute("123"))
                //              },
            )
        }

//        composable(
//            route = ScreenDestinations.FrameDone.route,
//            arguments = ScreenDestinations.FrameDone.arguments,
//        ) { navBackStackEntry ->
//            val frame = navBackStackEntry.arguments?.getString("frame") ?: ""
//            val uri = decodeBase64ToImage(frame)!!
//            val byteArray = Base64.decode(frame, Base64.DEFAULT)
//            FrameDoneScreen(
//                modifier = modifier,
//                frame = "123".toUri(),
//            )
//        }
        composable(
            route = ScreenDestinations.AuctionProcess.route,
            arguments = ScreenDestinations.AuctionProcess.arguments,
        ) {
            AuctionProcessScreen(
                modifier = modifier,
                auctionId = it.arguments?.getString("auctionId") ?: "",
            )
        }

        composable(
            route = ScreenDestinations.StoreFullView.route,
            arguments = ScreenDestinations.StoreFullView.arguments,
        ) { navBackStackEntry ->

            Log.d("test", "${navBackStackEntry.destination?.route}")
            StoreFullViewScreen(
                modifier = modifier,
                isFrame = navBackStackEntry.arguments?.getBoolean("isFrame") ?: false,
            )
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry =
        remember(this) {
            navController.getBackStackEntry(navGraphRoute)
        }
    return hiltViewModel(parentEntry)
}
