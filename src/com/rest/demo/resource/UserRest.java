package com.rest.demo.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.rest.demo.entity.User;

import util.FileUtil;


@Path("/user")
public class UserRest {
	
	@GET
	@Path("{version}")
	public String get(@Context UriInfo ui) {
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		MultivaluedMap<String, String> pathPatams = ui.getPathParameters();
		for(String key : queryParams.keySet()) {
			System.out.println(key +"  " + queryParams.get(key));
		}
		
		for(String key : pathPatams.keySet()) {
			System.out.println(key +"  " + pathPatams.get(key));
		}
		return "success";
	}
	
	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public User login(MultivaluedMap<String, String> formParams) {
		User user = new User();
		user.setName(formParams.getFirst("name").toString());
		user.setPassword(formParams.getFirst("password").toString());
		
		return user;
	}
	
	@Path("login2")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public User login(@FormParam("name") String name, @FormParam("password") String password) {
		User user = new User();
		user.setName(name);
		user.setPassword(password);
		return user;
		
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayHi() {
		return "say hi";
	}
	
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String helloWord() {
		return "hello word";
	}
	
	
	@GET
	@Path("/getUser/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser(@PathParam("id") String id, @QueryParam("name") String name) {
		User user = new User(id, 18, name);
		return user;
	}
	
	@GET
	@Path("/getUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getUsers(){
		List<User> users = new ArrayList<User>();
		
		for(int i = 0; i < 10; i++) {
			User user = new User(1, "cuiyao-" + i);
			users.add(user);
		} 
		
		return users;
	}
	
	@GET
	@Path("getMap")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, User> getMap(){
		Map<String, User> map = new HashMap<String, User>();
		map.put("bulity", new User(18, "cuiyao"));
		return map;
	}
	
	@POST
	@Path("addUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User addUser(User user) {
		System.out.println(user.toString());
		return user;
	}
	
	@POST
	@Path("updateUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, User> updateUser(Map<String, User> users) {
		System.out.println(users);
		Set<String> keys = users.keySet();
		for(String key: keys) {
			System.out.println(key+"---" + users.get(key).toString());
		}
		return users;
	}
	
	@POST
	@Path("upload")
	@Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public List<String>  upload(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		 String path = "D:"+File.separator+"imgs" + File.separator;
		  return FileUtil.upload(path, request);
	}
	
	
	
	

}