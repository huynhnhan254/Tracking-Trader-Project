package user;

import CurrentMarket.CurrentMarketObserver;
import CurrentMarket.CurrentMarketSide;
import exception.*;
import tradable.*;

import java.util.HashMap;
import java.util.Map;

public class User implements CurrentMarketObserver {
    private final String userId;
    private HashMap<String, TradableDTO> tradables;
    private HashMap<String, CurrentMarketSide[]> currentMarkets;

    public User(String userId) throws DataValidationException {
        checkUserID(userId);

        this.userId = userId;
        this.tradables = new HashMap<>();
        this.currentMarkets = new HashMap<>();
    }

    public void updateTradables(TradableDTO o) {
        if (o != null) {
            tradables.put(o.tradableId(), o);
        }
    }

    @Override
    public void updateCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) {
        CurrentMarketSide[] sides = new CurrentMarketSide[2];
        sides[0] = buySide;
        sides[1] = sellSide;
        currentMarkets.put(symbol, sides);
    }

    public String getCurrentMarkets() {
        StringBuilder sb = new StringBuilder();
        for (HashMap.Entry<String, CurrentMarketSide[]> entry : currentMarkets.entrySet()) {
            String symbol = entry.getKey();
            CurrentMarketSide buySide = entry.getValue()[0];
            CurrentMarketSide sellSide = entry.getValue()[1];
            sb.append(symbol).append(" ")
                    .append(buySide.toString())
                    .append(" - ")
                    .append(sellSide.toString())
                    .append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User Id: ").append(userId).append("\n");
        for (TradableDTO tradable : tradables.values()) {
            sb.append("Product: ").append(tradable.product()).append(", ")
                    .append("Price: $").append(tradable.price()).append(", ")
                    .append("OriginalVolume: ").append(tradable.originalVolume()).append(", ")
                    .append("RemainingVolume: ").append(tradable.remainingVolume()).append(", ")
                    .append("CancelledVolume: ").append(tradable.cancelVolume()).append(", ")
                    .append("FilledVolume: ").append(tradable.filledVolume()).append(", ")
                    .append("User: ").append(tradable.user()).append(", ")
                    .append("Side: ").append(tradable.side()).append(", ")
                    .append("Id: ").append(tradable.tradableId()).append("\n");
        }
        return sb.toString();
    }

    private void checkUserID(String user) throws DataValidationException {
        if (!user.matches("[A-Z]{3}")) {
            throw new DataValidationException("Invalid String input: " + user);
        }
    }

    public String getUserId() {
        return userId;
    }

}
