package com.example.appengine.java8;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.appengine.java8.ValidationResult.ResultType;
import com.google.appengine.api.datastore.Entity;

public class ApiGatewayServlet extends HttpServlet {

	private static final Logger logger = Logger.getLogger(ApiGatewayServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		Entity e = DbUtil.getOrInitialize("ApiGateway", "api_gateway");
		req.setAttribute("api", e);	
		req.getRequestDispatcher("/jsps/api_gateway.jsp").forward(req, res);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		try {
			double price_requests = Double.parseDouble(req.getParameter("price_requests"));
			int free_requests = Integer.parseInt(req.getParameter("free_requests"));
			
			validateParams(price_requests, free_requests);
			
			Entity e = DbUtil.getOrInitialize("ApiGateway", "api_gateway");
			e.setProperty("pricePerMRequest", price_requests);
			e.setProperty("freeRequests", free_requests);
			DbUtil.put(e);
			
			res.sendRedirect("/admin/settings");
		} catch (NumberFormatException e) {
			logger.severe("Invalid data entered. Must be a valid number. " + e);
		} catch (LambdaPersistenceException e) {
			logger.severe("Invalid data entered. " + e);
		}
	}
	
	private void validateParams(double price_requests, int free_requests) throws LambdaPersistenceException {	
		ValidationResult vr = Validator.validateDouble(price_requests, "Price per request");
		if(vr.getType() == ResultType.NOTOK)
			throw new LambdaPersistenceException(vr.getErrMsg());
		
		vr = Validator.validateDouble(free_requests, "Free Requests");
		if(vr.getType() == ResultType.NOTOK)
			throw new LambdaPersistenceException(vr.getErrMsg());
	}
}
