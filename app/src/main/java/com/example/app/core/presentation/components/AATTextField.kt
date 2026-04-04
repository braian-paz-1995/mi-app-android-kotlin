package com.ationet.androidterminal.core.presentation.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.util.DisplayType
import com.ationet.androidterminal.ui.theme.AATColorScheme
import com.ationet.androidterminal.ui.theme.DarkGray
import com.ationet.androidterminal.ui.theme.Gray
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun AATTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    outlined: Boolean = false,
    readOnly: Boolean = false,
    textStyle: TextStyle = if (!outlined) LocalTextStyle.current else LocalTextStyle.current.copy(
        fontSize = 18.sp,
        textAlign = TextAlign.Center
    ),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 3,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = if (!outlined) RoundedCornerShape(
        topStart = 15.dp,
        topEnd = 15.dp
    ) else RoundedCornerShape(15.dp),
    colors: TextFieldColors = if (!outlined) {
        TextFieldDefaults.colors(
            unfocusedContainerColor = Gray,
            focusedContainerColor = Gray,
            disabledContainerColor = Gray,
            errorContainerColor = Gray,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            errorIndicatorColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            errorTrailingIconColor = MaterialTheme.colorScheme.error,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            disabledTrailingIconColor = Gray,
            unfocusedTextColor = AATColorScheme.onSecondary,
            focusedTextColor = AATColorScheme.onSecondary,
            errorSupportingTextColor = AATColorScheme.error,
            errorTextColor = AATColorScheme.error
        )
    } else {
        OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledBorderColor = Gray,
            disabledPrefixColor = Color.Transparent,
            disabledTrailingIconColor = Color.Transparent,
            disabledSuffixColor = Color.Transparent,
            disabledPlaceholderColor = Color.Transparent,
            disabledSupportingTextColor = Color.Transparent,
            disabledLeadingIconColor = Color.Transparent,
            unfocusedTextColor = AATColorScheme.onSecondary,
            focusedTextColor = AATColorScheme.onSecondary,
            errorSupportingTextColor = AATColorScheme.error,
            errorTextColor = AATColorScheme.error
        )
    },
    isUnderline: Boolean = false,
    displayType: DisplayType? = null,
    quantityOrAmount: String = "",
) {

    if (!outlined) {
        if (isUnderline) {

            Column {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {

                    if (displayType == DisplayType.AMOUNT) {
                        Text(
                            text = quantityOrAmount,
                            fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    TextField(
                        value = value,
                        onValueChange = { onValueChange(it) },
                        modifier = Modifier
                            .weight(1f)
                            .then(modifier),
                        enabled = true,
                        readOnly = readOnly,
                        textStyle = TextStyle(
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        label = label,
                        placeholder = placeholder,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon,
                        prefix = prefix,
                        suffix = suffix,
                        //supportingText = supportingText,
                        isError = isError,
                        visualTransformation = visualTransformation,
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        singleLine = singleLine,
                        maxLines = maxLines,
                        minLines = minLines,
                        interactionSource = interactionSource,
                        shape = RectangleShape,
                        colors = colors.copy(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                            focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                            errorIndicatorColor = MaterialTheme.colorScheme.error,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    if (displayType == DisplayType.QUANTITY) {
                        Text(
                            text = quantityOrAmount,
                            fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                val spacer = Spacer(modifier = Modifier.height(4.dp))
                if (isError && supportingText != null) {
                    Box(modifier = Modifier.height(20.dp)) {
                        spacer
                        supportingText()

                    }
                } else {
                    Box(modifier = Modifier.height(20.dp))
                }
            }
        } else {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                enabled = false,
                readOnly = readOnly,
                textStyle = textStyle,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = prefix,
                suffix = suffix,
                supportingText = supportingText,
                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                interactionSource = interactionSource,
                shape = shape,
                colors = colors,
            )
            val spacerError = if (isError && supportingText != null)
                0.dp
            else
                10.dp
            Box(modifier = Modifier.height(spacerError))
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            label?.invoke()
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                enabled = false,
                readOnly = readOnly,
                textStyle = textStyle,
                label = null,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = prefix,
                suffix = suffix,
                supportingText = supportingText,
                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                interactionSource = interactionSource,
                shape = shape,
                colors = colors,
            )
        }
    }
}


@Composable
fun AATTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    outlined: Boolean = false,
    readOnly: Boolean = false,
    textStyle: TextStyle = if (!outlined) LocalTextStyle.current else LocalTextStyle.current.copy(
        fontSize = 18.sp,
        textAlign = TextAlign.Center
    ),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 3,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = if (!outlined) RoundedCornerShape(
        topStart = 15.dp,
        topEnd = 15.dp
    ) else RoundedCornerShape(15.dp),
    colors: TextFieldColors = if (!outlined) {
        TextFieldDefaults.colors(
            unfocusedContainerColor = Gray,
            focusedContainerColor = Gray,
            disabledContainerColor = Gray,
            errorContainerColor = Gray,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            errorIndicatorColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            errorTrailingIconColor = MaterialTheme.colorScheme.error,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            disabledTrailingIconColor = Gray,
            unfocusedTextColor = AATColorScheme.onSecondary,
            focusedTextColor = AATColorScheme.onSecondary,
            errorSupportingTextColor = AATColorScheme.error,
            errorTextColor = AATColorScheme.error
        )
    } else {
        OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledBorderColor = Gray,
            disabledPrefixColor = Color.Transparent,
            disabledTrailingIconColor = Color.Transparent,
            disabledSuffixColor = Color.Transparent,
            disabledPlaceholderColor = Color.Transparent,
            disabledSupportingTextColor = Color.Transparent,
            disabledLeadingIconColor = Color.Transparent,
            unfocusedTextColor = AATColorScheme.onSecondary,
            focusedTextColor = AATColorScheme.onSecondary,
            errorSupportingTextColor = AATColorScheme.error,
            errorTextColor = AATColorScheme.error
        )
    },
    isUnderline: Boolean = false,
    displayType: DisplayType? = null,
    quantityOrAmount: String = "",
    maxLength: Int? = null
) {

    if (!outlined) {
        if (isUnderline) {
            val lengthSupportingText: @Composable (() -> Unit)? = if (maxLength == null) null else {
                {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Text(
                            text = "${value.length}/$maxLength",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = DarkGray
                            ),
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            Column {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {

                    if (displayType == DisplayType.AMOUNT) {
                        Text(
                            text = quantityOrAmount,
                            fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    TextField(
                        value = value,
                        onValueChange = {
                            if (maxLength != null) {
                                if (it.length <= maxLength) {
                                    onValueChange(it)
                                }
                            } else {
                                onValueChange(it)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .then(modifier),
                        enabled = true,
                        readOnly = readOnly,
                        textStyle = TextStyle(
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        label = label,
                        placeholder = placeholder,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon,
                        prefix = prefix,
                        suffix = suffix,
                        //supportingText = supportingText,
                        isError = isError,
                        visualTransformation = visualTransformation,
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        singleLine = singleLine,
                        maxLines = maxLines,
                        minLines = minLines,
                        interactionSource = interactionSource,
                        shape = RectangleShape,
                        colors = colors.copy(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                            focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                            errorIndicatorColor = MaterialTheme.colorScheme.error,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    if (displayType == DisplayType.QUANTITY) {
                        Text(
                            text = quantityOrAmount,
                            fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.heightIn(20.dp)) {
                    if (isError && supportingText != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            supportingText?.invoke()
                        }
                    }
                    if (lengthSupportingText != null) {
                        Box(
                            modifier = Modifier,
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            lengthSupportingText()
                        }
                    }
                }
            }
        } else {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                enabled = false,
                readOnly = readOnly,
                textStyle = textStyle,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = prefix,
                suffix = suffix,
                supportingText = supportingText,
                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                interactionSource = interactionSource,
                shape = shape,
                colors = colors,
            )
            val spacerError = if (isError && supportingText != null)
                0.dp
            else
                10.dp
            Box(modifier = Modifier.height(spacerError))
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            label?.invoke()
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                enabled = false,
                readOnly = readOnly,
                textStyle = textStyle,
                label = null,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = prefix,
                suffix = suffix,
                supportingText = supportingText,
                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                interactionSource = interactionSource,
                shape = shape,
                colors = colors,
            )
        }
    }


}

@Composable
fun AATTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    outlined: Boolean = false,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    textStyle: TextStyle = if (!outlined) LocalTextStyle.current else LocalTextStyle.current.copy(
        fontSize = 18.sp,
        textAlign = TextAlign.Center,
    ),
    label: String? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 3,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = if (!outlined) RoundedCornerShape(
        topStart = 15.dp,
        topEnd = 15.dp
    ) else RoundedCornerShape(15.dp),
    colors: TextFieldColors = if (!outlined) {
        TextFieldDefaults.colors(
            unfocusedContainerColor = Gray,
            focusedContainerColor = Gray,
            disabledContainerColor = Gray,
            errorContainerColor = Gray,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            errorIndicatorColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            errorTrailingIconColor = MaterialTheme.colorScheme.error,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            disabledTrailingIconColor = Gray,
            unfocusedTextColor = AATColorScheme.onSecondary,
            focusedTextColor = AATColorScheme.onSecondary,
            errorSupportingTextColor = AATColorScheme.error,
            errorTextColor = AATColorScheme.error
        )
    } else {
        OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledBorderColor = Gray,
            disabledPrefixColor = Color.Transparent,
            disabledTrailingIconColor = Color.Transparent,
            disabledSuffixColor = Color.Transparent,
            disabledPlaceholderColor = Color.Transparent,
            disabledSupportingTextColor = Color.Transparent,
            disabledLeadingIconColor = Color.Transparent,
            unfocusedTextColor = AATColorScheme.onSecondary,
            focusedTextColor = AATColorScheme.onSecondary,
            errorSupportingTextColor = AATColorScheme.error,
            errorTextColor = AATColorScheme.error
        )
    }
) {

    if (!outlined) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = label?.let { { Text(it) } },
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors,
        )
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {

            label?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                enabled = enabled,
                readOnly = readOnly,
                textStyle = textStyle,
                label = null,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = prefix,
                suffix = suffix,
                supportingText = supportingText,
                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                interactionSource = interactionSource,
                shape = shape,
                colors = colors,
            )
        }
    }


}

@Composable
fun AATTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    outlined: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = if (!outlined) LocalTextStyle.current else LocalTextStyle.current.copy(
        fontSize = 18.sp,
        textAlign = TextAlign.Center,
    ),
    label: String? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 3,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = if (!outlined) RoundedCornerShape(
        topStart = 15.dp,
        topEnd = 15.dp
    ) else RoundedCornerShape(15.dp),
    colors: TextFieldColors = if (!outlined) {
        TextFieldDefaults.colors(
            unfocusedContainerColor = Gray,
            focusedContainerColor = Gray,
            disabledContainerColor = Gray,
            disabledTextColor = DarkGray,
            disabledLabelColor = DarkGray,
            errorContainerColor = Gray,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            errorIndicatorColor = AATColorScheme.error,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            errorTrailingIconColor = AATColorScheme.error,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            disabledTrailingIconColor = Gray,
            unfocusedTextColor = AATColorScheme.onSecondary,
            focusedTextColor = AATColorScheme.onSecondary,
            errorSupportingTextColor = AATColorScheme.error,
            errorTextColor = AATColorScheme.error
        )
    } else {
        OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledBorderColor = Gray,
            disabledPrefixColor = Color.Transparent,
            disabledTrailingIconColor = Color.Transparent,
            disabledSuffixColor = Color.Transparent,
            disabledPlaceholderColor = Color.Transparent,
            disabledSupportingTextColor = Color.Transparent,
            disabledLeadingIconColor = Color.Transparent,
            unfocusedTextColor = AATColorScheme.onSecondary,
            focusedTextColor = AATColorScheme.onSecondary,
            errorSupportingTextColor = AATColorScheme.error,
            errorTextColor = AATColorScheme.error
        )
    },
    maxLength: Int? = null
) {

    if (!outlined) {

        val lengthSupportingText: @Composable (() -> Unit)? = if (maxLength == null) null else {
            {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Text(
                        text = "${value.length}/$maxLength",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = DarkGray
                        ),
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }


        Column {
            TextField(
                value = value,
                onValueChange = {
                    if (maxLength != null) {
                        if (it.length <= maxLength) {
                            onValueChange(it)
                        }
                    } else {
                        onValueChange(it)
                    }
                },
                modifier = modifier,
                enabled = enabled,
                readOnly = readOnly,
                textStyle = textStyle,
                label = label?.let { { Text(it) } },
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = prefix,
                suffix = suffix,
                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                interactionSource = interactionSource,
                shape = shape,
                colors = colors,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isError && supportingText != null) {
                    supportingText()
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (maxLength != null && lengthSupportingText != null) {
                    lengthSupportingText()
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
            }

        }
    } else {

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {

//            label?.let {
//                Text(text = it, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
//            }
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                enabled = false,
                readOnly = readOnly,
                textStyle = textStyle.copy(fontWeight = FontWeight.Bold),
                label = null,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = prefix,
                suffix = suffix,
                supportingText = supportingText,
                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                interactionSource = interactionSource,
                shape = shape,
                colors = colors,
            )
        }
    }
}

@Composable
fun GenericSingleLineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLength: Int? = null,
    isError: Boolean = false,
    supportingText: String? = null
) {
    AATTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = { onValueChange(it) },
        isUnderline = true,
        keyboardOptions = keyboardOptions,
        isError = isError,
        supportingText = {
            if (supportingText != null && isError) {
                Text(
                    text = supportingText,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(20.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                Box(modifier = Modifier.height(20.dp))
            }
        },
        maxLength = maxLength
    )
}

@Composable
fun AmountQuantityTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    quantityOrAmount: String,
    displayType: DisplayType,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val textMeasurer = rememberTextMeasurer()
    val textWidth = textMeasurer.measure(
        text = AnnotatedString(quantityOrAmount),
        style = TextStyle(fontSize = 28.sp)
    ).size.width

    val dynamicPadding = with(LocalDensity.current) { textWidth.toDp() }

    AATTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (displayType == DisplayType.QUANTITY) dynamicPadding else 0.dp,
                end = if (displayType == DisplayType.AMOUNT) dynamicPadding else 0.dp
            ),
        value = value,
        onValueChange = { onValueChange(it) },
        isUnderline = true,
        displayType = displayType,
        quantityOrAmount = quantityOrAmount,
        isError = isError,
        supportingText = {
            if (supportingText != null && isError) {
                Box(modifier = Modifier.height(20.dp)) {
                    Text(
                        text = supportingText,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Box(modifier = Modifier.height(20.dp))
            }
        },
        keyboardOptions = keyboardOptions
    )
}

@Composable
fun TextFieldWithCounter(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    maxLength: Int,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    supportingText: String? = null
) {
    AATTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = label,
        enabled = enabled,
        outlined = false,
        maxLength = maxLength,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = {
            Text(
                text = supportingText?.let { it } ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    )
}

@Composable
fun TextFieldOutlined(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    supportingText: String? = null
) {
    AATTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = label,
        enabled = enabled,
        outlined = false,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = {
            Text(
                text = supportingText?.let { it } ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    )
}


@NewlandPreview
@Composable
private fun PreviewAATTextField() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AATTextField(
                modifier = Modifier.fillMaxWidth(),
                value = TextFieldValue("Premium regular diesel"),
                onValueChange = {},
                outlined = true,
                enabled = false
            )
            AATTextField(
                modifier = Modifier.fillMaxWidth(),
                value = TextFieldValue("Premium regular diesel"),
                onValueChange = {},
                outlined = true,
                enabled = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                ),
                placeholder = { TextFieldValue(stringResource(R.string.select_the_currency)) },
            )

            GenericSingleLineTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "testing@company.com",
                onValueChange = {},
            )
            GenericSingleLineTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "m",
                onValueChange = {},
                isError = true,
                supportingText = "Pasaron cosas",
            )
            GenericSingleLineTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "m",
                onValueChange = {},
                isError = true,
                supportingText = "Pasaron cosas",
                maxLength = 10
            )

            AmountQuantityTextField(
                modifier = Modifier.fillMaxWidth(),
                value = TextFieldValue("testing@company.com"),
                onValueChange = {},
                displayType = DisplayType.AMOUNT,
                quantityOrAmount = "$"
            )
            AmountQuantityTextField(
                modifier = Modifier.fillMaxWidth(),
                value = TextFieldValue("testing@company.com"),
                onValueChange = {},
                displayType = DisplayType.QUANTITY,
                quantityOrAmount = "L"
            )

            TextFieldOutlined(
                modifier = Modifier.fillMaxWidth(),
                value = "",
                onValueChange = { },
                label = stringResource(R.string.unit_price),
                isError = true,
                supportingText = "Pasaron cosas",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                    showKeyboardOnFocus = true
                )
            )

            TextFieldWithCounter(
                modifier = Modifier.fillMaxWidth(),
                value = "enabled@company.com",
                onValueChange = {},
                label = "Email",
                enabled = true,
                maxLength = 3,
                isError = true,
                supportingText = "Pasaron cosas"
            )

            AATTextField(
                modifier = Modifier.fillMaxWidth(),
                value = TextFieldValue("pedrito"),
                onValueChange = {},
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation('*'),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.TwoTone.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
            AATTextField(
                modifier = Modifier.fillMaxWidth(),
                value = TextFieldValue("pedrito"),
                isError = true,
                supportingText = {
                    Text("Pasaron cosas", fontSize = 10.sp)
                },
                onValueChange = {},
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation('*'),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.TwoTone.Lock,
                        contentDescription = null,
                    )
                }
            )
        }
    }
}