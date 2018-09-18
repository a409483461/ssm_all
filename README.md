# ssm_all
tomcat:7.0.85
jdk:1.7
本案例实现了ssm框架的整合与文件上传功能
一.ssm整合
	为了更好的学习 springmvc和mybatis整合开发的方法，需要将springmvc和mybatis进行整合。

整合目标：控制层采用springmvc、持久层使用mybatis实现。

1.需求
实现商品查询列表，从mysql数据库查询商品信息。

2.jar包

包括：spring（包括springmvc）、mybatis、mybatis-spring整合包、数据库驱动、第三方连接池。

参考：“mybatis与springmvc整合全部jar包”目录 
3.Dao
目标：
1、spring管理SqlSessionFactory、mapper

详细参考mybatis教程与spring整合章节。

3.1.db.properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/mybatis
jdbc.username=XXXX
jdbc.password=XXXX

3.2.log4j.properties
# Global logging configuration，建议开发环境中要用debug
log4j.rootLogger=DEBUG, stdout
# Console output...
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%5p [%t] - %m%n

3.3.sqlMapConfig.xml

在classpath下创建mybatis/sqlMapConfig.xml

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

<!—使用自动扫描器时，mapper.xml文件如果和mapper.java接口在一个目录则此处不用定义mappers -->
<mappers>
<package name="cn.itcast.ssm.mapper" />
</mappers>
</configuration>



3.4.applicationContext-dao.xml
配置数据源、事务管理，配置SqlSessionFactory、mapper扫描器。

<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:mvc="http://www.springframework.org/schema/mvc"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
		http://www.springframework.org/schema/beans/spring-beans-3.2.xsd 
		http://www.springframework.org/schema/mvc 
		http://www.springframework.org/schema/mvc/spring-mvc-3.2.xsd 
		http://www.springframework.org/schema/context 
		http://www.springframework.org/schema/context/spring-context-3.2.xsd 
		http://www.springframework.org/schema/aop 
		http://www.springframework.org/schema/aop/spring-aop-3.2.xsd 
		http://www.springframework.org/schema/tx 
		http://www.springframework.org/schema/tx/spring-tx-3.2.xsd ">
<!-- 加载配置文件 -->
<context:property-placeholder location="classpath:db.properties"/>
<!-- 数据库连接池 -->
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
       <property name="driverClassName" value="${jdbc.driver}"/>
		<property name="url" value="${jdbc.url}"/>
		<property name="username" value="${jdbc.username}"/>
		<property name="password" value="${jdbc.password}"/>
		<property name="maxActive" value="30"/>
		<property name="maxIdle" value="5"/>
</bean>	


<!-- 让spring管理sqlsessionfactory 使用mybatis和spring整合包中的 -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<!-- 数据库连接池 -->
		<property name="dataSource" ref="dataSource" />
		<!-- 加载mybatis的全局配置文件 -->
		<property name="configLocation" value="classpath:mybatis/SqlMapConfig.xml" />
	</bean>
<!-- mapper扫描器 -->
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
 <property name="basePackage" value="cn.itcast.springmvc.mapper"></property>
<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
	</bean>

</beans>


3.5.ItemsMapper.xml

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.itcast.ssm.mapper.ItemsMapper">
	
	<!-- sql片段 -->
	<!-- 商品查询条件 -->
	<sql id="query_items_where">
		<if test="items!=null">
			<if test="items.name!=null and items.name!=''">
				and items.name like '%${items.name}%'
			</if>
		</if>
	</sql>
	    
	<!-- 查询商品信息 -->
	<select id="findItemsList" parameterType="queryVo" resultType="items">
		select * from items 
		<where>
			<include refid="query_items_where"/>
		</where>
	</select>
	

</mapper>

3.6.ItemsMapper.java

public interface ItemsMapper {
	//商品列表
	public List<Items> findItemsList(QueryVo queryVo) throws Exception;
}

4.Service
目标：
1、Service由spring管理
2、	spring对Service进行事务控制。

4.1.applicationContext-service.xml
配置service接口。

4.2.applicationContext-transaction.xml
配置事务管理器。

<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:mvc="http://www.springframework.org/schema/mvc"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
		http://www.springframework.org/schema/beans/spring-beans-3.2.xsd 
		http://www.springframework.org/schema/mvc 
		http://www.springframework.org/schema/mvc/spring-mvc-3.2.xsd 
		http://www.springframework.org/schema/context 
		http://www.springframework.org/schema/context/spring-context-3.2.xsd 
		http://www.springframework.org/schema/aop 
		http://www.springframework.org/schema/aop/spring-aop-3.2.xsd 
		http://www.springframework.org/schema/tx 
		http://www.springframework.org/schema/tx/spring-tx-3.2.xsd ">

<!-- 事务管理器 -->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
	<!-- 数据源 -->
	<property name="dataSource" ref="dataSource"/>
</bean>


<!-- 通知 -->
<tx:advice id="txAdvice" transaction-manager="transactionManager">
  <tx:attributes>
     <!-- 传播行为 -->
    <tx:method name="save*" propagation="REQUIRED"/>
    <tx:method name="insert*" propagation="REQUIRED"/>
    <tx:method name="delete*" propagation="REQUIRED"/>
    <tx:method name="update*" propagation="REQUIRED"/>
    <tx:method name="find*" propagation="SUPPORTS" read-only="true"/>
    <tx:method name="get*" propagation="SUPPORTS" read-only="true"/>
  </tx:attributes>
