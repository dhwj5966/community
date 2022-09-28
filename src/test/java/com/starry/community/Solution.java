package com.starry.community;

import java.util.Random;

/**
 * @author Starry
 * @create 2022-09-28-2:11 AM
 * @Describe
 */
class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution(new int[]{1,2,3});
        int i = solution.pickIndex();
    }
    int[] prefixSum;
    Random random = new Random();
    public Solution(int[] w) {
        prefixSum = new int[w.length + 1];
        for (int i = 1; i < prefixSum.length; i++) {
            prefixSum[i] = prefixSum[i - 1] + w[i - 1];
        }
    }

    //时间复杂度O(logn)
    public int pickIndex() {
        int ran = random.nextInt(prefixSum[prefixSum.length - 1] + 1);
        int l = 1, r = prefixSum.length - 1;
        while (l <= r) {
            int mid = (l + r) / 2;
            if (ran == prefixSum[mid]) {
                return mid;
            } else if (ran > prefixSum[mid]) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }
        return -1;
    }
}
