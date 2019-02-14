package com.example.appengine.java8;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;

public class Analyzer {
	
	private static final Logger logger = Logger.getLogger(AnalyzeServlet.class.getName());
	
	private Long memory; //MB
	private Long numRequests;
	private double avgExecutionTime; //s
	private Long numReqApiGateway;
	private double dataTransferVolume; //GB
	private double pricePerVolume; // $ per GB
	private ArrayList<Integer> additionalCosts;
	
	private boolean freeTierCheck; //default false
	private boolean apiGatewayCheck;
	private boolean dataTransferCheck;
	
	private double pricePerGbs; // $
	private Long freeGbs; //GB-s
	private Long freeRequests;
	private double pricePerMRequest; //$
	private Long freeRequestsApi;
	private double pricePerMRequestApi; //$
	
	private double computeCost;
	private double requestsCost;
	private double ioCost;
	private double additionalCost;
	private double totalCost;
	
	public Analyzer() {
		freeTierCheck = false;
		additionalCosts = new ArrayList<Integer>();
		computeCost = 0;
		requestsCost = 0;
		ioCost = 0;
		additionalCost = 0;
		totalCost = 0;
	}
	
	// setters
	public Long getMemory() {
		return memory;
	}

	public void setMemory(Long memory) throws LambdaPersistenceException {
		this.memory = memory;
		
		Entity e = DbUtil.getEqualSingle("LambdaSetting", "memory", memory);
		this.pricePerGbs = (double) e.getProperty("pricePerGbs");
		this.pricePerMRequest = (double) e.getProperty("pricePerMRequest");
		this.freeGbs = (Long) e.getProperty("freeGbs");
		this.freeRequests = (Long) e.getProperty("freeRequests");
	}

	public Long getNumRequests() {
		return numRequests;
	}

	public void setNumRequests(Long numRequests) {
		this.numRequests = numRequests;
	}

	public double getAvgExecutionTime() {
		return avgExecutionTime;
	}

	public void setAvgExecutionTime(double avgExecutionTime) {
		this.avgExecutionTime = avgExecutionTime / 1000.0;
	}

	public Long getNumReqApiGateway() {
		return numReqApiGateway;
	}

	public void setNumReqApiGateway(Long numReqApiGateway) {
		this.numReqApiGateway = numReqApiGateway;
	}

	public double getDataTransferVolume() {
		return dataTransferVolume;
	}

	public void setDataTransferVolume(double dataTransferVolume) {
		this.dataTransferVolume = dataTransferVolume;
	}

	public ArrayList<Integer> getAdditionalCosts() {
		return additionalCosts;
	}

	public void setAdditionalCosts(ArrayList<Integer> additionalCosts) {
		this.additionalCosts = additionalCosts;
	}

	public boolean isFreeTierCheck() {
		return freeTierCheck;
	}

	public void setFreeTierCheck(boolean freeTierCheck) {
		this.freeTierCheck = freeTierCheck;
	}

	public boolean isApiGatewayCheck() {
		return apiGatewayCheck;
	}

	public void setApiGatewayCheck(Boolean apiGatewayCheck) throws LambdaPersistenceException {
		this.apiGatewayCheck = apiGatewayCheck;
		
		if(apiGatewayCheck.equals(true)) {
			try {
				Entity api = DbUtil.getOrInitialize("ApiGateway", "api_gateway");
				this.pricePerMRequestApi = (double) api.getProperty("pricePerMRequest");
				this.freeRequestsApi =  (Long) api.getProperty("freeRequests");
			} catch (NullPointerException e) {
				throw new LambdaPersistenceException("Values for Api gateway is not set yet!");
			}
		}
	}

	public boolean isDataTransferCheck() {
		return dataTransferCheck;
	}

	public void setDataTransferCheck(Boolean dataTransferCheck) throws LambdaPersistenceException {
		this.dataTransferCheck = dataTransferCheck;
		
		if(dataTransferCheck.equals(true)) {
			try {
				Entity e = DbUtil.getOrInitialize("DataTransfer", "data_transfer");
				this.pricePerVolume = (double) e.getProperty("pricePerGb");
			} catch (NullPointerException e) {
				throw new LambdaPersistenceException("Values for Data transfer is not set yet!");
			}
		}
		
	}
	
	private double getMax(double num1, double num2) {
		return (num2>num1 ? num2 : num1);
	}

	private double totalGbs() {
		return (memory/1024.0 * avgExecutionTime * numRequests);
	}
	
	private double computeCost() {
		if(freeTierCheck) {
			return getMax((totalGbs() - freeGbs), 0) * pricePerGbs;
		} else {
			return totalGbs() * pricePerGbs;
		}
	}
	
	private double requestsCost() {
		if(freeTierCheck) {
			return getMax((numRequests - freeRequests), 0) * (pricePerMRequest/1000000.0);
		} else {
			return numRequests * (pricePerMRequest/1000000.0);
		}
	}
	
	private double apiGatewayCost() {
		if(apiGatewayCheck) {
			if(freeTierCheck) {
				return getMax((numReqApiGateway - freeRequestsApi), 0) * (pricePerMRequestApi/1000000.0);
			} else {
				return numReqApiGateway * (pricePerMRequestApi/1000000.0);
			}
		} else {
			return 0;
		}
	}
	
	private int databaseCost() {
		//It is for future extension. It can be extended to various range of databases like DynamoDb, etc
		return 0;
	}
	
	private double dataTransferCost() {
		if(dataTransferCheck) {
			return dataTransferVolume * pricePerVolume;
		} else {
			return 0;
		}
	}
	
	private double ioCost() {
		//can be extended for more IO costs like cloud watch cost, etc
		return apiGatewayCost() + databaseCost() + dataTransferCost();
	}
	
	private int additionalCost() {
		int cost = 0;
		for(int i: additionalCosts) {
			cost += i;
		}
		return cost;
	}
	
	public double calculateCost() {
		if(totalCost == 0) {
			computeCost = computeCost();
			requestsCost = requestsCost();
			ioCost = ioCost();
			additionalCost = additionalCost();
			totalCost = computeCost + requestsCost + ioCost + additionalCost;
		}
		
		return totalCost;
	}
	
	public String generateReport() {
		calculateCost();
		DecimalFormat f = new DecimalFormat("##.00");
		
		String report = "<table class=\"table table-bordered\"><tr><th> Compute Cost: </th><td> " + f.format(computeCost) + " $ / Month</td></tr>" + "<tr><th>  Requests Cost: </th><td> " + f.format(requestsCost) + " $ / Month</td></tr>" 
				+ "<tr><th> IO Cost: </th><td>" + f.format(ioCost) + " $ / Month</td></tr>" + "<tr><th> Additional Cost: </th><td>" + 
				f.format(additionalCost) + " $ / Month</td></tr>" + "<tr><th>  Total Cost: </th><td>" + f.format(totalCost) + " $ / Month</td></tr>";
		
		return report;
	}

}
