package com.projectmanagement.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.projectmanagement.models.Task;

/**
 * Validates the edit-task form, which submits only the two effort fields — the
 * task name is neither editable there nor touched by
 * {@code TaskDao.saveTaskEdit}, so {@link TaskValidator} (which validates the
 * name) does not apply to it.
 */
@Component
public class TaskEditValidator implements Validator {

    @Override
    public boolean supports(Class<?> type) {
        return Task.class.isAssignableFrom(type);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Task task = (Task) target;
        rejectIfNegative(errors, "effortEstimate", task.getEffortEstimate(), "Effort estimate cannot be negative");
        rejectIfNegative(errors, "effortLogged", task.getEffortLogged(), "Logged effort cannot be negative");
    }

    private void rejectIfNegative(Errors errors, String fieldName, double value, String message) {
        // A field that failed type conversion already carries an error and binds as
        // 0.0, so skip it rather than stacking a second, misleading message.
        if (errors.getFieldError(fieldName) == null && value < 0.0) {
            errors.rejectValue(fieldName, "negative-"+fieldName, message);
        }
    }
}
