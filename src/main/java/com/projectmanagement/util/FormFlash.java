package com.projectmanagement.util;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Carries a rejected form and its validation errors across a redirect, so that
 * validation failures can follow Post-Redirect-Get instead of forwarding to the
 * form view from inside the POST.
 *
 * <p>Both the bound object and its {@link BindingResult} are stored as flash
 * attributes. Spring copies the flash map into the model before the redirect
 * target's handler runs, so the GET handler only has to avoid overwriting them
 * (see the {@code containsAttribute} guards in the form controllers) and the
 * {@code <form:...>} tags re-render the submitted values and messages exactly as
 * they did on a forward.
 */
public final class FormFlash {

    private FormFlash() {}

    public static void flashErrors(RedirectAttributes redirectAttributes, String modelAttributeName,
            Object target, BindingResult bindingResult) {
        redirectAttributes.addFlashAttribute(modelAttributeName, target);
        redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + modelAttributeName, bindingResult);
    }
}
