package com.ProHURT.exceptions;

/**
 * This method are handle errors of application.
 */
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String msg) {super(msg);}
}
