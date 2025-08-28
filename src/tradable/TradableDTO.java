package tradable;

import prices.Price;
import GlobalConstants.BookSide;

public record TradableDTO(String user, String product,
                          Price price, int originalVolume, int remainingVolume,
                          int cancelVolume, int filledVolume, BookSide side,
                          String traderID) {

    public TradableDTO(Tradable tradable) {
        this(tradable.getUser(),
                tradable.getProduct(),
                tradable.getPrice(),
                tradable.getOriginalVolume(),
                tradable.getRemainingVolume(),
                tradable.getCancelledVolume(),
                tradable.getFilledVolume(),
                tradable.getSide(),
                tradable.getId());
    }

    public String tradableId() {
        return traderID;
    }

    private String generateID(Tradable tradable) {
        return user + product + price.toString() + System.nanoTime();
    }
}

