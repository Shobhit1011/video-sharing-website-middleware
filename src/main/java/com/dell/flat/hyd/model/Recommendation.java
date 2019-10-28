package com.dell.flat.hyd.model;

import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import com.mysql.cj.jdbc.MysqlDataSource;

public class Recommendation {
	public List<RecommendedItem> recommend(int noOfRecommendations, int userId) throws IOException, TasteException{
		List<RecommendedItem> recommendations = null;
		MysqlDataSource datasource = new MysqlDataSource();
		datasource.setServerName("localhost");
		datasource.setUser("root");
		datasource.setPassword("12345");
		datasource.setDatabaseName("video_sharing_website");
		
		System.out.println(userId);
		
		JDBCDataModel dm = new MySQLJDBCDataModel(datasource,"ratings","userId","videoId","ratings","");
		ItemSimilarity itemSimilarity = new EuclideanDistanceSimilarity(dm);
		Recommender itemRecommender = new GenericItemBasedRecommender(dm,itemSimilarity);
		recommendations = itemRecommender.recommend(userId,noOfRecommendations);
		System.out.println(recommendations);
		return recommendations;
	}
}
