package com.eventify.exception;

public class NotEnoughTicketsException extends RuntimeException {
    public NotEnoughTicketsException(String msg) { super(msg); }
}