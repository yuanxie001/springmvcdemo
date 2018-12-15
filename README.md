# springmvcdemo
用来测试spring mvc各种场景功能！
目前添加的demo有
## 已完成功能
 - json,xml自适应输出。通过ContentNegotiatingViewResolver相关配置解决的。
 - 通过@InitBinder实现自定义校验的逻辑。
 - 添加拦截器，实现所有拦截器和url匹配拦截器的逻辑
 - 统一异常处理逻辑和异常的状态码设置。
 - @MatrixVariable的实现和配置逻辑
 - 使用flashAtrribute 实现重定向的数据传递的逻辑
 - spring boot的静态资源处理（这个正常应该放在Nginx，这里有点多余）
 - 整合freemarker 视图解析
 - 添加mybatis-generator自动生成mapper的处理
 - 整合mybatis。
 - 添加事务支持
 - 初步用aop现实缓存逻辑，redis还没接入。等待继续接入
 
 ## 待完成功能：
 - redis缓存支持。用aop实现。需要先删除缓存后更新数据库。同时防止出现脏数据。准备使用setnx分布式锁来实现这个功能
 - log写入消息kafka。
