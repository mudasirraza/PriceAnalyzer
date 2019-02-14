package com.example.appengine.java8;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.appengine.java8.ValidationResult.ResultType;
import com.google.appengine.api.datastore.Entity;

public class DataTransferServlet extends HttpServlet {
	
private static final Logger logger = Logger.getLogger(SettingsServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		Entity e = DbUtil.getOrInitialize("DataTransfer", "data_transfer");
		req.setAttribute("data_transfer", e);	
		req.getRequestDispatcher("/jsps/data_transfer.jsp").forward(req, res);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		try {
			double price_gb = Double.parseDouble(req.getParameter("price_gb"));
			
			validateParams(price_gb);
			
			Entity e = DbUtil.getOrInitialize("DataTransfer", "data_transfer");
			e.setProperty("pricePerGb", price_gb);
			DbUtil.put(e);
			
			res.sendRedirect("/admin/settings");
		} catch (NumberFormatException e) {
			logger.severe("Invalid data entered. Must be a valid number. " + e);
		} catch (LambdaPersistenceException e) {
			logger.severe("Invalid data entered. " + e);
		}
	}
	
	private void validateParams(double price_gb) throws LambdaPersistenceException {	
		ValidationResult vr = Validator.validateDouble(price_gb, "Price per request");
		if(vr.getType() == ResultType.NOTOK)
			throw new LambdaPersistenceException(vr.getErrMsg());
	}

}