</tx:advice>

<!-- 切面 -->
<aop:config>
  <aop:advisor advice-ref="txAdvice"
  pointcut="execution(* cn.itcast.springmvc.service.impl.*.*(..))"/>
</aop:config>

</beans>

4.3.OrderService

public interface OrderService {
	
	//商品查询列表
	public List<Items> findItemsList(QueryVo queryVo)throws Exception;
}

	
	@Autowired
	private ItemsMapper itemsMapper;

	@Override
	public List<Items> findItemsList(QueryVo queryVo) throws Exception {
		//查询商品信息
		return itemsMapper.findItemsList(queryVo);
	}

}

5.Action

5.1.springmvc.xml

<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:mvc="http://www.springframework.org/schema/mvc"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
		http://www.springframework.org/schema/beans/spring-beans-3.2.xsd 
		http://www.springframework.org/schema/mvc 
		http://www.springframework.org/schema/mvc/spring-mvc-3.2.xsd 
		http://www.springframework.org/schema/context 
		http://www.springframework.org/schema/context/spring-context-3.2.xsd 
		http://www.springframework.org/schema/aop 
		http://www.springframework.org/schema/aop/spring-aop-3.2.xsd 
		http://www.springframework.org/schema/tx 
		http://www.springframework.org/schema/tx/spring-tx-3.2.xsd ">
		
	<!-- 扫描controller注解,多个包中间使用半角逗号分隔 -->
	<context:component-scan base-package="cn.itcast.ssm.controller"/>
	
	<!--注解映射器 -->
	<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"/>
	<!--注解适配器 -->
	<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"/>
	
	<!-- ViewResolver -->
	<bean
	class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<property name="viewClass"
			value="org.springframework.web.servlet.view.JstlView" />
		<property name="prefix" value="/WEB-INF/jsp/" />
		<property name="suffix" value=".jsp" />
	</bean>

</beans>
5.2.web.xml

加载spring容器，配置springmvc前置控制器。

<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://java.sun.com/xml/ns/javaee" xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
	id="WebApp_ID" version="2.5">
	<display-name>springmvc</display-name>

	<!-- 加载spring容器 -->
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>/WEB-INF/classes/spring/applicationContext.xml,/WEB-INF/classes/spring/applicationContext-*.xml</param-value>
	</context-param>
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>

	<!-- 解决post乱码 -->
	<filter>
		<filter-name>CharacterEncodingFilter</filter-name>
		<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
		<init-param>
			<param-name>encoding</param-name>
			<param-value>utf-8</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>CharacterEncodingFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>


	<!-- springmvc的前端控制器 -->
	<servlet>
		<servlet-name>springmvc</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<!-- contextConfigLocation不是必须的， 如果不配置contextConfigLocation， springmvc的配置文件默认在：WEB-INF/servlet的name+"-servlet.xml" -->
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>classpath:spring/springmvc.xml</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>springmvc</servlet-name>
		<url-pattern>*.action</url-pattern>
	</servlet-mapping>

	<welcome-file-list>
		<welcome-file>index.html</welcome-file>
		<welcome-file>index.htm</welcome-file>
		<welcome-file>index.jsp</welcome-file>
		<welcome-file>default.html</welcome-file>
		<welcome-file>default.htm</welcome-file>
		<welcome-file>default.jsp</welcome-file>
	</welcome-file-list>
</web-app>

5.3.OrderController

@Controller
public class OrderController {
	
	@Autowired
	private OrderService orderService;

	@RequestMapping("/queryItem.action")
	public ModelAndView queryItem() throws Exception {
		// 商品列表
		List<Items> itemsList = orderService.findItemsList(null);

		// 创建modelAndView准备填充数据、设置视图
		ModelAndView modelAndView = new ModelAndView();

		// 填充数据
		modelAndView.addObject("itemsList", itemsList);
		// 视图
		modelAndView.setViewName("order/itemsList");

		return modelAndView;
	}

}

6.测试
http://localhost:8080/springmvc_mybatis/queryItem.action
7.注解开发-基础
7.1.需求
使用springmvc+mybatis架构实现商品信息维护。
7.2.商品修改
7.2.1.dao
使用逆向工程自动生成的代码：
ItemsMapper.java
ItemsMapper.xml

7.2.2.service
//根据id查询商品信息
	public Items findItemById(int id) throws Exception;
	
	//修改商品信息
	public void saveItem(Items items)throws Exception;

7.2.3.controller
修改商品信息显示页面：
@RequestMapping(value="/editItem")
	public String editItem(Model model, Integer id) throws Exception{
		
		//调用service查询商品信息
		Items item = itemService.findItemById(id);
		
		model.addAttribute("item", item);
		
		return "item/editItem";
	}

修改商品信息提交：
//商品修改提交
	@RequestMapping("/editItemSubmit")
	public String editItemSubmit(Items items)throws Exception{
		
		System.out.println(items);

		itemService.saveItem(items);
		
		return "success";
	}
7.2.4.页面
/WEB-INF/jsp/item/itemsList.jsp
/WEB-INF/jsp/item/editItem.jsp
