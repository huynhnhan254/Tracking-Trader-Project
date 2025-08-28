package quotes;

import GlobalConstants.BookSide;
import exception.InvalidStringInput;
import prices.Price;
import tradable.*;

public class QuoteSide implements Tradable {
    private final String user;
    private final String product;
    private final Price price;
    private final BookSide side;
    private final int originalVolume;
    private int remainingVolume;
    private int cancelVolume;
    private int filledVolume;
    private final String id;

    public QuoteSide (String user, String product, Price price, BookSide side, int originalVolume) throws InvalidStringInput {
        if (product == null || product.trim().isEmpty()|| !product.matches("[A-Za-z0-9.]{1,5}")) {
            throw new InvalidStringInput("Invalid symbol");
        }

        if (user == null || user.trim().isEmpty() || !user.matches("[A-Z]{3}")) {
            throw new InvalidStringInput("Invalid user name");
        }

        if (side == null) {
            throw new InvalidStringInput("Invalid String input: " + null);
        }

        this.user = user;
        this.product = product;
        this.price = price;
        this.side = side;
        this.originalVolume = originalVolume;
        this.remainingVolume = originalVolume;
        this.cancelVolume = 0;
        this.filledVolume = 0;
        this.id= user + product + price.toString() + System.nanoTime();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getRemainingVolume() {
        return this.remainingVolume;
    }

    @Override
    public void setCancelledVolume(int newVol){

        this.cancelVolume -= newVol;
    }

    @Override
    public int getCancelledVolume() {
        return this.cancelVolume;
    }

    @Override
    public void setRemainingVolume(int newVol) {

        this.remainingVolume = newVol;
    }

    @Override
    public TradableDTO makeTradableDTO() {
        return new TradableDTO(this);
    }

    @Override
    public Price getPrice() {
        return this.price;
    }

    @Override
    public void setFilledVolume(int newVol) {
        this.filledVolume += newVol;
    }

    @Override
    public int getFilledVolume() {
        return this.filledVolume;
    }

    @Override
    public BookSide getSide() {
        return this.side;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public String getProduct() {
        return this.product;
    }

    @Override
    public int getOriginalVolume() {
        return this.originalVolume;
    }

    @Override
    public String toString() {
        return String.format("%s %s side quote for %s: %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, " +
                        "CXL Vol: %d, ID: %s",
                user, side, product, price.toString(), originalVolume, remainingVolume, filledVolume,
                cancelVolume, id);

    }
}


