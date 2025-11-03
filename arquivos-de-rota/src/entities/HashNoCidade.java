package entities;

import java.util.HashMap;
import java.util.Map;

public class HashNoCidade {

     public static final Map<Integer, String> mapaNoCidade = new HashMap<>();

    public static boolean confereNo (Integer no, String cidade, HashMap<Integer, String> map) {

        if ( map.containsKey(no) ) {
            if ( map.get(no) == cidade) {
                return true;
            }
            return false;
        }

        return false;
    }

    public static void criaNoCidade (Integer no, String cidade) {
        mapaNoCidade.put(no, cidade);
    }
}
