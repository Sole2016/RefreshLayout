package com.zhitoupc.refreshloadapp;

import org.junit.Test;

/**
 * Created by ZhiTouPC on 2017/6/6.
 */

public class NineTest {
    @Test
    public void simple(){
//        int i,j;
//        for(i=9;i>0;i--){
//            for(j=1;j<=i;j++){
//                System.out.print(i+"*"+j+"="+(i * j));
//                System.out.print(" ");
//            }
//            System.out.printf("\n");
//        }


//        int m, n;
//        for(m=2; m<=50; m++)
//        {
//            for(n=2; n<m; n++)
//            {
//                System.out.print("n= "+n+",m="+m+","+(m%n == 0)+"\n");
//                if(m%n==0) {//什么条件下跳出当前循环
//                    break;
//                }//这里应该退出当前循环了
//            }
//            if(m == n)   //n循环结束后，如果m=n的话就输出m
//                System.out.printf("%d  ", m);
//        }

        /* 定义需要计算的日期 */
        int year = 2008;
        int month = 8;
        int day = 8;
    /*
     * 请使用switch语句，if...else语句完成本题
     * 如有想看小编思路的，可以点击左侧任务中的“不会了怎么办”
     * 小编还是希望大家独立完成哦~
     */
        int sumDays = 0;
        switch(month){
            case 12:
                sumDays = 6*31 + 4*30 + 29 + day;
                break;
            case 11:
                sumDays = 5*31 + 4*30 + 29 + day;
                break;
            case 9:
                sumDays = 5*31 + 2*30 + 29 + day;
                break;
            case 8:
                sumDays = 5*31 + 30 + 29 + day;
                break;
            case 7:
                sumDays = 4*31 + 30 + 29 + day;
                break;
            case 6:
                sumDays = 3*31 + 30 + 29 + day;
                break;
            case 5:
                sumDays = 3*31 + 29 + day;
                break;
            case 4:
                sumDays = 2*31 + 29 + day;
                break;
            case 3:
                sumDays = 31 + 29 + day;
                break;
            case 2:
                sumDays = 31 + day;
                break;
            case 1:
                sumDays = day;
                break;
        }

        if(year % 4 == 0 && year%100 != 0 || year % 100 == 0)
        {
            sumDays -= 1;
        }

        System.out.printf("%d年%d月%d日是该年的第%d天",year,month,day,sumDays);

    }
}
