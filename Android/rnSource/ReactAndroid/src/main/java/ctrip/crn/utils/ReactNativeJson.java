package ctrip.crn.utils;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by lxw on 16/5/19.
 */
public class ReactNativeJson {

    private final static String TAG = "ReactNativeJson";

    /**
     * fastjson obj convert to Map
     * @param jsonObject jsonObject
     */
    public static WritableNativeMap convertJsonToMap(com.alibaba.fastjson.JSONObject jsonObject) {
        WritableNativeMap map = new WritableNativeMap();
        Set<String> keySet = jsonObject.keySet();
        for (String key : keySet) {
            Object value = jsonObject.get(key);
            if (value == null) {
                map.putNull(key);
            } else if (value instanceof com.alibaba.fastjson.JSONObject) {
                map.putMap(key, convertJsonToMap((com.alibaba.fastjson.JSONObject) value));
            } else if (value instanceof com.alibaba.fastjson.JSONArray) {
                map.putArray(key, convertJsonToArray((com.alibaba.fastjson.JSONArray) value));
            } else if (value instanceof Boolean) {
                map.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                map.putInt(key, (Integer) value);
            } else if (value instanceof Double || value instanceof Float) {
                map.putDouble(key, (Double) value);
            } else if (value instanceof BigDecimal) {
                map.putDouble(key, ((BigDecimal) value).doubleValue());
            } else if (value instanceof Long) {
                map.putDouble(key, ((Long) value).doubleValue());
            } else if (value instanceof String) {
                map.putString(key, (String) value);
            } else {
                map.putString(key, JSON.toJSONString(value));
            }
        }

        return map;
    }

    /**
     * json object convert to JSON
     * @param jsonObject jsonObject
     */
    public static WritableNativeMap convertJsonToMap(JSONObject jsonObject) {
        WritableNativeMap map = new WritableNativeMap();

        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = null;
            try {
                value = jsonObject.get(key);
            } catch (JSONException ex) {
                Log.e(TAG, "convertJsonToMap error: ", ex);
            }
            if (value == null) {
                map.putNull(key);
            } else if (value instanceof JSONObject) {
                map.putMap(key, convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                map.putArray(key, convertJsonToArray((JSONArray) value));
            } else if (value instanceof Boolean) {
                map.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                map.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                map.putDouble(key, (Double) value);
            } else if (value instanceof String) {
                map.putString(key, (String) value);
            } else {
                map.putString(key, JSON.toJSONString(value));
            }
        }
        return map;
    }

    /**
     * convert fastjson JSONArray to WritableArray
     * @param jsonArray jsonArray
     */
    public static WritableArray convertJsonToArray(com.alibaba.fastjson.JSONArray jsonArray) {
        WritableArray array = new WritableNativeArray();

        for (int i = 0; i < jsonArray.size(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof com.alibaba.fastjson.JSONObject) {
                array.pushMap(convertJsonToMap((com.alibaba.fastjson.JSONObject) value));
            } else if (value instanceof com.alibaba.fastjson.JSONArray) {
                array.pushArray(convertJsonToArray((com.alibaba.fastjson.JSONArray) value));
            } else if (value instanceof Boolean) {
                array.pushBoolean((Boolean) value);
            } else if (value instanceof Integer) {
                array.pushInt((Integer) value);
            } else if (value instanceof Double) {
                array.pushDouble((Double) value);
            } else if (value instanceof String) {
                array.pushString((String) value);
            } else if (value != null) {
                array.pushString(JSON.toJSONString(value));
            }
        }
        return array;
    }

    /**
     * convert JSONArray to WritableArray
     * @param jsonArray jsonArray
     */
    public static WritableArray convertJsonToArray(JSONArray jsonArray) {
        WritableArray array = new WritableNativeArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = null;
            try {
                value = jsonArray.get(i);
            } catch (JSONException ex) {
                Log.e(TAG, "json array get object ", ex);
            }
            if (value instanceof JSONObject) {
                array.pushMap(convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                array.pushArray(convertJsonToArray((JSONArray) value));
            } else if (value instanceof Boolean) {
                array.pushBoolean((Boolean) value);
            } else if (value instanceof Integer) {
                array.pushInt((Integer) value);
            } else if (value instanceof Double) {
                array.pushDouble((Double) value);
            } else if (value instanceof String) {
                array.pushString((String) value);
            } else if (value != null) {
                array.pushString(JSON.toJSONString(value));
            }
        }
        return array;
    }

