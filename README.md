# FileSearch——文件搜索项目
### 一、项目目标
1.巩固多线程、JDBC、集合框架等技术  
2.满足文件搜索的需求
### 二、项目使用技术栈与平台
1.所用技术:JavaFX,多线程，SQLite,pinyin4j  
2.平台与环境：Windows,IDEA,Maven
### 三、项目背景
1.各个操作系统下，有提供文件搜索的功能，如Windows中查找文件  
![Windows](https://github.com/Mbabysbreath/FileSearch/blob/master/Windows.png)
2.搜索神器：Everthing,基于NTFS文件系统的USNJournal（Update SequenceNumber Journal）,是利用操作系统记录的文件操作日志来进行搜索。  
 特点：效率高，速度快  
 局限：只能用于NTFS文件系统
 ![Everthing](https://github.com/Mbabysbreath/FileSearch/blob/master/Everything.png)
3.本项目使用实时的本地文件进行搜索，保存文件信息以便于提高搜索效率  
### 四、项目功能
1.指定搜索目录，显示目录中的所有文件、文件夹信息  
2.使用多线程进行文件搜索操作，文件信息保存在数据库。如果已经保存有文件的信息，就将本地目录与数据库中的文件信息进行对比，更新数据库。  
3.文件名包含中文时，支持汉语拼音的搜索（全拼或是首字母匹配）  
### 五、项目成果展示
1.开始
![Start](https://github.com/Mbabysbreath/FileSearch/blob/master/startP.png)
2.选择目录
![选择目录](https://github.com/Mbabysbreath/FileSearch/blob/master/selectP.png)
3.按关键字查询
![关键字](https://github.com/Mbabysbreath/FileSearch/blob/master/seaechF.png)
4.按拼音首字母
![拼音首字母](https://github.com/Mbabysbreath/FileSearch/blob/master/searchH.png)
5.按拼音全拼
![拼音全拼](https://github.com/Mbabysbreath/FileSearch/blob/master/searchFP.png)

