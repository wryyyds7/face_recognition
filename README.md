1.下载python模块 pip install deepface -i https://pypi.tuna.tsinghua.edu.cn/simple

一般下不到文件，看这里面有一个weigh压缩包，在pip，解压后放在用户的.deepface目录下，比如C:\Users\24148.deepface下，解压 注意，这个.deepface文件建议不要动，我也不知道为什么，就算是改了配置也不能识别除了c盘用户根目录以外的位置！！

运行 D:\bianchenglianxi\java\project\face_recognition\python_port\img python模块里面的这个文件夹当做了永久的数据库，用来存放模型认为的、正确头像 （tmd，正确率极低，我用了数据集测试了，大约在60%左右，而且不能多头像，不然会报错， 这个报错不要单用trycatch、throw！！！非常不建议！！！） uploads文件为日志存放的失败或者成功的头像的位置，运行每一天自动删除 photos为检测到人脸之后的检查匹配头像所在位置，我懒得搞数据库存了
不建议用linux系统，因为时间太赶了，我没做翻译依赖和系统识别

语音模块用的是windows自带的tts，可能不同的机子会有bug，但是这个很容易修

太赶了，没一个一个测试，就是简单地搞了一下，应该有不少bug

鉴权是用的两层，一层Gateway里面的过滤，这个要求你先进行登录再进行功能使用， token的值要放在请求头的“Authorization”字段中

日志和实名检测没来的急写，日志就直接拿ruoyi搞了以下识别模块的日志，要加的话，建议在我 之前写的common里面的aop里面改，我写了一个简单的，你再写两个aop用来分页和入库就好

要有nacos、redis（没来的急用，我还没复习……）， 我之前写了一个简单的nacos、redis、neo4j一起启动的bat脚本， 要是下了而且配了环境可以直接点，然后就会启动

JAVA没什么好讲的，感谢ruoyi
配置主要是改数据库部分，在D:\bianchenglianxi\java\project\face_recognition\config中， 主要是有一些java和python模块的共有配置，记得改； 此外，记得recognition的配置也要改，我后面忘记把这两个合并了， 导致其实配置还有不少其实是分开的，之后你们可以搞一下

github地址