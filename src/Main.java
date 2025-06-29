import java.util.ArrayList;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String json = 
                """
                {
                    "Customers": {
                        "Mike": {"Age": 45, "Job": Lawyer},
                        "Helen": {"Age": 37, "Job": Programmer},
                        "Jack": {"Age": 25, "Job": Not Employed}
                    },
                    "Manager": Jim Sullivan,
                    "Products": {
                        "Models": {
                            "Audi": {"Year": 2021, "Price": 120000.0},
                            "Ford": {"Year": 2022, "Price": 345000.0},
                            "Volkswagen": {"Year": [2021, 2023], "Price": {2021: 350500.0, 2023: 850000.0}}
                        }
                    },
                    "Location": NULL,
                    "Product IDs": [1291, 2637, 1584, [2323, 1828], 2936, [1283, 9101]]
                }
                """;

        // Full Json
        Map<?, ?> map = JSONFile.parseJSON(json);
        System.out.println(map);

        //Get Manager. Simple
        System.out.println(map.get("Manager"));

        // Get the Customer Mike Age
        Map<?, ?> customers = (Map<?, ?>) map.get("Customers"); // get "Customers" object and convert it to map
        Map<?, ?> mike = (Map<?, ?>) customers.get("Mike"); // get "Mike" object and convert it to map
        System.out.println(Integer.parseInt((String)mike.get("Age"))); // Results are always String, should be parsed to integer to be able to do arithmetic

        // Get the Volkswagen's 2021 year
        var products = (Map<?, ?>) map.get("Products");
        var models = (Map<?, ?>) products.get("Models");
        var volkswagen = (Map<?, ?>) models.get("Volkswagen");
        var year = (ArrayList<?>) volkswagen.get("Year");
        System.out.println(year.getFirst());

        //Get the 2323 product id in one line
        System.out.println(((ArrayList<?>)(((ArrayList<?>) map.get("Product IDs")).get(3))).getFirst());


        //Limited Json. This json will contain only the value that has "Customers" key, others will not be parsed
        Map<?, ?> map2 = JSONFile.parseJSON(json, "Customers");
        System.out.println(map2);

        // Get the Ford's infos only
        Map<?, ?> ford = JSONFile.parseJSON(json, "Ford");
        System.out.println(ford);

        // Get the Helen's infos only
        Map<?, ?> helen = JSONFile.parseJSON(json, "Helen");
        System.out.println(helen);
        // for her job
        var heleninfos = (Map<?, ?>)helen.get("Helen");
        System.out.println(heleninfos.get("Job"));

    }
}
