@echo off
echo =========================================
echo       Redis环境测试脚本
echo =========================================

echo 正在编译测试程序...
javac -cp ".;C:\Users\haopangpang\.gradle\caches\modules-2\files-2.1\redis.clients\jedis\4.3.1\*.jar;C:\Users\haopangpang\.gradle\caches\modules-2\files-2.1\org.slf4j\slf4j-api\1.7.36\*.jar" -encoding UTF-8 RedisQuickTest.java

if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo 编译成功，正在运行测试...

java -cp ".;C:\Users\haopangpang\.gradle\caches\modules-2\files-2.1\redis.clients\jedis\4.3.1\*.jar;C:\Users\haopangpang\.gradle\caches\modules-2\files-2.1\org.slf4j\slf4j-api\1.7.36\*.jar;C:\Users\haopangpang\.gradle\caches\modules-2\files-2.1\org.apache.commons\commons-pool2\2.11.1\*.jar" RedisQuickTest

echo.
echo 测试完成！
pause