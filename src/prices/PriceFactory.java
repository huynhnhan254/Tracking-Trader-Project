package prices;

import exception.InvalidPriceException;

import java.util.HashMap;

public abstract class PriceFactory extends Price {

    private static final HashMap<Integer, Price> priceMap = new HashMap<>();

    public PriceFactory(int cents) {
        super(cents);
    }

    public static Price makePrice(int value) {

        if (!priceMap.containsKey(value)) {
            // If not, create and store the new price object
            Price newPrice = new Price(value);
            priceMap.put(value, newPrice);
        }
        return priceMap.get(value);
    }

    public static Price makePrice(String stringValueIn) throws InvalidPriceException {
        //Empty is invalid
        if (stringValueIn == null || stringValueIn.isEmpty()) {
            throw new InvalidPriceException("Price cannot be empty");
        }

        //extract the number
        stringValueIn = stringValueIn.replace("$","").replace(",", "").trim();

        //Add two "0" for "1." case
        if (stringValueIn.endsWith(".")) {
            stringValueIn = stringValueIn + "00";
        }

        /*************************************
         For "^-?(\\d+|\\.\\d{2}|\\d+\\.\\d{2})":
         -?:     negative or positive price;
         \\d+:   one or more digit;
         -For ".22":
         \\.:    match the dot;
         \\d{2}: exactly 2 digits after ".";
         -For normal case:
         \d+\.\d{2}: any whole number with exactly 2 digits after ".";
         ***************************************/
        if (!stringValueIn.matches("^-?(\\d+|\\.\\d{2}|\\d+\\.\\d{2})")) {
            throw new InvalidPriceException("Price contains invalid format: " + stringValueIn);
        }

        //Convert to double
        double doubleValue = Double.parseDouble(stringValueIn);
        //Convert to int
        int valueInCents = (int) Math.round(doubleValue * 100);

        return makePrice(valueInCents);
    }
}
