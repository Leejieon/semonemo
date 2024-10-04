package com.semonemo.presentation.screen.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.semonemo.domain.model.AssetDetail
import com.semonemo.domain.model.FrameDetail
import com.semonemo.domain.model.Profile
import com.semonemo.presentation.BuildConfig
import com.semonemo.presentation.R
import com.semonemo.presentation.component.BackButton
import com.semonemo.presentation.component.CustomTab
import com.semonemo.presentation.component.HashTag
import com.semonemo.presentation.component.LoadingDialog
import com.semonemo.presentation.component.SearchTextField
import com.semonemo.presentation.component.SectionHeader
import com.semonemo.presentation.theme.Gray01
import com.semonemo.presentation.theme.Gray03
import com.semonemo.presentation.theme.SemonemoTheme
import com.semonemo.presentation.theme.Typography
import com.semonemo.presentation.util.noRippleClickable
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    navigateToProfile: (Long) -> Unit,
    popUpBackStack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    SearchScreen(
        modifier = modifier,
        navigateToProfile = navigateToProfile,
        popUpBackStack = popUpBackStack,
        searchState = searchState,
        searchUser = { viewModel.userSearch(it) },
        searchFrame = { viewModel.frameSearch(it) },
        searchAsset = { viewModel.assetSearch(it) },
    )
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchState: SearchState,
    popUpBackStack: () -> Unit = {},
    navigateToProfile: (Long) -> Unit = {},
    searchUser: (String) -> Unit = {},
    searchFrame: (String) -> Unit = {},
    searchAsset: (String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val testData = remember { mutableStateListOf("이나경", "이지언", "이준형", "최현성", "전형선", "이재한") }
    var keyword by remember { mutableStateOf("") }

    Column(
        modifier =
            modifier
                .navigationBarsPadding()
                .statusBarsPadding()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
    ) {
        Row(modifier = Modifier) {
            BackButton(popUpBackStack = popUpBackStack)
            SearchTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 5.dp),
                keyword = keyword,
                onValueChanged = {
                    keyword = it
                },
                focusManager = focusManager,
                onSearchAction = {
                    keyword = it
                    searchUser(keyword)
                },
                onClearPressed = {
                    keyword = ""
                },
            )
        }
        Spacer(modifier = Modifier.weight(0.05f))
        when (searchState) {
            is SearchState.Loading -> {
                LoadingDialog()
            }

            is SearchState.Init -> {
                SearchInitScreen(
                    modifier = modifier,
                    recentSearchList = testData,
                    hotSearchList = searchState.hotList,
                    onClickedKeyword = {
                        keyword = it
                        searchUser(it)
                    },
                    onDeleteKeyword = {
                        testData.remove(it)
                    },
                )
            }

            is SearchState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchTabScreen(
                        keyword = keyword,
                        searchUser = { keyword ->
                            searchUser(keyword)
                        },
                        searchFrame = { keyword ->
                            searchFrame(keyword)
                        },
                        searchAsset = { keyword ->
                            searchAsset(keyword)
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SearchSuccessScreen(
                        modifier = modifier,
                        userList = searchState.userList,
                        frameList = searchState.frameList,
                        assetList = searchState.assetList,
                        navigateToProfile = navigateToProfile,
                    )
                }
            }

            is SearchState.Error -> {
            }
        }
    }
}

@Composable
fun SearchInitScreen(
    modifier: Modifier = Modifier,
    recentSearchList: List<String> = emptyList(),
    hotSearchList: List<String> = emptyList(),
    onClickedKeyword: (String) -> Unit,
    onDeleteKeyword: (String) -> Unit,
) {
    val (isEdit, setEdit) =
        remember {
            mutableStateOf(false)
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.weight(0.06f))
        RecentSearchSection(setEdit, isEdit)
        RecentSearchKeywords(isEdit, recentSearchList, onClickedKeyword, onDeleteKeyword)
        Spacer(modifier = Modifier.weight(0.08f))
        HotKeywordSection(modifier = Modifier)
        Spacer(modifier = Modifier.weight(0.06f))
        HotKeywords(keywords = hotSearchList, onClick = onClickedKeyword)
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun SearchTabScreen(
    modifier: Modifier = Modifier,
    keyword: String = "",
    searchUser: (String) -> Unit = {},
    searchFrame: (String) -> Unit = {},
    searchAsset: (String) -> Unit = {},
) {
    val tabList = listOf("사용자", "프레임", "에셋")
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        CustomTab(
            modifier = Modifier.fillMaxWidth(),
            tabList = tabList,
            selectedIndex = selectedIndex,
            onTabSelected = { index ->
                selectedIndex = index
            },
        )
        when (selectedIndex) {
            0 -> {
                searchUser(keyword)
            }

            1 -> {
                searchFrame(keyword)
            }

            2 -> {
                searchAsset(keyword)
            }
        }
    }
}

