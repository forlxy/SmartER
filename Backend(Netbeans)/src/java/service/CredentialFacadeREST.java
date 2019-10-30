/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import restws.Credential;
import restws.Resident;

import org.json.JSONArray;
import org.json.JSONObject;
        
/**
 *
 * @author kasal
 */
@Stateless
@Path("restws.credential")
public class CredentialFacadeREST extends AbstractFacade<Credential> {

    @PersistenceContext(unitName = "AssignmentPU")
    private EntityManager em;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    
    public CredentialFacadeREST() {
        super(Credential.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Credential entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") String id, Credential entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") String id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Credential find(@PathParam("id") String id) {
        return super.find(id);
    }

    //Static query by credential's username
    @GET
    @Path("findByUsername/{username}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Credential> findByUsername(@PathParam("username") String username) {
        Query query = em.createNamedQuery("Credential.findByUsername");
        query.setParameter("username", username);
        return query.getResultList();
    }
    
    //Static query by credential's hashcode of password
    @GET
    @Path("findByPasswdHash/{passwdHash}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Credential> findByPasswdHash(@PathParam("passwdHash") String passwdHash) {
        Query query = em.createNamedQuery("Credential.findByPasswdHash");
        query.setParameter("passwdHash", passwdHash);
        return query.getResultList();
    }
    
    //Static query by credential's registration date
    //Data should in form like yyyy-MM-dd
    @GET
    @Path("findByRegdate/{regdate}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Credential> findByRegdate(@PathParam("regdate") String regdate) throws ParseException {
        Query query = em.createNamedQuery("Credential.findByRegdate");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date;
        date = new java.util.Date( df.parse( regdate ).getTime() ); 
        query.setParameter("regdate", date);
        return query.getResultList();
    }
    
    //Static query by credential's resident ID (foreign key)
    @GET
    @Path("findByResid/{resid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Credential> findByResid(@PathParam("resid") Integer resid) {
        Query query = em.createNamedQuery("Credential.findByResid");
        query.setParameter("resid", resid);
        return query.getResultList();
    }
    
    
    //For android
    @GET
    @Path("findByUnPw/{username}/{passwdHash}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Resident> findByUnPw(@PathParam("username") String username, @PathParam("passwdHash") String passwdHash) {
        TypedQuery<Resident> query = em.createQuery("SELECT c.resid FROM Credential c WHERE UPPER(c.username) = UPPER(:username) "
                + "AND UPPER(c.passwdHash) = UPPER(:passwdHash)", Resident.class);
        query.setParameter("username", username);
        query.setParameter("passwdHash", passwdHash);
        return query.getResultList();
    }    
    
    //For android
    @POST
    @Path("postByUnPw")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postByUnPw(String msg) {
        JSONObject job = new JSONObject(msg);
        String username = job.getString("username");
        String passwdHash = job.getString("passwdHash");
        
        TypedQuery<Resident> query = em.createQuery("SELECT c.resid FROM Credential c WHERE c.username = :username "
                + "AND UPPER(c.passwdHash) = UPPER(:passwdHash)", Resident.class);
        query.setParameter("username", username);
        query.setParameter("passwdHash", passwdHash);
        List<Resident> result = query.getResultList();
        if(result.size() > 0)
            return Response.status(200).entity(result.get(0)).build();
        return Response.status(404).build();
    }   
    
    //For android
    @POST
    @Path("postCrential")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postCrential(String msg) {
//        JSONObject job = new JSONObject(msg).getJSONObject("crential");
        JSONObject job = new JSONObject(msg);
//        JSONObject rd = new JSONObject(msg).getJSONObject("resident");
        
        Integer rid = job.getJSONObject("resid").getInt("resid");
        TypedQuery<Resident> q = em.createQuery("SELECT r FROM Resident r WHERE r.resid = :resid", Resident.class);
        q.setParameter("resid", rid);
        Resident resid = q.getResultList().get(0);
        
        
        
        String username = job.getString("username");
        String passwdHash = job.getString("passwdHash");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        
        String input = job.getString("regdate");
        if ( input.endsWith( "Z" ) ) {
            input = input.substring( 0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;
            String s0 = input.substring( 0, input.length() - inset );
            String s1 = input.substring( input.length() - inset, input.length() );
            input = s0  + "GMT"+ s1;
        }
        
        java.util.Date regdate = null;
        try { 
            regdate = new java.util.Date( df.parse( input ).getTime() );
        } catch (ParseException ex) {
            Logger.getLogger(CredentialFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        Credential newCdt = new Credential();
        newCdt.setPasswdHash(passwdHash);
        newCdt.setRegdate(regdate);
        newCdt.setUsername(username);
        newCdt.setResid(resid);
        
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        javax.validation.Validator validator = factory.getValidator();
            Set<ConstraintViolation<Credential>> constraintViolations = validator.validate(newCdt);

            if (constraintViolations.size() > 0 ) {
            System.out.println("Constraint Violations occurred..");
            for (ConstraintViolation<Credential> contraints : constraintViolations) {
            System.out.println(contraints.getRootBeanClass().getSimpleName()+
            "." + contraints.getPropertyPath() + " " + contraints.getMessage());
              }
            }
        
        em.persist(newCdt);
        
        return Response.status(200).entity(newCdt.getResid()).build();
    }   
    
    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Credential> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Credential> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
