package com.example.appengine.java8;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.appengine.java8.ValidationResult.ResultType;
import com.google.appengine.api.datastore.Entity;

public class AnalyzeServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(AnalyzeServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		Iterable<Entity> lambdas = DbUtil.getAll("LambdaSetting");
		ArrayList<Long> memory_sizes = new ArrayList<Long>();
		for(Entity lambda: lambdas) {
			memory_sizes.add((Long) lambda.getProperty("memory"));
		}
		Collections.sort(memory_sizes);
		
		req.setAttribute("memory_sizes", memory_sizes);	
		req.getRequestDispatcher("/jsps/dashboard.jsp").forward(req, res);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {		
		try {
			Long memory = Long.parseLong(req.getParameter("memory"));
			Long num_requests = Long.parseLong(req.getParameter("num_requests"));
			double avg_execution_time = Double.parseDouble(req.getParameter("avg_execution_time"));
			
			Boolean free_tier_check = Boolean.parseBoolean(req.getParameter("free_tier_check"));
			Boolean api_gateway_check = Boolean.parseBoolean(req.getParameter("api_gateway_check"));
			Boolean data_transfer_check = Boolean.parseBoolean(req.getParameter("data_transfer_check"));
			
			Long num_requests_api = 0L;
			if(api_gateway_check.equals(true)) {
				num_requests_api = Long.parseLong(req.getParameter("num_requests_api"));
			}
			
			double data_transfer = 0;
			if(data_transfer_check.equals(true)) {
				data_transfer = Double.parseDouble(req.getParameter("data_transfer"));
			}
			
			validateParams(memory, num_requests, avg_execution_time, api_gateway_check, num_requests_api, data_transfer_check, data_transfer);
			
			Analyzer analyzer = new Analyzer();
			analyzer.setMemory(memory);
			analyzer.setNumRequests(num_requests);
			analyzer.setAvgExecutionTime(avg_execution_time);
			analyzer.setFreeTierCheck(free_tier_check);
			
			analyzer.setApiGatewayCheck(api_gateway_check);
			analyzer.setDataTransferCheck(data_transfer_check);
			analyzer.setNumReqApiGateway(num_requests_api);
			analyzer.setDataTransferVolume(data_transfer);
			
			String report = analyzer.generateReport();
			req.setAttribute("report", report);
			
		} catch (NumberFormatException e) {
			logger.severe("Invalid data entered. Check your data again. " + e);
		} catch (LambdaPersistenceException e) {
			logger.severe("Error: " + e);
		}
		
		req.getRequestDispatcher("/jsps/result.jsp").forward(req, res);
	}
	
	private void validateParams(Long memory, Long num_requests, double avg_execution_time, Boolean api_gateway_check, Long num_requests_api, Boolean data_transfer_check, double data_transfer) throws LambdaPersistenceException {
		ValidationResult vr = Validator.validateNumber(memory, "memory");
		if(vr.getType() == ResultType.NOTOK)
			throw new LambdaPersistenceException(vr.getErrMsg());
		
		vr = Validator.validateNumber(num_requests, "num_requests");
		if(vr.getType() == ResultType.NOTOK)
			throw new LambdaPersistenceException(vr.getErrMsg());
		
		vr = Validator.validateDouble(avg_execution_time, "avg_execution_time");
		if(vr.getType() == ResultType.NOTOK)
			throw new LambdaPersistenceException(vr.getErrMsg());
		
		if(api_gateway_check.equals(true)) {
			vr = Validator.validateNumber(num_requests_api, "num_requests_api");
			if(vr.getType() == ResultType.NOTOK)
				throw new LambdaPersistenceException(vr.getErrMsg());
		}
		
		if(data_transfer_check.equals(true)) {
			vr = Validator.validateDouble(data_transfer, "data_transfer");
			if(vr.getType() == ResultType.NOTOK)
				throw new LambdaPersistenceException(vr.getErrMsg());
		}
	}
}
