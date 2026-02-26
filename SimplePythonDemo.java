/**
 * Simple Python Learning Demo for Java Developers
 * Simplified version to avoid encoding issues
 */
public class SimplePythonDemo {
    public static void main(String[] args) {
        System.out.println("=== Python Basics for Java Developers ===");
        
        // 1. Variable Declaration Comparison
        System.out.println("\n1. Variables:");
        System.out.println("Java: int age = 25;");
        System.out.println("Python equivalent: age = 25  # dynamic typing");
        
        // Simulate Python-like dynamic variables
        Object age = 25;
        Object name = "Zhang San";
        Object isActive = true;
        
        System.out.println("Dynamic types:");
        System.out.println("age = " + age + " (type: " + age.getClass().getSimpleName() + ")");
        System.out.println("name = " + name + " (type: " + name.getClass().getSimpleName() + ")");
        
        // 2. Data Structures Comparison
        System.out.println("\n2. Data Structures:");
        
        // List operations (similar to ArrayList)
        System.out.println("Lists (like ArrayList):");
        java.util.List<String> fruits = new java.util.ArrayList<>();
        fruits.add("apple");
        fruits.add("banana");
        fruits.add("orange");
        System.out.println("Initial list: " + fruits);
        fruits.add("grape");
        System.out.println("After adding grape: " + fruits);
        System.out.println("First element: " + fruits.get(0));
        System.out.println("List size: " + fruits.size());
        
        // Dictionary operations (similar to HashMap)
        System.out.println("\nDictionaries (like HashMap):");
        java.util.Map<String, Object> student = new java.util.HashMap<>();
        student.put("name", "Li Si");
        student.put("age", 20);
        student.put("grade", 85.5);
        System.out.println("Student info: " + student);
        System.out.println("Name: " + student.get("name"));
        System.out.println("Keys: " + student.keySet());
        
        // 3. Control Structures
        System.out.println("\n3. Control Structures:");
        
        // If statement
        int score = 85;
        System.out.println("Score: " + score);
        if (score >= 90) {
            System.out.println("Grade: Excellent");
        } else if (score >= 80) {
            System.out.println("Grade: Good");
        } else if (score >= 60) {
            System.out.println("Grade: Pass");
        } else {
            System.out.println("Grade: Fail");
        }
        
        // Loops
        System.out.println("\nLoops:");
        System.out.print("For loop: ");
        for (int i = 0; i < 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        
        System.out.print("While loop: ");
        int count = 0;
        while (count < 3) {
            System.out.print("count" + count + " ");
            count++;
        }
        System.out.println();
        
        // Enhanced for loop (similar to Python's for item in list)
        System.out.print("Enhanced for loop: ");
        java.util.List<String> languages = java.util.Arrays.asList("Java", "Python", "Go");
        for (String lang : languages) {
            System.out.print(lang + " ");
        }
        System.out.println();
        
        // 4. Functions
        System.out.println("\n4. Functions:");
        int sum = add(10, 20);
        System.out.println("add(10, 20) = " + sum);
        
        greet("Wang Wu");
        greet("Zhao Liu", "Good evening");
        
        double avg = calculateAverage(85, 90, 78, 92);
        System.out.println("Average score: " + avg);
        
        // 5. OOP Comparison
        System.out.println("\n5. Object-Oriented Programming:");
        Student studentObj = new Student("Sun Qi", 19, "Computer Science");
        System.out.println("Student introduction: " + studentObj.getIntroduce());
        System.out.println("School: " + Student.getSchool());
        
        System.out.println("\n=== Key Differences Summary ===");
        System.out.println("1. Python uses indentation instead of braces {}");
        System.out.println("2. Dynamic typing - no need to declare variable types");
        System.out.println("3. Built-in rich data structures");
        System.out.println("4. Concise syntax");
        System.out.println("5. Powerful built-in functions and libraries");
    }
    
    // Utility functions
    public static int add(int a, int b) {
        return a + b;
    }
    
    public static void greet(String name) {
        greet(name, "Hello");
    }
    
    public static void greet(String name, String greeting) {
        System.out.println(greeting + ", " + name + "!");
    }
    
    public static double calculateAverage(int... scores) {
        if (scores.length == 0) return 0;
        int sum = 0;
        for (int score : scores) {
            sum += score;
        }
        return (double) sum / scores.length;
    }
    
    // Class definition example
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
            return String.format("I am %s, %d years old, majoring in %s", name, age, major);
        }
        
        public static String getSchool() {
            return "Tsinghua University";
        }
    }
}