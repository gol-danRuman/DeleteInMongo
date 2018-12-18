package com.example.deleteUsersDB;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;



@SpringBootApplication(exclude = {JmxAutoConfiguration.class})
public class DeleteUsersDbApplication {

	@Value("${file.path}")
	public String filePath;
	
	public static void main(String[] args) {
		
//		SpringApplication.run(DeleteUsersDbApplication.class, args);
		SpringApplication sa = new SpringApplication(DeleteUsersDbApplication.class);
        

        ApplicationContext c = sa.run(args);
        MyObject bean = c.getBean(MyObject.class);
        bean.deleteUsers("/home/fm-pc-lt-64/Desktop/email.csv");
	}
	
	@Component
	public static class MyObject{
	
	public List<String> deleteUsers(String filePath){
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(filePath));
            List<String> emails = new ArrayList<>();
            while ((line = br.readLine()) != null) {
            	String[] email = line.split(cvsSplitBy);
            	
                emails.add(email[1]);


            }
//            System.out.println("Emails [email= " + emails.toString()+ "]");
            
            MongoClient mongoClient = this.connnectToMongo("","lms","");
            MongoDatabase database = mongoClient.getDatabase("lms-test");
            MongoCollection<Document> collection = database.getCollection("users");
            for (Iterator<Document> iter = collection.find().iterator(); iter.hasNext(); ) {
                Document element = iter.next();
                try {
                String email = emails.stream()
                		.filter(e -> e.equalsIgnoreCase(element.get("email").toString()))
                		.findAny()
                		.orElse(null);
                
                if(null == email) {
                	System.out.println("Delete element: {}"+ element.toJson());
//                	collection.deleteOne(element);
                }
                }catch(NullPointerException e) {
                	System.out.println(e.getMessage()+ element.toJson());
                }
            }

//            System.out.println("Document : "+ doc.toJson());
//            DB database = mongoClient.getDB("lms");
//            DBCollection collection = database.getCollection("users");
//            Iterator<DBObject> cursor = collection.find().iterator();
//            DBObject obj = cursor.
//            System.out.println("Data" + collection.find());
////            DBCursor cursor = collection.find
//            BasicDBObject searchQuery = new BasicDBObject();
//            searchQuery.put("name", "John");
//             
//            collection.remove(searchQuery);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		return null;
	}
	
	
	public MongoClient connnectToMongo(String userName, String database, String passwords) {
		char[] password = passwords.toCharArray();
		 MongoCredential credential = MongoCredential.createCredential(userName, database, password);

		 MongoClientOptions options = MongoClientOptions.builder().sslEnabled(true).build();

//		 MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017),
//		                                           Arrays.asList(credential),
//		                                           options);
		 MongoClient mongoClient = new MongoClient("localhost", 27017);
		 return mongoClient;
		 
		 
	}

	}
}

