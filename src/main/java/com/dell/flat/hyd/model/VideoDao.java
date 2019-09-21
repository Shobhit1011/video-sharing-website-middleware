package com.dell.flat.hyd.model;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

public class VideoDao { 
  
  public SessionFactory sessionfactory;
  
  public void setSessionfactory(SessionFactory sessionfactory) {  
    this.sessionfactory = sessionfactory;  
}
  @Transactional
  public void save(Object obj) {
	  sessionfactory.getCurrentSession().save(obj);
  }
  
@SuppressWarnings("deprecation")
@Transactional
  public User get(String email) {
	  Criteria criteria=sessionfactory.getCurrentSession()
			  .createCriteria(User.class).add(Restrictions.eq("email", email));
	  User user =(User)criteria.uniqueResult();
	  return user;
  }

@SuppressWarnings("deprecation")
@Transactional
public List<User> getAll(){
	@SuppressWarnings("unchecked")
	List<User> list = sessionfactory.getCurrentSession().createCriteria(User.class).list();
	return list;
}
@SuppressWarnings("deprecation")
@Transactional
public List<Video> getAllVideosByUserId(int userId) {
	@SuppressWarnings("unchecked")
	List<Video> list = sessionfactory.getCurrentSession().createCriteria(Video.class)
			.add(Restrictions.eq("user_id", userId)).list();
	return list;
}

@Transactional
public Video getVideoById(int id) {
	Video video = (Video) sessionfactory.getCurrentSession().get(Video.class, id);
	return video;
}

@Transactional
public User getUserById(int id) {
	User user = (User) sessionfactory.getCurrentSession().get(User.class, id);
	return user;
}

@Transactional
public int count() {
	int records = ((Long)sessionfactory.getCurrentSession()
		.createQuery("select count(*) from Subscription").uniqueResult()).intValue();
	
	return records;
}
@SuppressWarnings("deprecation")
@Transactional
public List<Subscription> subscriptionsByUserId(int userId) {
	@SuppressWarnings("unchecked")
	List <Subscription> list = sessionfactory.getCurrentSession()
	.createCriteria(Subscription.class).add(Restrictions.eq("user_id", userId)).list();
	
	return list;
}

@Transactional
public List<Video> search(String name){
	Query query = sessionfactory.getCurrentSession().createQuery("Select id , name_in_folder , size , user_id from "
			+ "Video where name_in_folder LIKE CONCAT('%',?1,'%')");
	query.setParameter(1,name);
	@SuppressWarnings("unchecked")
	List<Video> list= query.getResultList();
	return list;
	
}

@Transactional()
public int getUserCount() {
	int records = ((Long)sessionfactory.getCurrentSession()
			.createQuery("select count(*) from User").uniqueResult()).intValue();
	
	return records;
}

@Transactional()
public int getVideoCount() {
	int records = ((Long)sessionfactory.getCurrentSession()
			.createQuery("select count(*) from Video").uniqueResult()).intValue();
		
		return records;
}

@Transactional()
public int getRatingsCount() {
	int records = ((Long)sessionfactory.getCurrentSession()
			.createQuery("select count(*) from Ratings").uniqueResult()).intValue();
		
		return records;
}

@SuppressWarnings({ "rawtypes" })
@Transactional
public int getRatings(int userId,int videoId) {
	Query query = sessionfactory.getCurrentSession().createQuery("Select serialNo from Ratings "
			+ "where userId ="+userId+" and videoId="+videoId);
	List list = query.getResultList();
	Iterator itr = list.iterator();
	int value = 0;
	if(itr.hasNext()) {
		value = (Integer)itr.next();
	}
	return value;
}

@Transactional
public Ratings getRatingsById(int id) {
	Ratings rating = (Ratings)sessionfactory.getCurrentSession().get(Ratings.class, id);
	return rating;
}

@Transactional
public void updateRatingsById(Ratings rating) {
	sessionfactory.getCurrentSession().update(rating);
 }

@SuppressWarnings({ "deprecation", "unchecked" })
@Transactional
public List<Video> getAllVideos(){
	List<Video> videosList = sessionfactory.getCurrentSession().createCriteria(Video.class).list();
	return videosList;
}

@Transactional
public List<Ratings> getRatingsByUser(int userId, int videoId) {
	CriteriaBuilder criteria = sessionfactory.getCurrentSession().getCriteriaBuilder();
	CriteriaQuery<Ratings> query = criteria.createQuery(Ratings.class);
	Root<Ratings> root = query.from(Ratings.class);
	Predicate userIdEqualTo = criteria.equal(root.get("userId"), userId);
	Predicate videoIdEqualTo = criteria.equal(root.get("videoId"),videoId);
	query.select(root).where(criteria.and(userIdEqualTo, videoIdEqualTo));
	Query newQuery = sessionfactory.getCurrentSession().createQuery(query);
	
	@SuppressWarnings("unchecked")
	List<Ratings> list = newQuery.getResultList();
	return list;
}
}


