package com.hanaset.taco.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SleepHelper {

    public static void Sleep(int time) {
        try{
            Thread.sleep(time);
        }catch (InterruptedException e) {
            log.error("time error");
        }
    }
}
