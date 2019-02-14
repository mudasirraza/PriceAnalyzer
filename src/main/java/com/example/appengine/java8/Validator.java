package com.example.appengine.java8;

import com.example.appengine.java8.ValidationResult.ResultType;


public class Validator {
	
	public static ValidationResult validateString(String theString, String property){
		ValidationResult vr = new ValidationResult();
		if(theString == null || theString.length() == 0){
			vr.setType(ResultType.NOTOK);
			vr.setErrMsg("No null or empty strings allowed for " + property);
		} else {
			vr.setType(ResultType.OK);
		}
		
		return vr;
	}
		
	public static ValidationResult validateNumber(Long theNumber, String property){
		ValidationResult vr = new ValidationResult();
		if(theNumber < 0){
			vr.setType(ResultType.NOTOK);
			vr.setErrMsg("Negative numbers not allowed for " + property);
		} else {
			vr.setType(ResultType.OK);
		}
		
		return vr;
	}
	
	public static ValidationResult validateDouble(double theNumber, String property){
		ValidationResult vr = new ValidationResult();
		if(theNumber < 0.0){
			vr.setType(ResultType.NOTOK);
			vr.setErrMsg("Negative numbers not allowed for " + property);
		} else {
			vr.setType(ResultType.OK);
		}
		
		return vr;
	}

}
