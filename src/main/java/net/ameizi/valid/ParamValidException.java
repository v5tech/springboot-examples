package net.ameizi.valid;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParamValidException extends Exception {

    private List<FieldError> fieldErrors = new ArrayList<>();

    public ParamValidException(List<FieldError> errors) {
        this.fieldErrors = errors;
    }

    public ParamValidException(BindException ex) {
        this.fieldErrors = bindExceptionToFieldError(ex);
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public ParamValidException(ConstraintViolationException violationException, MethodParameter[] methodParameters) {
        this.fieldErrors = violationException.getConstraintViolations().stream().map(constraintViolation -> {
            PathImpl pathImpl = (PathImpl) constraintViolation.getPropertyPath();
            int paramIndex = pathImpl.getLeafNode().getParameterIndex();
            String paramName = methodParameters[paramIndex].getParameterName();
            FieldError error = new FieldError();
            error.setName(paramName);
            error.setMessage(constraintViolation.getMessage());
            return error;
        }).collect(Collectors.toList());
    }

    private List<FieldError> bindExceptionToFieldError(BindException ex) {
        return ex.getFieldErrors().stream().map(f -> {
            FieldError error = new FieldError();
            error.setName(f.getObjectName() + "." + f.getField());
            error.setMessage(f.getDefaultMessage());
            return error;
        }).collect(Collectors.toList());
    }


    @Override
    public String getMessage() {
        return fieldErrors.toString();
    }
}
