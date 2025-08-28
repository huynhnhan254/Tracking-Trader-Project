# Tracking-Trader-Project
Description:
A Java simulation project that tracks top-of-book prices and volumes for stocks in real-time using OOP principles and the Observer pattern. Users can subscribe to receive current market updates for specific stocks.
CurrentMarketSide – Holds top price and volume per market side.

CurrentMarketObserver – Interface for receiving market updates.

CurrentMarketTracker – Singleton that calculates market width and forwards updates.

CurrentMarketPublisher – Singleton that manages observer subscriptions and publishes updates.

Integrated with ProductBook and User classes for dynamic updates.

Outputs live market snapshots with formatted top prices/volumes and market width.
