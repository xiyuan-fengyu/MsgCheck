package com.xiyuan.template.log;

import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * Created by xiyuan_fengyu on 2016/7/22.
 */
public class XYLog {

    private static String LOG_TAG = "xiyuan";

    public static void setLogTag(String logTag) {
        LOG_TAG = logTag;
    }

    public static void v(Object ...objs) {
        Log.v(LOG_TAG, argsToString(objs));
    }

    public static void d(Object ...objs) {
        Log.d(LOG_TAG, argsToString(objs));
    }

    public static void i(Object ...objs) {
        Log.i(LOG_TAG, argsToString(objs));
    }

    public static void w(Object ...objs) {
        Log.w(LOG_TAG, argsToString(objs));
    }

    public static void e(Object ...objs) {
        Log.e(LOG_TAG, argsToString(objs));
    }

    private static String argsToString(Object ...objs) {
        StringBuilder buffer = new StringBuilder();
        if (objs != null && objs.length > 0) {
            for (Object obj: objs) {
                buffer.append(anyToString(obj));
            }
        }
        return buffer.toString();
    }

    private static <T> String anyToString(T t) {
        if (t == null) {
            return "null";
        }

        Class<?> clazz = t.getClass();
        String clazzName = clazz.getName();
        String toString = t.toString();
        if (clazz == String.class) {
            return (String) t;
        }
        else if (clazzName.charAt(0) == '[') {
            if (clazzName.equalsIgnoreCase("[I")) {
                int[] objs = (int[]) t;
                return intArrToString(objs);
            }
            else if (clazzName.equalsIgnoreCase("[J")) {
                long[] objs = (long[]) t;
                return longArrToString(objs);
            }
            else if (clazzName.equalsIgnoreCase("[F")) {
                float[] objs = (float[]) t;
                return floatArrToString(objs);
            }
            else if (clazzName.equalsIgnoreCase("[D")) {
                double[] objs = (double[]) t;
                return doubleArrToString(objs);
            }
            else if (clazzName.equalsIgnoreCase("[S")) {
                short[] objs = (short[]) t;
                return shortArrToString(objs);
            }
            else if (clazzName.equalsIgnoreCase("[C")) {
                char[] objs = (char[]) t;
                return charArrToString(objs);
            }
            else if (clazzName.equalsIgnoreCase("[Z")) {
                boolean[] objs = (boolean[]) t;
                return booleanArrToString(objs);
            }
            else if (clazzName.equalsIgnoreCase("[B")) {
                byte[] objs = (byte[]) t;
                return byteArrToString(objs);
            }
            else {
                Object[] objs = (Object[]) t;
                return objArrToString(objs);
            }
        }
        else if (!toString.startsWith(clazzName + "@") && toString.charAt(0) != '[') {
            return toString;
        }
        else if (t instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) t;
            return iterableToString(iterable);
        }
        else {
            StringBuilder strBld = new StringBuilder();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field: fields) {
                try {
                    field.setAccessible(true);

                    String fName = field.getName();
                    String fTypeName = field.getType().getSimpleName();
                    Object value = field.get(t);
                    String valueStr = anyToString(value);
                    strBld.append('\n').append(fName).append(": ").append(fTypeName).append(" = ").append(valueStr).append(',');
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            int len = strBld.length();
            if (len > 0) {
                strBld.deleteCharAt(len - 1);
            }
            return "\n" + toString + " {" + strBld.toString() + "\n}\n";
        }
    }

    private static String intArrToString(int[] ts) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, len = ts.length; i < len; i++) {
            if (i == 0) {
                buffer.append('[').append(ts[i]).append(", ");
            }
            else if (i == len - 1) {
                buffer.append(ts[i]).append(']');
            }
            else {
                buffer.append(ts[i]).append(", ");
            }
        }
        return buffer.toString();
    }

    private static String longArrToString(long[] ts) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, len = ts.length; i < len; i++) {
            if (i == 0) {
                buffer.append('[').append(ts[i]).append(", ");
            }
            else if (i == len - 1) {
                buffer.append(ts[i]).append(']');
            }
            else {
                buffer.append(ts[i]).append(", ");
            }
        }
        return buffer.toString();
    }

    private static String floatArrToString(float[] ts) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, len = ts.length; i < len; i++) {
            if (i == 0) {
                buffer.append('[').append(ts[i]).append(", ");
            }
            else if (i == len - 1) {
                buffer.append(ts[i]).append(']');
            }
            else {
                buffer.append(ts[i]).append(", ");
            }
        }
        return buffer.toString();
    }

    private static String doubleArrToString(double[] ts) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, len = ts.length; i < len; i++) {
            if (i == 0) {
                buffer.append('[').append(ts[i]).append(", ");
            }
            else if (i == len - 1) {
                buffer.append(ts[i]).append(']');
            }
            else {
                buffer.append(ts[i]).append(", ");
            }
        }
        return buffer.toString();
    }

    private static String shortArrToString(short[] ts) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, len = ts.length; i < len; i++) {
            if (i == 0) {
                buffer.append('[').append(ts[i]).append(", ");
            }
            else if (i == len - 1) {
                buffer.append(ts[i]).append(']');
            }
            else {
                buffer.append(ts[i]).append(", ");
            }
        }
        return buffer.toString();
    }

    private static String booleanArrToString(boolean[] ts) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, len = ts.length; i < len; i++) {
            if (i == 0) {
                buffer.append('[').append(ts[i]).append(", ");
            }
            else if (i == len - 1) {
                buffer.append(ts[i]).append(']');
            }
            else {
                buffer.append(ts[i]).append(", ");
            }
        }
        return buffer.toString();
    }

    private static String byteArrToString(byte[] ts) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, len = ts.length; i < len; i++) {
            if (i == 0) {
                buffer.append('[').append(ts[i]).append(", ");
            }
            else if (i == len - 1) {
                buffer.append(ts[i]).append(']');
            }
            else {
                buffer.append(ts[i]).append(", ");
            }
        }
        return buffer.toString();
    }

    private static String charArrToString(char[] ts) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, len = ts.length; i < len; i++) {
            if (i == 0) {
                buffer.append('[').append(ts[i]).append(", ");
            }
            else if (i == len - 1) {
                buffer.append(ts[i]).append(']');
            }
            else {
                buffer.append(ts[i]).append(", ");
            }
        }
        return buffer.toString();
    }

    private static <T> String objArrToString(T[] ts) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, len = ts.length; i < len; i++) {
            String str = anyToString(ts[i]);
            if (i == 0) {
                buffer.append('[').append(str).append(", ");
            }
            else if (i == len - 1) {
                buffer.append(str).append(']');
            }
            else {
                buffer.append(str).append(", ");
            }
        }
        return buffer.toString();
    }

    private static String iterableToString(Iterable iterable) {
        Iterator<?> it = iterable.iterator();
        StringBuilder buffer = new StringBuilder();
        while (it.hasNext()) {
            buffer.append(anyToString(it.next())).append(", ");
        }
        int len = buffer.length();
        if (len > 0) {
            buffer.delete(len - 2, len);
        }
        return "[" + buffer.toString() + "]";
    }

}
