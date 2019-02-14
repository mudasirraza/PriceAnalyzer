package com.example.appengine.java8;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.appengine.java8.LambdaPersistenceException;
import com.example.appengine.java8.ValidationResult;
import com.example.appengine.java8.ValidationResult.ResultType;
import com.google.appengine.api.datastore.Entity;

public class SettingsServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(SettingsServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		Iterable<Entity> lambdas = DbUtil.getAll("LambdaSetting");
		Entity api = DbUtil.getOrInitialize("ApiGateway", "api_gateway");
		Entity dataTransfer = DbUtil.getOrInitialize("DataTransfer", "data_transfer");
		
		req.setAttribute("lambdas", lambdas);
		req.setAttribute("api", api);
		req.setAttribute("dataTransfer", dataTransfer);
		
		req.getRequestDispatcher("/jsps/admin_dashboard.jsp").forward(req, res);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		try {
			Long memory = Long.parseLong(req.getParameter("memory"));
			double price_gbs = Double.parseDouble(req.getParameter("price_gbs"));
			double price_requests = Double.parseDouble(req.getParameter("price_requests"));
			Long free_gbs = Long.parseLong(req.getParameter("free_gbs"));
			Long free_requests = Long.parseLong(req.getParameter("free_requests"));
			
			validateParams(memory, price_gbs, price_requests, free_gbs, free_requests);
			
			Entity e = new Entity("LambdaSetting");
			e.setProperty("memory", memory);
			e.setProperty("pricePerGbs", price_gbs);
			e.setProperty("pricePerMRequest", price_requests);
			e.setProperty("freeGbs", free_gbs);
			e.setProperty("freeRequests", free_requests);
			DbUtil.put(e);
			
			res.sendRedirect("/admin/settings");
		} catch (NumberFormatException e) {
			logger.severe("Invalid data entered. Must be a valid number. " + e);
		} catch (LambdaPersistenceException e) {
			logger.severe("Invalid data entered. " + e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
			int id = Integer.valueOf(req.getParameter("mem_id"));
		
		} catch(NumberFormatException e) {
			logger.severe("Invalid data for beverage id: Must be int" + e);
		}
	}
	
	private void validateParams(Long memory, double price_gbs, double price_requests, Long free_gbs, Long free_requests) throws LambdaPersistenceException {
		ValidationResult vr = Validator.validateNumber(memory, "memory");
		if(vr.getType() == ResultType.NOTOK)
			throw new LambdaPersistenceException(vr.getErrMsg());
		
		vr = Validator.validateDouble(memory, "Memory settings");
		if(vr.getType() == ResultType.NOTOK)
			throw new LambdaPersistenceException(vr.getErrMsg());
		
		vr = Validator.validateDouble(price_gbs, "Price for Gbs");
		if(vr.getType() == ResultType.NOTOK)
			throw new LambdaPersistenceException(vr.getErrMsg());
		
		vr = Validator.validateDouble(price_requests, "Price per request");
		if(vr.getType() == ResultType.NOTOK)
			throw new LambdaPersistenceException(vr.getErrMsg());
		
		vr = Validator.validateDouble(free_gbs, "Free Gbs");
		if(vr.getType() == ResultType.NOTOK)
			throw new LambdaPersistenceException(vr.getErrMsg());
		
		vr = Validator.validateDouble(free_requests, "Free Requests");
		if(vr.getType() == ResultType.NOTOK)
			throw new LambdaPersistenceException(vr.getErrMsg());
	}

}
