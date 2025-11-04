package entities;

import java.util.HashMap;
import java.util.Map;

public class HashNoCidade {

     public static final Map<Integer, String> mapaNoCidade = new HashMap<>();

    public static void criaNoCidade (Integer no, String cidade) {
        mapaNoCidade.put(no, cidade);
    }
}
