package us.zoom.spring.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;



public class MyRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

	
	
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		Map<RequestMappingInfo, HandlerMethod> handlerMethods = getHandlerMethods();
		List<RequestMappingInfo> requestMappingInfos = new ArrayList<>();
		for(Map.Entry<RequestMappingInfo, HandlerMethod> entry:handlerMethods.entrySet()) {
			HandlerMethod handlerMethod = entry.getValue();
			Method method = handlerMethod.getMethod();
			if (!canRegister(method)) {
				RequestMappingInfo requestMappingInfo = entry.getKey();
				requestMappingInfos.add(requestMappingInfo);
			}
		}
		for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
			unregisterMapping(requestMappingInfo);
		}
		
	}

	private boolean canRegister(Method method) {
		MethodRegister annotation = AnnotationUtils.findAnnotation(method, MethodRegister.class);
		if (annotation == null) {
			return true;
		}
		Class[] includeClass = annotation.includeClass();
		Class[] excludeClass = annotation.excludeClass();
		if (includeClass.length ==0 && excludeClass.length == 0) {
			return true;
		}
		
		ApplicationContext applicationContext2 = getApplicationContext();
		for (Class type:includeClass) {
			String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext2, type);
			if (beanNames != null && beanNames.length>0) {
				return true;
			}
			
		}
		
		for (Class type:excludeClass) {
			String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext2, type);
			if (beanNames != null && beanNames.length>0) {
				logger.info("method be filter:method name:"+ method.getName() + " match bean class:"+type.getName());
				return false;
			}
			
		}
		
		return true;
	}
}
