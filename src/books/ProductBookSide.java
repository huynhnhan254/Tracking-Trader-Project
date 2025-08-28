package books;

import GlobalConstants.BookSide;
import exception.DataValidationException;
import exception.InvalidNullException;
import prices.*;
import quotes.*;
import tradable.*;
import user.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

public class ProductBookSide {
    private final BookSide side;
    private final TreeMap<Price, ArrayList<Tradable>> bookEntries;


    public ProductBookSide(BookSide side) {
        this.side = side;
        if (side == BookSide.BUY) {
            this.bookEntries = new TreeMap<>(Comparator.reverseOrder()); // Descending order for BUY
        } else {
            this.bookEntries = new TreeMap<>(); // Ascending order for SELL (default)
        }
    }

    public TradableDTO add(Tradable o) throws InvalidNullException, DataValidationException {
        if (o == null) {
            throw new InvalidNullException("Product is null");
        }

        Price price = o.getPrice();
        if (!bookEntries.containsKey(price)) {
            bookEntries.put(price, new ArrayList<>());
            bookEntries.get(price).add(o);
        } else {
           bookEntries.get(price).add(o);
        }

        UserManager.getInstance().updateTradable(o.getUser(), new TradableDTO(o));

        return new TradableDTO(o);
    }

    public TradableDTO cancel(String tradableId) throws DataValidationException {
        for (Price price : bookEntries.keySet()) {
            ArrayList<Tradable> tradables = bookEntries.get(price);

            for (int i = 0; i < tradables.size(); i++) {
                Tradable t = tradables.get(i);

                if (t.getId().equals(tradableId)) {
//                    System.out.println("**CANCEL: " + t);

                    // Update volumes
                    t.setCancelledVolume(t.getCancelledVolume() + t.getRemainingVolume());
                    t.setRemainingVolume(0);

                    // Remove the tradable from the list
                    tradables.remove(i);

                    // If the list is empty, remove the price entry from the map
                    if (tradables.isEmpty()) {
                        bookEntries.remove(price);
                    }
                    UserManager.getInstance().updateTradable(t.getUser(), new TradableDTO(t));

                    return new TradableDTO(t);
                }
            }
        }
        return null;
    }

    public TradableDTO removeQuotesForUser(String userName) throws DataValidationException {
        for (Price price : bookEntries.keySet()) {
            ArrayList<Tradable> tradables = bookEntries.get(price);

            for (int i = 0; i < tradables.size(); i++) {
                Tradable t = tradables.get(i);

                if (t instanceof QuoteSide && t.getUser().equals(userName)) {
                    TradableDTO cancelledDTO = cancel(t.getId());

                    if (tradables.isEmpty()) {
                        bookEntries.remove(price);
                    }
                    UserManager.getInstance().updateTradable(t.getUser(), new TradableDTO(t));

                    return cancelledDTO;
                }
            }
        }
        return null;
    }

    public Price topOfBookPrice() {
        if (bookEntries.isEmpty()) {
            return null;
        }
        return bookEntries.firstKey();
    }

    public int topOfBookVolume() {

        if (bookEntries.isEmpty()) {
            return 0;
        }
        Price topPrice = topOfBookPrice();
        return bookEntries.get(topPrice).stream().mapToInt(Tradable::getRemainingVolume).sum();
    }


    public void tradeOut(Price price, int volToTrade) throws DataValidationException {
        // Get the top of book price
        Price topPrice = topOfBookPrice();

        // If the top price is null or greater than the price passed in, return
        if (topPrice == null || topPrice.compareTo(price) > 0) {
            return;
        }

        ArrayList<Tradable> atPrice = bookEntries.get(topPrice);

        int totalVolAtPrice = 0;
        for (Tradable t : atPrice) {
            totalVolAtPrice += t.getRemainingVolume();
        }


        if (volToTrade >= totalVolAtPrice) {
            for (Tradable t : atPrice) {
                // Save the remaining volume of the tradable
                int rv = t.getRemainingVolume();

                // Set the filled volume to be the original volume
                t.setFilledVolume(t.getOriginalVolume());

                // Set the remaining volume to be 0
                t.setRemainingVolume(0);

                // Print a FULL FILL message
                System.out.println("\t\tFULL FILL: (" + t.getSide()  + '\s' + t.getFilledVolume() + ") " + t);

                // Send the updated Tradable state to the UserManager
                UserManager.getInstance().updateTradable(t.getUser(), new TradableDTO(t));

            }

            // Remove the bookEntries ArrayList for the top of book price
            bookEntries.remove(topPrice);
            return;
        }
        // volToTrade < totalVolAtPrice
        else {
            int remainder = volToTrade;

            for (Tradable t : atPrice) {
                double ratio = (double) t.getRemainingVolume() / totalVolAtPrice;
                int toTrade = (int) Math.ceil(volToTrade * ratio);
                toTrade = Math.min(toTrade, remainder);

                // Update the filled and remaining volumes of the tradable
                t.setFilledVolume(t.getFilledVolume() + toTrade);
                t.setRemainingVolume(t.getRemainingVolume() - toTrade);

                // Print a PARTIAL FILL message
                System.out.println("\t\tPARTIAL FILL: (" + t.getSide() + '\s' + t.getFilledVolume() + ") "+ t );

                remainder -= toTrade;

                UserManager.getInstance().updateTradable(t.getUser(), new TradableDTO(t));
            }

        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for (Price price : bookEntries.keySet()) {
            sb.append("\t").append(price).append(":\n");

            for (Tradable tradable : bookEntries.get(price)) {
                sb.append("\t\t").append(tradable).append("\n");
            }
        }

        return sb.toString();
    }

    public TreeMap<Price, ArrayList<Tradable>> getBookEntries() {
        return this.bookEntries;
    }
}