    /**
     * toArrayList
     * @param arrayObj arrayObj
     */
    public static ArrayList<Object> toArrayList(ReadableArray arrayObj) {
        ArrayList<Object> arrayList = new ArrayList<>();

        for (int i = 0; i < arrayObj.size(); i++) {
            switch (arrayObj.getType(i)) {
                case Null:
                    arrayList.add(null);
                    break;
                case Boolean:
                    arrayList.add(arrayObj.getBoolean(i));
                    break;
                case Number:
                    arrayList.add(arrayObj.getDouble(i));
                    break;
                case String:
                    arrayList.add(arrayObj.getString(i));
                    break;
                case Map:
                    arrayList.add(toHashMap(arrayObj.getMap(i)));
                    break;
                case Array:
                    arrayList.add(toArrayList(arrayObj.getArray(i)));
                    break;
                default:
                    Log.e(TAG, "throw IllegalArgumentException Could not convert object at index: " + i + " .");
            }
        }

        return arrayList;
    }

    /**
     * toHashMap
     * @param map map
     */
    public static HashMap<String, Object> toHashMap(ReadableMap map) {
        if (map == null) return null;
        ReadableMapKeySetIterator iterator = map.keySetIterator();
        HashMap<String, Object> hashMap = new HashMap<>();

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            switch (map.getType(key)) {
                case Null:
                    hashMap.put(key, null);
                    break;
                case Boolean:
                    hashMap.put(key, map.getBoolean(key));
                    break;
                case Number:
                    hashMap.put(key, map.getDouble(key));
                    break;
                case String:
                    hashMap.put(key, map.getString(key));
                    break;
                case Map:
                    hashMap.put(key, toHashMap(map.getMap(key)));
                    break;
                case Array:
                    hashMap.put(key, toArrayList(map.getArray(key)));
                    break;
                default:
                    Log.e(TAG, "throw IllegalArgumentException Could not convert object with key: " + key + " .");
            }
        }
        return hashMap;
    }

    /**
     * convert ReadableMap to HashMap
     * @param map map
     */
    public static HashMap<String, String> toStringHashMap(ReadableMap map) {
        if (map == null) return null;
        ReadableMapKeySetIterator iterator = map.keySetIterator();
        HashMap<String, String> hashMap = new HashMap<>();

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            switch (map.getType(key)) {
                case Null:
                    hashMap.put(key, null);
                    break;
                case String:
                    hashMap.put(key, map.getString(key));
                    break;
                case Boolean:
                    hashMap.put(key, map.getBoolean(key) + "");
                    break;
                case Number:
                    hashMap.put(key, map.getDouble(key) + "");
                    break;
                default:
                    Log.e(TAG, "throw IllegalArgumentException Could not convert object with key: " + key + " .");
                    break;
            }
        }
        return hashMap;
    }

    /**
     * convert ReadableMap to JSONObject
     * @param readableMap readableMap
     */
    public static JSONObject convertMapToJson(ReadableMap readableMap) {
        JSONObject object = new JSONObject();
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            try {
                switch (readableMap.getType(key)) {
                    case Null:
                        object.put(key, JSONObject.NULL);
                        break;
                    case Boolean:
                        object.put(key, readableMap.getBoolean(key));
                        break;
                    case Number:
                        object.put(key, readableMap.getDouble(key));
                        break;
                    case String:
                        object.put(key, readableMap.getString(key));
                        break;
                    case Map:
                        object.put(key, convertMapToJson(readableMap.getMap(key)));
                        break;
                    case Array:
                        object.put(key, convertArrayToJson(readableMap.getArray(key)));
                        break;
                }
            } catch (JSONException ex) {
                Log.e(TAG, "ReactNativeJSON convertMapToJSON error: ", ex);
            }
        }
        return object;
    }

    /**
     * convert ReadableMap to fastjson JSONObject
     * @param readableMap readableMap
     */
    public static com.alibaba.fastjson.JSONObject convertMapToFastJson(ReadableMap readableMap) {
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            switch (readableMap.getType(key)) {
                case Null:
                    object.put(key, null);
                    break;
                case Boolean:
                    object.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    object.put(key, readableMap.getDouble(key));
                    break;
                case String:
                    object.put(key, readableMap.getString(key));
                    break;
                case Map:
                    object.put(key, convertMapToFastJson(readableMap.getMap(key)));
                    break;
                case Array:
                    object.put(key, convertArrayToFastJson(readableMap.getArray(key)));
                    break;
            }
        }
        return object;
    }

    /**
     * convert ReadableArray to fastjson JSONArray
     * @param readableArray readableArray
     */
    private static com.alibaba.fastjson.JSONArray convertArrayToFastJson(ReadableArray readableArray) {
        com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
        for (int i = 0; i < readableArray.size(); i++) {
            switch (readableArray.getType(i)) {
                case Null:
                    break;
                case Boolean:
                    array.add(readableArray.getBoolean(i));
                    break;
                case Number:
                    array.add(readableArray.getDouble(i));
                    break;
                case String:
                    array.add(readableArray.getString(i));
                    break;
                case Map:
                    array.add(convertMapToFastJson(readableArray.getMap(i)));
                    break;
                case Array:
                    array.add(convertArrayToFastJson(readableArray.getArray(i)));
                    break;
            }
        }
        return array;
    }

    /**
     * convertToPOJO
     */
    public static <T> T convertToPOJO(ReadableMap readableMap, Class<T> tClass) {
        try {
            if (readableMap != null) {
                String mapStr = readableMap.toString();
                JSONObject jsonObject = new JSONObject(mapStr);
                JSONObject realDataObj = jsonObject.getJSONObject("NativeMap");
                if (realDataObj != null) {
                    String realObjStr = realDataObj.toString();
                    if (!TextUtils.isEmpty(realObjStr)) {
                        return parse(realObjStr, tClass);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error when convertToPOJO", e);
        }
        try {
            return tClass.newInstance();
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    /**
     * convertArrayToJson
     * @param readableArray readableArray
     */
    private static JSONArray convertArrayToJson(ReadableArray readableArray) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < readableArray.size(); i++) {
            switch (readableArray.getType(i)) {
                case Null:
                    break;
                case Boolean:
                    array.put(readableArray.getBoolean(i));
                    break;
                case Number:
                    try {
                        array.put(readableArray.getDouble(i));
                    } catch (JSONException ex) {
                        Log.i(TAG, "convertArrayToJson error: ", ex);
                    }
                    break;
                case String:
                    array.put(readableArray.getString(i));
                    break;
                case Map:
                    array.put(convertMapToJson(readableArray.getMap(i)));
                    break;
                case Array:
                    array.put(convertArrayToJson(readableArray.getArray(i)));
                    break;
            }
        }
        return array;
    }

    /**
     * bundleFromMap
     * @param readableMap readableMap
     */
    public static Bundle bundleFromMap(Map readableMap) {
        if (readableMap == null) {
            return null;
        }

        Bundle bundle = new Bundle();
        Iterator<String> iterator = readableMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = readableMap.get(key);
            if (value == null) {
                continue;
            }
            if (value instanceof Number) {
                bundle.putDouble(key, (Double) value);
            } else if (value instanceof Boolean) {
                bundle.putBoolean(key, (Boolean) value);
            } else if (value instanceof String) {
                bundle.putString(key, (String) value);
            } else if (value instanceof Map) {
                bundle.putBundle(key, bundleFromMap((Map) value));
            } else if (value instanceof Array) {
                bundle.putString(key, value.toString());
            } else {
                bundle.putString(key, JSON.toJSONString(value));
            }
        }
        return bundle;
    }

    /**
     * Convert a JSON object to a Bundle that can be passed as the extras of
     * an Intent. It passes each number as a double, and everything else as a
     * String, arrays of those two are also supported.
     */
    public static Bundle fromJson(JSONObject s) {
        Bundle bundle = new Bundle();
        for (Iterator<String> it = s.keys(); it.hasNext(); ) {
            String key = it.next();
            JSONArray arr = s.optJSONArray(key);
            JSONObject jsonObj = s.optJSONObject(key);
            Double num = s.optDouble(key);
            String str = s.optString(key);
            if (jsonObj != null) {
                bundle.putBundle(key, fromJson(jsonObj));
            } else if (arr != null && arr.length() <= 0) {
                bundle.putStringArray(key, new String[]{});
            } else if (arr != null && !Double.isNaN(arr.optDouble(0))) {
                double[] newarr = new double[arr.length()];
                for (int i = 0; i < arr.length(); i++) {
                    newarr[i] = arr.optDouble(i);
                }
                bundle.putDoubleArray(key, newarr);
            } else if (arr != null && arr.optString(0) != null) {
                Bundle[] bundles = new Bundle[arr.length()];
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        bundles[i] = fromJson(arr.getJSONObject(i));
                    } catch (JSONException e) {
                        Log.e(TAG, "unable to transform get JsonObject from array " + key, e);
                    }
                }
                bundle.putParcelableArray(key, bundles);
            } else if (!num.isNaN()) {
                bundle.putDouble(key, num);
            } else if (str != null) {
                bundle.putString(key, str);
            } else {
                Log.e(TAG, "unable to transform json to bundle " + key);
            }
        }

        return bundle;
    }

    /**
     * toJson
     */
    public static String toJson(Object object) {
        return JSON.toJSONString(object, new SerializerFeature[]{SerializerFeature.DisableCheckSpecialChar});
    }

    /**
     * parse
     */
    public static <T> T parse(String jsonStr, Class<T> tClass) {
        return JSON.parseObject(jsonStr, tClass);
    }

}