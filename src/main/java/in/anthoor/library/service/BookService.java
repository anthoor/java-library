package in.anthoor.library.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import in.anthoor.library.entity.Book;
import in.anthoor.library.repository.BookRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final DataSource dataSource;

    @Autowired
    public BookService(BookRepository bookRepository, DataSource dataSource) {
        this.bookRepository = bookRepository;
        this.dataSource = dataSource;
    }

    public List<Book> findAll() {
        return IterableUtils.toList(bookRepository.findAll());
    }

    public Book findById(long id) {
        return bookRepository.findById(id).stream().findFirst().orElse(null);
    }

    public List<Book> findByBookTitle(String title) {
        return bookRepository.findByBookTitle(title);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public int updateAvailability(long id, boolean isAvailable) {
        return bookRepository.updateAvailability(id, isAvailable ? 1 : 0);
    }

    public JsonNode getAllDetails() {
        final String query = "SELECT B.BOOK_ID, B.BOOK_TITLE, B.BOOK_YEAR, B.BOOK_SHELF, B.BOOK_ROW, A.AUTHOR_NAME, P.PUBLISHER_NAME, C.CATEGORY_NAME, B.BOOK_AVAILABLE FROM BOOK B INNER JOIN BOOK_AUTHOR BA ON B.BOOK_ID = BA.BOOK_ID INNER JOIN AUTHOR A ON A.AUTHOR_ID = BA.AUTHOR_ID INNER JOIN BOOK_PUBLISHER BP ON B.BOOK_ID = BP.BOOK_ID INNER JOIN PUBLISHER P ON BP.PUBLISHER_ID = P.PUBLISHER_ID LEFT OUTER JOIN BOOK_CATEGORY BC ON BC.BOOK_ID = B.BOOK_ID LEFT OUTER JOIN CATEGORY C ON C.CATEGORY_ID = BC.CATEGORY_ID";
        ArrayNode resultNode = JsonNodeFactory.instance.arrayNode();

        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int bookId = resultSet.getInt(1);
                String bookTitle = resultSet.getString(2);
                int bookYear = resultSet.getInt(3);
                int bookShelf = resultSet.getInt(4);
                int bookRow = resultSet.getInt(5);
                String bookAuthor = resultSet.getString(6);
                String bookPublisher = resultSet.getString(7);
                String bookCategory = resultSet.getString(8);
                String bookAvailability = resultSet.getInt(9) == 1 ? "Available" : "Not available";
                JsonNode node = getBookNode(resultNode, bookId);
                if (node == null) {
                    node = JsonNodeFactory.instance.objectNode();
                    ((ObjectNode) node).set("id", new TextNode(String.valueOf(bookId)));
                    ((ObjectNode) node).set("title", new TextNode(bookTitle));
                    ((ObjectNode) node).set("year", new TextNode(String.valueOf(bookYear)));
                    ((ObjectNode) node).set("shelf", new TextNode(String.valueOf(bookShelf)));
                    ((ObjectNode) node).set("row", new TextNode(String.valueOf(bookRow)));
                    ((ObjectNode) node).set("availability", new TextNode(bookAvailability));
                    ArrayNode authorNode = JsonNodeFactory.instance.arrayNode();
                    authorNode.add(new TextNode(bookAuthor));
                    ((ObjectNode) node).set("authors", authorNode);
                    ArrayNode publisherNode = JsonNodeFactory.instance.arrayNode();
                    publisherNode.add(new TextNode(bookPublisher));
                    ((ObjectNode) node).set("publishers", publisherNode);
                    ArrayNode categoryNode = JsonNodeFactory.instance.arrayNode();
                    categoryNode.add(new TextNode(bookCategory));
                    ((ObjectNode) node).set("categories", categoryNode);
                    resultNode.add(node);
                } else {
                    ArrayNode authorNode = (ArrayNode) node.at("/authors");
                    if (isUnique(authorNode, bookAuthor)) {
                        authorNode.add(new TextNode(bookAuthor));
                    }
                    ArrayNode publisherNode = (ArrayNode) node.at("/publishers");
                    if (isUnique(publisherNode, bookPublisher)) {
                        publisherNode.add(new TextNode(bookPublisher));
                    }
                    ArrayNode categoryNode = (ArrayNode) node.at("/categories");
                    if (isUnique(categoryNode, bookCategory)) {
                        categoryNode.add(new TextNode(bookCategory));
                    }
                }
            }
            ObjectNode finalNode = JsonNodeFactory.instance.objectNode();
            finalNode.set("books", resultNode);
            return finalNode;
        } catch (SQLException sqlException) {
            System.err.println(sqlException);
        }
        return null;
    }

    private JsonNode getBookNode(ArrayNode arrayNode, int id) {
        for (JsonNode node : arrayNode) {
            if (node.at("/id").asInt() == id) {
                return node;
            }
        }
        return null;
    }

    private boolean isUnique(ArrayNode arrayNode, String value) {
        for (JsonNode node : arrayNode) {
            if (node.asText().equals(value)) {
                return false;
            }
        }
        return true;
    }
}
