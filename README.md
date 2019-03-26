# FileMExample

一个简单的文件系统

如何使用:
1.git clone 项目后
2.启动ConfigController后，在浏览器输入http://localhost:8080/test
浏览器将输出12345612489
3.修改 resources目录下jdbc.properties中的内容
将修改为jdbc = hello
4.10秒后再次访问http://localhost:8080/test,浏览器将输出
hello,可见在项目不重启的情况下，也获取了最新值



