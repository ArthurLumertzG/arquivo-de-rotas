package entities;

import java.util.HashMap;

public class HashNoCidade {

    static HashMap<Integer, String> mapNoCidade = new HashMap<>();

    public static HashMap<Integer, String> getMapNoCidade() {
        return mapNoCidade;
    }

    public static boolean confereNo (Integer no, String cidade, HashMap<Integer, String> map) {

        if ( map.containsKey(no) ) {
            if ( map.get(no) == cidade) {
                return true;
            }
            return false;
        }

        return false;
    }

    public static void criaNoCidade (Integer no, String cidade, HashMap<Integer, String> map) {
        map.put(no, cidade);
    }
}
