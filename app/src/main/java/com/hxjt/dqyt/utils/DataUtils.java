package com.hxjt.dqyt.utils;

import java.util.ArrayList;

public class DataUtils {

    /**
     * 从数组中找出最大值的索引
     * @param array
     * @return
     */
    public static int findMaxIndexFromArray(double[] array) {
        int maxIndex = 0;
        double maxValue = array[0];

        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    /**
     * 从数组中找出最小值的索引
     * @param array
     * @return
     */
    public static int findMinIndexFromArray(double[] array) {
        int minIndex = 0;
        double minValue = array[0];

        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
                minIndex = i;
            }
        }

        return minIndex;
    }

    /**
     * 数组倒叙
     * @param array
     */
    public static void reverseArray(double[] array) {
        int start = 0;
        int end = array.length - 1;

        while (start < end) {
            // Swap the elements at start and end
            double temp = array[start];
            array[start] = array[end];
            array[end] = temp;

            // Move to the next pair
            start++;
            end--;
        }
    }

    public static boolean isArrayListOfDouble(Object obj) {
        if (obj instanceof ArrayList<?>) {
            ArrayList<?> list = (ArrayList<?>) obj;
            for (Object element : list) {
                if (!(element instanceof Double)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static double[] convertToDoubleArray(ArrayList<?> doubleList) {
        // 创建一个与ArrayList大小相同的double数组
        double[] doubleArray = new double[doubleList.size()];

        // 将ArrayList中的元素复制到数组中
        for (int i = 0; i < doubleList.size(); i++) {
            Object obj = doubleList.get(i);
            if(obj instanceof Double){
                doubleArray[i] = (double)obj;
            } else {
                doubleArray[i] = 0.0;
            }
        }

        return doubleArray;
    }



}
