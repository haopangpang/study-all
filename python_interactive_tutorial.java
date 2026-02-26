package study.all.python.tutorial;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Python语法学习模拟器 - Java实现版本
 * 帮助Java开发者理解Python语法特点
 */
public class PythonInteractiveTutorial {
    
    public static void main(String[] args) {
        System.out.println("🐍 Python语法学习模拟器");
        System.out.println("========================");
        
        // 演示各种Python语法特性
        demonstrateBasicSyntax();
        demonstrateDataStructures();
        demonstrateControlStructures();
        demonstrateFunctions();
        demonstrateOOP();
        
        System.out.println("\n🎯 学习要点总结:");
        System.out.println("1. Python使用缩进而非大括号");
        System.out.println("2. 动态类型，无需声明变量类型");
        System.out.println("3. 内置丰富的数据结构");
        System.out.println("4. 简洁的语法表达");
        System.out.println("5. 强大的内置函数和库");
    }
    
    /**
     * 基础语法演示
     */
    public static void demonstrateBasicSyntax() {
        System.out.println("\n1. 基础语法对比");
        System.out.println("----------------");
        
        // 变量声明对比
        System.out.println("变量声明:");
        System.out.println("Java: int age = 25;");
        System.out.println("Python模拟: age = 25  # 动态类型");
        
        // 数据类型演示
        Object age = 25;           // int
        Object name = "张三";       // str
        Object height = 175.5;     // float
        Object isActive = true;    // bool
        
        System.out.println("动态类型演示:");
        System.out.println("age = " + age + " (类型: " + age.getClass().getSimpleName() + ")");
        System.out.println("name = " + name + " (类型: " + name.getClass().getSimpleName() + ")");
        
        // 字符串操作
        String text = "Hello Python World";
        System.out.println("\n字符串操作:");
        System.out.println("原字符串: " + text);
        System.out.println("长度: " + text.length());
        System.out.println("大写: " + text.toUpperCase());
        System.out.println("切片[0:5]: " + text.substring(0, 5));
        
        // 字符串格式化
        String formatted = String.format("我是%s，今年%d岁", name, age);
        System.out.println("格式化字符串: " + formatted);
    }
    
    /**
     * 数据结构对比演示
     */
    public static void demonstrateDataStructures() {
        System.out.println("\n2. 数据结构对比");
        System.out.println("----------------");
        
        // 列表(List) - 类似ArrayList
        System.out.println("列表(List)操作:");
        List<String> fruits = new ArrayList<>(Arrays.asList("苹果", "香蕉", "橙子"));
        System.out.println("初始列表: " + fruits);
        
        fruits.add("葡萄");
        System.out.println("添加元素后: " + fruits);
        
        System.out.println("第一个元素: " + fruits.get(0));
        System.out.println("列表长度: " + fruits.size());
        
        // 列表推导式模拟
        System.out.println("\n列表推导式模拟:");
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> squares = numbers.stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * n)
            .collect(Collectors.toList());
        System.out.println("偶数的平方: " + squares);
        
        // 字典(Map) - 类似HashMap
        System.out.println("\n字典(Dictionary)操作:");
        Map<String, Object> student = new HashMap<>();
        student.put("name", "李四");
        student.put("age", 20);
        student.put("grade", 85.5);
        
        System.out.println("学生信息: " + student);
        System.out.println("姓名: " + student.get("name"));
        System.out.println("所有键: " + new ArrayList<>(student.keySet()));
        
        // 集合(Set)操作
        System.out.println("\n集合(Set)操作:");
        Set<String> tags1 = new HashSet<>(Arrays.asList("Java", "Python", "编程"));
        Set<String> tags2 = new HashSet<>(Arrays.asList("Python", "AI", "机器学习"));
        
        System.out.println("集合1: " + tags1);
        System.out.println("集合2: " + tags2);
        
        // 交集
        Set<String> intersection = new HashSet<>(tags1);
        intersection.retainAll(tags2);
        System.out.println("交集: " + intersection);
        
