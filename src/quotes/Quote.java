package quotes;

import GlobalConstants.BookSide;
import prices.*;
import exception.*;

public class Quote {
    private final String user;
    private final String product;
    private final QuoteSide buyside;
    private final QuoteSide sellside;

    public Quote(String symbol, Price buyPrice, int buyVolume, Price sellPrice,
                 int sellVolume, String userName) throws InvalidStringInput {
        if (symbol == null || symbol.trim().isEmpty() || !symbol.matches("[A-Za-z0-9.]{1,5}")) {
            throw new InvalidStringInput("Invalid symbol");
        }

        if (userName == null || userName.trim().isEmpty() || !userName.matches("[A-Z]{3}")) {
            throw new InvalidStringInput("Invalid user name");
        }

        this.user = userName;
        this.product = symbol;
        this.buyside = new QuoteSide(user, product, buyPrice, BookSide.BUY, buyVolume);
        this.sellside = new QuoteSide(user, product, sellPrice, BookSide.SELL, sellVolume);

    }

    public QuoteSide getQuoteSide(BookSide sideIn) {
        return sideIn.equals(BookSide.BUY) ? buyside : sellside;
    }

    public String getSymbol() {
        return this.product;
    }

    public String getUser() {
        return this.user;
    }
}
