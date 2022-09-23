package com.starry.community.sort;

/**
 * @author Starry
 * @create 2022-09-22-6:01 PM
 * @Describe
 */
public class testOfSort {

    public static void main(String[] args) {
        int length = 100_00000;
        int[] arr = new int[length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int)(Math.random() * length * 10);
        }
        long l = System.currentTimeMillis();
        MergeSort.mergeSort(arr);
        for(int i = 0; i < 20; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
        System.out.println(System.currentTimeMillis() - l);
    }


}
