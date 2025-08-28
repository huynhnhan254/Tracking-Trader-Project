package books;

import GlobalConstants.BookSide;
import exception.*;
import quotes.*;
import tradable.*;
import user.*;

import java.util.HashMap;
import java.util.Random;

public class ProductManager {
    private final HashMap<String, ProductBook> productBooks;
    private static ProductManager instance;

    public ProductManager() {
        this.productBooks = new HashMap<>();
    }

    public static ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }

    public void addProduct(String symbol) throws DataValidationException, InvalidStringInput {
        checkProduct(symbol);

        productBooks.put(symbol, new ProductBook(symbol));
    }

    public ProductBook getProductBook(String symbol) throws DataValidationException {
        checkProduct(symbol);
        return productBooks.get(symbol);
    }

    public String getRandomProduct() throws DataValidationException {
        if (productBooks.isEmpty()) {
            throw new DataValidationException("There are no products available");
        }

        Object[] keys = productBooks.keySet().toArray();
        Random rand = new Random();
        int randomIndex = rand.nextInt(productBooks.size());

        return (String) keys[randomIndex];
    }

    public TradableDTO addTradable(Tradable o) throws DataValidationException, InvalidNullException, InvalidPriceException {
        if (o == null) {
            throw new DataValidationException("Tradable cannot be null.");
        }

        ProductBook book = getInstance().getProductBook(o.getProduct());

        if (book == null) {
            throw new DataValidationException("Tradable cannot be null.");
        }

        TradableDTO tradableDTO = book.add(o);

        UserManager.getInstance().updateTradable(o.getUser(), tradableDTO);

        return tradableDTO;
    }

    public TradableDTO[] addQuote(Quote q) throws DataValidationException, InvalidNullException, InvalidPriceException {
        if (q == null) {
            throw new DataValidationException("Quote cannot be null");
        }

        ProductBook productBook = getProductBook(q.getSymbol());
        productBook.removeQuotesForUser(q.getUser());

        TradableDTO buyDTO = addTradable(q.getQuoteSide(BookSide.BUY));
        TradableDTO sellDTO = addTradable(q.getQuoteSide(BookSide.SELL));

        return new TradableDTO[]{buyDTO, sellDTO};
    }

    public TradableDTO cancel(TradableDTO o) throws DataValidationException, InvalidNullException, InvalidPriceException {
        if (o ==null) {
            throw new DataValidationException("Tradable cannot be null");
        }

        ProductBook book = getProductBook(o.product());
        TradableDTO cancelled = book.cancel(o.side(), o.tradableId());

        if (cancelled == null) {
            System.out.println("Failed to cancel tradable with ID: " + o.tradableId());
        }
        return cancelled;
    }

    public TradableDTO[] cancelQuote(String symbol, String user) throws DataValidationException, InvalidNullException, InvalidPriceException {
        if (user == null) {
            throw new DataValidationException("User cannot be null");
        }
        checkProduct(symbol);

        ProductBook book = getProductBook(symbol);
        return book.removeQuotesForUser(user);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ProductBook book : productBooks.values()) {
            sb.append(book.toString()).append("\n");
        }
        return sb.toString().trim();
    }

    private void checkProduct(String product) throws DataValidationException {
        if (!product.matches("[A-Za-z0-9.]{1,5}")) {
            throw new DataValidationException("Invalid String input: " + product);
        }
    }
}
