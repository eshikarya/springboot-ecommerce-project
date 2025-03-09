package com.ecommerce.project.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    String resourceName;
    String fieldName;
    String field;
    long fieldId;

    public ResourceNotFoundException(String resourceName, String fieldName, String field) {
        super(String.format("%s not found with %s: %s",resourceName,field,fieldName));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.field = field;
    }

    public ResourceNotFoundException(String resourceName, String field, long fieldId) {
        super(String.format("%s not found with %s: %d",resourceName,field,fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }

    public ResourceNotFoundException() {
    }
}
