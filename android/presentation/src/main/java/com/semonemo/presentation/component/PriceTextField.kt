package com.semonemo.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.semonemo.presentation.theme.Gray02
import com.semonemo.presentation.theme.GunMetal
import com.semonemo.presentation.theme.Typography
import com.semonemo.presentation.theme.WhiteGray

@Composable
fun PriceTextField(
    modifier: Modifier = Modifier,
    onPriceChange: (String) -> Unit,
    price: String,
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = price,
            onValueChange = { newValue ->
                // 숫자만 허용
                if (newValue.all { it.isDigit() }) {
                    onPriceChange(newValue)
                }
            },
            textStyle = Typography.bodySmall.copy(fontSize = 13.sp),
            placeholder = {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = "0",
                    style = Typography.bodySmall.copy(fontSize = 13.sp),
                    color = Gray02,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = GunMetal,
                    unfocusedTextColor = Gray02,
                    focusedContainerColor = WhiteGray,
                    unfocusedContainerColor = WhiteGray,
                ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        Text(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 13.dp),
            text = "백구",
            style = Typography.bodySmall.copy(fontSize = 13.sp),
            color = Gray02,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PriceTextFieldPreview() {
    var price by remember { mutableStateOf("") }

    PriceTextField(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(10.dp),
        price = price,
        onPriceChange = { newPrice ->
            price = newPrice
        },
    )
}
