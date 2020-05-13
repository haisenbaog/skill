package top.kispower.skill.kafka2.test;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author haisenbao
 * @date 2020/5/12
 */
public class SerializerTest {
    public static void main(String[] args) {
        String jsonString = JSON.toJSONString("你好");
        System.err.println(jsonString);
        String studentString = JSON.toJSONString(new Student("张三", 20));
        System.err.println(studentString);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Student implements Serializable {

        private static final long serialVersionUID = 3125027714733827876L;

        private String name;
        private Integer age;
    }
}
