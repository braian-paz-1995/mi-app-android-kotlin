package com.ationet.androidterminal.core.change_pin.presentation.pin_prompt

sealed interface PinPromptAction {
    data class OnCurrentPinChange(val pin: String) : PinPromptAction
    data class OnConfirmPinChange(val pin: String) : PinPromptAction
    data class OnReConfirmPinChange(val pin: String) : PinPromptAction
    object OnToggleVisibilityCurrentPin : PinPromptAction
    object OnToggleVisibilityConfirmPin : PinPromptAction
    object OnToggleVisibilityReConfirmPin : PinPromptAction
    object OnNext : PinPromptAction
    object OnSubmit : PinPromptAction
    object OnBack : PinPromptAction
}