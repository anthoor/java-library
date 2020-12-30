package in.anthoor.library.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import in.anthoor.library.entity.Book;
import in.anthoor.library.entity.BookLease;
import in.anthoor.library.repository.BookLeaseRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookLeaseService {

    private final BookLeaseRepository bookLeaseRepository;
    private final BookService bookService;
    private final DataSource dataSource;

    @Autowired
    public BookLeaseService(BookLeaseRepository bookLeaseRepository, BookService bookService, DataSource dataSource) {
        this.bookLeaseRepository = bookLeaseRepository;
        this.bookService = bookService;
        this.dataSource = dataSource;
    }

    public BookLease findById(long id) {
        return bookLeaseRepository.findById(id).stream().findFirst().orElse(null);
    }

    public List<BookLease> findAll() {
        return IterableUtils.toList(bookLeaseRepository.findAll());
    }

    public int save(BookLease bookLease) {
        bookService.updateAvailability(bookLease.getBook().getBookId(), false);
        return bookLeaseRepository.save(bookLease.getBook().getBookId(), bookLease.getLeasedPerson(), bookLease.getLeasedOn());
    }

    public int update(long id, long bookId) {
        Date returnDate = Date.valueOf(LocalDate.now());
        bookService.updateAvailability(bookId, true);
        return bookLeaseRepository.returnBook(id, returnDate);
    }

    public JsonNode getAllDetails() {
        final String query = "SELECT BL.BOOK_LEASE_ID, BL.BOOK_ID, BL.LEASED_PERSON, BL.LEASED_ON, BL.RETURNED_ON, B.BOOK_TITLE, B.BOOK_YEAR, B.BOOK_SHELF, B.BOOK_ROW, A.AUTHOR_NAME, P.PUBLISHER_NAME FROM BOOK_LEASE BL INNER JOIN BOOK B ON BL.BOOK_ID = B.BOOK_ID INNER JOIN BOOK_AUTHOR BA ON B.BOOK_ID = BA.BOOK_ID INNER JOIN AUTHOR A ON A.AUTHOR_ID = BA.AUTHOR_ID INNER JOIN BOOK_PUBLISHER BP ON BP.BOOK_ID = B.BOOK_ID INNER JOIN PUBLISHER P ON P.PUBLISHER_ID = BP.PUBLISHER_ID ORDER BY BL.RETURNED_ON, BL.LEASED_ON";
        ArrayNode resultNode = JsonNodeFactory.instance.arrayNode();
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int bookLeaseId = resultSet.getInt(1);
                int bookId = resultSet.getInt(2);
                String leasedPerson = resultSet.getString(3);
                Date leasedOn = resultSet.getDate(4);
                Date returnedOn = resultSet.getDate(5);
                String bookTitle = resultSet.getString(6);
                int bookYear = resultSet.getInt(7);
                int bookShelf = resultSet.getInt(8);
                int bookRow = resultSet.getInt(9);
                String authorName = resultSet.getString(10);
                String publisherName = resultSet.getString(11);
                JsonNode node = getLeaseNode(resultNode, bookLeaseId);
                if (node == null) {
                    node = JsonNodeFactory.instance.objectNode();
                    ((ObjectNode) node).set("id", new TextNode(String.valueOf(bookLeaseId)));
                    ((ObjectNode) node).set("leasedPerson", new TextNode(leasedPerson));
                    ((ObjectNode) node).set("leasedOn", new TextNode(leasedOn.toString()));
                    ((ObjectNode) node).set("returnedOn", new TextNode(returnedOn == null ? "" : returnedOn.toString()));
                    ObjectNode bookNode = JsonNodeFactory.instance.objectNode();
                    bookNode.set("id", new TextNode(String.valueOf(bookId)));
                    bookNode.set("title", new TextNode(bookTitle));
                    bookNode.set("year", new TextNode(String.valueOf(bookYear)));
                    bookNode.set("shelf", new TextNode(String.valueOf(bookShelf)));
                    bookNode.set("row", new TextNode(String.valueOf(bookRow)));
                    ArrayNode authorNode = JsonNodeFactory.instance.arrayNode();
                    authorNode.add(new TextNode(authorName));
                    bookNode.set("authors", authorNode);
                    ArrayNode publisherNode = JsonNodeFactory.instance.arrayNode();
                    publisherNode.add(new TextNode(publisherName));
                    bookNode.set("publishers", publisherNode);
                    ((ObjectNode) node).set("book", bookNode);
                    resultNode.add(node);
                } else {
                    ArrayNode authorNode = (ArrayNode) node.at("/book/authors");
                    if (isUnique(authorNode, authorName)) {
                        authorNode.add(new TextNode(authorName));
                    }
                    ArrayNode publisherNode = (ArrayNode) node.at("/book/publishers");
                    if (isUnique(publisherNode, publisherName)) {
                        publisherNode.add(new TextNode(publisherName));
                    }
                }
            }
            ObjectNode finalNode = JsonNodeFactory.instance.objectNode();
            finalNode.set("leases", resultNode);
            return finalNode;
        } catch (SQLException sqlException) {
            System.err.println(sqlException);
        }
        return null;
    }

    private JsonNode getLeaseNode(ArrayNode arrayNode, int id) {
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