@Composable
fun SearchSuccessScreen(
    modifier: Modifier = Modifier,
    userList: List<Profile> = emptyList(),
    frameList: List<FrameDetail> = emptyList(),
    assetList: List<AssetDetail> = emptyList(),
    navigateToProfile: (Long) -> Unit = {},
) {
    if (userList.isNotEmpty()) {
        LazyColumn(
            modifier =
                modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(userList.size) { index ->
                val user = userList[index]
                UserListItem(
                    userId = user.userId,
                    profileImgUrl = user.profileImageUrl,
                    nickname = user.nickname,
                    navigateToProfile = navigateToProfile,
                )
            }
        }
    } else if (frameList.isNotEmpty()) {
        LazyVerticalGrid(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp),
            columns = GridCells.Fixed(2),
            state = rememberLazyGridState(),
        ) {
            items(frameList.size) { index ->
                val frame = frameList[index]
                val ipfsUrl = BuildConfig.IPFS_READ_URL
                val imgUrl = ipfsUrl + "ipfs/" + frame.nftInfo.data.image

                GlideImage(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f)
                            .padding(8.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .border(
                                width = 1.dp,
                                shape = RoundedCornerShape(10.dp),
                                color = Gray03,
                            ),
                    imageModel = imgUrl.toUri(),
                    contentScale = ContentScale.Fit,
                )
            }
        }
    } else if (assetList.isNotEmpty()) {
        LazyVerticalGrid(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp),
            columns = GridCells.Fixed(3),
            state = rememberLazyGridState(),
        ) {
            items(assetList.size) { index ->
                val asset = assetList[index]
                GlideImage(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(8.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .border(
                                width = 1.dp,
                                shape = RoundedCornerShape(10.dp),
                                color = Gray03,
                            ),
                    imageModel = asset.imageUrls.toUri(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

@Composable
fun UserListItem(
    modifier: Modifier = Modifier,
    userId: Long = 0L,
    profileImgUrl: String? = "",
    nickname: String = "",
    navigateToProfile: (Long) -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .noRippleClickable {
                    navigateToProfile(userId)
                },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(50.dp)
                    .clip(CircleShape),
        ) {
            GlideImage(
                imageModel = profileImgUrl?.toUri(),
                contentScale = ContentScale.Crop,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = nickname,
            style = Typography.bodySmall.copy(fontSize = 14.sp),
        )
    }
}

@Composable
private fun RecentSearchKeywords(
    isEdit: Boolean,
    recentSearchList: List<String>,
    onClickedKeyword: (String) -> Unit,
    onDeleteKeyword: (String) -> Unit,
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        modifier = Modifier.height(50.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = {
            items(recentSearchList) { item ->
                HashTag(
                    modifier = Modifier,
                    keyword = item,
                    isEdit = isEdit,
                    onTagClicked = onClickedKeyword,
                    onCloseClicked = onDeleteKeyword,
                )
            }
        },
    )
}

@Composable
private fun RecentSearchSection(
    setEdit: (Boolean) -> Unit,
    isEdit: Boolean,
) {
    val mode =
        if (isEdit) {
            stringResource(id = R.string.edit_true_text)
        } else {
            stringResource(id = R.string.edit_false_text)
        }
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = R.drawable.img_clock),
            contentDescription = null,
        )
        SectionHeader(
            modifier = Modifier,
            text = stringResource(R.string.recent_search_text),
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier =
                Modifier.noRippleClickable {
                    setEdit(isEdit.not())
                },
            text = mode,
            style = Typography.labelLarge.copy(color = Gray01),
        )
    }
}

@Composable
fun HotKeywordSection(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = R.drawable.img_fire),
            contentDescription = null,
        )
        SectionHeader(modifier = Modifier, text = stringResource(R.string.hot_section_text))
    }
}

@Composable
fun HotKeywords(
    keywords: List<String> = listOf(),
    onClick: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        repeat(keywords.size) { index ->
            val item = keywords[index]
            HotKeyWord(
                modifier =
                    Modifier.noRippleClickable {
                        onClick(item)
                    },
                number = index + 1,
                keyword = item,
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun SearchScreenPreview() {
    SemonemoTheme {
    }
}
