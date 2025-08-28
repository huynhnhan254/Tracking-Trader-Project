package books;

import CurrentMarket.CurrentMarketTracker;
import GlobalConstants.BookSide;
import exception.DataValidationException;
import exception.InvalidNullException;
import exception.InvalidPriceException;
import exception.InvalidStringInput;
import quotes.*;
import tradable.*;
import prices.*;
import java.util.ArrayList;
import java.util.TreeMap;

public class ProductBook {
    private final String product;
    private ProductBookSide buySide;
    private ProductBookSide sellSide;

    public ProductBook(String product) throws InvalidStringInput {
        checkProduct(product);
        this.product = product;
        this.buySide = new ProductBookSide(BookSide.BUY);
        this.sellSide = new ProductBookSide(BookSide.SELL);
    }

    public TradableDTO add(Tradable t) throws InvalidNullException, DataValidationException, InvalidPriceException {

//        System.out.println("**ADD: " + t);

        if (t == null) {
            throw new InvalidNullException("Tradable cannot be null");
        }
        TradableDTO dto = null;
        if (t.getSide() == BookSide.BUY) {
            dto = buySide.add(t);
        } else if (t.getSide() == BookSide.SELL) {
            dto = sellSide.add(t);
        }

        tryTrade();
        updateMarket();

        return dto;
    }

    public TradableDTO[] add(Quote qte) throws InvalidNullException, DataValidationException, InvalidPriceException {
        if (qte == null) {
            throw new InvalidNullException("Quote cannot be null");
        }


        removeQuotesForUser(qte.getUser());

        TradableDTO buyDTO = buySide.add(qte.getQuoteSide(BookSide.BUY));
//        System.out.println("**ADD: " + qte.getQuoteSide(BookSide.BUY));

        TradableDTO sellDTO = sellSide.add(qte.getQuoteSide(BookSide.SELL));
//        System.out.println("**ADD: " + qte.getQuoteSide(BookSide.SELL));

        tryTrade();

        return new TradableDTO[] {buyDTO, sellDTO};
    }

    public TradableDTO cancel(BookSide side, String orderID) throws InvalidNullException, DataValidationException, InvalidPriceException {
        if (orderID == null) {
            throw new InvalidNullException("Order ID cannot be null");
        }
        removeQuotesForUser(orderID);

        TradableDTO dto = null;
        if (side == BookSide.BUY) {
            dto = buySide.cancel(orderID);
        } else if (side == BookSide.SELL) {
            dto = sellSide.cancel(orderID);
        }

        updateMarket();

        return dto;
    }

    public TradableDTO[] removeQuotesForUser(String userName) throws InvalidNullException, DataValidationException, InvalidPriceException {
        if (userName == null) {
            throw new InvalidNullException("User name cannot be null");
        }
        TradableDTO buyDTO = buySide.removeQuotesForUser(userName);
        TradableDTO sellDTO = sellSide.removeQuotesForUser(userName);

        updateMarket();
        return new TradableDTO[] {buyDTO, sellDTO};
    }

    public void tryTrade() throws DataValidationException {
        Price topBuy = buySide.topOfBookPrice();
        Price topSell = sellSide.topOfBookPrice();

        if (topBuy == null || topSell == null) {
            return;
        }

        int buyVol = buySide.topOfBookVolume();
        int sellVol = sellSide.topOfBookVolume();
        int totalToTradeAmount = Math.max(buyVol, sellVol);

        if (totalToTradeAmount <= 0) {
            return;
        }

        if (topSell.compareTo(topBuy) > 0) {
            return;
        }

        while (totalToTradeAmount > 0) {
            topBuy = buySide.topOfBookPrice();
            topSell = sellSide.topOfBookPrice();

            if (topBuy == null || topSell == null) {
                return;
            }

            if (topSell.compareTo(topBuy) > 0) {
                return;
            }

            // Determine trade volume for this iteration
            int toTrade = Math.min(buyVol, sellVol);

            // Execute trade on both sides
            buySide.tradeOut(topBuy, toTrade);
            sellSide.tradeOut(topSell, toTrade);

            // Subtract traded volume from totalToTrade
            totalToTradeAmount -= toTrade;
        }

    }

    public String getTopOfBookString (BookSide side) {
        ProductBookSide prodSide;
        if (side == BookSide.BUY) {
            prodSide = buySide;
        } else {
            prodSide = sellSide;
        }

        Price topPrice = prodSide.topOfBookPrice();
        int topVolume = prodSide.topOfBookVolume();

        // If the top price is null, return "0" for the price
        if (topPrice == null) {
            topPrice = new Price(0);  // Set top price to 0 if it's null
            topVolume = 0;  // Set volume to 0 if no price is available
        }

        return "Top of " + side + " book: " + topPrice + "x" + topVolume;
    }

    private void updateMarket() throws InvalidPriceException {
        Price topPriceBuy = buySide.topOfBookPrice();
        Price topPriceSell = sellSide.topOfBookPrice();
        int topVolBuy = buySide.topOfBookVolume();
        int topVolSell = sellSide.topOfBookVolume();

        CurrentMarketTracker.getInstance().updateMarket(product, topPriceBuy, topVolBuy, topPriceSell, topVolSell);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------- \n");
        sb.append("Product: " + product + "\n");

        //BUY SIDE
        sb.append("SIDE: BUY \n");
        if (buySide.getBookEntries().isEmpty()) {
            sb.append("\t <EMPTY>\n");
        } else {
            sb.append(buySide.toString() +"\n");
        }

        //SELL SIDE
        sb.append("SIDE: SELL\n");
        if (sellSide.getBookEntries().isEmpty()) {
            sb.append("\t <EMPTY>\n");
        } else {
            sb.append(sellSide.toString() +"\n");
        }
        sb.append("--------------------------------- \n");
        return sb.toString();
    }

    private void checkProduct(String product) throws InvalidStringInput {
        if (!product.matches("[A-Za-z0-9.]{1,5}")) {
            throw new InvalidStringInput("Invalid String input: " + product);
        }
    }
}
