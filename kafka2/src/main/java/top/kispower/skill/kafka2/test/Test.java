package top.kispower.skill.kafka2.test;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @author haisenbao
 * @date 2020/5/20
 */
public class Test {
    public static void main(String[] args) {
        Student student = new Student();
        student.setName("张三");
        System.err.println(JSON.toJSONString(student));
    }


    public static class Student implements Serializable {

        private String name;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }




        public boolean isGood() {
            return true;
        }
        public Integer getAge() {
            return 9;
        }
        public Integer queryAge() {
            return 10;
        }

    }
}
