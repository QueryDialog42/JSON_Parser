import java.util.ArrayList;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String json = "{\"Driver\": {\"Name\": \"Anderson\", \"Age\": 23, \"Job\": \"Teacher\"}, \"Car\": {\"Model\": \"Audi\", \"Years\": {\"2021\": [\"Very Old\", \"Old\", \"New\"]}}, \"Class Members ID\": [11, 5, 34, 367, [45, \"Jack\", [67, 46, 44]], 128, [13, 14, 15, 16]], \"Manager\": \"Maria\", \"Education Season\": \"Summer\", \"Education Time\": 61.12, \"Countries\": [\"Istanbul\", \"Ankara\"], \"Times\": [\"12.00\", \"18.00\"]}";
        String json2 = "{\"Json\": [12, 13, {\"Name\": \"Mahmut\", \"Job\": \"Lawyer\"}, 17], \"Hello\"}";

        Map<String, Object> map = JSONFile.parseJSON(json);

        // example of getting map

        Map<?, ?> carmap = (Map<?, ?>)map.get("Car");
        Map<?, ?> yearsMap = (Map<?, ?>)carmap.get("Years");
        ArrayList<?> years = (ArrayList<?>)yearsMap.get("2021");
        System.out.println(years.get(1));


        Map<?, ?> map1 = (Map<?, ?>)map.get("Driver");
        System.out.println(map1.get("Age"));

        // all class member ID

        ArrayList<?> IDs = (ArrayList<?>)map.get("Class Members ID");
        printAllItems(IDs);
        System.out.println();

        // example of getting only a special key's value, others will not be processed

        Map<String, Object> map3 = JSONFile.parseJSON(json, "Manager");
        System.out.println(map3.get("Manager"));

        Map<String, Object> map4 = JSONFile.parseJSON(json, "Car");
        Map<?, ?> map5 = (Map<?, ?>)map4.get("Car");
        System.out.println(map5.get("Model"));

        Map<String, Object> map6 = JSONFile.parseJSON(json, "Education Time");
        System.out.println(map6.get("Education Time"));

        int number1 = Integer.parseInt((String)IDs.get(2));
        int number2 = Integer.parseInt((String)IDs.get(3));
        System.out.println(number1 + number2);

        ArrayList<?> list = (ArrayList<?>)map.get("Countries");
        System.out.println(list.get(1));

        ArrayList<?> list1 = (ArrayList<?>)map.get("Times");
        System.out.println(list1.get(0));

        Map<String, Object> map10 = JSONFile.parseJSON(json2);
        ArrayList<?>innlist1 = (ArrayList<?>)map10.get("Json");
        Map<?, ?>innmap1 = (Map<?, ?>)innlist1.get(2);
        System.out.println(innlist1.get(2));
        System.out.println(innmap1.get("Job"));
    }


    public static void printAllItems(ArrayList<?> list) {
        for (Object i : list) {
            if (i instanceof ArrayList<?>) {
                printAllItems((ArrayList<?>)i);
            }
            else System.out.print(i + " ");
        }
    }
}
