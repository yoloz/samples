package indi.yolo.sample.kafka;/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.gson.Gson;
import indi.yolo.sample.kafka.impl.ConsumerTest;
import indi.yolo.sample.kafka.impl.KafkaProperties;
import indi.yolo.sample.kafka.impl.ProducerTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PlainTest {

    private void test(String... args) {
        if (args == null || args.length < 3) {
            System.out.println("param[" + Arrays.toString(args) + "] error");
            System.exit(1);
        }
//        PropertyConfigurator.configure(PlainTest.class.getResourceAsStream("/log4j.properties"));
        String host = args[0];
        String topic = args[1];
        int index = 2;
        String flag = null;
        if ("producer".equalsIgnoreCase(args[index])) {
            flag = "producer";
            index += 1;
        } else if ("consumer".equalsIgnoreCase(args[index])) {
            flag = "consumer";
            index += 1;
        }
        StringBuilder msg = new StringBuilder();
        for (int i = index; i < args.length; i++) {
            msg.append(args[i]);
            if (i != args.length - 1) msg.append(" ");
        }
        if (flag == null || "producer".equals(flag)) {
            ProducerTest producerTest = new ProducerTest(KafkaProperties.plain(), host, topic);
            producerTest.write(msg.toString());
        }
        if (flag == null || "consumer".equals(flag)) {
            ConsumerTest consumerTest = new ConsumerTest(KafkaProperties.plain(), host, topic);
            consumerTest.read();
        }
    }

    public static void main(String[] args) {
//        StringBuilder msg = new StringBuilder();
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 1024 * 1024; i++) {
            map.put("test" + i, "value" + i);
        }
        Gson gson = new Gson();
        String str = gson.toJson(map);
        System.out.println(str.getBytes().length);
        PlainTest clientTest = new PlainTest();
        clientTest.test("192.168.1.251:9092", "test", str);                  //先写入后读取
//        clientTest.test("ip:9092", "topic", "producer", "msg");      //写入消息
//        clientTest.test("ip:9092", "topic", "consumer");             //读取消息
    }
}
