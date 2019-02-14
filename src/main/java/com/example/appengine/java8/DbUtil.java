package com.example.appengine.java8;

import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class DbUtil {

	private static final Logger LOGGER = Logger.getLogger(DbUtil.class.getName());
	
	public static Iterable<Entity> getAll(String entityName) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(entityName);
		PreparedQuery pq = datastore.prepare(q);
		Iterable<Entity> result = pq.asIterable();
		return result;
	}
	
	public static Iterable<Entity> getAllByAncestor(String entityName, Key Ancestor) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(entityName).setAncestor(Ancestor);
		PreparedQuery pq = datastore.prepare(q);
		Iterable<Entity> result = pq.asIterable();
		return result;
	}
	
	public static Iterable<Entity> getEqualAll(String entityName, String name, String value) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(entityName).setFilter(new FilterPredicate(name, FilterOperator.EQUAL, value));
		PreparedQuery pq = datastore.prepare(q);
		Iterable<Entity> result = pq.asIterable();
		return result;
	}
	
	public static Entity getEqualSingle(String entityName, String name, Long value) throws LambdaPersistenceException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(entityName).setFilter(new FilterPredicate(name, FilterOperator.EQUAL, value));
		PreparedQuery pq = datastore.prepare(q);
		Entity result = pq.asSingleEntity();
		if(result == null) {
			throw new LambdaPersistenceException("Entity: " + entityName + " not found for key: " + name + " of value: " + value);
		}
		return result;
	}
	
	public static Entity getByKey(String entityName, String key) throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key k = KeyFactory.createKey(entityName, key);
		Entity e = datastore.get(k);
		return e;
	}
	
	public static void deleteByKey(String entityName, String key) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key k = KeyFactory.createKey(entityName, key);
		datastore.delete(k);
	}
	
	public static void put(Entity entity) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(entity);
	}
	
	public static void putBatch(List<Entity> entities) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {
			datastore.put(txn, entities);
			txn.commit();
		} finally {
			if (txn.isActive()) {
			    txn.rollback();
			    LOGGER.warning("transaction failed for batch update");
			}
		}
	}
	
	public static void putInTransaction(List<Entity> entities) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {		
			for(Entity e: entities) {
				datastore.put(txn, e);
			}
			
			txn.commit();
		} finally {
			if (txn.isActive()) {
			    txn.rollback();
			    LOGGER.warning("transaction failed for batch update");
			}
		}
	}
	
	public static int countEntities(String entityName) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(entityName);
		int total = datastore.prepare(q).countEntities(FetchOptions.Builder.withDefaults());
		return total;
	}
	
	public static int countEntitiesByAncestor(String entityName, Key Ancestor) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(entityName).setAncestor(Ancestor);;
		int total = datastore.prepare(q).countEntities(FetchOptions.Builder.withDefaults());
		return total;
	}
	
	public static int countEqualEntities(String entityName, String name, String value) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(entityName).setFilter(new FilterPredicate(name, FilterOperator.EQUAL, value));
		int total = datastore.prepare(q).countEntities(FetchOptions.Builder.withDefaults());
		return total;
	}
	
	public static Entity getOrInitialize(String entity, String key) {
		Entity result;
		try {
			result = DbUtil.getByKey(entity, key);
		} catch (EntityNotFoundException e) {
			result = new Entity(entity, key);
		}
		
		return result;
	}
}
