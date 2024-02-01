package com.cubetiqs.api.book;

public record Book(
        String id,
        String title,
        String author,
        String isbn,
        String category) {
}