import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONFile {
    static private StringBuilder globalJsonValue;

    // Main functions. parseJSONs will be called only for once to reset globalJsonValue, parseJson functions can be called multiple times
    public static Map<String, Object> parseJSON(String json) {
        try{
            return parseJson(json);
        } finally {
            globalJsonValue = null;
        }
    }

    public static Map<String, Object> parseJSON(String json, String onlykey) {
        try{
            return parseJson(json, onlykey);
        } finally {
            globalJsonValue = null;
        }
    }



    private static Map<String, Object> parseJson(String json) {
        Map<String, Object> map = new HashMap<>();
        ArrayList<Object> pattern = new ArrayList<>();

        return parseAllJsonFile(json, map, pattern);
    }

    private static Map<String, Object> parseJson(String json, String onlykey) {
        Map<String, Object> map = new HashMap<>();
        ArrayList<Object> pattern = new ArrayList<>();

        return parseAllJsonFile(limitJsonString(json, onlykey), map, pattern);
    }

    private static Map<String, Object> parseAllJsonFile(String json, Map<String, Object> map, ArrayList<Object> pattern) {
        String[] key_valueKey = prepareJsonToParse(json);

        pattern.add(key_valueKey[0].trim().replace("\"", ""));

        for (short i = 1; i < key_valueKey.length; i++) { // the first index is added before
            String key_valueKey_index = key_valueKey[i].trim();

            if (i == key_valueKey.length - 1) {
                addLastItem(key_valueKey_index, pattern); // add the first key and the last value
                break;
            }

            switch(key_valueKey_index.charAt(0)) {
                case '[': splitListAndKey(key_valueKey_index, pattern); break;
                case '{': i += (short) splitJsonAndKey(pattern); break;
                default: splitStringAndKey(key_valueKey_index, pattern); // for integers and strings
            }
        }

        return addToMapThePattern(map, pattern);
    }

    private static int splitJsonAndKey(ArrayList<Object> pattern) { // {"Name"
        String[] items = newJsonFound();

        pattern.add(parseJson(items[0]));
        if (items[1] != null) pattern.add(items[1].replace("\"", "").trim());

        return items[0].split(":").length - 1; // - 1 is for i++
    }

    private static Map<String, Object> addToMapThePattern(Map<String, Object> map, ArrayList<Object> pattern) {
        if (pattern.size() % 2 != 0) pattern.removeLast(); // sometimes the last key is added the wrong pattern

        for (short i = 0; i < pattern.size(); i += 2) {
            map.put(pattern.get(i).toString(), pattern.get(i + 1)); // even numbers are keys, odd numbers are values
        }
        return map;
    }

    private static void splitStringAndKey(String item, ArrayList<Object> pattern) {
        String[] value_key = item.replace("\"", "").split(",");

        pattern.add(value_key[0].trim());
        pattern.add(value_key[1].trim());
    }

    private static void splitListAndKey(String item, ArrayList<Object> pattern) {
        int indexOfLastList;
        String value;
        String key;

        if ((indexOfLastList = item.lastIndexOf("]")) != -1) { // find the last ] to which commas should not be split
            int indexOfMiddleComma = item.indexOf(",", indexOfLastList); //find the first, to split as key - value

            value = item.substring(0, indexOfMiddleComma); // the part before comma, comma not included (list)
            key = item.substring(indexOfMiddleComma + 1); // the part after comma, comma not included by + 1 (key)
        }
        else { // the list includes a JSON. item now does not have the last index of ]
            value = newStringArray();

            int indexOfMiddleComma = globalJsonValue.indexOf(",", globalJsonValue.lastIndexOf(value)); // find , end of the list
            int firstIndex = globalJsonValue.indexOf("\"", indexOfMiddleComma); // find the first " for key
            int lastIndex = globalJsonValue.indexOf("\"", firstIndex);
            key = globalJsonValue.substring(firstIndex, lastIndex);
        }


        pattern.add(convertStringToArrayList(value));
        if (!key.isEmpty()) pattern.add(key.replace("\"", "").trim());
    }

    private static String[] prepareJsonToParse(String json) {
        int firstIndex = json.indexOf("{");
        int lastIndex = json.lastIndexOf("}");

        String preparedJson = json.substring(firstIndex + 1, lastIndex); // get rid of { } symbols and get the pattern of [key, value_key, value_key ... value]

        setJsonGlobalValue(preparedJson);

        return preparedJson.split(":");
    }

    private static ArrayList<Object> convertStringToArrayList(String list) {
        ArrayList<Object> listValue = new ArrayList<>();

        String[] items = prepareStringToParse(list);

        for (short i = 0; i < items.length; i++) {
            String item = items[i].trim();

            switch(item.charAt(0)) {
                case '[': i += (short) newListFounded(listValue); break;
                case '"': listValue.add(item.replace("\"", "")); break;
                case '{': i += (short) doJsonLogics(listValue); break; // do not work correctly
                default: listValue.add(item);
            }
        }

        return listValue;
    }

    private static int doJsonLogics(ArrayList<Object> listValue) {
        String[] items = newJsonFound();

        listValue.add(parseJson(items[0]));

        if (items[1] != null) listValue.add(items[1]);
        try{
            return items[0].split(",").length - 1;
        }
        catch (Exception e){
            return 0; // no need extra i++
        }
    }

    private static int newListFounded(ArrayList<Object> listValue) {
        ArrayList<Object> newList = convertStringToArrayList(newStringArray());

        listValue.add(newList);

        return countEveryItem(newList) - 1; // skip the list, - 1 is for i++ at last
    }

    private static String newStringArray() {
        int firstIndex =  globalJsonValue.indexOf("[");
        int endIndex = firstIndex; // starting position

        short listnumber = 1;   // one list is already found above

        Pattern pattern = Pattern.compile("[\\[\\]]"); // looking for [ or ] (pattern).
        Matcher matcher = pattern.matcher(globalJsonValue); // Search

        // find the last index of ]
        while (listnumber != 0 && matcher.find(endIndex + 1)) { // +1 to NOT include first character
            endIndex = matcher.start(); // find the start index of the pattern
            switch(globalJsonValue.charAt(endIndex)) {
                case '[': listnumber++; break; // new list beginning found
                case ']': listnumber--; break; // list ended
            }
        }

        return globalJsonValue.substring(firstIndex, endIndex + 1); // +1 to include ]
    }

    // second function for limited json
    private static String newStringArray(String extractedJson) {
        int firstIndex =  extractedJson.indexOf("[");
        int endIndex = firstIndex;

        short listnumber = 1;

        Pattern pattern = Pattern.compile("[\\[\\]]");
        Matcher matcher = pattern.matcher(extractedJson);

        while (listnumber != 0 && matcher.find(endIndex + 1)) {
            endIndex = matcher.start();
            switch(extractedJson.charAt(endIndex)) {
                case '[': listnumber++; break;
                case ']': listnumber--; break;
            }
        }

        return extractedJson.substring(firstIndex, endIndex + 1);
    }

    private static String delFirstAndLastChar(String str) {
        return str.trim().substring(1, str.length() - 1);
    }

    private static int countEveryItem(Object arrayList) {
        int count = 0;
        for (Object item : (ArrayList<?>)arrayList) {
            if (item instanceof ArrayList<?>) count += countEveryItem(item);
            else count++;
        }
        return count;
    }

    private static String[] prepareStringToParse(String list) {
        globalJsonValue = globalJsonValue.deleteCharAt(globalJsonValue.indexOf(list));
        list = delFirstAndLastChar(list);
        return list.split(",");
    }

    private static String[] newJsonFound() {
        String[] returns = new String[2];
        int firstIndex = globalJsonValue.indexOf("{");
        int endIndex = firstIndex;

        int jsonNumber = 1;

        Pattern pattern = Pattern.compile("[{}]"); // looking for { or }
        Matcher matcher = pattern.matcher(globalJsonValue);

        while(jsonNumber != 0 && matcher.find(endIndex + 1)) {
            endIndex = matcher.start();
            switch(globalJsonValue.charAt(endIndex)) {
                case '{': jsonNumber++; break;
                case '}': jsonNumber--; break;
            }
        }

        String newJsonString = globalJsonValue.substring(firstIndex, endIndex + 1); // + 1 is for included }
        returns[0] = newJsonString;

        try {
            String key = globalJsonValue.substring(globalJsonValue.indexOf("\"", endIndex), globalJsonValue.indexOf(":", endIndex));
            returns[1] = key;
        } catch(StringIndexOutOfBoundsException ex) {
            // do nothing at this exception
        }

        globalJsonValue.deleteCharAt(firstIndex);

        return returns;
    }

    private static void setJsonGlobalValue(String json) {
        if (globalJsonValue == null) globalJsonValue = new StringBuilder(json);
    }

    private static void addLastItem(String item, ArrayList<Object> pattern) {
        switch(item.charAt(0)) {
            case '"': pattern.add(item.replace("\"", "")); break;
            case '[': pattern.add(convertStringToArrayList(item)); break;
            case '{': pattern.add(parseJson(newJsonFound()[0]));
            default: pattern.add(item);
        }
    }

    private static String limitJsonString(String json, String onlykey) {
        int onlyKeyIndex = json.indexOf(onlykey);

        String extractedJson = json.substring(onlyKeyIndex - 1);
        char character = extractedJson.split(":")[1].trim().charAt(0);
        return switch (character) {
            case '"' -> extractJsonString(extractedJson);
            case '[' -> extractJsonList(extractedJson);
            case '{' -> extractJsonJson(extractedJson);
            default -> extractJsonInt(extractedJson);
        };
    }

    private static String extractJsonString(String extractedJson) {
        int fromIndex = 1;
        for (short i = 0; i < 3; i++) {
            fromIndex = extractedJson.indexOf("\"", fromIndex + 1);
        }
        return "{" + extractedJson.substring(0, fromIndex + 1) + "}";

    }

    private static String extractJsonList(String extractedJson) {
        String list = newStringArray(extractedJson);
        setJsonGlobalValue("{" +  extractedJson.substring(0, extractedJson.indexOf(":") + 1) + list + "}");
        return globalJsonValue.toString();
    }

    private static String extractJsonInt(String extractedJson){
        int lastIndex = extractedJson.indexOf(",");
        return "{" + extractedJson.substring(0, lastIndex) + "}";
    }

    private static String extractJsonJson(String extractedJson) {
        try{
            globalJsonValue = new StringBuilder(extractedJson);
            return  "{" + extractedJson.substring(0, extractedJson.indexOf(":") + 1) + newJsonFound()[0] + "}";
        } finally {
            globalJsonValue = null;
        }
    }
}
