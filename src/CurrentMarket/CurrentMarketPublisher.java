package CurrentMarket;

import java.util.ArrayList;
import java.util.HashMap;

public final class CurrentMarketPublisher {

    private static CurrentMarketPublisher instance;
    private HashMap<String, ArrayList<CurrentMarketObserver>> filters = new HashMap<>();

    public static CurrentMarketPublisher getInstance() {
        if (instance == null) {
            instance = new CurrentMarketPublisher();
        }
        return instance;
    }

    public void subscribeCurrentMarket(String symbol, CurrentMarketObserver cmo) {
        filters.putIfAbsent(symbol, new ArrayList<>());
        ArrayList<CurrentMarketObserver> observers = filters.get(symbol);
        if (!observers.contains(cmo)) {
            observers.add(cmo);
        }
    }

    public void unSubscribeCurrentMarket(String symbol, CurrentMarketObserver cmo) {
        ArrayList<CurrentMarketObserver> observers = filters.get(symbol);
        if (observers != null) {
            observers.remove(cmo);
        }
    }

    public void acceptCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) {
        ArrayList<CurrentMarketObserver> observers = filters.get(symbol);
        if (observers == null) return;

        for (CurrentMarketObserver observer : observers) {
            observer.updateCurrentMarket(symbol, buySide, sellSide);
        }
    }
}
