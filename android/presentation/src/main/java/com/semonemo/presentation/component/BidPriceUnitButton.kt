package com.semonemo.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.semonemo.presentation.R
import com.semonemo.presentation.theme.Gray01
import com.semonemo.presentation.theme.SemonemoTheme
import com.semonemo.presentation.theme.Typography
import com.semonemo.presentation.theme.White

@Composable
fun BidPriceUnitButton(
    modifier: Modifier = Modifier,
    bidPricePercentage: Int = 10,
    bidPriceUnit: Long = 1000,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        color = White,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Gray01),
    ) {
        Box(
            modifier = Modifier.padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "$bidPricePercentage%",
                    style = Typography.bodySmall,
                )

                Text(
                    text =
                        String.format(
                            "%,d ${stringResource(id = R.string.coin_price_unit)}",
                            bidPriceUnit,
                        ),
                    style =
                        Typography.labelMedium.copy(
                            fontFeatureSettings = "tnum",
                            fontSize = 12.sp,
                        ),
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun BidPriceUnitButtonPreview() {
    SemonemoTheme {
        BidPriceUnitButton()
    }
}