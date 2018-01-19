package com.wemarklinks.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.wemarklinks.common.RequestUtil;


@Component
public class CorrelationFilter implements Filter {

//	private static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-Id";
	private static final String CORRELATION_ID_LOG_VAR_NAME = "correlationId";
	private static final Logger logger = LoggerFactory.getLogger(CorrelationFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
//		    String contentType = request.getContentType();
//			logger.info("Request contentType :{}", contentType);
		    String path = RequestUtil.requestInfo((HttpServletRequest) request);
		    if(!path.contains("/webjars")){
		        logger.info("Request Info: {}", path);
		    }
			chain.doFilter(request, response);
		} finally {
			MDC.remove(CORRELATION_ID_LOG_VAR_NAME);
		}
	}

//	private String getCorrelationIdFromHeader(final HttpServletRequest request) {
//		String correlationId = request.getHeader(CORRELATION_ID_HEADER_NAME);
//		if (StringUtils.isEmpty(correlationId)) {
//			correlationId = generateUniqueCorrelationId();
//		}
//
//		return correlationId;
//	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	    logger.info("init filter");
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
	    logger.info("destroy filter");
		// TODO Auto-generated method stub
		
	}
}
