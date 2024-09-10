**新人学习苍穹外卖项目**
- 项目本身的application-dev.yml没有上传，里面涉及到比如阿里云的隐私
- 微信支付中将原来的支付路径跳过了
  为pay->index.js包下面的代码，将url重新定向了
  ![](https://raw.githubusercontent.com/comeback12345/picture/main/20240910210003.png)
- 项目本身里面的支付片段也跳过了
  OrderServiceImpl.java里面修改的代码片段
  ![](https://raw.githubusercontent.com/comeback12345/picture/main/20240910211309.png)
  ![](https://raw.githubusercontent.com/comeback12345/picture/main/20240910210819.png)
  ![](https://raw.githubusercontent.com/comeback12345/picture/main/20240910211555.png)
  ![](https://raw.githubusercontent.com/comeback12345/picture/main/20240910211645.png)
