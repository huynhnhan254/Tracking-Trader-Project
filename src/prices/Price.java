package prices;

import exception.InvalidPriceException;

import java.util.Objects;

public class Price implements Comparable<Price> {

    private final int cents;

    public Price(int cents) {
        this.cents = cents;
    }

    public boolean isNegative() {
        return cents < 0;
    }

    public Price add(Price price) throws InvalidPriceException {
        if (price == null) {
            throw new InvalidPriceException("Price cannot be null!!!");
        } else {
            Price newPrice = new Price(cents + price.cents);
            return newPrice;
        }
    }

    public Price subtract(Price price) throws InvalidPriceException {
        if (price == null) {
            throw new InvalidPriceException("Price cannot be null!!!");
        } else {
            Price newPrice = new Price(cents - price.cents);
            return newPrice;
        }
    }

    public Price multiply(int items){
        Price newPrice = new Price(cents * items);
        return newPrice;
    }

    public boolean greaterOrEqual(Price price) throws InvalidPriceException {
        if (price == null) {
            throw new InvalidPriceException("Price cannot be null!!!");
        } else {
            return cents >= price.cents;
        }
    }

    public boolean lessOrEqual(Price price) throws InvalidPriceException {
        if (price == null) {
            throw new InvalidPriceException("Price cannot be null!!!");
        } else {
            return cents <= price.cents;
        }
    }

    public boolean greaterThan(Price price) throws InvalidPriceException {
        if (price == null) {
            throw new InvalidPriceException("Price cannot be null!!!");
        } else { return cents > price.cents; }
    }

    public boolean lessThan(Price price) throws InvalidPriceException {
        if (price == null) {
            throw new InvalidPriceException("Price cannot be null!!!");
        } else {
            return cents < price.cents;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return cents == price.cents;
    }

    @Override
    public String toString() {
        return String.format("$%,4.2f",cents/100.00);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cents);
    }

    @Override
    public int compareTo(Price price) {
        return (price != null ? cents - price.cents : -1);
    }
}

