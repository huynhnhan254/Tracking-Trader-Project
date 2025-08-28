package CurrentMarket;

import exception.InvalidPriceException;
import prices.*;

public final class CurrentMarketTracker {

    private static CurrentMarketTracker instance;

    public static CurrentMarketTracker getInstance() {
        if (instance == null) {
            instance = new CurrentMarketTracker();
        }
        return instance;
    }

    public void updateMarket(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws InvalidPriceException {
        Price marketWidth = new Price(0);
        if (sellPrice != null || buyPrice != null) {
            marketWidth = new Price(0);
        }
        if (sellPrice != null && buyPrice != null) {
            marketWidth = sellPrice.subtract(buyPrice);
        }

        CurrentMarketSide buySide = new CurrentMarketSide(buyPrice, buyVolume);
        CurrentMarketSide sellSide = new CurrentMarketSide(sellPrice, sellVolume);

        System.out.println("*********** Current Market ***********");

        String buyStr = buySide.toString();
        String sellStr = sellSide.toString();
        String widthStr = (marketWidth != null) ? marketWidth.toString() : "$0.00";
        System.out.printf("* %s %s - %s [%s]%n", symbol, buyStr, sellStr, widthStr);

        System.out.println("**************************************");

        CurrentMarketPublisher.getInstance().acceptCurrentMarket(symbol, buySide, sellSide);
    }
}
