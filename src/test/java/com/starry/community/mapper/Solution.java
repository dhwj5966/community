package com.starry.community.mapper;

/**
 * @author Starry
 * @create 2022-09-01-9:31 PM
 * @Describe
 */
class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        int i = solution.nthUglyNumber(12);

    }

    public int nthUglyNumber(int n) {
        if (n == 1) {
            return 1;
        }
        //用来记录已经获取过的丑数
        int p2 = 1, p3 = 1, p5 = 1;
        int[] dp = new int[n + 1];
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            int num2 = dp[p2] * 2, num3 = dp[p3] * 3, num5 = dp[p5] * 5;
            dp[i] = Math.min(Math.min(num2, num3), num5);
            if (dp[i] == num2) {
                p2++;
            }
            if (dp[i] == num3) {
                p3++;
            }
            if (dp[i] == num5) {
                p5++;
            }
        }
        return dp[n];
    }
}
