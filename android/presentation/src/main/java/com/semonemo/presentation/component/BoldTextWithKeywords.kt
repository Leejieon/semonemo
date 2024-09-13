package com.semonemo.presentation.component

import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.semonemo.presentation.R
import com.semonemo.presentation.theme.Typography
import com.semonemo.presentation.ui.theme.Main02

/*
@Param  fullText : 전체 텍스트
        keywords : 강조할 키워드
        brushFlag : brush 여부 , true -> brush on | false -> brush off
        style : TextStyle
 */
@Composable
fun BoldTextWithKeywords(
    modifier: Modifier = Modifier,
    fullText: String,
    keywords: List<String>,
    brushFlag: List<Boolean>,
    boldStyle: TextStyle, // 키워드에 적용될 스타일
    normalStyle: TextStyle, // 일반 텍스트에 적용될 스타일
) {
    val annotatedText =
        buildAnnotatedString {
            var currentIndex = 0
            keywords.forEachIndexed { index, keyword ->
                val keywordIndex = fullText.indexOf(keyword, currentIndex)
                if (keywordIndex >= 0) {
                    // keyword 이외의 텍스트를 regular 스타일로 추가
                    withStyle(
                        style =
                            SpanStyle(
                                fontWeight = normalStyle.fontWeight ?: FontWeight.Normal,
                                fontSize = normalStyle.fontSize,
                                color = normalStyle.color,
                                letterSpacing = normalStyle.letterSpacing,
                            ),
                    ) {
                        append(fullText.substring(currentIndex, keywordIndex))
                    }
                    // keyword 텍스트에 원하는 스타일 적용
                    withStyle(
                        style =
                            SpanStyle(
                                fontFamily = FontFamily(Font(R.font.pretendard_extrabold)),
                                brush = if (brushFlag[index]) Main02 else null,
                                fontSize = boldStyle.fontSize,
                                letterSpacing = boldStyle.letterSpacing,
                            ),
                    ) {
                        append(keyword)
                    }
                    currentIndex = keywordIndex + keyword.length
                }
            }
            // 마지막 남은 텍스트 regular 스타일로 처리
            if (currentIndex < fullText.length) {
                withStyle(
                    style =
                        SpanStyle(
                            fontWeight = normalStyle.fontWeight ?: FontWeight.Normal,
                            fontSize = normalStyle.fontSize,
                            color = normalStyle.color,
                            letterSpacing = normalStyle.letterSpacing,
                        ),
                ) {
                    append(fullText.substring(currentIndex))
                }
            }
        }

    Text(modifier = modifier, text = annotatedText)
}

@Preview(showBackground = true)
@Composable
fun PreviewBoldTextWithKeywords() {
    val text = "오늘의 추억을 프레임 속에 담다"
    val boldKeywords = listOf("추억", "프레임")
    val brushFlag = listOf(true, true)
    BoldTextWithKeywords(
        modifier = Modifier,
        fullText = text,
        keywords = boldKeywords,
        brushFlag,
        boldStyle = Typography.titleSmall.copy(fontSize = 17.sp),
        normalStyle = Typography.labelLarge,
    )
}