        // 并集
        Set<String> union = new HashSet<>(tags1);
        union.addAll(tags2);
        System.out.println("并集: " + union);
    }
    
    /**
     * 控制结构演示
     */
    public static void demonstrateControlStructures() {
        System.out.println("\n3. 控制结构对比");
        System.out.println("----------------");
        
        // if语句对比
        System.out.println("条件判断:");
        int score = 85;
        System.out.println("分数: " + score);
        
        if (score >= 90) {
            System.out.println("等级: 优秀");
        } else if (score >= 80) {
            System.out.println("等级: 良好");
        } else if (score >= 60) {
            System.out.println("等级: 及格");
        } else {
            System.out.println("等级: 不及格");
        }
        
        // 循环对比
        System.out.println("\n循环结构:");
        
        System.out.println("for循环(range模拟):");
        for (int i = 0; i < 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        
        System.out.println("while循环:");
        int count = 0;
        while (count < 3) {
            System.out.print("计数" + count + " ");
            count++;
        }
        System.out.println();
        
        // 增强for循环
        System.out.println("增强for循环(类似Python的for item in list):");
        List<String> languages = Arrays.asList("Java", "Python", "Go");
        for (String lang : languages) {
            System.out.print(lang + " ");
        }
        System.out.println();
    }
    
    /**
     * 函数定义演示
     */
    public static void demonstrateFunctions() {
        System.out.println("\n4. 函数定义对比");
        System.out.println("----------------");
        
        // 简单函数
        System.out.println("简单函数:");
        int sum = add(10, 20);
        System.out.println("add(10, 20) = " + sum);
        
        // 带默认参数的函数
        System.out.println("\n默认参数函数:");
        greet("张三");
        greet("李四", "晚上好");
        
        // 可变参数函数
        System.out.println("\n可变参数函数:");
        double avg = calculateAverage(85, 90, 78, 92);
        System.out.println("平均分: " + avg);
        
        // Lambda表达式(类似Python的匿名函数)
        System.out.println("\nLambda表达式:");
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5);
        nums.replaceAll(x -> x * 2);
        System.out.println("每个数乘以2: " + nums);
    }
    
    /**
     * 面向对象编程演示
     */
    public static void demonstrateOOP() {
        System.out.println("\n5. 面向对象编程对比");
        System.out.println("--------------------");
        
        // 创建对象
        Student student = new Student("王五", 19, "计算机科学");
        System.out.println("学生信息: " + student.getIntroduce());
        System.out.println("学校: " + Student.getSchool());
        
        // 继承演示
        GraduateStudent gradStudent = new GraduateStudent("赵六", 23, "人工智能", "机器学习");
        System.out.println("研究生信息: " + gradStudent.getIntroduce());
        System.out.println("研究方向: " + gradStudent.getResearchArea());
    }
    
    // 工具函数实现
    public static int add(int a, int b) {
        return a + b;
    }
    
    public static void greet(String name, String greeting) {
        System.out.println(greeting + "，" + name + "！");
    }
    
    public static void greet(String name) {
        greet(name, "你好");
    }
    
    public static double calculateAverage(int... scores) {
        if (scores.length == 0) return 0;
        int sum = 0;
        for (int score : scores) {
            sum += score;
        }
        return (double) sum / scores.length;
    }
    
    // 类定义示例
    static class Student {
        private String name;
        private int age;
        private String major;
        
        public Student(String name, int age, String major) {
            this.name = name;
            this.age = age;
            this.major = major;
        }
        
        public String getIntroduce() {
            return String.format("我是%s，%d岁，专业是%s", name, age, major);
        }
        
        public static String getSchool() {
            return "清华大学";
        }
        
        // Getter和Setter方法
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public String getMajor() { return major; }
        public void setMajor(String major) { this.major = major; }
    }
    
    // 继承示例
    static class GraduateStudent extends Student {
        private String researchArea;
        
        public GraduateStudent(String name, int age, String major, String researchArea) {
            super(name, age, major);
            this.researchArea = researchArea;
        }
        
        public String getResearchArea() {
            return researchArea;
        }
        
        public void setResearchArea(String researchArea) {
            this.researchArea = researchArea;
        }
        
        @Override
        public String getIntroduce() {
            return super.getIntroduce() + "，研究方向是" + researchArea;
        }
    }
}