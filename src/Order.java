import GlobalConstants.BookSide;
import exception.*;
import prices.Price;
import tradable.Tradable;
import tradable.TradableDTO;

public class Order implements Tradable {
    private final String user;
    private final String product;
    private final Price price;
    private final BookSide side;
    private final int originalVolume;
    private int remainingVolume;
    private int cancelVolume;
    private int filledVolume;
    private final String traderID;

    public Order(String user, String product, Price price,
                  int originalVolume, BookSide side) throws InvalidStringInput {
        checkUser(user);
        checkProduct(product);
        checkSide(side);

        if (originalVolume < 0 || originalVolume > 100000) {
            throw new InvalidStringInput("Volume must be between 0 and 100.000");
        }

        this.user = user;
        this.product = product;
        this.price = price;
        this.originalVolume = originalVolume;
        this.side = side;
        setRemainingVolume(originalVolume);
        this.cancelVolume = 0;
        this.filledVolume = 0;
        this.traderID = user + product + price.toString() + System.nanoTime();
    }

    @Override
    public String getId() {
        return traderID;
    }

    @Override
    public int getRemainingVolume() {
        return remainingVolume;
    }

    @Override
    public void setCancelledVolume(int newVol){
        this.cancelVolume = newVol;
    }

    @Override
    public int getCancelledVolume() {
        return cancelVolume;
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
        this.filledVolume = newVol;
    }

    @Override
    public int getFilledVolume() {
        return filledVolume;
    }

    @Override
    public BookSide getSide() {
        return side;
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
        return originalVolume;
    }

    @Override
    public String toString() {
        return String.format("%s %s order: %s at %s, Orig Vol: %d, Rem Vol: %d, " +
                        "Fill Vol: %d, CXL Vol: %d, ID: %s",
                user, side, product, price, originalVolume, remainingVolume,
                filledVolume, cancelVolume, traderID);
    }

    private void checkUser(String user) throws InvalidStringInput {
        if (!user.matches("[A-Z]{3}")) {
            throw new InvalidStringInput("Invalid String input: " + user);
        }
    }

    private void checkProduct(String product) throws InvalidStringInput{
        if (!product.matches("[A-Za-z0-9.]{1,5}")) {
            throw new InvalidStringInput("Invalid String input: " + product);
        }
    }

    private void checkSide(BookSide side) throws InvalidStringInput{
        if (side == null || side.equals("")) {
            throw new InvalidStringInput("Invalid String input: " + side);
        }
    }
}
