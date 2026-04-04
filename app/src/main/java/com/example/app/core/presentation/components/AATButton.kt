package com.ationet.androidterminal.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Lock
import androidx.compose.material.icons.twotone.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ationet.androidterminal.core.presentation.util.debounced
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun AATButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = backgroundColor,
        disabledContainerColor = MaterialTheme.colorScheme.onBackground,
        disabledContentColor = MaterialTheme.colorScheme.surface
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp, 15.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    fontSize: TextUnit = 16.sp,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = debounced(onClick),
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            ProvideTextStyle(value = TextStyle(fontSize = fontSize)) {
                content()
            }
        },
    )
}

@Composable
fun AATTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(30.dp),
    backgroundColor: Color = Color.Transparent,
    colors: ButtonColors = ButtonDefaults.textButtonColors(
        containerColor = backgroundColor,
    ),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    textColor: Color = Color.Black,
    fontSize: Int = 16,
    leadingIcon: @Composable (RowScope.() -> Unit)? = null,
) {
    TextButton(
        onClick = debounced(onClick),
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            leadingIcon?.invoke(this)
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = fontSize.sp
            )
        },
    )
}

@Composable
fun AATButtonIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = backgroundColor,
        disabledContainerColor = MaterialTheme.colorScheme.onBackground,
        disabledContentColor = MaterialTheme.colorScheme.surface
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    fontSize: TextUnit = 16.sp,
    contentPadding: PaddingValues = PaddingValues(10.dp, 15.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = debounced(onClick),
        modifier = Modifier
            .height(58.dp)
            .then(modifier),
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            ProvideTextStyle(value = TextStyle(fontSize = fontSize)) {
                content()
            }
        },
    )
}

@Composable
fun AATButtonCard(
    onClick: () -> Unit,
    height: Dp = Dp.Unspecified,
    width: Dp = Dp.Unspecified,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(30.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = backgroundColor,
        disabledContainerColor = MaterialTheme.colorScheme.onBackground,
        disabledContentColor = Color.White
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(
        defaultElevation = 4.dp
    ),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    AATButton(
        onClick = debounced(onClick),
        modifier = modifier.then(
            if (height == Dp.Unspecified && width == Dp.Unspecified) Modifier
            else if (height == Dp.Unspecified) Modifier.width(width)
            else if (width == Dp.Unspecified) Modifier.height(height)
            else
                Modifier
                    .height(height)
                    .width(width)
        ),
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}


@NewlandPreview
@Composable
private fun PreviewAATButton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AATButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}) { Text("Let's complete it!") }
            AATButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
                backgroundColor = MaterialTheme.colorScheme.primary
            ) { Text("Clear transaction") }
            AATButtonIcon(
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
                backgroundColor = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    Text("Continue", modifier = Modifier.align(Alignment.Center))
                    Icon(
                        imageVector = Icons.TwoTone.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd),
                    )
                }
            }
            AATButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
                backgroundColor = MaterialTheme.colorScheme.primary,
                enabled = false
            ) { Text("Continue") }
            AATButtonIcon(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Scan your ID")
                            Icon(imageVector = Icons.TwoTone.Lock, contentDescription = null)
                        }
                    }
                }
            )
            AATTextButton(
                text = "Cancel",
                onClick = {},
                textColor = MaterialTheme.colorScheme.error
            )
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AATButtonCard(height = 100.dp, onClick = {}) {
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(baselineShift = BaselineShift.Superscript)) {
                            appendLine(
                                "Premium regular"
                            )
                        }
                        append("diesel")
                    }, fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                }
                AATButtonCard(
                    height = 100.dp,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.onSurface,
                    onClick = {}) {
                    Text(
                        buildAnnotatedString {
                            append("Premium diesel")
                        },
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
