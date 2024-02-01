package com.cubetiqs.api.book;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import io.helidon.config.Config;
import io.helidon.http.Status;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

class BookService implements HttpService {

    private final ConcurrentMap<String, Book> books = new java.util.concurrent.ConcurrentHashMap<>();

    BookService() {
        this(Config.global().get("app"));
    }

    BookService(Config appConfig) {
        books.put("1", new Book("1", "The Hobbit", "J.R.R. Tolkien", "978-0618260300", "Fantasy"));
        books.put("2", new Book("2", "The Fellowship of the Ring", "J.R.R. Tolkien", "978-0618346257", "Fantasy"));
        books.put("3", new Book("3", "The Two Towers", "J.R.R. Tolkien", "978-0618346264", "Fantasy"));
        books.put("4", new Book("4", "The Return of the King", "J.R.R. Tolkien", "978-0618346271", "Fantasy"));
    }

    @Override
    public void routing(HttpRules rules) {
        rules
                .get("/", this::getAllBooksHandler)
                .get("/{id}", this::getBookHandler)
                .put("/", this::updateBookHandler)
                .post("/", this::createBookHandler)
                .delete("/{id}", this::deleteBookHandler);
    }

    private void getAllBooksHandler(ServerRequest request, ServerResponse response) {
        List<Book> list = new ArrayList<>(books.values());
        response.send(list);
    }

    private void getBookHandler(ServerRequest request, ServerResponse response) {
        String id = request.path().pathParameters().get("id");
        if (id == null) {
            response.status(Status.BAD_REQUEST_400).send();
            return;
        }

        Book book = books.get(id);
        if (book != null) {
            response.send(book);
        } else {
            response.status(Status.NOT_FOUND_404).send();
        }
    }

    private void updateBookHandler(ServerRequest request, ServerResponse response) {
        String id = request.path().pathParameters().get("id");
        if (id == null) {
            response.status(Status.BAD_REQUEST_400).send();
            return;
        }

        Book input = request.content().as(Book.class);
        if (input == null) {
            response.status(Status.BAD_REQUEST_400).send();
            return;
        }

        Book book = books.get(id);
        if (book != null) {
            books.put(id, new Book(id, input.title(), input.author(), input.isbn(), input.category()));
            response.status(Status.NO_CONTENT_204).send();
        } else {
            response.status(Status.NOT_FOUND_404).send();
        }
    }

    private void createBookHandler(ServerRequest request, ServerResponse response) {
        Book input = request.content().as(Book.class);
        if (input == null) {
            response.status(Status.BAD_REQUEST_400).send();
            return;
        }

        String id = String.valueOf(books.size() + 1);
        Book book = new Book(id, input.title(), input.author(), input.isbn(), input.category());
        books.put(id, book);

        response.status(Status.CREATED_201).send(book);
    }

    private void deleteBookHandler(ServerRequest request, ServerResponse response) {
        String id = request.path().pathParameters().get("id");
        if (id == null) {
            response.status(Status.BAD_REQUEST_400).send();
            return;
        }

        Book book = books.remove(id);
        if (book != null) {
            response.status(Status.NO_CONTENT_204).send();
        } else {
            response.status(Status.NOT_FOUND_404).send();
        }
    }
}
