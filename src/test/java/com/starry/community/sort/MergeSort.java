package com.starry.community.sort;

/**
 * @author Starry
 * @create 2022-09-22-5:56 PM
 * @Describe 实现分治排序
 */
public class MergeSort {

    /**
     * 分治排序
     * @param arr
     */
    public static void mergeSort(int[] arr) {
        //边界情况
        if (arr == null || arr.length < 2) {
            return;
        }
        merge(arr, 0, arr.length - 1);
    }

    /**
     * 将arr从begin到end分治排序，取中间值mid，然后分别将[begin,mid) [mid,end] 排序,再把
     * @param arr
     * @param begin
     * @param end
     */
    private static void merge(int[] arr, int begin, int end) {
        if (begin >= end - 1) {
            return;
        }
        //以mid为分界线，对左边的数组和右边的数组分别排序
        int mid = begin + (end - begin) / 2;
        merge(arr, begin, mid - 1);
        merge(arr, mid, end);
        //合并
        int[] temp = new int[end - begin + 1];
        int index1 = begin;
        int index2 = mid;
        int index3 = 0;//指向temp
        while (index1 < mid && index2 <= end) {
            if (arr[index1] <= arr[index2]) {
                temp[index3] = arr[index1];
                index1++;
                index3++;
            } else {
                temp[index3] = arr[index2];
                index2++;
                index3++;
            }
        }
        while (index1 < mid) {
            temp[index3++] = arr[index1++];
        }
        while (index2 <= end) {
            temp[index3++] = arr[index2++];
        }
        for (int i = begin; i <= end; i++) {
            arr[i] = temp[i - begin];
        }
    }
}
