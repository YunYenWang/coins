package com.cht.iot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Staff {
    String id;
    String name;

    public void run() {
        Machine m = new Machine(9487); // this is Kotlin object

        log.info("version is {}", m.giveMeTheVersion());
    }
}
